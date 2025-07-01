package ru.edmebank.clients.app.impl.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.edmebank.clients.app.api.repository.AccountPriorityRepository;
import ru.edmebank.clients.app.api.repository.AccountRepository;
import ru.edmebank.clients.domain.entity.Account;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.clients.fw.exception.AccountPriorityException;
import ru.edmebank.clients.fw.security.JwtTokenUtil;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.enums.AccountPriorityStatus;
import ru.edmebank.contracts.enums.AccountPriorityType;
import ru.edmebank.contracts.enums.InitiatorRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPriorityServiceImplTest {

    private static final String TEST_USER_NAME = "Test User";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ.signature";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ.signature";
    private static final String ACCOUNT_STATUS_OPEN = "открыт";
    private static final String ACCOUNT_STATUS_BLOCKED = "заблокирован";
    private static final String ACCOUNT_TYPE_CREDIT = "кредит";
    private static final String ERROR_ACCOUNT_NOT_FOUND_CODE = "ACCOUNT_NOT_FOUND";
    private static final String ERROR_ACCOUNT_STATUS_BLOCKED = "ACCOUNT_BLOCKED";
    private static final String ERROR_ACCOUNT_NOT_FOUND_MESSAGE = "Счет не найден";
    private static final int INITIAL_VERSION = 1;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountPriorityRepository accountPriorityRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AccountPriorityServiceImpl accountPriorityService;

    private UUID accountId;
    private Account account;
    private AccountPriority accountPriority;
    private AccountPriorityUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();

        lenient().when(jwtTokenUtil.extractTokenFromHeader(TEST_AUTH_HEADER)).thenReturn(TEST_TOKEN);
        lenient().when(jwtTokenUtil.validateToken(TEST_TOKEN)).thenReturn(true);

        Claims mockClaims = mock(Claims.class);
        lenient().when(mockClaims.getSubject()).thenReturn(TEST_USER_ID);
        lenient().when(jwtTokenUtil.getClaims(TEST_TOKEN)).thenReturn(mockClaims);
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - успешный сценарий")
    void getAccountPriorityDetailsSuccess() {
        // Arrange
        account = createTestAccount(ACCOUNT_TYPE_CREDIT);
        accountPriority = createTestAccountPriority(account);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPriorityRepository.findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE))
                .thenReturn(accountPriority);

        // Act
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriorityDetails(accountId, TEST_AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertNotNull(response.getAccountType());
        assertFalse(response.getCurrentPriorities().getPriorityForWriteOff());
        assertTrue(response.getCurrentPriorities().getPriorityForAccrual());

        verify(accountRepository).findById(accountId);
        verify(accountPriorityRepository).findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - счет не найден")
    void getAccountPriorityDetailsAccountNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.getAccountPriorityDetails(accountId, TEST_AUTH_HEADER)
        );

        assertEquals(ERROR_ACCOUNT_NOT_FOUND_CODE, exception.getCode());
        assertEquals(ERROR_ACCOUNT_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(accountPriorityRepository);
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - создание дефолтных приоритетов")
    void getAccountPriorityDetailsCreateDefaultPriorities() {
        // Arrange
        account = createTestAccount(ACCOUNT_TYPE_CREDIT);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPriorityRepository.findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE))
                .thenReturn(null);

        // Act
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriorityDetails(accountId, TEST_AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertNotNull(response.getAccountType());
        assertFalse(response.getCurrentPriorities().getPriorityForWriteOff());
        assertFalse(response.getCurrentPriorities().getPriorityForAccrual());

        verify(accountRepository).findById(accountId);
        verify(accountPriorityRepository).findByAccountAccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);
    }

    @Test
    @DisplayName("Обновление приоритетов счета - счет заблокирован")
    void updateAccountPriorityBlockedAccount() {
        // Arrange
        account = createTestAccount(ACCOUNT_TYPE_CREDIT);
        account.setStatus(ACCOUNT_STATUS_BLOCKED);
        updateRequest = createTestUpdateRequest();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, updateRequest, TEST_AUTH_HEADER)
        );

        assertEquals(ERROR_ACCOUNT_STATUS_BLOCKED, exception.getCode());
        assertEquals("Невозможно изменить приоритеты для заблокированного счета", exception.getMessage());
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(accountPriorityRepository);
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - невалидный токен")
    void getAccountPriorityDetailsInvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid.token";
        when(jwtTokenUtil.extractTokenFromHeader(invalidToken)).thenReturn("invalid.token");
        when(jwtTokenUtil.validateToken("invalid.token")).thenReturn(false);

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.getAccountPriorityDetails(accountId, invalidToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED.name(), exception.getCode());
        assertEquals("Невалидный JWT токен", exception.getMessage());
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(accountPriorityRepository);
    }

    @Test
    @DisplayName("Обновление приоритетов счета - несовпадение ID инициатора")
    void updateAccountPriorityInitiatorMismatch() {
        // Arrange
        updateRequest = createTestUpdateRequest();
        String differentUserToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWZmZXJlbnQtdXNlciJ9.signature";
        when(jwtTokenUtil.extractTokenFromHeader(differentUserToken)).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWZmZXJlbnQtdXNlciJ9.signature");
        when(jwtTokenUtil.validateToken(anyString())).thenReturn(true);

        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("different-user");
        when(jwtTokenUtil.getClaims(anyString())).thenReturn(mockClaims);

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, updateRequest, differentUserToken)
        );

        assertEquals(HttpStatus.FORBIDDEN.name(), exception.getCode());
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(accountPriorityRepository);
    }

    private Account createTestAccount(String accountType) {
        Account acc = new Account();
        acc.setAccountId(accountId);
        acc.setStatus(ACCOUNT_STATUS_OPEN);
        acc.setBalance(BigDecimal.TEN);
        acc.setAccountType(accountType);
        acc.setCurrency("RUB");
        return acc;
    }

    private AccountPriority createTestAccountPriority(Account account) {
        AccountPriority priority = new AccountPriority();
        priority.setId(UUID.randomUUID());
        priority.setAccount(account);
        priority.setAccountType(AccountPriorityType.LOAN_PRINCIPAL.name());
        priority.setPriorityWriteOff(false);
        priority.setPriorityAccrual(true);
        priority.setCanSetWriteOff(true);
        priority.setCanSetAccrual(true);
        priority.setChangeReasons(Collections.emptyList());
        priority.setChangeReason("Initial setup");
        priority.setInitiatorId(TEST_USER_ID);
        priority.setInitiatorName(TEST_USER_NAME);
        priority.setInitiatorRole(InitiatorRole.CREDIT_MANAGER.name());
        priority.setLastUpdatedBy(TEST_USER_NAME);
        priority.setStatus(AccountPriorityStatus.ACTIVE);
        priority.setVersion(INITIAL_VERSION);

        LocalDateTime now = LocalDateTime.now();
        try {
            java.lang.reflect.Field createdAtField = AccountPriority.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(priority, now);

            java.lang.reflect.Field updatedAtField = AccountPriority.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(priority, now);
        } catch (Exception ignored) { }

        return priority;
    }

    private AccountPriorityUpdateRequest createTestUpdateRequest() {
        AccountPriorityUpdateRequest request = new AccountPriorityUpdateRequest();
        request.setPriorityForWriteOff(true);
        request.setPriorityForAccrual(false);
        request.setReason("Test reason");

        AccountPriorityUpdateRequest.Initiator initiator = new AccountPriorityUpdateRequest.Initiator();
        initiator.setId(TEST_USER_ID);
        initiator.setName(TEST_USER_NAME);
        initiator.setRole(InitiatorRole.CREDIT_MANAGER);
        request.setInitiator(initiator);

        return request;
    }
}
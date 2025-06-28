package ru.edmebank.clients.app.impl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edmebank.clients.app.api.repository.AccountPriorityRepository;
import ru.edmebank.clients.app.api.repository.AccountRepository;
import ru.edmebank.clients.domain.entity.Account;
import ru.edmebank.clients.domain.entity.AccountPriority;
import ru.edmebank.clients.fw.exception.AccountPriorityException;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;
import ru.edmebank.contracts.enums.AccountPriorityStatus;
import ru.edmebank.contracts.enums.AccountPriorityType;
import ru.edmebank.contracts.enums.InitiatorRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountPriorityServiceImplTest {

    private static final String ACCOUNT_TYPE_LOAN = "loan";
    private static final String TEST_USER_NAME = "Test User";
    private static final String ERROR_ACCOUNT_NOT_FOUND_CODE = "ACCOUNT_NOT_FOUND";
    private static final String ERROR_ACCOUNT_NOT_FOUND_MESSAGE = "Счет не найден";
    private static final String ERROR_CONFLICT_PRIORITIES_CODE = "CONFLICT_PRIORITIES";
    private static final String ERROR_CONFLICT_PRIORITIES_MESSAGE =
            "Для кредитных счетов можно установить только один тип приоритета";
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final int INITIAL_VERSION = 1;
    private static final int UPDATED_VERSION = INITIAL_VERSION + 1;
    private static final int EXPECTED_SAVE_CALLS = 2;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountPriorityRepository accountPriorityRepository;

    @InjectMocks
    private AccountPriorityServiceImpl accountPriorityService;

    private UUID accountId;
    private Account account;
    private AccountPriority accountPriority;
    private AccountPriorityUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = createTestAccount();
        accountPriority = createTestAccountPriority();
        updateRequest = createTestUpdateRequest();
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - успешный сценарий")
    void getAccountPriorityDetailsSuccess() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPriorityRepository.findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE))
                .thenReturn(accountPriority);

        // Act
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriorityDetails(accountId);

        // Assert
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertEquals(AccountPriorityType.LOAN_PRINCIPAL, response.getAccountType());
        assertFalse(response.getCurrentPriorities().getPriorityForWriteOff());
        assertTrue(response.getCurrentPriorities().getPriorityForAccrual());

        verify(accountRepository).findById(accountId);
        verify(accountPriorityRepository).findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - счет не найден")
    void getAccountPriorityDetailsAccountNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.getAccountPriorityDetails(accountId)
        );

        assertEquals(ERROR_ACCOUNT_NOT_FOUND_CODE, exception.getCode());
        assertEquals(ERROR_ACCOUNT_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(accountPriorityRepository);
    }

    @Test
    @DisplayName("Обновление приоритетов счета - успешный сценарий")
    void updateAccountPrioritySuccess() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPriorityRepository.findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE))
                .thenReturn(accountPriority);
        when(accountPriorityRepository.save(any(AccountPriority.class)))
                .thenAnswer(i -> mockSavedPriority(i.getArgument(FIRST_ARGUMENT_INDEX)));

        // Act
        AccountPriorityUpdateResponse response = accountPriorityService.updateAccountPriority(accountId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertTrue(response.getUpdatedPriorities().getPriorityForWriteOff());
        assertFalse(response.getUpdatedPriorities().getPriorityForAccrual());
        assertEquals(UPDATED_VERSION, response.getVersion());

        verify(accountRepository).findById(accountId);
        verify(accountPriorityRepository).findByAccount_AccountIdAndStatus(accountId, AccountPriorityStatus.ACTIVE);
        verify(accountPriorityRepository, times(EXPECTED_SAVE_CALLS)).save(any(AccountPriority.class));
    }

    @Test
    @DisplayName("Обновление приоритетов счета - конфликт приоритетов")
    void updateAccountPriorityConflictPriorities() {
        // Arrange
        updateRequest.setPriorityForWriteOff(true);
        updateRequest.setPriorityForAccrual(true);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        AccountPriorityException exception = assertThrows(
                AccountPriorityException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, updateRequest)
        );

        assertEquals(ERROR_CONFLICT_PRIORITIES_CODE, exception.getCode());
        assertEquals(ERROR_CONFLICT_PRIORITIES_MESSAGE, exception.getMessage());
        verify(accountRepository).findById(accountId);
        verify(accountPriorityRepository, never()).save(any(AccountPriority.class));
    }


    private Account createTestAccount() {
        Account acc = new Account();
        acc.setAccountId(accountId);
        acc.setStatus("открыт");
        acc.setBalance(BigDecimal.TEN);
        acc.setAccountType(ACCOUNT_TYPE_LOAN);
        return acc;
    }

    private AccountPriority createTestAccountPriority() {
        AccountPriority priority = new AccountPriority();
        priority.setId(UUID.randomUUID());
        priority.setAccount(account);
        priority.setAccountType(AccountPriorityType.LOAN_PRINCIPAL.name());
        priority.setPriorityWriteOff(false);
        priority.setPriorityAccrual(true);
        priority.setCanSetWriteOff(true);
        priority.setCanSetAccrual(true);
        priority.setChangeReasons(Collections.emptyList());
        priority.setStatus(AccountPriorityStatus.ACTIVE);
        priority.setVersion(INITIAL_VERSION);
        return priority;
    }

    private AccountPriorityUpdateRequest createTestUpdateRequest() {
        AccountPriorityUpdateRequest request = new AccountPriorityUpdateRequest();
        request.setPriorityForWriteOff(true);
        request.setPriorityForAccrual(false);

        AccountPriorityUpdateRequest.Initiator initiator = new AccountPriorityUpdateRequest.Initiator();
        initiator.setId("test-user-id");
        initiator.setName(TEST_USER_NAME);
        initiator.setRole(InitiatorRole.CREDIT_MANAGER);
        request.setInitiator(initiator);

        return request;
    }

    private AccountPriority mockSavedPriority(AccountPriority source) {
        AccountPriority mock = new AccountPriority();
        mock.setId(source.getId());
        mock.setAccount(source.getAccount());
        mock.setPriorityWriteOff(source.getPriorityWriteOff());
        mock.setPriorityAccrual(source.getPriorityAccrual());
        mock.setVersion(source.getVersion());

        try {
            java.lang.reflect.Field field = AccountPriority.class.getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(mock, LocalDateTime.now());
        } catch (Exception ignored) { }

        return mock;
    }
}
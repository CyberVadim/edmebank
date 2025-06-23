package ru.edmebank.accounts.app.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.edmebank.accounts.adapter.output.repository.AccountRepository;
import ru.edmebank.accounts.app.impl.service.AccountPriorityServiceImpl;
import ru.edmebank.accounts.domain.entity.Account;
import ru.edmebank.accounts.domain.enums.AccountStatus;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.AccountValidationException;
import ru.edmebank.accounts.fw.exception.TooManyRequestsException;
import ru.edmebank.accounts.utils.RateLimiter;
import ru.edmebank.contracts.dto.accounts.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountPriorityServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private AccountPriorityServiceImpl accountPriorityService;

    @Captor
    private ArgumentCaptor<UUID> accountIdCaptor;

    private UUID accountId;
    private UUID requestId;
    private Account testAccount;
    private AccountPriorityRequest validRequest;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        // Создаем тестовый аккаунт
        testAccount = new Account();
        testAccount.setAccountId(accountId);
        testAccount.setClientId(UUID.randomUUID());
        testAccount.setAccountType("CHECKING");
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setCurrency("RUB");
        testAccount.setStatus(AccountStatus.ACTIVE.toString());
        testAccount.setOpenedAt(LocalDateTime.now().minusMonths(6));
        testAccount.setCreatedAt(LocalDateTime.now().minusMonths(6));
        testAccount.setUpdatedAt(LocalDateTime.now().minusDays(10));

        // Создаем валидный запрос
        validRequest = new AccountPriorityRequest();
        validRequest.setPriorityForWriteOff(true);
        validRequest.setPriorityForAccrual(false);
        validRequest.setReason("Тестовая причина");

        AccountPriorityRequest.Initiator initiator = new AccountPriorityRequest.Initiator();
        initiator.setId("user123");
        initiator.setName("Тестовый Пользователь");
        initiator.setRole("CREDIT_MANAGER");
        validRequest.setInitiator(initiator);

        // Настраиваем RateLimiter для разрешения запросов по умолчанию
        when(rateLimiter.tryAcquire()).thenReturn(true);
    }

    @Test
    void updateAccountPrioritySuccess() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act
        AccountPriorityResponse response = accountPriorityService.updateAccountPriority(accountId, validRequest, requestId);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getData());
        assertEquals(accountId.toString(), response.getData().getAccountId());
        assertTrue(response.getData().getUpdatedPriorities().getPriorityForWriteOff());
        assertFalse(response.getData().getUpdatedPriorities().getPriorityForAccrual());
        assertNotNull(response.getData().getUpdatedAt());
        assertEquals(1, response.getData().getVersion());

        // Проверка, что репозиторий был вызван с правильным ID
        verify(accountRepository).findById(accountIdCaptor.capture());
        assertEquals(accountId, accountIdCaptor.getValue());
    }

    @Test
    void updateAccountPriorityAccountNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertTrue(exception.getMessage().contains(accountId.toString()));
    }

    @Test
    void updateAccountPriorityRateLimitExceeded() {
        // Arrange
        when(rateLimiter.tryAcquire()).thenReturn(false);

        // Act & Assert
        TooManyRequestsException exception = assertThrows(
                TooManyRequestsException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertTrue(exception.getMessage().contains("Превышен лимит запросов"));
        verify(accountRepository, never()).findById(any());
    }

    @Test
    void updateAccountPriorityClosedAccount() {
        // Arrange
        testAccount.setStatus(AccountStatus.CLOSED.toString());
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        AccountValidationException exception = assertThrows(
                AccountValidationException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertEquals("INVALID_ACCOUNT_STATE", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("закрытого счета"));
    }

    @Test
    void updateAccountPriorityBlockedAccount() {
        // Arrange
        testAccount.setStatus(AccountStatus.BLOCKED.toString());
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        AccountValidationException exception = assertThrows(
                AccountValidationException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertEquals("ACCOUNT_BLOCKED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("заблокированного счета"));
        assertNotNull(exception.getDetails());
        assertTrue(exception.getDetails().containsKey("blockReason"));
    }

    @Test
    void updateAccountPriorityConflictPriorities() {
        // Arrange
        testAccount.setAccountType("LOAN_PRINCIPAL");
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Создаем запрос с конфликтом приоритетов
        AccountPriorityRequest conflictRequest = new AccountPriorityRequest();
        conflictRequest.setPriorityForWriteOff(true);
        conflictRequest.setPriorityForAccrual(true);
        conflictRequest.setReason("Тестовая причина");
        conflictRequest.setInitiator(validRequest.getInitiator());

        // Act & Assert
        AccountValidationException exception = assertThrows(
                AccountValidationException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, conflictRequest, requestId)
        );

        assertEquals("CONFLICT_PRIORITIES", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("кредитных счетов"));
    }

    @Test
    void updateAccountPriorityDepositAccount() {
        // Arrange
        testAccount.setAccountType("DEPOSIT");
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        AccountValidationException exception = assertThrows(
                AccountValidationException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertEquals("INVALID_ACCOUNT_STATE", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("депозитных счетов"));
    }

    @Test
    void updateAccountPriorityNegativeBalance() {
        // Arrange
        testAccount.setBalance(BigDecimal.valueOf(-100));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        AccountValidationException exception = assertThrows(
                AccountValidationException.class,
                () -> accountPriorityService.updateAccountPriority(accountId, validRequest, requestId)
        );

        assertEquals("INVALID_ACCOUNT_STATE", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("отрицательным балансом"));
    }

    @Test
    void updateAccountPrioritySequentialUpdates() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act - первое обновление
        AccountPriorityResponse response1 = accountPriorityService.updateAccountPriority(accountId, validRequest, requestId);

        // Проверяем результат первого обновления
        assertEquals(1, response1.getData().getVersion());

        // Меняем запрос для второго обновления
        validRequest.setPriorityForWriteOff(false);
        validRequest.setPriorityForAccrual(true);

        // Act - второе обновление
        AccountPriorityResponse response2 = accountPriorityService.updateAccountPriority(accountId, validRequest, requestId);

        // Assert - проверяем, что версия увеличилась и значения обновились
        assertEquals(2, response2.getData().getVersion());
        assertFalse(response2.getData().getUpdatedPriorities().getPriorityForWriteOff());
        assertTrue(response2.getData().getUpdatedPriorities().getPriorityForAccrual());
    }

    @Test
    void getAccountPrioritySuccessWithExistingData() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Сначала сохраняем данные в кэш через обновление
        accountPriorityService.updateAccountPriority(accountId, validRequest, requestId);

        // Act - получаем данные приоритетов
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriority(accountId, requestId);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getData());
        assertEquals(accountId.toString(), response.getData().getAccountId());
        assertEquals(testAccount.getAccountType(), response.getData().getAccountType());

        // Проверяем текущие приоритеты (должны соответствовать ранее установленным)
        assertTrue(response.getData().getCurrentPriorities().getPriorityForWriteOff());
        assertFalse(response.getData().getCurrentPriorities().getPriorityForAccrual());

        // Проверяем разрешенные изменения
        assertTrue(response.getData().getAllowedChanges().getCanSetWriteOff());
        assertTrue(response.getData().getAllowedChanges().getCanSetAccrual());

        // Проверяем информацию о последнем обновлении
        assertNotNull(response.getData().getLastUpdated().getDate());
        assertTrue(response.getData().getLastUpdated().getBy().contains(validRequest.getInitiator().getName()));
    }

    @Test
    void getAccountPrioritySuccessWithoutData() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act - получаем данные приоритетов без предварительного сохранения
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriority(accountId, requestId);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());

        // Проверяем текущие приоритеты (должны быть false по умолчанию)
        assertFalse(response.getData().getCurrentPriorities().getPriorityForWriteOff());
        assertFalse(response.getData().getCurrentPriorities().getPriorityForAccrual());

        // Проверяем информацию о последнем обновлении
        assertEquals("Система", response.getData().getLastUpdated().getBy());
    }

    @Test
    void getAccountPriorityAccountNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountPriorityService.getAccountPriority(accountId, requestId)
        );

        assertTrue(exception.getMessage().contains(accountId.toString()));
    }

    @Test
    void getAccountPriorityWithRestrictions() {
        // Arrange - настраиваем аккаунт с ограничениями
        testAccount.setAccountType("LOAN_PRINCIPAL");
        testAccount.setOpenedAt(LocalDateTime.now().minusYears(6)); // Истекший срок
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        // Act
        AccountPriorityDetailsResponse response = accountPriorityService.getAccountPriority(accountId, requestId);

        // Assert
        assertNotNull(response);
        List<String> reasons = response.getData().getAllowedChanges().getReasons();

        // Вывод для диагностики
        System.out.println("Фактический список причин ограничений:");
        reasons.forEach(reason -> System.out.println(" - " + reason));

        // Проверяем только, что список не пустой
        assertFalse(reasons.isEmpty(), "Список причин не должен быть пустым");

        // Проверяем, что в списке есть хотя бы одна причина с упоминанием истекшего срока
        boolean hasExpirationRestriction = reasons.stream()
                .anyMatch(reason -> reason.toLowerCase().contains("истекшим сроком") ||
                                    reason.toLowerCase().contains("срок действия"));
        assertTrue(hasExpirationRestriction, "Список должен содержать ограничение из-за истекшего срока");
    }

    @Test
    void testMultipleAccountsInCache() {
        // Arrange
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();

        Account account1 = new Account();
        account1.setAccountId(accountId1);
        account1.setStatus(AccountStatus.ACTIVE.toString());
        account1.setAccountType("CHECKING");
        account1.setBalance(BigDecimal.valueOf(1000));
        account1.setOpenedAt(LocalDateTime.now().minusMonths(1));
        account1.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Account account2 = new Account();
        account2.setAccountId(accountId2);
        account2.setStatus(AccountStatus.ACTIVE.toString());
        account2.setAccountType("CHECKING");
        account2.setBalance(BigDecimal.valueOf(2000));
        account2.setOpenedAt(LocalDateTime.now().minusMonths(2));
        account2.setUpdatedAt(LocalDateTime.now().minusDays(2));

        when(accountRepository.findById(accountId1)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(accountId2)).thenReturn(Optional.of(account2));

        // Act - обновляем оба аккаунта с разными приоритетами
        AccountPriorityRequest request1 = new AccountPriorityRequest();
        request1.setPriorityForWriteOff(true);
        request1.setPriorityForAccrual(false);
        request1.setReason("Причина 1");
        request1.setInitiator(validRequest.getInitiator());

        AccountPriorityRequest request2 = new AccountPriorityRequest();
        request2.setPriorityForWriteOff(false);
        request2.setPriorityForAccrual(true);
        request2.setReason("Причина 2");
        request2.setInitiator(validRequest.getInitiator());

        accountPriorityService.updateAccountPriority(accountId1, request1, requestId);
        accountPriorityService.updateAccountPriority(accountId2, request2, requestId);

        // Получаем данные для обоих аккаунтов
        AccountPriorityDetailsResponse response1 = accountPriorityService.getAccountPriority(accountId1, requestId);
        AccountPriorityDetailsResponse response2 = accountPriorityService.getAccountPriority(accountId2, requestId);

        // Assert - проверяем, что у каждого аккаунта сохранены свои приоритеты
        assertTrue(response1.getData().getCurrentPriorities().getPriorityForWriteOff());
        assertFalse(response1.getData().getCurrentPriorities().getPriorityForAccrual());

        assertFalse(response2.getData().getCurrentPriorities().getPriorityForWriteOff());
        assertTrue(response2.getData().getCurrentPriorities().getPriorityForAccrual());
    }
}
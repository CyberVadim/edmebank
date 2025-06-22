package ru.edmebank.accounts.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.fw.exception.AccountExceptionHandler;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.AccountValidationException;
import ru.edmebank.contracts.dto.accounts.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountPriorityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountPriorityService accountPriorityService;

    private ObjectMapper objectMapper;
    private UUID accountId;
    private UUID requestId;
    private AccountPriorityRequest priorityRequest;
    private AccountPriorityResponse successResponse;
    private AccountPriorityDetailsResponse detailsResponse;

    @BeforeEach
    void setUp() {
        // Настраиваем MockMvc вручную с контроллером и глобальным обработчиком исключений
        AccountPriorityController controller = new AccountPriorityController(accountPriorityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AccountExceptionHandler())
                .build();

        // Настраиваем ObjectMapper для работы с Java 8 Date/Time API
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        accountId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        // Создаем запрос на обновление приоритетов
        priorityRequest = new AccountPriorityRequest();
        priorityRequest.setPriorityForWriteOff(true);
        priorityRequest.setPriorityForAccrual(false);
        priorityRequest.setReason("Изменение приоритетов по согласованию с клиентом");

        AccountPriorityRequest.Initiator initiator = new AccountPriorityRequest.Initiator();
        initiator.setId("12345");
        initiator.setName("Иванов Иван Иванович");
        initiator.setRole("CREDIT_MANAGER");
        priorityRequest.setInitiator(initiator);

        // Создаем успешный ответ на обновление
        successResponse = new AccountPriorityResponse();
        successResponse.setStatus("SUCCESS");

        AccountPriorityResponse.PriorityData data = new AccountPriorityResponse.PriorityData();
        data.setAccountId(accountId.toString());

        AccountPriorityResponse.UpdatedPriorities updatedPriorities = new AccountPriorityResponse.UpdatedPriorities();
        updatedPriorities.setPriorityForWriteOff(true);
        updatedPriorities.setPriorityForAccrual(false);

        data.setUpdatedPriorities(updatedPriorities);
        data.setUpdatedAt(ZonedDateTime.of(2025, 6, 13, 16, 15, 31, 0, ZoneId.of("UTC")));
        data.setVersion(5);

        successResponse.setData(data);

        // Создаем ответ на запрос деталей приоритетов
        detailsResponse = new AccountPriorityDetailsResponse();
        detailsResponse.setStatus("SUCCESS");

        AccountPriorityDetailsResponse.PriorityData priorityData = new AccountPriorityDetailsResponse.PriorityData();
        priorityData.setAccountId(accountId.toString());
        priorityData.setAccountType("LOAN_PRINCIPAL");

        AccountPriorityDetailsResponse.CurrentPriorities currentPriorities = new AccountPriorityDetailsResponse.CurrentPriorities();
        currentPriorities.setPriorityForWriteOff(false);
        currentPriorities.setPriorityForAccrual(true);
        priorityData.setCurrentPriorities(currentPriorities);

        AccountPriorityDetailsResponse.AllowedChanges allowedChanges = new AccountPriorityDetailsResponse.AllowedChanges();
        allowedChanges.setCanSetWriteOff(true);
        allowedChanges.setCanSetAccrual(false);
        allowedChanges.setReasons(List.of("Только один приоритет разрешен для кредитных счетов"));
        priorityData.setAllowedChanges(allowedChanges);

        AccountPriorityDetailsResponse.LastUpdated lastUpdated = new AccountPriorityDetailsResponse.LastUpdated();
        lastUpdated.setDate(ZonedDateTime.of(2025, 6, 13, 16, 15, 31, 0, ZoneId.of("UTC")));
        lastUpdated.setBy("Смирнова А.П. (CREDIT_MANAGER)");
        priorityData.setLastUpdated(lastUpdated);

        detailsResponse.setData(priorityData);
    }

    @Test
    void updateAccountPrioritySuccess() throws Exception {
        when(accountPriorityService.updateAccountPriority(eq(accountId), any(AccountPriorityRequest.class), any(UUID.class)))
                .thenReturn(successResponse);

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString())
                        .content(objectMapper.writeValueAsString(priorityRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.data.updatedPriorities.priorityForWriteOff").value(true))
                .andExpect(jsonPath("$.data.updatedPriorities.priorityForAccrual").value(false))
                .andExpect(jsonPath("$.data.version").value(5));
    }

    @Test
    void updateAccountPriorityAccountNotFound() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        when(accountPriorityService.updateAccountPriority(eq(nonexistentId), any(AccountPriorityRequest.class), any(UUID.class)))
                .thenThrow(new AccountNotFoundException("Счет не найден с идентификатором: " + nonexistentId));

        mockMvc.perform(put("/api/accounts/{accountId}/priority", nonexistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString())
                        .content(objectMapper.writeValueAsString(priorityRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void updateAccountPriorityValidationError() throws Exception {
        // Создаем запрос с отсутствующим обязательным полем
        AccountPriorityRequest invalidRequest = new AccountPriorityRequest();
        invalidRequest.setPriorityForWriteOff(true);
        invalidRequest.setPriorityForAccrual(false);
        // Отсутствует reason и initiator

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString())
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void updateAccountPriorityConflictPriorities() throws Exception {
        Map<String, Object> details = new HashMap<>();
        details.put("accountId", accountId.toString());

        when(accountPriorityService.updateAccountPriority(eq(accountId), any(AccountPriorityRequest.class), any(UUID.class)))
                .thenThrow(new AccountValidationException("CONFLICT_PRIORITIES",
                        "Для кредитных счетов можно установить только один тип приоритета", details));

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString())
                        .content(objectMapper.writeValueAsString(priorityRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("CONFLICT_PRIORITIES"));
    }

    @Test
    void getAccountPrioritySuccess() throws Exception {
        when(accountPriorityService.getAccountPriority(eq(accountId), any(UUID.class)))
                .thenReturn(detailsResponse);

        mockMvc.perform(get("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.data.accountType").value("LOAN_PRINCIPAL"))
                .andExpect(jsonPath("$.data.currentPriorities.priorityForWriteOff").value(false))
                .andExpect(jsonPath("$.data.currentPriorities.priorityForAccrual").value(true))
                .andExpect(jsonPath("$.data.allowedChanges.canSetWriteOff").value(true))
                .andExpect(jsonPath("$.data.allowedChanges.canSetAccrual").value(false))
                .andExpect(jsonPath("$.data.allowedChanges.reasons[0]").value("Только один приоритет разрешен для кредитных счетов"));
    }

    @Test
    void getAccountPriorityAccountNotFound() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        when(accountPriorityService.getAccountPriority(eq(nonexistentId), any(UUID.class)))
                .thenThrow(new AccountNotFoundException("Счет не найден с идентификатором: " + nonexistentId));

        mockMvc.perform(get("/api/accounts/{accountId}/priority", nonexistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void getAccountPriorityMissingRequestHeader() throws Exception {
        mockMvc.perform(get("/api/accounts/{accountId}/priority", accountId)
                                .contentType(MediaType.APPLICATION_JSON)
                        // Намеренно не указываем заголовок X-Request-ID
                )
                .andDo(print())
                .andExpect(status().isBadRequest()); // Ожидаем ошибку из-за отсутствия обязательного заголовка
    }

    @Test
    void getAccountPriorityInvalidUuid() throws Exception {
        // Проверка на невалидный UUID в URL
        mockMvc.perform(get("/api/accounts/{accountId}/priority", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Spring конвертирует это в BadRequest для TypeMismatchException
    }

    @Test
    void updateAccountPriorityTooManyRequests() throws Exception {
        // Тест для проверки ограничения скорости запросов
        // В реальном сценарии потребовался бы специальный mock для RateLimiter,
        // но для демонстрации мы просто имитируем соответствующее исключение

        when(accountPriorityService.updateAccountPriority(eq(accountId), any(AccountPriorityRequest.class), any(UUID.class)))
                .thenThrow(new ru.edmebank.accounts.fw.exception.TooManyRequestsException("Превышен лимит запросов на изменение приоритетов"));

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", requestId.toString())
                        .content(objectMapper.writeValueAsString(priorityRequest)))
                .andDo(print())
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("TOO_MANY_REQUESTS"));
    }
}
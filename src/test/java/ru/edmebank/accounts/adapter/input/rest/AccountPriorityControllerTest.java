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
import ru.edmebank.accounts.domain.enums.AccountType;
import ru.edmebank.accounts.fw.exception.AccountException;
import ru.edmebank.accounts.fw.exception.AccountsGlobalExceptionHandler;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.AccountPriorityGetData;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.AllowedChanges;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.CurrentPriorities;
import ru.edmebank.contracts.dto.accounts.AccountPriorityGetResponse.LastUpdated;
import ru.edmebank.contracts.dto.accounts.AccountPriorityResponse;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest.Initiator;
import ru.edmebank.contracts.dto.accounts.AccountPriorityUpdateRequest.InitiatorRole;

import java.time.LocalDateTime;
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

    private String accountId;
    private AccountPriorityUpdateRequest updateRequest;
    private AccountPriorityResponse successResponse;
    private AccountPriorityGetResponse getResponse;

    @BeforeEach
    void setUp() {
        // Настраиваем MockMvc вручную с контроллером и глобальным обработчиком исключений
        AccountPriorityController controller = new AccountPriorityController(accountPriorityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AccountsGlobalExceptionHandler())
                .build();

        // Настраиваем ObjectMapper для работы с Java 8 Date/Time API
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        accountId = "acc123456";

        // Создаем запрос на обновление приоритетов
        updateRequest = new AccountPriorityUpdateRequest();
        updateRequest.setPriorityForWriteOff(true);
        updateRequest.setPriorityForAccrual(false);
        updateRequest.setReason("Изменение приоритетов по согласованию с клиентом");

        Initiator initiator = new Initiator();
        initiator.setId("12345");
        initiator.setName("Иванов Иван Иванович");
        initiator.setRole(InitiatorRole.CREDIT_MANAGER);
        updateRequest.setInitiator(initiator);

        // Создаем успешный ответ на обновление
        successResponse = AccountPriorityResponse.success(
                accountId,
                true,
                false,
                LocalDateTime.of(2025, 6, 13, 16, 15, 31),
                5L
        );

        // Создаем ответ на запрос приоритетов
        getResponse = AccountPriorityGetResponse.success(
                new AccountPriorityGetData(
                        accountId,
                        AccountType.LOAN_PRINCIPAL,
                        new CurrentPriorities(false, true),
                        new AllowedChanges(true, false, List.of("Только один приоритет разрешен для кредитных счетов")),
                        new LastUpdated(
                                LocalDateTime.of(2025, 6, 13, 16, 15, 31),
                                "Смирнова А.П. (CREDIT_MANAGER)"
                        )
                )
        );
    }

    @Test
    void updatePrioritiesSuccess() throws Exception {
        when(accountPriorityService.updatePriorities(eq(accountId), any(AccountPriorityUpdateRequest.class)))
                .thenReturn(successResponse);

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accountId").value(accountId))
                .andExpect(jsonPath("$.data.updatedPriorities.priorityForWriteOff").value(true))
                .andExpect(jsonPath("$.data.updatedPriorities.priorityForAccrual").value(false));
    }

    @Test
    void updatePrioritiesAccountNotFound() throws Exception {
        when(accountPriorityService.updatePriorities(eq("nonexistent"), any(AccountPriorityUpdateRequest.class)))
                .thenThrow(new AccountException("ACCOUNT_NOT_FOUND", "Счет не найден", "nonexistent"));

        mockMvc.perform(put("/api/accounts/{accountId}/priority", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void updatePrioritiesValidationError() throws Exception {
        // Создаем запрос с отсутствующим обязательным полем
        AccountPriorityUpdateRequest invalidRequest = new AccountPriorityUpdateRequest();
        invalidRequest.setPriorityForWriteOff(true);
        invalidRequest.setPriorityForAccrual(false);
        // Отсутствует reason и initiator

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void updatePrioritiesConflictPriorities() throws Exception {
        when(accountPriorityService.updatePriorities(eq(accountId), any(AccountPriorityUpdateRequest.class)))
                .thenThrow(new AccountException("CONFLICT_PRIORITIES",
                        "Для депозитного счета нельзя одновременно установить оба приоритета",
                        Map.of("accountId", accountId)));

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("CONFLICT_PRIORITIES"));
    }

    @Test
    void getPrioritiesSuccess() throws Exception {
        when(accountPriorityService.getPriorities(accountId))
                .thenReturn(getResponse);

        mockMvc.perform(get("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accountId").value(accountId))
                .andExpect(jsonPath("$.data.currentPriorities.priorityForWriteOff").value(false))
                .andExpect(jsonPath("$.data.currentPriorities.priorityForAccrual").value(true));
    }

    @Test
    void getPrioritiesAccountNotFound() throws Exception {
        when(accountPriorityService.getPriorities("nonexistent"))
                .thenThrow(new AccountException("ACCOUNT_NOT_FOUND", "Счет не найден", "nonexistent"));

        mockMvc.perform(get("/api/accounts/{accountId}/priority", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void getPrioritiesInvalidAccountId() throws Exception {
        when(accountPriorityService.getPriorities("invalid-uuid"))
                .thenThrow(new AccountException("INVALID_REQUEST", "Неверный формат идентификатора счета", "invalid-uuid"));

        mockMvc.perform(get("/api/accounts/{accountId}/priority", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }
}
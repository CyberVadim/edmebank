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
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityGetResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityResponse;
import ru.edmebank.accounts.adapter.input.rest.dto.AccountPriorityUpdateRequest;
import ru.edmebank.accounts.app.api.service.AccountPriorityService;
import ru.edmebank.accounts.domain.enums.AccountType;
import ru.edmebank.accounts.fw.exception.AccountNotFoundException;
import ru.edmebank.accounts.fw.exception.GlobalExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    private String accountId;
    private AccountPriorityUpdateRequest updateRequest;
    private AccountPriorityResponse successResponse;
    private AccountPriorityGetResponse getResponse;

    @BeforeEach
    void setUp() {
        // Настраиваем MockMvc вручную с контроллером и глобальным обработчиком исключений
        AccountPriorityController controller = new AccountPriorityController(accountPriorityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Настраиваем ObjectMapper для работы с Java 8 Date/Time API
        objectMapper.findAndRegisterModules();

        accountId = "acc123456";

        // Создаем запрос на обновление приоритетов
        updateRequest = AccountPriorityUpdateRequest.builder()
                .priorityForWriteOff(true)
                .priorityForAccrual(false)
                .reason("Изменение приоритетов по согласованию с клиентом")
                .initiator(AccountPriorityUpdateRequest.Initiator.builder()
                        .id("12345")
                        .name("Иванов Иван Иванович")
                        .role(AccountPriorityUpdateRequest.InitiatorRole.CREDIT_MANAGER)
                        .build())
                .build();

        // Создаем успешный ответ на обновление
        successResponse = AccountPriorityResponse.success(
                accountId,
                true,
                false,
                LocalDateTime.of(2025, 6, 13, 16, 15, 31),
                5L
        );

        // Создаем ответ на запрос приоритетов
        getResponse = AccountPriorityGetResponse.builder()
                .status("SUCCESS")
                .data(AccountPriorityGetResponse.AccountPriorityGetData.builder()
                        .accountId(accountId)
                        .accountType(AccountType.LOAN_PRINCIPAL)
                        .currentPriorities(AccountPriorityGetResponse.CurrentPriorities.builder()
                                .priorityForWriteOff(false)
                                .priorityForAccrual(true)
                                .build())
                        .allowedChanges(AccountPriorityGetResponse.AllowedChanges.builder()
                                .canSetWriteOff(true)
                                .canSetAccrual(false)
                                .reasons(List.of("Только один приоритет разрешен для кредитных счетов"))
                                .build())
                        .lastUpdated(AccountPriorityGetResponse.LastUpdated.builder()
                                .date(LocalDateTime.of(2025, 6, 13, 16, 15, 31))
                                .by("Смирнова А.П. (CREDIT_MANAGER)")
                                .build())
                        .build())
                .build();
    }

    @Test
    void updatePriorities_Success() throws Exception {
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
    void updatePriorities_AccountNotFound() throws Exception {
        when(accountPriorityService.updatePriorities(eq("nonexistent"), any(AccountPriorityUpdateRequest.class)))
                .thenThrow(new AccountNotFoundException("nonexistent"));

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
    void updatePriorities_ValidationError() throws Exception {
        // Удаляем обязательное поле reason
        updateRequest.setReason(null);

        mockMvc.perform(put("/api/accounts/{accountId}/priority", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void getPriorities_Success() throws Exception {
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
    void getPriorities_AccountNotFound() throws Exception {
        when(accountPriorityService.getPriorities("nonexistent"))
                .thenThrow(new AccountNotFoundException("nonexistent"));

        mockMvc.perform(get("/api/accounts/{accountId}/priority", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", UUID.randomUUID().toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_NOT_FOUND"));
    }
}
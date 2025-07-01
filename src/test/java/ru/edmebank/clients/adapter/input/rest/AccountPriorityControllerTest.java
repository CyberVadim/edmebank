package ru.edmebank.clients.adapter.input.rest;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.clients.fw.exception.AccountPriorityException;
import ru.edmebank.clients.fw.security.JwtTokenUtil;
import ru.edmebank.config.AccountPriorityTestConfig;
import ru.edmebank.contracts.dto.request.AccountPriorityUpdateRequest;
import ru.edmebank.contracts.dto.response.AccountPriorityDetailsResponse;
import ru.edmebank.contracts.dto.response.AccountPriorityUpdateResponse;
import ru.edmebank.contracts.enums.AccountPriorityType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountPriorityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AccountPriorityTestConfig.class)
class AccountPriorityControllerTest {

    private static final String API_PATH = "/api/accounts/{accountId}/priority";
    private static final String AUTH_HEADER = "Authorization";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String TEST_REQUEST_ID = "test-request-id";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_USER_NAME = "Test User";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ.signature";
    private static final String AUTH_VALUE = "Bearer " + TEST_TOKEN;
    private static final String TEST_REASON = "Изменение приоритетов в рамках тестирования";
    private static final int INITIAL_VERSION = 1;
    private static final int UPDATED_VERSION = INITIAL_VERSION + 1;
    private static final String ERROR_CONFLICT_PRIORITIES_CODE = "CONFLICT_PRIORITIES";
    private static final String ERROR_CONFLICT_PRIORITIES_MESSAGE =
            "Для кредитных счетов можно установить только один тип приоритета";
    private static final String VALID_JSON = "{"
                                            + "\"priorityForWriteOff\": true,"
                                            + "\"priorityForAccrual\": false,"
                                            + "\"reason\": \"%s\","
                                            + "\"initiator\": {"
                                            + "    \"id\": \"%s\","
                                            + "    \"name\": \"%s\","
                                            + "    \"role\": \"CREDIT_MANAGER\""
                                            + "  }"
                                            + "}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountPriorityService accountPriorityService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        reset(accountPriorityService, jwtTokenUtil);
        accountId = UUID.randomUUID();
        setupJwtMock();
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - успешный сценарий")
    void getAccountPrioritySuccess() throws Exception {
        // Arrange
        when(accountPriorityService.getAccountPriorityDetails(any(UUID.class), anyString()))
                .thenReturn(createDetailsResponse());

        // Act & Assert
        performAuthenticatedGet()
                .andExpect(status().isOk());

        verify(accountPriorityService).getAccountPriorityDetails(eq(accountId), eq(AUTH_VALUE));
    }

    @Test
    @DisplayName("Получение деталей приоритетов счета - отсутствие токена")
    void getAccountPriorityNoToken() throws Exception {
        // Arrange
        when(jwtTokenUtil.extractTokenFromHeader(null)).thenReturn(null);
        doThrow(new AccountPriorityException(
                HttpStatus.UNAUTHORIZED.name(),
                "Отсутствует JWT токен в заголовке Authorization",
                HttpStatus.UNAUTHORIZED
        )).when(accountPriorityService).getAccountPriorityDetails(any(UUID.class), isNull());

        // Act & Assert
        performUnauthenticatedGet()
                .andExpect(status().isUnauthorized());

        verify(accountPriorityService).getAccountPriorityDetails(eq(accountId), isNull());
    }

    @Test
    @DisplayName("Обновление приоритетов счета - успешный сценарий")
    void updateAccountPrioritySuccess() throws Exception {
        // Arrange
        when(accountPriorityService.updateAccountPriority(
                any(UUID.class), any(AccountPriorityUpdateRequest.class), anyString()))
                .thenReturn(createUpdateResponse());

        // Act & Assert
        String json = createValidJson();

        mockMvc.perform(
                        put(API_PATH, accountId)
                                .header(AUTH_HEADER, AUTH_VALUE)
                                .header(REQUEST_ID_HEADER, TEST_REQUEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(accountPriorityService).updateAccountPriority(
                eq(accountId), any(AccountPriorityUpdateRequest.class), eq(AUTH_VALUE));
    }

    @Test
    @DisplayName("Обновление приоритетов счета - обработка исключения")
    void updateAccountPriorityExceptionHandling() throws Exception {
        // Arrange
        doThrow(new AccountPriorityException(
                ERROR_CONFLICT_PRIORITIES_CODE,
                ERROR_CONFLICT_PRIORITIES_MESSAGE,
                HttpStatus.CONFLICT
        )).when(accountPriorityService).updateAccountPriority(
                any(UUID.class), any(AccountPriorityUpdateRequest.class), anyString());

        // Act & Assert
        String json = createValidJson();

        mockMvc.perform(
                        put(API_PATH, accountId)
                                .header(AUTH_HEADER, AUTH_VALUE)
                                .header(REQUEST_ID_HEADER, TEST_REQUEST_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict());

        verify(accountPriorityService).updateAccountPriority(
                eq(accountId), any(AccountPriorityUpdateRequest.class), eq(AUTH_VALUE));
    }

    private void setupJwtMock() {
        when(jwtTokenUtil.extractTokenFromHeader(AUTH_VALUE)).thenReturn(TEST_TOKEN);
        when(jwtTokenUtil.validateToken(TEST_TOKEN)).thenReturn(true);

        Claims mockClaims = Mockito.mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn(TEST_USER_ID);
        when(jwtTokenUtil.getClaims(TEST_TOKEN)).thenReturn(mockClaims);
    }

    private ResultActions performAuthenticatedGet() throws Exception {
        return mockMvc.perform(
                get(API_PATH, accountId)
                        .header(AUTH_HEADER, AUTH_VALUE)
                        .header(REQUEST_ID_HEADER, TEST_REQUEST_ID)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performUnauthenticatedGet() throws Exception {
        return mockMvc.perform(
                get(API_PATH, accountId)
                        .header(REQUEST_ID_HEADER, TEST_REQUEST_ID)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * Создает валидный JSON для тестов обновления приоритетов.
     * Важно: ID инициатора должен совпадать с ID пользователя в JWT токене.
     */
    private String createValidJson() {
        return String.format(VALID_JSON, TEST_REASON, TEST_USER_ID, TEST_USER_NAME);
    }

    private AccountPriorityDetailsResponse createDetailsResponse() {
        return AccountPriorityDetailsResponse.builder()
                .accountId(accountId)
                .accountType(AccountPriorityType.LOAN_PRINCIPAL)
                .currentPriorities(
                        AccountPriorityDetailsResponse.CurrentPriorities.builder()
                                .priorityForWriteOff(false)
                                .priorityForAccrual(true)
                                .build()
                )
                .build();
    }

    private AccountPriorityUpdateResponse createUpdateResponse() {
        return AccountPriorityUpdateResponse.builder()
                .accountId(accountId)
                .updatedPriorities(
                        AccountPriorityUpdateResponse.UpdatedPriorities.builder()
                                .priorityForWriteOff(true)
                                .priorityForAccrual(false)
                                .build()
                )
                .updatedAt(LocalDateTime.now())
                .version(UPDATED_VERSION)
                .build();
    }
}
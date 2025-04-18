package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.service.ClientService;
import com.edmebank.clientmanagement.service.NotificationService;
import com.edmebank.clientmanagement.service.SpectrumService;
import com.edmebank.clientmanagement.service.impl.PassportServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ClientService clientService;
    @MockitoBean
    private SpectrumService spectrumService;
    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private PassportServiceImpl passportService;

    @Test
    public void testPatchClient() throws Exception {
        String systemId = "RequestService";
        String requestBody = "{\"firstName\":\"Иса\", \"lastName\":\"Исаев\", \"email\":\"example@mail.ru\"}";

        mockMvc.perform(patch("/api/v1/clients/10000000-0000-0000-0000-000000000201")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SystemId", systemId)
                .header("Content-Length", 10)
                .content(requestBody))

                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testPatchClientWithNotHeader() throws Exception {
        String systemId = "RequestService";
        String requestBody = "{\"firstName\":\"Иса\", \"lastName\":\"Исаев\", \"email\":\"example@mail.ru\"}";

        mockMvc.perform(patch("/api/v1/clients/10000000-0000-0000-0000-000000000201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Content-Length", 10)
                        .content(requestBody))

                .andExpect(status().isNotFound());
    }
}

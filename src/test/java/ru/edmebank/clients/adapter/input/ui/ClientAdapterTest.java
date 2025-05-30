package ru.edmebank.clients.adapter.input.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientAdapterTest {

    @Autowired
    private MockMvc mockMvc;
    ClassLoader loader = getClass().getClassLoader();

    @Test
    void completeCreateClientTest() throws Exception {
        String jsonContent = Files.readString(Path.of(loader.getResource("test-data/clients/create_client.json").toURI()),
                StandardCharsets.UTF_8);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/ui/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //@Disabled("Временное отключение до исправления валидации")
    @Test
    void createClientWithInvalidLastName() throws Exception {
        String jsonContent = Files.readString(Path.of(loader.getResource("test-data/clients/create_client_invalid_lastname.json").toURI()),
                StandardCharsets.UTF_8);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/ui/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //@Disabled("Временное отключение до исправления валидации")
    @Test
    void createClientWithInvalidBirthDate() throws Exception {
        String jsonContent = Files.readString(Path.of(loader.getResource("test-data/clients/create_client_invalid_birthdate.json").toURI()),
                StandardCharsets.UTF_8);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/ui/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //@Disabled("Временное отключение до исправления валидации")
    @Test
    void createClientWithInvalidPassportSeries() throws Exception {
        String jsonContent = Files.readString(Path.of(loader.getResource("test-data/clients/create_client_invalid_passport_series.json").toURI()),
                StandardCharsets.UTF_8);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/ui/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //@Disabled("Временное отключение до исправления валидации")
    @Test
    void createClientWithInvalidPassportIssueDate() throws Exception {
        String jsonContent = Files.readString(Path.of(loader.getResource("test-data/clients/create_client_invalid_passport_issuedate.json").toURI()),
                StandardCharsets.UTF_8);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/ui/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

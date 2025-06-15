package ru.edmebank.fw.log.sensitive;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ru.edmebank.clients.domain.entity.LogClientTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.edmebank.contracts.enums.MaskingPattern.ADDRESS;
import static ru.edmebank.contracts.enums.MaskingPattern.EMAIL;
import static ru.edmebank.contracts.enums.MaskingPattern.FULL_NAME;
import static ru.edmebank.contracts.enums.MaskingPattern.MASK;
import static ru.edmebank.contracts.enums.MaskingPattern.PHONE;
import static ru.edmebank.contracts.enums.MaskingPattern.POSTAL_CODE;
import static ru.edmebank.clients.fw.log.sensitive.Mask.sensitive;

@Slf4j
public class LogInterceptorAspectTest {
    private ListAppender<ILoggingEvent> listAppender;
    private LogClientTest logClientTest;
    
    @BeforeEach
    public void setUp() throws Exception {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(LogInterceptorAspectTest.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        rootLogger.addAppender(listAppender);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try (InputStream inputStream = getClass().getResourceAsStream("/test-data/clients/test-client.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource '/test-client.json' not found in test resources.");
            }
            logClientTest = objectMapper.readValue(inputStream, LogClientTest.class);
        }
    }
    
    @Test
    public void testAnnotationBasedMasking() {
        log.info("Person: {}", logClientTest);
        String msg = listAppender.list.get(0).getFormattedMessage();
        assertTrue(msg.contains("firstName=***"));
        assertTrue(msg.contains("email=***@mail.ru"));
        assertTrue(msg.contains("phone=+7(999)***-**-67"));
        assertTrue(msg.contains("passportSeries=45**"));
        assertTrue(msg.contains("passportNumber=******"));
        assertTrue(msg.contains("snils=123-***-***-**"));
        assertTrue(msg.contains("address=******, Российская Федерация, *** ***, *** р-н, ул. ***, д. ***, кв. ***"));
        assertTrue(msg.contains("passportSubdivisionCode=770-***"));
    }
    
    @Test
    public void testExplicitMaskArg() {
        log.info("First Name: {}", sensitive(logClientTest.getFirstName(), FULL_NAME));
        String msg = listAppender.list.get(0).getFormattedMessage();
        assertTrue(msg.contains("***"));
    }
    
    @Test
    public void testNoPattern() {
        String email = logClientTest.getEmail();
        log.info("Unmasked: {}", email);
        String msg = listAppender.list.get(0).getFormattedMessage();
        assertTrue(msg.contains(email));
    }
    
    @Test
    public void testMultiplePatterns() {
        String address = logClientTest.getAddress();
        log.info("Address: {}", sensitive(address, POSTAL_CODE, ADDRESS));
        String msg = listAppender.list.get(0).getFormattedMessage();
        assertTrue(msg.contains("******"));
        assertTrue(msg.contains("*** р-н"));
    }
    
    @Test
    public void testMultipleArgs() {
        log.info("id={}, email={}, phone={}, info={}",
                sensitive("12345", MASK),
                sensitive(logClientTest.getEmail(), EMAIL),
                sensitive(logClientTest.getPhone(), PHONE),
                "Additional info"
        );
        String msg = listAppender.list.get(0).getFormattedMessage();
        assertTrue(msg.contains("id=***"));
        assertTrue(msg.contains("email=***@mail.ru"));
        assertTrue(msg.contains("phone=+7(999)***-**-67"));
    }
}
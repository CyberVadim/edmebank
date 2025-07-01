package ru.edmebank.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.edmebank.clients.app.api.service.AccountPriorityService;
import ru.edmebank.clients.fw.security.JwtTokenUtil;

@Configuration
public class AccountPriorityTestConfig {
    @Bean
    public AccountPriorityService accountPriorityService() {
        return Mockito.mock(AccountPriorityService.class);
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return Mockito.mock(JwtTokenUtil.class);
    }
}
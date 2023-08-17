package com.mm.masterthesis.configuration;

import com.mm.masterthesis.jaas.InMemoryLoginModule;
import com.mm.masterthesis.jaas.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    LoginService loginService() {
        return new LoginService();
    }
}

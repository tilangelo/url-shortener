package com.example.shortener.config;

import com.example.shortener.application.port.out.IdGenerator;
import com.example.shortener.infrastructure.id.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {
    @Bean
    public IdGenerator idGenerator() {
        // В будущем workerId можно брать из env
        long workerId = 1L;
        return new SnowflakeIdGenerator(workerId);
    }
}

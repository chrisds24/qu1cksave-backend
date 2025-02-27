package com.qu1cksave.qu1cksave_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {
//    @Bean
//    public DataSource dataSource() {
//        // TODO
//    }

    @Bean
    public String fakeDataSource() {
        return "I am a fake data source";
    }
}

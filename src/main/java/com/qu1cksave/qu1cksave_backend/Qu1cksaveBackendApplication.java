package com.qu1cksave.qu1cksave_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Qu1cksaveBackendApplication {
    // TODO: Where do I put custom @Configuration?
	// - I might need one to specify the DataSource
	// - https://www.marcobehler.com/guides/spring-framework
	// TODO: How to add an ApplicationContext? Do I even need one?
	// - https://www.marcobehler.com/guides/spring-framework
	public static void main(String[] args) {
		SpringApplication.run(Qu1cksaveBackendApplication.class, args);
	}

}

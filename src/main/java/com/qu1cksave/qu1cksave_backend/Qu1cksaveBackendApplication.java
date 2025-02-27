package com.qu1cksave.qu1cksave_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Qu1cksaveBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(Qu1cksaveBackendApplication.class, args);
	}
}

// docker-compose up -d		Run the postgres docker container (in a different terminal)
// docker-compose down		To remove the docker-container

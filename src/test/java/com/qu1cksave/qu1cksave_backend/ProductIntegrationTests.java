package com.qu1cksave.qu1cksave_backend;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Or use this one below (Probably won't need it)
//@SpringBootTest(
//    webEnvironment = WebEnvironment.RANDOM_PORT,
//    classes = Qu1cksaveBackendApplication.class
//)
@TestPropertySource(
    locations = "classpath:application-product-integrationtest.properties"
)
@Testcontainers
// @Import(EmployeeServiceImplTestContextConfiguration.class)
// - Make equivalent if needed
// - Probably won't need to though
//@Sql("/data.sql") // Can this be put here? Probably don't need this either
// From https://www.baeldung.com/spring-boot-testcontainers-integration-test
@ActiveProfiles("tc")
class ProductIntegrationTests {
    @LocalServerPort
    private int port;

    // IF NEEDED
//    @Container
    // ME: Maybe I could use PostgreSQLContainer<>("postgres:latest") ?
    //   Or just postgres
//    static PostgreSQLContainer database = new PostgreSQLContainer<>("postgres:12")
//        .withUsername("duke")
//        .withPassword("secret")
//        .withInitScript("config/INIT.sql")
//        .withDatabaseName("tescontainers");

    // IF NEEDED
//    @DynamicPropertySource
//    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
            String.class)).contains("Hello, World");
    }
}

package com.qu1cksave.qu1cksave_backend;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    @Autowired
    private WebTestClient webTestClient;

//    @Test
//    void shouldCreateNewCustomers() {
//        this.webTestClient
//            .post()
//            .uri("/api/customers")
//            .bodyValue("""
//         {
//        "firstName": "Mike",
//        "lastName": "Thomson",
//        "id": 43
//       }
//        """)
//            .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
//            .exchange()
//            .expectStatus()
//            .isCreated();
//    }

    // https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
    // - Has useful methods/assertions
    @Test
    void shouldGetOneJob() {
        // '018eae1f-d0e7-7fa8-a561-6aa358134f7e'
        // Expected: 'Software Engineer', 'Microsoft', very long description
        this.webTestClient
            .get()
            .uri("/jobs/269a3d55-4eee-4a2e-8c64-e1fe386b76f8")
//            .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.title").isEqualTo("Software Engineer")
            .jsonPath("$.companyName").isEqualTo("Microsoft")
        ;
    }

    // TODO: (5/7/25): I need to set the URI for my whole backend
    //  Ex. /api/v1     instead of just /
    //  So it would become http://localhost:8080/api/v1/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
    //  Instead of http://localhost:8080/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8 (CURRENT)
}

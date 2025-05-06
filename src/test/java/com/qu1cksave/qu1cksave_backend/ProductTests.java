package com.qu1cksave.qu1cksave_backend;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Or use this one below
//@SpringBootTest(
//    webEnvironment = WebEnvironment.RANDOM_PORT,
//    classes = Qu1cksaveBackendApplication.class
//)
@ExtendWith(SpringExtension.class)
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties")
//@Import(EmployeeServiceImplTestContextConfiguration.class) Ex. If needed
class ProductTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
            String.class)).contains("Hello, World");
    }
}

// Resources
// https://spring.io/guides/gs/testing-web
// - contextLoads: test that will fail if the application context cannot start
// - webEnvironment=RANDOM_PORT to start the server with a random port (useful to avoid conflicts in test environments)
//   -- injection of the port with @LocalServerPort
// - Spring Boot has automatically provided a TestRestTemplate for you. All you have to do is add @Autowired to it.
//
 //
// https://www.baeldung.com/spring-boot-testing
// - The integration tests need to start up a container to execute the test cases
//   -- Hence, some additional setup is required for this — all of this is easy in Spring Boot
// - The @SpringBootTest annotation is useful when we need to bootstrap the entire container.
//   The annotation works by creating the ApplicationContext that will be utilized in our tests
// - webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//   -- we’re using WebEnvironment.MOCK here so that the container will operate in a mock servlet environment
//   -- ME: I'm using RANDOM_PORT since I don't want the container to run in a mock environment
// - the @TestPropertySource annotation helps configure the locations of properties files specific to our tests.
//   Note that the property file loaded with @TestPropertySource will override the existing application.properties file.
//   The application-integrationtest.properties contains the details to configure the persistence storage
//   TODO: (5/5/25) ME: I can just copy the application.properties file I have, then change
//    the DB name and URL. Though, I'd also need to dynamically change what the test
//    database uses (In SlugSell, we set process.env.POSTGRES_DB = "test", since the test
//    database uses POSTGRES_DB).
//    - HOWEVER, the process could be different when using Test Containers
// - a test annotated with @SpringBootTest will bootstrap the full application context,
//   which means we can @Autowire any bean that’s picked up by component scanning into our test
//   -- Ex. services, controllers, etc.
// - @TestConfiguration annotation: Use a special test configuration, such as when we want
//   to avoid bootstrapping the real application context
//   -- we can create a separate test configuration class
//   -- Configuration classes annotated with @TestConfiguration are excluded from component scanning.
//      Therefore, we need to import it explicitly in every test where we want to @Autowire it.
//      We can do that with the @Import annotation
// - @ExtendWith(SpringExtension.class) provides a bridge between Spring Boot test features and
//   JUnit. Whenever we are using any Spring Boot testing features in our JUnit tests,
//   this annotation will be required.
// - ME: I can use Mockito to mock AWS calls.
//
//
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
// - @Sql("/data.sql") // Optional: Initialize test data using SQL scripts
//  -- ME: They out this on top of each test method
//     Can I put this at a class level (for the whole test)?
//     Might not need this since the yaml file I'm using has references to data.sql, schema.sql, etc.
//
// https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
// - Both WebTestClient and TestRestTemplate are valid options for blocking integration tests
//   -- But WebTestClient can work for non-blocking apps (which is what it's used for) and
//      with mocked-servlet environments (MockMvc) and and for integration tests against
//      a running servlet container
//   -- IMPORTANT: We enrich this Spring Boot application with the Starter for Spring WebFlux.
//      However, we won’t mix non-blocking and blocking controller endpoints and stick to the blocking Tomcat servlet container.
//      The idea behind this additional dependency (Spring WebFlux) is to get access to the WebClient and the WebTestClient.
//   -- ME: See https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
//      + WebTestClient can be used to perform end-to-end HTTP tests. It can also be used to test Spring MVC and Spring WebFlux applications without a running server via mock server request and response objects
//
//
// https://rieckpil.de/spring-webtestclient-for-efficient-testing-of-your-rest-api/
// - When using both the Spring Boot Starter Web and WebFlux, Spring Boot assumes we want a blocking servlet stack and auto-configures the embedded Tomcat for us\
// - https://rieckpil.de/guide-to-springboottest-for-spring-boot-integration-tests/
// - Spring Boot autoconfigures a WebTestClient bean for us, once our test uses: @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// - The client is already autoconfigured, and the base URL points to our locally running Spring Boot. There’s no need to inject the random port and configure the client


// TODO: Go in this order
// https://spring.io/guides/gs/testing-web  DONE
// https://www.baeldung.com/spring-boot-testing     DONE
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
// https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
// https://www.baeldung.com/docker-test-containers
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
// https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/

// If there's time, might be good to just see:
//   https://www.youtube.com/watch?v=6uSnF6IuWIw

package com.qu1cksave.qu1cksave_backend;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MINUTES;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
	locations = "classpath:application-product-integrationtest.properties"
)
@Testcontainers
class Qu1cksaveBackendApplicationTests {
	// Not needed
//    @LocalServerPort
//    private int port;

	// Non-static field 'postgresUser' cannot be referenced from a static context
	// - Need to make this static to fix
	// - @Value doesn't work
//	@Value("${POSTGRES_DB}")
	private static final String postgresDb = System.getenv("POSTGRES_DB");

	private static String postgresUser = System.getenv("POSTGRES_USER");

	private static String postgresPassword = System.getenv("POSTGRES_PASSWORD");

	// This will use a random port
	@Container
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
		.withDatabaseName(postgresDb)
		.withUsername(postgresUser)
		.withPassword(postgresPassword)
		// sql folder needs to be in resources
		.withInitScripts("sql/schema.sql", "sql/data.sql")
		.withStartupTimeout(Duration.of(3, MINUTES));

	// Not needed
//	@BeforeAll
//	static void beforeAll() {
//		postgreSQLContainer.start();
//	}
//
//	@AfterAll
//	static void afterAll() {
//		postgreSQLContainer.stop();
//	}

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		// Need this since the postgreSQLContainer uses a random port
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		// Don't need these since already set to this
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void contextLoads() {
	}

	// https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
	// - Has useful methods/assertions
	// TODO: (5/10/25) Fix this test. Error in jsonPath("$.title")...
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

	// TODO: (5/7/25): I need to set the URI for my whole backend
	//  Ex. /api/v1     instead of just /
	//  So it would become http://localhost:8080/api/v1/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
	//  Instead of http://localhost:8080/jobs?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8 (CURRENT)
}

// TODO: (5/8/25) Could be useful
// I create the container using PostgreSQL Container with a given username,
//   password, db name, etc.
// Then I could set it in application-product-integrationtest.properties
//   so Spring Data JPA could use it


// ---------------- NOTES ---------------
//
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
//   Note that the property file loaded with @TestPropertySource will override the existing application-product-integrationtest.properties file.
//   The application-product-integrationtest.properties contains the details to configure the persistence storage
//   TODO: (5/5/25) ME: I can just copy the application-product-integrationtest.properties file I have, then change
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
//  -- ME: They put this on top of each test method
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
//
//
// https://www.baeldung.com/docker-test-containers
// - In this tutorial, we’ll be looking at the Java Testcontainers library. It allows us to use Docker containers within our tests.
// - Need to add testcontainers dependency
// - The rule is annotated with @ClassRule. As a result, it will start the Docker container before any test in that class runs.
//   The container will be destroyed after all methods are executed.
// - If you apply @Rule annotation, the GenericContainer rule will start a new container for each test method.
//   And it will stop the container when that test method finishes.
// - For example, we fire up a PostgreSQL container with PostgreSQLContainer rule
//   -- TODO (5/6/25) NOTE: (From ChatGPT) In JUnit 5, the @Container annotation is used for lifecycle management.
//      Static fields annotated with @Container will start and stop the container once for the entire
//      test instance, while instance fields will start and stop the container before and after each test method
//   -- It is also possible to run PostgreSQL as a generic container. But it’d be more difficult to configure the connection
// - If the tests require more complex services, we can specify them in a docker-compose file
//   -- TODO (5/6/25) ME: How do I integrate my repository layer to this container?
//      + FIRST, this is what happens when not using TestContainers
//      + Annotating the test file with @SpringBootTest creates the application context
//        using what was specified in the application-product-integrationtest.properties file (the test version)
//      + When starting an application normally, this connects to the Postgres docker container
//        * Though, it doesn't start the container (which is why we need to manually do docker-compose up -d)
//      + And it actually wouldn't be this container using testcontainers, but the one started using docker-compose up -d
//      + The same thing should happen when running the test
//        * HOWEVER, wouldn't we need to manually change the environment variable before calling docker-compose up -d ???
//        * YES, this is what we do in CSE 187 SlugSell, where we programmatically change
//          process.env.POSTGRES_DB to test in db.ts for the test folder
//          ** We can simply achieve this by through the test application-product-integrationtest.properties file
//        * BUT...it's tedious to have to manually start a container for each test suite (Ex. product, resume, etc.)
//        * So I'll need to have some script that automatically starts up a container when running each test suite
//        * WHICH IS WHERE TESTCONTAINERS COME TO THE RESCUE
//          ** But again, I'm not sure how to tell the repository to use this created container
//          ** Maybe, I can simply just not start it manually myself and let testcontainers handle it?
// - Then, we use DockerComposeContainer rule. This rule will start and run services as defined in the compose file.
//   -- We use getServiceHost and getServicePost methods to build connection address to the service
//   TODO: (5/6/25) ME: If the application context for the tests are created after this container has
//    been started, the datasource, db url, etc. specified in the test application-product-integrationtest.properties would
//    refer to the container created here
//
//
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// - To start using the PostgreSQL instance in a single test class, we have to create a container definition first and then use its parameters to establish a connection
// - This one has an example on how to set up a container with a certain db name, user , password, etc.
// - In addition to the JUnit 4 rules approach, we can modify the JDBC URL and instruct the Testcontainers to create a database instance per test class.
//   This approach will work without requiring us to write some infrastructural code in our tests.
//   -- spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db
//   -- The “tc:” will make Testcontainers instantiate database instances without any code change
//   -- TODO: (5/6/25) ME: jdbc:tc:postgresql://localhost:5432/dev
//      * I'll probably use this
//      * TODO: (5/6/25) NOTE: Replace dev with test if I'm using test as the POSTGRES_DB value
// - System.setProperty() is how we set environment variables
// - As in previous examples, we applied the @ClassRule annotation to a field holding the container definition.
//   This way, the DataSource connection properties are populated with correct values before Spring context creation
//
//
// https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
// - @Testcontainers
//   -- This annotation is from the Testcontainers library and is used to manage container lifecycles automatically during the test lifecycle.
//   -- It ensures that containers are started before any tests run and stopped when tests complete.
// - @Container
//   -- A Testcontainers-specific annotation that designates a field as a container, making sure the container starts before running tests and stops afterward.
//   -- In this case, it's used to manage a PostgreSQL container that emulates a database environment for testing
// - @ServiceConnection
//   -- This annotation is part of Spring Boot’s integration with Testcontainers. It allows automatic service discovery,
//      helping Spring Boot use the PostgreSQLContainer to connect to the
//      PostgreSQL instance.
//
//
// https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/
// - For projects where we have multiple dependencies from Testcontainers, we must align their
//   versions to avoid incompatibilities. Testcontainers provides a Maven Bill of Materials (BOM)
//   for this purpose. Once we define the testcontainers-bom as part of Maven’s dependencyManagement
//   section, we can include all Testcontainers dependencies without specifying their versions.
//   The BOM will align all Testcontainers dependency versions for us
// - TODO: (5/6/25) This one has a way to add dependencies using Gradle
// - Using JUnit Jupiter, we can register the Testcontainers extension with @Testcontainers.
//   Next, we have to identify all our container definitions with @Container
// - .withInitScript("config/INIT.sql")
//   -- ME: This could be a way to fill the db
//- Testcontainers maps the PostgreSQL’s main port (5432) to a random and ephemeral port,
//  so we must override our configuration dynamically.
//  For Spring Boot applications < 2.2.6, we can achieve this with an
//  ApplicationContextInitializer and set the connection parameters dynamically
// TODO: (5/6/25) For applications that use JUnit Jupiter (part of JUnit 5),
//  we can’t use the @ClassRule anymore.
//  The extension model of JUnit Jupiter exceeds the rule/runner API from JUnit 4.
//  With the help of @DynamicPropertySource we can dynamically override the datasource connection parameters
// TODO: (5/6/25) Simplified Spring Boot Configuration with @ServiceConnection
//  Using: JUnit 5 and Spring Boot >= 3.1
//  Traditionally, when using Testcontainers for integration testing, we would define a
//  @DynamicPropertySource to configure the application properties with container-specific
//  details dynamically. With Spring Boot 3.1, we can skip this step by annotating our container
//  fields with @ServiceConnection
//
//
// https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
// - Has start() for @BeforeAll
//
// https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
//- If you are using JUnit 5, there is no need to add the equivalent
//  @ExtendWith(SpringExtension.class) as @SpringBootTest and the other @Test annotations
//  are already annotated with it
//- The @LocalServerPort annotation can be used to inject the actual port used into your test.
//  For convenience, tests that need to make REST calls to the started server can additionally
//  autowire a WebTestClient
//  -- This setup requires spring-webflux on the classpath
//
//
// https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
// - @Testcontainers, @Container
// - Spring Boot’s auto-configuration can consume the details of a service connection and use
//   them to establish a connection to a remote service.
//   When doing so, the connection details take precedence over any connection-related configuration properties.
//   -- @ServiceConnection annotation on the field annotated with @Container
//   -- You’ll need to add the spring-boot-testcontainers module as a test dependency in order to use service connections with Testcontainers.
// - By default all applicable connection details beans will be created for a given Container.
//   For example, a PostgreSQLContainer will create both JdbcConnectionDetails and R2dbcConnectionDetails
// - A slightly more verbose but also more flexible alternative to service connections is @DynamicPropertySource.
//   A static @DynamicPropertySource method allows adding dynamic property values to the Spring Environment


// ------------ Resources -------------
// https://spring.io/guides/gs/testing-web
// https://www.baeldung.com/spring-boot-testing
// https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce
// https://rieckpil.de/spring-boot-testing-mockmvc-vs-webtestclient-vs-testresttemplate/
// https://rieckpil.de/spring-webtestclient-for-efficient-testing-of-your-rest-api/
// https://www.baeldung.com/docker-test-containers
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
// https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/
// https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/

// If there's time, might be good to just see:
//   https://www.youtube.com/watch?v=6uSnF6IuWIw

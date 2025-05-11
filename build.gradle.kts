plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.qu1cksave"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux") // To be able to use WebTestClient
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql")
	// Needed for error:
	//   com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.Instant` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
	// - I added the dependency, but it still doesn't work
	// - I commented out the return new ObjectMapper().writeValueAsString(this)
	//   temporarily since I don't need it yet
//	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	https://java.testcontainers.org/
	testImplementation("org.testcontainers:testcontainers:1.21.0")
//	implementation platform('org.testcontainers:testcontainers-bom:1.21.0')
	implementation(platform("org.testcontainers:testcontainers-bom:1.21.0"))
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

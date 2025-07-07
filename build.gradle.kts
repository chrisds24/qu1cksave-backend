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
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.security:spring-security-crypto")
	// https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-gradle.html
	implementation(platform("software.amazon.awssdk:bom:2.31.77"))
	implementation("software.amazon.awssdk:s3")
	// ------------ Not sure if I'll need these two ------------------
//	implementation("software.amazon.awssdk:sso")
//	implementation("software.amazon.awssdk:ssooidc")
	// --------------------------------------------------------------
//	implementation("io.jsonwebtoken:jjwt")
	// From ChatGPT, since I can't find how to properly add jjwt dependencies
	//   using Gradle w/ Kotlin (can't even find the version for my Spring
	//   Boot version)
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6") // Uses Jackson for JSON serialization
	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	https://java.testcontainers.org/
	testImplementation("org.testcontainers:testcontainers:1.21.0")
	implementation(platform("org.testcontainers:testcontainers-bom:1.21.0"))
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Finding the correct versions
// https://medium.com/@python-javascript-php-html-css/how-to-choose-the-correct-spring-security-crypto-version-for-spring-framework-5-3-27-c3d8330adc0e
// - Script to dynamically find and use the correct jar version through Gradle build automation
	// Use Gradle's dependency constraint mechanism
//	dependencies {
//		implementation platform('org.springframework.boot:spring-boot-dependencies:2.6.3')
//		implementation 'org.springframework.security:spring-security-crypto'
//	}
	// Specify platform dependencies to ensure all versions match
	// Run:    ./gradlew dependencies    to verify the selected versions
//
// https://stackoverflow.com/questions/78726416/which-version-of-spring-security-crypto-jar-compatible-to-support-spring-version
// - https://docs.spring.io/spring-boot/docs/2.7.12/reference/html/dependency-versions.html
//   -- I should look at the dependency versions for my specific spring boot version
// ME: Search spring boot 3.4.3 dependencies
//   https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-dependencies/3.4.3
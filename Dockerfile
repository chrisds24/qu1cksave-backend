# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first to leverage Docker cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the rest of the source code
COPY src src

# Build the application without tests and with layered jar
RUN chmod +x gradlew && \
    ./gradlew bootJar -x test

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the layered jar from the builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Enable layered jar support
ENV SPRING_BOOT_LAYERS_ENABLED=true

# Declare the port the app runs on
EXPOSE 8080

# Use Spring Boot's layered JAR entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
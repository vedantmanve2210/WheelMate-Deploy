# Multi-stage Dockerfile for WheelMate Spring Boot Backend
# Stage 1: Build JAR using Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy Maven wrapper and POM first for dependency caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal production runtime image using JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=builder /app/target/backend-*.jar app.jar

ENV PORT=7070
EXPOSE 7070

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

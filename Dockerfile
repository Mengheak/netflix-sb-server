# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies layer separately
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and compile
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy ONLY the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
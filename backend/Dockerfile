# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven wrapper and project metadata first for dependency caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Download dependencies (will be cached unless pom changes)
RUN ./mvnw dependency:go-offline

# Copy application source
COPY src src

# Build application without running tests
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable jar from build stage
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

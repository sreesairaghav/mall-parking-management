
# Stage 1: Build using Java 25 JDK
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Maven wrapper and project configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw -B dependency:go-offline

# Copy source and build application
COPY src/ src/

RUN ./mvnw -B clean package -DskipTests

# Stage 2: Smaller Alpine Java runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]

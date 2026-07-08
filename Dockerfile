# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application package and skip unit tests to speed up deployment
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image using a lightweight Java 21 JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the compiled jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render assigns a dynamic port via the PORT environment variable, so we expose it
EXPOSE 8080

# Run the spring boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
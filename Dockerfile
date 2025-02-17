# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the source code
COPY . .

# Build the Spring Boot application (skipping tests)
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the first stage
COPY --from=build /app/target/blog-application-0.0.1-SNAPSHOT.jar blog-application.jar

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "blog-application.jar"]

# Stage 1: Build the application using Maven Wrapper
FROM openjdk:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy application source code
COPY src src

# Set execution permissions for the Maven wrapper
RUN chmod +x mvnw

# Build the application (skip tests)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime Docker image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Create a volume to store logs or temp files (optional)
VOLUME /tmp

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar blog-application.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "blog-application.jar"]

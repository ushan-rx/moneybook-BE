
FROM eclipse-temurin:21-jdk as builder

#working directory
WORKDIR /app

# Copy Maven wrapper and the pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

#dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Use a smaller base image for the final container
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
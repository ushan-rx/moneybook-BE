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
COPY --from=builder /app/target/*SNAPSHOT.jar app.jar

# Create non-root user for security
RUN useradd -u 1001 -m appuser
USER appuser

# Expose application port
EXPOSE 8080

# JVM tuning (adjust as needed)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health (can be used by ECS/other orchestrators)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

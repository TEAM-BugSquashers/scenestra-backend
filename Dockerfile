FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file to container
COPY /build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080



# Run the application
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
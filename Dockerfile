FROM eclipse-temurin:21-jdk-alpine

# Install required packages
RUN apt-get update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src/ src/
COPY .env.example .env

# Build the application
RUN mvn clean compile jpro:build

# Expose port
EXPOSE 8080

# Start JPro server
CMD ["mvn", "jpro:run"]
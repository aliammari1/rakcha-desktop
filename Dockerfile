FROM eclipse-temurin:21-jdk-alpine

# Install required packages (Alpine uses apk, not apt-get)
RUN apk update && apk add --no-cache \
    maven \
    curl

# Set working directory
WORKDIR /app

# Copy Maven files first (for better caching)
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B || true

# Copy source code and environment
COPY src/ src/
COPY .env.example .env

# Build the application
RUN mvn clean compile jpro:build -DskipTests

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# Start JPro server
CMD ["mvn", "jpro:run"]
FROM maven:3.9-amazoncorretto-17 AS builder

# Set working directory
WORKDIR /app

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17

WORKDIR /app

# Copy the built artifact from the builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Copy resources needed at runtime
COPY src/main/resources ./resources

# Set environment variables
ENV DB_HOST=mysql
ENV DB_PORT=3306
ENV DB_NAME=rakcha_db
ENV DB_USER=root
ENV DB_PASSWORD=root

# Expose the application port (replace 8080 with your application's port if different)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]

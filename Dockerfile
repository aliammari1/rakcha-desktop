FROM amd64/ubuntu:24.04

# Update package lists
RUN apt-get update

# Install X11 and GTK dependencies for JavaFX
RUN apt-get install -y xorg libgtk-3-0

# Install wget, software-properties-common, and xdg-utils
RUN apt-get install -y wget software-properties-common xdg-utils

# Add the Adoptium (Eclipse Temurin) APT repository and import the GPG key
RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - && \
    add-apt-repository --yes https://packages.adoptium.net/artifactory/deb/

# Install Temurin 21 JDK and Maven with locale support
RUN apt-get update && \
    apt-get install -y temurin-21-jdk maven curl locales && \
    locale-gen en_US.UTF-8 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME and encoding environment variables
ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

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
RUN mvn clean compile -DskipTests

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# Start JPro server with proper encoding and headless configuration
CMD ["mvn", "jpro:run", "-Dfile.encoding=UTF-8", "-Dsun.jnu.encoding=UTF-8", "-Duser.language=en", "-Duser.country=US", "-Djpro.openURLOnStartup=false"]
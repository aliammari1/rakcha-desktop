FROM ubuntu:22.04 AS builder
USER root

# Set non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

# Install basic dependencies
RUN apt-get update && apt-get install -y curl zip unzip ca-certificates git && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Install SDKMAN and setup Java and Maven
RUN curl -s "https://get.sdkman.io" | bash
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
    yes | sdk install java 21.0.2-open && \
    yes | sdk install maven 3.9.4 && \
    sdk use java 21.0.2-open && \
    sdk use maven 3.9.4"

# Set working directory
WORKDIR /app

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && mvn clean package -DskipTests"

# Runtime stage with JavaFX support and noVNC
FROM ubuntu:22.04
USER root

# Set non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

# Install dependencies for JavaFX, X11, and noVNC
RUN apt-get update && apt-get install -y \
    curl \
    zip \
    unzip \
    ca-certificates \
    git \
    xvfb \
    x11vnc \
    xterm \
    fluxbox \
    wmctrl \
    wget \
    net-tools \
    supervisor \
    python3 \
    python3-numpy \
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxrandr2 \
    libasound2 \
    libpango-1.0-0 \
    libcairo2 \
    libatspi2.0-0 \
    libgtk-3-0 \
    libgbm1 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install SDKMAN with Java and Maven
RUN curl -s "https://get.sdkman.io" | bash
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
    yes | sdk install java 21.0.2-open && \
    yes | sdk install maven 3.9.4 && \
    sdk use java 21.0.2-open && \
    sdk use maven 3.9.4"

# Install noVNC
RUN mkdir -p /opt/novnc && \
    git clone https://github.com/novnc/noVNC.git /opt/novnc && \
    git clone https://github.com/novnc/websockify.git /opt/novnc/utils/websockify

WORKDIR /app

# Copy the Maven project structure instead of just the JAR
COPY pom.xml .
COPY src ./src

# Copy supervisor configuration
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Set environment variables for display and locale
ENV DISPLAY=:1
ENV HOME=/root
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8
ENV LC_ALL=C.UTF-8

# Note: Database credentials will be provided by docker-compose environment variables

# Add SDKMAN to PATH
ENV PATH="$PATH:/root/.sdkman/candidates/java/current/bin"

# Create supervisor log directory
RUN mkdir -p /var/log/supervisor

# Create startup script
RUN mkdir -p /var/log/supervisor
COPY <<EOF /app/entrypoint.sh
#!/bin/bash
# Start the supervisord service which manages all the processes
supervisord -c /etc/supervisor/conf.d/supervisord.conf
EOF

RUN chmod +x /app/entrypoint.sh

# Expose ports
EXPOSE 5900 6080

# Set entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]
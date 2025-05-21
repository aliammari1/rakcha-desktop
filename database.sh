#!/usr/bin/bash
# Use this script to start a docker container for a local development database

# TO RUN ON WINDOWS:
# 1. Install WSL (Windows Subsystem for Linux) - https://learn.microsoft.com/en-us/windows/wsl/install
# 2. Install Docker Desktop for Windows - https://docs.docker.com/docker-for-windows/install/
# 3. Open WSL - `wsl`
# 4. Run this script - `./database.sh start`

# On Linux and macOS you can run this script directly - `./database.sh start`

DB_CONTAINER_NAME="rakcha-mysql"
DATABASE_URL="mysql://root:root@localhost:3306/rakcha_db"

# Check if Docker is installed
if ! [ -x "$(command -v docker)" ]; then
    echo -e "Docker is not installed. Please install docker and try again.\nDocker install guide: https://docs.docker.com/engine/install/"
    exit 1
fi

# Check if Docker Compose is installed
if ! [ -x "$(command -v docker-compose)" ]; then
    echo -e "Docker Compose is not installed. Please install docker-compose and try again.\nDocker Compose install guide: https://docs.docker.com/compose/install/"
    exit 1
fi

# Function to start services
start_services() {
    echo "Starting Docker containers using docker-compose..."
    docker-compose up -d
    echo "Docker containers started successfully!"
}

# Function to stop services
stop_services() {
    echo "Stopping Docker containers..."
    docker-compose down
    echo "Docker containers stopped successfully!"
}

# Function to restart services
restart_services() {
    echo "Restarting Docker containers..."
    docker-compose down
    docker-compose up -d
    echo "Docker containers restarted successfully!"
}

# Function to show logs
show_logs() {
    echo "Showing logs for services..."
    docker-compose logs -f
}

# Command handling
case "$1" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    logs)
        show_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac

exit 0

# Inspect the container for its IP address
docker inspect $DB_CONTAINER_NAME | grep IPAddress
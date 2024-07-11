#!/usr/bin/bash
# Use this script to start a docker container for a local development database

# TO RUN ON WINDOWS:
# 1. Install WSL (Windows Subsystem for Linux) - https://learn.microsoft.com/en-us/windows/wsl/install
# 2. Install Docker Desktop for Windows - https://docs.docker.com/docker-for-windows/install/
# 3. Open WSL - `wsl`
# 4. Run this script - `./start-database.sh`

# On Linux and macOS you can run this script directly - `./start-database.sh`

DB_CONTAINER_NAME="rakcha-mysql"
DATABASE_URL="mysql://root:root@localhost:3306/rakcha" # Updated with new root password and container name

# Define any other environment variables you need directly in this script
# For example:
# DB_USER="root"
# DB_NAME="rakcha"

if ! [ -x "$(command -v docker)" ]; then
    echo -e "Docker is not installed. Please install docker and try again.\nDocker install guide: https://docs.docker.com/engine/install/"
    exit 1
fi

if [ "$(docker ps -q -f name=$DB_CONTAINER_NAME)" ]; then
    echo "Database container '$DB_CONTAINER_NAME' already running"
    exit 0
fi

if [ "$(docker ps -q -a -f name=$DB_CONTAINER_NAME)" ]; then
    docker start "$DB_CONTAINER_NAME"
    echo "Existing database container '$DB_CONTAINER_NAME' started"
    exit 0
fi

DB_PASSWORD=$(echo "$DATABASE_URL" | awk -F':' '{print $3}' | awk -F'@' '{print $1}')
DB_PORT=$(echo "$DATABASE_URL" | awk -F':' '{print $4}' | awk -F'\/' '{print $1}')

# Create the MySQL container with the new command
docker run -p 3306:3306 \
    --name $DB_CONTAINER_NAME \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=rakcha \
    -d mysql \
    -h 127.0.0.1 && echo "Database container '$DB_CONTAINER_NAME' was successfully created"

# Wait a bit longer for MySQL to initialize (optional, might be needed for the next command to succeed)
sleep 20

# Import the SQL file into the MySQL database
docker exec -i $DB_CONTAINER_NAME mysql --user=root --password=root rakcha < rakcha_db.sql

# Inspect the container for its IP address
docker inspect $DB_CONTAINER_NAME | grep IPAddress
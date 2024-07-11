#!/usr/bin/bash
# Use this script to start a docker container for a local development database

# TO RUN ON WINDOWS:
# 1. Install WSL (Windows Subsystem for Linux) - https://learn.microsoft.com/en-us/windows/wsl/install
# 2. Install Docker Desktop for Windows - https://docs.docker.com/docker-for-windows/install/
# 3. Open WSL - `wsl`
# 4. Run this script - `./start-database.sh`

# On Linux and macOS you can run this script directly - `./start-database.sh`

DB_CONTAINER_NAME="rakcha-mysql"
DATABASE_URL="mysql://root:password@localhost:3306/rakcha" # Replace with your actual database URL

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

# if [ "$DB_PASSWORD" = "password" ]; then
#     echo "You are using the default database password"
#     read -p "Should we generate a random password for you? [y/N]: " -r REPLY
#     if ! [[ $REPLY =~ ^[Yy]$ ]]; then
#         echo "Please set a password in the script and try again"
#         exit 1
#     fi
#     # Generate a random URL-safe password
#     DB_PASSWORD=$(openssl rand -base64 12 | tr '+/' '-_')
#     # Update DATABASE_URL with the new password
#     DATABASE_URL=$(echo "$DATABASE_URL" | sed -e "s/password/$DB_PASSWORD/")
# fi

# Create the MySQL container
docker run -d \
    --name $DB_CONTAINER_NAME \
    -e MYSQL_ROOT_PASSWORD=password \
    -e MYSQL_DATABASE=rakcha \
    -p 3306:3306 \
    mysql && echo "Database container '$DB_CONTAINER_NAME' was successfully created"

# Wait a bit for MySQL to initialize (optional, might be needed for the next command to succeed)
sleep 50

# Import the SQL file into the MySQL database
docker exec -i $DB_CONTAINER_NAME mysql --user=root --password=password rakcha < rakcha_db.sql

# Inspect the container for its IP address
docker inspect $DB_CONTAINER_NAME | grep IPAddress

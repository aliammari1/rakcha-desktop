#!/bin/bash

echo "🎬 Launching RAKCHA Desktop for Demo..."

# Check if application is built
if [ ! -d "target/classes" ]; then
    echo "🔄 Building application first..."
    mvn clean compile -q
fi

# Launch application
echo "🚀 Starting RAKCHA Desktop..."
mvn javafx:run

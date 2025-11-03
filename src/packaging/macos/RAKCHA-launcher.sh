#!/bin/bash
# RAKCHA macOS Launcher Script
# This script launches the RAKCHA application with proper JVM settings

# Get the application directory
APP_DIR="$(dirname "$(dirname "$(readlink -f "$0")")")"

# Launch the application
"$APP_DIR/MacOS/RAKCHA" "$@"

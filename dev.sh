#!/bin/bash

# RAKCHA Desktop Application - Development Helper Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_success "Java $JAVA_VERSION found"
        else
            print_error "Java 17 or higher required. Found Java $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java not found. Please install Java 17 or higher."
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        print_success "Maven found"
    else
        print_error "Maven not found. Please install Maven 3.6 or higher."
        exit 1
    fi
}

# Function to setup database
setup_database() {
    print_info "Setting up SQLite database..."
    
    # Create data directory
    mkdir -p data
    
    # Set environment variables
    export DB_TYPE=sqlite
    export DB_URL=jdbc:sqlite:data/rakcha_db.sqlite
    export DB_USER=""
    export DB_PASSWORD=""
    
    print_success "Database environment configured"
    echo "  - Type: SQLite"
    echo "  - Location: data/rakcha_db.sqlite"
}

# Function to build the application
build_app() {
    print_info "Building application..."
    
    mvn clean compile
    
    print_success "Application built successfully"
}

# Function to run tests
run_tests() {
    print_info "Running tests..."
    
    # Set test environment
    export TESTFX_HEADLESS=true
    export TESTFX_ROBOT=glass
    export PRISM_ORDER=sw
    
    mvn test
    
    print_success "Tests completed"
}

# Function to run the application
run_app() {
    print_info "Starting RAKCHA Desktop Application..."
    
    # Ensure database is set up
    setup_database
    
    # Run the application
    mvn exec:java -Dexec.mainClass="com.esprit.MainApp"
}

# Function to package the application
package_app() {
    print_info "Packaging application..."
    
    mvn clean package -DskipTests
    
    print_success "Application packaged successfully"
    print_info "JAR file created in target/ directory"
}

# Function to run code quality checks
quality_check() {
    print_info "Running code quality checks..."
    
    print_info "Running Checkstyle..."
    mvn checkstyle:check || print_warning "Checkstyle issues found"
    
    print_info "Running PMD..."
    mvn pmd:check || print_warning "PMD issues found"
    
    print_info "Running SpotBugs..."
    mvn spotbugs:check || print_warning "SpotBugs issues found"
    
    print_success "Code quality checks completed"
}

# Function to update dependencies
update_deps() {
    print_info "Checking for dependency updates..."
    
    mvn versions:display-dependency-updates
    mvn versions:display-plugin-updates
    
    print_info "To update dependencies, run:"
    echo "  mvn versions:use-latest-versions"
}

# Function to generate documentation
generate_docs() {
    print_info "Generating documentation..."
    
    mvn javadoc:javadoc
    
    print_success "Documentation generated in docs/ directory"
}

# Function to clean everything
clean_all() {
    print_info "Cleaning project..."
    
    mvn clean
    rm -rf data/rakcha_db.sqlite
    
    print_success "Project cleaned"
}

# Function to show help
show_help() {
    echo "RAKCHA Desktop Application - Development Helper"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  setup       - Check prerequisites and setup environment"
    echo "  build       - Build the application"
    echo "  test        - Run tests"
    echo "  run         - Run the application"
    echo "  package     - Package the application"
    echo "  quality     - Run code quality checks"
    echo "  update      - Check for dependency updates"
    echo "  docs        - Generate documentation"
    echo "  clean       - Clean project and database"
    echo "  help        - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 setup     # Setup development environment"
    echo "  $0 run       # Run the application"
    echo "  $0 test      # Run all tests"
}

# Main script logic
case "${1:-help}" in
    setup)
        check_prerequisites
        setup_database
        print_success "Development environment setup complete!"
        ;;
    build)
        check_prerequisites
        build_app
        ;;
    test)
        check_prerequisites
        setup_database
        run_tests
        ;;
    run)
        check_prerequisites
        run_app
        ;;
    package)
        check_prerequisites
        package_app
        ;;
    quality)
        check_prerequisites
        quality_check
        ;;
    update)
        check_prerequisites
        update_deps
        ;;
    docs)
        check_prerequisites
        generate_docs
        ;;
    clean)
        clean_all
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac

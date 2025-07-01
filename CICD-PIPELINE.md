# GitHub Actions CI/CD Pipeline for RAKCHA Desktop

This document describes the comprehensive CI/CD pipeline implemented for the RAKCHA JavaFX desktop application with SQLite database integration.

## Overview

The CI/CD pipeline consists of 8 automated workflows that handle building, testing, security scanning, deployment, and maintenance:

## Workflows

### 1. CI - Continuous Integration (`ci.yml`)

**Triggers**: Push/PR to main/develop branches
**Purpose**: Core testing and validation

**Features**:

- Multi-platform testing (Ubuntu, Windows, macOS)
- Unit and integration tests with JavaFX support
- SQLite database integration testing
- Code quality checks (Checkstyle, PMD)
- Artifact generation for each platform

**Environment**:

- Java 17
- SQLite database
- Maven build system
- Headless JavaFX testing

### 2. CD - Continuous Deployment (`cd.yml`)

**Triggers**: Git tags (v\*), manual dispatch
**Purpose**: Build and release application packages

**Features**:

- Cross-platform builds (Windows, Linux, macOS)
- Automated version management
- Distribution package creation with startup scripts
- GitHub Releases with detailed changelog
- Docker image building and publishing

**Artifacts**:

- Platform-specific executable packages
- Startup scripts for each OS
- Documentation and configuration files
- Docker images (multi-architecture)

### 3. Database Integration Tests (`database-tests.yml`)

**Triggers**: Daily schedule, database-related file changes
**Purpose**: Comprehensive database testing

**Features**:

- SQLite integration testing
- MySQL compatibility testing (with service container)
- H2 in-memory database testing
- Cross-database compatibility validation
- Database integrity checks

### 4. JavaFX Application Tests (`javafx-tests.yml`)

**Triggers**: JavaFX/UI related file changes
**Purpose**: UI and JavaFX specific testing

**Features**:

- Headless JavaFX testing on multiple platforms
- UI component validation
- Integration testing with virtual displays
- Performance testing with memory constraints
- Screenshot capture for debugging

### 5. Security and Dependency Scanning (`security.yml`)

**Triggers**: Weekly schedule, push/PR
**Purpose**: Security vulnerability detection

**Features**:

- OWASP Dependency Check with SARIF output
- SpotBugs static analysis
- License compliance checking
- Secret detection with TruffleHog
- Docker image security scanning with Trivy
- Automated security issue creation

### 6. PMD Code Quality Analysis (`pmd.yml`)

**Triggers**: Push/PR, weekly schedule
**Purpose**: Static code analysis

**Features**:

- PMD rule enforcement
- SARIF report generation
- Integration with GitHub Security tab
- Code quality metrics collection

### 7. Dependency Updates (`dependency-updates.yml`)

**Triggers**: Weekly schedule, manual dispatch
**Purpose**: Automated dependency management

**Features**:

- Automated patch version updates
- Security vulnerability detection
- Dependency update reports
- Auto-merge for safe updates
- Security issue creation for critical vulnerabilities

### 8. Documentation and Status Updates (`docs.yml`)

**Triggers**: Documentation changes, workflow completions
**Purpose**: Documentation maintenance

**Features**:

- README badge updates
- Javadoc generation and GitHub Pages deployment
- Project metrics collection
- Repository health checks

## Database Integration

### SQLite (Primary)

- Local file-based database (`data/rakcha_db.sqlite`)
- Automatic schema initialization
- Cross-platform compatibility
- No external dependencies

### MySQL (Optional)

- Service container testing in CI
- Production deployment support
- Connection pooling with HikariCP

### H2 (Testing)

- In-memory database for fast testing
- Development environment support

## Security Features

### Vulnerability Scanning

- **OWASP Dependency Check**: Identifies known vulnerabilities in dependencies
- **SpotBugs**: Static analysis for security bugs
- **Trivy**: Container image vulnerability scanning
- **TruffleHog**: Secret detection in code and history

### Security Reporting

- SARIF format integration with GitHub Security tab
- Automated security issue creation
- Vulnerability suppression management
- License compliance checking

### Access Control

- Minimal required permissions for each workflow
- Secure token usage
- Protected branch enforcement through CI requirements

## Platform Support

### Windows

- Native `.bat` startup scripts
- Windows-specific JAR packaging
- ZIP distribution format

### Linux

- Shell script launchers
- `.tar.gz` distribution format
- Docker container support

### macOS

- Shell script launchers
- `.tar.gz` distribution format
- Apple Silicon (ARM64) Docker support

## Development Workflow

### Local Development

1. Use `dev.sh` script for common tasks
2. Run `./dev.sh setup` for initial environment setup
3. Use `./dev.sh run` to start the application
4. Run `./dev.sh test` for comprehensive testing

### Pull Request Process

1. Create feature branch
2. CI automatically runs on PR creation
3. All checks must pass (tests, security, quality)
4. Code review and approval required
5. Auto-merge available for dependency updates

### Release Process

1. Push tag with `v*` format (e.g., `v1.0.0`)
2. CD workflow automatically triggered
3. Cross-platform builds created
4. GitHub Release published with artifacts
5. Docker images built and published

## Configuration

### Environment Variables

```yaml
DB_TYPE: sqlite # Database type
DB_URL: jdbc:sqlite:data/rakcha_db.sqlite # Database connection
DB_USER: "" # Database username
DB_PASSWORD: "" # Database password
TESTFX_HEADLESS: true # Headless testing
```

### Maven Profiles

- Default profile: SQLite development
- Production profile: Optimized builds
- Testing profile: In-memory H2 database

### Suppression Files

- `owasp-suppressions.xml`: Manages false positive security alerts
- Customized for desktop application context
- Regular review and updates required

## Monitoring and Maintenance

### Automated Monitoring

- Weekly dependency update checks
- Daily database integration testing
- Security vulnerability scanning
- License compliance verification

### Metrics Collection

- Code coverage reports
- Performance benchmarks
- Build time tracking
- Test result trends

### Health Checks

- Repository structure validation
- Essential file presence verification
- Workflow status monitoring
- Documentation currency checks

## Best Practices Implemented

### Testing

- Multi-platform compatibility testing
- Database integration testing
- UI testing with headless environments
- Performance and memory testing

### Security

- Dependency vulnerability scanning
- Static code analysis
- Secret detection
- Container security scanning

### Code Quality

- Automated code style checking
- PMD rule enforcement
- Documentation generation
- License header management

### Deployment

- Automated versioning
- Cross-platform packaging
- Distribution with startup scripts
- Container image publishing

## Troubleshooting

### Common Issues

1. **JavaFX Headless Testing**: Ensure proper JVM arguments and virtual display setup
2. **Database Connectivity**: Verify SQLite file permissions and directory creation
3. **Cross-Platform Builds**: Check platform-specific dependencies and paths
4. **Security Alerts**: Review OWASP suppressions and update as needed

### Debug Information

- Workflow logs available in GitHub Actions tab
- Artifact downloads for investigation
- Test reports and screenshots preserved
- Performance metrics collected

This comprehensive CI/CD pipeline ensures reliable, secure, and maintainable development of the RAKCHA desktop application while supporting multiple databases and platforms.

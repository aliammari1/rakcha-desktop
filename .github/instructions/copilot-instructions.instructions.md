---
applyTo: '**'
---

# JavaFX MVP Development Instructions for Product Hunt Launch

## Project Overview
This is a JavaFX desktop application being prepared for Product Hunt launch. The app uses FXML for UI, Maven for build management, and requires significant optimization and feature enhancement to become MVP-ready. Focus on fixing existing bugs, optimizing logic, and creating professional demo materials.

## Core Technical Requirements

### Build & Dependencies
- Maven project structure with proper dependency management
- Add Hibernate for ORM (absolutely NO Spring Boot or Spring-related packages)
- Add Lombok for reducing boilerplate code across all classes
- Include JavaFX dependencies and proper module configuration
- Add testing frameworks (JUnit 5, TestFX for UI testing)
- Include logging framework (SLF4J with Logback)
- Add any necessary utilities for screenshot and video generation

### Database & Models
- **CRITICAL**: Convert existing models to use Hibernate annotations - DO NOT create new model classes
- Apply Lombok annotations (@Data, @Entity, @Builder, @NoArgsConstructor, @AllArgsConstructor, etc.) to existing models
- Implement proper entity relationships and cascading on existing models
- Fix logic issues and bugs in existing model methods
- Add database migration scripts
- Use H2 for development, support PostgreSQL/MySQL for production
- Optimize existing queries and database interactions

### Code Organization & Architecture
- Implement proper package structure to reduce boilerplate:
  - `models` - Entity classes with Hibernate/Lombok (existing models only)
  - `services` - Business logic layer  
  - `controllers` - FXML controllers
  - `ui` - FXML files and view utilities
  - `utils` - Helper classes and utilities
  - `config` - Configuration classes
- Use dependency injection pattern (manual, no Spring)
- Implement proper exception handling throughout existing codebase
- Add input validation and sanitization
- Fix existing bugs and logic issues in all components

### UI/UX Requirements
- Modern, clean interface design using existing FXML files (optimize, don't recreate)
- Responsive layouts that work on different screen sizes
- Consistent styling with CSS
- Proper error messaging and user feedback
- Loading indicators for long operations
- Keyboard shortcuts and accessibility features
- Dark/light theme support

### Performance & Quality
- Optimize database queries and implement caching
- Use proper threading for non-blocking UI
- Implement proper resource management
- Add comprehensive logging throughout existing codebase
- **Priority**: Fix existing bugs and logic issues - this is critical for MVP
- Optimize memory usage and startup time
- Reduce boilerplate code through proper package organization

## Product Hunt Launch Requirements

### Demo Materials Creation
- Create automated screenshot generation using code/CLI tools:
  - Use JavaFX's WritableImage and ImageIO for programmatic screenshots
  - Implement screenshot utilities in util package
  - Generate screenshots of key features and workflows
  - Export in high-quality PNG format
- Create automated demo video generation:
  - Use FFmpeg or similar CLI tools for screen recording
  - Implement video recording utilities or scripts
  - Record 60-90 second demo showing key features
  - Include smooth transitions and export in 1080p MP4
  - Consider using tools like OBS Studio CLI or automated screen capture

### Demo Content Strategy
- Showcase main application workflows
- Highlight unique value propositions
- Demonstrate key features that solve user problems
- Show before/after improvements if applicable
- Professional presentation with consistent branding

### Documentation
- Create comprehensive README with:
  - Clear project description and value proposition
  - Installation and setup instructions
  - Feature highlights with generated screenshots
  - System requirements
  - Troubleshooting guide
- Add inline code documentation for existing code
- Create user manual/guide

### Packaging & Distribution
- Create executable JAR with all dependencies using Maven
- Generate native installers for Windows/Mac/Linux using jpackage
- Set up proper application icons and metadata
- Create installer with proper permissions and file associations
- Implement proper versioning and release management

## Development Best Practices

### Code Quality & Bug Fixes
- **Priority**: Fix existing logic issues and bugs in current codebase
- Use meaningful variable and method names in existing code
- Implement proper error handling with try-catch blocks
- Add input validation for all user inputs
- Use constants instead of magic numbers/strings
- Implement proper logging levels (DEBUG, INFO, WARN, ERROR)
- Reduce boilerplate through Lombok and proper package organization

### Testing Strategy
- Unit tests for service layer methods (especially existing buggy logic)
- Integration tests for database operations
- UI tests for critical user workflows using TestFX
- Performance tests for key operations

### Security Considerations
- Sanitize all user inputs in existing forms
- Implement proper data validation
- Secure database connections
- Handle sensitive data appropriately

## Strict Prohibitions
- **NO Spring Boot or any Spring Framework dependencies**
- NO web-based frameworks
- NO heavyweight application servers
- DO NOT create new model classes - only optimize existing ones

## MVP Success Criteria
- All existing bugs and logic issues resolved
- Application startup time under 5 seconds
- Memory usage under 200MB for typical usage
- Professional UI/UX that competes with commercial software
- Automated demo materials (screenshots + video) generated via code/CLI
- Complete packaging for distribution
- Comprehensive documentation ready for Product Hunt submission

## Technical Tools for Demo Creation
- FFmpeg for video processing and screen recording
- ImageIO and JavaFX WritableImage for screenshot generation
- CLI tools for automated media generation
- OBS Studio or similar for professional video capture
- Automated screenshot utilities integrated into the application

## Deployment & Release
- Semantic versioning (start with 1.0.0 for MVP)
- Automated build pipeline using Maven
- Code signing for executables
- Release notes and changelog
- Beta testing with feedback collection

Focus on making the existing codebase production-ready rather than adding new features. The goal is a polished, bug-free MVP ready for Product Hunt launch.
---
applyTo: '**'
---
Coding standards, domain knowledge, and preferences that AI should follow.
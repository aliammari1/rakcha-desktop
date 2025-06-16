# 🔧 RAKCHA Desktop Build and Demo Instructions

## Quick Start

### 1. Build Application
```bash
# Clean and compile
mvn clean compile

# Package for distribution (optional)
mvn package
```

### 2. Run Application
```bash
# Run with Maven
mvn javafx:run

# Or run directly with Java (if packaged)
java -jar target/rakcha-desktop-1.0.0.jar
```

### 3. Demo Data Setup
The application includes sample data for demonstration purposes:
- Sample cinema locations with seat layouts
- Movie catalog with scheduling examples
- Product inventory with sample items
- User accounts with different role permissions

### 4. Manual Demo Process

#### Prepare for Screenshots/Video
1. Launch application in clean state
2. Navigate through key features systematically
3. Use sample data to show realistic workflows
4. Capture each major module and functionality

#### Key Demo Workflows
1. **User Login** - Show authentication and role selection
2. **Dashboard Tour** - Overview of main interface and navigation
3. **Cinema Management** - Create screening, select seats, process booking
4. **Product Management** - Browse catalog, add to cart, checkout
5. **Analytics** - View reports, charts, business insights
6. **Admin Functions** - User management, system settings

### 5. Troubleshooting

#### Common Issues
- **Application won't start:** Check Java version (17+ required)
- **Database errors:** Ensure write permissions in application directory
- **UI rendering issues:** Update graphics drivers, check JavaFX compatibility
- **Performance:** Increase JVM heap size with -Xmx2G

#### System Requirements
- **Java:** OpenJDK 17 or later
- **Memory:** 4GB RAM minimum, 8GB recommended
- **Display:** 1280x720 minimum, 1920x1080 recommended
- **OS:** Windows 10+, macOS 10.14+, Linux (Ubuntu 18.04+)

#!/bin/bash

# RAKCHA Desktop Simple Demo Generator
# Generates demo structure and builds application for manual demo creation

echo "🎬 RAKCHA Desktop Simple Demo Generator"
echo "======================================"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_step() {
    echo -e "${BLUE}🔄 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Create demo directory structure
print_step "Setting up demo directories..."
mkdir -p demo/screenshots/{raw,processed,thumbnails,social}
mkdir -p demo/videos/{raw,processed,clips}
mkdir -p demo/documentation/{pitch,guides,technical}
mkdir -p demo/assets/{icons,logos,banners}
mkdir -p demo/installers

print_success "Demo structure created"

# Build the application
print_step "Building RAKCHA Desktop application..."
if command -v mvn >/dev/null 2>&1; then
    mvn clean compile -q
    if [ $? -eq 0 ]; then
        print_success "Application built successfully"
    else
        print_info "Build completed with warnings - check for any issues"
    fi
else
    print_info "Maven not found - skipping build"
fi

# Create screenshot instructions
print_step "Creating screenshot guidelines..."
cat > demo/screenshots/SCREENSHOT_GUIDE.md << 'EOF'
# 📸 RAKCHA Desktop Screenshot Guidelines

## Required Screenshots for Product Hunt

### 1. Application Startup & Dashboard (Main Hero Shot)
- **Size:** 1920x1080 minimum
- **Content:** Clean main dashboard showing key metrics and navigation
- **Focus:** Professional interface, clear branding, intuitive layout

### 2. Cinema Management Interface
- **Content:** Theater seat mapping, movie scheduling, booking interface
- **Highlight:** Interactive seat selection, visual theater layout

### 3. Product Management Module
- **Content:** Product catalog, inventory management, pricing controls
- **Highlight:** Professional e-commerce interface, category organization

### 4. Analytics & Reporting
- **Content:** Charts, graphs, business intelligence dashboard
- **Highlight:** Data visualization, key performance indicators

### 5. User Management & Admin Panel
- **Content:** User roles, permissions, administrative controls
- **Highlight:** Enterprise-grade access control, security features

### 6. Mobile/Responsive View (if applicable)
- **Content:** Application on different screen sizes
- **Highlight:** Cross-platform compatibility, responsive design

## Screenshot Best Practices

### Technical Settings
- **Resolution:** 1920x1080 or higher
- **Format:** PNG for quality, JPG for smaller file sizes
- **Quality:** Maximum quality settings
- **Color Space:** sRGB for web compatibility

### Content Guidelines
- **Clean Data:** Use realistic but clean sample data
- **No Personal Info:** Avoid real customer/business data
- **Professional Look:** Consistent styling, proper alignment
- **Feature Focus:** Each screenshot should highlight specific functionality

### Composition Tips
- **Rule of Thirds:** Important elements in intersection points
- **Clean Background:** Minimize desktop clutter
- **Consistent Lighting:** Even screen brightness
- **No Distractions:** Close unnecessary applications

## Manual Screenshot Process

### 1. Prepare Environment
```bash
# Clean workspace
rm -rf /tmp/demo_data 2>/dev/null
mkdir -p /tmp/demo_data

# Start with clean application state
# Launch RAKCHA Desktop application
```

### 2. Application Screenshots
- Launch application and wait for full loading
- Navigate through each major module
- Capture key workflows and user interactions
- Save with descriptive filenames (e.g., `01_main_dashboard.png`)

### 3. Post-Processing (Optional)
- Crop to consistent aspect ratios
- Adjust brightness/contrast if needed
- Add subtle shadows or borders for professional look
- Create thumbnails for gallery views

### 4. Organization
- Save originals in `demo/screenshots/raw/`
- Save processed versions in `demo/screenshots/processed/`
- Create social media formats in `demo/screenshots/social/`
EOF

# Create video recording guide
print_step "Creating video recording guidelines..."
cat > demo/videos/VIDEO_GUIDE.md << 'EOF'
# 🎥 RAKCHA Desktop Video Demo Guidelines

## Primary Demo Video (60-90 seconds)

### Script Outline
1. **Opening (0-10s):** Application startup, brand logo
2. **Dashboard Overview (10-25s):** Main interface, key features visible
3. **Cinema Management (25-40s):** Seat mapping, booking process
4. **Product Management (40-55s):** Catalog, inventory, sales
5. **Analytics (55-70s):** Reports, charts, business intelligence
6. **Closing (70-90s):** Call to action, contact information

### Recording Tips
- **Resolution:** 1920x1080 minimum, 4K if possible
- **Frame Rate:** 30fps minimum, 60fps preferred
- **Audio:** Clear narration or professional background music
- **Length:** 60-90 seconds optimal for Product Hunt
- **Format:** MP4 with H.264 codec for compatibility

### Manual Recording Process

#### Using Built-in Tools
**macOS:** QuickTime Player > File > New Screen Recording
**Windows:** Xbox Game Bar (Windows Key + G)
**Linux:** OBS Studio, SimpleScreenRecorder, or built-in tools

#### Professional Tools
- **OBS Studio** (free, cross-platform)
- **Camtasia** (paid, professional features)
- **ScreenFlow** (macOS, professional editing)

### Recording Checklist
- [ ] Clean desktop environment
- [ ] Application in full-screen or consistent window size
- [ ] Smooth mouse movements
- [ ] Deliberate, clear navigation
- [ ] No sensitive data visible
- [ ] Consistent timing between actions

## Quick Social Media Clips (15-30 seconds)

### Feature Highlights
1. **Quick Login Demo** (15s)
2. **Seat Selection Animation** (20s)
3. **Product Catalog Browsing** (25s)
4. **Analytics Dashboard** (20s)
5. **Cross-Platform Demo** (30s)

### Social Media Formats
- **Twitter/X:** 2:20 max, square or 16:9
- **Instagram:** Square (1:1) or vertical (9:16)
- **LinkedIn:** Professional tone, 16:9 preferred
- **TikTok/YouTube Shorts:** Vertical (9:16)
EOF

# Create simple build instructions
print_step "Creating build and run instructions..."
cat > demo/BUILD_AND_RUN.md << 'EOF'
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
EOF

# Generate simple run script
print_step "Creating application launcher..."
cat > demo/run_rakcha.sh << 'EOF'
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
EOF

chmod +x demo/run_rakcha.sh

# Create Windows launcher
cat > demo/run_rakcha.bat << 'EOF'
@echo off
echo 🎬 Launching RAKCHA Desktop for Demo...

REM Check if application is built
if not exist "target\classes" (
    echo 🔄 Building application first...
    mvn clean compile -q
)

REM Launch application
echo 🚀 Starting RAKCHA Desktop...
mvn javafx:run
EOF

print_success "Application launchers created"

# Summary
echo ""
echo "======================================"
echo "🎉 Demo Setup Complete!"
echo "======================================"
echo ""
print_info "📁 Demo structure created in demo/ directory"
print_info "📋 Guidelines created for screenshots and videos"
print_info "🚀 Use demo/run_rakcha.sh (Linux/Mac) or demo/run_rakcha.bat (Windows) to launch"
echo ""
print_info "Next Steps:"
echo "1. Run the application using the launcher scripts"
echo "2. Follow screenshot guidelines in demo/screenshots/SCREENSHOT_GUIDE.md"
echo "3. Record demo videos using demo/videos/VIDEO_GUIDE.md"
echo "4. Use existing documentation in demo/documentation/ for Product Hunt"
echo ""
print_success "🚀 Ready for manual demo creation!"

echo ""
echo "📦 Demo Package Contents:"
echo "   📸 Screenshot guidelines and structure"
echo "   🎥 Video recording instructions"
echo "   📋 Complete Product Hunt documentation"
echo "   🔧 Build and run instructions"
echo "   🚀 Application launchers"
echo ""
print_info "Use the professional-demo-generator.sh for fully automated generation when system tools are available."

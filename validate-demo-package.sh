#!/bin/bash

# RAKCHA Desktop Demo Validation Script
# Verifies that all demo materials and build are ready

echo "🔍 RAKCHA Desktop Demo Package Validation"
echo "========================================"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

success_count=0
error_count=0

print_check() {
    if [ $2 -eq 0 ]; then
        echo -e "${GREEN}✅ $1${NC}"
        ((success_count++))
    else
        echo -e "${RED}❌ $1${NC}"
        ((error_count++))
    fi
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

echo ""
echo "🏗️  Build System Validation"
echo "=========================="

# Check Java
if command -v java >/dev/null 2>&1; then
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" -ge 17 ]; then
        print_check "Java 17+ available (version: $java_version)" 0
    else
        print_check "Java 17+ required (found: $java_version)" 1
    fi
else
    print_check "Java installation" 1
fi

# Check Maven
if command -v mvn >/dev/null 2>&1; then
    print_check "Maven available" 0
else
    print_check "Maven installation" 1
fi

# Test build
echo ""
echo "🔨 Application Build Test"
echo "======================="
if mvn clean compile -q >/dev/null 2>&1; then
    print_check "Application builds successfully" 0
else
    print_check "Application build" 1
fi

echo ""
echo "📁 Demo Package Structure"
echo "======================="

# Check demo directories
demo_dirs=(
    "demo"
    "demo/screenshots"
    "demo/videos" 
    "demo/documentation"
    "demo/assets"
)

for dir in "${demo_dirs[@]}"; do
    if [ -d "$dir" ]; then
        print_check "Directory: $dir" 0
    else
        print_check "Directory: $dir" 1
    fi
done

echo ""
echo "📋 Documentation Validation"
echo "========================="

# Check key documentation files
docs=(
    "demo/documentation/pitch/PRODUCT_HUNT_SUBMISSION.md"
    "demo/documentation/guides/DEMO_GUIDE.md"
    "demo/documentation/guides/RAKCHA_USER_GUIDE.md"
    "demo/documentation/technical/RAKCHA_TECHNICAL_SPECS.md"
    "demo/screenshots/SCREENSHOT_GUIDE.md"
    "demo/videos/VIDEO_GUIDE.md"
    "demo/BUILD_AND_RUN.md"
    "demo/DEMO_PACKAGE_SUMMARY.md"
    "DEMO_GENERATION_SUCCESS.md"
)

for doc in "${docs[@]}"; do
    if [ -f "$doc" ]; then
        print_check "Documentation: $(basename "$doc")" 0
    else
        print_check "Documentation: $(basename "$doc")" 1
    fi
done

echo ""
echo "🚀 Application Launchers"
echo "====================="

# Check launcher scripts
if [ -f "demo/run_rakcha.sh" ] && [ -x "demo/run_rakcha.sh" ]; then
    print_check "Unix/Linux launcher (run_rakcha.sh)" 0
else
    print_check "Unix/Linux launcher (run_rakcha.sh)" 1
fi

if [ -f "demo/run_rakcha.bat" ]; then
    print_check "Windows launcher (run_rakcha.bat)" 0
else
    print_check "Windows launcher (run_rakcha.bat)" 1
fi

echo ""
echo "🛠️  Optional Tools Check"
echo "====================="

# Check optional tools for automated generation
if command -v ffmpeg >/dev/null 2>&1; then
    print_info "FFmpeg available - video generation enabled"
else
    print_info "FFmpeg not available - manual video recording needed"
fi

if command -v convert >/dev/null 2>&1; then
    print_info "ImageMagick available - image processing enabled"
else
    print_info "ImageMagick not available - manual image editing needed"
fi

if command -v xvfb-run >/dev/null 2>&1; then
    print_info "Xvfb available - headless screenshot generation possible"
else
    print_info "Xvfb not available - GUI mode required for screenshots"
fi

echo ""
echo "📊 Validation Summary"
echo "=================="
echo -e "${GREEN}✅ Successful checks: $success_count${NC}"
if [ $error_count -gt 0 ]; then
    echo -e "${RED}❌ Failed checks: $error_count${NC}"
fi

echo ""
if [ $error_count -eq 0 ]; then
    echo -e "${GREEN}🎉 Demo package is ready for Product Hunt launch!${NC}"
    echo ""
    echo "🚀 Next Steps:"
    echo "1. Run demo/run_rakcha.sh to launch the application"
    echo "2. Follow demo/screenshots/SCREENSHOT_GUIDE.md for capturing screenshots"
    echo "3. Use demo/videos/VIDEO_GUIDE.md for recording demo videos"
    echo "4. Review demo/documentation/pitch/PRODUCT_HUNT_SUBMISSION.md for launch content"
    echo ""
    echo -e "${BLUE}📦 Complete demo package available in demo/ directory${NC}"
else
    echo -e "${YELLOW}⚠️  Some checks failed - review issues above before launch${NC}"
    echo ""
    echo "🔧 Common Solutions:"
    echo "- Install Java 17+ for application development"
    echo "- Install Maven for build automation"
    echo "- Install FFmpeg for automated video generation"
    echo "- Install ImageMagick for automated image processing"
fi

echo ""
echo "📈 Ready for Product Hunt Success!"
echo "Target: Top 5 ranking, 500+ upvotes, 1000+ website visits"

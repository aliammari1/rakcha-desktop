#!/bin/bash

# Automated Video Demo Generator for RAKCHA Desktop
# Creates professional demo videos using CLI tools

echo "🎥 RAKCHA Desktop Automated Video Generator"
echo "=========================================="

# Configuration
DEMO_DIR="demo"
VIDEOS_DIR="$DEMO_DIR/videos"
SCREENSHOTS_DIR="$DEMO_DIR/screenshots"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
VIDEO_WIDTH=1920
VIDEO_HEIGHT=1080
VIDEO_FPS=30
VIDEO_QUALITY=23  # CRF value (lower = better quality)

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_step() {
    echo -e "${BLUE}🔄 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Check prerequisites
check_requirements() {
    print_step "Checking requirements..."
    
    local all_good=true
    
    if ! command -v ffmpeg >/dev/null 2>&1; then
        print_error "FFmpeg is required for video generation"
        print_info "Install with: sudo apt-get install ffmpeg (Ubuntu/Debian)"
        print_info "Install with: brew install ffmpeg (macOS)"
        all_good=false
    else
        print_success "FFmpeg found"
    fi
    
    if ! command -v java >/dev/null 2>&1; then
        print_error "Java is required to run screenshot generator"
        all_good=false
    else
        print_success "Java found"
    fi
    
    if [ "$all_good" = false ]; then
        print_error "Please install missing requirements"
        exit 1
    fi
}

# Create directory structure
setup_directories() {
    print_step "Setting up video directories..."
    
    mkdir -p "$VIDEOS_DIR"/{raw,processed,clips,social}
    mkdir -p "$SCREENSHOTS_DIR"/{raw,processed,thumbnails,social}
    
    print_success "Directory structure created"
}

# Generate screenshots first
generate_screenshots() {
    print_step "Generating demo screenshots..."
    
    # Compile and run the screenshot generator
    if ! mvn compile -q; then
        print_error "Failed to compile project"
        return 1
    fi
    
    # Run the automated screenshot generator
    java -cp "target/classes:target/lib/*" com.esprit.utils.AutoDemoScreenshotGenerator
    
    local screenshot_count=$(find "$SCREENSHOTS_DIR/raw" -name "*.png" 2>/dev/null | wc -l)
    if [ "$screenshot_count" -gt 0 ]; then
        print_success "Generated $screenshot_count screenshots"
        return 0
    else
        print_error "No screenshots generated"
        return 1
    fi
}

# Create slideshow video from screenshots
create_slideshow_video() {
    print_step "Creating slideshow video from screenshots..."
    
    local screenshot_dir="$SCREENSHOTS_DIR/raw"
    local output_file="$VIDEOS_DIR/processed/rakcha_slideshow_demo_$TIMESTAMP.mp4"
    
    if [ ! "$(ls -A $screenshot_dir/*.png 2>/dev/null)" ]; then
        print_error "No screenshots found in $screenshot_dir"
        return 1
    fi
    
    # Create slideshow with 3 seconds per image, smooth transitions
    ffmpeg -y -loglevel warning \
        -framerate 1/3 \
        -pattern_type glob \
        -i "$screenshot_dir/*.png" \
        -vf "scale=$VIDEO_WIDTH:$VIDEO_HEIGHT:force_original_aspect_ratio=decrease,pad=$VIDEO_WIDTH:$VIDEO_HEIGHT:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=$VIDEO_FPS,fade=in:0:15,fade=out:st=2.5:d=0.5" \
        -c:v libx264 \
        -preset medium \
        -crf $VIDEO_QUALITY \
        -pix_fmt yuv420p \
        -movflags +faststart \
        "$output_file"
    
    if [ $? -eq 0 ]; then
        print_success "Slideshow video created: $(basename "$output_file")"
        return 0
    else
        print_error "Failed to create slideshow video"
        return 1
    fi
}

# Create animated demo with text overlays
create_animated_demo() {
    print_step "Creating animated demo with text overlays..."
    
    local input_video="$VIDEOS_DIR/processed/rakcha_slideshow_demo_$TIMESTAMP.mp4"
    local output_file="$VIDEOS_DIR/processed/rakcha_animated_demo_$TIMESTAMP.mp4"
    
    if [ ! -f "$input_video" ]; then
        print_error "Input slideshow video not found"
        return 1
    fi
    
    # Create text overlay file
    local overlay_file="/tmp/rakcha_overlays.txt"
    cat > "$overlay_file" << 'EOF'
# Text overlays for RAKCHA Desktop demo
# Format: start_time-end_time: text

0-3: RAKCHA Desktop - Professional Cinema Management
3-6: Comprehensive Dashboard Overview
6-9: Interactive Cinema Seat Management
9-12: Advanced Product Catalog System
12-15: Real-time Analytics & Reporting
15-18: Enterprise User Management
18-21: Ready for Product Hunt Launch!
EOF
    
    # Add text overlays and transitions
    ffmpeg -y -loglevel warning \
        -i "$input_video" \
        -vf "drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='RAKCHA Desktop':fontcolor=white:fontsize=48:x=(w-text_w)/2:y=100:enable='between(t,0,3)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf:text='Professional Cinema Management':fontcolor=white:fontsize=24:x=(w-text_w)/2:y=150:enable='between(t,0,3)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Comprehensive Dashboard':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=100:enable='between(t,3,6)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Cinema Seat Management':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=100:enable='between(t,6,9)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Product Catalog System':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=100:enable='between(t,9,12)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Analytics & Reporting':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=100:enable='between(t,12,15)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='User Management':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=100:enable='between(t,15,18)',
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Product Hunt Ready!':fontcolor=#FFD700:fontsize=42:x=(w-text_w)/2:y=100:enable='between(t,18,21)'" \
        -c:v libx264 \
        -preset medium \
        -crf $VIDEO_QUALITY \
        -pix_fmt yuv420p \
        -movflags +faststart \
        "$output_file"
    
    if [ $? -eq 0 ]; then
        print_success "Animated demo created: $(basename "$output_file")"
        rm -f "$overlay_file"
        return 0
    else
        print_error "Failed to create animated demo"
        return 1
    fi
}

# Create social media clips
create_social_clips() {
    print_step "Creating social media clips..."
    
    local main_video="$VIDEOS_DIR/processed/rakcha_animated_demo_$TIMESTAMP.mp4"
    
    if [ ! -f "$main_video" ]; then
        print_error "Main demo video not found"
        return 1
    fi
    
    # 30-second teaser for Twitter/LinkedIn
    ffmpeg -y -loglevel warning \
        -i "$main_video" \
        -t 30 \
        -vf "scale=1200:675:force_original_aspect_ratio=increase,crop=1200:675" \
        -c:v libx264 \
        -preset fast \
        -crf 25 \
        -movflags +faststart \
        "$VIDEOS_DIR/social/rakcha_teaser_30s_$TIMESTAMP.mp4"
    
    # 15-second quick demo for Instagram/TikTok
    ffmpeg -y -loglevel warning \
        -i "$main_video" \
        -t 15 \
        -vf "scale=1080:1080:force_original_aspect_ratio=increase,crop=1080:1080" \
        -c:v libx264 \
        -preset fast \
        -crf 25 \
        -movflags +faststart \
        "$VIDEOS_DIR/social/rakcha_quick_15s_$TIMESTAMP.mp4"
    
    # 60-second Product Hunt optimized
    ffmpeg -y -loglevel warning \
        -i "$main_video" \
        -t 60 \
        -vf "scale=1200:675:force_original_aspect_ratio=increase,crop=1200:675" \
        -c:v libx264 \
        -preset medium \
        -crf 23 \
        -movflags +faststart \
        "$VIDEOS_DIR/social/rakcha_product_hunt_60s_$TIMESTAMP.mp4"
    
    print_success "Social media clips created"
}

# Create video thumbnails
create_thumbnails() {
    print_step "Creating video thumbnails..."
    
    for video in "$VIDEOS_DIR/processed"/*.mp4; do
        if [ -f "$video" ]; then
            local basename=$(basename "$video" .mp4)
            local thumbnail="$VIDEOS_DIR/processed/${basename}_thumbnail.jpg"
            
            # Extract frame at 25% of video duration for thumbnail
            ffmpeg -y -loglevel warning \
                -i "$video" \
                -vf "select=eq(pict_type\,I),scale=640:360" \
                -frames:v 1 \
                "$thumbnail"
        fi
    done
    
    print_success "Video thumbnails created"
}

# Add intro/outro to videos
add_branding() {
    print_step "Adding branding elements..."
    
    # Create simple intro video (3 seconds)
    ffmpeg -y -loglevel warning \
        -f lavfi \
        -i "color=c=#667eea:s=${VIDEO_WIDTH}x${VIDEO_HEIGHT}:d=3" \
        -vf "drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='RAKCHA Desktop':fontcolor=white:fontsize=72:x=(w-text_w)/2:y=(h-text_h)/2-50,
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf:text='Professional Cinema Management':fontcolor=white:fontsize=32:x=(w-text_w)/2:y=(h-text_h)/2+50" \
        -c:v libx264 \
        -preset fast \
        -crf $VIDEO_QUALITY \
        -pix_fmt yuv420p \
        "/tmp/rakcha_intro.mp4"
    
    # Create simple outro video (2 seconds)
    ffmpeg -y -loglevel warning \
        -f lavfi \
        -i "color=c=#764ba2:s=${VIDEO_WIDTH}x${VIDEO_HEIGHT}:d=2" \
        -vf "drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf:text='Try RAKCHA Desktop Today!':fontcolor=white:fontsize=48:x=(w-text_w)/2:y=(h-text_h)/2-30,
             drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf:text='Product Hunt Launch Ready':fontcolor=#FFD700:fontsize=28:x=(w-text_w)/2:y=(h-text_h)/2+30" \
        -c:v libx264 \
        -preset fast \
        -crf $VIDEO_QUALITY \
        -pix_fmt yuv420p \
        "/tmp/rakcha_outro.mp4"
    
    # Combine intro + main video + outro
    local main_video="$VIDEOS_DIR/processed/rakcha_animated_demo_$TIMESTAMP.mp4"
    local final_video="$VIDEOS_DIR/processed/rakcha_final_demo_$TIMESTAMP.mp4"
    
    if [ -f "$main_video" ]; then
        # Create file list for concatenation
        echo "file '/tmp/rakcha_intro.mp4'" > /tmp/concat_list.txt
        echo "file '$main_video'" >> /tmp/concat_list.txt
        echo "file '/tmp/rakcha_outro.mp4'" >> /tmp/concat_list.txt
        
        ffmpeg -y -loglevel warning \
            -f concat \
            -safe 0 \
            -i /tmp/concat_list.txt \
            -c copy \
            -movflags +faststart \
            "$final_video"
        
        if [ $? -eq 0 ]; then
            print_success "Final branded demo created: $(basename "$final_video")"
        fi
        
        # Cleanup
        rm -f /tmp/rakcha_intro.mp4 /tmp/rakcha_outro.mp4 /tmp/concat_list.txt
    fi
}

# Generate video summary report
generate_report() {
    print_step "Generating video report..."
    
    local report_file="$VIDEOS_DIR/VIDEO_GENERATION_REPORT.md"
    
    cat > "$report_file" << EOF
# 🎥 RAKCHA Desktop Video Generation Report

**Generated:** $(date)
**Timestamp:** $TIMESTAMP

## 📊 Generation Summary

### Screenshots Generated
$(find "$SCREENSHOTS_DIR/raw" -name "*.png" 2>/dev/null | wc -l) high-quality screenshots created

### Videos Created
$(find "$VIDEOS_DIR/processed" -name "*.mp4" 2>/dev/null | wc -l) video files generated

## 📁 Generated Files

### Main Demo Videos
$(ls -la "$VIDEOS_DIR/processed"/*.mp4 2>/dev/null | awk '{print "- " $9 " (" $5 " bytes)"}' || echo "- No main videos found")

### Social Media Clips
$(ls -la "$VIDEOS_DIR/social"/*.mp4 2>/dev/null | awk '{print "- " $9 " (" $5 " bytes)"}' || echo "- No social clips found")

### Video Specifications
- **Resolution:** ${VIDEO_WIDTH}x${VIDEO_HEIGHT}
- **Frame Rate:** ${VIDEO_FPS} FPS
- **Video Codec:** H.264 (libx264)
- **Quality:** CRF $VIDEO_QUALITY (high quality)
- **Format:** MP4 with fast start for web streaming

## 🎯 Usage Recommendations

### For Product Hunt
Use the main demo video (60-90 seconds) as the primary gallery video.

### For Social Media
- **Twitter/LinkedIn:** Use 30-second teaser clips
- **Instagram/TikTok:** Use 15-second square format clips
- **Website/Blog:** Use full demo with thumbnails

### For Presentations
Use the final branded demo with intro/outro for professional presentations.

## 📈 Technical Quality

All videos are optimized for:
- Web streaming (fast start)
- High quality compression
- Cross-platform compatibility
- Mobile device playback

---

**🎬 Demo generation completed successfully!**
**Ready for Product Hunt launch.**
EOF
    
    print_success "Video report generated: $report_file"
}

# Main execution
main() {
    echo ""
    print_step "Starting automated video generation..."
    echo ""
    
    check_requirements
    setup_directories
    
    # Generate screenshots first
    if generate_screenshots; then
        # Create videos from screenshots
        if create_slideshow_video; then
            create_animated_demo
            create_social_clips
            create_thumbnails
            add_branding
        fi
    else
        print_error "Screenshot generation failed - cannot create videos"
        exit 1
    fi
    
    generate_report
    
    echo ""
    print_success "🎉 Automated video generation completed!"
    echo ""
    print_info "Generated files:"
    print_info "📸 Screenshots: $SCREENSHOTS_DIR/raw/"
    print_info "🎥 Main videos: $VIDEOS_DIR/processed/"
    print_info "📱 Social clips: $VIDEOS_DIR/social/"
    print_info "📋 Report: $VIDEOS_DIR/VIDEO_GENERATION_REPORT.md"
    echo ""
    print_success "🚀 Ready for Product Hunt launch!"
}

# Execute main function
main "$@"

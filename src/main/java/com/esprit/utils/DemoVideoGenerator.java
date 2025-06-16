package com.esprit.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for generating demo videos for Product Hunt presentation Uses
 * FFmpeg through JavaCV for professional video creation
 */
public class DemoVideoGenerator {

    private static final Logger log = LoggerFactory.getLogger(DemoVideoGenerator.class);
    private static final String VIDEOS_DIR = "demo/videos";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd_HH-mm-ss";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    // Video settings optimized for Product Hunt demo
    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1080;
    private static final double FRAME_RATE = 30.0;
    private static final int VIDEO_BITRATE = 4000000; // 4 Mbps for high quality

    static {
        createVideosDirectory();
    }

    /**
     * Create videos directory if it doesn't exist
     */
    private static void createVideosDirectory() {
        try {
            Path videosPath = Paths.get(VIDEOS_DIR);
            if (!Files.exists(videosPath)) {
                Files.createDirectories(videosPath);
                log.info("Created videos directory: {}", videosPath.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Failed to create videos directory", e);
        }
    }

    /**
     * Create a demo video from a series of screenshots
     *
     * @param screenshotPaths
     *            Array of screenshot file paths
     * @param outputFilename
     *            Output video filename (without extension)
     * @param durationPerImage
     *            Duration in seconds for each image
     * @return Path to the generated video file
     */
    public static Path createVideoFromScreenshots(Path[] screenshotPaths, String outputFilename,
            double durationPerImage) {
        if (screenshotPaths == null || screenshotPaths.length == 0) {
            log.warn("Cannot create video: No screenshots provided");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String fileName = outputFilename != null ? outputFilename : "demo_" + timestamp;
            Path outputPath = Paths.get(VIDEOS_DIR, fileName + ".mp4");

            // Create video recorder
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath.toFile(), VIDEO_WIDTH, VIDEO_HEIGHT);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(FRAME_RATE);
            recorder.setVideoBitrate(VIDEO_BITRATE);
            recorder.setVideoQuality(0); // Highest quality

            // High quality encoding options
            recorder.setVideoOption("preset", "slow");
            recorder.setVideoOption("crf", "18"); // High quality
            recorder.setVideoOption("pix_fmt", "yuv420p");

            recorder.start();

            // Create converter for images
            Java2DFrameConverter converter = new Java2DFrameConverter();
            OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

            int framesPerImage = (int) (FRAME_RATE * durationPerImage);

            for (Path screenshotPath : screenshotPaths) {
                if (!Files.exists(screenshotPath)) {
                    log.warn("Screenshot file not found: {}", screenshotPath);
                    continue;
                }

                // Load and process image
                java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(screenshotPath.toFile());
                if (image == null) {
                    log.warn("Failed to load image: {}", screenshotPath);
                    continue;
                }

                // Resize image to video dimensions
                java.awt.image.BufferedImage resizedImage = resizeImage(image, VIDEO_WIDTH, VIDEO_HEIGHT);
                Frame frame = converter.convert(resizedImage);

                // Record frame multiple times for desired duration
                for (int i = 0; i < framesPerImage; i++) {
                    recorder.record(frame);
                }

                log.debug("Added image to video: {}", screenshotPath.getFileName());
            }

            recorder.stop();
            recorder.release();

            log.info("Demo video created successfully: {}", outputPath.toAbsolutePath());
            return outputPath;

        } catch (Exception e) {
            log.error("Failed to create video from screenshots", e);
            return null;
        }
    }

    /**
     * Create a screen recording using FFmpeg (requires external FFmpeg
     * installation)
     *
     * @param durationSeconds
     *            Duration of recording in seconds
     * @param outputFilename
     *            Output filename (without extension)
     * @return Path to the generated video file or null if failed
     */
    public static Path createScreenRecording(int durationSeconds, String outputFilename) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String fileName = outputFilename != null ? outputFilename : "screen_recording_" + timestamp;
            Path outputPath = Paths.get(VIDEOS_DIR, fileName + ".mp4");

            // Build FFmpeg command for screen recording
            ProcessBuilder pb = new ProcessBuilder();

            // Different commands for different operating systems
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows - capture desktop
                pb.command("ffmpeg", "-f", "gdigrab", "-framerate", "30", "-i", "desktop", "-t",
                        String.valueOf(durationSeconds), "-c:v", "libx264", "-preset", "ultrafast", "-crf", "18",
                        outputPath.toString());
            } else if (os.contains("mac")) {
                // macOS - capture screen
                pb.command("ffmpeg", "-f", "avfoundation", "-framerate", "30", "-i", "1", "-t",
                        String.valueOf(durationSeconds), "-c:v", "libx264", "-preset", "ultrafast", "-crf", "18",
                        outputPath.toString());
            } else {
                // Linux - capture X11 display
                pb.command("ffmpeg", "-f", "x11grab", "-framerate", "30", "-i", ":0.0", "-t",
                        String.valueOf(durationSeconds), "-c:v", "libx264", "-preset", "ultrafast", "-crf", "18",
                        outputPath.toString());
            }

            log.info("Starting screen recording for {} seconds...", durationSeconds);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0 && Files.exists(outputPath)) {
                log.info("Screen recording completed: {}", outputPath.toAbsolutePath());
                return outputPath;
            } else {
                log.error("Screen recording failed with exit code: {}", exitCode);
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to create screen recording", e);
            return null;
        }
    }

    /**
     * Resize image to fit video dimensions while maintaining aspect ratio
     */
    private static java.awt.image.BufferedImage resizeImage(java.awt.image.BufferedImage original, int targetWidth,
            int targetHeight) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Calculate scale to fit while maintaining aspect ratio
        double scaleX = (double) targetWidth / originalWidth;
        double scaleY = (double) targetHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        java.awt.image.BufferedImage resized = new java.awt.image.BufferedImage(targetWidth, targetHeight,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = resized.createGraphics();

        // Fill background with black
        g2d.setColor(java.awt.Color.BLACK);
        g2d.fillRect(0, 0, targetWidth, targetHeight);

        // Draw scaled image centered
        int x = (targetWidth - scaledWidth) / 2;
        int y = (targetHeight - scaledHeight) / 2;
        g2d.drawImage(original, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return resized;
    }

    /**
     * Generate a complete demo video workflow This combines multiple screenshots
     * into a professional demo video
     */
    public static Path generateCompleteDemo() {
        log.info("Generating complete demo video...");

        try {
            // Get all screenshots from the screenshots directory
            Path screenshotsDir = DemoScreenshotGenerator.getScreenshotsDirectory();
            if (!Files.exists(screenshotsDir)) {
                log.warn("Screenshots directory does not exist");
                return null;
            }

            File[] screenshotFiles = screenshotsDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
            if (screenshotFiles == null || screenshotFiles.length == 0) {
                log.warn("No screenshots found for demo video");
                return null;
            }

            // Sort by name for consistent ordering
            java.util.Arrays.sort(screenshotFiles);

            Path[] screenshotPaths = new Path[screenshotFiles.length];
            for (int i = 0; i < screenshotFiles.length; i++) {
                screenshotPaths[i] = screenshotFiles[i].toPath();
            }

            return createVideoFromScreenshots(screenshotPaths, "rakcha_demo_complete", 3.0);

        } catch (Exception e) {
            log.error("Failed to generate complete demo video", e);
            return null;
        }
    }

    /**
     * Get the videos directory path
     */
    public static Path getVideosDirectory() {
        return Paths.get(VIDEOS_DIR);
    }
}

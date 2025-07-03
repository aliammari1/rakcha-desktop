package com.esprit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Utility class for generating high-quality screenshots for Product Hunt demo
 * materials This class provides automated screenshot generation capabilities
 * for JavaFX applications
 */
public class DemoScreenshotGenerator {

    private static final Logger log = LoggerFactory.getLogger(DemoScreenshotGenerator.class);
    private static final String SCREENSHOTS_DIR = "demo/screenshots";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd_HH-mm-ss";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    static {
        createScreenshotsDirectory();
    }

    /**
     * Create screenshots directory if it doesn't exist
     */
    private static void createScreenshotsDirectory() {
        try {
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsPath)) {
                Files.createDirectories(screenshotsPath);
                log.info("Created screenshots directory: {}", screenshotsPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create screenshots directory", e);
        }
    }

    /**
     * Capture screenshot of entire stage/window
     *
     * @param stage
     *                 The JavaFX Stage to capture
     * @param filename
     *                 Custom filename (without extension)
     * @return Path to the generated screenshot file
     */
    public static Path captureStage(Stage stage, String filename) {
        if (stage == null || stage.getScene() == null) {
            log.warn("Cannot capture screenshot: Stage or Scene is null");
            return null;
        }

        try {
            Scene scene = stage.getScene();
            WritableImage writableImage = scene.snapshot(null);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String fileName = filename != null ? filename : "stage_" + timestamp;
            Path outputPath = Paths.get(SCREENSHOTS_DIR, fileName + ".png");

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(bufferedImage, "PNG", outputPath.toFile());

            log.info("Screenshot captured: {}", outputPath.toAbsolutePath());
            return outputPath;

        } catch (IOException e) {
            log.error("Failed to capture stage screenshot", e);
            return null;
        }
    }

    /**
     * Capture screenshot of specific node/component
     *
     * @param node
     *                 The JavaFX Node to capture
     * @param filename
     *                 Custom filename (without extension)
     * @return Path to the generated screenshot file
     */
    public static Path captureNode(Node node, String filename) {
        if (node == null) {
            log.warn("Cannot capture screenshot: Node is null");
            return null;
        }

        try {
            SnapshotParameters params = new SnapshotParameters();
            WritableImage writableImage = node.snapshot(params, null);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String fileName = filename != null ? filename : "node_" + timestamp;
            Path outputPath = Paths.get(SCREENSHOTS_DIR, fileName + ".png");

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(bufferedImage, "PNG", outputPath.toFile());

            log.info("Node screenshot captured: {}", outputPath.toAbsolutePath());
            return outputPath;

        } catch (IOException e) {
            log.error("Failed to capture node screenshot", e);
            return null;
        }
    }

    /**
     * Capture multiple screenshots of different scenes/stages
     *
     * @param stages
     *                     Array of stages to capture
     * @param baseFilename
     *                     Base filename (will be appended with numbers)
     * @return Array of paths to generated screenshot files
     */
    public static Path[] captureMultipleStages(Stage[] stages, String baseFilename) {
        if (stages == null || stages.length == 0) {
            log.warn("Cannot capture screenshots: No stages provided");
            return new Path[0];
        }

        Path[] results = new Path[stages.length];
        for (int i = 0; i < stages.length; i++) {
            String filename = baseFilename + "_" + (i + 1);
            results[i] = captureStage(stages[i], filename);
        }

        log.info("Captured {} screenshots", results.length);
        return results;
    }

    /**
     * Generate demo workflow screenshots This method captures key application
     * states for Product Hunt demo
     */
    public static void generateDemoWorkflow() {
        log.info("Starting demo workflow screenshot generation...");
        // This method can be called to capture predefined demo scenarios
        // Implementation would depend on specific application flows
    }

    /**
     * Clean up old screenshots (keeps only recent ones)
     *
     * @param keepCount
     *                  Number of recent screenshots to keep
     */
    public static void cleanupOldScreenshots(int keepCount) {
        try {
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsPath)) {
                return;
            }

            File[] files = screenshotsPath.toFile().listFiles((dir, name) -> name.endsWith(".png"));
            if (files == null || files.length <= keepCount) {
                return;
            }

            // Sort by last modified time (newest first)
            java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            // Delete old files
            for (int i = keepCount; i < files.length; i++) {
                if (files[i].delete()) {
                    log.debug("Deleted old screenshot: {}", files[i].getName());
                }
            }

            log.info("Cleaned up {} old screenshots, kept {} recent ones", files.length - keepCount,
                    Math.min(keepCount, files.length));

        } catch (Exception e) {
            log.error("Failed to cleanup old screenshots", e);
        }
    }

    /**
     * Get the screenshots directory path
     *
     * @return Path to screenshots directory
     */
    public static Path getScreenshotsDirectory() {
        return Paths.get(SCREENSHOTS_DIR);
    }
}

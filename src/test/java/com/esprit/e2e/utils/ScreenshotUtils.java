package com.esprit.e2e.utils;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for capturing screenshots during E2E testing.
 * <p>
 * This class provides methods to capture screenshots of JavaFX applications
 * during testing for documentation and debugging purposes. Screenshots are
 * automatically saved with timestamps and can be used for demo materials.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScreenshotUtils {

    /**
     * Default screenshot directory relative to project root.
     */
    private static final String SCREENSHOT_DIR = "demo/screenshots";

    /**
     * Date/time formatter for screenshot filenames.
     */
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    /**
     * Initializes the screenshot directory.
     * <p>
     * Creates the screenshot directory if it doesn't exist, ensuring
     * that screenshot capture operations will succeed.
     * </p>
     */
    public static void initializeScreenshotDirectory() {
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotPath);
        } catch (IOException e) {
            System.err.println("Failed to create screenshot directory: " + e.getMessage());
        }
    }

    /**
     * Captures a screenshot of the current scene.
     * <p>
     * Takes a screenshot of the currently active JavaFX scene and saves it
     * to the screenshots directory with a timestamped filename.
     * </p>
     * 
     * @param testClass   the test class taking the screenshot
     * @param testMethod  the test method name
     * @param description a brief description of what the screenshot shows
     * @return the path to the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(ApplicationTest testClass, String testMethod, String description) {
        return captureScreenshot(testClass, testMethod, description, null);
    }

    /**
     * Captures a screenshot of a specific stage.
     * <p>
     * Takes a screenshot of the specified JavaFX stage and saves it
     * to the screenshots directory with a timestamped filename.
     * </p>
     * 
     * @param testClass   the test class taking the screenshot
     * @param testMethod  the test method name
     * @param description a brief description of what the screenshot shows
     * @param stage       the specific stage to capture, or null for primary stage
     * @return the path to the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(ApplicationTest testClass, String testMethod,
            String description, Stage stage) {
        initializeScreenshotDirectory();

        final CountDownLatch latch = new CountDownLatch(1);
        final String[] resultPath = new String[1];

        Platform.runLater(() -> {
            try {
                Stage targetStage = stage;
                if (targetStage == null) {
                    // Get the primary stage
                    targetStage = testClass.listTargetWindows().stream()
                            .filter(window -> window instanceof Stage)
                            .map(window -> (Stage) window)
                            .findFirst()
                            .orElse(null);
                }

                if (targetStage != null && targetStage.getScene() != null) {
                    Scene scene = targetStage.getScene();
                    WritableImage writableImage = scene.snapshot(null);

                    // Convert to BufferedImage
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                    // Generate filename
                    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                    String className = testClass.getClass().getSimpleName();
                    String filename = String.format("%s_%s_%s_%s.png",
                            className, testMethod, description.replaceAll("[^a-zA-Z0-9]", "_"), timestamp);

                    File outputFile = new File(SCREENSHOT_DIR, filename);
                    ImageIO.write(bufferedImage, "PNG", outputFile);

                    resultPath[0] = outputFile.getAbsolutePath();
                    System.out.println("Screenshot captured: " + resultPath[0]);
                } else {
                    System.err.println("No stage or scene available for screenshot");
                }
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            // Wait for screenshot to complete (max 5 seconds)
            if (latch.await(5, TimeUnit.SECONDS)) {
                return resultPath[0];
            } else {
                System.err.println("Screenshot capture timed out");
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Screenshot capture interrupted");
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific node.
     * <p>
     * Takes a screenshot of a specific JavaFX node and saves it
     * to the screenshots directory. Useful for capturing specific
     * UI components during testing.
     * </p>
     * 
     * @param testClass   the test class taking the screenshot
     * @param testMethod  the test method name
     * @param description a brief description of what the screenshot shows
     * @param node        the specific node to capture
     * @return the path to the saved screenshot file, or null if capture failed
     */
    public static String captureNodeScreenshot(ApplicationTest testClass, String testMethod,
            String description, Node node) {
        if (node == null) {
            System.err.println("Cannot capture screenshot: node is null");
            return null;
        }

        initializeScreenshotDirectory();

        final CountDownLatch latch = new CountDownLatch(1);
        final String[] resultPath = new String[1];

        Platform.runLater(() -> {
            try {
                WritableImage writableImage = node.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                // Generate filename
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String className = testClass.getClass().getSimpleName();
                String filename = String.format("%s_%s_%s_node_%s.png",
                        className, testMethod, description.replaceAll("[^a-zA-Z0-9]", "_"), timestamp);

                File outputFile = new File(SCREENSHOT_DIR, filename);
                ImageIO.write(bufferedImage, "PNG", outputFile);

                resultPath[0] = outputFile.getAbsolutePath();
                System.out.println("Node screenshot captured: " + resultPath[0]);
            } catch (Exception e) {
                System.err.println("Failed to capture node screenshot: " + e.getMessage());
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            if (latch.await(5, TimeUnit.SECONDS)) {
                return resultPath[0];
            } else {
                System.err.println("Node screenshot capture timed out");
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Node screenshot capture interrupted");
            return null;
        }
    }

    /**
     * Captures multiple screenshots with a delay between each.
     * <p>
     * Useful for capturing a sequence of UI interactions during testing.
     * Each screenshot is saved with a sequence number in the filename.
     * </p>
     * 
     * @param testClass   the test class taking the screenshots
     * @param testMethod  the test method name
     * @param description base description for the screenshot sequence
     * @param count       number of screenshots to take
     * @param delayMs     delay in milliseconds between screenshots
     * @return array of paths to saved screenshot files
     */
    public static String[] captureScreenshotSequence(ApplicationTest testClass, String testMethod,
            String description, int count, long delayMs) {
        String[] results = new String[count];

        for (int i = 0; i < count; i++) {
            String sequenceDescription = String.format("%s_seq%02d", description, i + 1);
            results[i] = captureScreenshot(testClass, testMethod, sequenceDescription);

            if (i < count - 1) { // Don't delay after the last screenshot
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return results;
    }

    /**
     * Creates a screenshot capture report.
     * <p>
     * Generates a markdown report listing all screenshots captured during
     * a test session with their descriptions and file paths.
     * </p>
     * 
     * @param testClass       the test class that captured screenshots
     * @param screenshotPaths array of screenshot file paths
     * @param descriptions    array of descriptions for each screenshot
     */
    public static void createScreenshotReport(ApplicationTest testClass,
            String[] screenshotPaths, String[] descriptions) {
        try {
            String reportName = String.format("screenshot_report_%s_%s.md",
                    testClass.getClass().getSimpleName(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            File reportFile = new File(SCREENSHOT_DIR, reportName);
            StringBuilder report = new StringBuilder();

            report.append("# Screenshot Report\n\n");
            report.append("**Test Class:** ").append(testClass.getClass().getSimpleName()).append("\n");
            report.append("**Generated:** ").append(LocalDateTime.now()).append("\n\n");

            for (int i = 0; i < screenshotPaths.length && i < descriptions.length; i++) {
                if (screenshotPaths[i] != null) {
                    String filename = new File(screenshotPaths[i]).getName();
                    report.append("## ").append(descriptions[i]).append("\n\n");
                    report.append("**File:** `").append(filename).append("`\n\n");
                    report.append("![").append(descriptions[i]).append("](").append(filename).append(")\n\n");
                }
            }

            Files.write(reportFile.toPath(), report.toString().getBytes());
            System.out.println("Screenshot report created: " + reportFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to create screenshot report: " + e.getMessage());
        }
    }
}

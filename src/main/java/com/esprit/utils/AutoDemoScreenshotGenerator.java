package com.esprit.utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Automated Screenshot Generator for RAKCHA Desktop Demo
 * Generates high-quality screenshots programmatically using JavaFX
 */
public class AutoDemoScreenshotGenerator extends Application {

    private static final Logger logger = Logger.getLogger(AutoDemoScreenshotGenerator.class.getName());
    private static final String OUTPUT_DIR = "demo/screenshots/raw";
    private static final int SCREENSHOT_WIDTH = 1920;
    private static final int SCREENSHOT_HEIGHT = 1080;
    private static final int DELAY_MS = 2000;

    private static CountDownLatch latch = new CountDownLatch(1);
    private static int screenshotCount = 0;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Ensure output directory exists
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            // Create demo scenarios
            generateDemoScenarios(primaryStage);

        } catch (Exception e) {
            logger.severe("Error generating screenshots: " + e.getMessage());
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void generateDemoScenarios(Stage stage) throws InterruptedException {
        // Scenario 1: Application Startup/Welcome Screen
        generateWelcomeScreen(stage);
        Thread.sleep(DELAY_MS);

        // Scenario 2: Main Dashboard
        generateMainDashboard(stage);
        Thread.sleep(DELAY_MS);

        // Scenario 3: Cinema Management Interface
        generateCinemaManagement(stage);
        Thread.sleep(DELAY_MS);

        // Scenario 4: Product Catalog
        generateProductCatalog(stage);
        Thread.sleep(DELAY_MS);

        // Scenario 5: Analytics Dashboard
        generateAnalyticsDashboard(stage);
        Thread.sleep(DELAY_MS);

        // Scenario 6: User Management
        generateUserManagement(stage);
        Thread.sleep(DELAY_MS);

        logger.info("Generated " + screenshotCount + " screenshots successfully");
    }

    private void generateWelcomeScreen(Stage stage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
                "-fx-padding: 50px; -fx-alignment: center;");

        Label title = new Label("RAKCHA Desktop");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Professional Cinema & Entertainment Management");
        subtitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-opacity: 0.9;");

        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle("-fx-font-size: 18px; -fx-padding: 15px 40px; " +
                "-fx-background-color: white; -fx-text-fill: #667eea; " +
                "-fx-background-radius: 25px;");

        root.getChildren().addAll(title, subtitle, loginBtn);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - Welcome");
        stage.show();

        Platform.runLater(() -> {
            takeScreenshot(stage, "01_welcome_screen");
        });
    }

    private void generateMainDashboard(Stage stage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px;");

        // Header
        Label header = new Label("RAKCHA Dashboard");
        header.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Quick stats
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: white; -fx-padding: 20px; " +
                "-fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label todayRevenue = new Label("Today's Revenue: $12,450");
        todayRevenue.setStyle("-fx-font-size: 18px; -fx-text-fill: #27ae60;");

        Label totalBookings = new Label("Total Bookings: 245");
        totalBookings.setStyle("-fx-font-size: 18px; -fx-text-fill: #3498db;");

        Label activeScreens = new Label("Active Screens: 8/12");
        activeScreens.setStyle("-fx-font-size: 18px; -fx-text-fill: #e74c3c;");

        statsBox.getChildren().addAll(todayRevenue, totalBookings, activeScreens);

        // Navigation buttons
        VBox navBox = new VBox(10);
        String[] navItems = { "Cinema Management", "Product Catalog", "Customer Database",
                "Analytics & Reports", "User Management", "System Settings" };

        for (String item : navItems) {
            Button navBtn = new Button(item);
            navBtn.setStyle("-fx-font-size: 16px; -fx-padding: 15px 20px; " +
                    "-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 5px; -fx-min-width: 200px;");
            navBox.getChildren().add(navBtn);
        }

        root.getChildren().addAll(header, statsBox, navBox);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - Main Dashboard");

        Platform.runLater(() -> {
            takeScreenshot(stage, "02_main_dashboard");
        });
    }

    private void generateCinemaManagement(Stage stage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px;");

        Label header = new Label("Cinema Management - Screen Layout");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Simulate seat map
        VBox seatMap = new VBox(5);
        seatMap.setStyle("-fx-background-color: white; -fx-padding: 30px; " +
                "-fx-background-radius: 10px; -fx-alignment: center;");

        Label screenLabel = new Label("🎬 SCREEN");
        screenLabel.setStyle("-fx-font-size: 20px; -fx-background-color: #34495e; " +
                "-fx-text-fill: white; -fx-padding: 10px 50px; -fx-background-radius: 5px;");

        // Row indicators and seats
        for (int row = 1; row <= 8; row++) {
            VBox rowBox = new VBox(2);
            Label rowLabel = new Label("Row " + (char) ('A' + row - 1));
            rowLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            StringBuilder seatRow = new StringBuilder();
            for (int seat = 1; seat <= 12; seat++) {
                if (seat % 3 == 0) {
                    seatRow.append("🟢 "); // Available
                } else if (seat % 5 == 0) {
                    seatRow.append("🔴 "); // Occupied
                } else {
                    seatRow.append("🟡 "); // Selected
                }
            }

            Label seats = new Label(seatRow.toString());
            seats.setStyle("-fx-font-size: 16px;");

            rowBox.getChildren().addAll(rowLabel, seats);
            seatMap.getChildren().add(rowBox);
        }

        seatMap.getChildren().add(0, screenLabel);

        // Movie info
        VBox movieInfo = new VBox(10);
        movieInfo.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        Label movieTitle = new Label("Now Showing: Avatar: The Way of Water");
        movieTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label showTime = new Label("Showtime: 7:30 PM | Runtime: 192 min | Rating: PG-13");
        showTime.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        Label pricing = new Label("Pricing: Regular $12 | Premium $18 | VIP $25");
        pricing.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        movieInfo.getChildren().addAll(movieTitle, showTime, pricing);

        root.getChildren().addAll(header, seatMap, movieInfo);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - Cinema Management");

        Platform.runLater(() -> {
            takeScreenshot(stage, "03_cinema_management");
        });
    }

    private void generateProductCatalog(Stage stage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px;");

        Label header = new Label("Product Catalog & Inventory");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Product grid simulation
        VBox productGrid = new VBox(10);
        productGrid.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        String[] categories = { "🍿 Concessions", "🥤 Beverages", "🎬 Merchandise", "🎫 Gift Cards" };
        String[][] products = {
                { "Large Popcorn - $8.50", "Medium Popcorn - $6.50", "Small Popcorn - $4.50" },
                { "Large Soda - $5.50", "Medium Soda - $4.50", "Water Bottle - $2.50" },
                { "Movie T-Shirt - $19.99", "Poster - $12.99", "Collectible Mug - $15.99" },
                { "$25 Gift Card", "$50 Gift Card", "$100 Gift Card" }
        };

        for (int i = 0; i < categories.length; i++) {
            Label categoryLabel = new Label(categories[i]);
            categoryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

            VBox categoryBox = new VBox(5);
            for (String product : products[i]) {
                Label productLabel = new Label("  • " + product);
                productLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
                categoryBox.getChildren().add(productLabel);
            }

            productGrid.getChildren().addAll(categoryLabel, categoryBox);
        }

        // Sales summary
        VBox salesSummary = new VBox(10);
        salesSummary.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        Label salesHeader = new Label("Today's Sales Summary");
        salesHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label totalSales = new Label("Total Sales: $3,247.50");
        totalSales.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        Label topItem = new Label("Top Item: Large Popcorn (145 sold)");
        topItem.setStyle("-fx-font-size: 16px; -fx-text-fill: #3498db;");

        Label avgOrder = new Label("Average Order: $12.75");
        avgOrder.setStyle("-fx-font-size: 16px; -fx-text-fill: #9b59b6;");

        salesSummary.getChildren().addAll(salesHeader, totalSales, topItem, avgOrder);

        root.getChildren().addAll(header, productGrid, salesSummary);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - Product Catalog");

        Platform.runLater(() -> {
            takeScreenshot(stage, "04_product_catalog");
        });
    }

    private void generateAnalyticsDashboard(Stage stage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px;");

        Label header = new Label("Analytics & Business Intelligence");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // KPI Cards
        VBox kpiBox = new VBox(15);

        String[] kpis = {
                "📊 Revenue This Month: $156,750 (+12.5%)",
                "🎫 Tickets Sold: 12,450 (+8.2%)",
                "👥 Customer Satisfaction: 4.7/5.0",
                "🏆 Occupancy Rate: 78.5% (+5.1%)"
        };

        for (String kpi : kpis) {
            Label kpiLabel = new Label(kpi);
            kpiLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white; " +
                    "-fx-padding: 20px; -fx-background-radius: 10px; " +
                    "-fx-text-fill: #2c3e50; -fx-min-width: 400px;");
            kpiBox.getChildren().add(kpiLabel);
        }

        // Chart simulation
        VBox chartBox = new VBox(10);
        chartBox.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        Label chartTitle = new Label("Revenue Trend - Last 30 Days");
        chartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Simple ASCII chart
        String chart = "📈 Revenue Chart\n" +
                "   |\n" +
                "15k|    ▄▄\n" +
                "12k|  ▄▄  ▄▄\n" +
                " 9k|▄▄  ▄▄  ▄▄\n" +
                " 6k|  ▄▄  ▄▄  ▄▄\n" +
                " 3k|▄▄  ▄▄  ▄▄  ▄▄\n" +
                "   +─────────────\n" +
                "   Week1 Week2 Week3 Week4";

        Label chartDisplay = new Label(chart);
        chartDisplay.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");

        chartBox.getChildren().addAll(chartTitle, chartDisplay);

        root.getChildren().addAll(header, kpiBox, chartBox);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - Analytics Dashboard");

        Platform.runLater(() -> {
            takeScreenshot(stage, "05_analytics_dashboard");
        });
    }

    private void generateUserManagement(Stage stage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px;");

        Label header = new Label("User Management & Administration");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // User table simulation
        VBox userTable = new VBox(10);
        userTable.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        Label tableHeader = new Label("Active Users");
        tableHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        String[] users = {
                "👤 John Smith      | Manager     | Active   | Last Login: 2 min ago",
                "👤 Sarah Johnson   | Cashier     | Active   | Last Login: 15 min ago",
                "👤 Mike Wilson     | Admin       | Active   | Last Login: 1 hour ago",
                "👤 Emily Davis     | Staff       | Offline  | Last Login: 3 hours ago",
                "👤 David Brown     | Manager     | Active   | Last Login: 5 min ago"
        };

        for (String user : users) {
            Label userLabel = new Label(user);
            userLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-padding: 8px;");
            userTable.getChildren().add(userLabel);
        }

        // Permissions summary
        VBox permissionsBox = new VBox(10);
        permissionsBox.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-background-radius: 10px;");

        Label permHeader = new Label("Role Permissions");
        permHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        String[] permissions = {
                "🔑 Admin: Full system access, user management, system settings",
                "🎯 Manager: Reports, staff oversight, booking management",
                "💰 Cashier: Sales processing, customer service, inventory view",
                "👥 Staff: Basic operations, customer assistance, limited access"
        };

        for (String perm : permissions) {
            Label permLabel = new Label(perm);
            permLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-wrap-text: true;");
            permissionsBox.getChildren().add(permLabel);
        }

        permissionsBox.getChildren().add(0, permHeader);
        userTable.getChildren().add(0, tableHeader);

        root.getChildren().addAll(header, userTable, permissionsBox);

        Scene scene = new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("RAKCHA Desktop - User Management");

        Platform.runLater(() -> {
            takeScreenshot(stage, "06_user_management");
        });
    }

    private void takeScreenshot(Stage stage, String filename) {
        try {
            WritableImage screenshot = stage.getScene().snapshot(null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(screenshot, null);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File outputFile = new File(OUTPUT_DIR, filename + "_" + timestamp + ".png");

            ImageIO.write(bufferedImage, "PNG", outputFile);
            screenshotCount++;

            logger.info("Screenshot saved: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            logger.severe("Failed to save screenshot: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Run in separate thread to avoid blocking
        Thread fxThread = new Thread(() -> {
            Application.launch(AutoDemoScreenshotGenerator.class, args);
        });
        fxThread.setDaemon(true);
        fxThread.start();

        try {
            latch.await();
            logger.info("Screenshot generation completed. Generated " + screenshotCount + " screenshots.");
        } catch (InterruptedException e) {
            logger.severe("Screenshot generation interrupted: " + e.getMessage());
        }

        System.exit(0);
    }
}

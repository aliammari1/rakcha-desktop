package com.esprit.controllers.users;

import com.esprit.models.films.Film;
import com.esprit.models.films.Ticket;
import com.esprit.models.series.Series;
import com.esprit.models.users.User;
import com.esprit.models.users.Activity;
import com.esprit.models.users.Achievement;
import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.common.Review;
import com.esprit.services.series.SeriesService;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.OrderService;
import com.esprit.services.users.WatchProgressService;
import com.esprit.services.common.ReviewService;
import com.esprit.services.films.TicketService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for client analytics dashboard.
 */
public class ClientDashboardController {
    
    private static final Logger LOGGER = Logger.getLogger(ClientDashboardController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");
    
    // Overview stats
    @FXML private Label totalWatchTimeLabel;
    @FXML private Label filmsWatchedLabel;
    @FXML private Label seriesWatchedLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label cinemaVisitsLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private Label currentStreakLabel;
    
    // Charts
    @FXML private PieChart genreChart;
    @FXML private BarChart<String, Number> watchingTrendChart;
    @FXML private LineChart<String, Number> activityChart;
    @FXML private AreaChart<String, Number> spendingChart;
    
    // Recent activity
    @FXML private VBox recentActivityList;
    @FXML private VBox topGenresList;
    @FXML private VBox recommendationsList;
    @FXML private VBox achievementsList;
    
    // Filters
    @FXML private ComboBox<String> timeRangeCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    // Container
    @FXML private VBox dashboardContainer;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label welcomeLabel;
    @FXML private ImageView userAvatar;
    
    private final WatchProgressService watchProgressService;
    private final ReviewService reviewService;
    private final TicketService ticketService;
    private final FilmService filmService;
    private final SeriesService seriesService;
    private final OrderService orderService;
    private User currentUser;
    
    public ClientDashboardController() {
        this.watchProgressService = new WatchProgressService();
        this.reviewService = new ReviewService();
        this.ticketService = new TicketService();
        this.filmService = new FilmService();
        this.seriesService = new SeriesService();
        this.orderService = new OrderService();
    }
    
    @FXML
    public void initialize() {
        LOGGER.info("Initializing ClientDashboardController");
        
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }
        
        setupTimeRangeFilter();
        displayUserInfo();
        loadDashboardData();
    }
    
    private void setupTimeRangeFilter() {
        timeRangeCombo.getItems().addAll("Last 7 Days", "Last 30 Days", "Last 3 Months", "Last Year", "All Time", "Custom");
        timeRangeCombo.setValue("Last 30 Days");
        timeRangeCombo.setOnAction(e -> {
            String range = timeRangeCombo.getValue();
            boolean showCustom = "Custom".equals(range);
            if (startDatePicker != null) startDatePicker.setVisible(showCustom);
            if (endDatePicker != null) endDatePicker.setVisible(showCustom);
            
            if (!showCustom) {
                loadDashboardData();
            }
        });
        
        if (startDatePicker != null) {
            startDatePicker.setValue(LocalDate.now().minusMonths(1));
            startDatePicker.setVisible(false);
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(LocalDate.now());
            endDatePicker.setVisible(false);
        }
    }
    
    private void displayUserInfo() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + currentUser.getFirstName() + "!");
        }
        
        if (userAvatar != null && currentUser.getProfilePicture() != null) {
            try {
                userAvatar.setImage(new Image(currentUser.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading avatar", e);
            }
        }
        
        if (memberSinceLabel != null && currentUser.getCreatedAt() != null) {
            memberSinceLabel.setText(currentUser.getCreatedAt().toLocalDateTime().format(DATE_FORMAT));
        }
    }
    
    private LocalDate[] getDateRange() {
        String range = timeRangeCombo.getValue();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (range) {
            case "Last 7 Days":
                startDate = endDate.minusDays(7);
                break;
            case "Last 30 Days":
                startDate = endDate.minusDays(30);
                break;
            case "Last 3 Months":
                startDate = endDate.minusMonths(3);
                break;
            case "Last Year":
                startDate = endDate.minusYears(1);
                break;
            case "Custom":
                startDate = startDatePicker != null ? startDatePicker.getValue() : endDate.minusDays(30);
                endDate = endDatePicker != null ? endDatePicker.getValue() : LocalDate.now();
                break;
            default: // All Time
                startDate = LocalDate.of(2020, 1, 1);
        }
        
        return new LocalDate[]{startDate, endDate};
    }
    
    private void loadDashboardData() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                LocalDate[] dateRange = getDateRange();
                
                // Load all stats
                Map<String, Object> stats = loadAllStats(dateRange[0], dateRange[1]);
                
                Platform.runLater(() -> {
                    displayOverviewStats(stats);
                    displayCharts(stats);
                    displayRecentActivity(stats);
                    displayTopGenres(stats);
                    displayRecommendations(stats);
                    displayAchievements(stats);
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load dashboard data.");
                });
            }
        }).start();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadAllStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Watch statistics
            int totalWatchMinutes = watchProgressService.getTotalWatchTime(currentUser.getId());
            int filmsWatched = watchProgressService.getFilmsWatchedCount(currentUser.getId());
            int seriesWatched = watchProgressService.getSeriesWatchedCount(currentUser.getId());
            
            stats.put("totalWatchMinutes", totalWatchMinutes);
            stats.put("filmsWatched", filmsWatched);
            stats.put("seriesWatched", seriesWatched);
            
            // Reviews
            List<Review> reviews = reviewService.getReviewsByUser(currentUser.getId());
            stats.put("totalReviews", reviews.size());
            
            // Cinema visits
            List<Ticket> tickets = ticketService.getTicketsByUser(currentUser.getId());
            long cinemaVisits = tickets.stream()
                .filter(t -> {
                    if (t.getPurchaseDate() == null) return false;
                    LocalDate ticketDate = t.getPurchaseDate().toLocalDate();
                    return !ticketDate.isBefore(startDate) && !ticketDate.isAfter(endDate);
                })
                .count();
            stats.put("cinemaVisits", (int) cinemaVisits);
            
            // Loyalty points
            int loyaltyPoints = currentUser.getLoyaltyPoints();
            stats.put("loyaltyPoints", loyaltyPoints);
            
            // Current streak
            int streak = watchProgressService.getCurrentStreak(currentUser.getId());
            stats.put("currentStreak", streak);
            
            // Genre distribution
            Map<String, Integer> genreStats = watchProgressService.getGenreDistribution(currentUser.getId());
            stats.put("genreDistribution", genreStats);
            
            // Monthly watch time trend
            Map<String, Integer> monthlyStats = watchProgressService.getMonthlyWatchTime(currentUser.getId());
            stats.put("monthlyWatchTime", monthlyStats);
            
            // Daily activity
            Map<String, Integer> dailyActivity = watchProgressService.getDailyActivity(currentUser.getId());
            stats.put("dailyActivity", dailyActivity);
            
            // Recent activity
            List<Activity> recentActivity = watchProgressService.getRecentActivity(currentUser.getId(), 10);
            stats.put("recentActivity", recentActivity);
            
            // Recommendations
            List<Film> recommendations = filmService.getRecommendationsForUser(currentUser.getId(), 5);
            stats.put("recommendations", recommendations);
            
            // Achievements
            List<Achievement> achievements = watchProgressService.getUserAchievements(currentUser.getId());
            stats.put("achievements", achievements);
            
            // Spending (if shop enabled)
            try {
                double monthlySpending = orderService.getMonthlySpending(currentUser.getId(), startDate, endDate);
                stats.put("monthlySpending", monthlySpending);
            } catch (Exception e) {
                stats.put("monthlySpending", 0.0);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading stats", e);
        }
        
        return stats;
    }
    
    private void displayOverviewStats(Map<String, Object> stats) {
        int watchMinutes = (int) stats.getOrDefault("totalWatchMinutes", 0);
        int hours = watchMinutes / 60;
        int mins = watchMinutes % 60;
        
        if (totalWatchTimeLabel != null) {
            totalWatchTimeLabel.setText(hours + "h " + mins + "m");
        }
        if (filmsWatchedLabel != null) {
            filmsWatchedLabel.setText(String.valueOf(stats.getOrDefault("filmsWatched", 0)));
        }
        if (seriesWatchedLabel != null) {
            seriesWatchedLabel.setText(String.valueOf(stats.getOrDefault("seriesWatched", 0)));
        }
        if (totalReviewsLabel != null) {
            totalReviewsLabel.setText(String.valueOf(stats.getOrDefault("totalReviews", 0)));
        }
        if (cinemaVisitsLabel != null) {
            cinemaVisitsLabel.setText(String.valueOf(stats.getOrDefault("cinemaVisits", 0)));
        }
        if (loyaltyPointsLabel != null) {
            loyaltyPointsLabel.setText(String.valueOf(stats.getOrDefault("loyaltyPoints", 0)));
        }
        if (currentStreakLabel != null) {
            int streak = (int) stats.getOrDefault("currentStreak", 0);
            currentStreakLabel.setText(streak + " day" + (streak != 1 ? "s" : ""));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void displayCharts(Map<String, Object> stats) {
        // Genre pie chart
        if (genreChart != null) {
            genreChart.getData().clear();
            Map<String, Integer> genreDistribution = (Map<String, Integer>) stats.getOrDefault("genreDistribution", new HashMap<>());
            
            for (Map.Entry<String, Integer> entry : genreDistribution.entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                genreChart.getData().add(slice);
            }
            
            genreChart.setTitle("Genres Watched");
        }
        
        // Monthly watch time bar chart
        if (watchingTrendChart != null) {
            watchingTrendChart.getData().clear();
            Map<String, Integer> monthlyWatchTime = (Map<String, Integer>) stats.getOrDefault("monthlyWatchTime", new HashMap<>());
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Watch Time (hours)");
            
            for (Map.Entry<String, Integer> entry : monthlyWatchTime.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue() / 60));
            }
            
            watchingTrendChart.getData().add(series);
        }
        
        // Daily activity line chart
        if (activityChart != null) {
            activityChart.getData().clear();
            Map<String, Integer> dailyActivity = (Map<String, Integer>) stats.getOrDefault("dailyActivity", new HashMap<>());
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Activity Score");
            
            List<String> sortedDates = new ArrayList<>(dailyActivity.keySet());
            Collections.sort(sortedDates);
            
            for (String date : sortedDates) {
                series.getData().add(new XYChart.Data<>(date, dailyActivity.get(date)));
            }
            
            activityChart.getData().add(series);
        }
        
        // Spending area chart
        if (spendingChart != null) {
            spendingChart.getData().clear();
            Map<String, Double> monthlySpending = (Map<String, Double>) stats.getOrDefault("monthlySpending", new HashMap<>());
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Spending ($)");
            
            for (Map.Entry<String, Double> entry : monthlySpending.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            spendingChart.getData().add(series);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void displayRecentActivity(Map<String, Object> stats) {
        if (recentActivityList == null) return;
        
        recentActivityList.getChildren().clear();
        
        List<Activity> activities = (List<Activity>) stats.getOrDefault("recentActivity", new ArrayList<>());
        
        if (activities.isEmpty()) {
            Label noActivity = new Label("No recent activity");
            noActivity.getStyleClass().add("empty-message");
            recentActivityList.getChildren().add(noActivity);
            return;
        }
        
        for (Activity activity : activities) {
            HBox activityRow = new HBox(12);
            activityRow.getStyleClass().add("activity-row");
            activityRow.setAlignment(Pos.CENTER_LEFT);
            
            Label icon = new Label(getActivityIcon(activity.getType()));
            icon.getStyleClass().add("activity-icon");
            
            VBox detailsBox = new VBox(2);
            HBox.setHgrow(detailsBox, Priority.ALWAYS);
            
            Label descLabel = new Label(activity.getDescription());
            descLabel.getStyleClass().add("activity-description");
            
            Label timeLabel = new Label(formatRelativeTime(activity.getTimestamp()));
            timeLabel.getStyleClass().add("activity-time");
            
            detailsBox.getChildren().addAll(descLabel, timeLabel);
            
            activityRow.getChildren().addAll(icon, detailsBox);
            recentActivityList.getChildren().add(activityRow);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void displayTopGenres(Map<String, Object> stats) {
        if (topGenresList == null) return;
        
        topGenresList.getChildren().clear();
        
        Map<String, Integer> genreDistribution = (Map<String, Integer>) stats.getOrDefault("genreDistribution", new HashMap<>());
        
        List<Map.Entry<String, Integer>> sorted = genreDistribution.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .collect(Collectors.toList());
        
        int maxValue = sorted.isEmpty() ? 1 : sorted.get(0).getValue();
        
        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            
            HBox genreRow = new HBox(12);
            genreRow.getStyleClass().add("genre-row");
            genreRow.setAlignment(Pos.CENTER_LEFT);
            
            Label rankLabel = new Label("#" + (i + 1));
            rankLabel.getStyleClass().add("genre-rank");
            rankLabel.setMinWidth(30);
            
            Label nameLabel = new Label(entry.getKey());
            nameLabel.getStyleClass().add("genre-name");
            nameLabel.setMinWidth(100);
            
            ProgressBar bar = new ProgressBar((double) entry.getValue() / maxValue);
            bar.getStyleClass().add("genre-bar");
            bar.setPrefWidth(100);
            HBox.setHgrow(bar, Priority.ALWAYS);
            
            Label countLabel = new Label(entry.getValue() + " titles");
            countLabel.getStyleClass().add("genre-count");
            
            genreRow.getChildren().addAll(rankLabel, nameLabel, bar, countLabel);
            topGenresList.getChildren().add(genreRow);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void displayRecommendations(Map<String, Object> stats) {
        if (recommendationsList == null) return;
        
        recommendationsList.getChildren().clear();
        
        List<Film> recommendations = (List<Film>) stats.getOrDefault("recommendations", new ArrayList<>());
        
        if (recommendations.isEmpty()) {
            Label noRecs = new Label("Watch more to get personalized recommendations!");
            noRecs.getStyleClass().add("empty-message");
            recommendationsList.getChildren().add(noRecs);
            return;
        }
        
        for (Film film : recommendations) {
            HBox recRow = new HBox(12);
            recRow.getStyleClass().add("recommendation-row");
            recRow.setAlignment(Pos.CENTER_LEFT);
            
            ImageView poster = new ImageView();
            poster.setFitWidth(50);
            poster.setFitHeight(75);
            poster.setPreserveRatio(true);
            
            if (film.getImage() != null) {
                try {
                    poster.setImage(new Image(film.getImage(), true));
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Error loading poster", e);
                }
            }
            
            VBox infoBox = new VBox(4);
            HBox.setHgrow(infoBox, Priority.ALWAYS);
            
            Label titleLabel = new Label(film.getNom());
            titleLabel.getStyleClass().add("rec-title");
            
            Label ratingLabel = new Label(String.format("%.1f ★", film.getNote()));
            ratingLabel.getStyleClass().add("rec-rating");
            
            infoBox.getChildren().addAll(titleLabel, ratingLabel);
            
            Button watchBtn = new Button("Watch");
            watchBtn.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
            watchBtn.setOnAction(e -> watchFilm(film));
            
            recRow.getChildren().addAll(poster, infoBox, watchBtn);
            recommendationsList.getChildren().add(recRow);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void displayAchievements(Map<String, Object> stats) {
        if (achievementsList == null) return;
        
        achievementsList.getChildren().clear();
        
        List<Achievement> achievements = (List<Achievement>) stats.getOrDefault("achievements", new ArrayList<>());
        
        // Sort by most recent or by locked status
        achievements.sort((a, b) -> {
            if (a.isUnlocked() && !b.isUnlocked()) return -1;
            if (!a.isUnlocked() && b.isUnlocked()) return 1;
            return b.getUnlockedAt() != null && a.getUnlockedAt() != null ? 
                b.getUnlockedAt().compareTo(a.getUnlockedAt()) : 0;
        });
        
        for (Achievement achievement : achievements.stream().limit(6).collect(Collectors.toList())) {
            VBox achievementBox = new VBox(4);
            achievementBox.getStyleClass().add("achievement-box");
            achievementBox.setAlignment(Pos.CENTER);
            
            if (!achievement.isUnlocked()) {
                achievementBox.getStyleClass().add("locked");
            }
            
            Label iconLabel = new Label(achievement.getIcon());
            iconLabel.getStyleClass().add("achievement-icon");
            
            Label nameLabel = new Label(achievement.getName());
            nameLabel.getStyleClass().add("achievement-name");
            
            Label descLabel = new Label(achievement.getDescription());
            descLabel.getStyleClass().add("achievement-desc");
            descLabel.setWrapText(true);
            
            if (achievement.isUnlocked() && achievement.getUnlockedAt() != null) {
                Label dateLabel = new Label(achievement.getUnlockedAt().format(DATE_FORMAT));
                dateLabel.getStyleClass().add("achievement-date");
                achievementBox.getChildren().addAll(iconLabel, nameLabel, descLabel, dateLabel);
            } else {
                ProgressBar progressBar = new ProgressBar(achievement.getProgress());
                progressBar.getStyleClass().add("achievement-progress");
                achievementBox.getChildren().addAll(iconLabel, nameLabel, descLabel, progressBar);
            }
            
            achievementsList.getChildren().add(achievementBox);
        }
    }
    
    private String getActivityIcon(String type) {
        switch (type.toLowerCase()) {
            case "watch": return "🎬";
            case "review": return "⭐";
            case "ticket": return "🎟️";
            case "purchase": return "🛒";
            case "achievement": return "🏆";
            default: return "📌";
        }
    }
    
    private String formatRelativeTime(LocalDateTime timestamp) {
        if (timestamp == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(timestamp, now);
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        
        long hours = ChronoUnit.HOURS.between(timestamp, now);
        if (hours < 24) return hours + "h ago";
        
        long days = ChronoUnit.DAYS.between(timestamp, now);
        if (days < 7) return days + "d ago";
        
        return timestamp.format(DATE_FORMAT);
    }
    
    private void watchFilm(Film film) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/FilmDetails.fxml"));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setFilm", Film.class).invoke(controller, film);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "No setFilm method", e);
                }
            }
            
            Stage stage = (Stage) dashboardContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening film", e);
        }
    }
    
    @FXML
    private void handleApplyDateRange() {
        loadDashboardData();
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboardData();
    }
    
    @FXML
    private void handleExportStats() {
        showInfo("Export functionality coming soon!");
    }
    
    @FXML
    private void handleViewAllActivity() {
        showInfo("Full activity log coming soon!");
    }
    
    @FXML
    private void handleViewAllAchievements() {
        showInfo("Achievements page coming soon!");
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/FilmUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dashboardContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package com.esprit.controllers.series;

import com.esprit.models.series.Series;
import com.esprit.models.series.Season;
import com.esprit.models.series.Episode;
import com.esprit.models.users.User;
import com.esprit.models.users.WatchProgress;
import com.esprit.services.series.SeriesService;
import com.esprit.services.users.WatchProgressService;
import com.esprit.services.series.SeasonService;
import com.esprit.services.series.EpisodeService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for tracking series watching progress.
 */
public class SeriesWatchProgressController {

    private static final Logger LOGGER = Logger.getLogger(SeriesWatchProgressController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML private VBox progressContainer;
    @FXML private Label totalSeriesLabel;
    @FXML private Label totalWatchTimeLabel;
    @FXML private Label currentlyWatchingLabel;
    @FXML private Label completedCountLabel;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private VBox statsBox;
    @FXML private TabPane progressTabs;
    @FXML private Label watchingCountLabel;
    @FXML private Label episodesWatchedLabel;
    @FXML private Label totalTimeLabel;
    @FXML private ToggleButton continueWatchingTab;
    @FXML private ToggleButton allSeriesTab;
    @FXML private ToggleButton completedTab;
    @FXML private ToggleButton onHoldTab;
    @FXML private VBox continueWatchingSection;
    @FXML private Label continueCountBadge;
    @FXML private HBox continueWatchingContainer;
    @FXML private VBox continueWatchingEmpty;
    @FXML private VBox recentlyWatchedSection;
    @FXML private VBox recentlyWatchedContainer;
    @FXML private VBox allSeriesSection;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private FlowPane allSeriesGrid;
    @FXML private VBox allSeriesEmpty;
    @FXML private HBox detailPanelOverlay;
    @FXML private VBox detailPanel;
    @FXML private ImageView detailBackdrop;
    @FXML private ImageView detailPoster;
    @FXML private Label detailTitle;
    @FXML private Label detailRating;

    private final SeriesService seriesService;
    private final WatchProgressService watchProgressService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;
    private ObservableList<WatchProgress> allProgress;
    private User currentUser;

    public SeriesWatchProgressController() {
        this.seriesService = new SeriesService();
        this.watchProgressService = new WatchProgressService();
        this.seasonService = new SeasonService();
        this.episodeService = new EpisodeService();
        this.allProgress = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing SeriesWatchProgressController");

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupFilters();
        loadWatchProgress();
    }

    private void setupFilters() {
        statusFilter.getItems().addAll("All", "Continue Watching", "Completed", "Not Started");
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> filterProgress());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProgress());
        }
    }

    private void loadWatchProgress() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<WatchProgress> progressList = watchProgressService.getUserSeriesProgress(currentUser.getId());

                Platform.runLater(() -> {
                    allProgress.setAll(progressList);
                    displayProgress();
                    updateStats();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading watch progress", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load watch progress.");
                });
            }
        }).start();
    }

    private void displayProgress() {
        // Progress is displayed through the UI sections
        // No need to manually manage the lists
    }

    private Label createEmptyMessage(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("empty-message");
        return label;
    }

    private VBox createProgressCard(WatchProgress progress) {
        VBox card = new VBox(12);
        card.getStyleClass().add("progress-card");

        HBox content = new HBox(16);
        content.setAlignment(Pos.CENTER_LEFT);

        // Series poster
        ImageView poster = new ImageView();
        poster.setFitWidth(100);
        poster.setFitHeight(150);
        poster.setPreserveRatio(true);
        poster.getStyleClass().add("series-poster");

        Series series = progress.getSeries();
        if (series != null && series.getImage() != null) {
            try {
                poster.setImage(new Image(series.getImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading poster", e);
            }
        }

        // Series info
        VBox infoBox = new VBox(8);
        infoBox.getStyleClass().add("series-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label titleLabel = new Label(series != null ? series.getNom() : "Unknown Series");
        titleLabel.getStyleClass().add("series-title");

        // Progress details
        int watchedEpisodes = progress.getWatchedEpisodes();
        int totalEpisodes = progress.getTotalEpisodes();

        Label episodeProgress = new Label(watchedEpisodes + " / " + totalEpisodes + " episodes");
        episodeProgress.getStyleClass().add("episode-progress-text");

        // Progress bar
        HBox progressBarBox = new HBox(8);
        progressBarBox.setAlignment(Pos.CENTER_LEFT);

        ProgressBar progressBar = new ProgressBar(progress.getProgress());
        progressBar.getStyleClass().add("series-progress-bar");
        progressBar.setPrefWidth(200);

        Label percentLabel = new Label(String.format("%.0f%%", progress.getProgress() * 100));
        percentLabel.getStyleClass().add("progress-percent");

        progressBarBox.getChildren().addAll(progressBar, percentLabel);

        // Current episode info
        Label currentEpisode = new Label();
        if (progress.getLastWatchedEpisode() != null) {
            currentEpisode.setText("Last: S" + progress.getLastWatchedSeason() +
                " E" + progress.getLastWatchedEpisodeNum() + " - " + progress.getLastWatchedEpisode().getTitle());
        } else if (progress.getProgress() < 1.0) {
            currentEpisode.setText("Start watching");
        } else {
            currentEpisode.setText("Completed!");
        }
        currentEpisode.getStyleClass().add("current-episode");

        // Last watched date
        Label lastWatched = new Label();
        if (progress.getLastWatchedAt() != null) {
            lastWatched.setText("Last watched: " + progress.getLastWatchedAt().format(DATE_FORMAT));
        }
        lastWatched.getStyleClass().add("last-watched");

        infoBox.getChildren().addAll(titleLabel, episodeProgress, progressBarBox, currentEpisode, lastWatched);

        // Action buttons
        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER);

        Button continueBtn = new Button(progress.getProgress() > 0 && progress.getProgress() < 1 ?
            "Continue" : (progress.getProgress() >= 1 ? "Rewatch" : "Start"));
        continueBtn.getStyleClass().addAll("btn", "btn-primary");
        continueBtn.setOnAction(e -> continueWatching(progress));

        Button viewBtn = new Button("View Series");
        viewBtn.getStyleClass().addAll("btn", "btn-secondary");
        viewBtn.setOnAction(e -> viewSeries(progress));

        MenuButton moreBtn = new MenuButton("•••");
        moreBtn.getStyleClass().add("more-btn");

        MenuItem markWatched = new MenuItem("Mark as Watched");
        markWatched.setOnAction(e -> markAsWatched(progress));

        MenuItem resetProgress = new MenuItem("Reset Progress");
        resetProgress.setOnAction(e -> resetProgress(progress));

        MenuItem removeItem = new MenuItem("Remove from List");
        removeItem.setOnAction(e -> removeFromList(progress));

        moreBtn.getItems().addAll(markWatched, resetProgress, new SeparatorMenuItem(), removeItem);

        actionBox.getChildren().addAll(continueBtn, viewBtn, moreBtn);

        content.getChildren().addAll(poster, infoBox, actionBox);

        // Rating if completed
        if (progress.getProgress() >= 1.0) {
            HBox ratingBox = new HBox(8);
            ratingBox.setAlignment(Pos.CENTER_LEFT);
            ratingBox.getStyleClass().add("rating-box");

            Label ratingLabel = new Label("Your Rating:");

            HBox stars = new HBox(4);
            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                Label star = new Label(i <= progress.getUserRating() ? "★" : "☆");
                star.getStyleClass().add("rating-star");
                star.setOnMouseClicked(e -> rateSeries(progress, rating));
                stars.getChildren().add(star);
            }

            ratingBox.getChildren().addAll(ratingLabel, stars);
            card.getChildren().addAll(content, ratingBox);
        } else {
            card.getChildren().add(content);
        }

        return card;
    }

    private void updateStats() {
        int total = allProgress.size();
        int watching = (int) allProgress.stream().filter(p -> p.getProgress() > 0 && p.getProgress() < 1).count();
        int completed = (int) allProgress.stream().filter(p -> p.getProgress() >= 1).count();

        int totalWatchTime = allProgress.stream()
            .mapToInt(WatchProgress::getWatchedMinutes)
            .sum();

        if (totalSeriesLabel != null) {
            totalSeriesLabel.setText(String.valueOf(total));
        }
        if (currentlyWatchingLabel != null) {
            currentlyWatchingLabel.setText(String.valueOf(watching));
        }
        if (completedCountLabel != null) {
            completedCountLabel.setText(String.valueOf(completed));
        }
        if (totalWatchTimeLabel != null) {
            int hours = totalWatchTime / 60;
            int minutes = totalWatchTime % 60;
            totalWatchTimeLabel.setText(hours + "h " + minutes + "m");
        }
    }

    private void filterProgress() {
        // Filter is handled by the display methods
        // This is a simplified version that does nothing as actual filtering
        // happens in displayProgress() and other display methods
    }

    private void continueWatching(WatchProgress progress) {
        Series series = progress.getSeries();
        if (series == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/SeasonView.fxml"));
            Parent root = loader.load();

            SeasonViewController controller = loader.getController();
            if (controller != null) {
                controller.setSeries(series);
            }

            Stage stage = (Stage) progressContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening season view", e);
        }
    }

    private void viewSeries(WatchProgress progress) {
        continueWatching(progress); // Same behavior
    }

    private void markAsWatched(WatchProgress progress) {
        new Thread(() -> {
            try {
                watchProgressService.markSeriesAsWatched(currentUser.getId(), progress.getSeriesId());
                Platform.runLater(() -> {
                    showSuccess("Marked as watched!");
                    loadWatchProgress();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error marking as watched", e);
                Platform.runLater(() -> showError("Failed to update."));
            }
        }).start();
    }

    private void resetProgress(WatchProgress progress) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Progress");
        confirm.setHeaderText("Reset progress for " + (progress.getSeries() != null ? progress.getSeries().getNom() : "this series") + "?");
        confirm.setContentText("This will mark all episodes as unwatched.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        watchProgressService.resetSeriesProgress(currentUser.getId(), progress.getSeriesId());
                        Platform.runLater(() -> {
                            showSuccess("Progress reset!");
                            loadWatchProgress();
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error resetting progress", e);
                        Platform.runLater(() -> showError("Failed to reset."));
                    }
                }).start();
            }
        });
    }

    private void removeFromList(WatchProgress progress) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove from List");
        confirm.setHeaderText("Remove from your list?");
        confirm.setContentText("This will remove the series and all progress data.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        watchProgressService.removeFromList(currentUser.getId(), progress.getSeriesId());
                        Platform.runLater(() -> {
                            showSuccess("Removed from list!");
                            loadWatchProgress();
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error removing from list", e);
                        Platform.runLater(() -> showError("Failed to remove."));
                    }
                }).start();
            }
        });
    }

    private void rateSeries(WatchProgress progress, int rating) {
        new Thread(() -> {
            try {
                watchProgressService.rateSeries(currentUser.getId(), progress.getSeriesId(), rating);
                Platform.runLater(() -> {
                    showSuccess("Rating saved!");
                    loadWatchProgress();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving rating", e);
                Platform.runLater(() -> showError("Failed to save rating."));
            }
        }).start();
    }

    @FXML
    private void handleExportStats() {
        showInfo("Export functionality coming soon!");
    }

    @FXML
    private void showContinueWatching() {
        statusFilter.setValue("Continue Watching");
        filterProgress();
    }

    @FXML
    private void showAllSeries() {
        statusFilter.setValue("All");
        filterProgress();
    }

    @FXML
    private void showCompleted() {
        statusFilter.setValue("Completed");
        filterProgress();
    }

    @FXML
    private void showOnHold() {
        statusFilter.setValue("Not Started");
        filterProgress();
    }

    @FXML
    private void browseSeries() {
        handleBrowseSeries();
    }

    @FXML
    private void viewHistory() {
        // Navigate to full watch history
        LOGGER.info("View history clicked");
    }

    @FXML
    private void closeDetailPanel() {
        // Close detail panel implementation
    }

    @FXML
    private void handleBrowseSeries() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/SerieClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) progressContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to series", e);
        }
    }

    @FXML
    private void resumeEpisode() {
        LOGGER.info("Resume episode clicked");
    }

    @FXML
    private void continueWatching() {
        LOGGER.info("Continue watching clicked");
    }

    @FXML
    private void viewSeriesDetails() {
        LOGGER.info("View series details");
    }

    @FXML
    private void markSeasonWatched() {
        LOGGER.info("Mark season as watched");
    }

    @FXML
    private void markAllWatched() {
        LOGGER.info("Mark all as watched");
    }

    @FXML
    private void cancelMarkComplete() {
        LOGGER.info("Cancel mark complete");
    }

    @FXML
    private void confirmMarkComplete() {
        LOGGER.info("Confirm mark complete");
    }

    @FXML
    private void removeFromList() {
        LOGGER.info("Remove from list");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) progressContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void resetProgress(ActionEvent actionEvent) {
    }
}

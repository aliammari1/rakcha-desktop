package com.esprit.controllers.series;

import com.esprit.models.common.Category;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Season;
import com.esprit.models.series.Series;
import com.esprit.models.users.User;
import com.esprit.services.series.EpisodeService;
import com.esprit.services.series.SeasonService;
import com.esprit.services.series.SeriesService;
import com.esprit.services.users.WatchProgressService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for viewing series seasons and episodes.
 */
public class SeasonViewController {

    private static final Logger LOGGER = Logger.getLogger(SeasonViewController.class.getName());
    private final SeriesService seriesService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;
    private final WatchProgressService watchProgressService;
    @FXML
    private VBox seasonContainer;
    @FXML
    private ImageView seriesPoster;
    @FXML
    private Label seriesTitleLabel;
    @FXML
    private Label seriesYearLabel;
    @FXML
    private Label seriesRatingLabel;
    @FXML
    private Label seriesDescriptionLabel;
    @FXML
    private Label totalSeasonsLabel;
    @FXML
    private Label totalEpisodesLabel;
    @FXML
    private HBox seasonsNav;
    @FXML
    private VBox episodesList;
    @FXML
    private Label currentSeasonLabel;
    @FXML
    private Label seasonDescriptionLabel;
    @FXML
    private Label episodeCountLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Button playAllBtn;
    @FXML
    private Button addToWatchlistBtn;
    @FXML
    private HBox genresBox;
    private ObservableList<Season> seasons;
    private ObservableList<Episode> episodes;
    private Series currentSeries;
    private Season currentSeason;
    private User currentUser;

    public SeasonViewController() {
        this.seriesService = new SeriesService();
        this.seasonService = new SeasonService();
        this.episodeService = new EpisodeService();
        this.watchProgressService = new WatchProgressService();
        this.seasons = FXCollections.observableArrayList();
        this.episodes = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing SeasonViewController");
        currentUser = SessionManager.getCurrentUser();
    }

    /**
     * Set the series to display.
     */
    public void setSeries(Series series) {
        this.currentSeries = series;
        loadSeriesDetails();
    }

    /**
     * Set series by ID.
     */
    public void setSeriesId(int seriesId) {
        new Thread(() -> {
            try {
                Series series = seriesService.getSeriesById((long) seriesId);
                Platform.runLater(() -> {
                    if (series != null) {
                        setSeries(series);
                    } else {
                        showError("Series not found.");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading series", e);
            }
        }).start();
    }

    private void loadSeriesDetails() {
        if (currentSeries == null) return;

        // Display series info
        seriesTitleLabel.setText(currentSeries.getNom());
        seriesYearLabel.setText(String.valueOf(currentSeries.getAnnee()));
        seriesRatingLabel.setText(String.format("%.1f ★", currentSeries.getNote()));

        if (seriesDescriptionLabel != null) {
            seriesDescriptionLabel.setText(currentSeries.getDescription());
        }

        // Load poster
        if (currentSeries.getImage() != null && !currentSeries.getImage().isEmpty()) {
            try {
                seriesPoster.setImage(new Image(currentSeries.getImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading poster", e);
            }
        }

        // Display genres
        if (genresBox != null && currentSeries.getGenres() != null) {
            genresBox.getChildren().clear();
            for (Category genre : currentSeries.getGenres()) {
                Label genreLabel = new Label(genre.getName());
                genreLabel.getStyleClass().add("genre-badge");
                genresBox.getChildren().add(genreLabel);
            }
        }

        loadSeasons();
    }

    private void loadSeasons() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Season> seasonList = seasonService.getSeasonsBySeries(currentSeries.getIdserie());

                Platform.runLater(() -> {
                    seasons.setAll(seasonList);
                    displaySeasons();
                    showLoading(false);

                    if (!seasons.isEmpty()) {
                        selectSeason(seasons.get(0));
                    }

                    if (totalSeasonsLabel != null) {
                        totalSeasonsLabel.setText(seasons.size() + " Season" + (seasons.size() != 1 ? "s" : ""));
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading seasons", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load seasons.");
                });
            }
        }).start();
    }

    private void displaySeasons() {
        seasonsNav.getChildren().clear();

        for (Season season : seasons) {
            Button seasonBtn = new Button("Season " + season.getSeasonNumber());
            seasonBtn.getStyleClass().add("season-btn");

            if (currentSeason != null && currentSeason.getId() == season.getId()) {
                seasonBtn.getStyleClass().add("active");
            }

            seasonBtn.setOnAction(e -> selectSeason(season));
            seasonsNav.getChildren().add(seasonBtn);
        }
    }

    private void selectSeason(Season season) {
        currentSeason = season;
        displaySeasons(); // Refresh to show selection

        if (currentSeasonLabel != null) {
            currentSeasonLabel.setText("Season " + season.getSeasonNumber());
        }
        if (seasonDescriptionLabel != null) {
            seasonDescriptionLabel.setText(season.getDescription());
        }

        loadEpisodes();
    }

    private void loadEpisodes() {
        if (currentSeason == null) return;

        showLoading(true);

        new Thread(() -> {
            try {
                List<Episode> episodeList = episodeService.getEpisodesBySeason(currentSeason.getId());

                Platform.runLater(() -> {
                    episodes.setAll(episodeList);
                    displayEpisodes();
                    showLoading(false);

                    if (episodeCountLabel != null) {
                        episodeCountLabel.setText(episodes.size() + " Episode" + (episodes.size() != 1 ? "s" : ""));
                    }

                    // Update total episodes count
                    if (totalEpisodesLabel != null) {
                        int total = seasons.stream()
                            .mapToInt(s -> {
                                try {
                                    return episodeService.getEpisodesBySeason(s.getId()).size();
                                } catch (Exception e) {
                                    return 0;
                                }
                            })
                            .sum();
                        totalEpisodesLabel.setText(total + " Total Episodes");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading episodes", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load episodes.");
                });
            }
        }).start();
    }

    private void displayEpisodes() {
        episodesList.getChildren().clear();

        for (int i = 0; i < episodes.size(); i++) {
            Episode episode = episodes.get(i);
            episodesList.getChildren().add(createEpisodeCard(episode, i + 1));
        }
    }

    private HBox createEpisodeCard(Episode episode, int episodeNumber) {
        HBox card = new HBox(16);
        card.getStyleClass().add("episode-card");
        card.setAlignment(Pos.CENTER_LEFT);

        // Episode number
        Label numLabel = new Label(String.format("%02d", episodeNumber));
        numLabel.getStyleClass().add("episode-number");
        numLabel.setMinWidth(40);

        // Thumbnail
        StackPane thumbnailContainer = new StackPane();
        thumbnailContainer.getStyleClass().add("episode-thumbnail");
        thumbnailContainer.setPrefSize(160, 90);

        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(160);
        thumbnail.setFitHeight(90);
        thumbnail.setPreserveRatio(true);

        if (episode.getThumbnail() != null && !episode.getThumbnail().isEmpty()) {
            try {
                thumbnail.setImage(new Image(episode.getThumbnail(), true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading thumbnail", e);
            }
        }

        // Play overlay
        Label playIcon = new Label("▶");
        playIcon.getStyleClass().add("play-overlay");
        playIcon.setVisible(false);

        thumbnailContainer.getChildren().addAll(thumbnail, playIcon);
        thumbnailContainer.setOnMouseEntered(e -> playIcon.setVisible(true));
        thumbnailContainer.setOnMouseExited(e -> playIcon.setVisible(false));
        thumbnailContainer.setOnMouseClicked(e -> playEpisode(episode));

        // Progress bar (if watched)
        if (currentUser != null) {
            double progress = getWatchProgress(episode);
            if (progress > 0) {
                ProgressBar progressBar = new ProgressBar(progress);
                progressBar.getStyleClass().add("episode-progress");
                progressBar.setMaxWidth(160);
                StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
                thumbnailContainer.getChildren().add(progressBar);
            }
        }

        // Episode info
        VBox infoBox = new VBox(4);
        infoBox.getStyleClass().add("episode-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label titleLabel = new Label(episode.getTitle());
        titleLabel.getStyleClass().add("episode-title");

        Label durationLabel = new Label(formatDuration(episode.getDuration()));
        durationLabel.getStyleClass().add("episode-duration");

        Label descLabel = new Label(episode.getDescription());
        descLabel.getStyleClass().add("episode-description");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);

        infoBox.getChildren().addAll(titleLabel, durationLabel, descLabel);

        // Action buttons
        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button playBtn = new Button("▶ Play");
        playBtn.getStyleClass().addAll("btn", "btn-primary");
        playBtn.setOnAction(e -> playEpisode(episode));

        Button downloadBtn = new Button("↓");
        downloadBtn.getStyleClass().addAll("btn", "btn-secondary", "icon-btn");
        downloadBtn.setOnAction(e -> downloadEpisode(episode));

        actionBox.getChildren().addAll(playBtn, downloadBtn);

        card.getChildren().addAll(numLabel, thumbnailContainer, infoBox, actionBox);

        return card;
    }

    private double getWatchProgress(Episode episode) {
        if (currentUser == null) return 0;

        try {
            return watchProgressService.getProgress(currentUser.getId(), "episode", episode.getId());
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatDuration(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            return String.format("%dh %dm", hours, mins);
        }
    }

    private void playEpisode(Episode episode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/VideoPlayer.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setEpisode", Episode.class).invoke(controller, episode);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "No setEpisode method", e);
                }
            }

            Stage stage = (Stage) seasonContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening video player", e);
            showError("Failed to open video player.");
        }
    }

    private void downloadEpisode(Episode episode) {
        showInfo("Download starting for: " + episode.getTitle());
    }

    @FXML
    private void handlePlayAll() {
        if (episodes.isEmpty()) {
            showInfo("No episodes to play.");
            return;
        }

        // Find first unwatched or continue from last
        Episode toPlay = episodes.get(0);

        if (currentUser != null) {
            for (Episode ep : episodes) {
                double progress = getWatchProgress(ep);
                if (progress < 0.9) { // Less than 90% watched
                    toPlay = ep;
                    break;
                }
            }
        }

        playEpisode(toPlay);
    }

    @FXML
    private void handleAddToWatchlist() {
        if (currentUser == null) {
            showError("Please log in to add to watchlist.");
            return;
        }

        new Thread(() -> {
            try {
                watchProgressService.addToWatchlist(currentUser.getId(), "series", currentSeries.getIdserie());
                Platform.runLater(() -> {
                    showSuccess("Added to watchlist!");
                    if (addToWatchlistBtn != null) {
                        addToWatchlistBtn.setText("✓ In Watchlist");
                        addToWatchlistBtn.setDisable(true);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error adding to watchlist", e);
                Platform.runLater(() -> showError("Failed to add to watchlist."));
            }
        }).start();
    }

    @FXML
    private void handleShare() {
        String shareText = "Check out " + currentSeries.getNom() + " on Rakcha!";

        // Copy to clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(shareText);
        clipboard.setContent(content);

        showSuccess("Link copied to clipboard!");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/SerieClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seasonContainer.getScene().getWindow();
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
}

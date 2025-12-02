package com.esprit.controllers.common;

import com.esprit.models.common.Review;
import com.esprit.models.films.Film;
import com.esprit.models.series.Series;
import com.esprit.models.users.User;
import com.esprit.services.common.ReviewService;
import com.esprit.services.films.FilmService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for displaying user's review history.
 */
public class UserReviewHistoryController {

    private static final Logger LOGGER = Logger.getLogger(UserReviewHistoryController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final ReviewService reviewService;
    private final FilmService filmService;
    private final SeriesService seriesService;
    @FXML
    private VBox historyContainer;
    @FXML
    private VBox reviewsList;
    @FXML
    private Label totalReviewsLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private Label helpfulCountLabel;
    @FXML
    private Label mostReviewedLabel;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private TextField searchField;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox noReviewsBox;
    @FXML
    private VBox statsBox;
    @FXML
    private HBox ratingDistribution;
    private ObservableList<Review> reviews;
    private User currentUser;

    public UserReviewHistoryController() {
        this.reviewService = new ReviewService();
        this.filmService = new FilmService();
        this.seriesService = new SeriesService();
        this.reviews = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing UserReviewHistoryController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupSort();
        setupFilter();
        setupSearch();
        loadReviews();
    }

    private void setupSort() {
        sortCombo.getItems().addAll("Newest First", "Oldest First", "Highest Rated", "Lowest Rated", "Most Helpful");
        sortCombo.setValue("Newest First");
        sortCombo.setOnAction(e -> sortAndDisplayReviews());
    }

    private void setupFilter() {
        filterCombo.getItems().addAll("All", "Films Only", "Series Only", "5 Stars", "4 Stars", "3 Stars", "2 Stars", "1 Star");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> filterAndDisplayReviews());
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndDisplayReviews());
        }
    }

    private void loadReviews() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Review> reviewList = reviewService.getReviewsByUser(currentUser.getId());

                // Enrich reviews with content info
                for (Review review : reviewList) {
                    enrichReviewWithContent(review);
                }

                Platform.runLater(() -> {
                    reviews.setAll(reviewList);
                    displayReviews();
                    updateStats();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading reviews", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load reviews.");
                });
            }
        }).start();
    }

    private void enrichReviewWithContent(Review review) {
        try {
            if ("film".equals(review.getContentType())) {
                Film film = filmService.getFilmById(review.getContentId());
                if (film != null) {
                    review.setContentTitle(film.getNom());
                    review.setContentImage(film.getImage());
                }
            } else {
                Series series = seriesService.getSeriesById(review.getContentId());
                if (series != null) {
                    review.setContentTitle(series.getNom());
                    review.setContentImage(series.getImage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Error enriching review", e);
        }
    }

    private void displayReviews() {
        reviewsList.getChildren().clear();

        if (reviews.isEmpty()) {
            noReviewsBox.setVisible(true);
            reviewsList.setVisible(false);
        } else {
            noReviewsBox.setVisible(false);
            reviewsList.setVisible(true);

            for (Review review : reviews) {
                reviewsList.getChildren().add(createReviewCard(review));
            }
        }
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(12);
        card.getStyleClass().add("review-history-card");

        HBox content = new HBox(16);
        content.setAlignment(Pos.TOP_LEFT);

        // Content poster
        ImageView poster = new ImageView();
        poster.setFitWidth(80);
        poster.setFitHeight(120);
        poster.setPreserveRatio(true);
        poster.getStyleClass().add("content-poster");

        if (review.getContentImage() != null) {
            try {
                poster.setImage(new Image(review.getContentImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading poster", e);
            }
        }

        // Review details
        VBox detailsBox = new VBox(8);
        detailsBox.getStyleClass().add("review-details");
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        // Title and type
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(review.getContentTitle() != null ? review.getContentTitle() : "Unknown");
        titleLabel.getStyleClass().add("content-title");

        Label typeLabel = new Label(review.getContentType().toUpperCase());
        typeLabel.getStyleClass().addAll("content-type-badge",
            "film".equals(review.getContentType()) ? "type-film" : "type-series");

        titleRow.getChildren().addAll(titleLabel, typeLabel);

        // Rating and date
        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.getStyleClass().add("rating-star-small");
            stars.getChildren().add(star);
        }

        Label dateLabel = new Label(review.getCreatedAt() != null ?
            review.getCreatedAt().format(DATE_FORMAT) : "");
        dateLabel.getStyleClass().add("review-date");

        metaRow.getChildren().addAll(stars, dateLabel);

        // Review text
        Label reviewText = new Label(review.getText());
        reviewText.getStyleClass().add("review-text");
        reviewText.setWrapText(true);
        reviewText.setMaxHeight(60);

        // Stats
        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        Label helpfulLabel = new Label("👍 " + review.getHelpfulCount() + " found helpful");
        helpfulLabel.getStyleClass().add("helpful-count");

        statsRow.getChildren().add(helpfulLabel);

        detailsBox.getChildren().addAll(titleRow, metaRow, reviewText, statsRow);

        // Action buttons
        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.TOP_RIGHT);

        Button viewBtn = new Button("View");
        viewBtn.getStyleClass().addAll("btn", "btn-secondary");
        viewBtn.setOnAction(e -> viewContent(review));

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().addAll("btn", "btn-secondary");
        editBtn.setOnAction(e -> editReview(review));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().addAll("btn", "btn-danger");
        deleteBtn.setOnAction(e -> deleteReview(review));

        actionBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

        content.getChildren().addAll(poster, detailsBox, actionBox);
        card.getChildren().add(content);

        // Click to expand
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                viewContent(review);
            }
        });

        return card;
    }

    private void updateStats() {
        int total = reviews.size();
        double avgRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);
        int totalHelpful = reviews.stream()
            .mapToInt(Review::getHelpfulCount)
            .sum();

        if (totalReviewsLabel != null) {
            totalReviewsLabel.setText(String.valueOf(total));
        }
        if (averageRatingLabel != null) {
            averageRatingLabel.setText(String.format("%.1f ★", avgRating));
        }
        if (helpfulCountLabel != null) {
            helpfulCountLabel.setText(String.valueOf(totalHelpful));
        }

        // Most reviewed content type
        if (mostReviewedLabel != null) {
            long filmCount = reviews.stream().filter(r -> "film".equals(r.getContentType())).count();
            long seriesCount = reviews.stream().filter(r -> "series".equals(r.getContentType())).count();
            mostReviewedLabel.setText(filmCount > seriesCount ? "Films" :
                (seriesCount > filmCount ? "Series" : "Equal"));
        }

        // Rating distribution
        if (ratingDistribution != null) {
            ratingDistribution.getChildren().clear();

            Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

            for (int i = 1; i <= 5; i++) {
                long count = distribution.getOrDefault(i, 0L);
                double percent = total > 0 ? (count * 100.0 / total) : 0;

                VBox bar = new VBox(4);
                bar.setAlignment(Pos.BOTTOM_CENTER);
                bar.setPrefWidth(40);

                Region barFill = new Region();
                barFill.getStyleClass().add("distribution-bar");
                barFill.setPrefHeight(Math.max(5, percent * 0.8));
                barFill.setMaxWidth(30);

                Label label = new Label(i + "★");
                label.getStyleClass().add("distribution-label");

                bar.getChildren().addAll(barFill, label);
                ratingDistribution.getChildren().add(bar);
            }
        }
    }

    private void sortAndDisplayReviews() {
        String sortBy = sortCombo.getValue();

        reviews.sort((a, b) -> {
            switch (sortBy) {
                case "Newest First":
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                case "Oldest First":
                    return a.getCreatedAt().compareTo(b.getCreatedAt());
                case "Highest Rated":
                    return Integer.compare(b.getRating(), a.getRating());
                case "Lowest Rated":
                    return Integer.compare(a.getRating(), b.getRating());
                case "Most Helpful":
                    return Integer.compare(b.getHelpfulCount(), a.getHelpfulCount());
                default:
                    return 0;
            }
        });

        displayReviews();
    }

    private void filterAndDisplayReviews() {
        String filter = filterCombo.getValue();
        String search = searchField != null ? searchField.getText().toLowerCase() : "";

        reviewsList.getChildren().clear();

        for (Review review : reviews) {
            boolean match = true;

            // Type filter
            if ("Films Only".equals(filter)) {
                match = "film".equals(review.getContentType());
            } else if ("Series Only".equals(filter)) {
                match = "series".equals(review.getContentType());
            } else if (filter.contains("Stars")) {
                int filterRating = Integer.parseInt(filter.split(" ")[0]);
                match = review.getRating() == filterRating;
            }

            // Search filter
            if (match && !search.isEmpty()) {
                String title = review.getContentTitle() != null ? review.getContentTitle().toLowerCase() : "";
                String text = review.getText() != null ? review.getText().toLowerCase() : "";
                match = title.contains(search) || text.contains(search);
            }

            if (match) {
                reviewsList.getChildren().add(createReviewCard(review));
            }
        }

        if (reviewsList.getChildren().isEmpty()) {
            Label noResults = new Label("No reviews match your filters");
            noResults.getStyleClass().add("no-results");
            reviewsList.getChildren().add(noResults);
        }
    }

    private void viewContent(Review review) {
        try {
            String fxml = "film".equals(review.getContentType()) ?
                "/com/esprit/views/ReviewsAndRatings.fxml" : "/com/esprit/views/ReviewsAndRatings.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            ReviewsAndRatingsController controller = loader.getController();
            if (controller != null) {
                controller.setContent(review.getContentType(), review.getContentId());
            }

            Stage stage = (Stage) historyContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error viewing content", e);
        }
    }

    private void editReview(Review review) {
        // Open edit dialog
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Edit Review");
        dialog.setHeaderText("Edit your review for " + review.getContentTitle());

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPrefWidth(400);

        // Rating
        HBox ratingBox = new HBox(8);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        Label ratingLabel = new Label("Rating:");
        HBox stars = new HBox(4);
        final int[] selectedRating = {review.getRating()};

        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            Label star = new Label(i <= selectedRating[0] ? "★" : "☆");
            star.getStyleClass().add("rating-star-input");
            star.setStyle("-fx-font-size: 20px; -fx-cursor: hand;");
            star.setOnMouseClicked(e -> {
                selectedRating[0] = rating;
                for (int j = 0; j < stars.getChildren().size(); j++) {
                    ((Label) stars.getChildren().get(j)).setText(j < rating ? "★" : "☆");
                }
            });
            stars.getChildren().add(star);
        }
        ratingBox.getChildren().addAll(ratingLabel, stars);

        // Review text
        TextArea textArea = new TextArea(review.getText());
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);

        content.getChildren().addAll(ratingBox, new Label("Review:"), textArea);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveBtn) {
                review.setRating(selectedRating[0]);
                review.setText(textArea.getText().trim());
                return review;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedReview -> {
            new Thread(() -> {
                try {
                    reviewService.updateReview(updatedReview);
                    Platform.runLater(() -> {
                        showSuccess("Review updated!");
                        loadReviews();
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error updating review", e);
                    Platform.runLater(() -> showError("Failed to update review."));
                }
            }).start();
        });
    }

    private void deleteReview(Review review) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Review");
        confirm.setHeaderText("Delete review for " + review.getContentTitle() + "?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        reviewService.deleteReview(review.getId());
                        Platform.runLater(() -> {
                            showSuccess("Review deleted.");
                            loadReviews();
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error deleting review", e);
                        Platform.runLater(() -> showError("Failed to delete review."));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void handleExportReviews() {
        showInfo("Export functionality coming soon!");
    }

    @FXML
    private void handleBrowseContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/FilmUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) historyContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to content", e);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) historyContainer.getScene().getWindow();
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

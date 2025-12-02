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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for displaying and managing reviews and ratings.
 */
public class ReviewsAndRatingsController {

    private static final Logger LOGGER = Logger.getLogger(ReviewsAndRatingsController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final ReviewService reviewService;
    private final FilmService filmService;
    private final SeriesService seriesService;
    @FXML
    private VBox reviewsContainer;
    @FXML
    private ImageView contentPoster;
    @FXML
    private Label contentTitleLabel;
    @FXML
    private Label contentTypeLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private Label totalReviewsLabel;
    @FXML
    private HBox ratingStars;
    @FXML
    private VBox ratingBreakdown;
    @FXML
    private VBox reviewsList;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private TextArea reviewTextArea;
    @FXML
    private HBox userRatingStars;
    @FXML
    private Button submitReviewBtn;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox noReviewsBox;
    @FXML
    private Label userReviewedLabel;
    @FXML
    private VBox writeReviewBox;
    private ObservableList<Review> reviews;
    private User currentUser;

    private String contentType; // "film" or "series"
    private Long contentId;
    private int userRating = 0;
    private Review userExistingReview;

    public ReviewsAndRatingsController() {
        this.reviewService = new ReviewService();
        this.filmService = new FilmService();
        this.seriesService = new SeriesService();
        this.reviews = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing ReviewsAndRatingsController");

        currentUser = SessionManager.getCurrentUser();

        setupSort();
        setupFilter();
        setupUserRatingStars();
    }

    /**
     * Set the content to display reviews for.
     */
    public void setContent(String type, Long id) {
        this.contentType = type;
        this.contentId = id;
        loadContentDetails();
        loadReviews();
    }

    /**
     * Set content from a Film object.
     */
    public void setFilm(Film film) {
        if (film != null) {
            this.contentType = "film";
            this.contentId = film.getId();
            displayContentInfo(film.getTitle(), film.getImageUrl(), "Film");
            loadReviews();
        }
    }

    /**
     * Set content from a Serie object.
     */
    public void setSeries(Series series) {
        if (series != null) {
            this.contentType = "series";
            this.contentId = series.getId();
            displayContentInfo(series.getName(), series.getImageUrl(), "Series");
            loadReviews();
        }
    }

    private void setupSort() {
        sortCombo.getItems().addAll("Newest First", "Oldest First", "Highest Rated", "Lowest Rated", "Most Helpful");
        sortCombo.setValue("Newest First");
        sortCombo.setOnAction(e -> sortReviews());
    }

    private void setupFilter() {
        filterCombo.getItems().addAll("All Ratings", "5 Stars", "4 Stars", "3 Stars", "2 Stars", "1 Star");
        filterCombo.setValue("All Ratings");
        filterCombo.setOnAction(e -> filterReviews());
    }

    private void setupUserRatingStars() {
        if (userRatingStars == null)
            return;

        userRatingStars.getChildren().clear();

        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            Label star = new Label("☆");
            star.getStyleClass().add("rating-star-input");
            star.setOnMouseClicked(e -> setUserRating(rating));
            star.setOnMouseEntered(e -> previewRating(rating));
            star.setOnMouseExited(e -> displayUserRating());
            userRatingStars.getChildren().add(star);
        }
    }

    private void setUserRating(int rating) {
        this.userRating = rating;
        displayUserRating();
    }

    private void previewRating(int rating) {
        for (int i = 0; i < userRatingStars.getChildren().size(); i++) {
            Label star = (Label) userRatingStars.getChildren().get(i);
            star.setText(i < rating ? "★" : "☆");
        }
    }

    private void displayUserRating() {
        for (int i = 0; i < userRatingStars.getChildren().size(); i++) {
            Label star = (Label) userRatingStars.getChildren().get(i);
            star.setText(i < userRating ? "★" : "☆");
        }
    }

    private void loadContentDetails() {
        new Thread(() -> {
            try {
                String title = "";
                String image = "";
                String type = "";

                if ("film".equals(contentType)) {
                    Film film = filmService.getFilmById(contentId);
                    if (film != null) {
                        title = film.getName();
                        image = film.getImageUrl();
                        type = "Film";
                    }
                } else {
                    Series series = seriesService.getSeriesById(contentId);
                    if (series != null) {
                        title = series.getName();
                        image = series.getImageUrl();
                        type = "Series";
                    }
                }

                final String finalTitle = title;
                final String finalImage = image;
                final String finalType = type;

                Platform.runLater(() -> displayContentInfo(finalTitle, finalImage, finalType));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading content details", e);
            }
        }).start();
    }

    private void displayContentInfo(String title, String image, String type) {
        contentTitleLabel.setText(title);
        if (contentTypeLabel != null) {
            contentTypeLabel.setText(type);
        }

        if (image != null && !image.isEmpty()) {
            try {
                contentPoster.setImage(new Image(image, true));
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error loading poster", e);
            }
        }
    }

    private void loadReviews() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Review> reviewList = reviewService.getReviewsByContent(contentType, contentId);

                // Check if user has already reviewed
                Review existingReview = null;
                if (currentUser != null) {
                    existingReview = reviewService.getUserReview(currentUser.getId(), contentType, contentId);
                }

                final Review userReview = existingReview;

                Platform.runLater(() -> {
                    reviews.setAll(reviewList);
                    userExistingReview = userReview;
                    displayReviews();
                    displayRatingStats();
                    updateWriteReviewSection();
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

        if (totalReviewsLabel != null) {
            totalReviewsLabel.setText(reviews.size() + " review" + (reviews.size() != 1 ? "s" : ""));
        }
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(12);
        card.getStyleClass().add("review-card");

        // Check if this is the current user's review
        boolean isUserReview = currentUser != null && review.getUserId() == currentUser.getId();
        if (isUserReview) {
            card.getStyleClass().add("user-review");
        }

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        // User avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        avatar.getStyleClass().add("user-avatar");

        if (review.getUserAvatar() != null) {
            try {
                avatar.setImage(new Image(review.getUserAvatar(), true));
            } catch (Exception e) {
                // Use default avatar
            }
        }

        VBox userInfo = new VBox(2);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        Label usernameLabel = new Label(review.getUsername() != null ? review.getUsername() : "Anonymous");
        usernameLabel.getStyleClass().add("reviewer-name");
        if (isUserReview) {
            usernameLabel.setText(usernameLabel.getText() + " (You)");
        }

        Label dateLabel = new Label(review.getCreatedAt() != null ? review.getCreatedAt().format(DATE_FORMAT) : "");
        dateLabel.getStyleClass().add("review-date");

        userInfo.getChildren().addAll(usernameLabel, dateLabel);

        // Rating stars
        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.getStyleClass().add("rating-star");
        }
        stars.getChildren().addAll();
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.getStyleClass().add("rating-star-display");
            stars.getChildren().add(star);
        }

        header.getChildren().addAll(avatar, userInfo, stars);

        // Review title if present
        if (review.getTitle() != null && !review.getTitle().isEmpty()) {
            Label titleLabel = new Label(review.getTitle());
            titleLabel.getStyleClass().add("review-title");
            card.getChildren().add(titleLabel);
        }

        // Review text
        Label reviewText = new Label(review.getText());
        reviewText.getStyleClass().add("review-text");
        reviewText.setWrapText(true);

        // Footer
        HBox footer = new HBox(16);
        footer.setAlignment(Pos.CENTER_LEFT);

        Button helpfulBtn = new Button("👍 Helpful (" + review.getHelpfulCount() + ")");
        helpfulBtn.getStyleClass().add("helpful-btn");
        helpfulBtn.setOnAction(e -> markHelpful(review));

        Button reportBtn = new Button("🚩 Report");
        reportBtn.getStyleClass().add("report-btn");
        reportBtn.setOnAction(e -> reportReview(review));

        footer.getChildren().addAll(helpfulBtn);

        if (!isUserReview) {
            footer.getChildren().add(reportBtn);
        } else {
            Button editBtn = new Button("✏️ Edit");
            editBtn.getStyleClass().add("edit-btn");
            editBtn.setOnAction(e -> editReview(review));

            Button deleteBtn = new Button("🗑️ Delete");
            deleteBtn.getStyleClass().add("delete-btn");
            deleteBtn.setOnAction(e -> deleteReview(review));

            footer.getChildren().addAll(editBtn, deleteBtn);
        }

        card.getChildren().addAll(header, reviewText, footer);

        // Spoiler warning if marked
        if (review.hasSpoilers()) {
            Label spoilerWarning = new Label("⚠️ This review may contain spoilers");
            spoilerWarning.getStyleClass().add("spoiler-warning");
            card.getChildren().add(1, spoilerWarning);
        }

        return card;
    }

    private void displayRatingStats() {
        // Calculate average rating
        double avgRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);

        if (averageRatingLabel != null) {
            averageRatingLabel.setText(String.format("%.1f", avgRating));
        }

        // Display average stars
        if (ratingStars != null) {
            ratingStars.getChildren().clear();
            for (int i = 0; i < 5; i++) {
                Label star = new Label(i < Math.round(avgRating) ? "★" : "☆");
                star.getStyleClass().add("rating-star-large");
                ratingStars.getChildren().add(star);
            }
        }

        // Rating breakdown
        if (ratingBreakdown != null) {
            ratingBreakdown.getChildren().clear();

            int total = reviews.size();
            for (int rating = 5; rating >= 1; rating--) {
                final int r = rating;
                int count = (int) reviews.stream().filter(rev -> rev.getRating() == r).count();
                double percent = total > 0 ? (count * 100.0 / total) : 0;

                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                Label ratingLabel = new Label(rating + " ★");
                ratingLabel.setMinWidth(40);

                ProgressBar bar = new ProgressBar(percent / 100);
                bar.setPrefWidth(150);
                bar.getStyleClass().add("rating-bar");

                Label countLabel = new Label(count + "");
                countLabel.setMinWidth(30);

                row.getChildren().addAll(ratingLabel, bar, countLabel);
                ratingBreakdown.getChildren().add(row);
            }
        }
    }

    private void updateWriteReviewSection() {
        if (currentUser == null) {
            writeReviewBox.setVisible(false);
            if (userReviewedLabel != null) {
                userReviewedLabel.setText("Login to write a review");
                userReviewedLabel.setVisible(true);
            }
            return;
        }

        if (userExistingReview != null) {
            writeReviewBox.setVisible(false);
            if (userReviewedLabel != null) {
                userReviewedLabel.setText("You have already reviewed this " + contentType);
                userReviewedLabel.setVisible(true);
            }
        } else {
            writeReviewBox.setVisible(true);
            if (userReviewedLabel != null) {
                userReviewedLabel.setVisible(false);
            }
        }
    }

    @FXML
    private void handleSubmitReview() {
        if (currentUser == null) {
            showError("Please log in to submit a review.");
            return;
        }

        if (userRating == 0) {
            showError("Please select a rating.");
            return;
        }

        String reviewText = reviewTextArea.getText().trim();
        if (reviewText.isEmpty()) {
            showError("Please write a review.");
            return;
        }

        showLoading(true);
        submitReviewBtn.setDisable(true);

        new Thread(() -> {
            try {
                Review review = new Review();
                review.setUserId(currentUser.getId());
                review.setContentType(contentType);
                review.setContentId(contentId);
                review.setRating(userRating);
                review.setText(reviewText);

                reviewService.createReview(review);

                Platform.runLater(() -> {
                    showLoading(false);
                    submitReviewBtn.setDisable(false);
                    showSuccess("Review submitted successfully!");
                    reviewTextArea.clear();
                    userRating = 0;
                    displayUserRating();
                    loadReviews();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error submitting review", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    submitReviewBtn.setDisable(false);
                    showError("Failed to submit review.");
                });
            }
        }).start();
    }

    private void sortReviews() {
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

    private void filterReviews() {
        String filter = filterCombo.getValue();

        reviewsList.getChildren().clear();

        for (Review review : reviews) {
            boolean match = true;

            if (!"All Ratings".equals(filter)) {
                int filterRating = Integer.parseInt(filter.split(" ")[0]);
                match = review.getRating() == filterRating;
            }

            if (match) {
                reviewsList.getChildren().add(createReviewCard(review));
            }
        }
    }

    private void markHelpful(Review review) {
        if (currentUser == null) {
            showError("Please log in to mark reviews as helpful.");
            return;
        }

        new Thread(() -> {
            try {
                reviewService.markHelpful(review.getId(), currentUser.getId());
                Platform.runLater(() -> loadReviews());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error marking helpful", e);
            }
        }).start();
    }

    private void reportReview(Review review) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Review");
        dialog.setHeaderText("Why are you reporting this review?");
        dialog.setContentText("Reason:");

        dialog.showAndWait().ifPresent(reason -> {
            new Thread(() -> {
                try {
                    reviewService.reportReview(review.getId(), currentUser.getId(), reason);
                    Platform.runLater(() -> showSuccess("Review reported. We'll investigate."));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error reporting review", e);
                }
            }).start();
        });
    }

    private void editReview(Review review) {
        reviewTextArea.setText(review.getText());
        userRating = review.getRating();
        displayUserRating();
        writeReviewBox.setVisible(true);

        submitReviewBtn.setText("Update Review");
        submitReviewBtn.setOnAction(e -> updateReview(review));
    }

    private void updateReview(Review review) {
        if (userRating == 0 || reviewTextArea.getText().trim().isEmpty()) {
            showError("Please provide a rating and review text.");
            return;
        }

        review.setRating(userRating);
        review.setText(reviewTextArea.getText().trim());

        new Thread(() -> {
            try {
                reviewService.updateReview(review);
                Platform.runLater(() -> {
                    showSuccess("Review updated!");
                    submitReviewBtn.setText("Submit Review");
                    submitReviewBtn.setOnAction(e -> handleSubmitReview());
                    loadReviews();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating review", e);
                Platform.runLater(() -> showError("Failed to update review."));
            }
        }).start();
    }

    private void deleteReview(Review review) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Review");
        confirm.setHeaderText("Delete your review?");
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
    private void handleBack() {
        try {
            String fxml = "film".equals(contentType) ? "/com/esprit/views/FilmUser.fxml"
                : "/com/esprit/views/SerieClient.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) reviewsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null)
            loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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

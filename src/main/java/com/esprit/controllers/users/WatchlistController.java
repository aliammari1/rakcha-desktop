package com.esprit.controllers.users;

import com.esprit.models.films.Film;
import com.esprit.models.users.User;
import com.esprit.services.films.FilmService;
import com.esprit.services.users.WatchlistService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing user's movie watchlist.
 */
public class WatchlistController {

    private static final Logger LOGGER = Logger.getLogger(WatchlistController.class.getName());
    private final WatchlistService watchlistService;
    private final FilmService filmService;
    @FXML
    private FlowPane watchlistGrid;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private TextField searchField;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label watchedCountLabel;
    @FXML
    private ToggleButton gridViewToggle;
    @FXML
    private ToggleButton listViewToggle;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox emptyStateBox;
    private ObservableList<Film> watchlistItems;
    private User currentUser;
    private boolean isGridView = true;

    public WatchlistController() {
        this.watchlistService = new WatchlistService();
        this.filmService = new FilmService();
        this.watchlistItems = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing WatchlistController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupControls();
        loadWatchlist();
    }

    private void setupControls() {
        sortCombo.getItems().addAll("Date Added", "Title A-Z", "Title Z-A", "Rating", "Release Year");
        sortCombo.setValue("Date Added");
        sortCombo.setOnAction(e -> sortWatchlist());

        filterCombo.getItems().addAll("All", "Not Started", "In Progress", "Completed");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> filterWatchlist());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterWatchlist());
        }

        ToggleGroup viewToggle = new ToggleGroup();
        if (gridViewToggle != null) {
            gridViewToggle.setToggleGroup(viewToggle);
            gridViewToggle.setSelected(true);
        }
        if (listViewToggle != null) {
            listViewToggle.setToggleGroup(viewToggle);
        }
    }

    private void loadWatchlist() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Film> films = watchlistService.getWatchlistByUserId(currentUser.getId());

                Platform.runLater(() -> {
                    watchlistItems.setAll(films);
                    updateStatistics();
                    displayWatchlist();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading watchlist", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load watchlist.");
                });
            }
        }).start();
    }

    private void displayWatchlist() {
        watchlistGrid.getChildren().clear();

        if (watchlistItems.isEmpty()) {
            emptyStateBox.setVisible(true);
            watchlistGrid.setVisible(false);
        } else {
            emptyStateBox.setVisible(false);
            watchlistGrid.setVisible(true);

            for (Film film : watchlistItems) {
                if (isGridView) {
                    watchlistGrid.getChildren().add(createFilmCard(film));
                } else {
                    watchlistGrid.getChildren().add(createFilmListItem(film));
                }
            }
        }
    }

    private VBox createFilmCard(Film film) {
        VBox card = new VBox(8);
        card.getStyleClass().add("watchlist-card");
        card.setPrefWidth(180);

        // Poster
        ImageView poster = new ImageView();
        poster.setFitWidth(160);
        poster.setFitHeight(240);
        poster.setPreserveRatio(true);
        poster.getStyleClass().add("film-poster");

        if (film.getImage() != null && !film.getImage().isEmpty()) {
            try {
                poster.setImage(new Image(film.getImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load poster", e);
            }
        }

        // Title
        Label title = new Label(film.getNom());
        title.getStyleClass().add("film-title");
        title.setWrapText(true);

        // Year and rating
        HBox infoBox = new HBox(8);
        Label yearLabel = new Label(String.valueOf(film.getAnnee()));
        yearLabel.getStyleClass().add("film-year");

        Label ratingLabel = new Label("★ " + String.format("%.1f", film.getRating()));
        ratingLabel.getStyleClass().add("film-rating");

        infoBox.getChildren().addAll(yearLabel, ratingLabel);

        // Actions
        HBox actions = new HBox(8);
        actions.getStyleClass().add("card-actions");

        Button viewBtn = new Button("View");
        viewBtn.getStyleClass().add("view-btn");
        viewBtn.setOnAction(e -> viewFilmDetails(film));

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("remove-btn");
        removeBtn.setOnAction(e -> removeFromWatchlist(film));

        actions.getChildren().addAll(viewBtn, removeBtn);

        card.getChildren().addAll(poster, title, infoBox, actions);

        return card;
    }

    private HBox createFilmListItem(Film film) {
        HBox item = new HBox(16);
        item.getStyleClass().add("watchlist-list-item");

        ImageView poster = new ImageView();
        poster.setFitWidth(60);
        poster.setFitHeight(90);
        poster.setPreserveRatio(true);

        if (film.getImage() != null && !film.getImage().isEmpty()) {
            try {
                poster.setImage(new Image(film.getImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load poster", e);
            }
        }

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label title = new Label(film.getNom());
        title.getStyleClass().add("film-title");

        Label details = new Label(film.getAnnee() + " • " + film.getDuree() + " min");
        details.getStyleClass().add("film-details");

        Label rating = new Label("★ " + String.format("%.1f", film.getRating()));
        rating.getStyleClass().add("film-rating");

        info.getChildren().addAll(title, details, rating);

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-btn-small");
        removeBtn.setOnAction(e -> removeFromWatchlist(film));

        item.getChildren().addAll(poster, info, removeBtn);

        return item;
    }

    private void sortWatchlist() {
        String sortBy = sortCombo.getValue();

        switch (sortBy) {
            case "Title A-Z":
                FXCollections.sort(watchlistItems, (f1, f2) -> f1.getNom().compareToIgnoreCase(f2.getNom()));
                break;
            case "Title Z-A":
                FXCollections.sort(watchlistItems, (f1, f2) -> f2.getNom().compareToIgnoreCase(f1.getNom()));
                break;
            case "Rating":
                FXCollections.sort(watchlistItems, (f1, f2) -> Double.compare(f2.getRating(), f1.getRating()));
                break;
            case "Release Year":
                FXCollections.sort(watchlistItems, (f1, f2) -> Integer.compare(f2.getAnnee(), f1.getAnnee()));
                break;
            default:
                // Date Added - keep original order
                break;
        }

        displayWatchlist();
    }

    private void filterWatchlist() {
        String filter = filterCombo.getValue();
        String search = searchField != null ? searchField.getText().toLowerCase() : "";

        // Reload and filter
        loadWatchlist();
    }

    private void updateStatistics() {
        if (totalCountLabel != null) {
            totalCountLabel.setText(String.valueOf(watchlistItems.size()));
        }
    }

    @FXML
    private void handleToggleGridView() {
        isGridView = true;
        displayWatchlist();
    }

    @FXML
    private void handleToggleListView() {
        isGridView = false;
        displayWatchlist();
    }

    private void viewFilmDetails(Film film) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/FilmDetails.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) watchlistGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to film details", e);
        }
    }

    private void removeFromWatchlist(Film film) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove from Watchlist");
        confirm.setHeaderText("Remove \"" + film.getNom() + "\" from your watchlist?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        watchlistService.removeFromWatchlist(currentUser.getId(), film.getId());

                        Platform.runLater(() -> {
                            watchlistItems.remove(film);
                            updateStatistics();
                            displayWatchlist();
                            showSuccess("Removed from watchlist.");
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error removing from watchlist", e);
                        Platform.runLater(() -> showError("Failed to remove from watchlist."));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void handleClearWatchlist() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear Watchlist");
        confirm.setHeaderText("Remove all items from your watchlist?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        watchlistService.clearWatchlist(currentUser.getId());

                        Platform.runLater(() -> {
                            watchlistItems.clear();
                            updateStatistics();
                            displayWatchlist();
                            showSuccess("Watchlist cleared.");
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error clearing watchlist", e);
                        Platform.runLater(() -> showError("Failed to clear watchlist."));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void handleBrowseMovies() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/FilmUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) watchlistGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to browse movies", e);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) watchlistGrid.getScene().getWindow();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearWatchlist(ActionEvent actionEvent) {
    }

    public void browseFilms(ActionEvent actionEvent) {
    }

    public void playItem(ActionEvent actionEvent) {

    }

    public void viewDetails(ActionEvent actionEvent) {
    }

    public void findSessions(ActionEvent actionEvent) {
    }

    public void removeFromWatchlist(ActionEvent actionEvent) {
    }

    public void closeOptions(ActionEvent actionEvent) {
    }
}

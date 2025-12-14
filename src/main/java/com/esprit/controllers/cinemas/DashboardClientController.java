package com.esprit.controllers.cinemas;

import com.esprit.controllers.films.SeatSelectionController;
import com.esprit.enums.CinemaStatus;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.common.Review;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.common.ReviewService;
import com.esprit.services.users.UserService;
import com.esprit.utils.PageRequest;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling client dashboard operations for cinema
 * management.
 *
 * <p>
 * This controller provides functionality for clients to interact with cinema
 * data including
 * viewing cinema listings, filtering cinemas, viewing movie sessions, adding
 * comments and ratings,
 * and displaying cinema locations on maps. It handles the client-side interface
 * for the cinema
 * management system.
 * </p>
 *
 * <p>
 * Key features include cinema listing and filtering, movie session planning,
 * comment and rating
 * system, map integration, and top-rated cinema recommendations.
 * </p>
 *
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 */
public class DashboardClientController {

    private static final Logger LOGGER = Logger.getLogger(DashboardClientController.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> namesCheckBoxes = new ArrayList<>();
    private final List<VBox> moviesessionsVBoxList = new ArrayList<>(); // Liste pour stocker les conteneurs de séances
    @FXML
    private FlowPane cinemaFlowPane;
    @FXML
    private AnchorPane listCinemaClient;
    @FXML
    private AnchorPane PlanningPane;
    @FXML
    private FlowPane planningFlowPane;
    @FXML
    private FlowPane parentContainer;
    @FXML
    private TextField searchbar1;
    @FXML
    private AnchorPane filterAnchor;
    @FXML
    private TextArea txtAreaComments;
    @FXML
    private ScrollPane ScrollPaneComments;
    @FXML
    private AnchorPane AnchorComments;
    @FXML
    private Button closeDetailFilm;
    private Cinema cinema;
    private TilePane tilePane;
    private Long cinemaId;
    private List<Cinema> l1 = new ArrayList<>();
    @FXML
    private ComboBox<String> tricomboBox;
    private LocalDate lastSelectedDate;
    @FXML
    private AnchorPane Anchortop3;

    /**
     * @return List<Cinema>
     */
    // closeDetailFilm.setOnAction(new EventHandler<ActionEvent>() {
    // @Override
    // public void handle(ActionEvent event) {
    // PlanningPane.setVisible(false);
    // listCinemaClient.setOpacity(1);
    // listCinemaClient.setDisable(false);
    // }

    // });

    /**
     * Finds cinemas whose name contains the given search term.
     *
     * @param liste     the list of Cinema objects to search
     * @param recherche the substring to match against each cinema's name; cinemas
     *                  with a null name are ignored and matching is case-sensitive
     * @return a List<Cinema> containing cinemas from {@code liste} whose name
     *         contains {@code recherche}
     */
    @FXML
    /**
     * Performs rechercher operation.
     *
     * @return the result of the operation
     */
    public static List<Cinema> rechercher(final List<Cinema> liste, final String recherche) {
        final List<Cinema> resultats = new ArrayList<>();
        for (final Cinema element : liste) {
            if (null != element.getName() && element.getName().contains(recherche)) {
                resultats.add(element);
            }

        }

        return resultats;
    }

    /**
     * Hide the planning pane and restore the cinema list's visibility and opacity.
     *
     * @param event the ActionEvent that triggered this change
     */
    @FXML
    void Planninggclose(final ActionEvent event) {
        this.PlanningPane.setVisible(false);
        this.listCinemaClient.setOpacity(1);
        this.listCinemaClient.setVisible(true);
    }

    /**
     * Display accepted cinemas in the main list and hide the planning pane.
     * <p>
     * Clears the cinema flow pane and reloads accepted cinemas into the view.
     */
    @FXML
    void showListCinema(final ActionEvent event) {
        this.cinemaFlowPane.getChildren().clear();
        final HashSet<Cinema> acceptedCinemas = this.loadAcceptedCinemas();
        this.listCinemaClient.setVisible(true);
        this.PlanningPane.setVisible(false);
    }

    /**
     * Load cinemas with status "Accepted" and display each as a UI card in the
     * controller's cinemaFlowPane.
     * <p>
     * Also shows an information alert if no accepted cinemas are found.
     *
     * @return a HashSet of Cinema objects whose status equals "Accepted"
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        final CinemaService cinemaService = new CinemaService();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<Cinema> cinemas = cinemaService.read(pageRequest).getContent();
        final List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> CinemaStatus.ACCEPTED.getStatus().equals(cinema.getStatus()))
                .collect(Collectors.toList());
        if (acceptedCinemasList.isEmpty()) {
            this.showAlert("Aucun cinéma accepté n'est disponible.");
        }

        final HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        for (final Cinema cinema : acceptedCinemasSet) {
            final HBox cardContainer = this.createCinemaCard(cinema);
            this.cinemaFlowPane.getChildren().add(cardContainer);
        }

        return acceptedCinemasSet;
    }

    /**
     * Show an information alert dialog with the specified message.
     *
     * @param message the text to display in the alert dialog
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // Méthode pour créer les cartes des top 3 cinémas les mieux notés

    /**
     * Creates a UI card representing the given cinema with its logo, name, address,
     * interactive controls, and a rating widget.
     *
     * @param cinema the cinema to represent in the card
     * @return an HBox containing the constructed cinema card UI elements
     */
    private HBox createCinemaCard(final Cinema cinema) {
        final HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 15px;");

        final AnchorPane card = new AnchorPane();
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(30, 15, 18, 0.95), rgba(20, 10, 12, 0.98));" +
                        "-fx-background-radius: 20px;" +
                        "-fx-border-color: rgba(139, 0, 0, 0.4);" +
                        "-fx-border-radius: 20px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 15, 0, 0, 5);" +
                        "-fx-cursor: hand;");
        card.setPrefWidth(480);
        card.setPrefHeight(180);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(45, 22, 28, 0.98), rgba(30, 15, 18, 0.98));" +
                        "-fx-background-radius: 20px;" +
                        "-fx-border-color: #ff4444;" +
                        "-fx-border-radius: 20px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 0.6), 25, 0, 0, 8);" +
                        "-fx-cursor: hand;" +
                        "-fx-scale-x: 1.02;" +
                        "-fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(30, 15, 18, 0.95), rgba(20, 10, 12, 0.98));" +
                        "-fx-background-radius: 20px;" +
                        "-fx-border-color: rgba(139, 0, 0, 0.4);" +
                        "-fx-border-radius: 20px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 15, 0, 0, 5);" +
                        "-fx-cursor: hand;"));

        // Logo ImageView with enhanced styling
        final ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(140);
        logoImageView.setFitHeight(120);
        logoImageView.setLayoutX(20);
        logoImageView.setLayoutY(30);
        logoImageView.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.5), 10, 0, 0, 3);");
        try {
            final String logoString = cinema.getLogoUrl();
            if (logoString != null && !logoString.isEmpty()) {
                final Image logoImage = new Image(logoString);
                logoImageView.setImage(logoImage);
            }
        } catch (final Exception e) {
            DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        card.getChildren().add(logoImageView);

        // Name Label with premium styling
        final Label nameLabel = new Label(cinema.getName() != null ? cinema.getName() : "Unknown");
        nameLabel.setLayoutX(180);
        nameLabel.setLayoutY(30);
        nameLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                        "-fx-font-family: 'Arial Black';" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.5), 5, 0, 1, 1);");
        card.getChildren().add(nameLabel);

        // Address Label
        final Label adresseLabel = new Label(cinema.getAddress() != null ? cinema.getAddress() : "Unknown");
        adresseLabel.setLayoutX(180);
        adresseLabel.setLayoutY(58);
        adresseLabel.setStyle(
                "-fx-text-fill: #aaaaaa;" +
                        "-fx-font-size: 12px;");
        adresseLabel.setMaxWidth(280);
        card.getChildren().add(adresseLabel);

        // Action Buttons Container
        final HBox buttonContainer = new HBox(12);
        buttonContainer.setLayoutX(180);
        buttonContainer.setLayoutY(90);

        // Show Planning Button
        final Button planningButton = new Button("Schedule");
        planningButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #8b0000, #550000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #ff4444;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.5), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16;");
        planningButton.setUserData(cinema);
        planningButton.setOnAction(event -> this.showPlanning(cinema));
        planningButton.setOnMouseEntered(e -> planningButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #aa0000, #660000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #ff6666;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 0.7), 12, 0, 0, 3);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16;"));
        planningButton.setOnMouseExited(e -> planningButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #8b0000, #550000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #ff4444;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.5), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16;"));
        buttonContainer.getChildren().add(planningButton);

        // Map Button - Improved with text label and better styling
        final Button mapButton = new Button("Map");
        final FontIcon mapIcon = new FontIcon("mdi2m-map-marker");
        mapIcon.setIconSize(14);
        mapIcon.setIconColor(javafx.scene.paint.Color.web("#ffffff"));
        mapButton.setGraphic(mapIcon);
        mapButton.setTooltip(new javafx.scene.control.Tooltip("View location on map"));
        mapButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #2a6496, #1a4f7a);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #4a9fd4;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(42, 100, 150, 0.5), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14;");
        mapButton.setOnAction(event -> this.geocodeAddress(cinema.getAddress()));
        mapButton.setOnMouseEntered(e -> mapButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #3a7db6, #2a6496);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #6abfe8;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(74, 159, 212, 0.7), 12, 0, 0, 3);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14;"));
        mapButton.setOnMouseExited(e -> mapButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #2a6496, #1a4f7a);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-color: #4a9fd4;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(42, 100, 150, 0.5), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 14;"));
        buttonContainer.getChildren().add(mapButton);

        card.getChildren().add(buttonContainer);

        // Comment Icon with new styling
        final FontIcon CommentIcon = new FontIcon("mdi2c-comment-text-outline");
        CommentIcon.setIconSize(28);
        CommentIcon.setLayoutX(430);
        CommentIcon.setLayoutY(45);
        CommentIcon.setIconColor(javafx.scene.paint.Color.web("#ff6666"));
        CommentIcon.setStyle("-fx-cursor: hand;");
        card.getChildren().add(CommentIcon);
        CommentIcon.setOnMouseClicked(mouseEvent -> {
            this.cinemaId = cinema.getId();
            this.listCinemaClient.setOpacity(0.5);
            this.AnchorComments.setVisible(true);
            this.displayAllComments(this.cinemaId);
            DashboardClientController.LOGGER.info(String.valueOf(this.cinemaId));
        });

        // Rating widget with styling
        final ReviewService ratingService = new ReviewService();
        final Rating rating = new Rating();
        rating.setLayoutX(180);
        rating.setLayoutY(130);
        rating.setMax(5);

        final double specificRating = ratingService.getAverageRating(cinema.getId());
        if (0 <= specificRating) {
            rating.setRating(specificRating);
        }

        rating.ratingProperty().addListener((observable, oldValue, newValue) -> {
            final Client client = (Client) SessionManager.getCurrentUser();
            final Review ratingCinema = new Review(cinema, client, newValue.intValue());
            ratingService.create(ratingCinema);
        });
        card.getChildren().add(rating);

        // Rating Display Label
        final Label ratingLabel = new Label(String.format("★ %.1f", specificRating > 0 ? specificRating : 0));
        ratingLabel.setLayoutX(430);
        ratingLabel.setLayoutY(130);
        ratingLabel.setStyle(
                "-fx-text-fill: #ffc800;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;");
        card.getChildren().add(ratingLabel);

        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    /**
     * Populate an AnchorPane with vertically stacked cards for top-rated cinemas.
     * <p>
     * Each card displays the cinema's logo, name, and address and is positioned
     * with a fixed height and spacing.
     *
     * @param Anchortop3 the AnchorPane to receive the generated cinema cards
     */
    public void createTopRatedCinemaCards(final AnchorPane Anchortop3) {
        final ReviewService ratingCinemaService = new ReviewService();
        // Get actual top rated cinemas from reviews
        final var topRatedReviews = ratingCinemaService.getTopRatedCinemas(3);
        final double cardHeight = 110;
        final double cardSpacing = 15;
        double currentY = 10;
        int rank = 1;

        for (final var review : topRatedReviews) {
            final Cinema cinema = review.getCinema();
            if (cinema == null)
                continue;

            // Create the card with premium styling
            final HBox card = new HBox(15);
            card.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, rgba(50, 25, 30, 0.9), rgba(35, 18, 22, 0.95));"
                            +
                            "-fx-background-radius: 15px;" +
                            "-fx-border-color: rgba(255, 200, 0, 0.3);" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 15px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.3), 10, 0, 0, 3);" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 12px;");
            card.setPrefWidth(340);
            card.setPrefHeight(cardHeight);
            card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Hover effects
            card.setOnMouseEntered(e -> card.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, rgba(70, 35, 42, 0.95), rgba(50, 25, 30, 0.98));"
                            +
                            "-fx-background-radius: 15px;" +
                            "-fx-border-color: #ffc800;" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 15px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 200, 0, 0.4), 15, 0, 0, 5);" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 12px;"));
            card.setOnMouseExited(e -> card.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, rgba(50, 25, 30, 0.9), rgba(35, 18, 22, 0.95));"
                            +
                            "-fx-background-radius: 15px;" +
                            "-fx-border-color: rgba(255, 200, 0, 0.3);" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 15px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.3), 10, 0, 0, 3);" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 12px;"));

            // Rank Badge
            final StackPane rankBadge = new StackPane();
            final javafx.scene.shape.Circle rankCircle = new javafx.scene.shape.Circle(18);
            rankCircle.setStyle(
                    "-fx-fill: linear-gradient(to bottom, #ffc800, #cc9900);" +
                            "-fx-stroke: #ffdd44;" +
                            "-fx-stroke-width: 2;");
            final Label rankLabel = new Label("#" + rank);
            rankLabel.setStyle(
                    "-fx-text-fill: #1a0808;" +
                            "-fx-font-family: 'Arial Black';" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;");
            rankBadge.getChildren().addAll(rankCircle, rankLabel);
            card.getChildren().add(rankBadge);

            // Logo ImageView
            final ImageView logoImageView = new ImageView();
            logoImageView.setFitWidth(70);
            logoImageView.setFitHeight(70);
            logoImageView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.4), 8, 0, 0, 2);");
            try {
                final String logoString = cinema.getLogoUrl();
                if (logoString != null && !logoString.isEmpty()) {
                    final Image logoImage = new Image(logoString);
                    logoImageView.setImage(logoImage);
                }
            } catch (final Exception e) {
                DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            card.getChildren().add(logoImageView);

            // Info Container
            final VBox infoContainer = new VBox(5);
            infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            final Label nomLabel = new Label(cinema.getName() != null ? cinema.getName() : "Unknown");
            nomLabel.setStyle(
                    "-fx-text-fill: #ffffff;" +
                            "-fx-font-family: 'Arial Black';" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;");

            final Label adresseLabel = new Label(cinema.getAddress() != null ? cinema.getAddress() : "Unknown");
            adresseLabel.setStyle(
                    "-fx-text-fill: #aaaaaa;" +
                            "-fx-font-size: 11px;");
            adresseLabel.setMaxWidth(150);
            adresseLabel.setWrapText(true);

            // Rating display
            double avgRating = ratingCinemaService.getAverageRating(cinema.getId());
            final Label ratingLabel = new Label("★ " + String.format("%.1f", avgRating > 0 ? avgRating : 0));
            ratingLabel.setStyle(
                    "-fx-text-fill: #ffc800;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;");

            infoContainer.getChildren().addAll(nomLabel, adresseLabel, ratingLabel);
            card.getChildren().add(infoContainer);

            // Position the card
            AnchorPane.setTopAnchor(card, currentY);
            AnchorPane.setLeftAnchor(card, 10.0);
            AnchorPane.setRightAnchor(card, 10.0);
            Anchortop3.getChildren().add(card);

            currentY += cardHeight + cardSpacing;
            rank++;
        }
    }

    /**
     * Obtain geographic coordinates for the given address and display a map dialog
     * centered at that location.
     *
     * @param address the address to geocode and show on the map
     */
    private void geocodeAddress(final String address) {
        new Thread(() -> {
            try {
                String encodedAddress = java.net.URLEncoder.encode(address, StandardCharsets.UTF_8);
                String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json";

                URI uri = URI.create(apiUrl);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                final BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                final StringBuilder content = new StringBuilder();
                while (null != (inputLine = in.readLine())) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();
                final JSONArray jsonArray = new JSONArray(content.toString());
                if (0 < jsonArray.length()) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(0);
                    final double lat = jsonObject.getDouble("lat");
                    final double lon = jsonObject.getDouble("lon");
                    // Open the dialog with the map on the JavaFX Application Thread
                    Platform.runLater(() -> this.openMapDialog(lat, lon));
                }

            } catch (final Exception e) {
                DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }).start();
    }

    /**
     * Display a modal dialog with an embedded WebView map centered at the given
     * coordinates.
     *
     * @param lat latitude in decimal degrees to position the map marker
     * @param lon longitude in decimal degrees to position the map marker
     */
    private void openMapDialog(final double lat, final double lon) {
        // Create a new dialog
        final Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Map");
        // Create a WebView and load the map
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(this.getClass().getResource("/map.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);
        // Wait for the map to be loaded before placing the marker
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                // Call JavaScript function to update the marker
                webEngine.executeScript("updateMarker(" + lat + ", " + lon + ");");
            }

        });
        // Set the WebView as the dialog content
        dialog.getDialogPane().setContent(webView);
        dialog.getDialogPane().setPrefSize(600, 400); // Set dialog size (adjust as needed)
        // Add a close button
        final ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        // Show the dialog
        dialog.showAndWait();
    }

    /**
     * Show a 7-day planning view for the given cinema with clickable day labels.
     * <p>
     * Each day label displays the date for that weekday and opens the cinema's
     * movie sessions for that date when clicked.
     *
     * @param cinema the cinema whose weekly schedule will be displayed
     */
    public void showPlanning(final Cinema cinema) {
        this.listCinemaClient.setVisible(false);
        this.PlanningPane.setVisible(true);
        final VBox planningContent = new VBox();
        planningContent.setSpacing(15);
        planningContent.setStyle("-fx-background-color: transparent;");

        this.tilePane = new TilePane();
        this.tilePane.setPrefColumns(7);
        this.tilePane.setPrefRows(1);
        this.tilePane.setHgap(10);
        this.tilePane.setVgap(10);
        this.tilePane.setStyle("-fx-background-color: transparent;");

        final LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        final LocalDate today = LocalDate.now();

        for (int i = 0; 7 > i; i++) {
            final LocalDate date = startDateOfWeek.plusDays(i);
            final Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEE\ndd/MM")));
            dayLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            dayLabel.setAlignment(javafx.geometry.Pos.CENTER);
            dayLabel.setPrefWidth(85);
            dayLabel.setPrefHeight(60);

            // Highlight today's date with special styling
            if (date.equals(today)) {
                dayLabel.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #ffc800, #cc9900);" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 10;" +
                                "-fx-text-fill: #1a0808;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(255, 200, 0, 0.5), 8, 0, 0, 2);");
            } else {
                dayLabel.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, rgba(139, 0, 0, 0.6), rgba(80, 0, 0, 0.5));" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 10;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-border-color: rgba(255, 68, 68, 0.4);" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 12;" +
                                "-fx-cursor: hand;");
            }

            dayLabel.setOnMouseClicked(event -> this.displayMovieSessionsForDate(date, cinema));

            // Hover effects
            final LocalDate labelDate = date;
            dayLabel.setOnMouseEntered(e -> {
                if (!labelDate.equals(today)) {
                    dayLabel.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, rgba(180, 0, 0, 0.8), rgba(120, 0, 0, 0.7));"
                                    +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 10;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-border-color: #ff4444;" +
                                    "-fx-border-width: 1;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 0.5), 10, 0, 0, 3);");
                }
            });
            dayLabel.setOnMouseExited(e -> {
                if (labelDate.equals(today)) {
                    dayLabel.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, #ffc800, #cc9900);" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 10;" +
                                    "-fx-text-fill: #1a0808;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255, 200, 0, 0.5), 8, 0, 0, 2);");
                } else {
                    dayLabel.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, rgba(139, 0, 0, 0.6), rgba(80, 0, 0, 0.5));"
                                    +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 10;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-border-color: rgba(255, 68, 68, 0.4);" +
                                    "-fx-border-width: 1;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-cursor: hand;");
                }
            });

            this.tilePane.getChildren().add(dayLabel);
        }

        planningContent.getChildren().add(this.tilePane);
        this.planningFlowPane.getChildren().clear();
        this.planningFlowPane.getChildren().add(planningContent);
    }

    /**
     * Retrieve movie sessions for the 7-day period starting at the given date for
     * the specified cinema.
     *
     * @param startDate the first date of the 7-day period (inclusive)
     * @param cinema    the cinema whose sessions are requested
     * @return a map whose keys are each date in the 7-day period beginning at
     *         {@code startDate} and whose values are
     *         the lists of {@code MovieSession} scheduled at {@code cinema} on
     *         those dates
     */
    private Map<LocalDate, List<MovieSession>> loadCurrentWeekPlanning(final LocalDate startDate, final Cinema cinema) {
        // Obtenir la date de fin de la semaine courante (dimanche)
        final LocalDate endDate = startDate.plusDays(6);
        // Utiliser MovieSessionService pour obtenir les séances programmées pour cette
        // semaine pour cette cinéma
        final MovieSessionService moviesessionService = new MovieSessionService();
        return moviesessionService.getSessionsByDateRangeAndCinema(startDate, endDate, cinema);
    }

    /**
     * Display the weekly planning and show the movie sessions for the specified
     * date and cinema in the planning view.
     * <p>
     * Updates the controller's planningFlowPane with a week header and either the
     * list of sessions for the given date
     * or a message indicating no sessions are scheduled.
     *
     * @param date   the date whose sessions should be shown (a specific day within
     *               the displayed week)
     * @param cinema the cinema whose movie sessions will be displayed
     */
    private void displayMovieSessionsForDate(final LocalDate date, final Cinema cinema) {
        // Charger les séances pour la date spécifiée
        final Map<LocalDate, List<MovieSession>> weekMovieSessionsMap = this.loadCurrentWeekPlanning(date, cinema);
        final List<MovieSession> moviesessionsForDate = weekMovieSessionsMap.getOrDefault(date,
                Collections.emptyList());
        // Créer un VBox pour contenir les éléments du calendrier
        final VBox planningContent = new VBox();
        planningContent.setSpacing(10);
        // Ajouter le calendrier au VBox
        final TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(7);
        tilePane.setPrefRows(1);
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        final LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; 7 > i; i++) {
            final LocalDate currentDate = startDateOfWeek.plusDays(i);
            final Label dayLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("EEE dd/MM")));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;");
            dayLabel.setOnMouseClicked(event -> this.displayMovieSessionsForDate(currentDate, cinema));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            tilePane.getChildren().add(dayLabel);
        }

        VBox.setMargin(tilePane, new Insets(0, 0, 0, 50));
        planningContent.getChildren().add(tilePane);
        final Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(200);
        planningContent.getChildren().add(separator);
        // Vérifier si des séances sont disponibles pour la date spécifiée
        if (!moviesessionsForDate.isEmpty()) {
            // Créer un VBox pour contenir les cartes de séance correspondant à la date
            // sélectionnée
            final VBox moviesessionsForDateVBox = new VBox();
            moviesessionsForDateVBox.setSpacing(10);
            // Ajouter les cartes de séance au VBox
            for (final MovieSession moviesession : moviesessionsForDate) {
                final StackPane moviesessionCard = this.createMovieSessionCard(moviesession);
                moviesessionsForDateVBox.getChildren().add(moviesessionCard);
            }

            // Ajouter le VBox des séances au conteneur principal
            planningContent.getChildren().add(moviesessionsForDateVBox);
        } else {
            // Afficher un message lorsque aucune séance n'est disponible pour la date
            // spécifiée
            final Label noMovieSessionLabel = new Label("Aucune séance prévue pour cette journée.");
            noMovieSessionLabel.setStyle("-fx-font-size: 16px;");
            planningContent.getChildren().add(noMovieSessionLabel); // Ajouter le message au conteneur principal
        }

        // Effacer tout contenu précédent et ajouter le contenu actuel au conteneur
        // principal
        this.planningFlowPane.getChildren().clear();
        this.planningFlowPane.getChildren().add(planningContent);
    }

    /**
     * Builds a UI card representing the given movie session with premium styling.
     *
     * @param moviesession the MovieSession to represent in the card
     * @return a StackPane containing a premium styled card that displays the film
     *         name, cinema hall name, screening time, and price
     */
    private StackPane createMovieSessionCard(final MovieSession moviesession) {
        final StackPane cardContainer = new StackPane();
        cardContainer.setPrefWidth(650);
        cardContainer.setPrefHeight(140);
        cardContainer.setStyle("-fx-padding: 15;");

        final HBox card = new HBox(15);
        card.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(30, 15, 18, 0.95), rgba(45, 22, 28, 0.9));" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(139, 0, 0, 0.5);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 12, 0, 0, 4);" +
                        "-fx-cursor: hand;");

        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(50, 25, 30, 0.98), rgba(70, 35, 42, 0.95));" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #ff4444;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 0.5), 20, 0, 0, 5);" +
                        "-fx-cursor: hand;" +
                        "-fx-scale-x: 1.02;" +
                        "-fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(30, 15, 18, 0.95), rgba(45, 22, 28, 0.9));" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(139, 0, 0, 0.5);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 12, 0, 0, 4);" +
                        "-fx-cursor: hand;"));

        // Film poster with rounded corners
        final StackPane posterContainer = new StackPane();
        posterContainer.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-background-radius: 12;");
        posterContainer.setPrefWidth(100);
        posterContainer.setPrefHeight(120);

        final ImageView filmImageView = new ImageView();
        filmImageView.setFitWidth(100);
        filmImageView.setFitHeight(120);
        filmImageView.setPreserveRatio(true);
        filmImageView.setStyle("-fx-background-radius: 12;");

        Rectangle clip = new Rectangle(100, 120);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        filmImageView.setClip(clip);

        try {
            if (moviesession.getFilm() != null && moviesession.getFilm().getImageUrl() != null
                    && !moviesession.getFilm().getImageUrl().isEmpty()) {
                final String logoString = moviesession.getFilm().getImageUrl();
                final Image logoImage = new Image(logoString);
                filmImageView.setImage(logoImage);
            }
        } catch (final Exception e) {
            DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        posterContainer.getChildren().add(filmImageView);

        // Info container
        final VBox infoContainer = new VBox(8);
        infoContainer.setPadding(new Insets(5, 10, 5, 10));
        HBox.setHgrow(infoContainer, javafx.scene.layout.Priority.ALWAYS);

        // Film title
        final Label filmNameLabel = new Label(moviesession.getFilm().getTitle());
        filmNameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;");

        // Cinema hall with icon
        final HBox hallBox = new HBox(8);
        hallBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        final FontIcon hallIcon = new FontIcon("mdi2t-theater:14:#ff6666");
        final Label hallLabel = new Label(moviesession.getCinemaHall().getName());
        hallLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
        hallBox.getChildren().addAll(hallIcon, hallLabel);

        // Time with icon
        final HBox timeBox = new HBox(8);
        timeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        final FontIcon timeIcon = new FontIcon("mdi2c-clock-outline:14:#ff6666");
        final Label timeLabel = new Label(moviesession.getStartTime() + " - " + moviesession.getEndTime());
        timeLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        timeBox.getChildren().addAll(timeIcon, timeLabel);

        infoContainer.getChildren().addAll(filmNameLabel, hallBox, timeBox);

        // Right side with price and book button
        final VBox actionContainer = new VBox(10);
        actionContainer.setAlignment(javafx.geometry.Pos.CENTER);
        actionContainer.setPadding(new Insets(5));

        // Price badge
        final Label priceLabel = new Label(String.format("%.2f DT", moviesession.getPrice()));
        priceLabel.setStyle(
                "-fx-background-color: linear-gradient(to right, #ffc800, #cc9900);" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 6 14;" +
                        "-fx-text-fill: #1a0808;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;");

        // Book button
        final Button bookButton = new Button("Book Now");
        bookButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #dc143c, #8b0000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(220, 20, 60, 0.4), 6, 0, 0, 2);");

        bookButton.setOnMouseEntered(e -> bookButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff1a4f, #a00000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 0.6), 10, 0, 0, 3);"));
        bookButton.setOnMouseExited(e -> bookButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #dc143c, #8b0000);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(220, 20, 60, 0.4), 6, 0, 0, 2);"));

        bookButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/SeatSelection.fxml"));
                Parent root = loader.load();

                SeatSelectionController controller = loader.getController();
                Client client = (Client) SessionManager.getCurrentUser();
                controller.initializeWithData(moviesession, client);

                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                DashboardClientController.LOGGER.log(Level.SEVERE, "Error loading seat selection", e);
            }
        });

        actionContainer.getChildren().addAll(priceLabel, bookButton);

        card.getChildren().addAll(posterContainer, infoContainer, actionContainer);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    /**
     * Set up the controller UI: populate cinema cards, configure controls, and set
     * initial visibility.
     * <p>
     * Initializes the cinema list with accepted cinemas, fills the tri-state combo
     * box with sorting options,
     * attaches a live listener to the search field to filter displayed cinemas, and
     * establishes the initial
     * visibility state for listCinemaClient, PlanningPane, AnchorComments, and
     * filterAnchor.
     */
    public void initialize() {
        if (cinemaFlowPane != null) {
            cinemaFlowPane.getChildren().clear();
            loadAcceptedCinemas(); // This already creates the cinema cards
        }

        if (tricomboBox != null) {
            tricomboBox.getItems().addAll("Name", "Address", "Rating");
            tricomboBox.setValue("Name");
        }

        if (searchbar1 != null) {
            searchbar1.textProperty().addListener((observable, oldValue, newValue) -> {
                List<Cinema> searchResults = rechercher(new ArrayList<>(loadAcceptedCinemas()), newValue);
                if (cinemaFlowPane != null) {
                    cinemaFlowPane.getChildren().clear();
                    createCinemaCards(searchResults);
                }

            });
        }

        if (listCinemaClient != null) {
            listCinemaClient.setVisible(true);
        }

        if (PlanningPane != null) {
            PlanningPane.setVisible(false);
        }

        if (AnchorComments != null) {
            AnchorComments.setVisible(false);
        }

        if (filterAnchor != null) {
            filterAnchor.setVisible(false);
        }

    }

    /**
     * Creates a UI card for each cinema and appends it to this controller's
     * cinemaFlowPane.
     *
     * @param cinemas the cinemas for which cards will be created and added to the
     *                pane
     */
    private void createCinemaCards(List<Cinema> cinemas) {
        for (Cinema cinema : cinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }

    }

    /**
     * Fetches cinemas from the CinemaService using the first page (10 items).
     *
     * @return a list of Cinema objects from the first page (up to 10 items)
     */
    private List<Cinema> getAllCinemas() {
        final CinemaService cinemaService = new CinemaService();
        PageRequest pageRequest = PageRequest.defaultPage();
        return cinemaService.read(pageRequest).getContent();
    }

    /**
     * Show the filter pane and populate it with checkboxes for cinema addresses and
     * names.
     * <p>
     * Populates the filterAnchor with two VBoxes containing a labeled list of
     * CheckBox nodes
     * for each unique cinema address and name, and visually dims the main cinema
     * list.
     *
     * @param event the ActionEvent that triggered showing the filter pane
     */
    @FXML
    void filtrer(final ActionEvent event) {
        this.listCinemaClient.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.namesCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> addresses = this.getCinemaAddresses();
        // Récupérer les statuts uniques depuis la base de données
        final List<String> names = this.getCinemaNames();
        // Créer des VBox pour les adresses
        final VBox addressCheckBoxesVBox = new VBox();
        final Label addressLabel = new Label("Adresse");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (final String address : addresses) {
            final CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            this.addressCheckBoxes.add(checkBox);
        }

        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);
        // Créer des VBox pour les statuts
        final VBox namesCheckBoxesVBox = new VBox();
        final Label statusLabel = new Label("Names");
        statusLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        namesCheckBoxesVBox.getChildren().add(statusLabel);
        for (final String name : names) {
            final CheckBox checkBox = new CheckBox(name);
            namesCheckBoxesVBox.getChildren().add(checkBox);
            this.namesCheckBoxes.add(checkBox);
        }

        namesCheckBoxesVBox.setLayoutX(25);
        namesCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le filterAnchor
        this.filterAnchor.getChildren().addAll(addressCheckBoxesVBox, namesCheckBoxesVBox);
        this.filterAnchor.setVisible(true);
    }

    /**
     * Apply the selected address and name filters to the cinema list and update the
     * displayed cinema cards.
     * <p>
     * Reads the selected addresses and names, filters the set of all cinemas so
     * that cinemas match any selected
     * address and any selected name (an empty selection for a criterion means that
     * criterion is not applied),
     * clears the cinema flow pane, and repopulates it with the filtered cinemas.
     *
     * @param event the ActionEvent that triggered the filter action
     */
    @FXML
    void filtrercinema(final ActionEvent event) {
        this.listCinemaClient.setOpacity(1);
        this.filterAnchor.setVisible(false);
        // Récupérer les adresses sélectionnées
        final List<String> selectedAddresses = this.getSelectedAddresses();
        // Récupérer les noms sélectionnés
        final List<String> selectedNames = this.getSelectedNames();
        // Filtrer les cinémas en fonction des adresses et/ou des noms sélectionnés
        final List<Cinema> filteredCinemas = getAllCinemas().stream()
                .filter(cinema -> selectedAddresses.isEmpty() || selectedAddresses.contains(cinema.getAddress()))
                .filter(cinema -> selectedNames.isEmpty() || selectedNames.contains(cinema.getName()))
                .collect(Collectors.toList());
        // Afficher les cinémas filtrés
        this.cinemaFlowPane.getChildren().clear();
        this.createCinemaCards(filteredCinemas);
    }

    /**
     * Get addresses corresponding to the selected address checkboxes.
     *
     * @return a list of address strings for each selected CheckBox; empty if none
     *         are selected.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve the texts of all selected name checkboxes used for filtering.
     *
     * @return a list of selected checkbox texts representing names, or an empty
     *         list if none are selected.
     */
    private List<String> getSelectedNames() {
        // Récupérer les noms sélectionnés dans l'AnchorPane de filtrage
        return this.namesCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve the list of unique cinema addresses.
     *
     * @return a list of unique cinema address strings
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les adresses uniques des cinémas
        return cinemas.stream().map(Cinema::getAddress).distinct().collect(Collectors.toList());
    }

    /**
     * Return the unique cinema names from all cinemas.
     *
     * @return the list of unique cinema names in encounter order.
     */
    public List<String> getCinemaNames() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les noms uniques des cinémas
        return cinemas.stream().map(Cinema::getName).distinct().collect(Collectors.toList());
    }

    /**
     * Open the client events window and close the originating window.
     *
     * <p>
     * Loads the FXML resource "/ui/events/AffichageEvenementClient.fxml", shows it
     * in a new Stage titled "Event Client", and closes the stage that dispatched
     * the provided event.
     *
     * @param event the ActionEvent whose source window will be closed after the new
     *              window is shown
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void afficherEventsClient(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/events/AffichageEvenementClient.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Event Client");
        stage.show();
        currentStage.close();
    }

    /**
     * Display the movies UI (filmuser.fxml) in a new window and close the current
     * window.
     *
     * @param event the ActionEvent whose source window will be closed after the new
     *              window opens
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void afficherMoviesClient(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Movie Manegement");
        stage.show();
        currentStage.close();
    }

    /**
     * Open the product client interface in a new window and close the current
     * stage.
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void afficherProductsClient(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product Client");
        stage.show();
        currentStage.close();
    }

    /**
     * Open the client series UI in a new stage and close the current stage.
     *
     * @throws IOException if the FXML resource "/ui/series/SeriesClient.fxml"
     *                     cannot be loaded
     */
    @FXML
    void afficherSeriesClient(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Serie Client");
        stage.show();
        currentStage.close();
    }

    /**
     * Add the text from the comment input as a cinema comment after performing
     * sentiment analysis.
     *
     * <p>
     * If the input is empty, a warning dialog is shown and no comment is added.
     * Otherwise the comment's sentiment is determined, a CinemaComment for the
     * currently selected cinema is persisted, and the input area is cleared.
     * </p>
     */
    @FXML
    void addCommentaire() {
        final String message = this.txtAreaComments.getText();
        if (message.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        } else {
            final SentimentAnalysisController sentimentAnalysisController = new SentimentAnalysisController();
            final String sentimentResult = sentimentAnalysisController.analyzeSentiment(message);
            DashboardClientController.LOGGER
                    .info(this.cinemaId + " " + new CinemaService().getCinemaById(this.cinemaId));
            final Review commentaire = new Review(new CinemaService().getCinemaById(this.cinemaId),
                    (Client) SessionManager.getCurrentUser(), message, sentimentResult);
            DashboardClientController.LOGGER.info(commentaire + " " + SessionManager.getCurrentUser());
            final ReviewService cinemaCommentService = new ReviewService();
            cinemaCommentService.create(commentaire);
            this.txtAreaComments.clear();
        }

    }

    /**
     * Save the current comment for the selected cinema and refresh the displayed
     * comments.
     * <p>
     * This handler is invoked when the Add Comment UI element is clicked; it stores
     * the
     * comment entered by the user for the active cinema and reloads the comment
     * view.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.cinemaId);
    }

    /**
     * Get all comments for the specified cinema.
     *
     * @param cinemaId the cinema's identifier
     * @return a List of CinemaComment objects belonging to the specified cinema
     */
    private List<Review> getAllComment(final Long cinemaId) {
        final ReviewService cinemaCommentService = new ReviewService();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<Review> allComments = cinemaCommentService.read(pageRequest).getContent();
        final List<Review> cinemaComments = new ArrayList<>();
        for (final Review comment : allComments) {
            if (comment.getCinema().getId() == cinemaId) {
                cinemaComments.add(comment);
            }

        }

        return cinemaComments;
    }

    /**
     * Create an HBox view representing a cinema comment with the user's avatar,
     * name, and message.
     * <p>
     * If the comment's client has no profile image URL, a bundled default image is
     * used.
     *
     * @param commentaire CinemaComment whose client (name and photo) and
     *                    commentText supply the avatar, username, and message to
     *                    display
     * @return an HBox containing the user's circular avatar and a text card with
     *         the user's name and comment, ready to be placed into the comments
     *         ScrollPane
     */
    private HBox addCommentToView(final Review commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        final String imageUrl = commentaire.getUser().getProfilePictureUrl();
        final Image userImage;
        if (null != imageUrl && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
        }

        final ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);
        // Ajouter l'image au cercle
        final Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);
        // Création de la boîte pour l'image et la bordure du cercle
        final HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);
        // Création du conteneur pour la carte du commentaire
        final HBox cardContainer = new HBox();
        cardContainer.setStyle(
                "-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");
        // Nom de l'utilisateur
        final Text userName = new Text(
                commentaire.getUser().getFirstName() + " " + commentaire.getUser().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");
        // Commentaire
        final Text commentText = new Text(commentaire.getComment());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique
        // Création de la boîte pour le texte du commentaire
        final VBox textBox = new VBox();
        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);
        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);
        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        final HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);
        // Ajouter le conteneur principal au ScrollPane
        this.ScrollPaneComments.setContent(contentContainer);
        return contentContainer;
    }

    /**
     * Populate the comments ScrollPane with all comments for the specified cinema.
     *
     * @param cinemaId the ID of the cinema whose comments should be displayed
     */
    private void displayAllComments(final Long cinemaId) {
        final List<Review> comments = this.getAllComment(cinemaId);
        final VBox allCommentsContainer = new VBox();
        for (final Review comment : comments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        this.ScrollPaneComments.setContent(allCommentsContainer);
    }

    /**
     * Close the comments overlay and restore visibility of the main cinema list.
     */
    @FXML
    void closeCommets(final MouseEvent event) {
        this.listCinemaClient.setOpacity(1);
        this.AnchorComments.setVisible(false);
        this.listCinemaClient.setVisible(true);
    }

}

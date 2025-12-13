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
        cardContainer.setStyle("-fx-padding: 25px 0 0 8px ;");
        final AnchorPane card = new AnchorPane();
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;  ");
        card.setPrefWidth(450);
        card.setPrefHeight(150);
        final ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(140);
        logoImageView.setFitHeight(100);
        logoImageView.setLayoutX(15);
        logoImageView.setLayoutY(15);
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
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
        final Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(155);
        NomLabel.setLayoutY(45);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(NomLabel);
        final Label nameLabel = new Label(cinema.getName() != null ? cinema.getName() : "Unknown");
        nameLabel.setLayoutX(210);
        nameLabel.setLayoutY(45);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(nameLabel);
        final Label AdrsLabel = new Label("Adresse: ");
        AdrsLabel.setLayoutX(155);
        AdrsLabel.setLayoutY(85);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);
        final Label adresseLabel = new Label(cinema.getAddress() != null ? cinema.getAddress() : "Unknown");
        adresseLabel.setLayoutX(220);
        adresseLabel.setLayoutY(85);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);
        final Line verticalLine = new Line();
        verticalLine.setStartX(290);
        verticalLine.setStartY(10);
        verticalLine.setEndX(290);
        verticalLine.setEndY(140);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);
        card.getChildren().add(verticalLine);
        final Button moviesButton = new Button("Show Movies");
        moviesButton.setLayoutX(310);
        moviesButton.setLayoutY(35);
        moviesButton.getStyleClass().add("instert-btn");
        card.getChildren().add(moviesButton);
        final Button planningButton = new Button("Show Planning");
        planningButton.setLayoutX(302);
        planningButton.setLayoutY(77);
        planningButton.getStyleClass().add("instert-btn");
        planningButton.setUserData(cinema);
        planningButton.setOnAction(event -> this.showPlanning(cinema));
        card.getChildren().add(planningButton);
        logoImageView.setOnMouseClicked(event -> this.geocodeAddress(cinema.getAddress()));
        final FontIcon CommentIcon = new FontIcon("mdi2c-comment");
        CommentIcon.setIconSize(40);
        CommentIcon.setLayoutX(250);
        CommentIcon.setLayoutY(35);
        CommentIcon.setFill(Color.BLACK);
        card.getChildren().add(CommentIcon);
        CommentIcon.setOnMouseClicked(mouseEvent -> {
            this.cinemaId = cinema.getId();
            this.listCinemaClient.setOpacity(0.5);
            this.AnchorComments.setVisible(true);
            this.displayAllComments(this.cinemaId);
            DashboardClientController.LOGGER.info(String.valueOf(this.cinemaId));
        });
        // Ajout du composant de notation (Rating)
        final ReviewService ratingService = new ReviewService();
        final Rating rating = new Rating();
        rating.setLayoutX(100);
        rating.setLayoutY(100);
        rating.setMax(5);
        // Obtenez le taux spécifique pour ce client et ce cinéma
        final double specificRating = ratingService.getAverageRating(2L);
        // Si le taux spécifique est disponible, définissez le taux affiché
        if (0 <= specificRating) {
            rating.setRating(specificRating);
        }

        // Ajout d'un écouteur pour la notation
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> {
            final Client client = (Client) this.AnchorComments.getScene().getWindow().getUserData();
            // Enregistrez le nouveau taux dans la base de données
            final Review ratingCinema = new Review(cinema, client, newValue.intValue());
            ratingService.create(ratingCinema);
        });
        card.getChildren().add(rating);
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
        final double cardHeight = 100; // Hauteur de chaque carte
        final double cardSpacing = 50; // Espacement entre chaque carte
        double currentY = 10; // Position Y de la première carte
        for (final var review : topRatedReviews) {
            final Cinema cinema = review.getCinema();
            if (cinema == null)
                continue;
            // Création de la carte pour le cinéma
            final AnchorPane card = new AnchorPane();
            card.setStyle(
                    "-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
            card.setPrefWidth(350);
            card.setPrefHeight(cardHeight);
            // Création et positionnement des éléments de la carte (nom, adresse, logo,
            // etc.)
            final ImageView logoImageView = new ImageView();
            logoImageView.setFitWidth(140);
            logoImageView.setFitHeight(100);
            logoImageView.setLayoutX(15);
            logoImageView.setLayoutY(15);
            logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
            try {
                final String logoString = cinema.getLogoUrl();
                final Image logoImage = new Image(logoString);
                logoImageView.setImage(logoImage);
            } catch (final Exception e) {
                DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            card.getChildren().add(logoImageView);
            final Label nomLabel = new Label("Nom: " + cinema.getName());
            nomLabel.setLayoutX(200);
            nomLabel.setLayoutY(10);
            card.getChildren().add(nomLabel);
            final Label adresseLabel = new Label("Adresse: " + cinema.getAddress());
            adresseLabel.setLayoutX(200);
            adresseLabel.setLayoutY(40);
            card.getChildren().add(adresseLabel);
            // Vous pouvez ajouter d'autres éléments comme le logo, le bouton de
            // réservation, etc.
            // Positionnement de la carte dans Anchortop3
            AnchorPane.setTopAnchor(card, currentY);
            AnchorPane.setLeftAnchor(card, 30.0);
            Anchortop3.getChildren().add(card);
            // Mise à jour de la position Y pour la prochaine carte
            currentY += cardHeight + cardSpacing;
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
        planningContent.setSpacing(10);
        this.tilePane = new TilePane();
        this.tilePane.setPrefColumns(7);
        this.tilePane.setPrefRows(1);
        this.tilePane.setHgap(5);
        this.tilePane.setVgap(5);
        final LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; 7 > i; i++) {
            final LocalDate date = startDateOfWeek.plusDays(i);
            final Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEE dd/MM")));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;");
            dayLabel.setOnMouseClicked(event -> this.displayMovieSessionsForDate(date, cinema));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            this.tilePane.getChildren().add(dayLabel);
        }

        VBox.setMargin(this.tilePane, new Insets(0, 0, 0, 50));
        planningContent.getChildren().add(this.tilePane);
        final Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(200);
        planningContent.getChildren().add(separator);
        this.planningFlowPane.getChildren().clear();
        this.planningFlowPane.getChildren().add(planningContent);
        AnchorPane.setTopAnchor(planningContent, 50.0);
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
     * Builds a UI card representing the given movie session.
     *
     * @param moviesession the MovieSession to represent in the card
     * @return a StackPane containing a right-aligned card that displays the film
     *         name, cinema hall name, screening time, and price
     */
    private StackPane createMovieSessionCard(final MovieSession moviesession) {
        final StackPane cardContainer = new StackPane();
        cardContainer.setPrefWidth(600);
        cardContainer.setPrefHeight(100);
        cardContainer.setStyle("-fx-padding: 10 0 0 150;");
        final HBox card = new HBox();
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        final ImageView filmImageView = new ImageView();
        filmImageView.setFitWidth(140);
        filmImageView.setFitHeight(100);
        filmImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
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

        card.getChildren().add(filmImageView);
        final VBox labelsContainer = new VBox();
        labelsContainer.setSpacing(5);
        labelsContainer.setPadding(new Insets(10));
        final Label filmNameLabel = new Label("Film: " + moviesession.getFilm().getTitle());
        filmNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label cinemahallNameLabel = new Label("CinemaHall: " + moviesession.getCinemaHall().getName());
        cinemahallNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label timeLabel = new Label("Heure: " + moviesession.getStartTime() + " - " + moviesession.getEndTime());
        timeLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label priceLabel = new Label("Prix: " + moviesession.getPrice());
        priceLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Button bookButton = new Button("Reserver");
        bookButton.getStyleClass().add("instert-btn");
        bookButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/SeatSelection.fxml"));
                Parent root = loader.load();

                SeatSelectionController controller = loader.getController();
                // Assuming we can get the current client from the scene's user data or a global
                // context
                // For now, using the same method as used elsewhere in this controller (e.g.
                // rating)
                Client client = (Client) cardContainer.getScene().getWindow().getUserData();
                // If client is null (e.g. testing), we might need a fallback or check
                if (client == null) {
                    // Fallback for testing or if not set
                    // You might want to handle this better in a real app
                    com.esprit.services.users.UserService us = new com.esprit.services.users.UserService();
                    client = (Client) us.getUserById(2L); // Temporary fallback as seen in other methods
                }

                controller.initialize(moviesession, client);

                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                DashboardClientController.LOGGER.log(Level.SEVERE, "Error loading seat selection", e);
            }
        });
        labelsContainer.getChildren().addAll(filmNameLabel, cinemahallNameLabel, timeLabel, priceLabel, bookButton);
        card.getChildren().add(labelsContainer);
        cardContainer.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER_RIGHT); // Positionne la carte à droite dans le conteneur StackPane
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
            HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
            createCinemaCards(new ArrayList<>(acceptedCinemas));
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
                    (Client) new UserService().getUserById(2L), message, sentimentResult);
            DashboardClientController.LOGGER.info(commentaire + " " + new UserService().getUserById(2L));
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

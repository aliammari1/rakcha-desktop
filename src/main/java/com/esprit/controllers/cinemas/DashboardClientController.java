package com.esprit.controllers.cinemas;

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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.controlsfx.control.Rating;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaComment;
import com.esprit.models.cinemas.CinemaRating;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaCommentService;
import com.esprit.services.cinemas.CinemaRatingService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.users.UserService;
import com.esprit.utils.PageRequest;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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
     * Searches for Cinema objects in a list based on a search term and returns a
     * list of matching objects.
     *
     * @param liste
     *                  list of cinemas to search in.
     *                  <p>
     *                  - `liste` is a list of `Cinema` objects.
     * @param recherche
     *                  search query used to filter the list of cinemas returned by
     *                  the
     *                  function.
     * @return a list of Cinema objects containing the search query.
     *         <p>
     *         - The List of Cinema objects `resultats` is initialized and returned
     *         by the method. - It contains Cinema objects that match the search
     *         criteria, as determined by the `if` statement in the method body. -
     *         Each element in the list is a Cinema object with a non-null `nom`
     *         attribute that contains the search term `recherche`.
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
     * Sets the visibility of a pane and two lists to false and true, respectively,
     * upon an action event.
     *
     * @param event
     *              event that triggered the `Planninggclose()` method to be called,
     *              providing the necessary context for the method to perform its
     *              intended action.
     *              <p>
     *              Event: `ActionEvent`
     *              <p>
     *              - `target`: Reference to the component that triggered the event
     *              (in this case, `PlanningPane`) - `code`: The action that was
     *              performed (in this case, `setVisible(false)`)
     */
    @FXML
    void Planninggclose(final ActionEvent event) {
        this.PlanningPane.setVisible(false);
        this.listCinemaClient.setOpacity(1);
        this.listCinemaClient.setVisible(true);
    }

    /**
     * Clears the children of a `FlowPane`, loads and displays a list of accepted
     * cinemas, and sets the visibility of the `listCinemaClient` and `PlanningPane`
     * to `true` and `false`, respectively.
     *
     * @param event
     *              user's action of clicking the "Show List Cinema" button, which
     *              triggers the function to clear the content of the
     *              `cinemaFlowPane`, load the accepted cinemas, and set the
     *              visibility of the `listCinemaClient` and `PlanningPane`.
     *              <p>
     *              Event: An action event object representing a user interaction.
     */
    @FXML
    void showListCinema(final ActionEvent event) {
        this.cinemaFlowPane.getChildren().clear();
        final HashSet<Cinema> acceptedCinemas = this.loadAcceptedCinemas();
        this.listCinemaClient.setVisible(true);
        this.PlanningPane.setVisible(false);
    }

    /**
     * Loads a set of cinemas from a service, filters them based on their status,
     * and returns a HashSet of accepted cinemas to be displayed in a user
     * interface.
     *
     * @returns a set of `Cinema` objects representing the accepted cinemas.
     *          <p>
     *          - `HashSet<Cinema>` represents a set of accepted cinemas. - The set
     *          contains cinema objects that have a `Statut` field equal to
     *          "Accepted". - The set is generated by filtering the list of cinemas
     *          read from the `CinemaService` using the `filter()` method and then
     *          collecting the results into a list using the `collect()` method. -
     *          The list is then converted into a hash set using the `toList()`
     *          method.
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        final CinemaService cinemaService = new CinemaService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Cinema> cinemas = cinemaService.read(pageRequest).getContent();
        final List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> "Accepted".equals(cinema.getStatus())).collect(Collectors.toList());
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
     * Creates an Alert object with a title, header text, and content text. It then
     * shows the Alert to the user.
     *
     * @param message
     *                text to be displayed as an information alert when the
     *                `showAlert()` method is called.
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
     * Creates a card container and adds various components to it, including an
     * image view for the cinema logo, labels for the name and address, a vertical
     * line, buttons for showing movies and planning, and a rating component.
     *
     * @param cinema
     *               Cinema object that provides the necessary information for
     *               creating
     *               the cinema card, such as name, logo, address, and rating.
     *               <p>
     *               - `getLogo()`: returns the cinema's logo as a string -
     *               `getName()`: returns the cinema's name - `getAdresse()`:
     *               returns
     *               the cinema's address - `getId_cinema()`: returns the cinema's
     *               ID
     *               <p>
     *               These properties are used to create and display a card for the
     *               cinema.
     * @returns a HBox container with a cinema card displaying the cinema's name,
     *          address, and logo, along with a rating system for the client.
     *          <p>
     *          1/ `cardContainer`: This is the outermost container for the cinema
     *          card, which holds all the child elements. 2/ `card`: This is the
     *          inner container that holds all the elements related to the cinema,
     *          such as logo, name, address, and buttons. 3/ `logoImageView`: This
     *          is an image view containing the cinema's logo. 4/ `NomLabel`: This
     *          is a label displaying the cinema's name. 5/ `nameLabel`: This is
     *          another label displaying the cinema's nom (French for "name"). 6/
     *          `AdrsLabel`: This is a label displaying the cinema's address. 7/
     *          `adresseLabel`: Another label displaying the cinema's address. 8/
     *          `moviesButton`: This is a button that displays the text "Show
     *          Movies" and allows users to view movies available at the cinema. 9/
     *          `planningButton`: This is another button that displays the text
     *          "Show Planning" and allows users to view the cinema's planning. 10/
     *          `CommentIcon`: This is an icon view displaying a comment symbol,
     *          which allows users to leave comments for the cinema. 11/ `rating`:
     *          This is a rating view displaying the taux (French for "rate") of the
     *          cinema based on user feedback.
     *          <p>
     *          In summary, the `createCinemaCard` function returns a container
     *          object that holds all the elements related to a particular cinema.
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
            final String logoString = cinema.getLogoPath();
            final Image logoImage = new Image(logoString);
            logoImageView.setImage(logoImage);
        } catch (final Exception e) {
            DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        card.getChildren().add(logoImageView);
        final Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(155);
        NomLabel.setLayoutY(45);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(NomLabel);
        final Label nameLabel = new Label(cinema.getName());
        nameLabel.setLayoutX(210);
        nameLabel.setLayoutY(45);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(nameLabel);
        final Label AdrsLabel = new Label("Adresse: ");
        AdrsLabel.setLayoutX(155);
        AdrsLabel.setLayoutY(85);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);
        final Label adresseLabel = new Label(cinema.getAddress());
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
        final FontAwesomeIconView CommentIcon = new FontAwesomeIconView();
        CommentIcon.setGlyphName("COMMENTING");
        CommentIcon.setSize("2.5em");
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
        final CinemaRatingService ratingService = new CinemaRatingService();
        final Rating rating = new Rating();
        rating.setLayoutX(100);
        rating.setLayoutY(100);
        rating.setMax(5);
        // Obtenez le taux spécifique pour ce client et ce cinéma
        final int specificRating = ratingService.getRatingForClientAndCinema(2L, cinema.getId());
        // Si le taux spécifique est disponible, définissez le taux affiché
        if (0 <= specificRating) {
            rating.setRating(specificRating);
        }
        // Ajout d'un écouteur pour la notation
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> {
            final Client client = (Client) this.AnchorComments.getScene().getWindow().getUserData();
            // Enregistrez le nouveau taux dans la base de données
            final CinemaRating ratingCinema = new CinemaRating(cinema, client, newValue.intValue());
            ratingService.create(ratingCinema);
        });
        card.getChildren().add(rating);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    /**
     * Creates and positions top-rated cinema cards within an `AnchorPane`. Each
     * card displays the cinema's name, address, and logo, and is spaced apart by a
     * fixed distance. The position of the next card is updated after each card is
     * added.
     *
     * @param Anchortop3
     *                   `AnchorPane` where the top-rated cinema cards will be
     *                   added.
     *                   <p>
     *                   - `Anchortop3`: Anchor pane where the cinema cards will be
     *                   added.
     *                   - `topRatedCinemas`: List of top-rated cinemas to create
     *                   cards
     *                   for. - `cardHeight`: Height of each cinema card. -
     *                   `cardSpacing`:
     *                   Spacing between each cinema card. - `currentY`: Position Y
     *                   of the
     *                   first cinema card.
     */
    public void createTopRatedCinemaCards(final AnchorPane Anchortop3) {
        final CinemaRatingService ratingCinemaService = new CinemaRatingService();
        final List<Cinema> topRatedCinemas = ratingCinemaService.getTopRatedCinemas();
        final double cardHeight = 100; // Hauteur de chaque carte
        final double cardSpacing = 50; // Espacement entre chaque carte
        double currentY = 10; // Position Y de la première carte
        for (final Cinema cinema : topRatedCinemas) {
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
                final String logoString = cinema.getLogoPath();
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
     * Geocodes a given address by sending a GET request to the OpenStreetMap
     * Nominatim API and retrieving the lat and lon coordinates for the address. It
     * then opens a map dialog with the retrieved coordinates.
     *
     * @param address
     *                address to be geocoded, which is sent as a query to the
     *                Nominatim
     *                API to retrieve the latitude and longitude coordinates for the
     *                location.
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
     * Creates a new dialog, loads a map into a WebView, updates the marker position
     * using JavaScript, and displays the dialog with a close button.
     *
     * @param lat
     *            latitude coordinate of the map location, which is used to load the
     *            appropriate map and place a marker at the corresponding position
     *            on the map.
     * @param lon
     *            longitude coordinate of the location where the map should be
     *            displayed, which is used to load the appropriate map and place a
     *            marker at the specified position.
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
     * Displays a planning page for a cinema, consisting of 7 days of the week, each
     * day represented by a label with the date and a button to display
     * moviesessions for that date.
     *
     * @param cinema
     *               Cinema object that contains information about the cinema and
     *               its
     *               scheduling.
     *               <p>
     *               - `listCinemaClient`: A visible container for cinema client
     *               listings (set to `false`). - `PlanningPane`: Visible pane
     *               displaying the planning schedule (set to `true`). - `tilePane`:
     *               A
     *               container for displaying individual days of the week in a tile
     *               format (created and added to `planningContent`).
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
     * Loads the planning for the current week (Sunday to Saturday) for a given
     * cinema using MovieSessionService.
     *
     * @param startDate
     *                  starting date of the current week for which the planning is
     *                  being
     *                  loaded.
     *                  <p>
     *                  - LocalDate representing the start date of the current week
     *                  - Can
     *                  be modified or manipulated within the function
     *                  <p>
     *                  Please provide the Java code for which you would like a
     *                  summary.
     * @param cinema
     *                  cinemas for which the moviesession is being planned.
     *                  <p>
     *                  - Cinema is an object representing a cinema with unknown
     *                  details.
     * @returns a map containing the seating plan for the current week at a specific
     *          cinema.
     *          <p>
     *          The output is a map that contains key-value pairs, where the keys
     *          are `LocalDate` objects representing the dates of the current week,
     *          and the values are lists of `MovieSession` objects representing the
     *          moviesessions scheduled for those dates at the corresponding cinema.
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
     * Loads the current week's planning data, displays it in a VBox, and adds an
     * event listener to display more detailed moviesession information when a date
     * is clicked.
     *
     * @param date
     *               LocalDate for which to display the cinema moviesessions, and it
     *               is
     *               used to load the relevant planning data from the database or
     *               API.
     *               <p>
     *               - `LocalDate date`: represents a specific date in the format
     *               `YYYY-MM-DD`. - ` cinema`: represents the cinema for which the
     *               seating plan is being generated.
     * @param cinema
     *               cinema for which the moviesessions are being displayed, and is
     *               used to load the relevant planning data into the function.
     *               <p>
     *               - `cinema`: A `Cinema` object representing the cinema for which
     *               the moviesessions are being displayed. Its main properties
     *               include
     *               the cinema's name and address.
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
     * Generates a stack pane containing a card with information about a
     * moviesession, including the film's name, cinema hall, screening time, and
     * price.
     *
     * @param moviesession
     *                     MovieSession object that contains information about the
     *                     film,
     *                     cinemahall, and time of the screening, which is used to
     *                     populate
     *                     the card with relevant labels.
     *                     <p>
     *                     - `moviesession.getFilmcinema()`: Returns an instance of
     *                     `Filmcinema` containing information about the film
     *                     showing at the
     *                     moviesession. - `moviesession.getName_cinemahall()`:
     *                     Returns the
     *                     name of the hall where the moviesession is taking place.
     *                     -
     *                     `moviesession.getHD()` and `moviesession.getHF()`: Return
     *                     the
     *                     starting time and ending time of the moviesession,
     *                     respectively. -
     *                     `moviesession.getPrice()`: Returns the price of the
     *                     moviesession.
     * @returns a stack pane containing a HBox with an ImageView and three Labels.
     *          <p>
     *          - `cardContainer`: A StackPane that contains all the elements that
     *          make up the moviesession card. - `filmImageView`: An ImageView that
     *          displays an image of the film being shown in the moviesession. -
     *          `labelsContainer`: A VBox that contains four Labels displaying
     *          information about the moviesession, including the film name,
     *          cinemahall name, time, and price. - `filmNameLabel`,
     *          `cinemahallNameLabel`, `timeLabel`, and `priceLabel`: The labels
     *          that are contained within the `labelsContainer`. Each label displays
     *          a piece of information related to the moviesession.
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
            final String logoString = moviesession.getFilm().getImage();
            final Image logoImage = new Image(logoString);
            filmImageView.setImage(logoImage);
        } catch (final Exception e) {
            DashboardClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        card.getChildren().add(filmImageView);
        final VBox labelsContainer = new VBox();
        labelsContainer.setSpacing(5);
        labelsContainer.setPadding(new Insets(10));
        final Label filmNameLabel = new Label("Film: " + moviesession.getFilm().getName());
        filmNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label cinemahallNameLabel = new Label("CinemaHall: " + moviesession.getCinemaHall().getName());
        cinemahallNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label timeLabel = new Label("Heure: " + moviesession.getStartTime() + " - " + moviesession.getEndTime());
        timeLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        final Label priceLabel = new Label("Prix: " + moviesession.getPrice());
        priceLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        labelsContainer.getChildren().addAll(filmNameLabel, cinemahallNameLabel, timeLabel, priceLabel);
        card.getChildren().add(labelsContainer);
        cardContainer.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER_RIGHT); // Positionne la carte à droite dans le conteneur StackPane
        return cardContainer;
    }

    /**
     * Sets up the user interface and connects it to a `CinemaService` for movie
     * data retrieval. It listens for text changes in a search bar, queries the
     * service for relevant movies, and displays them on a pane. Additionally, it
     * sorts and displays top-rated movies based on a predetermined criterion.
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
     * Creates film cards for a list of cinemas and adds them to a pane containing
     * the film cards.
     *
     * @param cinemas
     *                List of cinemas for which film cards are to be created and
     *                added
     *                to the pane.
     */
    private void createCinemaCards(List<Cinema> cinemas) {
        for (Cinema cinema : cinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }
    }

    /**
     * Retrieves a list of cinemas through a call to the ` CinemaService`. It then
     * returns the list of cinemas.
     *
     * @returns a list of `Cinema` objects retrieved from the Cinema Service.
     */
    private List<Cinema> getAllCinemas() {
        final CinemaService cinemaService = new CinemaService();
        PageRequest pageRequest = new PageRequest(0, 10);
        return cinemaService.read(pageRequest).getContent();
    }

    /**
     * Updates the visibility of the `filterAnchor` pane and adds two VBoxes
     * containing check boxes for addresses and names to the pane.
     *
     * @param event
     *              Anchor Button's event that triggered the filtration process.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the triggered
     *              action. - `listCinemaClient`: A `VBox` container for displaying
     *              the cinema client list. - `filterAnchor`: A `Region` component
     *              for
     *              hosting the filtering controls.
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
     * Filters a list of cinemas based on selected addresses and/or names, and
     * displays the filtered list in a flow pane.
     *
     * @param event
     *              occurrence of an action event, triggering the function to
     *              execute
     *              and filter the cinemas based on the selected addresses and/or
     *              names.
     *              <p>
     *              - `listCinemaClient`: A reference to an observable list of
     *              cinemas. - `filterAnchor`: A reference to a component that
     *              displays a filter option. - `getSelectedAddresses()` and
     *              `getSelectedNames()`: Methods that return lists of selected
     *              addresses and names, respectively.
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
     * Streams, filters, and collects the selected addresses from the
     * `addressCheckBoxes` array, returning a list of strings representing the
     * selected addresses.
     *
     * @returns a list of selected addresses.
     *          <p>
     *          - The list contains only the strings of selected addresses. - Each
     *          string represents a single address selected in the AnchorPane of
     *          filtering. - The list is generated by streaming the checked
     *          CheckBoxes, applying the `filter()` method to select only the
     *          checked ones, and then mapping the text property of each checked
     *          CheckBox to its corresponding string value.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * In Java code returns a list of selected names from an `AnchorPane` of
     * filtering by streaming, filtering, and mapping the values of `CheckBox`
     * objects.
     *
     * @returns a list of selected names from an AnchorPane of filtering controls.
     *          <p>
     *          - The list contains only strings representing the selected names
     *          from the `namesCheckBoxes`. - The elements in the list are the texts
     *          of the selected checkboxes.
     */
    private List<String> getSelectedNames() {
        // Récupérer les noms sélectionnés dans l'AnchorPane de filtrage
        return this.namesCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all cinemas from a database, extracts unique addresses from them
     * using Stream API, and returns a list of those addresses.
     *
     * @returns a list of unique cinema addresses retrieved from a database.
     *          <p>
     *          - The output is a list of Strings, representing the unique addresses
     *          of cinemas. - The list contains the addresses of all cinemas
     *          retrieved from the database through the `getAllCinemas()` function.
     *          - The addresses are obtained by calling the `getAdresse()` method on
     *          each `Cinema` object in the list and then applying a `distinct()`
     *          operation to eliminate duplicates using the `Collectors.toList()`
     *          method.
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les adresses uniques des cinémas
        return cinemas.stream().map(Cinema::getAddress).distinct().collect(Collectors.toList());
    }

    /**
     * Retrieves a list of unique cinema names from a database by mapping and
     * collecting the `nom` attributes of each `Cinema` object in the list.
     *
     * @returns a list of unique cinema names retrieved from the database.
     *          <p>
     *          - The output is a list of unique strings, representing the names of
     *          cinemas. - The list is generated by streaming the `cinemas` list,
     *          applying the `map` method to extract the names, and then using the
     *          `distinct` method to remove duplicates. - Finally, the list is
     *          collected into a new list using the `collect` method.
     */
    public List<String> getCinemaNames() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les noms uniques des cinémas
        return cinemas.stream().map(Cinema::getName).distinct().collect(Collectors.toList());
    }

    /**
     * Loads an fxml file and displays a stage with the content from the loaded
     * fxml.
     *
     * @param event
     *              Event Object that triggered the function, providing information
     *              about the event that occurred, such as the source of the event
     *              and
     *              the type of event.
     *              <p>
     *              Event type: The type of event that triggered the function
     *              execution, which could be any of the possible types recognized
     *              by
     *              the application.
     */
    @FXML
    void afficherEventsClient(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/AffichageEvenementClient.fxml"));
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
     * Displays a FXML user interface for managing movies using an FXMLLoader and a
     * Stage object.
     *
     * @param event
     *              an action event that triggered the function execution, providing
     *              the necessary context for the code to perform its intended task.
     *              <p>
     *              - `event` is an `ActionEvent`, indicating that the function was
     *              triggered by user action.
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
     * Loads an FXML file to display a product client interface, creates a new stage
     * for the interface, and closes the previous stage.
     *
     * @param event
     *              event that triggered the function, specifically the button click
     *              event that activates the function to display the product client
     *              interface.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user action
     *              that
     *              triggered the function execution.
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
     * Loads a FXML file "/ui/series/SeriesClient.fxml" and displays it on a new
     * stage, replacing the current stage.
     *
     * @param event
     *              event that triggered the execution of the
     *              `afficherSeriesClient()`
     *              method, specifically the button click event on the client series
     *              view.
     *              <p>
     *              - Event type: `ActionEvent` - Source object: (`Node`) reference
     *              to
     *              the element that triggered the event
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
     * Allows users to add comments to a cinema. When a user clicks on the "Add
     * Comment" button, the function takes the user's comment and analyzes its
     * sentiment using a sentiment analysis controller. If the comment is not empty,
     * the function creates a new `CinemaComment` object with the cinema ID, user
     * ID, message, and sentiment result, and saves it to the database using the
     * `CommentaireCinamaService`.
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
            final CinemaComment commentaire = new CinemaComment(new CinemaService().getCinemaById(this.cinemaId),
                    (Client) new UserService().getUserById(2L), message, sentimentResult);
            DashboardClientController.LOGGER.info(commentaire + " " + new UserService().getUserById(2L));
            final CinemaCommentService cinemaCommentService = new CinemaCommentService();
            cinemaCommentService.create(commentaire);
            this.txtAreaComments.clear();
        }
    }

    /**
     * Adds a comment to a cinema and displays all comments for that cinema when the
     * event is triggered.
     *
     * @param event
     *              user's click on the "Add Comment" button, which triggers the
     *              execution of the `addCommentaire()` method and the display of
     *              all
     *              comments for the specified `cinemaId`.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.cinemaId);
    }

    /**
     * Retrieves all comments related to a specific cinema, using a service to read
     * the comments and then filtering them based on the cinema ID.
     *
     * @param cinemaId
     *                 Id of the cinema for which the comments are to be retrieved.
     * @returns a list of `CinemaComment` objects for the specified cinema ID.
     *          <p>
     *          - The output is a list of `CinemaComment` objects, representing all
     *          comments for a given cinema ID. - Each comment is associated with a
     *          cinema ID and a list of other attributes such as text, author, date,
     *          etc. - The list of comments is obtained through a service call to
     *          the CinemaCommentService class.
     */
    private List<CinemaComment> getAllComment(final Long cinemaId) {
        final CinemaCommentService cinemaCommentService = new CinemaCommentService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<CinemaComment> allComments = cinemaCommentService.read(pageRequest).getContent();
        final List<CinemaComment> cinemaComments = new ArrayList<>();
        for (final CinemaComment comment : allComments) {
            if (comment.getCinema().getId() == cinemaId) {
                cinemaComments.add(comment);
            }
        }
        return cinemaComments;
    }

    /**
     * Creates a container for displaying a user's comment and image, with a
     * transparent background and padding. It also sets the image view's position to
     * center the image within the circle.
     *
     * @param commentaire
     *                    CinemaComment object passed to the function, containing
     *                    information about the user's comment and image.
     *                    <p>
     *                    - `client`: contains information about the user who made
     *                    the
     *                    comment + `getPhotoDeProfil()`: the URL of the user's
     *                    profile
     *                    picture - `getCommentaire()`: the actual comment made by
     *                    the user
     *                    <p>
     *                    Both properties are used to generate the image and text
     *                    display
     *                    for the comment.
     * @returns a `HBox` container containing an image and text related to a
     *          comment.
     *          <p>
     *          - `HBox contentContainer`: This is the container that holds all the
     *          elements related to a comment, including the image, username, and
     *          comment text. It has a prefheight of 50 pixels and a style of
     *          `-fx-background-color: transparent; -fx-padding: 10px`. - `ImageBox
     *          imageBox`: This is the box that holds the image of the user who made
     *          the comment. It has no style defined. - `ImageView userImage`: This
     *          is the image view that displays the image of the user. It has a fit
     *          width and height of 50 pixels each, and is centered in the image box
     *          using `setTranslateX` and `setTranslateY`. - `Group imageGroup`:
     *          This is the group that holds both the image and the image view. It
     *          has no style defined. - `Text userName`: This is the text that
     *          displays the user's name. It has a style of `-fx-font-family: 'Arial
     *          Rounded MT Bold'; -fx-font-style: bold;`. - `Text commentText`: This
     *          is the text that displays the comment made by the user. It has a
     *          style of `-fx-font-family: 'Arial'; -fx-max-width: 300 ;`. - `VBox
     *          textBox`: This is the box that holds both the user name and comment
     *          text. It has no style defined. - `CardContainer cardContainer`: This
     *          is the container that holds all the elements related to a single
     *          comment, including the image, username, and comment text. It has a
     *          style of `-fx-background-color: white; -fx-padding: 5px ;
     *          -fx-border-radius: 8px; -fx-border-color: #000;
     *          -fx-background-radius: 8px;`.
     */
    private HBox addCommentToView(final CinemaComment commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        final String imageUrl = commentaire.getClient().getPhotoDeProfil();
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
                commentaire.getClient().getFirstName() + " " + commentaire.getClient().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");
        // Commentaire
        final Text commentText = new Text(commentaire.getCommentText());
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
     * Displays all comments associated with a particular cinema ID in a scroll
     * pane.
     *
     * @param cinemaId
     *                 identity of the cinema for which all comments are to be
     *                 displayed.
     */
    private void displayAllComments(final Long cinemaId) {
        final List<CinemaComment> comments = this.getAllComment(cinemaId);
        final VBox allCommentsContainer = new VBox();
        for (final CinemaComment comment : comments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }
        this.ScrollPaneComments.setContent(allCommentsContainer);
    }

    /**
     * Sets the opacity of a component to 1, makes an component invisible and
     * another visible.
     *
     * @param event
     *              mouse event that triggered the execution of the `closeCommets()`
     *              method.
     *              <p>
     *              Event type: MouseEvent Target element: AnchorComments Current
     *              state: Visible
     */
    @FXML
    void closeCommets(final MouseEvent event) {
        this.listCinemaClient.setOpacity(1);
        this.AnchorComments.setVisible(false);
        this.listCinemaClient.setVisible(true);
    }
}

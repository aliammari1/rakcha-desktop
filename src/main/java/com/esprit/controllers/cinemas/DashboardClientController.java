package com.esprit.controllers.cinemas;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CommentaireCinema;
import com.esprit.models.cinemas.RatingCinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.CommentaireCinemaService;
import com.esprit.services.cinemas.RatingCinemaService;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.services.users.UserService;
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
import org.controlsfx.control.Rating;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Is responsible for handling user interactions related to the cinema dashboard. It
 * provides methods for displaying all comments, adding new comments, and closing the
 * comment section. The class also includes a scroll pane for displaying all comments
 * for a given cinema ID. Additionally, it includes an HBox for each comment, which
 * contains an image of the user who made the comment, their name, and the comment itself.
 */
public class DashboardClientController {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> namesCheckBoxes = new ArrayList<>();
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
    private AnchorPane FilterAnchor;
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
    private int cinemaId;
    private List<Cinema> l1 = new ArrayList<>();
    @FXML
    private ComboBox<String> tricomboBox;
    private LocalDate lastSelectedDate;
    @FXML
    private AnchorPane Anchortop3;
    /**
     * Searches for Cinema objects in a list based on a search term and returns a list
     * of matching objects.
     * 
     * @param liste list of cinemas to search in.
     * 
     * 	- `liste` is a list of `Cinema` objects.
     * 
     * @param recherche search query used to filter the list of cinemas returned by the
     * function.
     * 
     * @returns a list of `Cinema` objects containing the search query.
     * 
     * 	- The `List<Cinema>` object `resultats` is initialized and returned by the method.
     * 	- It contains Cinema objects that match the search criteria, as determined by the
     * `if` statement in the method body.
     * 	- Each element in the list is a Cinema object with a non-null `nom` attribute
     * that contains the search term `recherche`.
     */
    @FXML
    public static List<Cinema> rechercher(List<Cinema> liste, String recherche) {
        List<Cinema> resultats = new ArrayList<>();
        for (Cinema element : liste) {
            if (element.getNom() != null && element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }
        return resultats;
    }
//     closeDetailFilm.setOnAction(new EventHandler<ActionEvent>() {
//        @Override
//        public void handle(ActionEvent event) {
//            PlanningPane.setVisible(false);
//            listCinemaClient.setOpacity(1);
//            listCinemaClient.setDisable(false);
//        }
//    });
    /**
     * Sets the visibility of a pane and two lists to false and true, respectively, upon
     * an action event.
     * 
     * @param event event that triggered the `Planninggclose()` method to be called,
     * providing the necessary context for the method to perform its intended action.
     * 
     * Event: `ActionEvent`
     * 
     * 	- `target`: Reference to the component that triggered the event (in this case, `PlanningPane`)
     * 	- `code`: The action that was performed (in this case, `setVisible(false)`)
     */
    @FXML
    void Planninggclose(ActionEvent event) {
        PlanningPane.setVisible(false);
        listCinemaClient.setOpacity(1);
        listCinemaClient.setVisible(true);
    }
    /**
     * Clears the children of a `FlowPane`, loads and displays a list of accepted cinemas,
     * and sets the visibility of the `listCinemaClient` and `PlanningPane` to `true` and
     * `false`, respectively.
     * 
     * @param event user's action of clicking the "Show List Cinema" button, which triggers
     * the function to clear the content of the `cinemaFlowPane`, load the accepted
     * cinemas, and set the visibility of the `listCinemaClient` and `PlanningPane`.
     * 
     * Event: An action event object representing a user interaction.
     */
    @FXML
    void showListCinema(ActionEvent event) {
        cinemaFlowPane.getChildren().clear();
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
        listCinemaClient.setVisible(true);
        PlanningPane.setVisible(false);
    }
    /**
     * Loads a set of cinemas from a service, filters them based on their status, and
     * returns a HashSet of accepted cinemas to be displayed in a user interface.
     * 
     * @returns a set of `Cinema` objects representing the accepted cinemas.
     * 
     * 	- `HashSet<Cinema>` represents a set of accepted cinemas.
     * 	- The set contains cinema objects that have a `Statut` field equal to "Accepted".
     * 	- The set is generated by filtering the list of cinemas read from the `CinemaService`
     * using the `filter()` method and then collecting the results into a list using the
     * `collect()` method.
     * 	- The list is then converted into a hash set using the `toList()` method.
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Accepted"))
                .collect(Collectors.toList());
        if (acceptedCinemasList.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
        }
        HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        for (Cinema cinema : acceptedCinemasSet) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }
        return acceptedCinemasSet;
    }
    /**
     * Creates an Alert object with a title, header text, and content text. It then shows
     * the Alert to the user.
     * 
     * @param message text to be displayed as an information alert when the `showAlert()`
     * method is called.
     */
    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    /**
     * Creates a card container and adds various components to it, including an image
     * view for the cinema logo, labels for the name and address, a vertical line, buttons
     * for showing movies and planning, and a rating component.
     * 
     * @param cinema Cinema object that provides the necessary information for creating
     * the cinema card, such as name, logo, address, and rating.
     * 
     * 	- `getLogo()`: returns the cinema's logo as a string
     * 	- `getNom()`: returns the cinema's name
     * 	- `getAdresse()`: returns the cinema's address
     * 	- `getId_cinema()`: returns the cinema's ID
     * 
     * These properties are used to create and display a card for the cinema.
     * 
     * @returns a HBox container with a cinema card displaying the cinema's name, address,
     * and logo, along with a rating system for the client.
     * 
     * 1/ `cardContainer`: This is the outermost container for the cinema card, which
     * holds all the child elements.
     * 2/ `card`: This is the inner container that holds all the elements related to the
     * cinema, such as logo, name, address, and buttons.
     * 3/ `logoImageView`: This is an image view containing the cinema's logo.
     * 4/ `NomLabel`: This is a label displaying the cinema's name.
     * 5/ `nameLabel`: This is another label displaying the cinema's nom (French for "name").
     * 6/ `AdrsLabel`: This is a label displaying the cinema's address.
     * 7/ `adresseLabel`: Another label displaying the cinema's address.
     * 8/ `moviesButton`: This is a button that displays the text "Show Movies" and allows
     * users to view movies available at the cinema.
     * 9/ `planningButton`: This is another button that displays the text "Show Planning"
     * and allows users to view the cinema's planning.
     * 10/ `CommentIcon`: This is an icon view displaying a comment symbol, which allows
     * users to leave comments for the cinema.
     * 11/ `rating`: This is a rating view displaying the taux (French for "rate") of the
     * cinema based on user feedback.
     * 
     * In summary, the `createCinemaCard` function returns a container object that holds
     * all the elements related to a particular cinema.
     */
    private HBox createCinemaCard(Cinema cinema) {
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 25px 0 0 8px ;");
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;  ");
        card.setPrefWidth(450);
        card.setPrefHeight(150);
        ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(140);
        logoImageView.setFitHeight(100);
        logoImageView.setLayoutX(15);
        logoImageView.setLayoutY(15);
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
        try {
            String logoString = cinema.getLogo();
            Image logoImage = new Image(logoString);
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(logoImageView);
        Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(155);
        NomLabel.setLayoutY(45);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(NomLabel);
        Label nameLabel = new Label(cinema.getNom());
        nameLabel.setLayoutX(210);
        nameLabel.setLayoutY(45);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        card.getChildren().add(nameLabel);
        Label AdrsLabel = new Label("Adresse: ");
        AdrsLabel.setLayoutX(155);
        AdrsLabel.setLayoutY(85);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);
        Label adresseLabel = new Label(cinema.getAdresse());
        adresseLabel.setLayoutX(220);
        adresseLabel.setLayoutY(85);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);
        Line verticalLine = new Line();
        verticalLine.setStartX(290);
        verticalLine.setStartY(10);
        verticalLine.setEndX(290);
        verticalLine.setEndY(140);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);
        card.getChildren().add(verticalLine);
        Button moviesButton = new Button("Show Movies");
        moviesButton.setLayoutX(310);
        moviesButton.setLayoutY(35);
        moviesButton.getStyleClass().add("instert-btn");
        card.getChildren().add(moviesButton);
        Button planningButton = new Button("Show Planning");
        planningButton.setLayoutX(302);
        planningButton.setLayoutY(77);
        planningButton.getStyleClass().add("instert-btn");
        planningButton.setUserData(cinema);
        planningButton.setOnAction(event -> showPlanning(cinema));
        card.getChildren().add(planningButton);
        logoImageView.setOnMouseClicked(event -> geocodeAddress(cinema.getAdresse()));
        FontAwesomeIconView CommentIcon = new FontAwesomeIconView();
        CommentIcon.setGlyphName("COMMENTING");
        CommentIcon.setSize("2.5em");
        CommentIcon.setLayoutX(250);
        CommentIcon.setLayoutY(35);
        CommentIcon.setFill(Color.BLACK);
        card.getChildren().add(CommentIcon);
        CommentIcon.setOnMouseClicked(mouseEvent -> {
            cinemaId = cinema.getId_cinema();
            listCinemaClient.setOpacity(0.5);
            AnchorComments.setVisible(true);
            displayAllComments(cinemaId);
            System.out.println(cinemaId);
        });
        // Ajout du composant de notation (Rating)
        RatingCinemaService ratingService = new RatingCinemaService();
        Rating rating = new Rating();
        rating.setLayoutX(100);
        rating.setLayoutY(100);
        rating.setMax(5);
        // Obtenez le taux spécifique pour ce client et ce cinéma
        int specificRating = ratingService.getRatingForClientAndCinema(2, cinema.getId_cinema());
        // Si le taux spécifique est disponible, définissez le taux affiché
        if (specificRating >= 0) {
            rating.setRating(specificRating);
        }
        // Ajout d'un écouteur pour la notation
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> {
            Client client =(Client) AnchorComments.getScene().getWindow().getUserData();
            // Enregistrez le nouveau taux dans la base de données
            RatingCinema ratingCinema = new RatingCinema(cinema, client, newValue.intValue());
            ratingService.create(ratingCinema);
        });
        card.getChildren().add(rating);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }
    // Méthode pour créer les cartes des top 3 cinémas les mieux notés
    /**
     * Creates and positions top-rated cinema cards within an `AnchorPane`. Each card
     * displays the cinema's name, address, and logo, and is spaced apart by a fixed
     * distance. The position of the next card is updated after each card is added.
     * 
     * @param Anchortop3 `AnchorPane` where the top-rated cinema cards will be added.
     * 
     * 	- `Anchortop3`: Anchor pane where the cinema cards will be added.
     * 	- `topRatedCinemas`: List of top-rated cinemas to create cards for.
     * 	- `cardHeight`: Height of each cinema card.
     * 	- `cardSpacing`: Spacing between each cinema card.
     * 	- `currentY`: Position Y of the first cinema card.
     */
    public void createTopRatedCinemaCards(AnchorPane Anchortop3) {
        RatingCinemaService ratingCinemaService = new RatingCinemaService();
        List<Cinema> topRatedCinemas = ratingCinemaService.getTopRatedCinemas();
        double cardHeight = 100; // Hauteur de chaque carte
        double cardSpacing = 50; // Espacement entre chaque carte
        double currentY = 10; // Position Y de la première carte
        for (Cinema cinema : topRatedCinemas) {
            // Création de la carte pour le cinéma
            AnchorPane card = new AnchorPane();
            card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
            card.setPrefWidth(350);
            card.setPrefHeight(cardHeight);
            // Création et positionnement des éléments de la carte (nom, adresse, logo, etc.)
            ImageView logoImageView = new ImageView();
            logoImageView.setFitWidth(140);
            logoImageView.setFitHeight(100);
            logoImageView.setLayoutX(15);
            logoImageView.setLayoutY(15);
            logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
            try {
                String logoString = cinema.getLogo();
                Image logoImage = new Image(logoString);
                logoImageView.setImage(logoImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            card.getChildren().add(logoImageView);
            Label nomLabel = new Label("Nom: " + cinema.getNom());
            nomLabel.setLayoutX(200);
            nomLabel.setLayoutY(10);
            card.getChildren().add(nomLabel);
            Label adresseLabel = new Label("Adresse: " + cinema.getAdresse());
            adresseLabel.setLayoutX(200);
            adresseLabel.setLayoutY(40);
            card.getChildren().add(adresseLabel);
            // Vous pouvez ajouter d'autres éléments comme le logo, le bouton de réservation, etc.
            // Positionnement de la carte dans Anchortop3
            AnchorPane.setTopAnchor(card, currentY);
            AnchorPane.setLeftAnchor(card, 30.0);
            Anchortop3.getChildren().add(card);
            // Mise à jour de la position Y pour la prochaine carte
            currentY += cardHeight + cardSpacing;
        }
    }
    /**
     * Geocodes a given address by sending a GET request to the OpenStreetMap Nominatim
     * API and retrieving the lat and lon coordinates for the address. It then opens a
     * map dialog with the retrieved coordinates.
     * 
     * @param address address to be geocoded, which is sent as a query to the Nominatim
     * API to retrieve the latitude and longitude coordinates for the location.
     */
    private void geocodeAddress(String address) {
        new Thread(() -> {
            try {
                String apiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + address.replaceAll(" ", "+");
                URL url = new URL(apiUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                JSONArray jsonArray = new JSONArray(content.toString());
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    double lat = jsonObject.getDouble("lat");
                    double lon = jsonObject.getDouble("lon");
                    // Open the dialog with the map on the JavaFX Application Thread
                    Platform.runLater(() -> openMapDialog(lat, lon));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    /**
     * Creates a new dialog, loads a map into a WebView, updates the marker position using
     * JavaScript, and displays the dialog with a close button.
     * 
     * @param lat latitude coordinate of the map location, which is used to load the
     * appropriate map and place a marker at the corresponding position on the map.
     * 
     * @param lon longitude coordinate of the location where the map should be displayed,
     * which is used to load the appropriate map and place a marker at the specified position.
     */
    private void openMapDialog(double lat, double lon) {
        // Create a new dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Map");
        // Create a WebView and load the map
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/map.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);
        // Wait for the map to be loaded before placing the marker
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Call JavaScript function to update the marker
                webEngine.executeScript("updateMarker(" + lat + ", " + lon + ");");
            }
        });
        // Set the WebView as the dialog content
        dialog.getDialogPane().setContent(webView);
        dialog.getDialogPane().setPrefSize(600, 400); // Set dialog size (adjust as needed)
        // Add a close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        // Show the dialog
        dialog.showAndWait();
    }
    private List<VBox> seancesVBoxList = new ArrayList<>(); // Liste pour stocker les conteneurs de séances
    /**
     * Displays a planning page for a cinema, consisting of 7 days of the week, each day
     * represented by a label with the date and a button to display seances for that date.
     * 
     * @param cinema Cinema object that contains information about the cinema and its scheduling.
     * 
     * 	- `listCinemaClient`: A visible container for cinema client listings (set to `false`).
     * 	- `PlanningPane`: Visible pane displaying the planning schedule (set to `true`).
     * 	- `tilePane`: A container for displaying individual days of the week in a tile
     * format (created and added to `planningContent`).
     */
    public void showPlanning(Cinema cinema) {
        listCinemaClient.setVisible(false);
        PlanningPane.setVisible(true);
        VBox planningContent = new VBox();
        planningContent.setSpacing(10);
        tilePane = new TilePane();
        tilePane.setPrefColumns(7);
        tilePane.setPrefRows(1);
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDateOfWeek.plusDays(i);
            Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEE dd/MM")));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;");
            dayLabel.setOnMouseClicked(event -> displaySeancesForDate(date, cinema));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            tilePane.getChildren().add(dayLabel);
        }
        VBox.setMargin(tilePane, new Insets(0, 0, 0, 50));
        planningContent.getChildren().add(tilePane);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(200);
        planningContent.getChildren().add(separator);
        planningFlowPane.getChildren().clear();
        planningFlowPane.getChildren().add(planningContent);
        AnchorPane.setTopAnchor(planningContent, 50.0);
    }
    /**
     * Loads the planning for the current week (Sunday to Saturday) for a given cinema
     * using SeanceService.
     * 
     * @param startDate starting date of the current week for which the planning is being
     * loaded.
     * 
     * 	- LocalDate representing the start date of the current week
     * 	- Can be modified or manipulated within the function
     * 
     * Please provide the Java code for which you would like a summary.
     * 
     * @param cinema cinemas for which the seance is being planned.
     * 
     * 	- Cinema is an object representing a cinema with unknown details.
     * 
     * @returns a map containing the seating plan for the current week at a specific cinema.
     * 
     * The output is a map that contains key-value pairs, where the keys are `LocalDate`
     * objects representing the dates of the current week, and the values are lists of
     * `Seance` objects representing the seances scheduled for those dates at the
     * corresponding cinema.
     */
    private Map<LocalDate, List<Seance>> loadCurrentWeekPlanning(LocalDate startDate, Cinema cinema) {
        // Obtenir la date de fin de la semaine courante (dimanche)
        LocalDate endDate = startDate.plusDays(6);
        // Utiliser SeanceService pour obtenir les séances programmées pour cette semaine pour cette cinéma
        SeanceService seanceService = new SeanceService();
        return seanceService.getSeancesByDateRangeAndCinema(startDate, endDate, cinema);
    }
    /**
     * Loads the current week's planning data, displays it in a VBox, and adds an event
     * listener to display more detailed seance information when a date is clicked.
     * 
     * @param date LocalDate for which to display the cinema seances, and it is used to
     * load the relevant planning data from the database or API.
     * 
     * 	- `LocalDate date`: represents a specific date in the format `YYYY-MM-DD`.
     * 	- ` cinema`: represents the cinema for which the seating plan is being generated.
     * 
     * @param cinema cinema for which the seances are being displayed, and is used to
     * load the relevant planning data into the function.
     * 
     * 	- `cinema`: A `Cinema` object representing the cinema for which the seances are
     * being displayed. Its main properties include the cinema's name and address.
     */
    private void displaySeancesForDate(LocalDate date, Cinema cinema) {
        // Charger les séances pour la date spécifiée
        Map<LocalDate, List<Seance>> weekSeancesMap = loadCurrentWeekPlanning(date, cinema);
        List<Seance> seancesForDate = weekSeancesMap.getOrDefault(date, Collections.emptyList());
        // Créer un VBox pour contenir les éléments du calendrier
        VBox planningContent = new VBox();
        planningContent.setSpacing(10);
        // Ajouter le calendrier au VBox
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(7);
        tilePane.setPrefRows(1);
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDateOfWeek.plusDays(i);
            Label dayLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("EEE dd/MM")));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;");
            dayLabel.setOnMouseClicked(event -> displaySeancesForDate(currentDate, cinema));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            tilePane.getChildren().add(dayLabel);
        }
        VBox.setMargin(tilePane, new Insets(0, 0, 0, 50));
        planningContent.getChildren().add(tilePane);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(200);
        planningContent.getChildren().add(separator);
        // Vérifier si des séances sont disponibles pour la date spécifiée
        if (!seancesForDate.isEmpty()) {
            // Créer un VBox pour contenir les cartes de séance correspondant à la date sélectionnée
            VBox seancesForDateVBox = new VBox();
            seancesForDateVBox.setSpacing(10);
            // Ajouter les cartes de séance au VBox
            for (Seance seance : seancesForDate) {
                StackPane seanceCard = createSeanceCard(seance);
                seancesForDateVBox.getChildren().add(seanceCard);
            }
            // Ajouter le VBox des séances au conteneur principal
            planningContent.getChildren().add(seancesForDateVBox);
        } else {
            // Afficher un message lorsque aucune séance n'est disponible pour la date spécifiée
            Label noSeanceLabel = new Label("Aucune séance prévue pour cette journée.");
            noSeanceLabel.setStyle("-fx-font-size: 16px;");
            planningContent.getChildren().add(noSeanceLabel); // Ajouter le message au conteneur principal
        }
        // Effacer tout contenu précédent et ajouter le contenu actuel au conteneur principal
        planningFlowPane.getChildren().clear();
        planningFlowPane.getChildren().add(planningContent);
    }
    /**
     * Generates a stack pane containing a card with information about a seance, including
     * the film's name, cinema hall, screening time, and price.
     * 
     * @param seance Seance object that contains information about the film, salle, and
     * time of the screening, which is used to populate the card with relevant labels.
     * 
     * 	- `seance.getFilmcinema()`: Returns an instance of `Filmcinema` containing
     * information about the film showing at the seance.
     * 	- `seance.getNom_salle()`: Returns the name of the hall where the seance is taking
     * place.
     * 	- `seance.getHD()` and `seance.getHF()`: Return the starting time and ending time
     * of the seance, respectively.
     * 	- `seance.getPrix()`: Returns the price of the seance.
     * 
     * @returns a stack pane containing a HBox with an ImageView and three Labels.
     * 
     * 	- `cardContainer`: A StackPane that contains all the elements that make up the
     * seance card.
     * 	- `filmImageView`: An ImageView that displays an image of the film being shown
     * in the seance.
     * 	- `labelsContainer`: A VBox that contains four Labels displaying information about
     * the seance, including the film name, salle name, time, and price.
     * 	- `filmNameLabel`, `salleNameLabel`, `timeLabel`, and `priceLabel`: The labels
     * that are contained within the `labelsContainer`. Each label displays a piece of
     * information related to the seance.
     */
    private StackPane createSeanceCard(Seance seance) {
        StackPane cardContainer = new StackPane();
        cardContainer.setPrefWidth(600);
        cardContainer.setPrefHeight(100);
        cardContainer.setStyle("-fx-padding: 10 0 0 150;");
        HBox card = new HBox();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        ImageView filmImageView = new ImageView();
        filmImageView.setFitWidth(140);
        filmImageView.setFitHeight(100);
        filmImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
        try {
            String logoString = seance.getFilmcinema().getId_film().getImage();
            Image logoImage = new Image(logoString);
            filmImageView.setImage(logoImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(filmImageView);
        VBox labelsContainer = new VBox();
        labelsContainer.setSpacing(5);
        labelsContainer.setPadding(new Insets(10));
        Label filmNameLabel = new Label("Film: " + seance.getFilmcinema().getId_film().getNom());
        filmNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        Label salleNameLabel = new Label("Salle: " + seance.getNom_salle());
        salleNameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        Label timeLabel = new Label("Heure: " + seance.getHD() + " - " + seance.getHF());
        timeLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        Label priceLabel = new Label("Prix: " + seance.getPrix());
        priceLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px;");
        labelsContainer.getChildren().addAll(filmNameLabel, salleNameLabel, timeLabel, priceLabel);
        card.getChildren().add(labelsContainer);
        cardContainer.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER_RIGHT); // Positionne la carte à droite dans le conteneur StackPane
        return cardContainer;
    }
    /**
     * Sets up the user interface and connects it to a `CinemaService` for movie data
     * retrieval. It listens for text changes in a search bar, queries the service for
     * relevant movies, and displays them on a pane. Additionally, it sorts and displays
     * top-rated movies based on a predetermined criterion.
     */
    public void initialize() {
        CinemaService cinemaService = new CinemaService();
        l1 = cinemaService.read();
        searchbar1.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Cinema> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            cinemaFlowPane.getChildren().clear();
            createfilmCards(produitsRecherches);
        });
        cinemaFlowPane.getChildren().clear();
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
        listCinemaClient.setVisible(true);
        PlanningPane.setVisible(false);
        tricomboBox.getItems().addAll("nom");
        tricomboBox.setValue("");
        tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            cinemaFlowPane.getChildren().clear();
            List<Cinema> filmList = new CinemaService().sort(t1);
            for (Cinema film : filmList) {
                cinemaFlowPane.getChildren().add(createCinemaCard(film));
            }
        });
        createTopRatedCinemaCards(Anchortop3);
    }
    /**
     * Creates film cards for a list of cinemas and adds them to a pane containing the cinemas.
     * 
     * @param Cinemas list of cinemas whose film cards will be created and displayed by
     * the `createfilmCards()` method.
     * 
     * 	- `Cinemas`: A list of Cinema objects representing various cinemas.
     * 	- Each Cinema object contains information about the cinema's name, address, and
     * film schedule.
     */
    private void createfilmCards(List<Cinema> Cinemas) {
        for (Cinema cinema : Cinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }
    }
    /**
     * Retrieves a list of cinemas through a call to the ` CinemaService`. It then returns
     * the list of cinemas.
     * 
     * @returns a list of `Cinema` objects retrieved from the Cinema Service.
     */
    private List<Cinema> getAllCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        return cinemas;
    }
    /**
     * Updates the visibility of the `FilterAnchor` pane and adds two VBoxes containing
     * check boxes for addresses and names to the pane.
     * 
     * @param event Anchor Button's event that triggered the filtration process.
     * 
     * 	- `event`: An `ActionEvent` object representing the triggered action.
     * 	- `listCinemaClient`: A `VBox` container for displaying the cinema client list.
     * 	- `FilterAnchor`: A `Region` component for hosting the filtering controls.
     */
    @FXML
    void filtrer(ActionEvent event) {
        listCinemaClient.setOpacity(0.5);
        FilterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        namesCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> addresses = getCinemaAddresses();
        // Récupérer les statuts uniques depuis la base de données
        List<String> names = getCinemaNames();
        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Adresse");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : addresses) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);
        // Créer des VBox pour les statuts
        VBox namesCheckBoxesVBox = new VBox();
        Label statusLabel = new Label("Names");
        statusLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        namesCheckBoxesVBox.getChildren().add(statusLabel);
        for (String name : names) {
            CheckBox checkBox = new CheckBox(name);
            namesCheckBoxesVBox.getChildren().add(checkBox);
            namesCheckBoxes.add(checkBox);
        }
        namesCheckBoxesVBox.setLayoutX(25);
        namesCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox, namesCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }
    /**
     * Filters a list of cinemas based on selected addresses and/or names, and displays
     * the filtered list in a flow pane.
     * 
     * @param event occurrence of an action event, triggering the function to execute and
     * filter the cinemas based on the selected addresses and/or names.
     * 
     * 	- `listCinemaClient`: A reference to an observable list of cinemas.
     * 	- `FilterAnchor`: A reference to a component that displays a filter option.
     * 	- `getSelectedAddresses()` and `getSelectedNames()`: Methods that return lists
     * of selected addresses and names, respectively.
     */
    @FXML
    void filtrercinema(ActionEvent event) {
        listCinemaClient.setOpacity(1);
        FilterAnchor.setVisible(false);
        // Récupérer les adresses sélectionnées
        List<String> selectedAddresses = getSelectedAddresses();
        // Récupérer les noms sélectionnés
        List<String> selectedNames = getSelectedNames();
        // Filtrer les cinémas en fonction des adresses et/ou des noms sélectionnés
        List<Cinema> filteredCinemas = l1.stream()
                .filter(cinema -> selectedAddresses.isEmpty() || selectedAddresses.contains(cinema.getAdresse()))
                .filter(cinema -> selectedNames.isEmpty() || selectedNames.contains(cinema.getNom()))
                .collect(Collectors.toList());
        // Afficher les cinémas filtrés
        cinemaFlowPane.getChildren().clear();
        createfilmCards(filteredCinemas);
    }
    /**
     * Streams, filters, and collects the selected addresses from the `addressCheckBoxes`
     * array, returning a list of strings representing the selected addresses.
     * 
     * @returns a list of selected addresses.
     * 
     * 	- The list contains only the strings of selected addresses.
     * 	- Each string represents a single address selected in the AnchorPane of filtering.
     * 	- The list is generated by streaming the checked CheckBoxes, applying the `filter()`
     * method to select only the checked ones, and then mapping the text property of each
     * checked CheckBox to its corresponding string value.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * In Java code returns a list of selected names from an `AnchorPane` of filtering
     * by streaming, filtering, and mapping the values of `CheckBox` objects.
     * 
     * @returns a list of selected names from an AnchorPane of filtering controls.
     * 
     * 	- The list contains only strings representing the selected names from the `namesCheckBoxes`.
     * 	- The elements in the list are the texts of the selected checkboxes.
     */
    private List<String> getSelectedNames() {
        // Récupérer les noms sélectionnés dans l'AnchorPane de filtrage
        return namesCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves all cinemas from a database, extracts unique addresses from them using
     * Stream API, and returns a list of those addresses.
     * 
     * @returns a list of unique cinema addresses retrieved from a database.
     * 
     * 	- The output is a list of Strings, representing the unique addresses of cinemas.
     * 	- The list contains the addresses of all cinemas retrieved from the database
     * through the `getAllCinemas()` function.
     * 	- The addresses are obtained by calling the `getAdresse()` method on each `Cinema`
     * object in the list and then applying a `distinct()` operation to eliminate duplicates
     * using the `Collectors.toList()` method.
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();
        // Extraire les adresses uniques des cinémas
        return cinemas.stream()
                .map(Cinema::getAdresse)
                .distinct()
                .collect(Collectors.toList());
    }
    /**
     * Retrieves a list of unique cinema names from a database by mapping and collecting
     * the `nom` attributes of each `Cinema` object in the list.
     * 
     * @returns a list of unique cinema names retrieved from the database.
     * 
     * 	- The output is a list of unique strings, representing the names of cinemas.
     * 	- The list is generated by streaming the `cinemas` list, applying the `map` method
     * to extract the names, and then using the `distinct` method to remove duplicates.
     * 	- Finally, the list is collected into a new list using the `collect` method.
     */
    public List<String> getCinemaNames() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();
        // Extraire les noms uniques des cinémas
        return cinemas.stream()
                .map(Cinema::getNom)
                .distinct()
                .collect(Collectors.toList());
    }
    /**
     * Loads an fxml file and displays a stage with the content from the loaded fxml.
     * 
     * @param event Event Object that triggered the function, providing information about
     * the event that occurred, such as the source of the event and the type of event.
     * 
     * Event type: The type of event that triggered the function execution, which could
     * be any of the possible types recognized by the application.
     */
    @FXML
    void afficherEventsClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Event Client");
        stage.show();
        currentStage.close();
    }
    /**
     * Displays a FXML user interface for managing movies using an FXMLLoader and a Stage
     * object.
     * 
     * @param event an action event that triggered the function execution, providing the
     * necessary context for the code to perform its intended task.
     * 
     * 	- `event` is an `ActionEvent`, indicating that the function was triggered by user
     * action.
     */
    @FXML
    void afficherMoviesClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Movie Manegement");
        stage.show();
        currentStage.close();
    }
    /**
     * Loads an FXML file to display a product client interface, creates a new stage for
     * the interface, and closes the previous stage.
     * 
     * @param event event that triggered the function, specifically the button click event
     * that activates the function to display the product client interface.
     * 
     * 	- `event`: An `ActionEvent` object representing a user action that triggered the
     * function execution.
     */
    @FXML
    void afficherProduitsClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product Client");
        stage.show();
        currentStage.close();
    }
    /**
     * Loads a FXML file "SeriesClient.fxml" and displays it on a new stage, replacing
     * the current stage.
     * 
     * @param event event that triggered the execution of the `afficherSeriesClient()`
     * method, specifically the button click event on the client series view.
     * 
     * 	- Event type: `ActionEvent`
     * 	- Source object: (`Node`) reference to the element that triggered the event
     */
    @FXML
    void afficherSeriesClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Serie Client");
        stage.show();
        currentStage.close();
    }
    /**
     * Allows users to add comments to a cinema. When a user clicks on the "Add Comment"
     * button, the function takes the user's comment and analyzes its sentiment using a
     * sentiment analysis controller. If the comment is not empty, the function creates
     * a new `CommentaireCinema` object with the cinema ID, user ID, message, and sentiment
     * result, and saves it to the database using the `CommentaireCinamaService`.
     */
    @FXML
    void addCommentaire() {
        String message = txtAreaComments.getText();
        if (message.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        } else {
            SentimentAnalysisController sentimentAnalysisController = new SentimentAnalysisController();
            String sentimentResult = sentimentAnalysisController.analyzeSentiment(message);
            System.out.println(cinemaId + " " + new CinemaService().getCinema(cinemaId));
            CommentaireCinema commentaire = new CommentaireCinema(new CinemaService().getCinema(cinemaId), (Client) new UserService().getUserById(2), message, sentimentResult);
            System.out.println(commentaire + " " + new UserService().getUserById(2));
            CommentaireCinemaService commentaireCinemaService = new CommentaireCinemaService();
            commentaireCinemaService.create(commentaire);
            txtAreaComments.clear();
        }
    }
    /**
     * Adds a comment to a cinema and displays all comments for that cinema when the event
     * is triggered.
     * 
     * @param event user's click on the "Add Comment" button, which triggers the execution
     * of the `addCommentaire()` method and the display of all comments for the specified
     * `cinemaId`.
     */
    @FXML
    void AddComment(MouseEvent event) {
        addCommentaire();
        displayAllComments(cinemaId);
    }
    /**
     * Retrieves all comments related to a specific cinema, using a service to read the
     * comments and then filtering them based on the cinema ID.
     * 
     * @param cinemaId Id of the cinema for which the comments are to be retrieved.
     * 
     * @returns a list of `CommentaireCinema` objects for the specified cinema ID.
     * 
     * 	- The output is a list of `CommentaireCinema` objects, representing all comments
     * for a given cinema ID.
     * 	- Each comment is associated with a cinema ID and a list of other attributes such
     * as text, author, date, etc.
     * 	- The list of comments is obtained through a service call to the CommentaireCinemaService
     * class.
     */
    private List<CommentaireCinema> getAllComment(int cinemaId) {
        CommentaireCinemaService commentaireCinemaService = new CommentaireCinemaService();
        List<CommentaireCinema> allComments = commentaireCinemaService.read();
        List<CommentaireCinema> cinemaComments = new ArrayList<>();
        for (CommentaireCinema comment : allComments) {
            if (comment.getCinema().getId_cinema() == cinemaId) {
                cinemaComments.add(comment);
            }
        }
        return cinemaComments;
    }
    /**
     * Creates a container for displaying a user's comment and image, with a transparent
     * background and padding. It also sets the image view's position to center the image
     * within the circle.
     * 
     * @param commentaire CommentaireCinema object passed to the function, containing
     * information about the user's comment and image.
     * 
     * 	- `client`: contains information about the user who made the comment
     * 	+ `getPhoto_de_profil()`: the URL of the user's profile picture
     * 	- `getCommentaire()`: the actual comment made by the user
     * 
     * Both properties are used to generate the image and text display for the comment.
     * 
     * @returns a `HBox` container containing an image and text related to a comment.
     * 
     * 	- `HBox contentContainer`: This is the container that holds all the elements
     * related to a comment, including the image, username, and comment text. It has a
     * prefheight of 50 pixels and a style of `-fx-background-color: transparent;
     * -fx-padding: 10px`.
     * 	- `ImageBox imageBox`: This is the box that holds the image of the user who made
     * the comment. It has no style defined.
     * 	- `ImageView userImage`: This is the image view that displays the image of the
     * user. It has a fit width and height of 50 pixels each, and is centered in the image
     * box using `setTranslateX` and `setTranslateY`.
     * 	- `Group imageGroup`: This is the group that holds both the image and the image
     * view. It has no style defined.
     * 	- `Text userName`: This is the text that displays the user's name. It has a style
     * of `-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;`.
     * 	- `Text commentText`: This is the text that displays the comment made by the user.
     * It has a style of `-fx-font-family: 'Arial'; -fx-max-width: 300 ;`.
     * 	- `VBox textBox`: This is the box that holds both the user name and comment text.
     * It has no style defined.
     * 	- `CardContainer cardContainer`: This is the container that holds all the elements
     * related to a single comment, including the image, username, and comment text. It
     * has a style of `-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius:
     * 8px; -fx-border-color: #000; -fx-background-radius: 8px;`.
     */
    private HBox addCommentToView(CommentaireCinema commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        String imageUrl = commentaire.getClient().getPhoto_de_profil();
        Image userImage;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(getClass().getResourceAsStream("/Logo.png"));
        }
        ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);
        // Ajouter l'image au cercle
        Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);
        // Création de la boîte pour l'image et la bordure du cercle
        HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);
        // Création du conteneur pour la carte du commentaire
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");
        // Nom de l'utilisateur
        Text userName = new Text(commentaire.getClient().getFirstName() + " " + commentaire.getClient().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");
        // Commentaire
        Text commentText = new Text(commentaire.getCommentaire());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique
        // Création de la boîte pour le texte du commentaire
        VBox textBox = new VBox();
        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);
        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);
        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);
        // Ajouter le conteneur principal au ScrollPane
        ScrollPaneComments.setContent(contentContainer);
        return contentContainer;
    }
    /**
     * Displays all comments associated with a particular cinema ID in a scroll pane.
     * 
     * @param cinemaId identity of the cinema for which all comments are to be displayed.
     */
    private void displayAllComments(int cinemaId) {
        List<CommentaireCinema> comments = getAllComment(cinemaId);
        VBox allCommentsContainer = new VBox();
        for (CommentaireCinema comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }
        ScrollPaneComments.setContent(allCommentsContainer);
    }
    /**
     * Sets the opacity of a component to 1, makes an component invisible and another visible.
     * 
     * @param event mouse event that triggered the execution of the `closeCommets()` method.
     * 
     * Event type: MouseEvent
     * Target element: AnchorComments
     * Current state: Visible
     */
    @FXML
    void closeCommets(MouseEvent event) {
        listCinemaClient.setOpacity(1);
        AnchorComments.setVisible(false);
        listCinemaClient.setVisible(true);
    }
}

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

    @FXML
    void Planninggclose(ActionEvent event) {
        PlanningPane.setVisible(false);
        listCinemaClient.setOpacity(1);
        listCinemaClient.setVisible(true);
    }

    @FXML
    void showListCinema(ActionEvent event) {
        cinemaFlowPane.getChildren().clear();
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
        listCinemaClient.setVisible(true);
        PlanningPane.setVisible(false);
    }

    private HashSet<Cinema> loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
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


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

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
        cardContainer.setOnMouseClicked(event -> geocodeAddress(cinema.getAdresse()));

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
        });

        // Ajout du composant de notation (Rating)
        RatingCinemaService ratingService = new RatingCinemaService();
        Rating rating = new Rating();
        rating.setLayoutX(100);
        rating.setLayoutY(100);
        rating.setMax(5);

        // Obtenez la note moyenne depuis le service de notation
        double averageRating = ratingService.getAverageRating(cinema.getId_cinema());

        // Si la note moyenne est disponible, définissez la note affichée
        if (averageRating > 0) {
            rating.setRating(averageRating);
        }

        // Ajout d'un écouteur pour la notation
        rating.ratingProperty().addListener((observable, oldValue, newValue) -> {
            int userId = 1; // Supposons que l'ID de l'utilisateur est 1 (variable fixe)
            RatingCinemaService ratingCinemaService = new RatingCinemaService(); // Instanciation du service
            UserService userService = new UserService(); // Instanciation du service UserService
            Client client = (Client) userService.getUserById(userId); // Récupérer le client par son ID à partir du service UserService
            RatingCinema ratingCinema = new RatingCinema(cinema, client, newValue.intValue()); // Création de l'objet RatingCinema avec les valeurs appropriées
            ratingCinemaService.create(ratingCinema); // Enregistrement de la note dans la base de données
        });

        card.getChildren().add(rating);

        cardContainer.getChildren().add(card);
        return cardContainer;
    }

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

    public void showPlanning(Cinema cinema) {
        listCinemaClient.setVisible(false); // Rendre l'AnchorPane listCinemaClient invisible
        PlanningPane.setVisible(true); // Rendre l'AnchorPane PlanningPane visible

        // Créer un VBox pour contenir à la fois le TilePane du calendrier et les séances correspondantes
        VBox planningContent = new VBox();
        planningContent.setSpacing(10);

        // Créer un TilePane pour afficher les jours de la semaine
        tilePane = new TilePane();
        tilePane.setPrefColumns(7);
        tilePane.setPrefRows(1);
        tilePane.setHgap(5);
        tilePane.setVgap(5);

        // Ajouter chaque jour de la semaine courante au TilePane
        LocalDate startDateOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDateOfWeek.plusDays(i);
            Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEE dd/MM")));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;"); // Rouge avec texte blanc
            dayLabel.setOnMouseClicked(event -> displaySeancesForDate(date, cinema));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14)); // Changer la police en Arial, en gras, taille 14
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

    private Map<LocalDate, List<Seance>> loadCurrentWeekPlanning(LocalDate startDate, Cinema cinema) {
        // Obtenir la date de fin de la semaine courante (dimanche)
        LocalDate endDate = startDate.plusDays(6);

        // Utiliser SeanceService pour obtenir les séances programmées pour cette semaine pour cette cinéma
        SeanceService seanceService = new SeanceService();
        return seanceService.getSeancesByDateRangeAndCinema(startDate, endDate, cinema);
    }

    private void displaySeancesForDate(LocalDate date, Cinema cinema) {
        // Charger les séances pour la date spécifiée
        Map<LocalDate, List<Seance>> weekSeancesMap = loadCurrentWeekPlanning(date, cinema);
        List<Seance> seancesForDate = weekSeancesMap.getOrDefault(date, Collections.emptyList());

        // Vérifier que la liste de séances n'est pas vide avant de la traiter
        if (!seancesForDate.isEmpty()) {
            // Créer un VBox pour contenir les cartes de séance correspondant à la date sélectionnée
            VBox seancesForDateVBox = new VBox();
            seancesForDateVBox.setSpacing(10);

            // Ajouter les cartes de séance au VBox
            for (Seance seance : seancesForDate) {
                StackPane seanceCard = createSeanceCard(seance);
                seancesForDateVBox.getChildren().add(seanceCard);
            }

            // Nettoyer le deuxième enfant de PlanningPane s'il s'agit d'un VBox
            if (PlanningPane.getChildren().size() > 1 && PlanningPane.getChildren().get(1) instanceof VBox existingSeancesVBox) {
                existingSeancesVBox.getChildren().clear(); // Nettoyer le VBox existant
            }

            planningFlowPane.getChildren().add(seancesForDateVBox);
            AnchorPane.setTopAnchor(seancesForDateVBox, 30.0);
        } else {

            System.out.println("Aucune séance disponible pour la date spécifiée.");
        }
    }

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

    }


    private void createfilmCards(List<Cinema> Cinemas) {
        for (Cinema cinema : Cinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);


        }

    }

    private List<Cinema> getAllCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        return cinemas;
    }

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

    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private List<String> getSelectedNames() {
        // Récupérer les noms sélectionnés dans l'AnchorPane de filtrage
        return namesCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();

        // Extraire les adresses uniques des cinémas
        return cinemas.stream()
                .map(Cinema::getAdresse)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getCinemaNames() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();

        // Extraire les noms uniques des cinémas
        return cinemas.stream()
                .map(Cinema::getNom)
                .distinct()
                .collect(Collectors.toList());
    }

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
            CommentaireCinema commentaire = new CommentaireCinema(new CinemaService().getCinema(cinemaId), (Client) new UserService().getUserById(3), message, sentimentResult);
            System.out.println(commentaire + " " + new UserService().getUserById(3));
            CommentaireCinemaService commentaireCinemaService = new CommentaireCinemaService();
            commentaireCinemaService.create(commentaire);
            txtAreaComments.clear();
        }
    }

    @FXML
    void AddComment(MouseEvent event) {
        addCommentaire();
        displayAllComments(cinemaId);
    }

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


    private void displayAllComments(int cinemaId) {
        List<CommentaireCinema> comments = getAllComment(cinemaId);
        VBox allCommentsContainer = new VBox();

        for (CommentaireCinema comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        ScrollPaneComments.setContent(allCommentsContainer);
    }

    @FXML
    void closeCommets(MouseEvent event) {
        listCinemaClient.setOpacity(1);
        AnchorComments.setVisible(false);
        listCinemaClient.setVisible(true);

    }


}

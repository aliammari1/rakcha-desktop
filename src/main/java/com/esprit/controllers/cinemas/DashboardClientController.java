package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.RatingCinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.RatingCinemaService;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.services.users.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.ByteArrayInputStream;

import java.sql.Blob;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.controlsfx.control.Rating;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class DashboardClientController {
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
        moviesButton.getStyleClass().add("movies-btn");
        card.getChildren().add(moviesButton);

        Button planningButton = new Button("Show Planning");
        planningButton.setLayoutX(302);
        planningButton.setLayoutY(77);
        planningButton.getStyleClass().add("movies-btn");
        planningButton.setUserData(cinema);
        planningButton.setOnAction(this::showPlanning);
        card.getChildren().addAll(planningButton);

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
            Client client = userService.getClientById(userId); // Récupérer le client par son ID à partir du service UserService
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



    private void showPlanning(ActionEvent event) {
        planningFlowPane.getChildren().clear();
        listCinemaClient.setVisible(false);
        PlanningPane.setVisible(true);

        // Récupérer le bouton "Show Planning" qui a déclenché l'événement
        Button planningButton = (Button) event.getSource();

        // Récupérer le cinéma à partir de la propriété userData du bouton
        Cinema cinema = (Cinema) planningButton.getUserData();

        if (cinema == null) {
            showAlert("Cinéma non trouvé.");
            return;
        }

        // Charger et afficher les séances de la semaine courante pour ce cinéma
        Map<LocalDate, List<Seance>> weekSeancesMap = loadCurrentWeekPlanning(cinema);
        if (weekSeancesMap.isEmpty()) {
            showAlert("Aucune séance programmée pour la semaine courante pour ce cinéma.");
        } else {
            // Afficher les séances de chaque jour
            displayWeekSeances(weekSeancesMap);
        }
    }


    private Map<LocalDate, List<Seance>> loadCurrentWeekPlanning(Cinema cinema) {
        // Obtenir la date de début de la semaine courante (lundi)
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        // Obtenir la date de fin de la semaine courante (dimanche)
        LocalDate endDate = startDate.plusDays(6);

        // Utiliser SeanceService pour obtenir les séances programmées pour cette semaine pour cette cinéma
        SeanceService seanceService = new SeanceService();
        return seanceService.getSeancesByDateRangeAndCinema(startDate, endDate, cinema);
    }

    private void displayWeekSeances(Map<LocalDate, List<Seance>> weekSeancesMap) {
        // Parcourir chaque jour de la semaine
        for (LocalDate date : weekSeancesMap.keySet()) {
            List<Seance> seancesForDate = weekSeancesMap.getOrDefault(date, Collections.emptyList());

            // Créer un VBox pour chaque jour
            VBox dayBox = new VBox();
            dayBox.getChildren().add(new Label("Séances pour " + date));

            // Ajouter les cartes de séance au VBox du jour
            for (Seance seance : seancesForDate) {
                HBox carteSeance = createSeanceCard(seance);
                dayBox.getChildren().add(carteSeance);
            }

            // Ajouter le VBox du jour au parent (par exemple, un autre VBox ou un ScrollPane)
            planningFlowPane.getChildren().add(dayBox);
        }
    }

    private Cinema getCinemaFromPlanningButton(Button planningButton) {
        // Récupérer le parent du bouton (qui est le AnchorPane)
        AnchorPane card = (AnchorPane) planningButton.getParent();

        // Récupérer le parent de l'AnchorPane (qui est le HBox cardContainer)
        HBox cardContainer = (HBox) card.getParent();

        // Utiliser la position du cardContainer dans cinemaFlowPane pour trouver la cinéma associée
        int cinemaIndex = cinemaFlowPane.getChildren().indexOf(cardContainer);
        if (cinemaIndex != -1) {
            // Récupérer la cinéma correspondante dans la liste des cinémas acceptés
            List<Cinema> acceptedCinemasList = new ArrayList<>(loadAcceptedCinemas());
            if (cinemaIndex < acceptedCinemasList.size()) {
                return acceptedCinemasList.get(cinemaIndex);
            }
        }
        return null; // Aucune cinéma trouvée
    }






    private HBox createSeanceCard(Seance seance) {
        HBox card = new HBox();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;  ");
        card.setPrefWidth(400);
        card.setPrefHeight(100);

        Label filmLabel = new Label("Film: " + (seance.getFilmcinema().getId_film() != null ? seance.getFilmcinema().getId_film().getNom() : "Unknown"));
        Label salleLabel = new Label("Salle: " + (seance.getSalle() != null ? seance.getSalle().getNom_salle() : "Unknown"));
        Label timeLabel = new Label("Heure: " + seance.getHD() + " - " + seance.getHF());
        Label priceLabel = new Label("Prix: " + seance.getPrix());

        card.getChildren().addAll(filmLabel, salleLabel, timeLabel, priceLabel);
        return card;
    }
    private List<Cinema> l1 = new ArrayList<>();
    public void initialize() {
        CinemaService cinemaService = new CinemaService();
        l1 = cinemaService.read();

        searchbar1.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Cinema> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            cinemaFlowPane.getChildren().clear();
            createfilmCards(produitsRecherches);
});

    }

   private void createfilmCards(List<Cinema> Cinemas) {
        for (Cinema cinema : Cinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);


}

}

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

    private List<Cinema> getAllCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        return cinemas;
    }

    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> namesCheckBoxes = new ArrayList<>();

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




}

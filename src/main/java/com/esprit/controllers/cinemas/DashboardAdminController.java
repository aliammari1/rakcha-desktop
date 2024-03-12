package com.esprit.controllers.cinemas;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DashboardAdminController {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();

    @FXML
    private AnchorPane cinemasList;
    @FXML
    private TableColumn<Cinema, Void> colAction;
    @FXML
    private TableColumn<Cinema, String> colAdresse;
    @FXML
    private TableColumn<Cinema, String> colCinema;
    @FXML
    private TableColumn<Cinema, ImageView> colLogo;
    @FXML
    private TableColumn<Cinema, String> colResponsable;
    @FXML
    private TableColumn<Cinema, String> colStatut;
    @FXML
    private TableView<Cinema> listCinema;
    @FXML
    private TextField tfSearch;
    @FXML
    private AnchorPane FilterAnchor;


    @FXML
    void afficherCinemas(ActionEvent event) {
        cinemasList.setVisible(true);
        // Appelée lorsque le fichier FXML est chargé
        // Configurer les cellules des colonnes pour afficher les données
        colCinema.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));

        // Configurer la cellule de la colonne Logo pour afficher l'image
        colLogo.setCellValueFactory(cellData -> {
            Cinema cinema = cellData.getValue();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(50); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences

            String logo = cinema.getLogo();
            if (!logo.isEmpty()) {
                Image image = new Image(logo);
                imageView.setImage(image);
            } else {
                // Afficher une image par défaut si le logo est null
                Image defaultImage = new Image(getClass().getResourceAsStream("/Logo.png"));
                imageView.setImage(defaultImage);
            }

            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        colStatut.setCellValueFactory(new PropertyValueFactory<>("Statut"));

        // Configurer la cellule de la colonne Action
        colAction.setCellFactory(new Callback<TableColumn<Cinema, Void>, TableCell<Cinema, Void>>() {
            @Override
            public TableCell<Cinema, Void> call(TableColumn<Cinema, Void> param) {
                return new TableCell<Cinema, Void>() {
                    private final Button acceptButton = new Button("Accepter");
                    private final Button refuseButton = new Button("Refuser");
                    private final Button showMoviesButton = new Button("Show Movies");

                    {
                        acceptButton.getStyleClass().add("delete-btn");
                        acceptButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            // Mettre à jour le statut du cinéma en "Acceptée"
                            cinema.setStatut("Acceptée");
                            // Mettre à jour le statut dans la base de données
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.update(cinema);
                            // Rafraîchir la TableView pour refléter les changements
                            getTableView().refresh();
                        });

                        refuseButton.getStyleClass().add("delete-btn");
                        refuseButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.delete(cinema);
                            getTableView().getItems().remove(cinema);
                        });
                        showMoviesButton.getStyleClass().add("delete-btn");
                        showMoviesButton.setOnAction(event -> {

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Récupérer le cinéma associé à cette ligne
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            if (cinema.getStatut().equals("Acceptée")) {
                                // Afficher le bouton "Show Movies" si le statut est "Acceptée"
                                setGraphic(showMoviesButton);
                            } else {
                                // Afficher les boutons "Accepter" et "Refuser" si le statut est "En attente"
                                setGraphic(new HBox(acceptButton, refuseButton));
                            }
                        }
                    }
                };
            }
        });


        loadCinemas();
    }

    private void loadCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        ObservableList<Cinema> cinemaObservableList = FXCollections.observableArrayList(cinemas);

        listCinema.setItems(cinemaObservableList);
    }

    private List<Cinema> getAllCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        return cinemas;
    }

    @FXML
    public void initialize() {
        // Ajouter un écouteur de changement pour le champ de recherche
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filtrer la liste des cinémas en fonction du nouveau texte saisi dans le champ de recherche
            filterCinemas(newValue.trim());
        });

        // Charger tous les cinémas initialement
        loadCinemas();
    }

    private void filterCinemas(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Cinema> filteredList = FXCollections.observableArrayList();
            for (Cinema cinema : listCinema.getItems()) {
                if (cinema.getNom().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(cinema);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            listCinema.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            loadCinemas();
        }
    }

    @FXML
    void filtrer(ActionEvent event) {
        cinemasList.setOpacity(0.5);
        FilterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> addresses = getCinemaAddresses();
        // Récupérer les statuts uniques depuis la base de données
        List<String> statuses = getCinemaStatuses();

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
        VBox statusCheckBoxesVBox = new VBox();
        Label statusLabel = new Label("Statut");
        statusLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        statusCheckBoxesVBox.getChildren().add(statusLabel);
        for (String status : statuses) {
            CheckBox checkBox = new CheckBox(status);
            statusCheckBoxesVBox.getChildren().add(checkBox);
            statusCheckBoxes.add(checkBox);
        }
        statusCheckBoxesVBox.setLayoutX(25);
        statusCheckBoxesVBox.setLayoutY(120);

        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox, statusCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }


    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();

        // Extraire les adresses uniques des cinémas
        List<String> addresses = cinemas.stream()
                .map(Cinema::getAdresse)
                .distinct()
                .collect(Collectors.toList());

        return addresses;
    }

    public List<String> getCinemaStatuses() {
        // Créer une liste de statuts pré-définis
        List<String> statuses = new ArrayList<>();
        statuses.add("En_Attente");
        statuses.add("Acceptée");

        return statuses;
    }

    @FXML
    void filtrercinema(ActionEvent event) {
        cinemasList.setOpacity(1);
        FilterAnchor.setVisible(false);

        // Récupérer les adresses sélectionnées
        List<String> selectedAddresses = getSelectedAddresses();
        // Récupérer les statuts sélectionnés
        List<String> selectedStatuses = getSelectedStatuses();

        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Cinema> filteredCinemas = getAllCinemas().stream()
                .filter(cinema -> selectedAddresses.isEmpty() || selectedAddresses.contains(cinema.getAdresse()))
                .filter(cinema -> selectedStatuses.isEmpty() || selectedStatuses.contains(cinema.getStatut()))
                .collect(Collectors.toList());

        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Cinema> filteredList = FXCollections.observableArrayList(filteredCinemas);
        listCinema.setItems(filteredList);
    }


    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private List<String> getSelectedStatuses() {
        // Récupérer les statuts sélectionnés dans l'AnchorPane de filtrage
        return statusCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    @FXML
    void afficherEventsAdmin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Event Manegement");
        stage.show();
        currentStage.close();

    }

    @FXML
    void afficherMovieAdmin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Film Manegement");
        stage.show();
        currentStage.close();

    }

    @FXML
    void afficherserieAdmin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Serie-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Serie Manegement");
        stage.show();
        currentStage.close();

    }

    @FXML
    void AfficherProduitAdmin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product Manegement");
        stage.show();
        currentStage.close();

    }

}


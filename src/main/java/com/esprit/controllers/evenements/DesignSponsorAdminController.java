package com.esprit.controllers.evenements;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.evenements.Sponsor;
import com.esprit.models.users.Client;
import com.esprit.services.evenements.SponsorService;
import com.esprit.utils.DataSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DesignSponsorAdminController {
    private final ObservableList<Sponsor> masterData = FXCollections.observableArrayList();

    private File selectedFile; // pour stocke le fichier image selectionné
    @FXML
    private Button bAddS;
    @FXML
    private FontAwesomeIconView iconLogoS;
    @FXML
    private ImageView image;
    @FXML
    private TableColumn<Sponsor, ImageView> tcLogo;
    @FXML
    private TableColumn<Sponsor, String> tcNomS;
    @FXML
    private TextField tfNomS;
    @FXML
    private TableView<Sponsor> tvSponsor;
    @FXML
    private TableColumn<Sponsor, Button> tcDeleteS;
    @FXML
    private TextField tfRechercheS;

    @FXML

    public static List<Sponsor> rechercher(List<Sponsor> liste, String recherche) {
        List<Sponsor> resultats = new ArrayList<>();

        if (recherche.isEmpty()) {
            return resultats;
        }

        for (Sponsor element : liste) {
            if (element.getNomSociete() != null && element.getNomSociete().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }


    @FXML
    void initialize() {

        afficher_sponsor();
        initDeleteColumn();
        setupSearchFilter();

    }

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void selectImage(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            image.setImage(selectedImage);
        }

    }

    @FXML
    public void ajouter_sponsor(ActionEvent actionEvent) {

        // Vérifier si une image a été sélectionnée
        if (selectedFile != null) {
            // Récupérer les valeurs des champs de saisie
            String nomSponsor = tfNomS.getText().trim();

            // Vérifier si les champs sont vides
            if (nomSponsor.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Information");
                alert.setContentText("Please fill out the form.");
                alert.show();
            }

            // Vérifier si le nom ne contient que des alphabets et des chiffres
            if (!nomSponsor.matches("[a-zA-Z0-9]*")) {
                showAlert("Please enter a valid name with no special characters.");
                return; // Arrêter l'exécution de la méthode si le nom n'est pas valide
            }

            // Convertir le fichier en un objet Blob
            Connection connection = null;
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();

                // Définir le flux d'entrée de l'image dans l'objet Blob
                try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Créer l'objet Sponsor avec l'image Blob
                SponsorService ss = new SponsorService();
                Sponsor nouveauSponsor = new Sponsor(nomSponsor, imageBlob);
                ss.create(nouveauSponsor);

                // Ajouter le nouveau produit à la liste existante
                tvSponsor.setItems(FXCollections.observableArrayList(ss.read()));

                // Rafraîchir la TableView
                tvSponsor.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sponsor Added");
                alert.setContentText("Sponsor Added !");
                alert.show();
            } catch (SQLException | IOException e) {
                showAlert("Error while adding sponsor : " + e.getMessage());
            }
        } else {
            showAlert("Please select an image !");
        }
    }

    @FXML
    void modifier_sponsor(Sponsor sponsor) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouveauNom = sponsor.getNomSociete();
        Blob img = sponsor.getLogo();
        int id = sponsor.getId();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        SponsorService ss = new SponsorService();
        ss.update(sponsor);
    }

    @FXML
    void gestionSeries(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Serie-view.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Series Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void gestionProduits(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Products Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void gestionFilms(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Movies Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void gestionCinemas(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdminCinema.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Cinemas Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void gestionEvenements(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Events Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    void afficher_sponsor() {

        //tvSponsor.getItems().clear();
        ImageView imageView = new ImageView();

        tcNomS.setCellValueFactory(new PropertyValueFactory<Sponsor, String>("nomSociete"));
        tcNomS.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomS.setOnEditCommit(event -> {
            Sponsor sponsor = event.getRowValue();
            sponsor.setNomSociete(event.getNewValue());
            modifier_sponsor(sponsor);
        });


        // Configurer la colonne Logo pour afficher et changer l'image
        tcLogo.setCellValueFactory(cellData -> {
            Sponsor s = cellData.getValue();

            imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
            try {
                Blob blob = s.getLogo();
                if (blob != null) {
                    Image image = new Image(blob.getBinaryStream());
                    imageView.setImage(image);
                } else {
                    // Afficher une image par défaut si le logo est null
                    Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("default_image.png")));
                    imageView.setImage(defaultImage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        // Configurer l'événement de clic pour changer l'image
        tcLogo.setCellFactory(col -> new TableCell<Sponsor, ImageView>() {

            private final ImageView imageView = new ImageView();
            private Sponsor sponsor;

            {
                setOnMouseClicked(event -> {
                    if (!isEmpty()) {

                        changerImage(sponsor);
                        afficher_sponsor();
                    }
                });
            }

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    setGraphic(imageView);
                    setText(null);
                }

                sponsor = getTableRow().getItem();
            }
        });


        // Activer l'édition en cliquant sur une ligne
        tvSponsor.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        tvSponsor.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Sponsor selectedSponsor = tvSponsor.getSelectionModel().getSelectedItem();
                if (selectedSponsor != null) {
                    modifier_sponsor(selectedSponsor);
                }
            }
        });

        // Utiliser une ObservableList pour stocker les éléments
        SponsorService ss = new SponsorService();
        masterData.addAll(ss.read());
        tvSponsor.setItems(FXCollections.observableArrayList(ss.read()));

        // Activer la sélection de cellules
        tvSponsor.getSelectionModel().setCellSelectionEnabled(true);

    }

    private void initDeleteColumn() {
        Callback<TableColumn<Sponsor, Button>, TableCell<Sponsor, Button>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sponsor, Button> call(final TableColumn<Sponsor, Button> param) {
                return new TableCell<Sponsor, Button>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Sponsor sponsor = getTableView().getItems().get(getIndex());
                            SponsorService ss = new SponsorService();
                            ss.delete(sponsor);

                            // Mise à jour de la TableView après la suppression de la base de données
                            tvSponsor.setItems(FXCollections.observableArrayList(ss.read()));
                            tvSponsor.refresh();
                        });
                    }

                    @Override
                    protected void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };

            }
        };

        //tcDeleteS.setCellFactory(cellFactory);

        //tvSponsor.getColumns().add(tcDeleteS);
        tcDeleteS.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sponsor, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Sponsor, Button> sponsorButtonCellDataFeatures) {
                final Button btnDelete = new Button("Delete");
                btnDelete.getStyleClass().add("delete-button");
                btnDelete.setOnAction((ActionEvent event) -> {
                    //Evenement evenement = getTableView().getItems().get(getIndex());
                    SponsorService ss = new SponsorService();
                    ss.delete(sponsorButtonCellDataFeatures.getValue());
                    // Mise à jour de la TableView après la suppression de la base de données
                    tvSponsor.setItems(FXCollections.observableArrayList(ss.read()));
                    tvSponsor.refresh();
                });
                return new SimpleObjectProperty<Button>(btnDelete);
            }
        });
    }

    private void setupSearchFilter() {
        FilteredList<Sponsor> filteredData = new FilteredList<>(masterData, p -> true);

        tfRechercheS.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(sponsor -> {
                // If filter text is empty, display all events.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare event name, category, and description of every event with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                return sponsor.getNomSociete().toLowerCase().contains(lowerCaseFilter); // Filter matches category name.
            });
        });

        // Wrap the FilteredList in a SortedList.
        SortedList<Sponsor> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tvSponsor.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        tvSponsor.setItems(sortedData);
    }

    // Méthode pour changer l'image
    private void changerImage(Sponsor sponsor) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                // Convertir le fichier en un objet Blob
                FileInputStream fis = new FileInputStream(selectedFile);
                connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();

                // Définir le flux d'entrée de l'image dans l'objet Blob
                try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                sponsor.setLogo(imageBlob);
                modifier_sponsor(sponsor);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showAlert("Error while loading image : " + e.getMessage());
            }
        }
    }


}

package com.esprit.controllers.evenements;

import com.esprit.models.evenements.Categorie_evenement;
import com.esprit.models.evenements.Evenement;
import com.esprit.services.evenements.CategorieService;
import com.esprit.services.evenements.EvenementService;
import com.esprit.services.evenements.SmsService;
import com.esprit.utils.DataSource;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DesignEvenementAdminController {

    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    private File selectedFile; // pour stocke le fichier image selectionné
    @FXML
    private ComboBox<String> cbCategorie;
    @FXML
    private Button bPDF;
    @FXML
    private Button bExcel;
    @FXML
    private DatePicker dpDD;
    @FXML
    private DatePicker dpDF;
    @FXML
    private TableColumn<Evenement, String> tcCategorieE;
    @FXML
    private TableColumn<Evenement, Date> tcDDE;
    @FXML
    private TableColumn<Evenement, Date> tcDFE;
    @FXML
    private TableColumn<Evenement, Button> tcDeleteE;
    @FXML
    private TableColumn<Evenement, String> tcDescriptionE;
    @FXML
    private TableColumn<Evenement, String> tcEtatE;
    @FXML
    private TableColumn<Evenement, String> tcLieuE;
    @FXML
    private TableColumn<Evenement, String> tcNomE;
    @FXML
    private TableColumn<Evenement, ImageView> tcPoster;
    @FXML
    private TextArea taDescription;
    @FXML
    private TextField tfEtat;
    @FXML
    private TextField tfLieu;
    @FXML
    private TextField tfNomEvenement;
    @FXML
    private TextField SearchBar;
    @FXML
    private Button bgesSeries;
    @FXML
    private TableView<Evenement> tvEvenement;
    @FXML
    private ImageView image;
    @FXML
    private AnchorPane FilterAnchor;

    @FXML
    void initialize() {

        CategorieService cs = new CategorieService();

        for (Categorie_evenement c : cs.read()) {
            cbCategorie.getItems().add(c.getNom_categorie());
        }
        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
            //filterCategorieEvenements(newValue.trim());

        });

        afficher_evenement();
        initDeleteColumn();
//        setupSearchFilter();


    }

    @FXML
    private void search(String keyword) {
        EvenementService es = new EvenementService();
        ObservableList<Evenement> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(es.read());
        } else {
            for (Evenement evenement : es.read()) {
                if (evenement.getNom().toLowerCase().contains(keyword.toLowerCase()) || evenement.getLieu().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getEtat().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getNom_categorieEvenement().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(evenement);
                }
            }
        }
        tvEvenement.setItems(filteredList);
    }

    private void initDeleteColumn() {
        Callback<TableColumn<Evenement, Button>, TableCell<Evenement, Button>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Evenement, Button> call(final TableColumn<Evenement, Button> param) {
                return new TableCell<Evenement, Button>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            EvenementService es = new EvenementService();
                            es.delete(evenement);

                            // Mise à jour de la TableView après la suppression de la base de données
                            tvEvenement.setItems(FXCollections.observableArrayList(es.read()));
                            tvEvenement.refresh();
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

        //tcDeleteE.setCellFactory(cellFactory);

        tcDeleteE.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Evenement, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Evenement, Button> evenementButtonCellDataFeatures) {
                final Button btnDelete = new Button("Delete");
                btnDelete.getStyleClass().add("delete-button");
                btnDelete.setOnAction((ActionEvent event) -> {
                    //Evenement evenement = getTableView().getItems().get(getIndex());
                    EvenementService es = new EvenementService();
                    es.delete(evenementButtonCellDataFeatures.getValue());
                    // Mise à jour de la TableView après la suppression de la base de données
                    tvEvenement.setItems(FXCollections.observableArrayList(es.read()));
                    tvEvenement.refresh();
                });
                return new SimpleObjectProperty<Button>(btnDelete);
            }
        });
        //tvEvenement.getColumns().add(tcDeleteE);
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
    void ajouterEvenement(ActionEvent event) {
        if (selectedFile != null) {

            // Récupérer les valeurs des champs de saisie
            String nomEvenement = tfNomEvenement.getText().trim();
            LocalDate dateDebut = dpDD.getValue();
            LocalDate dateFin = dpDF.getValue();
            String lieu = tfLieu.getText().trim();
            String nomCategorie = cbCategorie.getValue();
            String etat = tfEtat.getText().trim();
            String description = taDescription.getText().trim();

            // Vérifier si les champs sont vides
            if (nomEvenement.isEmpty() || lieu.isEmpty() || nomCategorie.isEmpty() || etat.isEmpty() || description.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Typing Error");
                alert.setContentText("Please fill out the form.");
                alert.show();
            }
            // Arrêter l'exécution de la méthode si les champs sont vides

            if (!nomEvenement.matches("[a-zA-Z0-9 ]*")) {
                showAlert("Please fill out the form with no special characters");
                // Arrêter l'exécution de la méthode si le nom n'est pas valide
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

                // Créer l'objet Evenement
                EvenementService es = new EvenementService();
                CategorieService cs = new CategorieService();
                Evenement nouvelEvenement = new Evenement(nomEvenement, Date.valueOf(dateDebut), Date.valueOf(dateFin), lieu, cs.getCategorieByNom(nomCategorie), etat, description, imageBlob);
                es.create(nouvelEvenement);

                // Ajouter le nouvel evenement à la liste existante
                tvEvenement.setItems(FXCollections.observableArrayList(es.read()));

                SmsService.sendSms("+21622757828", "   A new adventure is here ! RAKCHA just added an event, feel free to know the details in the events list !");

                // Rafraîchir la TableView
                tvEvenement.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Event added");
                alert.setContentText("Event added !");
                alert.show();

            } catch (SQLException | IOException e) {
                showAlert("Error while adding event : " + e.getMessage());
            }
        } else {
            showAlert("Please select an image !");
        }
    }

    @FXML
    void afficher_evenement() {

        ImageView imageView = new ImageView();

        tcCategorieE.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Evenement, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Evenement, String> evenementStringCellDataFeatures) {
                return new SimpleStringProperty(evenementStringCellDataFeatures.getValue().getNom_categorieEvenement());
            }
        });
        // Définissez le rendu de la cellule en utilisant le ComboBox
        tcCategorieE.setCellFactory(column -> {
            return new TableCell<Evenement, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        CategorieService cs = new CategorieService();
                        ComboBox<String> newComboBox = new ComboBox<>();

                        // Utilisez la méthode getCategorieByNom pour obtenir la catégorie
                        Categorie_evenement categorie = cs.getCategorieByNom(item);
                        // ComboBox newComboBox =new ComboBox<>();
                        // Ajoutez le nom de la catégorie au ComboBox
                        newComboBox.setItems(cbCategorie.getItems());
                        newComboBox.setValue(categorie.getNom_categorie());

                        // Afficher le ComboBox nouvellement créé dans la cellule
                        setGraphic(newComboBox);
                        newComboBox.setOnAction(event -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            evenement.setCategorie(new CategorieService().getCategorieByNom(newComboBox.getValue()));
                            modifier_evenement(evenement);
                            newComboBox.getStyleClass().add("combo-box-red");
                        });
                    }
                }
            };
        });

        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom"));
        tcNomE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setNom(event.getNewValue());
            modifier_evenement(evenement);
        });


        tcDDE.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>(cellData.getValue().getDateDebut());
            return property;
        });

        tcDDE.setCellFactory(column -> new TableCell<Evenement, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(format.format(date));
                }
            }
        });


        tcDFE.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>(cellData.getValue().getDateFin());
            return property;
        });

        tcDFE.setCellFactory(column -> new TableCell<Evenement, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(format.format(date));
                }
            }
        });

        tcLieuE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("lieu"));
        tcLieuE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcLieuE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setLieu(event.getNewValue());
            modifier_evenement(evenement);
        });

        tcEtatE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("etat"));
        tcEtatE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcEtatE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setEtat(event.getNewValue());
            modifier_evenement(evenement);
        });

        tcDescriptionE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("description"));
        tcDescriptionE.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescriptionE.setOnEditCommit(event -> {
            Evenement evenement = event.getRowValue();
            evenement.setDescription(event.getNewValue());
            modifier_evenement(evenement);
        });

        // Configurer la colonne Logo pour afficher et changer l'image
        tcPoster.setCellValueFactory(cellData -> {
            Evenement ev = cellData.getValue();

            imageView.setFitWidth(30); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(30); // Réglez la hauteur de l'image selon vos préférences
            try {
                Blob blob = ev.getAffiche_event();
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
        tcPoster.setCellFactory(col -> new TableCell<Evenement, ImageView>() {

            private final ImageView imageView = new ImageView();
            private Evenement evenement;

            {
                setOnMouseClicked(event -> {
                    if (!isEmpty()) {
                        changerImage(evenement);
                        afficher_evenement();
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
                    imageView.setFitWidth(30);
                    imageView.setFitHeight(30);
                    setGraphic(imageView);
                    setText(null);
                }

                evenement = getTableRow().getItem();
            }
        });


        // Activer l'édition en cliquant sur une ligne
        tvEvenement.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        tvEvenement.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Evenement selectedEvent = tvEvenement.getSelectionModel().getSelectedItem();
                if (selectedEvent != null) {
                    modifier_evenement(selectedEvent);
                }
            }
        });

        // Utiliser une ObservableList pour stocker les éléments
        EvenementService es = new EvenementService();
        masterData.addAll(es.read());
        tvEvenement.setItems(FXCollections.observableArrayList(es.read()));

        // Activer la sélection de cellules
        tvEvenement.getSelectionModel().setCellSelectionEnabled(true);

    }

    private void setupSearchFilter() {
        FilteredList<Evenement> filteredData = new FilteredList<>(masterData, p -> true);

        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(evenement -> {
                // If filter text is empty, display all events.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare event name, category, and description of every event with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (evenement.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches event name.
                } else if (evenement.getNom_categorieEvenement().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches category.
                } else if (evenement.getLieu().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches description.
                } else // Filter matches description.
                    if (evenement.getEtat().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches description.
                    } else return evenement.getDescription().toLowerCase().contains(lowerCaseFilter);// Does not match.
            });
        });

        // Wrap the FilteredList in a SortedList.
        SortedList<Evenement> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tvEvenement.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        tvEvenement.setItems(sortedData);
    }

    @FXML
    void modifier_evenement(Evenement evenement) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouvelleCategorie = evenement.getNom_categorieEvenement();
        String nouveauNom = evenement.getNom();
        Date nouvelledateD = evenement.getDateDebut();
        Date nouvelledateF = evenement.getDateFin();
        String nouveauLieu = evenement.getLieu();
        String nouvelEtat = evenement.getEtat();
        String nouvelleDescription = evenement.getDescription();
        Blob img = evenement.getAffiche_event();
        int id = evenement.getId();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        EvenementService es = new EvenementService();
        es.update(evenement);
        if (Objects.equals(evenement.getEtat().toLowerCase(), "postponed")) {
            SmsService.sendSms("+21622757828", String.format("We're sorry, The event : %s has been postponed to a later date", evenement.getNom()));

        } else if (Objects.equals(evenement.getEtat().toLowerCase(), "ongoing")) {
            SmsService.sendSms("+21622757828", String.format("Hurry up ! The event '%s' has just started, Feel free to Join us as soon as possible ", evenement.getNom()));

        }
    }

    @FXML
    void gestionCategorie(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListproduitAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignCategorieEventAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Category Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
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


    @FXML
    void gestionSponsor(ActionEvent event) throws IOException {

        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignSponsorAdmin.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Sponsor Management");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();

    }


    @FXML
    void generatePDF() {
        EvenementService es = new EvenementService();
        es.generateEventPDF();
    }

    @FXML
    void generateExcel() {
        EvenementService es = new EvenementService();
        es.generateEventExcel();
    }

    private void changerImage(Evenement evenement) {
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
                evenement.setAffiche_event(imageBlob);
                modifier_evenement(evenement);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showAlert("Error while loading image : " + e.getMessage());
            }
        }
    }

    private void filterCategorieEvenements(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Evenement> filteredList = FXCollections.observableArrayList();
            for (Evenement categorie : tvEvenement.getItems()) {
                if (categorie.getCategorie().getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(categorie);

                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            tvEvenement.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            afficher_evenement();
        }
    }


    private List<Evenement> getAllCategories() {
        EvenementService evenementService = new EvenementService();
        List<Evenement> categorie = evenementService.read();
        return categorie;
    }

    @FXML
    void filtrer(MouseEvent event) {

        tvEvenement.setOpacity(0.5);
        FilterAnchor.setVisible(true);

        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> categorie = getCategorie_Evenement();


        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(70);


        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }


    public List<String> getCategorie_Evenement() {
        // Récupérer tous les cinémas depuis la base de données
        List<Evenement> categories = getAllCategories();

        // Extraire les adresses uniques des cinémas
        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());

        return categorie;
    }

    @FXML
    public void filtercinema(ActionEvent event) {

        tvEvenement.setOpacity(1);


        FilterAnchor.setVisible(false);

        tvEvenement.setVisible(true);

        // Récupérer les adresses sélectionnées
        List<String> selectedCategories = getSelectedCategories();
        // Récupérer les statuts sélectionnés

        Evenement evenement = new Evenement();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Evenement> filteredCategories = getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getCategorie().getNom_categorie()))
                .collect(Collectors.toList());

        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Evenement> filteredList = FXCollections.observableArrayList(filteredCategories);
        tvEvenement.setItems(filteredList);


    }

    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }


}




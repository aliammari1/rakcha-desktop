package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.models.Film;
import com.esprit.models.Salle;
import com.esprit.models.Seance;
import com.esprit.services.CinemaService;
import com.esprit.services.FilmService;
import com.esprit.services.SalleService;
import com.esprit.services.SeanceService;
import com.esprit.utils.DataSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardResponsableController implements Initializable {

    @FXML
    private File selectedFile;

    @FXML
    private ImageView image;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfResponsable;

    @FXML
    private FlowPane cinemaFlowPane;
    @FXML
    private AnchorPane cinemaFormPane;

    @FXML
    private AnchorPane sessionFormPane;

    @FXML
    private AnchorPane cinemaListPane;

    @FXML
    private ComboBox<String> comboCinema;

    @FXML
    private ComboBox<String> comboMovie;

    @FXML
    private ComboBox<String> comboRoom;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfDepartureTime;

    @FXML
    private TextField tfEndTime;

    @FXML
    private TextField tfPrice;

    @FXML
    private TableView<Seance> SessionTableView;

    @FXML
    private TableColumn<Seance,Void> colAction;

    @FXML
    private TableColumn<Seance, String> colCinema;

    @FXML
    private TableColumn<Seance, Date> colDate;

    @FXML
    private TableColumn<Seance, Time> colDepartTime;

    @FXML
    private TableColumn<Seance, Time> colEndTime;

    @FXML
    private TableColumn<Seance, String> colMovie;

    @FXML
    private TableColumn<Seance, String> colMovieRoom;

    @FXML
    private TableColumn<Seance, Integer> colPrice;


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void addCinema(ActionEvent event) {
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

                String defaultStatut = "En_Attente";

                Cinema cinema = new Cinema(tfNom.getText(), tfAdresse.getText(), tfResponsable.getText(), imageBlob, defaultStatut);

                CinemaService cs = new CinemaService();
                cs.create(cinema);
                showAlert("Cinéma ajouté avec succès !");
            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de l'ajout du cinéma : " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        showAlert("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    }
                }
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }

    }



    @FXML
    void selectImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            image.setImage(selectedImage);
        }

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Cinema> acceptedCinemas = loadAcceptedCinemas();

        cinemaFormPane.setVisible(true);
        sessionFormPane.setVisible(false);
        cinemaListPane.setVisible(true);
        SessionTableView.setVisible(false);

        for (Cinema c : acceptedCinemas) {
            comboCinema.getItems().add(c.getNom());
        }

        // Ajouter un écouteur de changement de sélection pour le ComboBox des cinémas
        comboCinema.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                // Convertir newValue en objet Cinema
                Cinema selectedCinema = acceptedCinemas.stream()
                        .filter(cinema -> cinema.getNom().equals(newValue))
                        .findFirst()
                        .orElse(null);

                if (selectedCinema != null) {
                    // Charger les films en fonction du cinéma sélectionné
                    loadMoviesForCinema(selectedCinema.getId_cinema());
                    // Charger les salles en fonction du cinéma sélectionné
                    loadRoomsForCinema(selectedCinema.getId_cinema());
                }
            }
        });
    }


    private void loadMoviesForCinema(int cinemaId) {
        comboMovie.getItems().clear();
        FilmService fs = new FilmService();
        List<Film> moviesForCinema = fs.readMoviesForCinema(cinemaId);
        for (Film f : moviesForCinema) {
            comboMovie.getItems().add(f.getNom());
        }
    }

    private void loadRoomsForCinema(int cinemaId) {
        comboRoom.getItems().clear();
        SalleService ss = new SalleService();
        List<Salle> roomsForCinema = ss.readRoomsForCinema(cinemaId);
        for (Salle s : roomsForCinema) {
            comboRoom.getItems().add(s.getNom_salle());
        }
    }

    private List<Cinema> loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        List<Cinema> acceptedCinemas = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
                .collect(Collectors.toList());

        if (acceptedCinemas.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
        }

        for (Cinema cinema : acceptedCinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }
        return acceptedCinemas;
    }



    private HBox createCinemaCard(Cinema cinema) {
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  25px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        card.setPrefWidth(400);

        ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(70); // la largeur de l'image
        logoImageView.setFitHeight(70);
        logoImageView.setLayoutX(15); // Padding à droite
        logoImageView.setLayoutY(15); // Padding en haut
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");

        try {
            Blob logoBlob = cinema.getLogo();
            if (logoBlob != null) {
                byte[] logoBytes = logoBlob.getBytes(1, (int) logoBlob.length());
                Image logoImage = new Image(new ByteArrayInputStream(logoBytes));
                logoImageView.setImage(logoImage);
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("default_logo.png"));
                logoImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(logoImageView);


        Label nameLabel = new Label("Name: " + cinema.getNom());
        nameLabel.setLayoutX(115);
        nameLabel.setLayoutY(25);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(nameLabel);


        Label adresseLabel = new Label("Address: " + cinema.getAdresse());
        adresseLabel.setLayoutX(115);
        adresseLabel.setLayoutY(50);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);

        Line verticalLine = new Line();
        verticalLine.setStartX(240);
        verticalLine.setStartY(10);
        verticalLine.setEndX(240);
        verticalLine.setEndY(90);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);

        card.getChildren().add(verticalLine);


        Circle circle = new Circle();
        circle.setRadius(30);
        circle.setLayoutX(285);
        circle.setLayoutY(45);
        circle.setFill(Color.web("#ae2d3c"));


        FontAwesomeIconView deleteIcon = new FontAwesomeIconView();
        deleteIcon.setGlyphName("TRASH");
        deleteIcon.setSize("3.5em");
        deleteIcon.setLayoutX(270);
        deleteIcon.setLayoutY(57);
        deleteIcon.setFill(Color.WHITE);

        deleteIcon.setOnMouseClicked(event -> {
            CinemaService cinemaService = new CinemaService();
            cinemaService.delete(cinema);
            cinemaFlowPane.getChildren().remove(card);
        });

        card.getChildren().addAll(circle, deleteIcon);

        Circle SalleCircle = new Circle();
        SalleCircle.setRadius(30);
        SalleCircle.setLayoutX(360);
        SalleCircle.setLayoutY(45);
        SalleCircle.setFill(Color.web("#ae2d3c"));

        FontAwesomeIconView salleIcon = new FontAwesomeIconView();
        salleIcon.setGlyphName("GROUP");
        salleIcon.setSize("3em");
        salleIcon.setLayoutX(340);
        salleIcon.setLayoutY(57);
        salleIcon.setFill(Color.WHITE);

        salleIcon.setOnMouseClicked(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListSalleResponsable.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                ListSalleResponsableController controller = loader.getController();
                controller.initData(cinema.getId_cinema());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });

        card.getChildren().addAll(SalleCircle, salleIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    @FXML
    private void showCinemaList() {
        // Afficher le formulaire d'ajout de cinéma et la liste des cinémas
        cinemaFormPane.setVisible(true);
        sessionFormPane.setVisible(false);
        cinemaListPane.setVisible(true);
        SessionTableView.setVisible(false);

    }

    @FXML
    private void showSessionForm() {
        cinemaFormPane.setVisible(false);
        sessionFormPane.setVisible(true);
        cinemaListPane.setVisible(false);
        SessionTableView.setVisible(true);
        colMovie.setCellValueFactory(new PropertyValueFactory<>("nom_film"));
        colCinema.setCellValueFactory(new PropertyValueFactory<>("nom_cinema"));
        colMovieRoom.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDepartTime.setCellValueFactory(new PropertyValueFactory<>("HD"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("HF"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colAction.setCellFactory(new Callback<TableColumn<Seance, Void>, TableCell<Seance, Void>>() {
            @Override
            public TableCell<Seance, Void> call(TableColumn<Seance, Void> param) {
                return new TableCell<Seance,Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.getStyleClass().add("delete-btn");
                        deleteButton.setOnAction(event -> {
                            Seance seance = getTableView().getItems().get(getIndex());
                            SeanceService seanceService = new SeanceService();
                            seanceService.delete(seance);
                            getTableView().getItems().remove(seance);
                        });
                    }


                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Afficher les boutons dans la cellule de la colonne Action
                            setGraphic(new HBox(deleteButton));
                        }
                    }
                };
            }
        });

        SessionTableView.setEditable(true);

        colPrice.setCellFactory(tc -> new TableCell<Seance, Integer>() {
            @Override
            protected void updateItem(Integer prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(prix + " DT");
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (isEmpty()) {
                    return;
                }
                TextField textField = new TextField(getItem().toString());
                textField.setOnAction(event -> {
                    commitEdit(Integer.parseInt(textField.getText()));
                });
                setGraphic(textField);
                setText(null);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem() + " DT");
                setGraphic(null);
            }

            @Override
            public void commitEdit(Integer newValue) {
                super.commitEdit(newValue);
                Seance seance = getTableView().getItems().get(getIndex());
                seance.setPrix(newValue);
                SeanceService seanceService = new SeanceService();
                seanceService.update(seance);
                setText(newValue + " DT");
                setGraphic(null);
            }
        });
        loadSeances();

    }

    @FXML
    void addSeance() {
        String selectedCinemaName = comboCinema.getValue();
        String selectedFilmName = comboMovie.getValue();
        String selectedRoomName = comboRoom.getValue();
        LocalDate selectedDate = dpDate.getValue();
        String departureTimeText = tfDepartureTime.getText();
        String endTimeText = tfEndTime.getText();
        String priceText = tfPrice.getText();

        if (selectedCinemaName == null || selectedFilmName == null || selectedRoomName == null || selectedDate == null ||
                departureTimeText.isEmpty() || endTimeText.isEmpty() || priceText.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        CinemaService cinemaService = new CinemaService();
        Cinema selectedCinema = cinemaService.getCinemaByName(selectedCinemaName);

        FilmService filmService = new FilmService();
        Film selectedFilm = filmService.getFilmByName(selectedFilmName);

        SalleService salleService = new SalleService();
        Salle selectedRoom = salleService.getSalleByName(selectedRoomName);

        Time departureTime = Time.valueOf(LocalTime.parse(departureTimeText));
        Time endTime = Time.valueOf(LocalTime.parse(endTimeText));

        Date date = Date.valueOf(selectedDate);

        int price = Integer.parseInt(priceText);

        Seance newSeance = new Seance(selectedFilm, selectedRoom, departureTime, endTime, date, selectedCinema, price);

        SeanceService seanceService = new SeanceService();
        seanceService.create(newSeance);

        System.out.println("Séance ajoutée avec succès !");
        loadSeances();
    }

    private void loadSeances() {
        SeanceService seanceService = new SeanceService();
        List<Seance> seances = seanceService.read();

        ObservableList<Seance> seanceObservableList = FXCollections.observableArrayList(seances);

        SessionTableView.setItems(seanceObservableList);
    }


}

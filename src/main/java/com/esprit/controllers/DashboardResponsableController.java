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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;


import javax.imageio.IIOParam;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

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
    private AnchorPane addRoomForm;

    @FXML
    private TextField tfNbrPlaces;

    @FXML
    private TextField tfNomSalle;

    private int cinemaId;

    @FXML
    private TableView<Salle> RoomTableView;

    @FXML
    private TableColumn<Salle, Void> colActionRoom;

    @FXML
    private TableColumn<Salle, String> colNameRoom;

    @FXML
    private TableColumn<Salle, Integer> colNbrPlaces;


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
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();

        cinemaFormPane.setVisible(true);
        sessionFormPane.setVisible(false);
        cinemaListPane.setVisible(true);
        SessionTableView.setVisible(false);
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);


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

    private HashSet<Cinema> chargerAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
                .collect(Collectors.toList());

        if (acceptedCinemasList.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
        }

        HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        return acceptedCinemasSet;
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

        // Modifier la partie où vous créez l'ImageView pour permettre le double clic
        logoImageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Ouvrir une boîte de dialogue de sélection de fichier pour choisir une nouvelle image
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir une nouvelle image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
                );
                File selectedFile = fileChooser.showOpenDialog(null);

                // Si l'utilisateur a choisi un fichier
                if (selectedFile != null) {
                    // Mettre à jour l'image dans la base de données
                    try {
                        // Convertir le fichier en tableau de bytes
                        byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
                        // Mettre à jour l'image dans la base de données pour le cinéma
                        CinemaService cinemaService = new CinemaService();
                        cinema.setLogo(new javax.sql.rowset.serial.SerialBlob(imageBytes));
                        cinemaService.update(cinema);

                        // Mettre à jour l'image dans l'ImageView
                        Image newImage = new Image(new ByteArrayInputStream(imageBytes));
                        logoImageView.setImage(newImage);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                        // Gérer l'exception
                    }
                }
            }
        });

        Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(115);
        NomLabel.setLayoutY(25);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(NomLabel);

        Label nameLabel = new Label(cinema.getNom());
        nameLabel.setLayoutX(160);
        nameLabel.setLayoutY(25);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(nameLabel);
        nameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Vérifiez si c'est un double clic
                TextField nameTextField = new TextField(nameLabel.getText()); // Créez un TextField avec le texte actuel du Label
                nameTextField.setLayoutX(nameLabel.getLayoutX());
                nameTextField.setLayoutY(nameLabel.getLayoutY());
                nameTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                nameTextField.setPrefWidth(nameLabel.getWidth()); // Ajustez la largeur pour correspondre au Label

                // Lorsque l'utilisateur appuie sur Entrée dans le TextField, mettez à jour le Label avec le nouveau nom
                nameTextField.setOnAction(e -> {
                    nameLabel.setText(nameTextField.getText());
                    // Mettez à jour le nom du cinéma dans la base de données si nécessaire
                    cinema.setNom(nameTextField.getText()); // Assurez-vous de mettre à jour le nom du cinéma dans votre objet Cinema
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema); // Mettez à jour le cinéma dans la base de données
                    card.getChildren().remove(nameTextField); // Supprimez le TextField
                });

                // Ajoutez le TextField au conteneur AnchorPane
                card.getChildren().add(nameTextField);

                // Focus sur le TextField pour permettre la saisie immédiate
                nameTextField.requestFocus();
                nameTextField.selectAll(); // Sélectionnez tout le texte pour une modification facile
            }
        });

        Label AdrsLabel = new Label("Adresse: ");
        AdrsLabel.setLayoutX(115);
        AdrsLabel.setLayoutY(50);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);

        Label adresseLabel = new Label(cinema.getAdresse());
        adresseLabel.setLayoutX(180);
        adresseLabel.setLayoutY(50);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);

        adresseLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Vérifiez si c'est un double clic
                TextField adresseTextField = new TextField(adresseLabel.getText()); // Créez un TextField avec le texte actuel du Label
                adresseTextField.setLayoutX(adresseLabel.getLayoutX());
                adresseTextField.setLayoutY(adresseLabel.getLayoutY());
                adresseTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                adresseTextField.setPrefWidth(adresseLabel.getWidth()); // Ajustez la largeur pour correspondre au Label

                // Lorsque l'utilisateur appuie sur Entrée dans le TextField, mettez à jour le Label avec la nouvelle adresse
                adresseTextField.setOnAction(e -> {
                    adresseLabel.setText(adresseTextField.getText());
                    // Mettez à jour l'adresse du cinéma dans la base de données si nécessaire
                    cinema.setAdresse(adresseTextField.getText());
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    card.getChildren().remove(adresseTextField); // Supprimez le TextField
                });


                card.getChildren().add(adresseTextField);

                // Focus sur le TextField pour permettre la saisie immédiate
                adresseTextField.requestFocus();
                adresseTextField.selectAll(); // Sélectionnez tout le texte pour une modification facile
            }
        });


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
            cinemaId = cinema.getId_cinema();
            cinemaFormPane.setVisible(false);
            cinemaListPane.setVisible(false);
            addRoomForm.setVisible(true);
            RoomTableView.setVisible(true);
            colNameRoom.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
            colNbrPlaces.setCellValueFactory(new PropertyValueFactory<>("nb_places"));
            colActionRoom.setCellFactory(new Callback<TableColumn<Salle, Void>, TableCell<Salle, Void>>() {
                @Override
                public TableCell<Salle, Void> call(TableColumn<Salle, Void> param) {
                    return new TableCell<Salle,Void>() {
                        private final Button deleteRoomButton = new Button("Delete");

                        {
                            deleteRoomButton.getStyleClass().add("delete-btn");
                            deleteRoomButton.setOnAction(event -> {
                                Salle salle = getTableView().getItems().get(getIndex());
                                SalleService salleService = new SalleService();
                                salleService.delete(salle);
                                getTableView().getItems().remove(salle);
                            });
                        }


                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                // Afficher les boutons dans la cellule de la colonne Action
                                setGraphic(new HBox(deleteRoomButton));
                            }
                        }
                    };
                }
            });
            RoomTableView.setEditable(true);
            colNbrPlaces.setCellFactory(tc -> new TableCell<Salle, Integer>() {
                @Override
                protected void updateItem(Integer nb_salles, boolean empty) {
                    super.updateItem(nb_salles, empty);
                    if (empty || nb_salles == null) {
                        setText(null);
                    } else {
                        setText(nb_salles + " places");
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
                    setText(getItem() + " places");
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Integer newValue) {
                    super.commitEdit(newValue);
                    Salle salle = getTableView().getItems().get(getIndex());
                    salle.setNb_places(newValue);
                    SalleService salleService = new SalleService();
                    salleService.update(salle);
                    setText(newValue + " places");
                    setGraphic(null);
                }
            });
            colNameRoom.setCellFactory(tc -> new TableCell<Salle, String>() {
                @Override
                protected void updateItem(String nom_salle, boolean empty) {
                    super.updateItem(nom_salle, empty);
                    if (empty || nom_salle == null) {
                        setText(null);
                    } else {
                        setText(nom_salle);
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
                        commitEdit((textField.getText()));
                    });
                    setGraphic(textField);
                    setText(null);
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getItem());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(String newValue) {
                    super.commitEdit(newValue);
                    Salle salle = getTableView().getItems().get(getIndex());
                    salle.setNom_salle(newValue);
                    SalleService salleService = new SalleService();
                    salleService.update(salle);
                    setText(newValue);
                    setGraphic(null);
                }
            });
            loadsalles();
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
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);
    }

    @FXML
    private void showSessionForm() {
        cinemaFormPane.setVisible(false);
        sessionFormPane.setVisible(true);
        cinemaListPane.setVisible(false);
        SessionTableView.setVisible(true);
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);
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
        colEndTime.setCellFactory(tc -> new TableCell<Seance, Time>() {

            @Override
            protected void updateItem(Time HF, boolean empty) {
                super.updateItem(HF, empty);
                if (empty || HF == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(HF));
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
                    commitEdit(Time.valueOf((textField.getText())));
                });
                setGraphic(textField);
                setText(null);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(String.valueOf(getItem()));
                setGraphic(null);
            }

            @Override
            public void commitEdit(Time newValue) {
                super.commitEdit(newValue);
                Seance seance = getTableView().getItems().get(getIndex());
                seance.setHF(newValue);
                SeanceService seanceService = new SeanceService();
                seanceService.update(seance);
                setText(String.valueOf(newValue));
                setGraphic(null);
            }
        });
        colDepartTime.setCellFactory(tc -> new TableCell<Seance, Time>() {

            @Override
            protected void updateItem(Time HD, boolean empty) {
                super.updateItem(HD, empty);
                if (empty || HD == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(HD));
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
                    commitEdit(Time.valueOf((textField.getText())));
                });
                setGraphic(textField);
                setText(null);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(String.valueOf(getItem()));
                setGraphic(null);
            }

            @Override
            public void commitEdit(Time newValue) {
                super.commitEdit(newValue);
                Seance seance = getTableView().getItems().get(getIndex());
                seance.setHD(newValue);
                SeanceService seanceService = new SeanceService();
                seanceService.update(seance);
                setText(String.valueOf(newValue));
                setGraphic(null);
            }
        });

        colDate.setCellFactory(tc -> new TableCell<Seance, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.toString());
                }

                // Double clic détecté
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        // Ouvrir un DatePicker
                        DatePicker datePicker = new DatePicker();

                        // Si la valeur de la cellule n'est pas vide, définissez la date sélectionnée dans le DatePicker
                        if (!isEmpty() && getItem() != null) {
                            datePicker.setValue(getItem().toLocalDate());
                        }

                        // Définir un EventHandler pour le changement de date dans le DatePicker
                        datePicker.setOnAction(e -> {
                            LocalDate selectedDate = datePicker.getValue();
                            if (selectedDate != null) {
                                Date newDate = Date.valueOf(selectedDate);
                                commitEdit(newDate);
                            }
                        });

                        // Afficher le DatePicker dans une fenêtre contextuelle
                        StackPane root = new StackPane(datePicker);
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.show();
                    }
                });
            }
            @Override
            public void commitEdit(Date newValue) {
                super.commitEdit(newValue);
                Seance seance = getTableView().getItems().get(getIndex());
                seance.setDate(newValue);
                SeanceService seanceService = new SeanceService();
                seanceService.update(seance);
                setText(String.valueOf(newValue));
                setGraphic(null);
            }
        });
        colCinema.setCellFactory(tc -> new TableCell<Seance, String>() {
            @Override
            protected void updateItem(String cinemaName, boolean empty) {
                super.updateItem(cinemaName, empty);
                if (empty || cinemaName == null) {
                    setText(null);
                } else {
                    setText(cinemaName);
                }

                // Double clic détecté
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        // Créer un ComboBox contenant les noms des cinémas acceptés
                        ComboBox<String> cinemaComboBox = new ComboBox<>();
                        HashSet<Cinema> acceptedCinema = chargerAcceptedCinemas();
                        for (Cinema cinema : acceptedCinema) {
                            cinemaComboBox.getItems().add(cinema.getNom());
                        }

                        // Sélectionner le nom du cinéma correspondant à la valeur actuelle de la cellule
                        cinemaComboBox.setValue(cinemaName);

                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        cinemaComboBox.setOnAction(e -> {
                            String selectedCinemaName = cinemaComboBox.getValue();
                            // Mettre à jour la valeur de la cellule dans le TableView
                            commitEdit(selectedCinemaName);
                            // Mettre à jour la base de données en utilisant la méthode update de seanceService
                            Seance seance = getTableView().getItems().get(getIndex());
                            for (Cinema cinema : acceptedCinema) {
                                if (cinema.getNom().equals(selectedCinemaName)) {
                                    seance.setCinema(cinema);
                                    break;
                                }
                            }
                            SeanceService seanceService = new SeanceService();
                            seanceService.update(seance);
                        });

                        // Afficher le ComboBox dans la cellule
                        setGraphic(cinemaComboBox);
                    }
                });
            }
        });
        colMovieRoom.setCellFactory(tc -> new TableCell<Seance, String>() {
            @Override
            protected void updateItem(String salleName, boolean empty) {
                super.updateItem(salleName, empty);
                if (empty || salleName == null) {
                    setText(null);
                } else {
                    setText(salleName);
                }


                // Double clic détecté
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        // Créer un ComboBox contenant les noms des salles associées au cinéma sélectionné
                        ComboBox<String> salleComboBox = new ComboBox<>();
                        Seance seance = getTableView().getItems().get(getIndex());
                        Cinema selectedCinema = seance.getCinema();

                        // Récupérer les salles associées au cinéma sélectionné
                        List<Salle> associatedSalles = loadAssociatedSalles(selectedCinema.getId_cinema());
                        for (Salle salle : associatedSalles) {
                            salleComboBox.getItems().add(salle.getNom_salle());
                        }

                        // Sélectionner le nom de la salle correspondant à la valeur actuelle de la cellule
                        salleComboBox.setValue(salleName);

                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        salleComboBox.setOnAction(e -> {
                            String selectedSalleName = salleComboBox.getValue();
                            // Mettre à jour la valeur de la cellule dans le TableView
                            commitEdit(selectedSalleName);
                            // Mettre à jour la base de données en utilisant la méthode update de seanceService
                            for (Salle salle : associatedSalles) {
                                if (salle.getNom_salle().equals(selectedSalleName)) {
                                    seance.setSalle(salle);
                                    break;
                                }
                            }
                            SeanceService seanceService = new SeanceService();
                            seanceService.update(seance);
                        });

                        // Afficher le ComboBox dans la cellule
                        setGraphic(salleComboBox);
                    }
                });
            }
            private List<Salle> loadAssociatedSalles(int idCinema) {
                SalleService salleService = new SalleService();
                return salleService.readRoomsForCinema(idCinema);
            }
        });

        colMovie.setCellFactory(tc -> new TableCell<Seance, String>() {
            @Override
            protected void updateItem(String filmName, boolean empty) {
                super.updateItem(filmName, empty);
                if (empty || filmName == null) {
                    setText(null);
                } else {
                    setText(filmName);
                }


                // Double clic détecté
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        // Créer un ComboBox contenant les noms des films associées au cinéma sélectionné
                        ComboBox<String> filmComboBox = new ComboBox<>();
                        Seance seance = getTableView().getItems().get(getIndex());
                        Cinema selectedCinema = seance.getCinema();

                        // Récupérer les films associées au cinéma sélectionné
                        List<Film> associatedFilms = loadAssociatedFilms(selectedCinema.getId_cinema());
                        for (Film film : associatedFilms) {
                            filmComboBox.getItems().add(film.getNom());
                        }

                        // Sélectionner le nom de la salle correspondant à la valeur actuelle de la cellule
                        filmComboBox.setValue(filmName);

                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        filmComboBox.setOnAction(e -> {
                            String selectedFilmName = filmComboBox.getValue();
                            // Mettre à jour la valeur de la cellule dans le TableView
                            commitEdit(selectedFilmName);
                            // Mettre à jour la base de données en utilisant la méthode update de seanceService
                            for (Film film : associatedFilms) {
                                if (film.getNom().equals(selectedFilmName)) {
                                    seance.setFilm(film);
                                    break;
                                }
                            }
                            SeanceService seanceService = new SeanceService();
                            seanceService.update(seance);
                        });

                        // Afficher le ComboBox dans la cellule
                        setGraphic(filmComboBox);
                    }
                });
            }
            private List<Film> loadAssociatedFilms(int idCinema) {
                FilmService filmService = new FilmService();
                return filmService.readMoviesForCinema(idCinema);
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

    @FXML
    void AjouterSalle(ActionEvent event) {
        SalleService ss = new SalleService();
        ss.create(new Salle( cinemaId, parseInt(tfNbrPlaces.getText()), tfNomSalle.getText()));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salle ajoutée");
        alert.setContentText("Salle ajoutée !");
        alert.show();
        loadsalles();
    }

    private void loadsalles() {
        SalleService salleService = new SalleService();
        List<Salle> salles = salleService.read();

        List<Salle> salles_cinema = salles.stream()
                .filter(salle -> salle.getId_cinema() == cinemaId)
                .collect(Collectors.toList());

        if (salles_cinema.isEmpty()) {
            showAlert("Aucune salle n'est disponible");
            return;
        }

        ObservableList<Salle> salleInfos = FXCollections.observableArrayList(salles_cinema);

        RoomTableView.setItems(salleInfos);
    }
}

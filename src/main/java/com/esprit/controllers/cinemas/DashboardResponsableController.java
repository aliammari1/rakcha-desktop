package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.Salle;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcinema;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.SalleService;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.FilmcinemaService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardResponsableController implements Initializable {

    Responsable_de_cinema responsableDeCinema;
    @FXML
    private ImageView image;
    @FXML
    private TextField tfAdresse;
    @FXML
    private TextField tfNom;
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
    private TableColumn<Seance, Void> colAction;
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
    private TableColumn<Seance, Double> colPrice;
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
    private AnchorPane facebookAnchor;
    @FXML
    private TextArea txtareaStatut;

    @FXML
    private AnchorPane StatisticsAnchor;

    @FXML
    private Button showStat;
    @FXML
    private PieChart pieChart;

    @FXML
    public void showSeance(ActionEvent event) {
        sessionFormPane.setVisible(true);
        SessionTableView.setVisible(true);
        cinemaFormPane.setVisible(false);
        cinemaFlowPane.setVisible(false);
        cinemaListPane.setVisible(false);
    }

    public void setData(Responsable_de_cinema resp) {
        this.responsableDeCinema = resp;
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
    void addCinema(ActionEvent event) {
        if (tfNom.getText().isEmpty() || tfAdresse.getText().isEmpty()) {
            showAlert("please complete all fields!");
            return;
        }

        String defaultStatut = "En_Attente";

        Cinema cinema = new Cinema(tfNom.getText(), tfAdresse.getText(), responsableDeCinema, image.getImage().getUrl(), defaultStatut);

        CinemaService cs = new CinemaService();
        cs.create(cinema);
        showAlert("Cinema added successfully !");

    }

    @FXML
    void selectImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory = "./src/main/resources/pictures/films/";
                Path destinationPath = Paths.get(destinationDirectory);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath);
                Image selectedImage = new Image(destinationFilePath.toUri().toString());
                image.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        //showStat.setVisible(true);


        for (Cinema c : acceptedCinemas) {
            comboCinema.getItems().add(c.getNom());
        }

        comboCinema.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                Cinema selectedCinema = acceptedCinemas.stream()
                        .filter(cinema -> cinema.getNom().equals(newValue))
                        .findFirst()
                        .orElse(null);

                if (selectedCinema != null) {
                    loadMoviesForCinema(selectedCinema.getId_cinema());
                    loadRoomsForCinema(selectedCinema.getId_cinema());
                }
            }
        });
    }


    private void loadMoviesForCinema(int cinemaId) {
        comboMovie.getItems().clear();
        FilmcinemaService fs = new FilmcinemaService();
        List<Film> moviesForCinema = fs.readMoviesForCinema(cinemaId);
        for (Film f : moviesForCinema) {
            System.out.println("moviesForCinema: " + f);
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
            showAlert("No accepted cinemas are available.");
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
            showAlert("No accepted cinemas are available.");
        }

        HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        return acceptedCinemasSet;
    }


    private HBox createCinemaCard(Cinema cinema) {
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  25px;");

        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        card.setPrefWidth(400);

        ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(70);
        logoImageView.setFitHeight(70);
        logoImageView.setLayoutX(15);
        logoImageView.setLayoutY(15);
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");

        Image image = null;
        if (!cinema.getLogo().isEmpty())
            image = new Image(cinema.getLogo());
        else
            image = new Image("Logo.png");
        logoImageView.setImage(image);
        card.getChildren().add(logoImageView);

        logoImageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose a new image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
                );
                File selectedFile = fileChooser.showOpenDialog(null);

                if (selectedFile != null) {
                    CinemaService cinemaService = new CinemaService();
                    cinema.setLogo("");
                    cinemaService.update(cinema);

                    Image newImage = new Image(cinema.getLogo());
                    logoImageView.setImage(newImage);
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
            if (event.getClickCount() == 2) {
                TextField nameTextField = new TextField(nameLabel.getText());
                nameTextField.setLayoutX(nameLabel.getLayoutX());
                nameTextField.setLayoutY(nameLabel.getLayoutY());
                nameTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                nameTextField.setPrefWidth(nameLabel.getWidth());
                nameTextField.setOnAction(e -> {
                    nameLabel.setText(nameTextField.getText());
                    cinema.setNom(nameTextField.getText());
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    card.getChildren().remove(nameTextField);
                });


                card.getChildren().add(nameTextField);


                nameTextField.requestFocus();
                nameTextField.selectAll();
            }
        });

        Label AdrsLabel = new Label("Address: ");
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
            if (event.getClickCount() == 2) {
                TextField adresseTextField = new TextField(adresseLabel.getText());
                adresseTextField.setLayoutX(adresseLabel.getLayoutX());
                adresseTextField.setLayoutY(adresseLabel.getLayoutY());
                adresseTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                adresseTextField.setPrefWidth(adresseLabel.getWidth());


                adresseTextField.setOnAction(e -> {
                    adresseLabel.setText(adresseTextField.getText());
                    cinema.setAdresse(adresseTextField.getText());
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    card.getChildren().remove(adresseTextField);
                });


                card.getChildren().add(adresseTextField);

                adresseTextField.requestFocus();
                adresseTextField.selectAll();
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Voulez-vous vraiment supprimer ce cinéma ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CinemaService cinemaService = new CinemaService();
                cinemaService.delete(cinema);

                cardContainer.getChildren().remove(card);
            }
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
                    return new TableCell<Salle, Void>() {
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
                    TextField textField = new TextField(getItem());
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

        FontAwesomeIconView facebookIcon = new FontAwesomeIconView();
        facebookIcon.setGlyphName("FACEBOOK");
        facebookIcon.setSize("3.5em");
        facebookIcon.setLayoutX(270);
        facebookIcon.setLayoutY(100);
        facebookIcon.setFill(Color.WHITE);
        facebookIcon.setOnMouseClicked(event -> {
            facebookAnchor.setVisible(true);
        });

        card.getChildren().addAll(facebookIcon);

        card.getChildren().addAll(SalleCircle, salleIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    @FXML
    private void showCinemaList() {
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
        colMovie.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Seance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Seance, String> seanceStringCellDataFeatures) {
                return new SimpleStringProperty(seanceStringCellDataFeatures.getValue().getFilmcinema().getId_film().getNom());
            }
        });
        colCinema.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Seance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Seance, String> seanceStringCellDataFeatures) {
                return new SimpleStringProperty(seanceStringCellDataFeatures.getValue().getFilmcinema().getId_cinema().getNom());
            }
        });
        colMovieRoom.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDepartTime.setCellValueFactory(new PropertyValueFactory<>("HD"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("HF"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colAction.setCellFactory(new Callback<TableColumn<Seance, Void>, TableCell<Seance, Void>>() {
            @Override
            public TableCell<Seance, Void> call(TableColumn<Seance, Void> param) {
                return new TableCell<Seance, Void>() {
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
                            setGraphic(new HBox(deleteButton));
                        }
                    }
                };
            }
        });

        SessionTableView.setEditable(true);

        colPrice.setCellFactory(tc -> new TableCell<Seance, Double>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
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
                    commitEdit(Double.parseDouble(textField.getText()));
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
            public void commitEdit(Double newValue) {
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

                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        DatePicker datePicker = new DatePicker();

                        if (!isEmpty() && getItem() != null) {
                            datePicker.setValue(getItem().toLocalDate());
                        }

                        datePicker.setOnAction(e -> {
                            LocalDate selectedDate = datePicker.getValue();
                            if (selectedDate != null) {
                                Date newDate = Date.valueOf(selectedDate);
                                commitEdit(newDate);
                            }
                        });

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

                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
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
                                    seance.getFilmcinema().setId_cinema(cinema);
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
                        Cinema selectedCinema = seance.getFilmcinema().getId_cinema();

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
                        Cinema selectedCinema = seance.getFilmcinema().getId_cinema();

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
                                    seance.getFilmcinema().setId_film(film);
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
                FilmcinemaService filmService = new FilmcinemaService();
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
            showAlert("Please complete all fields.");
            return;
        }

        // Vérifier que les champs de l'heure de début et de fin sont au format heure
        try {
            Time.valueOf(LocalTime.parse(departureTimeText));
            Time.valueOf(LocalTime.parse(endTimeText));
        } catch (DateTimeParseException e) {
            showAlert("The Start Time and End Time fields must be in the format HH:MM:SS.");
            return;
        }

        // Vérifier que le champ price contient un nombre réel
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showAlert("The price must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("The Price field must be a real number.");
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

        double price = Double.parseDouble(priceText);

        Seance newSeance = new Seance(selectedRoom, departureTime, endTime, date, price, new Filmcinema(selectedFilm, selectedCinema));

        SeanceService seanceService = new SeanceService();
        seanceService.create(newSeance);

        showAlert("Session added successfully!");
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
        // Vérifier que tous les champs sont remplis
        if (tfNbrPlaces.getText().isEmpty() || tfNomSalle.getText().isEmpty()) {
            showAlert("please complete all fields!");
            return;
        }


        try {
            int nombrePlaces = Integer.parseInt(tfNbrPlaces.getText());
            if (nombrePlaces <= 0) {
                showAlert("The number of places must be a positive integer!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("The number of places must be an integer!");
            return;
        }

        SalleService ss = new SalleService();
        ss.create(new Salle(cinemaId, Integer.parseInt(tfNbrPlaces.getText()), tfNomSalle.getText()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added room");
        alert.setContentText("Added room!");
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
            showAlert("No rooms are available");
            return;
        }

        ObservableList<Salle> salleInfos = FXCollections.observableArrayList(salles_cinema);

        RoomTableView.setItems(salleInfos);
    }

    @FXML
    void closeAnchor(ActionEvent event) {
        facebookAnchor.setVisible(false);
    }

    @FXML
    void PublierStatut(ActionEvent event) {
        String message = txtareaStatut.getText();
        String accessToken = "EAAQzq3ZC1QRwBO1ANXqPJE0gbGdvugxiIwh4y5UuB4H9touxQpQaZBzDQ8gwewD4JVRMUzqOwbDmsrC8EMYRb19deQAEhWFX7uQJAcOIAnBcpHx1JnbNgMITZCq55N6ZCppxZBmHAS1itmrSt9B4aCQbNsP3AMi6mXZAJZAwaZAXCe72fP6OuzjWZAgdUgZAygeFsZD";
        String url = "https://graph.facebook.com/v19.0/me/feed";
        String data = "message=" + message + "&access_token=" + accessToken;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void AfficherFilmResponsable(ActionEvent event) throws IOException {
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


}

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
import java.net.URI;
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
/**
 * Is responsible for handling user interactions related to the dashboard section of
 * the application. It provides functionality such as displaying the room form, back
 * button, and cinema list pane, as well as handling events related to showing sessions
 * and importing images. The class also includes methods for loading seances and
 * showing session forms.
 */
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
    private FontAwesomeIconView backButton;
    @FXML
    private Button sessionButton;
    @FXML
    private PieChart pieChart;
    @FXML
    private FontAwesomeIconView backSession;
    /**
     * Sets the value of the `responsableDeCinema` field to the input parameter `resp`.
     * 
     * @param resp Responsable_de_cinema object that will be associated with the method's
     * caller, thereby transferring ownership of the object to the method caller.
     */
    public void setData(Responsable_de_cinema resp) {
        this.responsableDeCinema = resp;
    }
    /**
     * Creates an Alert object and displays a message in it using the `show()` method.
     * 
     * @param message text to be displayed as an alert message when the `showAlert()`
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
     * Allows users to input cinema details, including name and address. If fields are
     * empty, an alert is displayed. Then, a responsible cinema object is created based
     * on ID, and the CinemaService creates a new cinema object using the provided details.
     * 
     * @param event action event triggered by the user's click on the "Add Cinema" button,
     * which initiates the functionality of the function.
     * 
     * 	- `tfNom`: A text field containing the name of the cinema.
     * 	- `tfAdresse`: A text field containing the address of the cinema.
     */
    @FXML
    void addCinema(ActionEvent event) {
        if (tfNom.getText().isEmpty() || tfAdresse.getText().isEmpty()) {
            showAlert("Please complete all fields!");
            return;
        }
        String defaultStatut = "Pending";
        // Fetch the responsible cinema by its ID
        Responsable_de_cinema responsableDeCinema = (Responsable_de_cinema) tfNom.getScene().getWindow().getUserData();
        URI uri = null;
        try {
            String fullPath = image.getImage().getUrl();
            String requiredPath = fullPath.substring(fullPath.indexOf("/img/cinemas/"));
            uri = new URI(requiredPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // Create the cinema object
        Cinema cinema = new Cinema(tfNom.getText(), tfAdresse.getText(), responsableDeCinema, uri.getPath(), defaultStatut);
        // Call the CinemaService to create the cinema
        CinemaService cs = new CinemaService();
        cs.create(cinema);
        showAlert("Cinema added successfully!");
    }
    /**
     * Allows the user to select an image file, which is then copied to a specified
     * directory and set as the image for a `Image` component.
     * 
     * @param event mouse event that triggered the function execution, providing the
     * necessary information to determine the appropriate action to take.
     * 
     * 	- `event`: A `MouseEvent` object representing the user's action that triggered
     * the function.
     */
    @FXML
    void selectImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory = "./src/main/resources/img/cinemas/";
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
    /**
     * Loads accepted cinemas and sets pane visibility, adds cinema names to a combo box,
     * and listens for selection changes to load movies and rooms for the selected cinema.
     * 
     * @param location URL of the initial page to load, which in this case is the home
     * page with the list of cinemas.
     * 
     * 	- `location`: A `URL` object representing the location of the application.
     * 	- `resources`: A `ResourceBundle` object containing localized messages and data
     * for the application.
     * 
     * @param resources ResourceBundle containing the translation keys for the application's
     * messages, which are used to display the labels and values in the user interface.
     * 
     * 	- `location`: represents the URL of the web page being loaded.
     * 	- `resources`: contains resource bundles for displaying messages and other
     * information to the user.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashSet<Cinema> acceptedCinemas = loadAcceptedCinemas();
        cinemaFormPane.setVisible(true);
        sessionFormPane.setVisible(false);
        cinemaListPane.setVisible(true);
        SessionTableView.setVisible(false);
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);
        sessionButton.setVisible(true);
        backSession.setVisible(false);
        backButton.setVisible(false);
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
    /**
     * Clears the list of movies for a specified cinema and then reads the movies from
     * the FilmcinemaService, adding them to the combo movie list.
     * 
     * @param cinemaId unique identifier of the cinema for which the movies are to be loaded.
     */
    private void loadMoviesForCinema(int cinemaId) {
        comboMovie.getItems().clear();
        FilmcinemaService fs = new FilmcinemaService();
        List<Film> moviesForCinema = fs.readMoviesForCinema(cinemaId);
        for (Film f : moviesForCinema) {
            System.out.println("moviesForCinema: " + f);
            comboMovie.getItems().add(f.getNom());
        }
    }
    /**
     * Clears the items of a `JList` called `comboRoom`, then reads the rooms for a given
     * cinema using the `SalleService`, and adds the room names to the list.
     * 
     * @param cinemaId id of the cinema for which the rooms are being loaded.
     */
    private void loadRoomsForCinema(int cinemaId) {
        comboRoom.getItems().clear();
        SalleService ss = new SalleService();
        List<Salle> roomsForCinema = ss.readRoomsForCinema(cinemaId);
        for (Salle s : roomsForCinema) {
            comboRoom.getItems().add(s.getNom_salle());
        }
    }
    /**
     * Loads a set of accepted cinemas from a CinemaService and displays them as cards
     * on a flow pane.
     * 
     * @returns a set of Cinema objects representing the accepted cinemas.
     * 
     * 	- `HashSet<Cinema>` represents a set of accepted cinemas in the system.
     * 	- The set contains only cinemas with a "Accepted" status.
     * 	- The list of cinemas is collected from the `read()` method of the `CinemaService`
     * class.
     * 	- The `HBox` objects created for each cinema are added to the `cinemaFlowPane` component.
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Accepted"))
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
    /**
     * Retrieves a list of cinemas from a service, filters them based on their status,
     * and returns a set of accepted cinemas.
     * 
     * @returns a hash set of Cinema objects that represent accepted cinemas.
     * 
     * 1/ The output is a `HashSet` containing only cinemas that have a `Statut` equal
     * to "Accepted".
     * 2/ The `HashSet` contains only a subset of the original list of cinemas, specifically
     * those that meet the filter condition.
     * 3/ The size of the `HashSet` is either zero or the number of cinemas that meet the
     * filter condition, depending on whether any cinemas have a `Statut` equal to "Accepted".
     */
    private HashSet<Cinema> chargerAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Accepted"))
                .collect(Collectors.toList());
        if (acceptedCinemasList.isEmpty()) {
            showAlert("No accepted cinemas are available.");
        }
        HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        return acceptedCinemasSet;
    }
    /**
     * Creates a card that displays a cinema's details, including its name, capacity, and
     * delete button. It also includes a Facebook icon and anchor for opening the cinema's
     * Facebook page.
     * 
     * @param cinema cinema object that will be deleted or updated, and is used to access
     * its properties and methods in the function.
     * 
     * 	- `id_cinema`: the unique identifier of the cinema
     * 	- `nom_cinema`: the name of the cinema
     * 	- `adresse_cinema`: the address of the cinema
     * 	- `capacite_cinema`: the capacity of the cinema.
     * 
     * @returns a Card object containing a Circle and an FontAwesomeIconView, representing
     * a cinema.
     * 
     * 	- `card`: The root element of the card that contains information about a cinema.
     * 	- `SalleCircle`: A circle with a radius of 30 pixels used to represent the cinema's
     * capacity.
     * 	- `facebookIcon`: An instance of `FontAwesomeIconView` representing the Facebook
     * logo.
     * 	- `facebookAnchor`: An instance of `Hyperlink` that displays the Facebook page
     * for the cinema.
     * 	- `circlefacebook`: A circle with a radius of 30 pixels used to represent the
     * Facebook logo.
     * 	- `salleIcon`: An instance of `FontAwesomeIconView` representing the building
     * icon used to indicate the cinema's location.
     * 	- `cardContainer`: The container element that holds the card containing information
     * about the cinema.
     */
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
        try {
            if (!cinema.getLogo().isEmpty()) {
                image = new Image(cinema.getLogo());
            } else {
                image = new Image("Logo.png");
            }
        } catch (Exception e) {
            System.out.println("line 335 " + e.getMessage());
            image = new Image("Logo.png");
        }
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
        verticalLine.setEndY(110);
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
            backButton.setVisible(true);
            sessionButton.setVisible(false);
            colNameRoom.setCellValueFactory(new PropertyValueFactory<>("nom_salle"));
            colNbrPlaces.setCellValueFactory(new PropertyValueFactory<>("nb_places"));
            colActionRoom.setCellFactory(new Callback<TableColumn<Salle, Void>, TableCell<Salle, Void>>() {
                /**
                 * Generates a `TableCell` instance that displays a button for deleting a room from
                 * a `TableView`. When the button is clicked, the function calls the `SalleService`
                 * to delete the room and then removes it from the `TableView`.
                 * 
                 * @param param TableColumn object that triggered the cell's action, and it is used
                 * to get the index of the room in the table view.
                 * 
                 * 	- `param`: A `TableColumn<Salle, Void>` object representing a column in a table.
                 * 
                 * @returns a `TableCell` object that displays a delete button for each room in the
                 * table.
                 * 
                 * 	- The output is a `TableCell` object, which represents a cell in a table.
                 * 	- The cell contains a button with the label "Delete".
                 * 	- The button has an `OnAction` handler that deletes the item at the specified
                 * position in the table when clicked.
                 */
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
                        /**
                         * Updates an item's graphic based on whether it is empty or not. If the item is
                         * empty, the function sets its graphic to null. Otherwise, it sets the graphic to a
                         * new HBox containing a delete room button.
                         * 
                         * @param item Void to be updated, which is then passed to the superclass's `updateItem()`
                         * method for further processing.
                         * 
                         * 	- `item`: A void value representing an item to be updated.
                         * 	- `empty`: A boolean value indicating whether the item is empty or not.
                         * 
                         * @param empty status of the item being updated, with `true` indicating that the
                         * item is empty and `false` indicating otherwise.
                         */
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
                /**
                 * Updates an item's quantity based on user input. If the quantity is null or empty,
                 * it sets the text to null. Otherwise, it sets the text to the updated quantity.
                 * 
                 * @param nb_salles number of sales, which is used to set the text value of the
                 * `setText()` method call.
                 * 
                 * 	- `nb_salles` represents the number of places available for renting.
                 * 	- It can be null or an integer value.
                 * 	- When it is not null, it signifies that there are available places for renting.
                 * 
                 * @param empty state of the item, with a value of `true` indicating an empty item
                 * and a value of `false` indicating an item with a number of places.
                 */
                @Override
                protected void updateItem(Integer nb_salles, boolean empty) {
                    super.updateItem(nb_salles, empty);
                    if (empty || nb_salles == null) {
                        setText(null);
                    } else {
                        setText(nb_salles + " places");
                    }
                }
                /**
                 * 1) calls super's `startEdit`, 2) checks if the item is empty, and 3) creates a
                 * `TextField` with the item's value and sets an `OnAction` listener to commit the
                 * edit when the user types something.
                 */
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
                /**
                 * In Java is used to cancel any ongoing editing activity and reset the text and
                 * graphic properties of an object to their original values.
                 */
                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getItem() + " places");
                    setGraphic(null);
                }
                /**
                 * Updates the number of places in a `Salle` object based on a user input, then calls
                 * the super method to commit the change, and sets the text and graphic of the cell
                 * to display the updated value.
                 * 
                 * @param newValue new value of the number of places for the salle to be updated by
                 * the `SalleService`.
                 * 
                 * 	- `Integer newValue`: The new value for the number of places in a salle.
                 */
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
                /**
                 * Updates an item's text based on whether it is empty or not, and sets the text to
                 * the hall name if it is not empty.
                 * 
                 * @param nom_salle name of the hall to be updated, which is passed to the superclass's
                 * `updateItem()` method and then further processed based on its value.
                 * 
                 * @param empty whether the salle is empty or not, and triggers the appropriate text
                 * display in the `updateItem` method.
                 */
                @Override
                protected void updateItem(String nom_salle, boolean empty) {
                    super.updateItem(nom_salle, empty);
                    if (empty || nom_salle == null) {
                        setText(null);
                    } else {
                        setText(nom_salle);
                    }
                }
                /**
                 * Initializes a new `TextField` instance and sets its text to the current item's
                 * value. It also sets an action listener on the `TextField` that calls the `commitEdit`
                 * method when the user presses enter or clicks outside the field. Finally, it replaces
                 * the `TextField` with the newly created instance in the graphical representation
                 * of the editor.
                 */
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
                /**
                 * In Java overrides the parent `cancelEdit()` method and sets the text and graphic
                 * properties of an object to their original values.
                 */
                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getItem());
                    setGraphic(null);
                }
                /**
                 * Updates a salle object's nom_salle property by calling the `update` method of the
                 * SalleService class, and then sets the new value for the nom_salle property of the
                 * salle object.
                 * 
                 * @param newValue new value of the `nom_salle` field for the selected `Salle` object
                 * in the `TableView`.
                 */
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
        Circle circlefacebook = new Circle();
        circlefacebook.setRadius(30);
        circlefacebook.setLayoutX(320);
        circlefacebook.setLayoutY(100);
        circlefacebook.setFill(Color.web("#ae2d3c"));
        FontAwesomeIconView facebookIcon = new FontAwesomeIconView();
        facebookIcon.setGlyphName("FACEBOOK");
        facebookIcon.setSize("3.5em");
        facebookIcon.setLayoutX(310);
        facebookIcon.setLayoutY(120);
        facebookIcon.setFill(Color.WHITE);
        facebookIcon.setOnMouseClicked(event -> {
            facebookAnchor.setVisible(true);
        });
        card.getChildren().addAll(circlefacebook, facebookIcon);
        card.getChildren().addAll(SalleCircle, salleIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }
    /**
     * Sets the visible state of various panes and tables within a JavaFX application,
     * making the cinema list pane visible and the other components hidden.
     */
    @FXML
    private void showCinemaList() {
        cinemaFormPane.setVisible(true);
        sessionFormPane.setVisible(false);
        cinemaListPane.setVisible(true);
        SessionTableView.setVisible(false);
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);
    }
    /**
     * Is responsible for creating and displaying a form within a table cell to allow
     * users to edit the cinema's information, including its name and address, as well
     * as the names of the salle and film associated with it.
     */
    @FXML
    private void showSessionForm() {
        cinemaFormPane.setVisible(false);
        sessionFormPane.setVisible(true);
        cinemaListPane.setVisible(false);
        SessionTableView.setVisible(true);
        addRoomForm.setVisible(false);
        RoomTableView.setVisible(false);
        colMovie.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Seance, String>, ObservableValue<String>>() {
            /**
             * Generates a `SimpleStringProperty` observable value from the `getValue()` of the
             * `Seance` object, which contains the film ID as a string.
             * 
             * @param seanceStringCellDataFeatures cell data features of a Seance object, which
             * contains the value of the film's ID.
             * 
             * @returns a `SimpleStringProperty` object representing the film's ID.
             */
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Seance, String> seanceStringCellDataFeatures) {
                return new SimpleStringProperty(seanceStringCellDataFeatures.getValue().getFilmcinema().getId_film().getNom());
            }
        });
        colCinema.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Seance, String>, ObservableValue<String>>() {
            /**
             * Takes a `TableColumn.CellDataFeatures` object as input and returns an `ObservableValue`
             * of type `String`, which represents the cinema ID.
             * 
             * @param seanceStringCellDataFeatures cell data features of a table column containing
             * strings that correspond to the cinema ID of the movie being displayed.
             * 
             * @returns a `SimpleStringProperty` representing the cinema ID.
             */
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
            /**
             * Generates a `TableCell` that displays a button for deleting a `Seance` object.
             * When the button is clicked, the `SeanceService` service is invoked to delete the
             * `Seance`, and the cell's graphic is updated to show null when there is no item or
             * an icon button when an item exists.
             * 
             * @param param TableColumn that the function is called on, allowing the function to
             * modify its behavior based on the context of the table it is a part of.
             * 
             * 	- `param`: A `TableColumn<Seance, Void>` object representing a table column.
             * 
             * @returns a `TableCell` object that displays a "Delete" button for each item in the
             * table.
             * 
             * 	- `TableCell<Seance, Void>`: The type of the cell, indicating that it is a table
             * cell for objects of type `Seance` and void.
             * 	- `Button deleteButton`: A button with the text "Delete", which when clicked will
             * call the `setOnAction` method to delete the corresponding `Seance` object.
             * 	- `SeanceService seanceService`: An instance of the `SeanceService` class, which
             * is used to delete the `Seance` object.
             * 	- `getTableView().getItems()`: A method that returns a list of all `Seance` objects
             * in the table.
             * 	- `getIndex()`: A method that returns the index of the `Seance` object in the list.
             * 	- `updateItem(Void item, boolean empty)`: A method that updates the graphics of
             * the cell based on whether the `item` is null or not. If `item` is null, the graphics
             * are set to null, otherwise they are set to a new `HBox` containing the `deleteButton`.
             */
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
                    /**
                     * Updates an item's graphic based on whether it is empty or not. If the item is
                     * empty, the function sets the graphic to null. Otherwise, it sets the graphic to a
                     * new HBox containing a delete button.
                     * 
                     * @param item Void item being updated, which is passed to the superclass's `updateItem()`
                     * method along with a boolean value indicating whether the item is empty or not.
                     * 
                     * `item`: A Void object representing an item to be updated. Its main property is
                     * whether it is empty or not.
                     * 
                     * @param empty ether the item is empty or not, and it is used to determine the
                     * graphics displayed in the updateItem method.
                     */
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
            /**
             * Updates an item's price and emptiness status. If the item is empty or the price
             * is null, the text is set to null. Otherwise, the text is set to the price plus "DT".
             * 
             * @param prix price of the item being updated, which is used to set the text value
             * of the component.
             * 
             * 	- `prix` is a double value that represents the price of an item.
             * 	- It can be either `null` or a non-null value indicating whether the item is empty
             * or not.
             * 
             * @param empty state of the item, and when it is `true`, the `setText()` method sets
             * the text to `null`.
             */
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(prix + " DT");
                }
            }
            /**
             * Initializes a `TextField` widget with the value of the currently selected item,
             * sets an `OnAction` listener to commit the edit when the user types something, and
             * sets the `Text` field to null to indicate that editing is in progress.
             */
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
            /**
             * In the provided Java code cancels the editing state of an item and sets the text
             * and graphic properties accordingly.
             */
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem() + " DT");
                setGraphic(null);
            }
            /**
             * Updates a `Seance` object's `prix` field with a new value, and then calls the
             * `update` method of the `SeanceService` class to save the changes. The function
             * also updates the display value of the cell to show the new value.
             * 
             * @param newValue updated price of the seance that is being edited, which is then
             * set to the `prix` field of the corresponding `Seance` object and saved in the
             * database through the `update()` method of the `SeanceService`.
             * 
             * 	- `Double`: `newValue` is a `Double` value representing the new price for the seance.
             */
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
            /**
             * Updates an item's text based on whether it is empty or contains a valid value from
             * the `Time` class.
             * 
             * @param HF time value that is to be updated in the text field, and its value
             * determines whether the text field's text is set to null or the string representation
             * of the time value.
             * 
             * 	- `HF` is a `Time` object representing a specific moment in time.
             * 	- It can be either `null` or a non-`null` value indicating the presence of an
             * item at that time.
             * 
             * @param empty state of the item being updated, with `true` indicating an empty state
             * and `false` indicating otherwise.
             */
            @Override
            protected void updateItem(Time HF, boolean empty) {
                super.updateItem(HF, empty);
                if (empty || HF == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(HF));
                }
            }
            /**
             * Initializes a `TextField` component with the value of the current item, sets an
             * `OnAction` listener to commit the edit when the user types something, and replaces
             * the text field with the one created.
             */
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
            /**
             * In Java overrides the parent method and performs the following actions: cancels
             * editing, sets the text to the original value, and sets the graphic to null.
             */
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(String.valueOf(getItem()));
                setGraphic(null);
            }
            /**
             * Updates a seance's high fidelity value based on a new value provided, and saves
             * the changes to the seance in the database using the `SeanceService`.
             * 
             * @param newValue updated value of the `HF` field for the corresponding `Seance`
             * object in the `getTableView().getItems()` collection, which is then updated in the
             * database through the `SeanceService`.
             * 
             * 	- `Time newValue`: Represents a time value that represents the updated seance duration.
             */
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
            /**
             * Updates an item's text based on whether it is empty or contains a valid value from
             * the `Time` object passed as a parameter.
             * 
             * @param HD time value to be updated, which is passed through to the superclass's
             * `updateItem()` method and then processed further in the current implementation.
             * 
             * 	- If `empty` is true or `HD` is null, then `setText` method sets the text to null.
             * 	- Otherwise, `setText` method sets the text to a string representation of `HD`.
             * 
             * @param empty whether the time is empty or not, and determines whether the `setText()`
             * method should be called with a null value or the string representation of the time.
             */
            @Override
            protected void updateItem(Time HD, boolean empty) {
                super.updateItem(HD, empty);
                if (empty || HD == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(HD));
                }
            }
            /**
             * 1) calls superclass `startEdit`, 2) checks if the object is empty, and 3) creates
             * a new `TextField` with the object's value as its text.
             */
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
            /**
             * In Java overrides the parent method and performs two actions: first, it calls the
             * superclass's `cancelEdit` method; second, it sets the text field to the original
             * value of the item and removes any graphic associated with the editable component.
             */
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(String.valueOf(getItem()));
                setGraphic(null);
            }
            /**
             * Updates the `HD` field of a `Seance` object in a table view, and then calls the
             * `update` method of the `SeanceService` class to persist the changes.
             * 
             * @param newValue new time value that will be assigned to the `HD` field of the
             * `Seance` object referenced by the `getTableView().getItems().get(getIndex());`
             * method call.
             * 
             * 	- It represents a time value that has been edited by the user.
             * 	- It is an instance of the `Time` class in Java, which represents time values in
             * milliseconds since the Unix epoch (January 1, 1970, 00:00:00 UTC).
             */
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
            /**
             * Updates an item's text based on whether it is empty or not, and adds a mouse click
             * event listener that displays a date picker when clicked twice, allowing the user
             * to select a date which is then committed as the item's value.
             * 
             * @param date date to be updated or retrieved, which is passed to the super method
             * `updateItem()` and used to set the text value of the item.
             * 
             * 	- `date` can be either `null` or a `Date` object representing a specific date and
             * time.
             * 	- If `empty` is `true`, then `date` will be `null`.
             * 	- The `toString()` method is called on `date` to obtain its string representation,
             * which is assigned to the `setText()` method's argument.
             * 
             * @param empty presence or absence of a value for the item being updated, and
             * determines whether the `setText()` method is called with a null value or the date
             * string representation when the item is empty.
             */
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
            /**
             * Updates a `Seance` object's `Date` field by calling the superclass's `commitEdit`
             * method, then setting the updated value to the `Seance` object and saving it to the
             * database using the `SeanceService`.
             * 
             * @param newValue new date to be updated for the corresponding `Seance` object in
             * the `getTableView()` method.
             * 
             * 	- `Date`: represents the date to be updated for the corresponding seance in the
             * table view.
             */
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
            /**
             * Updates a cell's value in a `TableView` based on user input, displaying a `ComboBox`
             * with available cinema names and handling selection changes to update the cell value
             * and database entry for the corresponding cinema.
             * 
             * @param cinemaName name of the cinema to which the cell's value will be updated,
             * and it is used to determine whether to display the ComboBox or not, as well as to
             * set the value of the ComboBox when the user clicks twice on the cell.
             * 
             * @param empty empty state of the ` cinemaName ` field, which determines whether to
             * display or hide the ComboBox containing the list of cinemas when the cell is clicked.
             */
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
            /**
             * Updates the value of a cell in a table view based on user input. It creates a
             * ComboBox to display associated salle names and selects the corresponding salle
             * name upon second click.
             * 
             * @param salleName name of the salle to be updated, which is used to set the text
             * value of the cell or to select the corresponding salle from a combo box when the
             * user double-clicks on the cell.
             * 
             * @param empty absence of a salle name or a null reference, which triggers the
             * corresponding actions in the function, such as setting the text to null or displaying
             * the ComboBox with associated salle names.
             */
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
            /**
             * Retrieves a list of `Salle` objects associated with a given cinema ID using the `SalleService`.
             * 
             * @param idCinema ID of the cinema for which the associated salles are to be loaded.
             * 
             * @returns a list of `Salle` objects associated with the specified cinema id.
             */
            private List<Salle> loadAssociatedSalles(int idCinema) {
                SalleService salleService = new SalleService();
                return salleService.readRoomsForCinema(idCinema);
            }
        });
        colMovie.setCellFactory(tc -> new TableCell<Seance, String>() {
            /**
             * Updates the item value in a TableView based on user input, and displays a ComboBox
             * containing film names associated with the selected cinema. When the user double-clicks
             * on the cell, the ComboBox is displayed, and the user can select a film name to
             * update the item value and display the corresponding film name in the TableView.
             * 
             * @param filmName name of the film to be updated in the cinema's database, which is
             * used to set the value of the `setText()` method and trigger the event handler for
             * the ComboBox.
             * 
             * @param empty value of the `filmName` field when it is left blank or null, and it
             * determines whether to display a message or not when the user clicks twice on the
             * cell.
             */
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
            /**
             * Retrieves a list of films associated with a given cinema ID using the `readMoviesForCinema`
             * method provided by the `FilmcinemaService`.
             * 
             * @param idCinema unique identifier of the cinema for which associated films are to
             * be loaded.
             * 
             * @returns a list of movies associated with the given cinema ID.
             */
            private List<Film> loadAssociatedFilms(int idCinema) {
                FilmcinemaService filmService = new FilmcinemaService();
                return filmService.readMoviesForCinema(idCinema);
            }
        });
        loadSeances();
    }
    /**
     * Allows users to input cinema, film and room information, as well as a start and
     * end time, and price. It then creates a new seance in the SeanceService with the
     * relevant details.
     */
    @FXML
    void addSeance() {
        String selectedCinemaName = comboCinema.getValue();
        String selectedFilmName = comboMovie.getValue();
        String selectedRoomName = comboRoom.getValue();
        LocalDate selectedDate = dpDate.getValue();
        String departureTimeText = tfDepartureTime.getText();
        String endTimeText = tfEndTime.getText();
        String priceText = tfPrice.getText();
        if (selectedCinemaName == null || selectedFilmName == null || selectedRoomName == null || selectedDate == null
                || departureTimeText.isEmpty() || endTimeText.isEmpty() || priceText.isEmpty()) {
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
        showSessionForm();
    }
    /**
     * Retrieves a list of `Seance` objects from an external service, converts it to an
     * observable list, and sets it as the items of a view.
     * 
     * @returns a list of Seance objects.
     * 
     * 1/ List<Seance>: This is the type of the returned output, indicating that it is a
     * list of `Seance` objects.
     * 2/ SeanceService: The class used to read the seance data, which is likely a database
     * or API call.
     * 3/ read(): The method called on the `SeanceService` instance to retrieve the seance
     * data.
     * 4/ List<Seance>: The list of `Seance` objects returned by the `read()` method.
     * 5/ ObservableList<Seance>: An observable list of `Seance` objects, which means
     * that the list can be modified through operations such as adding, removing, or
     * modifying elements.
     */
    private List<Seance> loadSeances() {
        SeanceService seanceService = new SeanceService();
        List<Seance> seances = seanceService.read();
        ObservableList<Seance> seanceObservableList = FXCollections.observableArrayList(seances);
        SessionTableView.setItems(seanceObservableList);
        return seances;
    }
    /**
     * Verifies that all fields are filled, and then creates a new room in the cinema's
     * database with the provided number of places and name, displaying an alert message
     * after successful creation.
     * 
     * @param event event of a button click and triggers the execution of the code within
     * the function.
     * 
     * 	- `event` is an `ActionEvent`, indicating that the method was called as a result
     * of user action.
     */
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
    /**
     * Reads salle data from a service, filters them based on cinema Id, and displays the
     * available rooms in a list view.
     */
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
    /**
     * Makes the `facebookAnchor` component invisible when the `Facebook` button is clicked.
     * 
     * @param event occurrence of a button click event that triggered the function execution.
     */
    @FXML
    void closeAnchor(ActionEvent event) {
        facebookAnchor.setVisible(false);
    }
    /**
     * Posts a status update to Facebook using an access token and message from a text area.
     * 
     * @param event action that triggered the function execution, providing the necessary
     * context for the code to perform its intended task.
     * 
     * 	- `txtareaStatut`: This is a text area where the status message to be published
     * is entered by the user.
     */
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
    /**
     * Loads an FXML file, creates a new stage and replaces the current stage with it,
     * displaying the contents of the FXML file on the screen.
     * 
     * @param event event that triggered the function, specifically the button click event
     * that initiates the display of the film management interface.
     * 
     * 	- `event` represents an ActionEvent object, which carries information about the
     * action that triggered the function.
     */
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
    /**
     * Makes the `addRoomForm`, `backButton`, `RoomTableView`, and `cinemaFormPane`
     * invisible, while making the `sessionButton` visible, when a user clicks the back
     * button.
     * 
     * @param event mouse event that triggered the execution of the `back` method.
     * 
     * Event type: `MouseEvent`
     * Target element: `backButton`
     */
    @FXML
    void back(MouseEvent event) {
        addRoomForm.setVisible(false);
        backButton.setVisible(false);
        RoomTableView.setVisible(false);
        cinemaFormPane.setVisible(true);
        cinemaListPane.setVisible(true);
        sessionButton.setVisible(true);
    }
    /**
     * Makes the `sessionFormPane`, `SessionTableView`, and `backSession` components
     * visible, while hiding `cinemaFormPane`, `cinemaListPane`, and `sessionButton`. It
     * also calls `loadSeances()` and `showSessionForm()` to display the session form and
     * content.
     * 
     * @param event occurrence of an action, triggering the execution of the `showSessions()`
     * method.
     * 
     * 	- `event` is an `ActionEvent` object representing the user action that triggered
     * the function.
     */
    @FXML
    void showSessions(ActionEvent event) {
        cinemaFormPane.setVisible(false);
        cinemaListPane.setVisible(false);
        sessionFormPane.setVisible(true);
        SessionTableView.setVisible(true);
        sessionButton.setVisible(false);
        backSession.setVisible(true);
        loadSeances();
        showSessionForm();
        System.out.println(loadSeances());
    }
    /**
     * Sets the visibility of various components in a JavaFX application, including the
     * `cinemaFormPane`, `cinemaListPane`, `sessionFormPane`, and `SessionTableView`. It
     * makes these components visible or invisible based on a user input event.
     * 
     * @param event mouse event that triggered the `back2()` method, providing information
     * about the location and type of the event.
     * 
     * 	- Type: `MouseEvent`
     * 	- Target: `cinemaFormPane` or `sessionButton` (depending on the location of the
     * click)
     * 	- Code: The button that was clicked (either `cinemaFormPane` or `sessionButton`)
     */
    @FXML
    void back2(MouseEvent event) {
        cinemaFormPane.setVisible(true);
        cinemaListPane.setVisible(true);
        sessionFormPane.setVisible(false);
        SessionTableView.setVisible(false);
        sessionButton.setVisible(true);
    }
    /**
     * Allows the user to select an image file, then copies it to a specified directory
     * and sets the selected image as the `image` field.
     * 
     * @param event mouse event that triggered the `importImage()` method and provides
     * the location of the selected file through its `FileChooser` object.
     * 
     * 	- `event` is a `MouseEvent` object representing a user's interaction with the application.
     */
    @FXML
    void importImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/cinemas/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\cinemas\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                image.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

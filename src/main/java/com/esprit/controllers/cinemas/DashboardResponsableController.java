package com.esprit.controllers.cinemas;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.models.users.CinemaManager;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.FilmCinemaService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;

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

/**
 * Controller responsible for handling cinema manager dashboard operations.
 * 
 * <p>
 * This controller provides functionality for cinema managers to manage their
 * cinemas,
 * including adding new cinemas, managing cinema halls, scheduling movie
 * sessions,
 * and publishing to social media. It handles the cinema management interface
 * for
 * responsible users.
 * </p>
 * 
 * <p>
 * Key features include:
 * - Cinema creation and management
 * - Cinema hall management with capacity tracking
 * - Movie session scheduling and pricing
 * - Image selection and upload for cinema logos
 * - Facebook integration for social media posting
 * </p>
 * 
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 * @see com.esprit.models.cinemas.Cinema
 * @see com.esprit.models.cinemas.CinemaHall
 * @see com.esprit.models.cinemas.MovieSession
 * @see com.esprit.models.users.CinemaManager
 */
public class DashboardResponsableController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(DashboardResponsableController.class.getName());

    CinemaManager cinemaManager;
    private String cloudinaryImageUrl;
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
    private TableView<MovieSession> SessionTableView;
    @FXML
    private TableColumn<MovieSession, Void> colAction;
    @FXML
    private TableColumn<MovieSession, String> colCinema;
    @FXML
    private TableColumn<MovieSession, Date> colDate;
    @FXML
    private TableColumn<MovieSession, Time> colDepartTime;
    @FXML
    private TableColumn<MovieSession, Time> colEndTime;
    @FXML
    private TableColumn<MovieSession, String> colMovie;
    @FXML
    private TableColumn<MovieSession, String> colMovieRoom;
    @FXML
    private TableColumn<MovieSession, Double> colPrice;
    @FXML
    private AnchorPane addRoomForm;
    @FXML
    private TextField tfNbrPlaces;
    @FXML
    private TextField tfNomCinemaHall;
    private Long cinemaId;
    @FXML
    private TableView<CinemaHall> RoomTableView;
    @FXML
    private TableColumn<CinemaHall, Void> colActionRoom;
    @FXML
    private TableColumn<CinemaHall, String> colNameRoom;
    @FXML
    private TableColumn<CinemaHall, Integer> colNbrPlaces;
    @FXML
    private AnchorPane facebookAnchor;
    @FXML
    private TextArea txtareaStatut;
    @FXML
    private FontAwesomeIconView backButton;
    @FXML
    private Button sessionButton;
    @FXML
    private FontAwesomeIconView backSession;

    /**
     * Sets the cinema manager data for this controller.
     *
     * @param resp the CinemaManager object to be associated with this controller
     * @since 1.0
     */
    public void setData(final CinemaManager resp) {
        cinemaManager = resp;
    }

    /**
     * Displays an information alert with the provided message.
     *
     * @param message the text to be displayed as an alert message
     * @since 1.0
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Allows users to input cinema details, including name and address. If fields
     * are empty, an alert is displayed. Then, a responsible cinema object is
     * created based on ID, and the CinemaService creates a new cinema object using
     * the provided details.
     *
     * @param event
     *              action event triggered by the user's click on the "Add Cinema"
     *              button, which initiates the functionality of the function.
     *              <p>
     *              - `tfNom`: A text field containing the name of the cinema. -
     *              `tfAdresse`: A text field containing the address of the cinema.
     *              </p>
     */
    @FXML
    void addCinema(final ActionEvent event) {
        if (this.tfNom.getText().isEmpty() || this.tfAdresse.getText().isEmpty()) {
            this.showAlert("Please complete all fields!");
            return;
        }
        final String defaultStatut = "Pending";
        // Fetch the responsible cinema by its ID
        final CinemaManager cinemaManager = (CinemaManager) this.tfNom.getScene().getWindow().getUserData();
        URI uri = null;
        try {
            final String fullPath = this.image.getImage().getUrl();
            final String requiredPath = fullPath.substring(fullPath.indexOf("/img/cinemas/"));
            uri = new URI(requiredPath);
        } catch (final Exception e) {
            DashboardResponsableController.LOGGER.info(e.getMessage());
        }
        // Create the cinema object
        final Cinema cinema = new Cinema(this.tfNom.getText(), this.tfAdresse.getText(), cinemaManager,
                (uri != null ? uri.getPath() : ""), // fix null pointer
                defaultStatut);
        // Call the CinemaService to create the cinema
        final CinemaService cs = new CinemaService();
        cs.create(cinema);
        this.showAlert("Cinema added successfully!");
    }

    /**
     * Allows the user to select an image file, which is then copied to a specified
     * directory and set as the image for a `Image` component.
     *
     * @param event
     *              mouse event that triggered the function execution, providing the
     *              necessary information to determine the appropriate action to
     *              take.
     *              <p>
     *              - `event`: A `MouseEvent` object representing the user's action
     *              that triggered the function.
     *              </p>
     */
    @FXML
    void selectImage(final MouseEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                final String destinationDirectory = "./src/main/resources/img/cinemas/";
                final Path destinationPath = Paths.get(destinationDirectory);
                final String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                final Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath);
                final Image selectedImage = new Image(destinationFilePath.toUri().toString());
                this.image.setImage(selectedImage);
            } catch (final IOException e) {
                DashboardResponsableController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Loads accepted cinemas and sets pane visibility, adds cinema names to a combo
     * box, and listens for selection changes to load movies and rooms for the
     * selected cinema.
     *
     * @param location
     *                 URL of the initial page to load, which in this case is the
     *                 home
     *                 page with the list of cinemas.
     *                 <p>
     *                 - `location`: A `URL` object representing the location of the
     *                 application. - `resources`: A `ResourceBundle` object
     *                 containing
     *                 localized messages and data for the application.
     *                 </p>
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        final HashSet<Cinema> acceptedCinemas = this.loadAcceptedCinemas();
        this.cinemaFormPane.setVisible(true);
        this.sessionFormPane.setVisible(false);
        this.cinemaListPane.setVisible(true);
        this.SessionTableView.setVisible(false);
        this.addRoomForm.setVisible(false);
        this.RoomTableView.setVisible(false);
        this.sessionButton.setVisible(true);
        this.backSession.setVisible(false);
        this.backButton.setVisible(false);
        for (final Cinema c : acceptedCinemas) {
            this.comboCinema.getItems().add(c.getName());
        }
        this.comboCinema.getSelectionModel().selectedItemProperty().addListener(
                (final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
                    if (null != newValue) {
                        final Cinema selectedCinema = acceptedCinemas.stream()
                                .filter(cinema -> cinema.getName().equals(newValue)).findFirst().orElse(null);
                        if (null != selectedCinema) {
                            this.loadMoviesForCinema(selectedCinema.getId());
                            this.loadRoomsForCinema(selectedCinema.getId());
                        }
                    }
                });
    }

    /**
     * Clears the list of movies for a specified cinema and then reads the movies
     * from the FilmCinemaService, adding them to the combo movie list.
     *
     * @param cinemaId
     *                 unique identifier of the cinema for which the movies are to
     *                 be
     *                 loaded.
     */
    private void loadMoviesForCinema(final Long cinemaId) {
        this.comboMovie.getItems().clear();
        FilmCinemaService filmCinemaService = new FilmCinemaService();
        List<Film> moviesForCinema = filmCinemaService.readMoviesForCinema(cinemaId);

        for (final Film f : moviesForCinema) {
            this.comboMovie.getItems().add(f.getName());
        }
    }

    /**
     * Clears the items of a `JList` called `comboRoom`, then reads the rooms for a
     * given cinema using the `CinemaHallService`, and adds the room names to the
     * list.
     *
     * @param cinemaId
     *                 id of the cinema for which the rooms are being loaded.
     */
    private void loadRoomsForCinema(final Long cinemaId) {
        this.comboRoom.getItems().clear();
        final CinemaHallService ss = new CinemaHallService();
        final List<CinemaHall> roomsForCinema = ss.getCinemaHallsByCinemaId(cinemaId);
        for (final CinemaHall s : roomsForCinema) {
            this.comboRoom.getItems().add(s.getName());
        }
    }

    /**
     * Loads a set of accepted cinemas from a CinemaService and displays them as
     * cards on a flow pane.
     *
     * @returns a set of Cinema objects representing the accepted cinemas.
     *          <p>
     *          - `HashSet<Cinema>` represents a set of accepted cinemas in the
     *          system. - The set contains only cinemas with a "Accepted" status. -
     *          The list of cinemas is collected from the `read()` method of the
     *          `CinemaService` class. - The `HBox` objects created for each cinema
     *          are added to the `cinemaFlowPane` component.
     *          </p>
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        final List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> "Accepted".equals(cinema.getStatus())).collect(Collectors.toList());
        if (acceptedCinemasList.isEmpty()) {
            this.showAlert("No accepted cinemas are available.");
        }
        final HashSet<Cinema> acceptedCinemasSet = new HashSet<>(acceptedCinemasList);
        for (final Cinema cinema : acceptedCinemasSet) {
            final HBox cardContainer = this.createCinemaCard(cinema);
            this.cinemaFlowPane.getChildren().add(cardContainer);
        }
        return acceptedCinemasSet;
    }

    /**
     * Retrieves a list of cinemas from a service, filters them based on their
     * status, and returns a set of accepted cinemas.
     *
     * @returns a hash set of Cinema objects that represent accepted cinemas.
     *          <p>
     *          1/ The output is a `HashSet` containing only cinemas that have a
     *          `Statut` equal to "Accepted". 2/ The `HashSet` contains only a
     *          subset of the original list of cinemas, specifically those that meet
     *          the filter condition. 3/ The size of the `HashSet` is either zero or
     *          the number of cinemas that meet the filter condition, depending on
     *          whether any cinemas have a `Statut` equal to "Accepted".
     *          </p>
     */
    private HashSet<Cinema> chargerAcceptedCinemas() {
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        final List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> "Accepted".equals(cinema.getStatus())).collect(Collectors.toList());
        if (acceptedCinemasList.isEmpty()) {
            this.showAlert("No accepted cinemas are available.");
        }
        return new HashSet<>(acceptedCinemasList);
    }

    /**
     * Creates a card that displays a cinema's details, including its name,
     * capacity, and delete button. It also includes a Facebook icon and anchor for
     * opening the cinema's Facebook page.
     *
     * @param cinema
     *               cinema object that will be deleted or updated, and is used to
     *               access its properties and methods in the function.
     *               <p>
     *               - `id_cinema`: the unique identifier of the cinema -
     *               `nom_cinema`:
     *               the name of the cinema - `adresse_cinema`: the address of the
     *               cinema - `capacite_cinema`: the capacity of the cinema.
     *               </p>
     * @returns a Card object containing a Circle and an FontAwesomeIconView,
     *          representing a cinema.
     *          <p>
     *          - `card`: The root element of the card that contains information
     *          about a cinema. - `CinemaHallCircle`: A circle with a radius of 30
     *          pixels used to represent the cinema's capacity. - `facebookIcon`: An
     *          instance of `FontAwesomeIconView` representing the Facebook logo. -
     *          `facebookAnchor`: An instance of `Hyperlink` that displays the
     *          Facebook page for the cinema. - `circlefacebook`: A circle with a
     *          radius of 30 pixels used to represent the Facebook logo. -
     *          `cinemahallIcon`: An instance of `FontAwesomeIconView` representing
     *          the building icon used to indicate the cinema's location. -
     *          `cardContainer`: The container element that holds the card
     *          containing information about the cinema.
     *          </p>
     */
    private HBox createCinemaCard(final Cinema cinema) {
        final HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  25px;");
        final AnchorPane card = new AnchorPane();
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        card.setPrefWidth(400);
        final ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(70);
        logoImageView.setFitHeight(70);
        logoImageView.setLayoutX(15);
        logoImageView.setLayoutY(15);
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");
        Image image = null;
        try {
            if (!cinema.getLogoPath().isEmpty()) {
                image = new Image(cinema.getLogoPath());
            } else {
                image = new Image("Logo.png");
            }
        } catch (final Exception e) {
            DashboardResponsableController.LOGGER.info("line 335 " + e.getMessage());
            image = new Image("Logo.png");
        }
        logoImageView.setImage(image);
        card.getChildren().add(logoImageView);
        logoImageView.setOnMouseClicked(event -> {
            if (2 == event.getClickCount()) {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose a new image");
                fileChooser.getExtensionFilters()
                        .addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));
                final File selectedFile = fileChooser.showOpenDialog(null);
                if (null != selectedFile) {
                    final CinemaService cinemaService = new CinemaService();
                    cinema.setLogoPath("");
                    cinemaService.update(cinema);
                    final Image newImage = new Image(cinema.getLogoPath());
                    logoImageView.setImage(newImage);
                }
            }
        });
        final Label NomLabel = new Label("Name: ");
        NomLabel.setLayoutX(115);
        NomLabel.setLayoutY(25);
        NomLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(NomLabel);
        final Label nameLabel = new Label(cinema.getName());
        nameLabel.setLayoutX(160);
        nameLabel.setLayoutY(25);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(nameLabel);
        nameLabel.setOnMouseClicked(event -> {
            if (2 == event.getClickCount()) {
                final TextField nameTextField = new TextField(nameLabel.getText());
                nameTextField.setLayoutX(nameLabel.getLayoutX());
                nameTextField.setLayoutY(nameLabel.getLayoutY());
                nameTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                nameTextField.setPrefWidth(nameLabel.getWidth());
                nameTextField.setOnAction(e -> {
                    nameLabel.setText(nameTextField.getText());
                    cinema.setName(nameTextField.getText());
                    final CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    card.getChildren().remove(nameTextField);
                });
                card.getChildren().add(nameTextField);
                nameTextField.requestFocus();
                nameTextField.selectAll();
            }
        });
        final Label AdrsLabel = new Label("Address: ");
        AdrsLabel.setLayoutX(115);
        AdrsLabel.setLayoutY(50);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);
        final Label adresseLabel = new Label(cinema.getAddress());
        adresseLabel.setLayoutX(180);
        adresseLabel.setLayoutY(50);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);
        adresseLabel.setOnMouseClicked(event -> {
            if (2 == event.getClickCount()) {
                final TextField adresseTextField = new TextField(adresseLabel.getText());
                adresseTextField.setLayoutX(adresseLabel.getLayoutX());
                adresseTextField.setLayoutY(adresseLabel.getLayoutY());
                adresseTextField.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
                adresseTextField.setPrefWidth(adresseLabel.getWidth());
                adresseTextField.setOnAction(e -> {
                    adresseLabel.setText(adresseTextField.getText());
                    cinema.setAddress(adresseTextField.getText());
                    final CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    card.getChildren().remove(adresseTextField);
                });
                card.getChildren().add(adresseTextField);
                adresseTextField.requestFocus();
                adresseTextField.selectAll();
            }
        });
        final Line verticalLine = new Line();
        verticalLine.setStartX(240);
        verticalLine.setStartY(10);
        verticalLine.setEndX(240);
        verticalLine.setEndY(110);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);
        card.getChildren().add(verticalLine);
        final Circle circle = new Circle();
        circle.setRadius(30);
        circle.setLayoutX(285);
        circle.setLayoutY(45);
        circle.setFill(Color.web("#ae2d3c"));
        final FontAwesomeIconView deleteIcon = new FontAwesomeIconView();
        deleteIcon.setGlyphName("TRASH");
        deleteIcon.setSize("3.5em");
        deleteIcon.setLayoutX(270);
        deleteIcon.setLayoutY(57);
        deleteIcon.setFill(Color.WHITE);
        deleteIcon.setOnMouseClicked(event -> {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Voulez-vous vraiment supprimer ce cinéma ?");
            final Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                final CinemaService cinemaService = new CinemaService();
                cinemaService.delete(cinema);
                cardContainer.getChildren().remove(card);
            }
        });
        card.getChildren().addAll(circle, deleteIcon);
        final Circle CinemaHallCircle = new Circle();
        CinemaHallCircle.setRadius(30);
        CinemaHallCircle.setLayoutX(360);
        CinemaHallCircle.setLayoutY(45);
        CinemaHallCircle.setFill(Color.web("#ae2d3c"));
        final FontAwesomeIconView cinemahallIcon = new FontAwesomeIconView();
        cinemahallIcon.setGlyphName("GROUP");
        cinemahallIcon.setSize("3em");
        cinemahallIcon.setLayoutX(340);
        cinemahallIcon.setLayoutY(57);
        cinemahallIcon.setFill(Color.WHITE);
        cinemahallIcon.setOnMouseClicked(event -> {
            this.cinemaId = cinema.getId();
            this.cinemaFormPane.setVisible(false);
            this.cinemaListPane.setVisible(false);
            this.addRoomForm.setVisible(true);
            this.RoomTableView.setVisible(true);
            this.backButton.setVisible(true);
            this.sessionButton.setVisible(false);
            this.colNameRoom.setCellValueFactory(new PropertyValueFactory<>("name"));
            this.colNbrPlaces.setCellValueFactory(new PropertyValueFactory<>("seatCapacity"));
            this.colActionRoom
                    .setCellFactory(new Callback<TableColumn<CinemaHall, Void>, TableCell<CinemaHall, Void>>() {
                        @Override
                        /**
                         * Performs call operation.
                         *
                         * @return the result of the operation
                         */
                        public TableCell<CinemaHall, Void> call(final TableColumn<CinemaHall, Void> param) {
                            return new TableCell<CinemaHall, Void>() {
                                private final Button deleteRoomButton = new Button("Delete");

                                {
                                    this.deleteRoomButton.getStyleClass().add("delete-btn");
                                    this.deleteRoomButton.setOnAction(event -> {
                                        final CinemaHall cinemahall = this.getTableView().getItems()
                                                .get(this.getIndex());
                                        final CinemaHallService cinemahallService = new CinemaHallService();
                                        cinemahallService.delete(cinemahall);
                                        this.getTableView().getItems().remove(cinemahall);
                                    });
                                }

                                @Override
                                protected void updateItem(final Void item, final boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        this.setGraphic(null);
                                    } else {
                                        this.setGraphic(new HBox(this.deleteRoomButton));
                                    }
                                }
                            };
                        }
                    });
            this.RoomTableView.setEditable(true);
            this.colNbrPlaces.setCellFactory(tc -> new TableCell<CinemaHall, Integer>() {
                /**
                 * Updates an item's quantity based on user input. If the quantity is null or
                 * empty, it sets the text to null. Otherwise, it sets the text to the updated
                 * quantity.
                 *
                 * @param nb_cinemahalls
                 *                       number of sales, which is used to set the text value of
                 *                       the
                 *                       `setText()` method call.
                 *
                 *                       - `nb_cinemahalls` represents the number of places
                 *                       available for
                 *                       renting. - It can be null or an integer value. - When
                 *                       it is not
                 *                       null, it signifies that there are available places for
                 *                       renting.
                 *
                 * @param empty
                 *                       state of the item, with a value of `true` indicating an
                 *                       empty item
                 *                       and a value of `false` indicating an item with a number
                 *                       of places.
                 */
                @Override
                protected void updateItem(final Integer nb_cinemahalls, final boolean empty) {
                    super.updateItem(nb_cinemahalls, empty);
                    if (empty || null == nb_cinemahalls) {
                        this.setText(null);
                    } else {
                        this.setText(nb_cinemahalls + " places");
                    }
                }

                /**
                 * 1) calls super's `startEdit`, 2) checks if the item is empty, and 3) creates
                 * a `TextField` with the item's value and sets an `OnAction` listener to commit
                 * the edit when the user types something.
                 */
                @Override
                /**
                 * Performs startEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void startEdit() {
                    super.startEdit();
                    if (this.isEmpty()) {
                        return;
                    }
                    final TextField textField = new TextField(this.getItem().toString());
                    textField.setOnAction(event -> {
                        this.commitEdit(Integer.parseInt(textField.getText()));
                    });
                    this.setGraphic(textField);
                    this.setText(null);
                }

                /**
                 * In Java is used to cancel any ongoing editing activity and reset the text and
                 * graphic properties of an object to their original values.
                 */
                @Override
                /**
                 * Performs cancelEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void cancelEdit() {
                    super.cancelEdit();
                    this.setText(this.getItem() + " places");
                    this.setGraphic(null);
                }

                /**
                 * Updates the number of places in a `CinemaHall` object based on a user input,
                 * then calls the super method to commit the change, and sets the text and
                 * graphic of the cell to display the updated value.
                 *
                 * @param newValue
                 *                 new value of the number of places for the cinemahall to be
                 *                 updated
                 *                 by the `CinemaHallService`.
                 *
                 *                 - `Integer newValue`: The new value for the number of places
                 *                 in a
                 *                 cinemahall.
                 */
                @Override
                /**
                 * Performs commitEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void commitEdit(final Integer newValue) {
                    super.commitEdit(newValue);
                    final CinemaHall cinemahall = this.getTableView().getItems().get(this.getIndex());
                    cinemahall.setSeatCapacity(newValue);
                    final CinemaHallService cinemahallService = new CinemaHallService();
                    cinemahallService.update(cinemahall);
                    this.setText(newValue + " places");
                    this.setGraphic(null);
                }
            });
            this.colNameRoom.setCellFactory(tc -> new TableCell<CinemaHall, String>() {
                /**
                 * Updates an item's text based on whether it is empty or not, and sets the text
                 * to the hall name if it is not empty.
                 *
                 * @param nom_cinemahall
                 *                       name of the hall to be updated, which is passed to the
                 *                       superclass's `updateItem()` method and then further
                 *                       processed
                 *                       based on its value.
                 *
                 * @param empty
                 *                       whether the cinemahall is empty or not, and triggers
                 *                       the
                 *                       appropriate text display in the `updateItem` method.
                 */
                @Override
                protected void updateItem(final String nom_cinemahall, final boolean empty) {
                    super.updateItem(nom_cinemahall, empty);
                    if (empty || null == nom_cinemahall) {
                        this.setText(null);
                    } else {
                        this.setText(nom_cinemahall);
                    }
                }

                /**
                 * Initializes a new `TextField` instance and sets its text to the current
                 * item's value. It also sets an action listener on the `TextField` that calls
                 * the `commitEdit` method when the user presses enter or clicks outside the
                 * field. Finally, it replaces the `TextField` with the newly created instance
                 * in the graphical representation of the editor.
                 */
                @Override
                /**
                 * Performs startEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void startEdit() {
                    super.startEdit();
                    if (this.isEmpty()) {
                        return;
                    }
                    final TextField textField = new TextField(this.getItem());
                    textField.setOnAction(event -> {
                        this.commitEdit((textField.getText()));
                    });
                    this.setGraphic(textField);
                    this.setText(null);
                }

                /**
                 * In Java overrides the parent `cancelEdit()` method and sets the text and
                 * graphic properties of an object to their original values.
                 */
                @Override
                /**
                 * Performs cancelEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void cancelEdit() {
                    super.cancelEdit();
                    this.setText(this.getItem());
                    this.setGraphic(null);
                }

                /**
                 * Updates a cinemahall object's nom_cinemahall property by calling the `update`
                 * method of the CinemaHallService class, and then sets the new value for the
                 * nom_cinemahall property of the cinemahall object.
                 *
                 * @param newValue
                 *                 new value of the `nom_cinemahall` field for the selected
                 *                 `CinemaHall` object in the `TableView`.
                 */
                @Override
                /**
                 * Performs commitEdit operation.
                 *
                 * @return the result of the operation
                 */
                public void commitEdit(final String newValue) {
                    super.commitEdit(newValue);
                    final CinemaHall cinemahall = this.getTableView().getItems().get(this.getIndex());
                    cinemahall.setName(newValue);
                    final CinemaHallService cinemahallService = new CinemaHallService();
                    cinemahallService.update(cinemahall);
                    this.setText(newValue);
                    this.setGraphic(null);
                }
            });
            this.loadcinemahalls();
        });
        final Circle circlefacebook = new Circle();
        circlefacebook.setRadius(30);
        circlefacebook.setLayoutX(320);
        circlefacebook.setLayoutY(100);
        circlefacebook.setFill(Color.web("#ae2d3c"));
        final FontAwesomeIconView facebookIcon = new FontAwesomeIconView();
        facebookIcon.setGlyphName("FACEBOOK");
        facebookIcon.setSize("3.5em");
        facebookIcon.setLayoutX(310);
        facebookIcon.setLayoutY(120);
        facebookIcon.setFill(Color.WHITE);
        facebookIcon.setOnMouseClicked(event -> {
            this.facebookAnchor.setVisible(true);
        });
        card.getChildren().addAll(circlefacebook, facebookIcon);
        card.getChildren().addAll(CinemaHallCircle, cinemahallIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    /**
     * Sets the visible state of various panes and tables within a JavaFX
     * application, making the cinema list pane visible and the other components
     * hidden.
     */
    @FXML
    private void showCinemaList() {
        this.cinemaFormPane.setVisible(true);
        this.sessionFormPane.setVisible(false);
        this.cinemaListPane.setVisible(true);
        this.SessionTableView.setVisible(false);
        this.addRoomForm.setVisible(false);
        this.RoomTableView.setVisible(false);
    }

    /**
     * Is responsible for creating and displaying a form within a table cell to
     * allow users to edit the cinema's information, including its name and address,
     * as well as the names of the cinemahall and film associated with it.
     */
    @FXML
    private void showSessionForm() {
        this.cinemaFormPane.setVisible(false);
        this.sessionFormPane.setVisible(true);
        this.cinemaListPane.setVisible(false);
        this.SessionTableView.setVisible(true);
        this.addRoomForm.setVisible(false);
        this.RoomTableView.setVisible(false);
        this.colMovie.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<MovieSession, String>, ObservableValue<String>>() {
                    /**
                     * Generates a `SimpleStringProperty` observable value from the `getValue()` of
                     * the `MovieSession` object, which contains the film ID as a string.
                     *
                     * @param moviesessionStringCellDataFeatures
                     *                                           cell data features of a
                     *                                           MovieSession object, which contains
                     *                                           the
                     *                                           value of the film's ID.
                     *
                     * @returns a `SimpleStringProperty` object representing the film's ID.
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<MovieSession, String> moviesessionStringCellDataFeatures) {
                        return new SimpleStringProperty(
                                moviesessionStringCellDataFeatures.getValue().getFilm().getName());
                    }
                });
        this.colCinema.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<MovieSession, String>, ObservableValue<String>>() {
                    /**
                     * Takes a `TableColumn.CellDataFeatures` object as input and returns an
                     * `ObservableValue` of type `String`, which represents the cinema ID.
                     *
                     * @param moviesessionStringCellDataFeatures
                     *                                           cell data features of a table
                     *                                           column containing strings that
                     *                                           correspond to the cinema ID of the
                     *                                           movie being displayed.
                     *
                     * @returns a `SimpleStringProperty` representing the cinema ID.
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<MovieSession, String> moviesessionStringCellDataFeatures) {
                        return new SimpleStringProperty(
                                moviesessionStringCellDataFeatures.getValue().getCinemaHall().getCinema().getName());
                    }
                });
        this.colMovieRoom.setCellValueFactory(new PropertyValueFactory<>("cinemaHall"));
        this.colDate.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        this.colDepartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        this.colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        this.colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        this.colAction.setCellFactory(new Callback<TableColumn<MovieSession, Void>, TableCell<MovieSession, Void>>() {
            /**
             * Generates a `TableCell` that displays a button for deleting a `MovieSession`
             * object. When the button is clicked, the `MovieSessionService` service is
             * invoked to delete the `MovieSession`, and the cell's graphic is updated to
             * show null when there is no item or an icon button when an item exists.
             *
             * @param param
             *              TableColumn that the function is called on, allowing the
             *              function
             *              to modify its behavior based on the context of the table it is a
             *              part of.
             *
             *              - `param`: A `TableColumn<MovieSession, Void>` object
             *              representing
             *              a table column.
             *
             * @returns a `TableCell` object that displays a "Delete" button for each item
             *          in the table.
             *
             *          - `TableCell<MovieSession, Void>`: The type of the cell, indicating
             *          that it is a table cell for objects of type `MovieSession` and void.
             *          - `Button deleteButton`: A button with the text "Delete", which when
             *          clicked will call the `setOnAction` method to delete the
             *          corresponding `MovieSession` object. - `MovieSessionService
             *          moviesessionService`: An instance of the `MovieSessionService`
             *          class, which is used to delete the `MovieSession` object. -
             *          `getTableView().getItems()`: A method that returns a list of all
             *          `MovieSession` objects in the table. - `getIndex()`: A method that
             *          returns the index of the `MovieSession` object in the list. -
             *          `updateItem(Void item, boolean empty)`: A method that updates the
             *          graphics of the cell based on whether the `item` is null or not. If
             *          `item` is null, the graphics are set to null, otherwise they are set
             *          to a new `HBox` containing the `deleteButton`.
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<MovieSession, Void> call(final TableColumn<MovieSession, Void> param) {
                return new TableCell<MovieSession, Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        this.deleteButton.getStyleClass().add("delete-btn");
                        this.deleteButton.setOnAction(event -> {
                            final MovieSession ms = this.getTableView().getItems().get(this.getIndex()); // rename to
                                                                                                         // avoid
                                                                                                         // shadowing
                            final MovieSessionService moviesessionService = new MovieSessionService();
                            moviesessionService.delete(ms);
                            this.getTableView().getItems().remove(ms);
                        });
                    }

                    /**
                     * Updates an item's graphic based on whether it is empty or not. If the item is
                     * empty, the function sets the graphic to null. Otherwise, it sets the graphic
                     * to a new HBox containing a delete button.
                     *
                     * @param item
                     *              Void item being updated, which is passed to the superclass's
                     *              `updateItem()` method along with a boolean value indicating
                     *              whether the item is empty or not.
                     *
                     *              `item`: A Void object representing an item to be updated. Its
                     *              main
                     *              property is whether it is empty or not.
                     *
                     * @param empty
                     *              ether the item is empty or not, and it is used to determine the
                     *              graphics displayed in the updateItem method.
                     */
                    @Override
                    protected void updateItem(final Void item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic(null);
                        } else {
                            this.setGraphic(new HBox(this.deleteButton));
                        }
                    }
                };
            }
        });
        this.SessionTableView.setEditable(true);
        this.colPrice.setCellFactory(tc -> new TableCell<MovieSession, Double>() {
            /**
             * Updates an item's price and emptiness status. If the item is empty or the
             * price is null, the text is set to null. Otherwise, the text is set to the
             * price plus "DT".
             *
             * @param prix
             *              price of the item being updated, which is used to set the text
             *              value of the component.
             *
             *              - `prix` is a double value that represents the price of an item.
             *              -
             *              It can be either `null` or a non-null value indicating whether
             *              the
             *              item is empty or not.
             *
             * @param empty
             *              state of the item, and when it is `true`, the `setText()` method
             *              sets the text to `null`.
             */
            @Override
            protected void updateItem(final Double prix, final boolean empty) {
                super.updateItem(prix, empty);
                if (empty || null == prix) {
                    this.setText(null);
                } else {
                    this.setText(prix + " DT");
                }
            }

            /**
             * Initializes a `TextField` widget with the value of the currently selected
             * item, sets an `OnAction` listener to commit the edit when the user types
             * something, and sets the `Text` field to null to indicate that editing is in
             * progress.
             */
            @Override
            /**
             * Performs startEdit operation.
             *
             * @return the result of the operation
             */
            public void startEdit() {
                super.startEdit();
                if (this.isEmpty()) {
                    return;
                }
                final TextField textField = new TextField(this.getItem().toString());
                textField.setOnAction(event -> {
                    this.commitEdit(Double.parseDouble(textField.getText()));
                });
                this.setGraphic(textField);
                this.setText(null);
            }

            /**
             * In the provided Java code cancels the editing state of an item and sets the
             * text and graphic properties accordingly.
             */
            @Override
            /**
             * Performs cancelEdit operation.
             *
             * @return the result of the operation
             */
            public void cancelEdit() {
                super.cancelEdit();
                this.setText(this.getItem() + " DT");
                this.setGraphic(null);
            }

            /**
             * Updates a `MovieSession` object's `prix` field with a new value, and then
             * calls the `update` method of the `MovieSessionService` class to save the
             * changes. The function also updates the display value of the cell to show the
             * new value.
             *
             * @param newValue
             *                 updated price of the moviesession that is being edited, which
             *                 is
             *                 then set to the `prix` field of the corresponding
             *                 `MovieSession`
             *                 object and saved in the database through the `update()`
             *                 method of
             *                 the `MovieSessionService`.
             *
             *                 - `Double`: `newValue` is a `Double` value representing the
             *                 new
             *                 price for the moviesession.
             */
            @Override
            /**
             * Performs commitEdit operation.
             *
             * @return the result of the operation
             */
            public void commitEdit(final Double newValue) {
                super.commitEdit(newValue);
                final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                moviesession.setPrice(newValue);
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(newValue + " DT");
                this.setGraphic(null);
            }
        });
        this.colEndTime.setCellFactory(tc -> new TableCell<MovieSession, Time>() {
            /**
             * Updates an item's text based on whether it is empty or contains a valid value
             * from the `Time` class.
             *
             * @param HF
             *              time value that is to be updated in the text field, and its
             *              value
             *              determines whether the text field's text is set to null or the
             *              string representation of the time value.
             *
             *              - `HF` is a `Time` object representing a specific moment in
             *              time.
             *              - It can be either `null` or a non-`null` value indicating the
             *              presence of an item at that time.
             *
             * @param empty
             *              state of the item being updated, with `true` indicating an empty
             *              state and `false` indicating otherwise.
             */
            @Override
            protected void updateItem(final Time HF, final boolean empty) {
                super.updateItem(HF, empty);
                if (empty || null == HF) {
                    this.setText(null);
                } else {
                    this.setText(String.valueOf(HF));
                }
            }

            /**
             * Initializes a `TextField` component with the value of the current item, sets
             * an `OnAction` listener to commit the edit when the user types something, and
             * replaces the text field with the one created.
             */
            @Override
            /**
             * Performs startEdit operation.
             *
             * @return the result of the operation
             */
            public void startEdit() {
                super.startEdit();
                if (this.isEmpty()) {
                    return;
                }
                final TextField textField = new TextField(this.getItem().toString());
                textField.setOnAction(event -> {
                    this.commitEdit(Time.valueOf((textField.getText())));
                });
                this.setGraphic(textField);
                this.setText(null);
            }

            /**
             * In Java overrides the parent method and performs the following actions:
             * cancels editing, sets the text to the original value, and sets the graphic to
             * null.
             */
            @Override
            /**
             * Performs cancelEdit operation.
             *
             * @return the result of the operation
             */
            public void cancelEdit() {
                super.cancelEdit();
                this.setText(String.valueOf(this.getItem()));
                this.setGraphic(null);
            }

            /**
             * Updates a moviesession's high fidelity value based on a new value provided,
             * and saves the changes to the moviesession in the database using the
             * `MovieSessionService`.
             *
             * @param newValue
             *                 updated value of the `HF` field for the corresponding
             *                 `MovieSession` object in the `getTableView().getItems()`
             *                 collection, which is then updated in the database through the
             *                 `MovieSessionService`.
             *
             *                 - `Time newValue`: Represents a time value that represents
             *                 the
             *                 updated moviesession duration.
             */
            @Override
            /**
             * Performs commitEdit operation.
             *
             * @return the result of the operation
             */
            public void commitEdit(final Time newValue) {
                super.commitEdit(newValue);
                final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                moviesession.setEndTime(newValue);
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }
        });
        this.colDepartTime.setCellFactory(tc -> new TableCell<MovieSession, Time>() {
            /**
             * Updates an item's text based on whether it is empty or contains a valid value
             * from the `Time` object passed as a parameter.
             *
             * @param HD
             *              time value to be updated, which is passed through to the
             *              superclass's `updateItem()` method and then processed further in
             *              the current implementation.
             *
             *              - If `empty` is true or `HD` is null, then `setText` method sets
             *              the text to null. - Otherwise, `setText` method sets the text to
             *              a
             *              string representation of `HD`.
             *
             * @param empty
             *              whether the time is empty or not, and determines whether the
             *              `setText()` method should be called with a null value or the
             *              string representation of the time.
             */
            @Override
            protected void updateItem(final Time HD, final boolean empty) {
                super.updateItem(HD, empty);
                if (empty || null == HD) {
                    this.setText(null);
                } else {
                    this.setText(String.valueOf(HD));
                }
            }

            /**
             * 1) calls superclass `startEdit`, 2) checks if the object is empty, and 3)
             * creates a new `TextField` with the object's value as its text.
             */
            @Override
            /**
             * Performs startEdit operation.
             *
             * @return the result of the operation
             */
            public void startEdit() {
                super.startEdit();
                if (this.isEmpty()) {
                    return;
                }
                final TextField textField = new TextField(this.getItem().toString());
                textField.setOnAction(event -> {
                    this.commitEdit(Time.valueOf((textField.getText())));
                });
                this.setGraphic(textField);
                this.setText(null);
            }

            /**
             * In Java overrides the parent method and performs two actions: first, it calls
             * the superclass's `cancelEdit` method; second, it sets the text field to the
             * original value of the item and removes any graphic associated with the
             * editable component.
             */
            @Override
            /**
             * Performs cancelEdit operation.
             *
             * @return the result of the operation
             */
            public void cancelEdit() {
                super.cancelEdit();
                this.setText(String.valueOf(this.getItem()));
                this.setGraphic(null);
            }

            /**
             * Updates the `HD` field of a `MovieSession` object in a table view, and then
             * calls the `update` method of the `MovieSessionService` class to persist the
             * changes.
             *
             * @param newValue
             *                 new time value that will be assigned to the `HD` field of the
             *                 `MovieSession` object referenced by the
             *                 `getTableView().getItems().get(getIndex());` method call.
             *
             *                 - It represents a time value that has been edited by the
             *                 user. -
             *                 It is an instance of the `Time` class in Java, which
             *                 represents
             *                 time values in milliseconds since the Unix epoch (January 1,
             *                 1970,
             *                 00:00:00 UTC).
             */
            @Override
            /**
             * Performs commitEdit operation.
             *
             * @return the result of the operation
             */
            public void commitEdit(final Time newValue) {
                super.commitEdit(newValue);
                final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                moviesession.setStartTime(newValue);
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }
        });
        this.colDate.setCellFactory(tc -> new TableCell<MovieSession, Date>() {
            /**
             * Updates an item's text based on whether it is empty or not, and adds a mouse
             * click event listener that displays a date picker when clicked twice, allowing
             * the user to select a date which is then committed as the item's value.
             *
             * @param date
             *              date to be updated or retrieved, which is passed to the super
             *              method `updateItem()` and used to set the text value of the
             *              item.
             *
             *              - `date` can be either `null` or a `Date` object representing a
             *              specific date and time. - If `empty` is `true`, then `date` will
             *              be `null`. - The `toString()` method is called on `date` to
             *              obtain
             *              its string representation, which is assigned to the `setText()`
             *              method's argument.
             *
             * @param empty
             *              presence or absence of a value for the item being updated, and
             *              it determines whether the `setText()` method is called with a
             *              null
             *              value or the date string representation when the item is empty.
             */
            @Override
            protected void updateItem(final Date date, final boolean empty) {
                super.updateItem(date, empty);
                if (empty || null == date) {
                    this.setText(null);
                } else {
                    this.setText(date.toString());
                }
                this.setOnMouseClicked(event -> {
                    if (2 == event.getClickCount()) {
                        final DatePicker datePicker = new DatePicker();
                        if (!this.isEmpty() && null != getItem()) {
                            datePicker.setValue(this.getItem().toLocalDate());
                        }
                        datePicker.setOnAction(e -> {
                            final LocalDate selectedDate = datePicker.getValue();
                            if (null != selectedDate) {
                                final Date newDate = Date.valueOf(selectedDate);
                                this.commitEdit(newDate);
                            }
                        });
                        final StackPane root = new StackPane(datePicker);
                        final Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.show();
                    }
                });
            }

            /**
             * Updates a `MovieSession` object's `Date` field by calling the superclass's
             * `commitEdit` method, then setting the updated value to the `MovieSession`
             * object and saving it to the database using the `MovieSessionService`.
             *
             * @param newValue
             *                 new date to be updated for the corresponding `MovieSession`
             *                 object
             *                 in the `getTableView()` method.
             *
             *                 - `Date`: represents the date to be updated for the
             *                 corresponding
             *                 moviesession in the table view.
             */
            @Override
            /**
             * Performs commitEdit operation.
             *
             * @return the result of the operation
             */
            public void commitEdit(final Date newValue) {
                super.commitEdit(newValue);
                final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                moviesession.setSessionDate(newValue);
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }
        });
        this.colCinema.setCellFactory(tc -> new TableCell<MovieSession, String>() {
            @Override
            protected void updateItem(final String cinemaName, final boolean empty) {
                super.updateItem(cinemaName, empty);
                if (empty || null == cinemaName) {
                    this.setText(null);
                } else {
                    this.setText(cinemaName);
                }
                this.setOnMouseClicked(event -> {
                    if (2 == event.getClickCount()) {
                        final ComboBox<String> cinemaComboBox = new ComboBox<>();
                        final HashSet<Cinema> acceptedCinema = DashboardResponsableController.this
                                .chargerAcceptedCinemas();
                        for (final Cinema cinema : acceptedCinema) {
                            cinemaComboBox.getItems().add(cinema.getName());
                        }
                        cinemaComboBox.setValue(cinemaName);
                        cinemaComboBox.setOnAction(e -> {
                            final String selectedCinemaName = cinemaComboBox.getValue();
                            this.commitEdit(selectedCinemaName);
                            final MovieSession ms = this.getTableView().getItems().get(this.getIndex());
                            for (final Cinema cinema : acceptedCinema) {
                                if (cinema.getName().equals(selectedCinemaName)) { // fix getName() -> getName()
                                    // ms.setCinema(cinema); // remove, likely not needed
                                    break;
                                }
                            }
                            final MovieSessionService moviesessionService = new MovieSessionService();
                            moviesessionService.update(ms);
                        });
                        this.setGraphic(cinemaComboBox);
                    }
                });
            }
        });
        this.colMovieRoom.setCellFactory(tc -> new TableCell<MovieSession, String>() {
            /**
             * Updates the value of a cell in a table view based on user input. It creates a
             * ComboBox to display associated cinemahall names and selects the corresponding
             * cinemahall name upon second click.
             *
             * @param cinemahallName
             *                       name of the cinemahall to be updated, which is used to
             *                       set the
             *                       text value of the cell or to select the corresponding
             *                       cinemahall
             *                       from a combo box when the user double-clicks on the
             *                       cell.
             *
             * @param empty
             *                       absence of a cinemahall name or a null reference, which
             *                       triggers
             *                       the corresponding actions in the function, such as
             *                       setting the
             *                       text to null or displaying the ComboBox with associated
             *                       cinemahall
             *                       names.
             */
            @Override
            protected void updateItem(final String cinemahallName, final boolean empty) {
                super.updateItem(cinemahallName, empty);
                if (empty || null == cinemahallName) {
                    this.setText(null);
                } else {
                    this.setText(cinemahallName);
                }
                // Double clic détecté
                this.setOnMouseClicked(event -> {
                    if (2 == event.getClickCount()) {
                        // Créer un ComboBox contenant les noms des cinemahalls associées au cinéma
                        // sélectionné
                        final ComboBox<String> cinemahallComboBox = new ComboBox<>();
                        final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                        final Cinema selectedCinema = moviesession.getCinemaHall().getCinema();
                        // Récupérer les cinemahalls associées au cinéma sélectionné
                        final List<CinemaHall> associatedCinemaHalls = this
                                .loadAssociatedCinemaHalls(selectedCinema.getId());
                        for (final CinemaHall cinemahall : associatedCinemaHalls) {
                            cinemahallComboBox.getItems().add(cinemahall.getName());
                        }
                        // Sélectionner le nom de la cinemahall correspondant à la valeur actuelle de la
                        // cellule
                        cinemahallComboBox.setValue(cinemahallName);
                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        cinemahallComboBox.setOnAction(e -> {
                            final String selectedCinemaHallName = cinemahallComboBox.getValue();
                            // Mettre à jour la valeur de la cellule dans le TableView
                            this.commitEdit(selectedCinemaHallName);
                            // Mettre à jour la base de données en utilisant la méthode update de
                            // moviesessionService
                            final MovieSession ms = this.getTableView().getItems().get(this.getIndex());
                            for (final CinemaHall cinemahall : associatedCinemaHalls) {
                                if (cinemahall.getName().equals(selectedCinemaHallName)) {
                                    moviesession.setCinemaHall(cinemahall);
                                    break;
                                }
                            }
                            final MovieSessionService moviesessionService = new MovieSessionService();
                            moviesessionService.update(moviesession);
                        });
                        // Afficher le ComboBox dans la cellule
                        this.setGraphic(cinemahallComboBox);
                    }
                });
            }

            /**
             * Retrieves a list of `CinemaHall` objects associated with a given cinema ID
             * using the `CinemaHallService`.
             *
             * @param idCinema
             *                 ID of the cinema for which the associated cinemahalls are to
             *                 be
             *                 loaded.
             *
             * @returns a list of `CinemaHall` objects associated with the specified cinema
             *          id.
             */
            private List<CinemaHall> loadAssociatedCinemaHalls(final Long idCinema) {
                final CinemaHallService cinemahallService = new CinemaHallService();
                return cinemahallService.getCinemaHallsByCinemaId(idCinema);
            }
        });
        this.colMovie.setCellFactory(tc -> new TableCell<MovieSession, String>() {
            /**
             * Updates the item value in a TableView based on user input, and displays a
             * ComboBox containing film names associated with the selected cinema. When the
             * user double-clicks on the cell, the ComboBox is displayed, and the user can
             * select a film name to update the item value and display the corresponding
             * film name in the TableView.
             *
             * @param filmName
             *                 name of the film to be updated in the cinema's database,
             *                 which is
             *                 used to set the value of the `setText()` method and trigger
             *                 the
             *                 event handler for the ComboBox.
             *
             * @param empty
             *                 value of the `filmName` field when it is left blank or null,
             *                 and
             *                 it determines whether to display a message or not when the
             *                 user
             *                 clicks twice on the cell.
             */
            @Override
            protected void updateItem(final String filmName, final boolean empty) {
                super.updateItem(filmName, empty);
                if (empty || null == filmName) {
                    this.setText(null);
                } else {
                    this.setText(filmName);
                }
                // Double clic détecté
                this.setOnMouseClicked(event -> {
                    if (2 == event.getClickCount()) {
                        // Créer un ComboBox contenant les noms des films associées au cinéma
                        // sélectionné
                        final ComboBox<String> filmComboBox = new ComboBox<>();
                        final MovieSession moviesession = this.getTableView().getItems().get(this.getIndex());
                        final Cinema selectedCinema = moviesession.getCinemaHall().getCinema();
                        // Récupérer les films associées au cinéma sélectionné
                        final List<Film> associatedFilms = this.loadAssociatedFilms(selectedCinema.getId());
                        for (final Film film : associatedFilms) {
                            filmComboBox.getItems().add(film.getName());
                        }
                        // Sélectionner le nom de la cinemahall correspondant à la valeur actuelle de la
                        // cellule
                        filmComboBox.setValue(filmName);
                        // Définir un EventHandler pour le changement de sélection dans le ComboBox
                        filmComboBox.setOnAction(e -> {
                            final String selectedFilmName = filmComboBox.getValue();
                            // Mettre à jour la valeur de la cellule dans le TableView
                            this.commitEdit(selectedFilmName);
                            // Mettre à jour la base de données en utilisant la méthode update de
                            // moviesessionService
                            for (final Film film : associatedFilms) {
                                if (film.getName().equals(selectedFilmName)) {
                                    moviesession.setFilm(film);
                                    break;
                                }
                            }
                            final MovieSessionService moviesessionService = new MovieSessionService();
                            moviesessionService.update(moviesession);
                        });
                        // Afficher le ComboBox dans la cellule
                        this.setGraphic(filmComboBox);
                    }
                });
            }

            /**
             * Retrieves a list of films associated with a given cinema ID using the
             * `readMoviesForCinema` method provided by the `FilmCinemaService`.
             *
             * @param idCinema
             *                 unique identifier of the cinema for which associated films
             *                 are to
             *                 be loaded.
             *
             * @returns a list of movies associated with the given cinema ID.
             */
            private List<Film> loadAssociatedFilms(final Long idCinema) {
                FilmCinemaService filmCinemaService = new FilmCinemaService();
                return filmCinemaService.readMoviesForCinema(idCinema);
            }
        });
        this.loadMovieSessions();
    }

    /**
     * Allows users to input cinema, film and room information, as well as a start
     * and end time, and price. It then creates a new moviesession in the
     * MovieSessionService with the relevant details.
     */
    @FXML
    void addMovieSession() {
        final String selectedCinemaName = this.comboCinema.getValue();
        final String selectedFilmName = this.comboMovie.getValue();
        final String selectedRoomName = this.comboRoom.getValue();
        final LocalDate selectedDate = this.dpDate.getValue();
        final String departureTimeText = this.tfDepartureTime.getText();
        final String endTimeText = this.tfEndTime.getText();
        final String priceText = this.tfPrice.getText();
        if (null == selectedCinemaName || null == selectedFilmName || null == selectedRoomName || null == selectedDate
                || departureTimeText.isEmpty() || endTimeText.isEmpty() || priceText.isEmpty()) {
            this.showAlert("Please complete all fields.");
            return;
        }
        // Vérifier que les champs de l'heure de début et de fin sont au format heure
        try {
            Time.valueOf(LocalTime.parse(departureTimeText));
            Time.valueOf(LocalTime.parse(endTimeText));
        } catch (final DateTimeParseException e) {
            this.showAlert("The Start Time and End Time fields must be in the format HH:MM:SS.");
            return;
        }
        // Vérifier que le champ price contient un nombre réel
        try {
            final double price = Double.parseDouble(priceText);
            if (0 >= price) {
                this.showAlert("The price must be a positive number.");
                return;
            }
        } catch (final NumberFormatException e) {
            this.showAlert("The Price field must be a real number.");
            return;
        }
        final CinemaService cinemaService = new CinemaService();
        final Cinema selectedCinema = cinemaService.getCinemaByName(selectedCinemaName);
        final FilmService filmService = new FilmService();
        final Film selectedFilm = filmService.getFilmByName(selectedFilmName);
        final CinemaHallService cinemahallService = new CinemaHallService();
        final CinemaHall selectedRoom = cinemahallService.getCinemaHallByName(selectedRoomName);
        final Time departureTime = Time.valueOf(LocalTime.parse(departureTimeText));
        final Time endTime = Time.valueOf(LocalTime.parse(endTimeText));
        final Date date = Date.valueOf(selectedDate);
        final double price = Double.parseDouble(priceText);
        final MovieSession newMovieSession = new MovieSession(selectedRoom, selectedFilm, departureTime, endTime, date,
                price);
        final MovieSessionService moviesessionService = new MovieSessionService();
        moviesessionService.create(newMovieSession);
        this.showAlert("Session added successfully!");
        this.loadMovieSessions();
        this.showSessionForm();
    }

    /**
     * Retrieves a list of `MovieSession` objects from an external service, converts
     * it to an observable list, and sets it as the items of a view.
     *
     * @returns a list of MovieSession objects.
     *          <p>
     *          1/ List<MovieSession>: This is the type of the returned output,
     *          indicating that it is a list of `MovieSession` objects. 2/
     *          MovieSessionService: The class used to read the moviesession data,
     *          which is likely a database or API call. 3/ read(): The method called
     *          on the `MovieSessionService` instance to retrieve the moviesession
     *          data. 4/ List<MovieSession>: The list of `MovieSession` objects
     *          returned by the `read()` method. 5/ ObservableList<MovieSession>: An
     *          observable list of `MovieSession` objects, which means that the list
     *          can be modified through operations such as adding, removing, or
     *          modifying elements.
     *          </p>
     */
    private List<MovieSession> loadMovieSessions() {
        final MovieSessionService moviesessionService = new MovieSessionService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<MovieSession> moviesessions = moviesessionService.read(pageRequest).getContent();
        final ObservableList<MovieSession> moviesessionObservableList = FXCollections
                .observableArrayList(moviesessions);
        this.SessionTableView.setItems(moviesessionObservableList);
        return moviesessions;
    }

    /**
     * Verifies that all fields are filled, and then creates a new room in the
     * cinema's database with the provided number of places and name, displaying an
     * alert message after successful creation.
     *
     * @param event
     *              event of a button click and triggers the execution of the code
     *              within the function.
     *              <p>
     *              - `event` is an `ActionEvent`, indicating that the method was
     *              called as a result of user action.
     *              </p>
     */
    @FXML
    void AjouterCinemaHall(final ActionEvent event) {
        // Vérifier que tous les champs sont remplis
        if (this.tfNbrPlaces.getText().isEmpty() || this.tfNomCinemaHall.getText().isEmpty()) {
            this.showAlert("please complete all fields!");
            return;
        }
        try {
            final int nombrePlaces = Integer.parseInt(this.tfNbrPlaces.getText());
            if (0 >= nombrePlaces) {
                this.showAlert("The number of places must be a positive integer!");
                return;
            }
        } catch (final NumberFormatException e) {
            this.showAlert("The number of places must be an integer!");
            return;
        }
        final CinemaHallService ss = new CinemaHallService();
        final CinemaService cinemaService = new CinemaService();
        final Cinema cinema = cinemaService.getCinemaById(this.cinemaId);
        ss.create(new CinemaHall(cinema, Integer.parseInt(this.tfNbrPlaces.getText()), this.tfNomCinemaHall.getText()));
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added room");
        alert.setContentText("Added room!");
        alert.show();
        this.loadcinemahalls();
    }

    /**
     * Reads cinemahall data from a service, filters them based on cinema Id, and
     * displays the available rooms in a list view.
     */
    private void loadcinemahalls() {
        final CinemaHallService cinemahallService = new CinemaHallService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<CinemaHall> cinemahalls = cinemahallService.read(pageRequest).getContent();
        final List<CinemaHall> cinemahalls_cinema = cinemahalls.stream()
                .filter(cinemahall -> cinemahall.getCinema().getId().equals(this.cinemaId))
                .collect(Collectors.toList());
        if (cinemahalls_cinema.isEmpty()) {
            this.showAlert("No rooms are available");
            return;
        }
        final ObservableList<CinemaHall> cinemahallInfos = FXCollections.observableArrayList(cinemahalls_cinema);
        this.RoomTableView.setItems(cinemahallInfos);
    }

    /**
     * Makes the `facebookAnchor` component invisible when the `Facebook` button is
     * clicked.
     *
     * @param event
     *              occurrence of a button click event that triggered the function
     *              execution.
     */
    @FXML
    void closeAnchor(final ActionEvent event) {
        this.facebookAnchor.setVisible(false);
    }

    /**
     * Posts a status update to Facebook using an access token and message from a
     * text area.
     *
     * @param event
     *              action that triggered the function execution, providing the
     *              necessary context for the code to perform its intended task.
     *              <p>
     *              - `txtareaStatut`: This is a text area where the status message
     *              to
     *              be published is entered by the user.
     *              </p>
     */
    @FXML
    void PublierStatut(final ActionEvent event) {
        final String message = this.txtareaStatut.getText();
        final String accessToken = System.getenv("FACEBOOK_API_KEY");
        final String url = "https://graph.facebook.com/v19.0/me/feed";
        final String data = "message=" + message + "&access_token=" + accessToken;
        try {
            final URL obj = new URL(url);
            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
            final OutputStream os = con.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        } catch (final IOException e) {
            DashboardResponsableController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file, creates a new stage and replaces the current stage with
     * it, displaying the contents of the FXML file on the screen.
     *
     * @param event
     *              event that triggered the function, specifically the button click
     *              event that initiates the display of the film management
     *              interface.
     *              <p>
     *              - `event` represents an ActionEvent object, which carries
     *              information about the action that triggered the function.
     *              </p>
     */
    @FXML
    void AfficherFilmResponsable(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceFilm.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Film Manegement");
        stage.show();
        currentStage.close();
    }

    /**
     * Makes the `addRoomForm`, `backButton`, `RoomTableView`, and `cinemaFormPane`
     * invisible, while making the `sessionButton` visible, when a user clicks the
     * back button.
     *
     * @param event
     *              mouse event that triggered the execution of the `back` method.
     *              <p>
     *              Event type: `MouseEvent` Target element: `backButton`
     *              </p>
     */
    @FXML
    void back(final MouseEvent event) {
        this.addRoomForm.setVisible(false);
        this.backButton.setVisible(false);
        this.RoomTableView.setVisible(false);
        this.cinemaFormPane.setVisible(true);
        this.cinemaListPane.setVisible(true);
        this.sessionButton.setVisible(true);
    }

    /**
     * Makes the `sessionFormPane`, `SessionTableView`, and `backSession` components
     * visible, while hiding `cinemaFormPane`, `cinemaListPane`, and
     * `sessionButton`. It also calls `loadMovieSessions()` and `showSessionForm()`
     * to display the session form and content.
     *
     * @param event
     *              occurrence of an action, triggering the execution of the
     *              `showSessions()` method.
     *              <p>
     *              - `event` is an `ActionEvent` object representing the user
     *              action
     *              that triggered the function.
     *              </p>
     */
    @FXML
    void showSessions(final ActionEvent event) {
        this.cinemaFormPane.setVisible(false);
        this.cinemaListPane.setVisible(false);
        this.sessionFormPane.setVisible(true);
        this.SessionTableView.setVisible(true);
        this.sessionButton.setVisible(false);
        this.backSession.setVisible(true);
        this.loadMovieSessions();
        this.showSessionForm();
    }

    /**
     * Sets the visibility of various components in a JavaFX application, including
     * the `cinemaFormPane`, `cinemaListPane`, `sessionFormPane`, and
     * `SessionTableView`. It makes these components visible or invisible based on a
     * user input event.
     *
     * @param event
     *              mouse event that triggered the `back2()` method, providing
     *              information about the location and type of the event.
     *              <p>
     *              - Type: `MouseEvent` - Target: `cinemaFormPane` or
     *              `sessionButton`
     *              (depending on the location of the click) - Code: The button that
     *              was clicked (either `cinemaFormPane` or `sessionButton`)
     *              </p>
     */
    @FXML
    void back2(final MouseEvent event) {
        this.cinemaFormPane.setVisible(true);
        this.cinemaListPane.setVisible(true);
        this.sessionFormPane.setVisible(false);
        this.SessionTableView.setVisible(false);
        this.sessionButton.setVisible(true);
    }

    /**
     * Allows the user to select an image file, then copies it to a specified
     * directory and sets the selected image as the `image` field.
     *
     * @param event
     *              mouse event that triggered the `importImage()` method and
     *              provides
     *              the location of the selected file through its `FileChooser`
     *              object.
     *              <p>
     *              - `event` is a `MouseEvent` object representing a user's
     *              interaction with the application.
     *              </p>
     */
    @FXML
    void importImage(final MouseEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.image.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }
        }
    }
}

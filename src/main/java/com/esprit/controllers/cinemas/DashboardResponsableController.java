package com.esprit.controllers.cinemas;

import com.esprit.enums.CinemaStatus;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.models.users.CinemaManager;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
 * @see com.esprit.models.cinemas.Cinema
 * @see com.esprit.models.cinemas.CinemaHall
 * @see com.esprit.models.cinemas.MovieSession
 * @see com.esprit.models.users.CinemaManager
 * @since 1.0
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
    private VBox cinemaFormPane;
    @FXML
    private VBox sessionFormPane;
    @FXML
    private VBox cinemaListPane;
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
    private Node backButton;
    @FXML
    private Button sessionButton;
    @FXML
    private Node backSession;

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
     * Create a new Cinema from the controller's UI fields, associate it with the
     * current
     * CinemaManager (from the window's userData), persist it with status "Pending",
     * and
     * show a confirmation alert.
     */
    @FXML
    void addCinema(final ActionEvent event) {
        if (this.tfNom.getText().isEmpty() || this.tfAdresse.getText().isEmpty()) {
            this.showAlert("Please complete all fields!");
            return;
        }
        final String defaultStatut = CinemaStatus.PENDING.getStatus();
        // Fetch the responsible cinema by its ID
        final CinemaManager cinemaManager = (CinemaManager) this.tfNom.getScene().getWindow().getUserData();

        String logoPath = "";
        if (cloudinaryImageUrl != null && !cloudinaryImageUrl.isEmpty()) {
            logoPath = cloudinaryImageUrl;
        } else if (this.image.getImage() != null) {
            logoPath = this.image.getImage().getUrl();
        }

        // Create the cinema object
        final Cinema cinema = new Cinema(this.tfNom.getText(), this.tfAdresse.getText(), cinemaManager,
                logoPath,
                defaultStatut);
        // Call the CinemaService to create the cinema
        final CinemaService cs = new CinemaService();
        cs.create(cinema);
        this.showAlert("Cinema added successfully!");
    }

    /**
     * Let the user select an image file, copy it into
     * ./src/main/resources/img/cinemas/ with a unique name, and set that file as
     * the controller's ImageView.
     *
     * <p>
     * If copying fails or no file is chosen, the controller's image is not changed
     * and the error is logged.
     * </p>
     */
    @FXML
    void selectImage(final MouseEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.image.setImage(selectedImage);
            } catch (final IOException e) {
                DashboardResponsableController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

    }

    /**
     * Set initial UI visibility, load accepted cinemas into the cinema combo box,
     * and attach a listener that loads movies and rooms when a cinema is selected.
     * <p>
     * Called by the JavaFX framework after the FXML is loaded.
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
     * Populates the movie ComboBox with the names of films available for the given
     * cinema.
     *
     * @param cinemaId the id of the cinema whose films should populate the combo
     *                 box
     */
    private void loadMoviesForCinema(final Long cinemaId) {
        this.comboMovie.getItems().clear();
        // Get films that have sessions at this specific cinema
        MovieSessionService sessionService = new MovieSessionService();
        List<Film> moviesForCinema = sessionService.getFilmsByCinemaId(cinemaId);

        for (final Film f : moviesForCinema) {
            this.comboMovie.getItems().add(f.getTitle());
        }

    }

    /**
     * Loads cinema hall names for the given cinema into the comboRoom control,
     * replacing any existing items.
     *
     * @param cinemaId the identifier of the cinema whose halls will be loaded
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
     * Load and display cinemas with status "Accepted" as cards in the controller's
     * flow pane.
     *
     * @return a HashSet of Cinema instances with status "Accepted"
     */
    private HashSet<Cinema> loadAcceptedCinemas() {
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        final List<Cinema> acceptedCinemasList = cinemas.stream()
                .filter(cinema -> CinemaStatus.ACCEPTED.getStatus().equals(cinema.getStatus()))
                .collect(Collectors.toList());
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

    /**
     * Create a visual card HBox that displays a cinema's details and exposes
     * actions.
     * <p>
     * The card shows the cinema logo, editable name and address, controls to delete
     * the cinema, navigate to/manage its halls, and open the cinema's Facebook
     * anchor.
     *
     * @param cinema the Cinema to display and manage (used for editing, deletion,
     *               and navigation)
     * @return an HBox containing the constructed cinema card UI
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
            if (!cinema.getLogoUrl().isEmpty()) {
                image = new Image(cinema.getLogoUrl());
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
                    try {
                        CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                        String newUrl = cloudinaryStorage.uploadImage(selectedFile);

                        cinema.setLogoUrl(newUrl);
                        final CinemaService cinemaService = new CinemaService();
                        cinemaService.update(cinema);

                        final Image newImage = new Image(newUrl);
                        logoImageView.setImage(newImage);
                    } catch (IOException e) {
                        DashboardResponsableController.LOGGER.log(Level.SEVERE, "Error uploading image", e);
                    }
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

        final Label AdrsLabel = new Label(
                "Address: ");
        AdrsLabel.setLayoutX(115);
        AdrsLabel.setLayoutY(50);
        AdrsLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(AdrsLabel);
        final Label adresseLabel = new Label(cinema
                .getAddress());
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
        final org.kordamp.ikonli.javafx.FontIcon deleteIcon = new org.kordamp.ikonli.javafx.FontIcon();
        deleteIcon.setIconLiteral("mdi2t-trash");
        deleteIcon.setIconSize(56);
        deleteIcon.setLayoutX(270);
        deleteIcon.setLayoutY(57);
        deleteIcon.setIconColor(Color.WHITE);
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
        final org.kordamp.ikonli.javafx.FontIcon cinemahallIcon = new org.kordamp.ikonli.javafx.FontIcon();
        cinemahallIcon.setIconLiteral("mdi2a-account-multiple");
        cinemahallIcon.setIconSize(48);
        cinemahallIcon.setLayoutX(340);
        cinemahallIcon.setLayoutY(57);
        cinemahallIcon.setIconColor(Color.WHITE);
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
                        /**
                         * Create a table cell that shows a "Delete" button and removes the cell's
                         * CinemaHall from storage and the table when pressed.
                         *
                         * @param param the TableColumn for which this cell factory is created
                         * @return a TableCell containing a delete control that deletes its associated
                         *         CinemaHall from persistent storage and from the table view
                         */
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

                                /**
                                 * Updates the table cell's graphic to show the deleteRoomButton when the cell
                                 * is not empty.
                                 *
                                 * When `empty` is true the cell's graphic is cleared; otherwise the cell's
                                 * graphic is set to an HBox
                                 * containing the deleteRoomButton.
                                 *
                                 * @param item  the cell item (ignored for this cell type)
                                 * @param empty true if the cell does not contain data and should be displayed
                                 *              empty
                                 */
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
                 * Displays the number of places for the cell or clears the cell when it is
                 * empty.
                 *
                 * If the cell is empty or `nb_cinemahalls` is null, the cell text is cleared;
                 * otherwise the text
                 * is set to "`<n> places`".
                 *
                 * @param nb_cinemahalls the number of places to display; may be null
                 * @param empty          true if the cell should be treated as empty
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
                 * Enters edit mode and replaces the cell content with an inline TextField for
                 * editing the integer value.
                 *
                 * <p>
                 * If the cell is empty, editing is not started. The TextField is initialized
                 * with the cell's current
                 * value; when the user submits the field (e.g., presses Enter), the entered
                 * text is parsed as an
                 * Integer and committed as the new cell value.
                 *
                 * @throws NumberFormatException if the entered text is not a valid integer
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
                 * Restore the cell to its non-editing state and display the current item
                 * suffixed with " places".
                 *
                 * Updates the cell text to the cell's current item followed by " places" and
                 * removes any graphic used for editing.
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
                 * Commit edited seat capacity for the table row's CinemaHall and persist the
                 * change.
                 *
                 * Updates the model with the provided seat count, persists the change, and
                 * updates the cell text to display the new value.
                 *
                 * @param newValue the new seat capacity for the CinemaHall
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
                 * Sets the cell's text to the provided cinema hall name, or clears the text
                 * when the cell is empty or the name is null.
                 *
                 * @param nom_cinemahall the cinema hall name to display in the cell; ignored
                 *                       when null
                 * @param empty          true if the cell is empty and should be cleared
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
                 * Enter edit mode by replacing the cell's content with an editable TextField.
                 *
                 * If the cell is empty, no edit is started. The TextField is initialized with
                 * the
                 * cell's current value and commits the edited string when the user triggers its
                 * action (for example, presses Enter).
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
                 * Restore the cell's display after an edit is cancelled.
                 *
                 * Replaces the editing UI with the cell's current text and clears any graphic.
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
                 * Commit the edited cinema hall name and persist the change.
                 *
                 * Updates the CinemaHall for the current table row with the provided name and
                 * saves the updated entity.
                 *
                 * @param newValue the new name to set for the cinema hall
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
        final org.kordamp.ikonli.javafx.FontIcon facebookIcon = new org.kordamp.ikonli.javafx.FontIcon();
        facebookIcon.setIconLiteral("mdi2f-facebook");
        facebookIcon.setIconSize(56);
        facebookIcon.setLayoutX(310);
        facebookIcon.setLayoutY(120);
        facebookIcon.setIconColor(Color.WHITE);
        facebookIcon.setOnMouseClicked(event -> {
            this.facebookAnchor.setVisible(true);
        });
        card.getChildren().addAll(circlefacebook, facebookIcon);
        card.getChildren().addAll(CinemaHallCircle, cinemahallIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

    /**
     * Display the cinema list view while hiding session and room management panes
     * and tables.
     * <p>
     * Specifically, sets cinemaFormPane and cinemaListPane visible; hides
     * sessionFormPane, SessionTableView,
     * addRoomForm, and RoomTableView.
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
     * Display the movie session management view and configure the session table for
     * inline editing.
     * <p>
     * Sets the UI to show the session form and initializes the SessionTableView's
     * columns and cell factories so users can edit cinema, hall, film, date,
     * start/end times, and price, delete sessions, and persist changes. Finally,
     * loads current MovieSession entries into the table.
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
                     * Provides an observable string containing the MovieSession's film name.
                     *
                     * @param moviesessionStringCellDataFeatures cell data features for the
                     *                                           MovieSession row
                     * @return an ObservableValue containing the film's name
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
                                moviesessionStringCellDataFeatures.getValue().getFilm().getTitle());
                    }

                });
        this.colCinema.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<MovieSession, String>, ObservableValue<String>>() {
                    /**
                     * Provide the cinema name of the MovieSession for table-cell binding.
                     *
                     * @param moviesessionStringCellDataFeatures cell data features for the table
                     *                                           row containing the MovieSession
                     * @return a SimpleStringProperty containing the session's cinema name
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
             * Creates a table cell that displays a "Delete" button which removes its
             * MovieSession row and deletes the session.
             *
             * @param param the table column for which this cell factory is created
             * @return a TableCell whose graphic is a delete button that deletes the
             *         corresponding MovieSession and removes it from the table
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
                     * Show an HBox containing the delete button as the cell's graphic when the cell
                     * is not empty; clear the graphic when it is empty.
                     *
                     * @param empty true if the cell has no content and its graphic should be
                     *              cleared, false to display the delete-button HBox
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
             * Displays the cell's price or clears the text when the cell is empty or the
             * price is null.
             *
             * @param prix  the price to display; may be null to indicate no value
             * @param empty true when the cell should be treated as empty and its text
             *              cleared
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
             * Begins inline editing by replacing the cell's display with a TextField and
             * committing the entered value as a double.
             *
             * If the cell is empty, editing is not started. When the user confirms input
             * (for example by pressing Enter),
             * the text is parsed as a `double` and committed via `commitEdit`.
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
             * Cancel the cell's edit and restore its displayed text and graphic.
             *
             * After cancelling, the cell's text is set to the current item value followed
             * by " DT" and the graphic is cleared.
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
             * Commit the edited price into the underlying MovieSession, persist the change,
             * and update the cell text.
             *
             * @param newValue the new price value in DT to set on the MovieSession
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
             * Set the cell text to the Time value's string representation or clear the
             * text.
             *
             * @param HF    the Time value to display; if null the cell text is cleared
             * @param empty true if the cell is empty and should be cleared
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
             * Enters edit mode for the table cell and replaces its content with a
             * TextField.
             *
             * Confirms the edit on Enter by committing the cell value parsed via
             * java.sql.Time.valueOf from the
             * TextField's text. If the cell is empty, editing is not started. The entered
             * text must be a valid
             * Time.valueOf string (for example "HH:MM:SS"); otherwise the commit will fail.
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
             * Reverts any in-progress edit and restores the cell's displayed value.
             *
             * After cancelling, the cell's text is reset to the cell's current item and any
             * editing graphic is removed.
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
             * Commits and persists a new end time for the MovieSession in this table row.
             *
             * @param newValue the end time to set on the MovieSession
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
                if (moviesession.getEndTime() != null) {
                    moviesession.setEndTime(moviesession.getEndTime().withHour(newValue.toLocalTime().getHour())
                            .withMinute(newValue.toLocalTime().getMinute())
                            .withSecond(newValue.toLocalTime().getSecond()));
                } else {
                    moviesession.setEndTime(LocalDateTime.now().withHour(newValue.toLocalTime().getHour())
                            .withMinute(newValue.toLocalTime().getMinute()));
                }
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }

        });
        this.colDepartTime.setCellFactory(tc -> new TableCell<MovieSession, Time>() {
            /**
             * Display the given Time in the cell or clear the cell when it is empty.
             *
             * @param HD    the Time value to display; if null the cell is cleared
             * @param empty true when the cell is empty, in which case the cell text is
             *              cleared
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
             * Enters edit mode for the table cell and replaces its content with a
             * TextField.
             *
             * Confirms the edit on Enter by committing the cell value parsed via
             * java.sql.Time.valueOf from the
             * TextField's text. If the cell is empty, editing is not started. The entered
             * text must be a valid
             * Time.valueOf string (for example "HH:MM:SS"); otherwise the commit will fail.
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
             * Reverts any in-progress edit and restores the cell's displayed value.
             *
             * After cancelling, the cell's text is reset to the cell's current item and any
             * editing graphic is removed.
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
             * Commits an edited start time into the MovieSession represented by this table
             * row and persists the update.
             *
             * @param newValue the new start time to assign to the MovieSession for this
             *                 table row
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
                if (moviesession.getStartTime() != null) {
                    moviesession.setStartTime(moviesession.getStartTime().withHour(newValue.toLocalTime().getHour())
                            .withMinute(newValue.toLocalTime().getMinute())
                            .withSecond(newValue.toLocalTime().getSecond()));
                } else {
                    moviesession.setStartTime(LocalDateTime.now().withHour(newValue.toLocalTime().getHour())
                            .withMinute(newValue.toLocalTime().getMinute()));
                }
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }

        });
        this.colDate.setCellFactory(tc -> new TableCell<MovieSession, Date>() {
            /**
             * Update the table cell's displayed text and allow committing a new Date by
             * double-clicking to open a DatePicker.
             *
             * When the cell is empty or the provided `date` is null, the cell text is
             * cleared. On a double-click the user can pick
             * a date in a modal DatePicker; a chosen date is converted to a `java.sql.Date`
             * and committed as the cell's value.
             *
             * @param date  the current Date value for the cell; may be null
             * @param empty true if the cell is considered empty and its display should be
             *              cleared
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
             * Commits an edited session date to the MovieSession and persists the change.
             * Also updates the table cell text to the new date and clears the cell's
             * graphic.
             *
             * @param newValue the new session date to set on the MovieSession
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
                java.time.LocalDate localDate = newValue.toLocalDate();
                if (moviesession.getStartTime() != null) {
                    moviesession.setStartTime(moviesession.getStartTime().withYear(localDate.getYear())
                            .withMonth(localDate.getMonthValue()).withDayOfMonth(localDate.getDayOfMonth()));
                } else {
                    moviesession.setStartTime(localDate.atStartOfDay());
                }
                final MovieSessionService moviesessionService = new MovieSessionService();
                moviesessionService.update(moviesession);
                this.setText(String.valueOf(newValue));
                this.setGraphic(null);
            }

        });
        this.colCinema.setCellFactory(tc -> new TableCell<MovieSession, String>() {
            /**
             * Displays the cinema name for a table cell and allows inline editing of the
             * associated MovieSession's cinema.
             *
             * When the cell is activated for edit (double-click), a ComboBox populated with
             * accepted cinema names is shown;
             * choosing a different cinema commits the new name into the cell and triggers
             * persistence of the modified
             * MovieSession via MovieSessionService.update.
             *
             * @param cinemaName the cinema name to display in this cell; may be null
             * @param empty      true if this cell does not contain data and should be
             *                   cleared
             */
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
                                .loadAcceptedCinemas();
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
             * Displays the current cinema hall name in the table cell and, on double-click,
             * replaces the cell with a ComboBox of cinema halls for the same cinema so the
             * user
             * can select a different hall; the chosen hall is committed to the cell and
             * persisted to the MovieSessionService.
             *
             * @param cinemahallName the current string value of this cell (the cinema hall
             *                       name)
             * @param empty          true if this cell does not contain data and should be
             *                       treated as empty
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
             * Retrieve the list of cinema halls belonging to the specified cinema.
             *
             * @param idCinema the ID of the cinema whose halls should be retrieved
             * @return a list of CinemaHall objects belonging to the specified cinema
             */
            private List<CinemaHall> loadAssociatedCinemaHalls(final Long idCinema) {
                final CinemaHallService cinemahallService = new CinemaHallService();
                return cinemahallService.getCinemaHallsByCinemaId(idCinema);
            }

        });
        this.colMovie.setCellFactory(tc -> new TableCell<MovieSession, String>() {
            /**
             * Shows a ComboBox of films for a session's cinema on double-click and persists
             * the chosen film.
             *
             * <p>
             * When the cell is double-clicked, a ComboBox populated with films associated
             * with the
             * MovieSession's cinema replaces the cell content. Selecting a film commits the
             * cell edit,
             * assigns the corresponding Film to the MovieSession, and calls
             * MovieSessionService.update(...)
             * to persist the change.
             *
             * @param filmName the current textual value displayed in the cell (film name)
             * @param empty    whether the cell is empty
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
                            filmComboBox.getItems().add(film.getTitle());
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
                                if (film.getTitle().equals(selectedFilmName)) {
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
             * Load films associated with the specified cinema.
             *
             * @param idCinema the cinema's identifier
             * @return a list of Film objects linked to the specified cinema
             */
            private List<Film> loadAssociatedFilms(final Long idCinema) {
                FilmService filmCinemaService = new FilmService();
                return filmCinemaService.read(PageRequest.defaultPage()).getContent();
            }

        });
        this.loadMovieSessions();
    }

    /**
     * Create and persist a new MovieSession from the controller's form inputs and
     * switch to the session view.
     * <p>
     * Validates that cinema, film, room, date, start/end times (format HH:MM:SS),
     * and price are provided and valid; shows an alert and aborts on validation
     * failure. On success, resolves the selected Cinema, Film, and CinemaHall,
     * saves the MovieSession via MovieSessionService, refreshes the session list,
     * and displays the session form.
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
     * Loads movie sessions from the service and populates the session table view.
     *
     * @return the list of loaded MovieSession objects
     */
    private List<MovieSession> loadMovieSessions() {
        final MovieSessionService moviesessionService = new MovieSessionService();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<MovieSession> moviesessions = moviesessionService.read(pageRequest).getContent();
        final ObservableList<MovieSession> moviesessionObservableList = FXCollections
                .observableArrayList(moviesessions);
        this.SessionTableView.setItems(moviesessionObservableList);
        return moviesessions;
    }

    /**
     * Create a new cinema hall for the currently selected cinema after validating
     * input.
     * <p>
     * Validates that the hall name and number of places are provided and that the
     * number of places is a positive integer. On success, persists the new
     * CinemaHall, shows an informational alert, and refreshes the displayed hall
     * list.
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
     * Load cinema halls for the controller's selected cinema and display them in
     * the room table view.
     * <p>
     * Retrieves cinema halls from the service, filters them to entries whose cinema
     * id equals this controller's
     * {@code cinemaId}, shows an informational alert when no matching halls are
     * found, and otherwise sets the
     * filtered list as the items of {@code RoomTableView}.
     */
    private void loadcinemahalls() {
        final CinemaHallService cinemahallService = new CinemaHallService();
        PageRequest pageRequest = PageRequest.defaultPage();
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
     * Hides the facebookAnchor UI component.
     */
    @FXML
    void closeAnchor(final ActionEvent event) {
        this.facebookAnchor.setVisible(false);
    }

    /**
     * Publish the controller's status text to the current user's Facebook feed.
     * <p>
     * Sends the `message` and `access_token` form parameters to the Facebook Graph
     * API `/me/feed` endpoint.
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
     * Opens the film management interface in a new window and closes the current
     * window.
     *
     * @param event the ActionEvent that triggered navigation to the film management
     *              interface
     * @throws IOException if the FXML resource for the film interface cannot be
     *                     loaded
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
     * Switches the UI from room-management to the cinema list and form view.
     *
     * @param event the mouse event that triggered the navigation
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
     * Switches the UI to the session-management view.
     * <p>
     * Shows the session form and session table, hides the cinema form and cinema
     * list,
     * and adjusts navigation controls to the session-management state.
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
     * Restore the cinema-management view by showing cinema-related panes and hiding
     * session-related session panes.
     */
    @FXML
    void back2(final MouseEvent event) {
        this.cinemaFormPane.setVisible(true);
        this.cinemaListPane.setVisible(true);
        this.sessionFormPane.setVisible(false);
        this.SessionTableView.setVisible(false);
        this.sessionButton.setVisible(true);
    }
}

package com.esprit.controllers.films;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.controlsfx.control.Rating;
import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.models.films.FilmComment;
import com.esprit.models.films.FilmRating;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.*;
import com.esprit.services.users.UserService;
import com.esprit.utils.PageRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

/**
 * Is responsible for handling user interactions related to viewing and
 * commenting on films within a cinema website. It displays a scrolling pane
 * containing all comments for a given film, and allows users to add new
 * comments or view previous comments by clicking buttons in the interface. The
 * controller also provides methods for retrieving all comments for a specific
 * film and displaying them in the scrolling pane.
 */
public class FilmUserController {
    private static final Logger LOGGER = Logger.getLogger(FilmUserController.class.getName());

    // Remove unused fields
    // private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> yearsCheckBoxes = new ArrayList<>();

    @FXML
    public Button closeDetailFilm2;
    FlowPane flowpaneFilm;
    FlowPane flowPaneactors;
    @FXML
    private Button closeDetailFilm1;
    @FXML
    private AnchorPane AnchorComments;
    private List<Film> l1 = new ArrayList<>();
    @FXML
    private Rating filmRate;
    @FXML
    private Button Cinema_Button;
    @FXML
    private Button event_button;
    @FXML
    private Button product;
    @FXML
    private Button trailer_Button;
    @FXML
    private AnchorPane anchorPane_Trailer;
    @FXML
    private Label labelavregeRate;
    @FXML
    private AnchorPane anchorPaneFilm;
    @FXML
    private ScrollPane ScrollPaneComments;
    @FXML
    private TextField txtAreaComments;
    @FXML
    private AnchorPane Anchore_Pane_filtrage;
    @FXML
    private AnchorPane detalAnchorPane;
    @FXML
    private Button closeDetailFilm;
    @FXML
    private TextArea descriptionDETAILfilm;
    @FXML
    private ImageView imagefilmDetail;
    @FXML
    private Label filmNomDetail;
    @FXML
    private VBox topthreeVbox;
    @FXML
    private ScrollPane filmScrollPane;
    // Remove unused field
    // private ScrollPane actorScrollPane;
    @FXML
    private TextField serach_film_user;
    @FXML
    private Button reserver_Film;
    @FXML
    private Button SerieButton;
    @FXML
    private ImageView qrcode;
    @FXML
    private ComboBox<String> top3combobox;
    @FXML
    private VBox topthreeVbox1;
    @FXML
    private ComboBox<String> tricomboBox;
    private Long filmId; // Changed from int to Long

    // Remove unused field
    // private final HashMap<Integer, Double> userPreferences = new HashMap<>();
    // Add moviesession field
    private MovieSession moviesession;

    /**
     * Finds films whose name contains the given search string.
     *
     * @param liste   the list of films to search; elements with a null name are ignored
     * @param recherche the substring to look for in each film's name
     * @return a new list of films from {@code liste} whose names contain {@code recherche}, preserving their original order
     */
    @FXML
    /**
     * Performs rechercher operation.
     *
     * @return the result of the operation
     */
    public static List<Film> rechercher(final List<Film> liste, final String recherche) {
        final List<Film> resultats = new ArrayList<>();
        for (final Film element : liste) {
            if (null != element.getName() && element.getName().contains(recherche)) {
                resultats.add(element);
            }

        }

        return resultats;
    }


    /**
     * Open the seat selection UI for the given film and display it in the current stage.
     *
     * If a MovieSession exists for the film, initializes the SeatSelectionController with
     * that session and the current client, then replaces the current scene with the loaded UI.
     * If no MovieSession is available, shows an error alert and leaves the current scene unchanged.
     *
     * @param nom the name of the film to open seat selection for
     * @throws IOException if loading the SeatSelection FXML or its controller fails
     */
    public void switchtopayment(final String nom) throws IOException {
        // Remove unused variable
        // final FilmService filmService = new FilmService();
        final Film film = new FilmService().getFilmByName(nom);

        // Initialize moviesession before passing to SeatSelection
        this.moviesession = new MovieSessionService().getFirstSessionForFilm(film.getId());
        if (this.moviesession == null) {
            showError("No Available MovieSession", "There are no available moviesessions for this film.");
            return;
        }


        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/films/SeatSelection.fxml"));
        final AnchorPane root = fxmlLoader.load();
        final Stage stage = (Stage) this.reserver_Film.getScene().getWindow();
        final SeatSelectionController controller = fxmlLoader.getController();
        final Client client = (Client) stage.getUserData();
        controller.initialize(moviesession, client);
        final Scene scene = new Scene(root);
        stage.setScene(scene);
    }


    /**
     * Displays an error alert with the given title and message.
     * 
     * <p>
     * This method creates and shows a JavaFX Alert dialog of type ERROR
     * with the specified title and message. The dialog blocks the application
     * until the user acknowledges it by clicking OK.
     * </p>
     *
     * @param title   the title text for the error dialog
     * @param message the detailed error message to display
     * @see Alert
     * @see Alert.AlertType#ERROR
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Create and add a UI card to the flow pane for each film in the provided list.
     *
     * @param Films list of films whose cards will be created and appended to {@code flowpaneFilm}
     */
    private void createfilmCards(final List<Film> Films) {
        for (final Film film : Films) {
            final AnchorPane cardContainer = this.createFilmCard(film);
            this.flowpaneFilm.getChildren().add(cardContainer);
        }

    }


    /**
     * Initializes the JavaFX controller and sets up UI components.
     * 
     * <p>
     * This method is called automatically by JavaFX after loading the FXML file.
     * It configures UI components such as comboboxes, flowpanes, and event handlers
     * for film display and interaction.
     * </p>
     * 
     * <p>
     * Specifically, it:
     * </p>
     * <ul>
     * <li>Sets up the top 3 films and actors combobox</li>
     * <li>Configures sorting options for films</li>
     * <li>Initializes search functionality</li>
     * <li>Sets up the film cards display area</li>
     * <li>Registers event handlers for UI interactions</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        // Initialize basic UI components first
        setupBasicUI();

        // Defer loading recommendations until scene is ready
        Platform.runLater(this::setupRecommendations);
    }


    /**
     * Initializes and configures the controller's primary user-interface elements and their listeners.
     *
     * Sets up the "Top 3" selector and its view toggles, sorting selector, search field listener, the film
     * FlowPane inside the ScrollPane (layout, spacing and padding), the detail-close action, and populates
     * the initial film cards and top-three displays from the film service.
     */
    private void setupBasicUI() {
        PageRequest pageRequest = new PageRequest(0, 3);
        this.top3combobox.getItems().addAll("Top 3 Films", "Top 3 Actors");
        this.top3combobox.setValue("Top 3 Films");
        this.top3combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Top 3 Films".equals(newValue)) {
                this.topthreeVbox1.setVisible(false);
                this.topthreeVbox.setVisible(true);
            }
 else if ("Top 3 Actors".equals(newValue)) {
                final ObservableList<Node> topthreevboxactorsChildren = this.topthreeVbox1.getChildren();
                topthreevboxactorsChildren.clear();
                this.topthreeVbox.setVisible(false);
                this.topthreeVbox1.setVisible(true);
                for (int i = 1; i < this.flowpaneFilm.getChildren().size() && 4 > i; i++) {
                    topthreevboxactorsChildren.add(this.createActorDetails(i));
                }

                this.topthreeVbox1.setSpacing(10);
            }

        }
);
        this.tricomboBox.getItems().addAll("nom", "annederalisation");
        this.tricomboBox.setValue("");
        this.tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            this.flowpaneFilm.getChildren().clear();
            final List<Film> filmList = new FilmService().sort(pageRequest, t1).getContent();
            for (final Film film : filmList) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }

        }
);
        final FilmService filmService1 = new FilmService();
        PageRequest filmPageRequest = new PageRequest(0, 10);
        this.l1 = filmService1.read(filmPageRequest).getContent();
        this.serach_film_user.textProperty().addListener((observable, oldValue, newValue) -> {
            final List<Film> produitsRecherches = FilmUserController.rechercher(this.l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            this.flowpaneFilm.getChildren().clear();
            this.createfilmCards(produitsRecherches);
        }
);
        this.flowpaneFilm = new FlowPane();
        this.filmScrollPane.setContent(this.flowpaneFilm);
        this.filmScrollPane.setFitToWidth(true);
        this.filmScrollPane.setFitToHeight(true);
        this.filmScrollPane.setStyle("-fx-background-color: transparent;");
        this.topthreeVbox.setSpacing(15);

        // Configure FlowPane for responsive grid layout
        this.flowpaneFilm.setHgap(15); // Better spacing between cards
        this.flowpaneFilm.setVgap(20); // Better vertical spacing
        this.flowpaneFilm.setStyle("-fx-background-color: transparent;");
        this.closeDetailFilm.setOnAction(event -> {
            this.detalAnchorPane.setVisible(false);
            this.anchorPaneFilm.setOpacity(1);
            this.anchorPaneFilm.setDisable(false);
        }
);
        // Set better padding for card grid
        this.flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));
        final List<Film> filmList = new FilmService().read(filmPageRequest).getContent();
        for (final Film film : filmList) {
            this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
        }

        final ObservableList<Node> topthreevboxChildren = this.topthreeVbox.getChildren();
        for (int i = 0; i < this.flowpaneFilm.getChildren().size() && 3 > i; i++) {
            topthreevboxChildren.add(this.createtopthree(i));
        }

    }


    /**
     * Augments the film list UI by adding per-card sharing controls and appending a
     * "Recommended for You" section populated from top-rated films.
     *
     * <p>When the film scroll pane is attached to a window, this method:
     * - Adds a "Share" button to each film card that opens a simple share dialog for that film.
     * - Enables drag-and-drop on each film card to copy the film's name and image URL.
     * - Builds a recommendations VBox titled "Recommended for You" using up to three films
     *   from FilmRatingService.getAverageRatingSorted() and appends it to the main flow pane
     *   when recommendations are available.</p>
     */
    private void setupRecommendations() {
        if (filmScrollPane.getScene() != null && filmScrollPane.getScene().getWindow() != null) {
            // Add sharing button to film cards
            flowpaneFilm.getChildren().forEach(node -> {
                if (node instanceof AnchorPane) {
                    Button shareButton = new Button("Share");
                    shareButton.setOnAction(e -> {
                        if (node.getUserData() instanceof Film) {
                            Film film = (Film) node.getUserData();
                            // Open a simple sharing dialog or directly share to a platform
                            Alert shareAlert = new Alert(Alert.AlertType.INFORMATION);
                            shareAlert.setTitle("Share Film");
                            shareAlert.setHeaderText("Share \"" + film.getName() + "\" to social media");
                            shareAlert.setContentText("Film URL: " + film.getImage());
                            shareAlert.showAndWait();
                        }

                    }
);
                    ((AnchorPane) node).getChildren().add(shareButton);

                    // Set up drag and drop for sharing
                    if (node.getUserData() instanceof Film) {
                        final Film film = (Film) node.getUserData();

                        node.setOnDragDetected(event -> {
                            Dragboard db = node.startDragAndDrop(TransferMode.COPY);
                            ClipboardContent content = new ClipboardContent();
                            content.putString("Film: " + film.getName() + "\nURL: " + film.getImage());
                            db.setContent(content);
                            event.consume();
                        }
);
                    }

                }

            }
);

            // Add recommendations section based on user history
            // This is a simplified version; in a real app, this would use actual user data
            try {
                VBox recommendationsBox = new VBox(10);
                recommendationsBox.getChildren().add(new Label("Recommended for You"));

                // Get top rated films as recommendations
                List<FilmRating> topRatings = new FilmRatingService().getAverageRatingSorted();
                if (topRatings != null && !topRatings.isEmpty()) {
                    for (int i = 0; i < Math.min(3, topRatings.size()); i++) {
                        Film film = topRatings.get(i).getFilm();
                        if (film != null) {
                            recommendationsBox.getChildren().add(createFilmCard(film));
                        }

                    }

                    // Add to main flow if there are recommendations
                    if (recommendationsBox.getChildren().size() > 1) {
                        flowpaneFilm.getChildren().add(recommendationsBox);
                    }

                }

            }
 catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load recommendations", e);
            }

        }

    }


    /**
         * Show only film cards whose title contains the given keyword (case-insensitive).
         *
         * @param keyword the substring to match against each film's title (case-insensitive)
         */
    @FXML
    public void filterByKeyword(final String keyword) {
        for (final Node node : this.flowpaneFilm.getChildren()) {
            final AnchorPane filmCard = (AnchorPane) node;
            final Label nomFilm = (Label) filmCard.lookup(".nomFilm");
            if (null != nomFilm) {
                final boolean isVisible = nomFilm.getText().toLowerCase().contains(keyword.toLowerCase());
                filmCard.setVisible(isVisible);
                filmCard.setManaged(isVisible);
            }

        }

    }


    /**
     * Create a JavaFX AnchorPane representing a film card with poster, title,
     * displayed rating, and actions for reserving and viewing details.
     *
     * @param film the film to display in the card; its metadata (id, name, image,
     *             ratings and related links) will be used to populate the UI
     * @return an AnchorPane containing the film poster, title label, current
     *         average rating label, a Reserve button, a View Details hyperlink and
     *         associated UI for QR-code generation, trailer playback, and user
     *         rating interaction for the provided film
     */
    private AnchorPane createFilmCard(final Film film) {
        final AnchorPane copyOfAnchorPane = new AnchorPane();
        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(265, 425); // Better proportions for modern dark cards
        copyOfAnchorPane.setMinSize(240, 405);
        copyOfAnchorPane.setMaxSize(290, 450);
        copyOfAnchorPane.getStyleClass().addAll("anchorfilm", "animated-button", "film-card");
        copyOfAnchorPane.setUserData(film); // Store film data

        final ImageView imageView = new ImageView();
        try {
            // Load image directly from URL
            String imagePath = film.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    // Try loading directly as URL
                    Image image = new Image(imagePath, true); // Enable background loading
                    imageView.setImage(image);
                }
 catch (Exception e) {
                    // Fallback to resource if URL fails
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                        LOGGER.log(Level.WARNING, "Failed to load image URL, using default: " + imagePath, e);
                    }
 catch (Exception e2) {
                        LOGGER.log(Level.SEVERE, "Failed to load both URL and default image", e2);
                    }

                }

            }
 else {
                LOGGER.log(Level.WARNING, "Image path is null or empty for film ID: " + film.getId());
                // Set a default image
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            }


            // Set responsive image dimensions with red glow effect
            imageView.setFitWidth(235);
            imageView.setFitHeight(295); // Taller for movie poster
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setLayoutX(15);
            imageView.setLayoutY(10);
            imageView.getStyleClass().add("film-card-image");

        }
 catch (final Exception e) {
            // Handle exception or set a default image
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            }
 catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Could not load any image, even default", e2);
            }

            LOGGER.log(Level.WARNING, "Failed to load image for film: " + film.getId(), e);
        }


        // Film title with white text and red glow
        final Label nomFilm = new Label(film.getName());
        nomFilm.setLayoutX(15);
        nomFilm.setLayoutY(315);
        nomFilm.setPrefSize(235, 40);
        nomFilm.setMaxWidth(235);
        nomFilm.setWrapText(true);
        nomFilm.setFont(new Font(15));
        nomFilm.getStyleClass().addAll("labeltext", "animated-text", "film-card-title");

        // Rating with white text
        final Label ratefilm = new Label();
        ratefilm.setLayoutX(15);
        ratefilm.setLayoutY(358);
        ratefilm.setPrefSize(120, 25);
        ratefilm.setFont(new Font(13));
        ratefilm.getStyleClass().addAll("labeltext", "film-card-rating");
        final double rate = new FilmRatingService().getAvergeRating(film.getId());
        ratefilm.setText(String.format("%.1f/5", rate));

        final FilmRating ratingFilm = new FilmRatingService().ratingExists(film.getId(), 2L);
        this.filmRate.setRating(null != ratingFilm ? ratingFilm.getRating() : 0);

        // Golden star icon
        final FontIcon etoile = new FontIcon();
        etoile.setIconLiteral("fa-star");
        etoile.setLayoutX(125);
        etoile.setLayoutY(373);
        etoile.setIconSize(16);
        etoile.setFill(Color.web("#ffaa00"));
        etoile.setEffect(new DropShadow(5, Color.rgb(255, 170, 0, 0.8)));

        // Reserve button with red gradient
        final Button button = new Button("RESERVE");
        button.setLayoutX(15);
        button.setLayoutY(385);
        button.setPrefSize(235, 36);
        button.getStyleClass().addAll("action-button", "animated-button", "film-reserve-button");
        button.setOnAction(event -> {
            try {
                this.switchtopayment(nomFilm.getText());
            }
 catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }
);

        // View Details hyperlink with red theme
        final Hyperlink hyperlink = new Hyperlink("View Details");
        hyperlink.setLayoutX(145);
        hyperlink.setLayoutY(361);
        hyperlink.getStyleClass().add("film-details-link");
        hyperlink.setOnAction(event -> {
            this.detalAnchorPane.setVisible(true);
            this.anchorPaneFilm.setOpacity(0.26);
            this.anchorPaneFilm.setDisable(true);
            final Film film1 = new Film(film);
            this.filmId = film.getId();
            this.filmNomDetail.setText(film1.getName());
            this.descriptionDETAILfilm.setText(
                    film1.getDescription() + "\nTime:" + film1.getDuration() + "\nYear:" + film1.getReleaseYear()
                            + "\nCategories: " + new FilmCategoryService().getCategoryNames(film1.getId())
                            + "\nActors: " + new ActorFilmService().getActorsNames(film1.getId()));

            // Load detail image from URL
            try {
                this.imagefilmDetail.setImage(new Image(film1.getImage()));
            }
 catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load detail image URL: " + film1.getImage(), e);
                try {
                    // Fallback to default
                    this.imagefilmDetail.setImage(
                            new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                }
 catch (Exception e2) {
                    LOGGER.log(Level.SEVERE, "Failed to load default detail image", e2);
                }

            }


            final double rate1 = new FilmRatingService().getAvergeRating(film1.getId());
            this.labelavregeRate.setText(rate1 + "/5");
            final FilmRating ratingFilm1 = new FilmRatingService().ratingExists(film1.getId(), 2L);
            final Rating rateFilm = new Rating();
            rateFilm.setLayoutX(103);
            rateFilm.setLayoutY(494);
            rateFilm.setPrefSize(199, 35);
            rateFilm.setRating(null != ratingFilm1 ? ratingFilm1.getRating() : 0);
            // Stage stage = (Stage) hyperlink.getScene().getWindow();
            String text = film1.getName();// Créer un objet QRCodeWriter pour générer le QR code
            String url = FilmService.getIMDBUrlbyNom(text);
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix bitMatrix;
            try {
                bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            }
 catch (final WriterException e) {
                throw new RuntimeException(e);
            }

            // Convertir la matrice de bits en image BufferedImage
            final BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            this.qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            this.qrcode.setVisible(true);
            rateFilm.ratingProperty().addListener((observableValue, number, t1) -> {
                final FilmRatingService ratingFilmService = new FilmRatingService();
                final FilmRating ratingFilm2 = ratingFilmService.ratingExists(film1.getId(), 2L);
                FilmUserController.LOGGER.info("---------   " + film1.getId());
                if (null != ratingFilm2) {
                    ratingFilmService.delete(ratingFilm2);
                }

                ratingFilmService
                        .create(new FilmRating(film1, (Client) new UserService().getUserById(2L), t1.intValue()));
                final double rate2 = new FilmRatingService().getAvergeRating(film1.getId());
                this.labelavregeRate.setText(rate2 + "/5");
                ratefilm.setText(rate2 + "/5");
                this.topthreeVbox.getChildren().clear();
                for (int i = 0; 3 > i; i++) {
                    this.topthreeVbox.getChildren().add(this.createtopthree(i));
                }

            }
);
            this.trailer_Button.setOnAction(trailerEvent -> {
                this.anchorPane_Trailer.getChildren().forEach(node -> {
                    node.setDisable(false);
                }
);
                final WebView webView = new WebView();
                FilmUserController.LOGGER.info(film1.getName());
                Platform.runLater(() -> {
                    webView.getEngine().load(new FilmService().getTrailerFilm(film1.getName()));
                }
);
                FilmUserController.LOGGER.info("film passed");
                this.anchorPane_Trailer.setVisible(true);
                this.anchorPane_Trailer.getChildren().add(webView);
                this.anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if (KeyCode.ESCAPE == keyEvent.getCode()) {
                        this.anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(true);
                        }
);
                        this.anchorPane_Trailer.setVisible(false);
                    }

                }
);
            }
);
            this.detalAnchorPane.getChildren().add(rateFilm);
        }
);
        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);
        return copyOfAnchorPane;
    }


    /**
     * Create a styled AnchorPane containing an actor's image, name with film count, and biography for the actor at the given placement.
     *
     * <p>If no actor exists for the provided placement, an empty styled AnchorPane with the same layout is returned.</p>
     *
     * @param actorPlacement the placement or ranking used to look up the actor (e.g., 1 for top actor)
     * @return an AnchorPane populated with the actor's image, name and number of appearances, and biography, or an empty styled AnchorPane when the actor is not found
     */
    public AnchorPane createActorDetails(final int actorPlacement) {
        final ActorService as = new ActorService();
        final Actor actor = as.getActorByPlacement(actorPlacement);
        final AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefSize(480, 200);
        anchorPane.getStyleClass().addAll("top-film-card", "animated-button");
        anchorPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(139, 0, 0, 0.95), rgba(178, 34, 34, 0.85));"
                        +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.8), 20, 0, 0, 5);" +
                        "-fx-border-color: rgba(255, 68, 68, 0.4);" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 20;");

        if (null != actor) {
            final ImageView imageView = new ImageView();
            try {
                if (actor.getImage() != null && !actor.getImage().isEmpty()) {
                    try {
                        // Try loading directly as a URL
                        imageView.setImage(new Image(actor.getImage()));
                    }
 catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load actor image URL: " + actor.getImage(), e);
                        try {
                            // Try loading a default image
                            imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                        }
 catch (Exception e2) {
                            LOGGER.log(Level.SEVERE, "Failed to load default actor image", e2);
                        }

                    }

                }
 else {
                    // If image is null or empty, try to load default
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                    }
 catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load default actor image", e);
                    }

                }

            }
 catch (final Exception e) {
                FilmUserController.LOGGER.log(Level.SEVERE, "Error processing actor image", e);
            }

            imageView.setLayoutX(15);
            imageView.setLayoutY(10);
            imageView.setFitHeight(180);
            imageView.setFitWidth(130);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.7), 15, 0, 0, 3);" +
                            "-fx-background-radius: 12;");

            // Combine actor name and number of appearances in one label
            final String actorDetailsText = actor.getName().trim() + ": " + actor.getNumberOfAppearances() + " Films";
            FilmUserController.LOGGER.info(actorDetailsText);
            final Label actorDetails = new Label(actorDetailsText);
            actorDetails.setLayoutX(160);
            actorDetails.setLayoutY(15);
            actorDetails.setPrefSize(300, 40);
            actorDetails.setMaxWidth(300);
            actorDetails.setWrapText(true);
            actorDetails.setFont(new Font(18));
            actorDetails.setTextFill(Color.WHITE);
            actorDetails.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, #ff4444, 10, 0, 1, 1);");

            // Actor biography - fix method name
            final TextArea actorBio = new TextArea(actor.getBiography()); // Changed from getBiographie
            actorBio.setLayoutX(160);
            actorBio.setLayoutY(65);
            actorBio.setPrefSize(300, 120);
            actorBio.setMaxWidth(300);
            actorBio.setWrapText(true);
            actorBio.setEditable(false);
            actorBio.setStyle(
                    "-fx-control-inner-background: rgba(10, 5, 5, 0.6);" +
                            "-fx-text-fill: rgba(255, 255, 255, 0.9);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: rgba(139, 0, 0, 0.3);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 8;" +
                            "-fx-font-size: 12px;" +
                            "-fx-opacity: 1;");
            anchorPane.getChildren().addAll(imageView, actorDetails, actorBio);
        }

        return anchorPane;
    }


    /**
     * Builds a styled UI card for the film at the given rank in the average-rating leaderboard.
     *
     * The returned pane contains the film poster (if available), title, a disabled average-rating control,
     * a numeric rating label, and a "RESERVE" button; if no film exists at the specified rank the method
     * returns an empty styled AnchorPane.
     *
     * @param filmRank zero-based index into the average-rating-sorted film list (0 returns the top film)
     * @return an AnchorPane containing the top-film card UI for the specified rank, or an empty styled pane when no film is available
     */
    public AnchorPane createtopthree(final int filmRank) {
        final List<FilmRating> ratingFilmList = new FilmRatingService().getAverageRatingSorted(); // Fixed method name
        final AnchorPane anchorPane = new AnchorPane();

        if (ratingFilmList.size() > filmRank) {
            anchorPane.setLayoutX(0);
            anchorPane.setLayoutY(0);
            anchorPane.setPrefSize(420, 180); // Better responsive size for top 3
            anchorPane.getStyleClass().addAll("top-film-card", "animated-button");
            anchorPane.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, rgba(139, 0, 0, 0.95), rgba(178, 34, 34, 0.85));"
                            +
                            "-fx-background-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.8), 20, 0, 0, 5);" +
                            "-fx-border-color: rgba(255, 68, 68, 0.4);" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 20;");

            final FilmRating ratingFilm = ratingFilmList.get(filmRank);
            if (ratingFilm == null || ratingFilm.getFilm() == null) { // Fixed method name
                LOGGER.warning("Invalid rating film data at rank: " + filmRank);
                return anchorPane;
            }


            final Film film = ratingFilm.getFilm(); // Fixed method name
            final ImageView imageView = new ImageView();

            try {
                String imagePath = film.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    // Load directly from URL
                    try {
                        Image image = new Image(imagePath);
                        imageView.setImage(image);
                        imageView.setFitWidth(130); // Adjusted for top 3 section
                        imageView.setFitHeight(160); // Maintain movie poster ratio
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setLayoutX(15);
                        imageView.setLayoutY(10);
                        imageView.setStyle(
                                "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.7), 15, 0, 0, 3);" +
                                        "-fx-background-radius: 12;");
                    }
 catch (Exception e) {
                        LOGGER.warning("Failed to load image from URL: " + imagePath + ", " + e.getMessage());
                        // Try loading default image
                        try {
                            Image defaultImage = new Image(getClass().getResourceAsStream("/img/films/default.jpg"));
                            imageView.setImage(defaultImage);
                            imageView.setFitWidth(130);
                            imageView.setFitHeight(160);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setLayoutX(15);
                            imageView.setLayoutY(10);
                            imageView.setStyle(
                                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.7), 15, 0, 0, 3);" +
                                            "-fx-background-radius: 12;");
                        }
 catch (Exception e2) {
                            LOGGER.severe("Failed to load default image: " + e2.getMessage());
                        }

                    }

                }

            }
 catch (Exception e) {
                LOGGER.warning("Error loading image for film: " + film.getId() + ", " + e.getMessage());
            }


            try {
                final Label nomFilm = new Label(film.getName() != null ? film.getName() : "Untitled");
                nomFilm.setLayoutX(145);
                nomFilm.setLayoutY(15);
                nomFilm.setPrefSize(260, 35);
                nomFilm.setMaxWidth(280);
                nomFilm.setWrapText(true);
                nomFilm.setFont(new Font(20));
                nomFilm.setTextFill(Color.WHITE);
                nomFilm.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-effect: dropshadow(gaussian, #ff4444, 10, 0, 1, 1);");

                final Button button = new Button("RESERVE");
                button.setLayoutX(145);
                button.setLayoutY(135);
                button.setPrefSize(260, 38);
                button.getStyleClass().addAll("action-button", "animated-button");
                button.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #8b0000, #b22222);" +
                                "-fx-background-radius: 18;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 13px;" +
                                "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.9), 12, 0, 0, 3);");
                button.setOnMouseEntered(e -> {
                    button.setStyle(
                            "-fx-background-color: linear-gradient(to bottom right, #ff4444, #ff6666);" +
                                    "-fx-background-radius: 18;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255, 68, 68, 1.0), 18, 0, 0, 5);" +
                                    "-fx-scale-x: 1.05;" +
                                    "-fx-scale-y: 1.05;");
                }
);
                button.setOnMouseExited(e -> {
                    button.setStyle(
                            "-fx-background-color: linear-gradient(to bottom right, #8b0000, #b22222);" +
                                    "-fx-background-radius: 18;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.9), 12, 0, 0, 3);");
                }
);

                final Rating rating = new Rating();
                rating.setLayoutX(145);
                rating.setLayoutY(55);
                rating.setPrefSize(176, 32);
                rating.setPartialRating(true);
                rating.setStyle(
                        "-fx-rating-fill: #ffaa00;" +
                                "-fx-rating-empty-fill: rgba(255, 170, 0, 0.2);");

                final double rate = new FilmRatingService().getAvergeRating(film.getId());
                rating.setRating(rate);
                rating.setDisable(true);

                // Rating label
                final Label rateLabel = new Label(String.format("%.1f/5", rate));
                rateLabel.setLayoutX(145);
                rateLabel.setLayoutY(95);
                rateLabel.setPrefSize(100, 25);
                rateLabel.setFont(new Font(16));
                rateLabel.setTextFill(Color.web("#ffaa00"));
                rateLabel.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.9), 3, 0, 0, 0);");

                anchorPane.getChildren().addAll(nomFilm, button, rating, rateLabel);
                if (imageView.getImage() != null) {
                    anchorPane.getChildren().add(imageView);
                }

            }
 catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error creating film card components", e);
            }

        }

        return anchorPane;
    }


    /**
     * Returns the distinct release years present among the retrieved films.
     *
     * @return a list of unique release years as Integer objects, in the order they were encountered
     */
    private List<Integer> getCinemaYears() {
        final FilmService cinemaService = new FilmService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Film> cinemas = cinemaService.read(pageRequest).getContent();
        // Extraire les années de réalisation uniques des films
        return cinemas.stream().map(Film::getReleaseYear).distinct().collect(Collectors.toList());
    }


    /**
     * Displays the year filter pane, dims the main film view, and populates the pane with
     * checkboxes for each available film release year.
     *
     * The created checkboxes are stored in the controller's `yearsCheckBoxes` list for later use.
     *
     * @param event the ActionEvent that triggered showing the filter pane
     */
    @FXML
    void filtrer(final ActionEvent event) {
        this.flowpaneFilm.setOpacity(0.5);
        this.Anchore_Pane_filtrage.setVisible(true);
        // Nettoyer la liste des cases à cocher
        this.yearsCheckBoxes.clear();
        // Récupérer les années de réalisation uniques depuis la base de données
        final List<Integer> years = this.getCinemaYears();
        // Créer des VBox pour les années de réalisation
        final VBox yearsCheckBoxesVBox = new VBox();
        final Label yearLabel = new Label("Années de réalisation");
        yearLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        yearsCheckBoxesVBox.getChildren().add(yearLabel);
        for (final Integer year : years) {
            final CheckBox checkBox = new CheckBox(String.valueOf(year));
            yearsCheckBoxesVBox.getChildren().add(checkBox);
            this.yearsCheckBoxes.add(checkBox);
        }

        yearsCheckBoxesVBox.setLayoutX(25);
        yearsCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le filterAnchor
        this.Anchore_Pane_filtrage.getChildren().addAll(yearsCheckBoxesVBox);
        this.Anchore_Pane_filtrage.setVisible(true);
    }


    /**
     * Restores the film detail pane and hides the comments pane.
     *
     * Sets the detail anchor pane's opacity to 1 and makes it visible, and hides the comments anchor pane.
     */
    @FXML
    void closercommets(final ActionEvent event) {
        this.detalAnchorPane.setOpacity(1);
        this.AnchorComments.setVisible(false);
        this.detalAnchorPane.setVisible(true);
    }


    /**
     * Apply the selected release-year filters to the current film list and update the UI with the matching films.
     *
     * If no years are selected, all films from the current list are displayed.
     */
    @FXML
    void filtrercinema(final ActionEvent event) {
        this.flowpaneFilm.setOpacity(1);
        this.Anchore_Pane_filtrage.setVisible(false);
        // Récupérer les années de réalisation sélectionnées
        final List<Integer> selectedYears = this.getSelectedYears();
        // Filtrer les films en fonction des années de réalisation sélectionnées
        final List<Film> filteredCinemas = this.l1.stream()
                .filter(cinema -> selectedYears.isEmpty() || selectedYears.contains(cinema.getReleaseYear()))
                .collect(Collectors.toList());
        // Afficher les films filtrés
        this.flowpaneFilm.getChildren().clear();
        this.createfilmCards(filteredCinemas);
    }


    /**
     * Collects the years selected by the user from the filter checkboxes.
     *
     * Parses the text of each selected checkbox as an integer and returns them.
     *
     * @return a list of integers corresponding to the selected year checkboxes; empty if none are selected
     */
    private List<Integer> getSelectedYears() {
        // Récupérer les années de réalisation sélectionnées dans l'AnchorPane de
        // filtrage
        return this.yearsCheckBoxes.stream().filter(CheckBox::isSelected)
                .map(checkBox -> Integer.parseInt(checkBox.getText())).collect(Collectors.toList());
    }


    /**
     * Switches the application view to the series client UI by loading
     * "/ui/series/SeriesClient.fxml" and replacing the current stage scene.
     *
     * @param event the action event that triggered the view change
     */
    public void switchtoajouterCinema(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.product.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        }
 catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Opens the event-listing view and replaces the current scene with that UI.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    public void switchtevent(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui//ui/AffichageEvenementClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.event_button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        }
 catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Switches the current window to the cinema dashboard view.
     *
     * Loads the cinema dashboard FXML and replaces the scene on the current stage,
     * sizing the new scene to 1280x700.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    public void switchtcinemaaa(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.Cinema_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        }
 catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Loads the product listing UI (AfficherProductClient.fxml) and replaces the current window's scene with it.
     *
     * @param event the UI action event that triggered the scene switch
     */
    public void switchtoajouterproduct(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.product.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        }
 catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Replace the current window's scene with the Series client UI loaded from FXML.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    public void switchtoSerie(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.SerieButton.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        }
 catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Add the current text in the comment input as a persisted comment for the currently selected film.
     *
     * <p>If the input is empty a warning alert is shown and no comment is created. Otherwise a
     * FilmComment is constructed using the entered text, the user returned by
     * UserService.getUserById(4L), and the film identified by this.filmId; the comment is persisted
     * via FilmCommentService.create, logged, and the input field is cleared.
     */
    @FXML
    void addCommentaire() {
        final String message = this.txtAreaComments.getText();
        if (message.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        }
 else {
            final FilmComment commentaire = new FilmComment(message, (Client) new UserService().getUserById(4L), // Fixed
                                                                                                                 // type
                    new FilmService().getFilm(this.filmId));
            FilmUserController.LOGGER.info(commentaire + " " + new UserService().getUserById(4L)); // Fixed type
            final FilmCommentService cinemaCommentService = new FilmCommentService();
            cinemaCommentService.create(commentaire);
            this.txtAreaComments.clear();
        }

    }


    /**
     * Add the current comment for the selected film and refresh the displayed comment list.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.filmId);
    }


    /**
     * Computes a numeric similarity score between a film and a user's genre preferences.
     *
     * The score combines the sum of the user's weights for the film's genres and a contribution
     * based on the film's average rating (weighted by 0.3) so that higher values indicate a closer match.
     *
     * @param film            the film to score
     * @param userPreferences a map from genre name to preference weight used when scoring genres
     * @return                a numeric similarity score; larger values indicate greater similarity to the user's preferences
     */
    private double calculateSimilarityScore(Film film, Map<String, Double> userPreferences) {
        double score = 0.0;
        List<String> filmGenres = Arrays.asList(new FilmCategoryService().getCategoryNames(film.getId()).split(","));

        for (String genre : filmGenres) {
            score += userPreferences.getOrDefault(genre.trim(), 0.0);
        }


        // Add rating weight
        double avgRating = new FilmRatingService().getAvergeRating(film.getId());
        score += avgRating * 0.3;

        return score;
    }


    /**
     * Builds a horizontal comment card containing the commenter's profile image and the comment text.
     *
     * @param commentaire the FilmComment whose author, profile image, and text will be displayed
     * @return an HBox containing the profile image and a VBox with the commenter name and comment text
     */
    private HBox addCommentToView(final FilmComment commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);

        // Image de l'utilisateur
        final String imageUrl = commentaire.getClient().getPhotoDeProfil();
        Image userImage = null;

        try {
            if (null != imageUrl && !imageUrl.isEmpty()) {
                // Load directly from URL
                userImage = new Image(imageUrl);
            }

        }
 catch (Exception e) {
            LOGGER.warning("Failed to load profile image from URL: " + imageUrl);
        }


        // If loading from URL failed or image was null/empty, use default
        if (userImage == null) {
            try {
                userImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
            }
 catch (Exception e) {
                LOGGER.severe("Failed to load default profile image: " + e.getMessage());
            }

        }


        final ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);

        // Ajouter l'image au cercle
        final Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);

        // Création de la boîte pour l'image et la bordure du cercle
        final HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);

        // Création du conteneur pour la carte du commentaire
        final HBox cardContainer = new HBox();
        cardContainer.setStyle(
                "-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");

        // Nom de l'utilisateur
        final Text userName = new Text(
                commentaire.getClient().getFirstName() + " " + commentaire.getClient().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");

        // Commentaire
        final Text commentText = new Text(commentaire.getComment());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique

        // Création de la boîte pour le texte du commentaire
        final VBox textBox = new VBox();

        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);

        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);

        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        final HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);

        return contentContainer;
    }


    /**
     * Populates the controller's ScrollPaneComments with UI views for all comments belonging to the given film.
     *
     * @param filmId the id of the film whose comments will be displayed
     */
    @FXML
    public void displayAllComments(final Long filmId) {
        // Get comments from database
        final FilmCommentService cinemaCommentService = new FilmCommentService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<FilmComment> allComments = cinemaCommentService.read(pageRequest).getContent();
        final List<FilmComment> filmComments = new ArrayList<>();

        // Filter comments for this film
        for (final FilmComment comment : allComments) {
            if (comment.getFilm().getId().equals(filmId)) {
                filmComments.add(comment);
            }

        }


        // Display comments
        final VBox allCommentsContainer = new VBox();
        for (final FilmComment comment : filmComments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        this.ScrollPaneComments.setContent(allCommentsContainer);
    }


    /**
     * Shows film comments in a panel.
     *
     * @param event The mouse event that triggered this action
     */
    @FXML
    void afficherAnchorComment(final MouseEvent event) {
        this.AnchorComments.setVisible(true);
        this.displayAllComments(this.filmId);
    }

}

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
     * Queries a list of films for any that contain a specified search term in their
     * name, and returns a list of matches.
     *
     * @param liste
     *                  list of films that will be searched for matching titles
     *                  within the
     *                  provided `recherche` parameter.
     *                  <p>
     *                  - It is a list of `Film` objects - Each element in the list
     *                  has a
     *                  `nom` attribute that can contain the search query
     * @param recherche
     *                  search query, which is used to filter the list of films in
     *                  the
     *                  function.
     * @returns a list of `Film` objects that contain the searched string in their
     *          name.
     *          <p>
     *          - The list of films is filtered based on the search query, resulting
     *          in a subset of films that match the query. - The list contains only
     *          films with a non-null `nom` attribute and containing the search
     *          query in their name. - The list is returned as a new list of films,
     *          which can be used for further processing or analysis.
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
     * Loads an FXML user interface from a resource file, sets data for the
     * controller, and displays the stage with the loaded scene.
     *
     * @param nom
     *            name of the client for which the payment user interface is to be
     *            displayed.
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
     * Creates film cards for a list of films by creating an AnchorPane container
     * for each card and adding it to a `FlowPane` containing other cards.
     *
     * @param Films
     *              list of films to create film cards for, which are then added as
     *              children of the `flowpaneFilm`.
     *              <p>
     *              - `Film` objects are contained in the list. - Each `Film` object
     *              has various attributes, such as title, director, year of
     *              release,
     *              etc.
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

    private void setupBasicUI() {
        PageRequest pageRequest = new PageRequest(0, 3);
        this.top3combobox.getItems().addAll("Top 3 Films", "Top 3 Actors");
        this.top3combobox.setValue("Top 3 Films");
        this.top3combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Top 3 Films".equals(newValue)) {
                this.topthreeVbox1.setVisible(false);
                this.topthreeVbox.setVisible(true);
            } else if ("Top 3 Actors".equals(newValue)) {
                final ObservableList<Node> topthreevboxactorsChildren = this.topthreeVbox1.getChildren();
                topthreevboxactorsChildren.clear();
                this.topthreeVbox.setVisible(false);
                this.topthreeVbox1.setVisible(true);
                for (int i = 1; i < this.flowpaneFilm.getChildren().size() && 4 > i; i++) {
                    topthreevboxactorsChildren.add(this.createActorDetails(i));
                }
                this.topthreeVbox1.setSpacing(10);
            }
        });
        this.tricomboBox.getItems().addAll("nom", "annederalisation");
        this.tricomboBox.setValue("");
        this.tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            this.flowpaneFilm.getChildren().clear();
            final List<Film> filmList = new FilmService().sort(pageRequest, t1).getContent();
            for (final Film film : filmList) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        });
        final FilmService filmService1 = new FilmService();
        PageRequest filmPageRequest = new PageRequest(0, 10);
        this.l1 = filmService1.read(filmPageRequest).getContent();
        this.serach_film_user.textProperty().addListener((observable, oldValue, newValue) -> {
            final List<Film> produitsRecherches = FilmUserController.rechercher(this.l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            this.flowpaneFilm.getChildren().clear();
            this.createfilmCards(produitsRecherches);
        });
        this.flowpaneFilm = new FlowPane();
        this.filmScrollPane.setContent(this.flowpaneFilm);
        this.filmScrollPane.setFitToWidth(true);
        this.filmScrollPane.setFitToHeight(true);
        this.topthreeVbox.setSpacing(10);

        // String trailerURL = filmService.getTrailerFilm("garfield");
        this.flowpaneFilm.setHgap(10);
        this.flowpaneFilm.setVgap(10);
        this.closeDetailFilm.setOnAction(event -> {
            this.detalAnchorPane.setVisible(false);
            this.anchorPaneFilm.setOpacity(1);
            this.anchorPaneFilm.setDisable(false);
        });
        // Set the padding
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
                    });
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
                        });
                    }
                }
            });

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
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load recommendations", e);
            }
        }
    }

    /**
     * Filters film cards by name to show only matching films
     * 
     * @param keyword The search term to filter by
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
     * Creates a UI component representing a movie card with various details and
     * ratings. It generates a QR code for the movie's IMDB page, which can be
     * scanned to open the page in a browser. The function also adds event listeners
     * to handle clicks on the movie card and the QR code.
     *
     * @param film
     *             film object that will be displayed in the anchor pane, and is
     *             used
     *             to retrieve the film's information such as title, image, rating,
     *             and trailer link.
     *             <p>
     *             - `id`: a unique identifier for the film - `nom`: the film's
     *             title
     *             - `description`: a brief description of the film - `duree`: the
     *             film's duration - `annderalisation`: the film's release date -
     *             `categories`: an array of categories the film belongs to -
     *             `actors`: an array of actors appearing in the film.
     * @returns an AnchorPane with a QR code generator, trailer player, and rating
     *          system for a given film.
     *          <p>
     *          - `hyperlink`: A Hyperlink component that displays the film's title
     *          and opens the IMDB page when clicked. - `imagefilmDetail`: An Image
     *          component that displays the film's poster image. -
     *          `descriptionDETAILfilm`: A Text component that displays the film's
     *          detailed description. - `labelavregeRate`: A Label component that
     *          displays the average rating of the film. - `ratefilm`: A Text
     *          component that displays the current rating of the film. -
     *          `topthreeVbox`: A VBox component that displays the top three actors
     *          of the film. - `trailer_Button`: A Button component that plays the
     *          film's trailer when clicked.
     *          <p>
     *          Note: The output is a JavaFX AnchorPane that contains all the
     *          components explained above.
     */
    private AnchorPane createFilmCard(final Film film) {
        final AnchorPane copyOfAnchorPane = new AnchorPane();
        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(250, 400); // Increased size for better proportions
        copyOfAnchorPane.getStyleClass().add("anchorfilm");

        final ImageView imageView = new ImageView();
        try {
            // Load image directly from URL
            String imagePath = film.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    // Try loading directly as URL
                    Image image = new Image(imagePath);
                    imageView.setImage(image);
                } catch (Exception e) {
                    // Fallback to resource if URL fails
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                        LOGGER.log(Level.WARNING, "Failed to load image URL, using default: " + imagePath, e);
                    } catch (Exception e2) {
                        LOGGER.log(Level.SEVERE, "Failed to load both URL and default image", e2);
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Image path is null or empty for film ID: " + film.getId());
                // Set a default image
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            }

            // Set consistent image dimensions
            imageView.setFitWidth(220); // Slightly smaller than card width
            imageView.setFitHeight(300); // Taller for movie poster aspect ratio
            imageView.setPreserveRatio(true); // Maintain aspect ratio
            imageView.setSmooth(true); // Better image quality
            imageView.setLayoutX(15); // Center in card
            imageView.setLayoutY(10); // Small top margin

        } catch (final Exception e) {
            // Handle exception or set a default image
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Could not load any image, even default", e2);
            }
            LOGGER.log(Level.WARNING, "Failed to load image for film: " + film.getId(), e);
        }

        // Adjust positions of other elements to account for new image size
        final Label nomFilm = new Label(film.getName());
        nomFilm.setLayoutX(15);
        nomFilm.setLayoutY(320); // Position below image
        nomFilm.setPrefSize(220, 32);
        nomFilm.setFont(new Font(18)); // Copy the font size
        nomFilm.getStyleClass().addAll("labeltext");

        final Label ratefilm = new Label(film.getName());
        ratefilm.setLayoutX(15);
        ratefilm.setLayoutY(350); // Adjust for new layout
        ratefilm.setPrefSize(176, 32);
        ratefilm.setFont(new Font(16)); // Copy the font size
        ratefilm.getStyleClass().addAll("labeltext");
        final double rate = new FilmRatingService().getAvergeRating(film.getId());
        ratefilm.setText(rate + "/5");
        final FilmRating ratingFilm = new FilmRatingService().ratingExists(film.getId(), 2L);
        this.filmRate.setRating(null != ratingFilm ? ratingFilm.getRating() : 0);
        final FontIcon etoile = new FontIcon();
        etoile.setIconLiteral("fa-star");
        etoile.setLayoutX(128);
        etoile.setLayoutY(247);
        etoile.setFill(Color.web("#f2b604"));
        final Button button = new Button("reserve");
        button.setLayoutX(39);
        button.setLayoutY(385); // Move to bottom
        button.setPrefSize(172, 42);
        button.getStyleClass().addAll("sale");
        button.setOnAction(event -> {
            try {
                this.switchtopayment(nomFilm.getText());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        final Hyperlink hyperlink = new Hyperlink("Details");
        hyperlink.setLayoutX(89);
        hyperlink.setLayoutY(251);
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
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load detail image URL: " + film1.getImage(), e);
                try {
                    // Fallback to default
                    this.imagefilmDetail.setImage(
                            new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                } catch (Exception e2) {
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
            } catch (final WriterException e) {
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
            });
            this.trailer_Button.setOnAction(trailerEvent -> {
                this.anchorPane_Trailer.getChildren().forEach(node -> {
                    node.setDisable(false);
                });
                final WebView webView = new WebView();
                FilmUserController.LOGGER.info(film1.getName());
                Platform.runLater(() -> {
                    webView.getEngine().load(new FilmService().getTrailerFilm(film1.getName()));
                });
                FilmUserController.LOGGER.info("film passed");
                this.anchorPane_Trailer.setVisible(true);
                this.anchorPane_Trailer.getChildren().add(webView);
                this.anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if (KeyCode.ESCAPE == keyEvent.getCode()) {
                        this.anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(true);
                        });
                        this.anchorPane_Trailer.setVisible(false);
                    }
                });
            });
            this.detalAnchorPane.getChildren().add(rateFilm);
        });
        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);
        return copyOfAnchorPane;
    }

    /**
     * Performs createActorDetails operation.
     *
     * @return the result of the operation
     */
    public AnchorPane createActorDetails(final int actorPlacement) {
        final ActorService as = new ActorService();
        final Actor actor = as.getActorByPlacement(actorPlacement);
        final AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefSize(244, 226);
        anchorPane.getStyleClass().add("meilleurfilm");
        if (null != actor) {
            final ImageView imageView = new ImageView();
            try {
                if (actor.getImage() != null && !actor.getImage().isEmpty()) {
                    try {
                        // Try loading directly as a URL
                        imageView.setImage(new Image(actor.getImage()));
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load actor image URL: " + actor.getImage(), e);
                        try {
                            // Try loading a default image
                            imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                        } catch (Exception e2) {
                            LOGGER.log(Level.SEVERE, "Failed to load default actor image", e2);
                        }
                    }
                } else {
                    // If image is null or empty, try to load default
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load default actor image", e);
                    }
                }
            } catch (final Exception e) {
                FilmUserController.LOGGER.log(Level.SEVERE, "Error processing actor image", e);
            }
            imageView.setLayoutX(21);
            imageView.setLayoutY(21);
            imageView.setFitHeight(167);
            imageView.setFitWidth(122);
            imageView.getStyleClass().addAll("bg-white");
            // Combine actor name and number of appearances in one label
            final String actorDetailsText = actor.getName().trim() + ": " + actor.getNumberOfAppearances() + " Films";
            FilmUserController.LOGGER.info(actorDetailsText);
            final Label actorDetails = new Label(actorDetailsText);
            actorDetails.setLayoutX(153);
            actorDetails.setLayoutY(8); // Adjusted to top, similar to imageView
            actorDetails.setPrefSize(500, 70);
            actorDetails.setFont(new Font(22));
            actorDetails.setTextFill(Color.WHITE);
            // Actor biography - fix method name
            final TextArea actorBio = new TextArea(actor.getBiography()); // Changed from getBiographie
            actorBio.setLayoutX(153);
            actorBio.setLayoutY(75); // Positioned directly under actorDetails label
            actorBio.setPrefSize(400, 100); // You can adjust this size as needed
            actorBio.setWrapText(true);
            actorBio.setEditable(false);
            // Set the background of the TextArea to transparent and text color to white
            actorBio.setStyle("-fx-control-inner-background:#de3030 ; -fx-text-fill: WHITE; -fx-opacity: 1;");
            anchorPane.getChildren().addAll(imageView, actorDetails, actorBio);
        }
        return anchorPane;
    }

    /**
     * Performs createtopthree operation.
     *
     * @return the result of the operation
     */
    public AnchorPane createtopthree(final int filmRank) {
        final List<FilmRating> ratingFilmList = new FilmRatingService().getAverageRatingSorted(); // Fixed method name
        final AnchorPane anchorPane = new AnchorPane();

        if (ratingFilmList.size() > filmRank) {
            anchorPane.setLayoutX(0);
            anchorPane.setLayoutY(0);
            anchorPane.setPrefSize(544, 226);
            anchorPane.getStyleClass().add("meilleurfilm");

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
                        imageView.setFitWidth(180); // Adjusted size for top 3 section
                        imageView.setFitHeight(260); // Maintain movie poster ratio
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setLayoutX(21);
                        imageView.setLayoutY(21);
                        imageView.getStyleClass().addAll("bg-white");
                    } catch (Exception e) {
                        LOGGER.warning("Failed to load image from URL: " + imagePath + ", " + e.getMessage());
                        // Try loading default image
                        try {
                            Image defaultImage = new Image(getClass().getResourceAsStream("/img/films/default.jpg"));
                            imageView.setImage(defaultImage);
                            imageView.setFitWidth(180);
                            imageView.setFitHeight(260);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setLayoutX(21);
                            imageView.setLayoutY(21);
                            imageView.getStyleClass().addAll("bg-white");
                        } catch (Exception e2) {
                            LOGGER.severe("Failed to load default image: " + e2.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Error loading image for film: " + film.getId() + ", " + e.getMessage());
            }

            try {
                final Label nomFilm = new Label(film.getName() != null ? film.getName() : "Untitled");
                nomFilm.setLayoutX(153);
                nomFilm.setLayoutY(87);
                nomFilm.setPrefSize(205, 35);
                nomFilm.setFont(new Font(22));
                nomFilm.setTextFill(Color.WHITE);

                final Button button = new Button("reserve");
                button.setLayoutX(346);
                button.setLayoutY(154);
                button.setPrefSize(172, 42);
                button.getStyleClass().addAll("sale");

                final Rating rating = new Rating();
                rating.setLayoutX(344);
                rating.setLayoutY(38);
                rating.setPrefSize(176, 32);
                rating.setPartialRating(true);

                final double rate = new FilmRatingService().getAvergeRating(film.getId());
                rating.setRating(rate);
                rating.setDisable(true);

                anchorPane.getChildren().addAll(nomFilm, button, rating);
                if (imageView.getImage() != null) {
                    anchorPane.getChildren().add(imageView);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error creating film card components", e);
            }
        }
        return anchorPane;
    }

    /**
     * Retrieves a list of unique film release years from a database using
     * `FilmService`.
     *
     * @returns a list of unique cinema years obtained from the films' release
     *          dates.
     *          <p>
     *          1/ The list contains unique `Integer` objects representing the
     *          cinema years. 2/ The list is generated by transforming the original
     *          list of films using a series of methods, specifically `map`,
     *          `distinct`, and `collect`. 3/ The transformation involves extracting
     *          the year of release from each film object using the `getReleaseYear`
     *          method.
     */
    private List<Integer> getCinemaYears() {
        final FilmService cinemaService = new FilmService();
        PageRequest pageRequest = new PageRequest(0, 10);
        final List<Film> cinemas = cinemaService.read(pageRequest).getContent();
        // Extraire les années de réalisation uniques des films
        return cinemas.stream().map(Film::getReleaseYear).distinct().collect(Collectors.toList());
    }

    /**
     * Sets the opacity of a panel to 0.5 and makes a pane visible, clears a list of
     * checkboxes, recieves unique cinema years from a database, creates a VBox for
     * each year, adds the VBox to an anchor pane, and makes the anchor pane
     * visible.
     *
     * @param event
     *              action event that triggered the filtration process.
     *              <p>
     *              - Event type: `ActionEvent` - Target: `Anchore_Pane_filtrage` (a
     *              pane in the scene) - Command: Unspecified (as the function does
     *              not use a specific command)
     *              </p>
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
     * Sets the opacity and visibility of an AnchorPane, and also makes a different
     * AnchorPane visible.
     *
     * @param event
     *              user interaction that triggered the execution of the
     *              `closercommets` method.
     *              <p>
     *              - Target: detalAnchorPane - Action: setOpacity() and
     *              setVisible()
     *              methods
     *              </p>
     */
    @FXML
    void closercommets(final ActionEvent event) {
        this.detalAnchorPane.setOpacity(1);
        this.AnchorComments.setVisible(false);
        this.detalAnchorPane.setVisible(true);
    }

    /**
     * Filters a list of cinemas based on user-selected years of release and
     * displays the filtered cinemas in a flow pane.
     *
     * @param event
     *              occurrence of an action event, such as clicking on the "Filtrer"
     *              button, that triggers the execution of the `filtrercinema()`
     *              method.
     *              <p>
     *              - `event` is an `ActionEvent`, indicating that the function was
     *              called as a result of user action. - The `event` object contains
     *              information about the source of the action, such as the button
     *              or
     *              link that was clicked.
     *              </p>
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
     * Retrieves the selected years from an `AnchorPane` widget, filters out
     * non-selected years using `filter`, maps the selected check boxes to their
     * corresponding integers using `map`, and collects the list of integers
     * representing the selected years.
     *
     * @returns a list of integer values representing the selected years.
     *          <p>
     *          The output is a list of integers representing the selected years
     *          from the check boxes in the AnchorPane.
     *          <p>
     *          Each integer in the list corresponds to an individual check box that
     *          was selected by the user.
     *          <p>
     *          The list contains only the unique years that were selected by the
     *          user, without duplicates or invalid input.
     */
    private List<Integer> getSelectedYears() {
        // Récupérer les années de réalisation sélectionnées dans l'AnchorPane de
        // filtrage
        return this.yearsCheckBoxes.stream().filter(CheckBox::isSelected)
                .map(checkBox -> Integer.parseInt(checkBox.getText())).collect(Collectors.toList());
    }

    /**
     * Loads a FXML file "/ui/series/SeriesClient.fxml" into a stage, replacing the
     * current scene.
     *
     * @param event
     *              event that triggered the execution of the
     *              `switchtoajouterCinema()` method.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user action.
     *              </p>
     */
    public void switchtoajouterCinema(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.product.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads and displays a FXML file using the `FXMLLoader` class, replacing the
     * current scene with the new one.
     *
     * @param event
     *              event that triggered the call to the `switchtevent()`
     *              method.
     *              <p>
     *              - Type: ActionEvent - indicates that the event was triggered by
     *              an
     *              action (e.g., button click) - Target: null - indicates that the
     *              event did not originate from a specific component or element -
     *              Code: 0 - no code is provided with this event
     *              </p>
     */
    public void switchtevent(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui//ui/AffichageEvenementClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.event_button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a FXML file using the `FXMLLoader` class, creates a new `AnchorPane`
     * root element, sets it as the scene of a stage, and displays the stage in a
     * window with a specified size.
     *
     * @param event
     *              ActionEvent that triggered the call to the `switchtcinemaaa()`
     *              method.
     *              <p>
     *              - `ActionEvent event`: Represents an action that occurred in the
     *              application, carrying information about the source of the action
     *              and the type of action performed.
     *              </p>
     */
    public void switchtcinemaaa(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.Cinema_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file, creates a Stage and sets the Scene for displaying a user
     * interface.
     *
     * @param event
     *              event that triggered the call to the `switchtoajouterproduct()`
     *              method.
     *              <p>
     *              - Type: ActionEvent - indicates that the event was triggered by
     *              an
     *              action (e.g., button click) - Target: null - indicates that the
     *              event did not originate from a specific component or element -
     *              Code: 0 - no code is provided with this event
     *              </p>
     */
    public void switchtoajouterproduct(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.product.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads and displays a FXML file using the `FXMLLoader` class, replacing the
     * current scene with the new one.
     *
     * @param event
     *              ActionEvent that triggered the call to the `switchtoSerie()`
     *              method.
     *              <p>
     *              - Type: ActionEvent, indicating that the event was triggered by
     *              a
     *              user action.
     *              </p>
     */
    public void switchtoSerie(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.SerieButton.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Allows users to add comments to a film by providing a text input, displaying
     * an alert if the comment is empty, and then creating a new FilmComment object
     * with the provided message, user ID, and film ID using the FilmCommentService.
     */
    @FXML
    void addCommentaire() {
        final String message = this.txtAreaComments.getText();
        if (message.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        } else {
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
     * @param event
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.filmId);
    }

    /**
     * Calculates a similarity score between a film and user preferences based on
     * genre preferences
     * and average rating
     * 
     * @param film            The film to score
     * @param userPreferences Map of genre preferences
     * @return A similarity score
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
     * Creates an HBox containing an ImageView and a VBox with text, image and card
     * container. It adds the HBox to a ScrollPaneComments.
     *
     * @param commentaire FilmComment object containing information about a comment
     * @returns a HBox container that displays an image and text related to a
     *          comment.
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
        } catch (Exception e) {
            LOGGER.warning("Failed to load profile image from URL: " + imageUrl);
        }

        // If loading from URL failed or image was null/empty, use default
        if (userImage == null) {
            try {
                userImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
            } catch (Exception e) {
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
     * Displays all comments associated with a specific film ID in a scroll pane.
     *
     * @param filmId identifier of the film to display all comments for.
     */
    @FXML
    public void displayAllComments(final Long filmId) {
        // Get comments from database
        final FilmCommentService cinemaCommentService = new FilmCommentService();
        PageRequest pageRequest = new PageRequest(0,10);
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

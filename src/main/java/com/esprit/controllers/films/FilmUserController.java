package com.esprit.controllers.films;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
public class FilmUserController extends Application {
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
     *            list of films that will be searched for matching titles within the
     *            provided `recherche` parameter.
     *            <p>
     *            - It is a list of `Film` objects - Each element in the list has a
     *            `nom` attribute that can contain the search query
     * @param recherche
     *            search query, which is used to filter the list of films in the
     *            function.
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
     *            list of films to create film cards for, which are then added as
     *            children of the `flowpaneFilm`.
     *            <p>
     *            - `Film` objects are contained in the list. - Each `Film` object
     *            has various attributes, such as title, director, year of release,
     *            etc.
     */
    private void createfilmCards(final List<Film> Films) {
        for (final Film film : Films) {
            final AnchorPane cardContainer = this.createFilmCard(film);
            this.flowpaneFilm.getChildren().add(cardContainer);
        }
    }

    /**
     * Sets up the user interface for a film application, including creating a flow
     * pane to display films and three combos to display top actors or directors.
     */
    @FXML
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize() {
        // Initialize basic UI components first
        setupBasicUI();

        // Defer loading recommendations until scene is ready
        Platform.runLater(this::setupRecommendations);
    }

    private void setupBasicUI() {
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
            final List<Film> filmList = new FilmService().sort(t1);
            for (final Film film : filmList) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        });
        final FilmService filmService1 = new FilmService();
        this.l1 = filmService1.read();
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
            FilmUserController.this.detalAnchorPane.setVisible(false);
            FilmUserController.this.anchorPaneFilm.setOpacity(1);
            FilmUserController.this.anchorPaneFilm.setDisable(false);
        });
        // Set the padding
        this.flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));
        final List<Film> filmList = new FilmService().read();
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
                    shareButton.setOnAction(e -> shareToSocial(((Film) node.getUserData())));
                    ((AnchorPane) node).getChildren().add(shareButton);

                    // Set up drag and drop
                    setupDragAndDrop((AnchorPane) node, (Film) node.getUserData());
                }
            });

            // Add recommendations section
            try {
                VBox recommendationsBox = new VBox(10);
                recommendationsBox.getChildren().add(new Label("Recommended for You"));

                Long userId = getCurrentUserId();
                if (userId > 0) {
                    getRecommendations(userId).forEach(film -> {
                        recommendationsBox.getChildren().add(createFilmCard(film));
                    });
                    flowpaneFilm.getChildren().add(recommendationsBox);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load recommendations", e);
            }
        }
    }

    /**
     * Filters a `Flowpane` of `AnchorPane` elements based on the text content of a
     * `.nomFilm` label, making the visible or invisible elements dependent on the
     * keyword search result.
     *
     * @param keyword
     *            search term used to filter the film cards, and its value
     *            determines whether or not a card is visible and managed.
     */
    private void filterByName(final String keyword) {
        for (final Node node : this.flowpaneFilm.getChildren()) {
            final AnchorPane filmCard = (AnchorPane) node;
            final Label nomFilm = (Label) filmCard.lookup(".nomFilm"); // Supposons que le nom du film soit représenté
                                                                       // par une
            // classe CSS ".nomFilm"
            if (null != nomFilm) {
                final boolean isVisible = nomFilm.getText().toLowerCase().contains(keyword); // Vérifie si le nom du
                                                                                             // film
                // contient le mot-clé de
                // recherche
                filmCard.setVisible(isVisible); // Définit la visibilité de la carte en fonction du résultat du filtrage
                filmCard.setManaged(isVisible); // Définit la gestion de la carte en fonction du résultat du filtrage
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
     *            film object that will be displayed in the anchor pane, and is used
     *            to retrieve the film's information such as title, image, rating,
     *            and trailer link.
     *            <p>
     *            - `id`: a unique identifier for the film - `nom`: the film's title
     *            - `description`: a brief description of the film - `duree`: the
     *            film's duration - `annderalisation`: the film's release date -
     *            `categories`: an array of categories the film belongs to -
     *            `actors`: an array of actors appearing in the film.
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
            // Remove caching logic and load image directly
            String imagePath = film.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
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
            imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
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
                FilmUserController.this.switchtopayment(nomFilm.getText());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        final Hyperlink hyperlink = new Hyperlink("Details");
        hyperlink.setLayoutX(89);
        hyperlink.setLayoutY(251);
        hyperlink.setOnAction(event -> {
            FilmUserController.this.detalAnchorPane.setVisible(true);
            FilmUserController.this.anchorPaneFilm.setOpacity(0.26);
            FilmUserController.this.anchorPaneFilm.setDisable(true);
            final Film film1 = new Film(film);
            FilmUserController.this.filmId = film.getId();
            FilmUserController.this.filmNomDetail.setText(film1.getName());
            FilmUserController.this.descriptionDETAILfilm.setText(
                    film1.getDescription() + "\nTime:" + film1.getDuration() + "\nYear:" + film1.getReleaseYear()
                            + "\nCategories: " + new FilmCategoryService().getCategoryNames(film1.getId())
                            + "\nActors: " + new ActorFilmService().getActorsNames(film1.getId()));
            FilmUserController.this.imagefilmDetail.setImage(new Image(film1.getImage()));
            final double rate1 = new FilmRatingService().getAvergeRating(film1.getId());
            FilmUserController.this.labelavregeRate.setText(rate1 + "/5");
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
            FilmUserController.this.qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            FilmUserController.this.qrcode.setOnMouseClicked(e -> {
                final HostServices hostServices = FilmUserController.this.getHostServices();
                // Open the URL in the default system browser
                hostServices.showDocument(url);
            }); // HBox qrCodeImgModel = (HBox) ((Node)
            // event.getSource()).getScene().lookup("#qrCodeImgModel");
            FilmUserController.this.qrcode.setVisible(true);
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
                FilmUserController.this.labelavregeRate.setText(rate2 + "/5");
                ratefilm.setText(rate2 + "/5");
                FilmUserController.this.topthreeVbox.getChildren().clear();
                for (int i = 0; 3 > i; i++) {
                    FilmUserController.this.topthreeVbox.getChildren().add(FilmUserController.this.createtopthree(i));
                }
            });
            FilmUserController.this.trailer_Button.setOnAction(trailerEvent -> {
                FilmUserController.this.anchorPane_Trailer.getChildren().forEach(node -> {
                    node.setDisable(false);
                });
                final WebView webView = new WebView();
                FilmUserController.LOGGER.info(film1.getName());
                Platform.runLater(() -> {
                    webView.getEngine().load(new FilmService().getTrailerFilm(film1.getName()));
                });
                FilmUserController.LOGGER.info("film passed");
                FilmUserController.this.anchorPane_Trailer.setVisible(true);
                FilmUserController.this.anchorPane_Trailer.getChildren().add(webView);
                FilmUserController.this.anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if (KeyCode.ESCAPE == keyEvent.getCode()) {
                        FilmUserController.this.anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(true);
                        });
                        FilmUserController.this.anchorPane_Trailer.setVisible(false);
                    }
                });
            });
            FilmUserController.this.detalAnchorPane.getChildren().add(rateFilm);
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
                if (!actor.getImage().isEmpty()) {
                    imageView.setImage(new Image(actor.getImage()));
                }
            } catch (final Exception e) {
                FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
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
                    // Try to load from resources first
                    Image image = null;
                    try {
                        image = new Image(getClass().getResourceAsStream(imagePath));
                    } catch (Exception e) {
                        // If resource loading fails, try as direct path
                        try {
                            image = new Image("file:" + imagePath);
                        } catch (Exception e2) {
                            LOGGER.warning("Failed to load image from both resource and file: " + imagePath);
                        }
                    }

                    if (image != null) {
                        imageView.setImage(image);
                        imageView.setFitWidth(180); // Adjusted size for top 3 section
                        imageView.setFitHeight(260); // Maintain movie poster ratio
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setLayoutX(21);
                        imageView.setLayoutY(21);
                        imageView.getStyleClass().addAll("bg-white");
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Error loading image for film: " + film.getId().intValue());
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
        final List<Film> cinemas = cinemaService.read();
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
     *            action event that triggered the filtration process.
     *            <p>
     *            - Event type: `ActionEvent` - Target: `Anchore_Pane_filtrage` (a
     *            pane in the scene) - Command: Unspecified (as the function does
     *            not use a specific command)
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
     *            user interaction that triggered the execution of the
     *            `closercommets` method.
     *            <p>
     *            - Target: detalAnchorPane - Action: setOpacity() and setVisible()
     *            methods
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
     *            occurrence of an action event, such as clicking on the "Filtrer"
     *            button, that triggers the execution of the `filtrercinema()`
     *            method.
     *            <p>
     *            - `event` is an `ActionEvent`, indicating that the function was
     *            called as a result of user action. - The `event` object contains
     *            information about the source of the action, such as the button or
     *            link that was clicked.
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
     *            event that triggered the execution of the
     *            `switchtoajouterCinema()` method.
     *            <p>
     *            - `event`: An `ActionEvent` object representing a user action.
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
     *            ActionEvent that triggered the call to the `switchtevent()`
     *            method.
     *            <p>
     *            - Type: ActionEvent - indicates that the event was triggered by an
     *            action (e.g., button click) - Target: null - indicates that the
     *            event did not originate from a specific component or element -
     *            Code: 0 - no code is provided with this event
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
     *            ActionEvent that triggered the call to the `switchtcinemaaa()`
     *            method.
     *            <p>
     *            - `ActionEvent event`: Represents an action that occurred in the
     *            application, carrying information about the source of the action
     *            and the type of action performed.
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
     *            event that triggered the call to the `switchtoajouterproduct()`
     *            method.
     *            <p>
     *            - Type: ActionEvent - indicates that the event was triggered by an
     *            action (e.g., button click) - Target: null - indicates that the
     *            event did not originate from a specific component or element -
     *            Code: 0 - no code is provided with this event
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
     *            ActionEvent that triggered the call to the `switchtoSerie()`
     *            method.
     *            <p>
     *            - Type: ActionEvent, indicating that the event was triggered by a
     *            user action.
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

    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.filmId);
    }

    private List<FilmComment> getAllComment(final Long filmId) { // Changed parameter type
        final FilmCommentService cinemaCommentService = new FilmCommentService();
        final List<FilmComment> allComments = cinemaCommentService.read(); // Récupérer tous les commentaires
        final List<FilmComment> cinemaComments = new ArrayList<>();
        // Filtrer les commentaires pour ne conserver que ceux du cinéma correspondant
        for (final FilmComment comment : allComments) {
            if (comment.getFilm().getId().equals(filmId)) { // Fixed method call and comparison
                cinemaComments.add(comment);
            }
        }
        return cinemaComments;
    }

    private long getCurrentUserId() { // Changed return type
        if (filmScrollPane == null || filmScrollPane.getScene() == null
                || filmScrollPane.getScene().getWindow() == null) {
            return 2L; // Default user ID if scene is not ready
        }

        Stage stage = (Stage) filmScrollPane.getScene().getWindow();
        Client client = (Client) stage.getUserData();
        return client != null ? client.getId() : 2L; // Fixed return type
    }

    private List<Film> getRecommendations(long userId) { // Changed parameter type
        // Get user's ratings
        FilmRatingService ratingService = new FilmRatingService();
        List<FilmRating> userRatings = ratingService.getUserRatings(userId);

        // Calculate genre preferences
        Map<String, Double> genreScores = new HashMap<>();
        for (FilmRating rating : userRatings) {
            Film film = rating.getFilm(); // Fixed method name
            List<String> genres = Arrays.asList(new FilmCategoryService().getCategoryNames(film.getId()).split(",")); // Fixed
                                                                                                                      // parameter
                                                                                                                      // type
            for (String genre : genres) {
                genreScores.merge(genre.trim(), rating.getRating() * 0.2, Double::sum); // Fixed method name
            }
        }

        // Find similar films
        FilmService filmService = new FilmService();
        List<Film> allFilms = filmService.read();
        PriorityQueue<Film> recommendations = new PriorityQueue<>(
                Comparator.comparingDouble(f -> calculateSimilarityScore(f, genreScores)));

        allFilms.forEach(recommendations::offer);

        return recommendations.stream().limit(10).collect(Collectors.toList());
    }

    @FXML
    void afficherAnchorComment(final MouseEvent event) {
        this.AnchorComments.setVisible(true);
        this.displayAllComments(this.filmId);
    }

    /**
     * Creates an HBox containing an ImageView and a VBox with text, image and card
     * container. It adds the HBox to a ScrollPaneComments.
     *
     * @param commentaire
     *            FilmComment object containing information about a comment made by
     *            a user on a film, which is used to display the commenter's name
     *            and comment text in the function's output.
     *            <p>
     *            - `commentaire`: an object of class `FilmComment`, which contains
     *            information about a user's comment on a film. - `User_id`: a field
     *            in the `FilmComment` class that represents the user who made the
     *            comment. - `Photo_de_profil`: a field in the `FilmComment` class
     *            that represents the user's profile picture URL.
     * @returns a HBox container that displays an image and text related to a
     *          comment.
     *          <p>
     *          - `HBox contentContainer`: This is the primary container for the
     *          image and comment card. It has a pref height of 50 pixels and a
     *          style of `-fx-background-color: transparent; -fx-padding: 10px`. -
     *          `imageBox` and `cardContainer`: These are sub-containers within the
     *          `contentContainer`. The `imageBox` contains the image of the user,
     *          while the `cardContainer` contains the text box with the user's name
     *          and comment.
     */
    private HBox addCommentToView(final FilmComment commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        final String imageUrl = commentaire.getClient().getPhotoDeProfil();
        final Image userImage;
        if (null != imageUrl && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
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
        // Ajouter le conteneur principal au ScrollPane
        this.ScrollPaneComments.setContent(contentContainer);
        return contentContainer;
    }

    /**
     * Retrieves all comments for a given film ID and filters them to only include
     * those that belong to the corresponding cinema.
     *
     * @param filmId
     *            id of the film for which the comments are to be retrieved.
     * @returns a list of commentaries for a specific cinema, filtered from all
     *          comments based on their film ID.
     *          <p>
     *          - `List<FilmComment>` is the type of the returned value, indicating
     *          that it is a list of `FilmComment` objects. - The variable
     *          `cinemaComments` is initialized as an empty list, indicating that no
     *          comments have been filtered yet. - The function uses a loop to
     *          iterate over all the comments in the `allComments` list and checks
     *          if the `film_id` of each comment matches the `filmId` parameter
     *          passed to the function. If it does, the comment is added to the
     *          `cinemaComments` list.
     */
    private List<FilmComment> getAllComment(final int filmId) {
        final FilmCommentService cinemaCommentService = new FilmCommentService();
        final List<FilmComment> allComments = cinemaCommentService.read(); // Récupérer tous les commentaires
        final List<FilmComment> cinemaComments = new ArrayList<>();
        // Filtrer les commentaires pour ne conserver que ceux du cinéma correspondant
        for (final FilmComment comment : allComments) {
            if (comment.getFilm().getId() == filmId) {
                cinemaComments.add(comment);
            }
        }
        return cinemaComments;
    }

    /**
     * Displays all comments associated with a specific film ID in a scroll pane.
     *
     * @param filmId
     *            identifier of the film to display all comments for.
     */
    private void displayAllComments(final Long filmId) {
        final List<FilmComment> comments = this.getAllComment(filmId);
        final VBox allCommentsContainer = new VBox();
        for (final FilmComment comment : comments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }
        this.ScrollPaneComments.setContent(allCommentsContainer);
    }

    /**
     * Is called when the Java application begins and sets up the Stage for further
     * interaction.
     *
     * @param stage
     *            Stage object that serves as the root of the JavaFX application's
     *            event handling and visual representation, and it is used to
     *            initialize the application's UI components and layout when the
     *            `start()` method is called.
     */
    @Override
    /**
     * Performs start operation.
     *
     * @return the result of the operation
     */
    public void start(final Stage stage) throws Exception {
    }

    private void setupDragAndDrop(AnchorPane filmCard, Film film) {
        filmCard.setOnDragDetected(event -> {
            Dragboard db = filmCard.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(film.getId().intValue()));
            db.setContent(content);
            event.consume();
        });

        filmCard.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                // Handle successful drag
                double x = event.getSceneX();
                double y = event.getSceneY();
                filmCard.setLayoutX(x);
                filmCard.setLayoutY(y);
            }
            event.consume();
        });
    }

    private List<Film> getRecommendations(Long userId) {
        // Get user's ratings
        FilmRatingService ratingService = new FilmRatingService();
        List<FilmRating> userRatings = ratingService.getUserRatings(userId);

        // Calculate genre preferences
        Map<String, Double> genreScores = new HashMap<>();
        for (FilmRating rating : userRatings) {
            Film film = rating.getFilm();
            List<String> genres = Arrays.asList(new FilmCategoryService().getCategoryNames(film.getId()).split(","));
            for (String genre : genres) {
                genreScores.merge(genre.trim(), rating.getRating() * 0.2, Double::sum);
            }
        }

        // Find similar films
        FilmService filmService = new FilmService();
        List<Film> allFilms = filmService.read();
        PriorityQueue<Film> recommendations = new PriorityQueue<>(
                Comparator.comparingDouble(f -> calculateSimilarityScore(f, genreScores)));

        allFilms.forEach(recommendations::offer);

        return recommendations.stream().limit(10).collect(Collectors.toList());
    }

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

    private void shareToSocial(Film film) {
        String shareText = String.format("Check out %s! Released in %d. %s", film.getName(), film.getReleaseYear(),
                FilmService.getIMDBUrlbyNom(film.getName()));

        // Create sharing dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Share Film");
        dialog.setHeaderText("Share this film on social media");

        ButtonType twitterType = new ButtonType("Twitter");
        ButtonType facebookType = new ButtonType("Facebook");
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(twitterType, facebookType, cancelType);

        dialog.showAndWait().ifPresent(response -> {
            if (response == twitterType) {
                openTwitterShare(shareText);
            } else if (response == facebookType) {
                openFacebookShare(shareText);
            }
        });
    }

    private void openTwitterShare(String text) {
        String twitterUrl = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        getHostServices().showDocument(twitterUrl);
    }

    private void openFacebookShare(String text) {
        String fbUrl = "https://www.facebook.com/sharer/sharer.php?u="
                + URLEncoder.encode(text, StandardCharsets.UTF_8);
        getHostServices().showDocument(fbUrl);
    }

    /**
     * Performs close operation.
     *
     * @return the result of the operation
     */
    public void close() {
    }
}

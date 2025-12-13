package com.esprit.controllers.films;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.common.Review;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.common.ReviewService;
import com.esprit.services.films.ActorService;
import com.esprit.services.films.FilmCategoryService;
import com.esprit.services.films.FilmService;
import com.esprit.services.search.SearchService;
import com.esprit.services.search.SearchService.EntityType;
import com.esprit.services.search.SearchService.SearchResult;
import com.esprit.services.search.SearchService.UserRole;
import com.esprit.services.users.UserService;
import com.esprit.utils.PageRequest;
import com.esprit.utils.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Rating;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private static final long SEARCH_DEBOUNCE_MS = 150;
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
    private VBox AnchorComments;
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
    private VBox Anchore_Pane_filtrage;
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
    private HBox topthreeVbox;
    @FXML
    private ScrollPane filmScrollPane;
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
    // New immersive UI elements
    @FXML
    private VBox inlineDetailPanel;
    @FXML
    private VBox filmGridContainer;
    @FXML
    private Label heroTitle;
    @FXML
    private Label heroRating;
    @FXML
    private Label heroYear;
    @FXML
    private Label heroDuration;
    @FXML
    private Label heroGenre;
    @FXML
    private Label heroDescription;
    @FXML
    private ImageView heroPoster;
    @FXML
    private ImageView heroBackdrop;
    @FXML
    private Button heroPlayBtn;
    @FXML
    private Button heroTrailerBtn;
    @FXML
    private StackPane trailerOverlay;
    @FXML
    private Button closeTrailerBtn;
    @FXML
    private HBox filterPanelWrapper;
    @FXML
    private Label movieCountLabel;
    @FXML
    private HBox genreTags;
    @FXML
    private Button shareDetailBtn;
    // Search components
    private SearchService searchEngine;
    private javafx.stage.Popup searchPopup;
    private VBox searchResultsContainer;
    private Timer searchDebounceTimer;
    // Remove unused field
    // private final HashMap<Integer, Double> userPreferences = new HashMap<>();
    // Add moviesession field
    private MovieSession moviesession;

    // Featured film for hero section
    private Film featuredFilm;

    /**
     * Queries a list of films for any that contain a specified search term in their
     * name, and returns a list of matches.
     *
     * @param liste     list of films that will be searched for matching titles
     *                  within the
     *                  provided `recherche` parameter.
     *                  <p>
     *                  - It is a list of `Film` objects - Each element in the list
     *                  has a
     *                  `nom` attribute that can contain the search query
     * @param recherche search query, which is used to filter the list of films in
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
            if (null != element.getTitle() && element.getTitle().contains(recherche)) {
                resultats.add(element);
            }

        }

        return resultats;
    }

    /**
     * Open the seat selection UI for the given film and display it in the current
     * stage.
     * <p>
     * If a MovieSession exists for the film, initializes the
     * SeatSelectionController with
     * that session and the current client, then replaces the current scene with the
     * loaded UI.
     * If no MovieSession is available, shows an error alert and leaves the current
     * scene unchanged.
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
     * @param Films list of films whose cards will be created and appended to
     *              {@code flowpaneFilm}
     */
    private void createfilmCards(final List<Film> Films) {
        for (final Film film : Films) {
            final AnchorPane cardContainer = this.createFilmCard(film);
            this.flowpaneFilm.getChildren().add(cardContainer);
        }

    }

    /**
     * Initialize the controller and configure UI components after the FXML is
     * loaded.
     *
     * <p>
     * Configures the top-3 films/actors combobox, film sorting options, search
     * behavior,
     * film card display area, and related event handlers. Recommendation loading is
     * deferred
     * to run once the scene is ready.
     * </p>
     */
    @FXML
    public void initialize() {
        // Initialize basic UI components first
        setupBasicUI();

        // Defer loading recommendations until scene is ready
        Platform.runLater(this::setupRecommendations);
    }

    /**
     * Initialize and configure primary UI controls, listeners, and populate initial
     * film and top-three views.
     *
     * <p>
     * Configures the Top‑3 selector, sorting selector, search field listener, film
     * FlowPane layout and padding,
     * the detail-close action, and loads the initial film cards and top‑three
     * displays from services.
     */
    private void setupBasicUI() {
        PageRequest pageRequest = PageRequest.defaultPage();
        this.top3combobox.getItems().addAll("Top 3 Films", "Top 3 Actors");
        this.top3combobox.setValue("Top 3 Films");
        this.top3combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Top 3 Films".equals(newValue)) {
                this.topthreeVbox1.setVisible(false);
                this.topthreeVbox1.setManaged(false);
                this.topthreeVbox.setVisible(true);
                this.topthreeVbox.setManaged(true);
            } else if ("Top 3 Actors".equals(newValue)) {
                final ObservableList<Node> topthreevboxactorsChildren = this.topthreeVbox1.getChildren();
                topthreevboxactorsChildren.clear();
                this.topthreeVbox.setVisible(false);
                this.topthreeVbox.setManaged(false);
                this.topthreeVbox1.setVisible(true);
                this.topthreeVbox1.setManaged(true);
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
            updateMovieCount();
        });
        final FilmService filmService1 = new FilmService();
        PageRequest filmPageRequest = PageRequest.defaultPage();
        this.l1 = filmService1.read(filmPageRequest).getContent();

        // Initialize universal search engine
        this.searchEngine = SearchService.getInstance();
        setupEnhancedSearch();

        // Create FlowPane and add to grid container
        this.flowpaneFilm = new FlowPane();
        this.flowpaneFilm.setHgap(20);
        this.flowpaneFilm.setVgap(25);
        this.flowpaneFilm.setStyle("-fx-background-color: transparent;");
        this.flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));

        // Add FlowPane to filmGridContainer (new immersive layout)
        if (this.filmGridContainer != null) {
            this.filmGridContainer.getChildren().add(this.flowpaneFilm);
        }

        // Also set filmScrollPane content for compatibility
        if (this.filmScrollPane != null) {
            this.filmScrollPane.setFitToWidth(true);
            this.filmScrollPane.setFitToHeight(true);
            this.filmScrollPane.setStyle("-fx-background-color: transparent;");
        }

        // Setup close button for inline detail panel
        if (this.closeDetailFilm != null) {
            this.closeDetailFilm.setOnAction(event -> {
                hideInlineDetailPanel();
            });
        }

        // Setup share button for detail panel
        if (this.shareDetailBtn != null) {
            this.shareDetailBtn.setOnAction(event -> {
                String filmTitle = this.filmNomDetail.getText();
                if (filmTitle != null && !filmTitle.isEmpty()) {
                    String shareText = "Check out \"" + filmTitle + "\" on RAKCHA!";
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new java.awt.datatransfer.StringSelection(shareText), null);
                    showShareNotification(filmTitle);
                }
            });
        }

        // Setup trailer overlay - close when clicking on background
        if (this.trailerOverlay != null) {
            this.trailerOverlay.setOnMouseClicked(event -> {
                // Only close if clicking on the overlay background, not on the trailer
                // container
                if (event.getTarget() == trailerOverlay) {
                    closeTrailerOverlay();
                }
            });

            // Also allow pressing Escape to close the trailer
            this.trailerOverlay.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    closeTrailerOverlay();
                }
            });
        }

        // Load films
        final List<Film> filmList = new FilmService().read(filmPageRequest).getContent();
        System.out.println(filmList);
        for (final Film film : filmList) {
            this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
        }

        // Setup hero section with featured film
        setupHeroSection(filmList);

        // Setup top 3 picks (horizontal layout)
        final ObservableList<Node> topthreevboxChildren = this.topthreeVbox.getChildren();
        for (int i = 0; i < this.flowpaneFilm.getChildren().size() && 3 > i; i++) {
            topthreevboxChildren.add(this.createtopthree(i));
        }

        // Update movie count
        updateMovieCount();
    }

    /**
     * Collects distinct release years from films retrieved from FilmService.
     * <p>
     * Only films from the first page (10 items) are considered.
     *
     * @return a list of unique release years in the order they were encountered
     */
    private List<Integer> getCinemaYears() {
        final FilmService cinemaService = new FilmService();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<Film> cinemas = cinemaService.read(pageRequest).getContent();
        // Extraire les années de réalisation uniques des films
        return cinemas.stream().map(Film::getReleaseYear).distinct().collect(Collectors.toList());
    }

    /**
     * Displays the year filter pane, dims the main film view, and populates the
     * pane with
     * checkboxes for each available film release year.
     * <p>
     * The created checkboxes are stored in the controller's `yearsCheckBoxes` list
     * for later use.
     *
     * @param event the ActionEvent that triggered showing the filter pane
     */
    @FXML
    void filtrer(final ActionEvent event) {
        // Show filter panel with animation
        if (filterPanelWrapper != null) {
            filterPanelWrapper.setVisible(true);
            filterPanelWrapper.setManaged(true);
            filterPanelWrapper.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), filterPanelWrapper);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }

        this.flowpaneFilm.setOpacity(0.5);
        this.Anchore_Pane_filtrage.setVisible(true);
        // Nettoyer la liste des cases à cocher
        this.yearsCheckBoxes.clear();
        // Récupérer les années de réalisation uniques depuis la base de données
        final List<Integer> years = this.getCinemaYears();
        // Créer des VBox pour les années de réalisation
        final VBox yearsCheckBoxesVBox = new VBox();
        yearsCheckBoxesVBox.setSpacing(8);
        final Label yearLabel = new Label("Release Year");
        yearLabel.getStyleClass().add("filter-section-label");
        yearLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px; -fx-text-fill: white;");
        yearsCheckBoxesVBox.getChildren().add(yearLabel);
        for (final Integer year : years) {
            final CheckBox checkBox = new CheckBox(String.valueOf(year));
            checkBox.setStyle("-fx-text-fill: white;");
            yearsCheckBoxesVBox.getChildren().add(checkBox);
            this.yearsCheckBoxes.add(checkBox);
        }
        // Add to the filter panel's children
        if (!this.Anchore_Pane_filtrage.getChildren().contains(yearsCheckBoxesVBox)) {
            this.Anchore_Pane_filtrage.getChildren().add(yearsCheckBoxesVBox);
        }
    }

    /**
     * Closes the comments section within the inline detail panel
     *
     * @param event user interaction that triggered the execution of the
     *              `closercommets` method.
     */
    @FXML
    void closercommets(final ActionEvent event) {
        if (AnchorComments != null) {
            AnchorComments.setVisible(false);
            AnchorComments.setManaged(false);
        }
    }

    /**
     * Filters a list of cinemas based on user-selected years of release and
     * displays the filtered cinemas in a flow pane.
     *
     * @param event occurrence of an action event, such as clicking on the "Filtrer"
     *              button, that triggers the execution of the `filtrercinema()`
     *              method.
     */
    @FXML
    void filtrercinema(final ActionEvent event) {
        // Hide filter panel with animation
        if (filterPanelWrapper != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), filterPanelWrapper);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                filterPanelWrapper.setVisible(false);
                filterPanelWrapper.setManaged(false);
            });
            fadeOut.play();
        }

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
        updateMovieCount();
    }

    /**
     * Collects the years selected by the user from the filter checkboxes.
     * <p>
     * Parses the text of each selected checkbox as an integer and returns them.
     *
     * @return a list of integers corresponding to the selected year checkboxes;
     *         empty if none are selected
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
        } catch (final Exception e) {
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
                    this.getClass().getResource("/ui/AffichageEvenementClient.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.event_button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Navigate to the cinema dashboard view and replace the current stage scene.
     * <p>
     * Loads the FXML resource "/ui/cinemas/DashboardClientCinema.fxml" and sets it
     * as the active scene sized to 1280x700.
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
     * Loads the product listing UI (AfficherProductClient.fxml) and replaces the
     * current window's scene with it.
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
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Replace the current window's scene with the Series client UI loaded from
     * FXML.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    public void switchtoSerie(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/series/SeriesClient.fxml"));
            final VBox root = fxmlLoader.load();
            final Stage stage = (Stage) this.SerieButton.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Persist the text from the comment input as a new comment for the currently
     * selected film.
     *
     * <p>
     * If the input is empty a warning alert is shown and no comment is created.
     * Otherwise a
     * FilmComment is created for the film identified by this controller's current
     * filmId, persisted,
     * and the comment input field is cleared.
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
            final Review commentaire = new Review(message, (Client) SessionManager.getCurrentUser(),
                    new FilmService().getFilm(this.filmId));
            FilmUserController.LOGGER.info(commentaire + " " + SessionManager.getCurrentUser());
            final ReviewService cinemaCommentService = new ReviewService();
            cinemaCommentService.create(commentaire);
            this.txtAreaComments.clear();
        }

    }

    /**
     * Adds the current comment for the selected film and refreshes the displayed
     * comments.
     */
    @FXML
    void AddComment(final MouseEvent event) {
        this.addCommentaire();
        this.displayAllComments(this.filmId);
    }

    /**
     * Builds a horizontal comment card containing the commenter's profile image and
     * the comment text.
     *
     * @param commentaire the FilmComment whose author, profile image, and text will
     *                    be displayed
     * @return an HBox containing the profile image and a VBox with the commenter
     *         name and comment text
     */
    private HBox addCommentToView(final Review commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        final Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);

        // Image de l'utilisateur
        final String imageUrl = commentaire.getUser().getProfilePictureUrl();
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
                commentaire.getUser().getFirstName() + " " + commentaire.getUser().getLastName());
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
     * Populate the comments scroll pane with UI nodes for all comments belonging to
     * the specified film.
     *
     * <p>
     * This loads the first page of comments (10 items) from FilmCommentService,
     * filters them by the
     * given film id, creates view nodes for each comment, and replaces the
     * ScrollPaneComments content
     * with the assembled list.
     * </p>
     *
     * @param filmId the id of the film whose comments should be displayed
     */
    @FXML
    public void displayAllComments(final Long filmId) {
        // Get comments from database
        final ReviewService cinemaCommentService = new ReviewService();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<Review> allComments = cinemaCommentService.read(pageRequest).getContent();
        final List<Review> filmComments = new ArrayList<>();

        // Filter comments for this film
        for (final Review comment : allComments) {
            if (comment.getFilm().getId().equals(filmId)) {
                filmComments.add(comment);
            }

        }

        // Display comments with improved styling
        final VBox allCommentsContainer = new VBox();
        allCommentsContainer.setSpacing(10);
        allCommentsContainer.setStyle("-fx-background-color: transparent;");

        for (final Review comment : filmComments) {
            final HBox commentView = this.addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        if (this.ScrollPaneComments != null) {
            this.ScrollPaneComments.setContent(allCommentsContainer);
        }
    }

    /**
     * Shows film comments in a panel with smooth animation.
     *
     * @param event The mouse event that triggered this action
     */
    @FXML
    void afficherAnchorComment(final MouseEvent event) {
        if (AnchorComments != null) {
            AnchorComments.setVisible(true);
            AnchorComments.setManaged(true);
            AnchorComments.setOpacity(0);

            // Smooth fade-in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), AnchorComments);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
        this.displayAllComments(this.filmId);
    }

    /**
     * Shows a notification that the film share text has been copied to clipboard.
     *
     * @param filmTitle The title of the film being shared
     */
    private void showShareNotification(final String filmTitle) {
        // Show console message confirming copy
        System.out.println("Copied to clipboard: Check out \"" + filmTitle + "\" on RAKCHA!");
    }

    /**
     * Filters films by selected category
     */
    @FXML
    void filterByCategory(final ActionEvent event) {
        Button source = (Button) event.getSource();
        String category = source.getText();
        FilmUserController.LOGGER.info("Filter by category: " + category);

        // Update pill button styles - remove active from all, add to selected
        if (source.getParent() instanceof HBox categoryPillsBox) {
            for (Node node : categoryPillsBox.getChildren()) {
                if (node instanceof Button btn) {
                    btn.getStyleClass().remove("active");
                }
            }
            source.getStyleClass().add("active");
        }

        // Filter films based on category
        this.flowpaneFilm.getChildren().clear();

        if ("All".equalsIgnoreCase(category)) {
            // Show all films
            for (final Film film : this.l1) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        } else if ("Now Showing".equalsIgnoreCase(category)) {
            // Show current year films
            int currentYear = java.time.Year.now().getValue();
            List<Film> filtered = this.l1.stream()
                    .filter(f -> f.getReleaseYear() == currentYear || f.getReleaseYear() == currentYear - 1)
                    .collect(Collectors.toList());
            for (final Film film : filtered) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        } else if ("Coming Soon".equalsIgnoreCase(category)) {
            // Show future films
            int currentYear = java.time.Year.now().getValue();
            List<Film> filtered = this.l1.stream()
                    .filter(f -> f.getReleaseYear() > currentYear)
                    .collect(Collectors.toList());
            for (final Film film : filtered) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        } else if ("Top Rated".equalsIgnoreCase(category)) {
            // Show top rated films
            ReviewService ratingService = new ReviewService();
            List<Film> sorted = this.l1.stream()
                    .sorted((f1, f2) -> Double.compare(
                            ratingService.getAverageRating(f2.getId()),
                            ratingService.getAverageRating(f1.getId())))
                    .limit(10)
                    .collect(Collectors.toList());
            for (final Film film : sorted) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        } else {
            // Filter by genre/category name
            FilmCategoryService filmCategoryService = new FilmCategoryService();
            List<Film> filtered = this.l1.stream()
                    .filter(f -> {
                        String categories = filmCategoryService.getCategoryNames(f.getId());
                        return categories != null && categories.toLowerCase().contains(category.toLowerCase());
                    })
                    .collect(Collectors.toList());
            for (final Film film : filtered) {
                this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
            }
        }

        updateMovieCount();
    }

    /**
     * Sorts films by selected option in view combo
     */
    @FXML
    void sortBySelection(final ActionEvent event) {
        FilmUserController.LOGGER.info("Sort by selection");
        // This is handled by the combo box listener in setupBasicUI
    }

    /**
     * Sorts films by header sort combo box
     */
    @FXML
    void sortFilms(final ActionEvent event) {
        String sortOption = this.tricomboBox.getValue();
        FilmUserController.LOGGER.info("Sort films by: " + sortOption);

        if (sortOption == null || sortOption.isEmpty())
            return;

        this.flowpaneFilm.getChildren().clear();
        PageRequest pageRequest = PageRequest.defaultPage();
        final List<Film> filmList = new FilmService().sort(pageRequest, sortOption).getContent();
        for (final Film film : filmList) {
            this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
        }
        updateMovieCount();
    }

    /**
     * Adds currently featured film to user's watchlist
     */
    @FXML
    void addToWatchlist(final ActionEvent event) {
        FilmUserController.LOGGER.info("Add to watchlist");

        if (this.featuredFilm != null) {
            // Show visual feedback
            Button btn = (Button) event.getSource();
            FontIcon icon = (FontIcon) btn.getGraphic();

            // Toggle icon between plus and check
            if (icon.getIconLiteral().contains("plus")) {
                icon.setIconLiteral("mdi2c-check:22");
                showToast("Added \"" + this.featuredFilm.getTitle() + "\" to watchlist!");
            } else {
                icon.setIconLiteral("mdi2p-plus:22");
                showToast("Removed \"" + this.featuredFilm.getTitle() + "\" from watchlist");
            }
        }
    }

    /**
     * Shares hero section featured film to clipboard
     */
    @FXML
    void shareHeroFilm(final ActionEvent event) {
        FilmUserController.LOGGER.info("Share hero film");

        if (this.featuredFilm != null) {
            String shareText = "🎬 Check out \"" + this.featuredFilm.getTitle() + "\" on RAKCHA Cinema!\n"
                    + "Year: " + this.featuredFilm.getReleaseYear() + "\n"
                    + "Duration: " + this.featuredFilm.getDurationMin();

            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(shareText), null);

            showShareNotification(this.featuredFilm.getTitle());
        }
    }

    /**
     * Shares detail panel film to clipboard
     */
    @FXML
    void shareDetailFilm(final ActionEvent event) {
        FilmUserController.LOGGER.info("Share detail film");

        String filmTitle = this.filmNomDetail.getText();
        if (filmTitle != null && !filmTitle.isEmpty()) {
            String description = this.descriptionDETAILfilm.getText();
            String shareText = "🎬 Check out \"" + filmTitle + "\" on RAKCHA Cinema!\n"
                    + (description != null ? description.substring(0, Math.min(100, description.length())) + "..."
                            : "");

            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(shareText), null);

            showShareNotification(filmTitle);
        }
    }

    /**
     * Toggles favorite status for current detail film
     */
    @FXML
    void toggleFavorite(final ActionEvent event) {
        FilmUserController.LOGGER.info("Toggle favorite");

        Button btn = (Button) event.getSource();
        FontIcon icon = (FontIcon) btn.getGraphic();

        // Toggle heart icon between outline and filled
        if (icon.getIconLiteral().contains("outline")) {
            icon.setIconLiteral("mdi2h-heart:20");
            icon.setStyle("-fx-fill: #ff4444;");
            showToast("Added to favorites!");
        } else {
            icon.setIconLiteral("mdi2h-heart-outline:20");
            icon.setStyle("-fx-fill: inherit;");
            showToast("Removed from favorites");
        }
    }

    /**
     * Resets all active filters and shows all films
     */
    @FXML
    void resetFilters(final ActionEvent event) {
        FilmUserController.LOGGER.info("Reset filters");

        // Clear year checkboxes
        this.yearsCheckBoxes.forEach(cb -> cb.setSelected(false));

        // Clear filter options container if exists
        if (this.Anchore_Pane_filtrage != null) {
            for (Node child : this.Anchore_Pane_filtrage.getChildren()) {
                if (child instanceof VBox filterContainer) {
                    for (Node filterChild : filterContainer.getChildren()) {
                        if (filterChild instanceof CheckBox cb) {
                            cb.setSelected(false);
                        }
                    }
                }
            }
        }

        // Reset sort combo box
        if (this.tricomboBox != null) {
            this.tricomboBox.setValue("");
        }

        // Reload all films
        this.flowpaneFilm.getChildren().clear();
        for (final Film film : this.l1) {
            this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
        }
        updateMovieCount();

        showToast("Filters reset!");
    }

    /**
     * Shows notifications panel/popup
     */
    @FXML
    void showNotifications(final ActionEvent event) {
        FilmUserController.LOGGER.info("Show notifications");

        // Create notification popup
        Alert notificationAlert = new Alert(Alert.AlertType.INFORMATION);
        notificationAlert.setTitle("Notifications");
        notificationAlert.setHeaderText("Your Notifications");

        VBox notificationContent = new VBox(10);
        notificationContent.setPadding(new Insets(10));

        // Sample notifications
        Label notification1 = new Label("🎬 New film \"Avatar 3\" coming soon!");
        Label notification2 = new Label("🎟️ Your booking for tomorrow is confirmed");
        Label notification3 = new Label("⭐ Rate the films you've watched recently");

        notificationContent.getChildren().addAll(notification1, notification2, notification3);

        if (notificationContent.getChildren().isEmpty()) {
            notificationContent.getChildren().add(new Label("No new notifications"));
        }

        notificationAlert.getDialogPane().setContent(notificationContent);
        notificationAlert.getDialogPane().setMinWidth(350);
        notificationAlert.showAndWait();
    }

    /**
     * Opens application settings dialog
     */
    @FXML
    void openSettings(final ActionEvent event) {
        FilmUserController.LOGGER.info("Open settings");

        // Create settings dialog
        Dialog<Void> settingsDialog = new Dialog<>();
        settingsDialog.setTitle("Settings");
        settingsDialog.setHeaderText("Application Settings");

        VBox settingsContent = new VBox(15);
        settingsContent.setPadding(new Insets(20));
        settingsContent.setPrefWidth(400);

        // Theme setting
        HBox themeBox = new HBox(10);
        themeBox.setAlignment(Pos.CENTER_LEFT);
        Label themeLabel = new Label("Theme:");
        themeLabel.setPrefWidth(150);
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Dark (Default)", "Light", "Cinema Red");
        themeCombo.setValue("Dark (Default)");
        themeBox.getChildren().addAll(themeLabel, themeCombo);

        // Language setting
        HBox langBox = new HBox(10);
        langBox.setAlignment(Pos.CENTER_LEFT);
        Label langLabel = new Label("Language:");
        langLabel.setPrefWidth(150);
        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Français", "العربية");
        langCombo.setValue("English");
        langBox.getChildren().addAll(langLabel, langCombo);

        // Notifications toggle
        HBox notifBox = new HBox(10);
        notifBox.setAlignment(Pos.CENTER_LEFT);
        Label notifLabel = new Label("Enable Notifications:");
        notifLabel.setPrefWidth(150);
        CheckBox notifCheck = new CheckBox();
        notifCheck.setSelected(true);
        notifBox.getChildren().addAll(notifLabel, notifCheck);

        // Auto-play trailers toggle
        HBox autoPlayBox = new HBox(10);
        autoPlayBox.setAlignment(Pos.CENTER_LEFT);
        Label autoPlayLabel = new Label("Auto-play Trailers:");
        autoPlayLabel.setPrefWidth(150);
        CheckBox autoPlayCheck = new CheckBox();
        autoPlayCheck.setSelected(false);
        autoPlayBox.getChildren().addAll(autoPlayLabel, autoPlayCheck);

        settingsContent.getChildren().addAll(themeBox, langBox, notifBox, autoPlayBox);

        settingsDialog.getDialogPane().setContent(settingsContent);
        settingsDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        settingsDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                showToast("Settings saved!");
            }
            return null;
        });

        settingsDialog.showAndWait();
    }

    /**
     * Shows a brief toast-style notification
     */
    private void showToast(String message) {
        // Create toast label
        Label toast = new Label(message);
        toast.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8);" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 15 25 15 25;" +
                        "-fx-background-radius: 25;" +
                        "-fx-font-size: 14px;");

        // Find root StackPane to add toast
        if (this.inlineDetailPanel != null && this.inlineDetailPanel.getScene() != null) {
            StackPane root = (StackPane) this.inlineDetailPanel.getScene().getRoot();

            // Position at bottom
            StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
            StackPane.setMargin(toast, new Insets(0, 0, 50, 0));

            root.getChildren().add(toast);

            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // Fade out after delay
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(2));
            fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

            fadeIn.play();
            fadeOut.play();
        } else {
            // Fallback: print to console
            System.out.println("Toast: " + message);
        }
    }

    private void updateMovieCount() {
        if (movieCountLabel != null && flowpaneFilm != null) {
            int count = flowpaneFilm.getChildren().size();
            movieCountLabel.setText(count + " film" + (count != 1 ? "s" : ""));
        }
    }

    private void setupEnhancedSearch() {
        if (serach_film_user == null)
            return;

        searchPopup = new javafx.stage.Popup();
        searchPopup.setAutoHide(true);

        searchResultsContainer = new VBox(5);
        searchResultsContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(25,25,35,0.98), rgba(20,20,30,0.98));" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.1);" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);");
        searchResultsContainer.setMinWidth(400);
        searchResultsContainer.setMaxWidth(500);
        searchResultsContainer.setMaxHeight(400);

        ScrollPane scrollPane = new ScrollPane(searchResultsContainer);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(400);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        searchPopup.getContent().add(scrollPane);

        serach_film_user.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() >= 2) {
                debounceSearch(newVal);
            } else if (newVal == null || newVal.isEmpty()) {
                searchPopup.hide();
                flowpaneFilm.getChildren().clear();
                createfilmCards(l1);
                updateMovieCount();
            }
        });

        serach_film_user.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (focused && serach_film_user.getText() != null && serach_film_user.getText().length() >= 2) {
                showSearchPopup();
            } else if (!focused) {
                Platform.runLater(() -> {
                    if (!searchResultsContainer.isFocused()) {
                        searchPopup.hide();
                    }
                });
            }
        });

        serach_film_user.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    searchPopup.hide();
                    event.consume();
                    break;
                case ENTER:
                    performFullSearch(serach_film_user.getText());
                    searchPopup.hide();
                    event.consume();
                    break;
                default:
                    break;
            }
        });
    }

    private void debounceSearch(String query) {
        if (searchDebounceTimer != null) {
            searchDebounceTimer.cancel();
        }
        searchDebounceTimer = new Timer();
        searchDebounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> performSearch(query));
            }
        }, SEARCH_DEBOUNCE_MS);
    }

    private void performSearch(String query) {
        if (query == null || query.trim().length() < 2)
            return;

        java.util.concurrent.CompletableFuture.supplyAsync(() -> searchEngine.search(query, UserRole.CLIENT, 10))
                .thenAccept(results -> {
                    Platform.runLater(() -> displaySearchResults(results, query));
                });
    }

    private void displaySearchResults(List<SearchResult> results, String query) {
        searchResultsContainer.getChildren().clear();

        if (results.isEmpty()) {
            Label noResults = new Label("No results for \"" + query + "\"");
            noResults.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-padding: 20;");
            searchResultsContainer.getChildren().add(noResults);
        } else {
            var grouped = results.stream().collect(Collectors.groupingBy(SearchResult::getType));

            for (EntityType type : EntityType.values()) {
                var typeResults = grouped.get(type);
                if (typeResults != null && !typeResults.isEmpty()) {
                    HBox header = new HBox(8);
                    header.setAlignment(Pos.CENTER_LEFT);
                    header.setPadding(new Insets(5, 10, 2, 10));

                    FontIcon icon = new FontIcon(type.icon + ":14");
                    icon.setIconColor(javafx.scene.paint.Color.web(type.color));

                    Label label = new Label(type.name.substring(0, 1).toUpperCase() + type.name.substring(1) + "s");
                    label.setStyle("-fx-text-fill: " + type.color + "; -fx-font-size: 11px; -fx-font-weight: bold;");

                    header.getChildren().addAll(icon, label);
                    searchResultsContainer.getChildren().add(header);

                    for (SearchResult result : typeResults) {
                        searchResultsContainer.getChildren().add(createSearchResultItem(result));
                    }
                }
            }
        }

        showSearchPopup();
    }

    private HBox createSearchResultItem(SearchResult result) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setStyle("-fx-background-radius: 8; -fx-cursor: hand;");

        StackPane thumbnail = new StackPane();
        thumbnail.setPrefSize(40, 40);
        thumbnail.setMinSize(40, 40);

        if (result.getImageUrl() != null && !result.getImageUrl().isEmpty()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            imageView.setPreserveRatio(true);

            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(40, 40);
            clip.setArcWidth(8);
            clip.setArcHeight(8);
            imageView.setClip(clip);

            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    Image img = new Image(result.getImageUrl(), 40, 40, true, true, true);
                    Platform.runLater(() -> imageView.setImage(img));
                } catch (Exception e) {
                }
            });

            thumbnail.getChildren().add(imageView);
        } else {
            javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(20);
            bg.setFill(javafx.scene.paint.Color.web(result.getColor() + "33"));
            FontIcon icon = new FontIcon(result.getIcon() + ":18");
            icon.setIconColor(javafx.scene.paint.Color.web(result.getColor()));
            thumbnail.getChildren().addAll(bg, icon);
        }

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label(result.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        title.setMaxWidth(280);

        Label subtitle = new Label(result.getSubtitle());
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");

        textBox.getChildren().addAll(title, subtitle);

        Label badge = new Label(result.getType().name.toUpperCase());
        badge.setStyle(
                "-fx-background-color: " + result.getColor() + "33;" +
                        "-fx-text-fill: " + result.getColor() + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-padding: 2 6;" +
                        "-fx-background-radius: 10;");

        item.getChildren().addAll(thumbnail, textBox, badge);

        item.setOnMouseEntered(e -> item
                .setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-radius: 8; -fx-cursor: hand;"));

        item.setOnMouseClicked(e -> {
            handleSearchResultClick(result);
            searchPopup.hide();
        });

        return item;
    }

    private void handleSearchResultClick(SearchResult result) {
        switch (result.getType()) {
            case FILM:
                Film film = new FilmService().getFilm(result.getId());
                if (film != null) {
                    showFilmDetails(film);
                    showInlineDetailPanel();
                }
                break;
            case SERIES:
                try {
                    switchtoSerie(null);
                } catch (Exception e) {
                    LOGGER.warning("Could not switch to series: " + e.getMessage());
                }
                break;
            case ACTOR:
                serach_film_user.setText(result.getTitle());
                break;
            case CINEMA:
                showToast("Cinema: " + result.getTitle());
                break;
            case PRODUCT:
                showToast("Product: " + result.getTitle());
                break;
            case CATEGORY:
                filterByKeyword(result.getTitle());
                break;
        }
    }

    private void performFullSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            flowpaneFilm.getChildren().clear();
            createfilmCards(l1);
            updateMovieCount();
            return;
        }

        List<Film> filtered = FilmUserController.rechercher(l1, query);
        flowpaneFilm.getChildren().clear();
        createfilmCards(filtered);
        updateMovieCount();
    }

    private void showSearchPopup() {
        if (!searchPopup.isShowing() && serach_film_user.getScene() != null) {
            var bounds = serach_film_user.localToScreen(serach_film_user.getBoundsInLocal());
            if (bounds != null) {
                searchPopup.show(serach_film_user.getScene().getWindow(),
                        bounds.getMinX(), bounds.getMaxY() + 5);
            }
        }
    }

    private void setupHeroSection(List<Film> films) {
        if (films == null || films.isEmpty())
            return;

        List<Review> topRatings = new ReviewService().getAverageRatingSorted();
        Film featured = null;

        if (topRatings != null && !topRatings.isEmpty() && topRatings.get(0).getFilm() != null) {
            featured = topRatings.get(0).getFilm();
        } else {
            featured = films.get(0);
        }

        this.featuredFilm = featured;

        if (heroTitle != null) {
            heroTitle.setText(featured.getTitle());
        }
        if (heroYear != null) {
            heroYear.setText(String.valueOf(featured.getReleaseYear()));
        }
        if (heroDuration != null) {
            heroDuration.setText(featured.getDurationMin() + " min");
        }
        if (heroGenre != null) {
            String categories = new FilmCategoryService().getCategoryNames(featured.getId());
            if (categories != null && !categories.isEmpty()) {
                heroGenre.setText(categories.split(",")[0].trim());
            } else {
                heroGenre.setText("Action");
            }
        }
        if (heroRating != null) {
            double rating = new ReviewService().getAverageRating(featured.getId());
            heroRating.setText(String.format("%.1f", rating));
        }
        if (heroDescription != null) {
            String desc = featured.getDescription();
            heroDescription.setText(desc != null && desc.length() > 150 ? desc.substring(0, 150) + "..." : desc);
        }

        try {
            String imageUrl = featured.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image posterImage = new Image(imageUrl, true);
                if (heroPoster != null) {
                    heroPoster.setImage(posterImage);
                }
                if (heroBackdrop != null) {
                    heroBackdrop.setImage(posterImage);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load hero images", e);
        }

        final Film finalFeatured = featured;
        if (heroPlayBtn != null) {
            heroPlayBtn.setOnAction(e -> {
                try {
                    switchtopayment(finalFeatured.getTitle());
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to switch to payment", ex);
                }
            });
        }
        if (heroTrailerBtn != null) {
            heroTrailerBtn.setOnAction(e -> {
                showTrailer(finalFeatured);
            });
        }
    }

    private void showInlineDetailPanel() {
        if (inlineDetailPanel != null) {
            inlineDetailPanel.setVisible(true);
            inlineDetailPanel.setManaged(true);
            inlineDetailPanel.setOpacity(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), inlineDetailPanel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }

    private void hideInlineDetailPanel() {
        if (inlineDetailPanel != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), inlineDetailPanel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                inlineDetailPanel.setVisible(false);
                inlineDetailPanel.setManaged(false);
                if (AnchorComments != null) {
                    AnchorComments.setVisible(false);
                    AnchorComments.setManaged(false);
                }
            });
            fadeOut.play();
        }
    }

    private void showTrailer(Film film) {
        if (trailerOverlay != null && anchorPane_Trailer != null) {
            trailerOverlay.setVisible(true);
            trailerOverlay.setManaged(true);

            final WebView webView = new WebView();
            webView.setContextMenuEnabled(false);

            Platform.runLater(() -> {
                try {
                    String trailerUrl = new FilmService().getTrailerFilm(film.getTitle());

                    if (trailerUrl != null && !trailerUrl.isEmpty() &&
                            trailerUrl.contains("/embed/") && !trailerUrl.equals("https://www.youtube.com")) {

                        String videoId = trailerUrl.substring(trailerUrl.lastIndexOf("/embed/") + 7);
                        if (videoId.contains("?")) {
                            videoId = videoId.substring(0, videoId.indexOf("?"));
                        }

                        String embedHtml = "<!DOCTYPE html><html><head>" +
                                "<meta charset='UTF-8'>" +
                                "<style>" +
                                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                                "body { background: #1a1a1a; width: 100vw; height: 100vh; display: flex; " +
                                "       align-items: center; justify-content: center; overflow: hidden; }" +
                                "iframe { width: 100%; height: 100%; border: none; }" +
                                ".error-container { color: white; text-align: center; font-family: Arial, sans-serif; "
                                +
                                "                   display: none; padding: 40px; }" +
                                ".error-container h2 { margin-bottom: 15px; color: #ff6b6b; }" +
                                ".error-container p { color: #ccc; margin: 10px 0; }" +
                                ".error-container a { color: #4dabf7; text-decoration: none; }" +
                                "</style></head><body>" +
                                "<iframe id='player' src='https://www.youtube.com/embed/" + videoId +
                                "?autoplay=1&rel=0&modestbranding=1&enablejsapi=1' " +
                                "allow='autoplay; encrypted-media' allowfullscreen></iframe>" +
                                "<div id='error' class='error-container'>" +
                                "<h2>🎬 Trailer Unavailable</h2>" +
                                "<p>The trailer for '<strong>" + escapeHtml(film.getTitle())
                                + "</strong>' cannot be played.</p>" +
                                "<p>This may be due to regional restrictions or the video being unavailable.</p>" +
                                "<p><a href='https://www.youtube.com/results?search_query=" +
                                escapeHtml(film.getTitle()).replace(" ", "+") + "+trailer' target='_blank'>" +
                                "Search on YouTube</a></p></div>" +
                                "<script>" +
                                "var iframe = document.getElementById('player');" +
                                "iframe.onerror = function() {" +
                                "  document.getElementById('player').style.display = 'none';" +
                                "  document.getElementById('error').style.display = 'block';" +
                                "};" +
                                "setTimeout(function() {" +
                                "  try { if(iframe.contentWindow.document.body.innerHTML.indexOf('Error') > -1) {" +
                                "    document.getElementById('player').style.display = 'none';" +
                                "    document.getElementById('error').style.display = 'block';" +
                                "  }} catch(e) {}" +
                                "}, 3000);" +
                                "</script></body></html>";

                        webView.getEngine().loadContent(embedHtml);
                    } else {
                        showTrailerNotAvailable(webView, film.getTitle());
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error loading trailer: " + e.getMessage(), e);
                    showTrailerError(webView);
                }
            });
            anchorPane_Trailer.getChildren().clear();
            anchorPane_Trailer.getChildren().add(webView);
            AnchorPane.setTopAnchor(webView, 0.0);
            AnchorPane.setBottomAnchor(webView, 0.0);
            AnchorPane.setLeftAnchor(webView, 0.0);
            AnchorPane.setRightAnchor(webView, 0.0);
        }
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void showTrailerNotAvailable(WebView webView, String filmName) {
        String html = "<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); " +
                "       width: 100vw; height: 100vh; display: flex; align-items: center; " +
                "       justify-content: center; font-family: 'Segoe UI', Arial, sans-serif; margin: 0; }" +
                ".container { text-align: center; color: white; padding: 40px; }" +
                ".icon { font-size: 64px; margin-bottom: 20px; }" +
                "h2 { color: #ff6b6b; margin-bottom: 15px; font-size: 28px; }" +
                "p { color: #a0a0a0; margin: 10px 0; font-size: 16px; line-height: 1.6; }" +
                ".film-name { color: #4dabf7; font-weight: bold; }" +
                ".btn { display: inline-block; margin-top: 20px; padding: 12px 30px; " +
                "       background: #e50914; color: white; text-decoration: none; " +
                "       border-radius: 4px; font-weight: bold; transition: background 0.3s; }" +
                ".btn:hover { background: #ff1a1a; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='icon'>🎬</div>" +
                "<h2>Trailer Not Available</h2>" +
                "<p>Sorry, the trailer for '<span class='film-name'>" + escapeHtml(filmName)
                + "</span>' is not available.</p>" +
                "<p>The video may have been removed or is restricted in your region.</p>" +
                "<a class='btn' href='https://www.youtube.com/results?search_query=" +
                escapeHtml(filmName).replace(" ", "+") + "+official+trailer' target='_blank'>Search on YouTube</a>" +
                "</div></body></html>";
        webView.getEngine().loadContent(html);
    }

    private void showTrailerError(WebView webView) {
        String html = "<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); " +
                "       width: 100vw; height: 100vh; display: flex; align-items: center; " +
                "       justify-content: center; font-family: 'Segoe UI', Arial, sans-serif; margin: 0; }" +
                ".container { text-align: center; color: white; padding: 40px; }" +
                ".icon { font-size: 64px; margin-bottom: 20px; }" +
                "h2 { color: #ff6b6b; margin-bottom: 15px; font-size: 28px; }" +
                "p { color: #a0a0a0; margin: 10px 0; font-size: 16px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='icon'>⚠️</div>" +
                "<h2>Connection Error</h2>" +
                "<p>Failed to load the trailer.</p>" +
                "<p>Please check your internet connection and try again.</p>" +
                "</div></body></html>";
        webView.getEngine().loadContent(html);
    }

    @FXML
    void closeTrailer(ActionEvent event) {
        closeTrailerOverlay();
    }

    private void closeTrailerOverlay() {
        if (trailerOverlay != null) {
            if (anchorPane_Trailer != null) {
                anchorPane_Trailer.getChildren().forEach(node -> {
                    if (node instanceof javafx.scene.web.WebView) {
                        javafx.scene.web.WebView wv = (javafx.scene.web.WebView) node;
                        wv.getEngine().load("about:blank");
                    }
                });
                anchorPane_Trailer.getChildren().clear();
            }

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), trailerOverlay);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                trailerOverlay.setVisible(false);
                trailerOverlay.setManaged(false);
                trailerOverlay.setOpacity(1.0);
            });
            fadeOut.play();
        }
    }

    @FXML
    void refreshMovies(ActionEvent event) {
        PageRequest filmPageRequest = PageRequest.defaultPage();
        this.l1 = new FilmService().read(filmPageRequest).getContent();
        this.flowpaneFilm.getChildren().clear();
        for (final Film film : l1) {
            this.flowpaneFilm.getChildren().add(this.createFilmCard(film));
        }
        updateMovieCount();
    }

    private void setupRecommendations() {
        if (filmScrollPane.getScene() != null && filmScrollPane.getScene().getWindow() != null) {
            flowpaneFilm.getChildren().forEach(node -> {
                if (node instanceof AnchorPane && node.getUserData() instanceof Film) {
                    Film film = (Film) node.getUserData();

                    node.setOnDragDetected(event -> {
                        Dragboard db = node.startDragAndDrop(TransferMode.COPY);
                        ClipboardContent content = new ClipboardContent();
                        content.putString("Film: " + film.getTitle() + "\nURL: " + film.getImageUrl());
                        db.setContent(content);
                        event.consume();
                    });
                }

            });

            try {
                List<Review> topRatings = new ReviewService().getAverageRatingSorted();
                if (topRatings != null && !topRatings.isEmpty()) {
                    for (int i = 0; i < Math.min(3, topRatings.size()); i++) {
                        Review filmRating = topRatings.get(i);
                        if (filmRating != null && filmRating.getFilm() != null) {
                            Film film = filmRating.getFilm();
                            flowpaneFilm.getChildren().add(createFilmCard(film));
                        }
                    }

                }

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load recommendations", e);
            }

        }

    }

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

    private AnchorPane createFilmCard(final Film film) {
        final AnchorPane copyOfAnchorPane = new AnchorPane();
        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(265, 425);
        copyOfAnchorPane.setMinSize(240, 405);
        copyOfAnchorPane.setMaxSize(290, 450);
        copyOfAnchorPane.getStyleClass().addAll("anchorfilm", "animated-button", "film-card");
        copyOfAnchorPane.setUserData(film);

        final ImageView imageView = new ImageView();
        try {
            String imagePath = film.getImageUrl();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image image = new Image(imagePath, true);
                    imageView.setImage(image);
                } catch (Exception e) {
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                        LOGGER.log(Level.WARNING, "Failed to load image URL, using default: " + imagePath, e);
                    } catch (Exception e2) {
                        LOGGER.log(Level.SEVERE, "Failed to load both URL and default image", e2);
                    }

                }

            } else {
                LOGGER.log(Level.WARNING, "Image path is null or empty for film ID: " + film.getId());
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            }

            imageView.setFitWidth(235);
            imageView.setFitHeight(295);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setLayoutX(15);
            imageView.setLayoutY(10);
            imageView.getStyleClass().add("film-card-image");

        } catch (final Exception e) {
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Could not load any image, even default", e2);
            }

            LOGGER.log(Level.WARNING, "Failed to load image for film: " + film.getId(), e);
        }

        final Label nomFilm = new Label(film.getTitle());
        nomFilm.setLayoutX(15);
        nomFilm.setLayoutY(315);
        nomFilm.setPrefSize(235, 40);
        nomFilm.setMaxWidth(235);
        nomFilm.setWrapText(true);
        nomFilm.setFont(new Font(15));
        nomFilm.getStyleClass().addAll("labeltext", "animated-text", "film-card-title");

        final Label ratefilm = new Label();
        ratefilm.setLayoutX(15);
        ratefilm.setLayoutY(358);
        ratefilm.setPrefSize(120, 25);
        ratefilm.setFont(new Font(13));
        ratefilm.getStyleClass().addAll("labeltext", "film-card-rating");
        final double rate = new ReviewService().getAverageRating(film.getId());
        ratefilm.setText(String.format("%.1f/5", rate));

        Long currentUserId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : 0L;
        final Review ratingFilm = new ReviewService().ratingExists(film.getId(), currentUserId);
        this.filmRate.setRating(null != ratingFilm ? ratingFilm.getRating() : 0);

        final FontIcon etoile = new FontIcon();
        etoile.setIconLiteral("mdi2s-star");
        etoile.setLayoutX(125);
        etoile.setLayoutY(373);
        etoile.setIconSize(16);
        etoile.setFill(Color.web("#ffaa00"));
        etoile.setEffect(new DropShadow(5, Color.rgb(255, 170, 0, 0.8)));

        final Button button = new Button("RESERVE");
        button.setLayoutX(15);
        button.setLayoutY(385);
        button.setPrefSize(235, 36);
        button.getStyleClass().addAll("action-button", "animated-button", "film-reserve-button");
        button.setOnAction(event -> {
            try {
                this.switchtopayment(nomFilm.getText());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

        });

        final Hyperlink hyperlink = new Hyperlink("View Details");
        hyperlink.setLayoutX(145);
        hyperlink.setLayoutY(361);
        hyperlink.getStyleClass().add("film-details-link");
        hyperlink.setOnAction(event -> {
            showFilmDetails(film);
        });
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);

        copyOfAnchorPane.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                showFilmDetails(film);
            }
        });

        return copyOfAnchorPane;
    }

    private void showFilmDetails(Film film) {
        final Film film1 = new Film(film);
        this.filmId = film.getId();

        if (this.filmNomDetail != null) {
            this.filmNomDetail.setText(film1.getTitle());
        }
        if (this.descriptionDETAILfilm != null) {
            this.descriptionDETAILfilm.setText(film1.getDescription());
        }

        if (genreTags != null) {
            genreTags.getChildren().clear();
            String categories = new FilmCategoryService().getCategoryNames(film1.getId());
            for (String cat : categories.split(",")) {
                Label genreLabel = new Label(cat.trim());
                genreLabel.getStyleClass().add("genre-tag");
                genreTags.getChildren().add(genreLabel);
            }
        }

        try {
            if (this.imagefilmDetail != null) {
                this.imagefilmDetail.setImage(new Image(film1.getImageUrl()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load detail image URL: " + film1.getImageUrl(), e);
            try {
                if (this.imagefilmDetail != null) {
                    this.imagefilmDetail.setImage(
                            new Image(getClass().getResourceAsStream("/img/films/default.jpg")));
                }
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Failed to load default detail image", e2);
            }
        }

        final double rate1 = new ReviewService().getAverageRating(film1.getId());
        if (this.labelavregeRate != null) {
            this.labelavregeRate.setText(String.format("%.1f", rate1));
        }

        Long userId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : 0L;
        final Review ratingFilm1 = new ReviewService().ratingExists(film1.getId(), userId);
        if (this.filmRate != null) {
            this.filmRate.setRating(null != ratingFilm1 ? ratingFilm1.getRating() : 0);

            this.filmRate.ratingProperty().addListener((observableValue, oldVal, newVal) -> {
                final ReviewService ratingFilmService = new ReviewService();
                Long currentUserId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId()
                        : 0L;
                final Review existingRating = ratingFilmService.ratingExists(film1.getId(), currentUserId);
                if (null != existingRating) {
                    ratingFilmService.delete(existingRating);
                }
                ratingFilmService
                        .create(new Review(film1, (Client) SessionManager.getCurrentUser(), newVal.intValue()));
                final double newRate = new ReviewService().getAverageRating(film1.getId());
                if (this.labelavregeRate != null) {
                    this.labelavregeRate.setText(String.format("%.1f", newRate));
                }
                refreshTopPicks();
            });
        }

        if (this.trailer_Button != null) {
            this.trailer_Button.setOnAction(trailerEvent -> {
                showTrailer(film1);
            });
        }

        if (this.reserver_Film != null) {
            this.reserver_Film.setOnAction(e -> {
                try {
                    switchtopayment(film1.getTitle());
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to switch to payment", ex);
                }
            });
        }

        generateQRCode(film1);

        showInlineDetailPanel();
    }

    private void refreshTopPicks() {
        if (this.topthreeVbox != null) {
            this.topthreeVbox.getChildren().clear();
            for (int i = 0; i < 3; i++) {
                this.topthreeVbox.getChildren().add(this.createtopthree(i));
            }
        }
    }

    private void generateQRCode(Film film) {
        if (this.qrcode == null)
            return;

        try {
            String url = FilmService.getIMDBUrlbyNom(film.getTitle());
            if (url == null || url.isEmpty() || url.contains("400") || url.contains("403")) {
                LOGGER.log(Level.WARNING, "Invalid URL from IMDB service, using fallback");
                url = "https://www.imdb.com/find?q=" + film.getTitle().replace(" ", "+");
            }
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            final BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            this.qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            this.qrcode.setVisible(true);
        } catch (final WriterException e) {
            LOGGER.log(Level.WARNING, "Failed to generate QR code", e);
            this.qrcode.setVisible(false);
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Error generating QR code: " + e.getMessage(), e);
            this.qrcode.setVisible(false);
        }
    }

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
                if (actor.getImageUrl() != null && !actor.getImageUrl().isEmpty()) {
                    try {
                        imageView.setImage(new Image(actor.getImageUrl()));
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load actor image URL: " + actor.getImageUrl(), e);
                        try {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                        } catch (Exception e2) {
                            LOGGER.log(Level.SEVERE, "Failed to load default actor image", e2);
                        }

                    }

                } else {
                    try {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/img/actors/default.jpg")));
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load default actor image", e);
                    }

                }

            } catch (final Exception e) {
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

            final String actorDetailsText = actor.getName().trim() + ": Films";
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

            final TextArea actorBio = new TextArea(actor.getBiography());
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

    public AnchorPane createtopthree(final int filmRank) {
        final List<Review> ratingFilmList = new ReviewService().getAverageRatingSorted();
        final AnchorPane anchorPane = new AnchorPane();

        if (ratingFilmList.size() > filmRank) {
            anchorPane.setLayoutX(0);
            anchorPane.setLayoutY(0);
            anchorPane.setPrefSize(420, 180);
            anchorPane.getStyleClass().addAll("top-film-card", "animated-button");
            anchorPane.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, rgba(139, 0, 0, 0.95), rgba(178, 34, 34, 0.85));"
                            +
                            "-fx-background-radius: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.8), 20, 0, 0, 5);" +
                            "-fx-border-color: rgba(255, 68, 68, 0.4);" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 20;");

            final Review ratingFilm = ratingFilmList.get(filmRank);
            if (ratingFilm == null || ratingFilm.getFilm() == null) {
                LOGGER.warning("Invalid rating film data at rank: " + filmRank);
                return anchorPane;
            }

            final Film film = ratingFilm.getFilm();
            final ImageView imageView = new ImageView();

            try {
                String imagePath = film.getImageUrl();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        Image image = new Image(imagePath);
                        imageView.setImage(image);
                        imageView.setFitWidth(130);
                        imageView.setFitHeight(160);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setLayoutX(15);
                        imageView.setLayoutY(10);
                        imageView.setStyle(
                                "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.7), 15, 0, 0, 3);" +
                                        "-fx-background-radius: 12;");
                    } catch (Exception e) {
                        LOGGER.warning("Failed to load image from URL: " + imagePath + ", " + e.getMessage());
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
                        } catch (Exception e2) {
                            LOGGER.severe("Failed to load default image: " + e2.getMessage());
                        }

                    }

                }

            } catch (Exception e) {
                LOGGER.warning("Error loading image for film: " + film.getId() + ", " + e.getMessage());
            }

            try {
                final Label nomFilm = new Label(film.getTitle() != null ? film.getTitle() : "Untitled");
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
                });
                button.setOnMouseExited(e -> {
                    button.setStyle(
                            "-fx-background-color: linear-gradient(to bottom right, #8b0000, #b22222);" +
                                    "-fx-background-radius: 18;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(139, 0, 0, 0.9), 12, 0, 0, 3);");
                });

                final Rating rating = new Rating();
                rating.setLayoutX(145);
                rating.setLayoutY(55);
                rating.setPrefSize(176, 32);
                rating.setPartialRating(true);
                rating.setStyle(
                        "-fx-rating-fill: #ffaa00;" +
                                "-fx-rating-empty-fill: rgba(255, 170, 0, 0.2);");

                final double rate = new ReviewService().getAverageRating(film.getId());
                rating.setRating(rate);
                rating.setDisable(true);

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

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error creating film card components", e);
            }

        }

        return anchorPane;
    }
}

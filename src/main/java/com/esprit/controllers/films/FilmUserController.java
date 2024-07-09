package com.esprit.controllers.films;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcoment;
import com.esprit.models.films.RatingFilm;
import com.esprit.models.users.Client;
import com.esprit.services.films.*;
import com.esprit.services.users.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import org.controlsfx.control.Rating;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Is responsible for handling user interactions related to viewing and commenting
 * on films within a cinema website. It displays a scrolling pane containing all
 * comments for a given film, and allows users to add new comments or view previous
 * comments by clicking buttons in the interface. The controller also provides methods
 * for retrieving all comments for a specific film and displaying them in the scrolling
 * pane.
 */
public class FilmUserController extends Application {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
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
    private ScrollPane actorScrollPane;
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
    private int filmId;
    /**
     * Queries a list of films for any that contain a specified search term in their name,
     * and returns a list of matches.
     * 
     * @param liste list of films that will be searched for matching titles within the
     * provided `recherche` parameter.
     * 
     * 	- It is a list of `Film` objects
     * 	- Each element in the list has a `nom` attribute that can contain the search query
     * 
     * @param recherche search query, which is used to filter the list of films in the function.
     * 
     * @returns a list of `Film` objects that contain the searched string in their name.
     * 
     * 	- The list of films is filtered based on the search query, resulting in a subset
     * of films that match the query.
     * 	- The list contains only films with a non-null `nom` attribute and containing the
     * search query in their name.
     * 	- The list is returned as a new list of films, which can be used for further
     * processing or analysis.
     */
    @FXML
    public static List<Film> rechercher(List<Film> liste, String recherche) {
        List<Film> resultats = new ArrayList<>();
        for (Film element : liste) {
            if (element.getNom() != null && element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }
        return resultats;
    }
    /**
     * Loads an FXML user interface from a resource file, sets data for the controller,
     * and displays the stage with the loaded scene.
     * 
     * @param nom name of the client for which the payment user interface is to be displayed.
     */
    public void switchtopayment(String nom) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Paymentuser.fxml"));
        AnchorPane root = fxmlLoader.load();
        Stage stage = (Stage) reserver_Film.getScene().getWindow();
        PaymentuserController paymentuserController = fxmlLoader.getController();
        Client client = (Client) stage.getUserData();
        paymentuserController.setData(client, nom);
        Scene scene = new Scene(root, 1507, 855);
        stage.setScene(scene);
    }
    /**
     * Creates film cards for a list of films by creating an AnchorPane container for
     * each card and adding it to a `FlowPane` containing other cards.
     * 
     * @param Films list of films to create film cards for, which are then added as
     * children of the `flowpaneFilm`.
     * 
     * 	- `Film` objects are contained in the list.
     * 	- Each `Film` object has various attributes, such as title, director, year of
     * release, etc.
     */
    private void createfilmCards(List<Film> Films) {
        for (Film film : Films) {
            AnchorPane cardContainer = createFilmCard(film);
            flowpaneFilm.getChildren().add(cardContainer);
        }
    }
    /**
     * Sets up the user interface for a film application, including creating a flow pane
     * to display films and three combos to display top actors or directors.
     */
    public void initialize() {
        top3combobox.getItems().addAll("Top 3 Films", "Top 3 Actors");
        top3combobox.setValue("Top 3 Films");
        top3combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Top 3 Films")) {
                topthreeVbox1.setVisible(false);
                topthreeVbox.setVisible(true);
            } else if (newValue.equals("Top 3 Actors")) {
                ObservableList<Node> topthreevboxactorsChildren = topthreeVbox1.getChildren();
                topthreevboxactorsChildren.clear();
                topthreeVbox.setVisible(false);
                topthreeVbox1.setVisible(true);
                for (int i = 1; i < flowpaneFilm.getChildren().size() && i < 4; i++) {
                    topthreevboxactorsChildren.add(createActorDetails(i));
                }
                topthreeVbox1.setSpacing(10);
            }
        });
        tricomboBox.getItems().addAll("nom", "annederalisation");
        tricomboBox.setValue("");
        tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            flowpaneFilm.getChildren().clear();
            List<Film> filmList = new FilmService().sort(t1);
            for (Film film : filmList) {
                flowpaneFilm.getChildren().add(createFilmCard(film));
            }
        });
        FilmService filmService1 = new FilmService();
        l1 = filmService1.read();
        serach_film_user.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Film> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            flowpaneFilm.getChildren().clear();
            createfilmCards(produitsRecherches);
        });
        flowpaneFilm = new FlowPane();
        filmScrollPane.setContent(flowpaneFilm);
        filmScrollPane.setFitToWidth(true);
        filmScrollPane.setFitToHeight(true);
        topthreeVbox.setSpacing(10);
        FilmService filmService = new FilmService();
        // String trailerURL = filmService.getTrailerFilm("garfield");
        flowpaneFilm.setHgap(10);
        flowpaneFilm.setVgap(10);
        closeDetailFilm.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * Sets the visibility of an `AnchorPane` element to false, sets the opacity of another
             * `AnchorPane` element to 1, and disables the latter element.
             * 
             * @param event ActionEvent object that triggered the handling of the event by the
             * `handle()` method.
             * 
             * 	- `event`: an instance of `ActionEvent`, representing a user action that triggered
             * the function.
             */
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(false);
                anchorPaneFilm.setOpacity(1);
                anchorPaneFilm.setDisable(false);
            }
        });
        // Set the padding
        flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));
        List<Film> filmList = new FilmService().read();
        for (Film film : filmList) {
            flowpaneFilm.getChildren().add(createFilmCard(film));
        }
        ObservableList<Node> topthreevboxChildren = topthreeVbox.getChildren();
        for (int i = 0; i < flowpaneFilm.getChildren().size() && i < 3; i++) {
            topthreevboxChildren.add(createtopthree(i));
        }
    }
    /**
     * Filters a `Flowpane` of `AnchorPane` elements based on the text content of a
     * `.nomFilm` label, making the visible or invisible elements dependent on the keyword
     * search result.
     * 
     * @param keyword search term used to filter the film cards, and its value determines
     * whether or not a card is visible and managed.
     */
    private void filterByName(String keyword) {
        for (Node node : flowpaneFilm.getChildren()) {
            AnchorPane filmCard = (AnchorPane) node;
            Label nomFilm = (Label) filmCard.lookup(".nomFilm"); // Supposons que le nom du film soit représenté par une
                                                                 // classe CSS ".nomFilm"
            if (nomFilm != null) {
                boolean isVisible = nomFilm.getText().toLowerCase().contains(keyword); // Vérifie si le nom du film
                                                                                       // contient le mot-clé de
                                                                                       // recherche
                filmCard.setVisible(isVisible); // Définit la visibilité de la carte en fonction du résultat du filtrage
                filmCard.setManaged(isVisible); // Définit la gestion de la carte en fonction du résultat du filtrage
            }
        }
    }
    /**
     * Creates a UI component representing a movie card with various details and ratings.
     * It generates a QR code for the movie's IMDB page, which can be scanned to open the
     * page in a browser. The function also adds event listeners to handle clicks on the
     * movie card and the QR code.
     * 
     * @param film film object that will be displayed in the anchor pane, and is used to
     * retrieve the film's information such as title, image, rating, and trailer link.
     * 
     * 	- `id`: a unique identifier for the film
     * 	- `nom`: the film's title
     * 	- `description`: a brief description of the film
     * 	- `duree`: the film's duration
     * 	- `annderalisation`: the film's release date
     * 	- `categories`: an array of categories the film belongs to
     * 	- `actors`: an array of actors appearing in the film.
     * 
     * @returns an AnchorPane with a QR code generator, trailer player, and rating system
     * for a given film.
     * 
     * 	- `hyperlink`: A Hyperlink component that displays the film's title and opens the
     * IMDB page when clicked.
     * 	- `imagefilmDetail`: An Image component that displays the film's poster image.
     * 	- `descriptionDETAILfilm`: A Text component that displays the film's detailed description.
     * 	- `labelavregeRate`: A Label component that displays the average rating of the film.
     * 	- `ratefilm`: A Text component that displays the current rating of the film.
     * 	- `topthreeVbox`: A VBox component that displays the top three actors of the film.
     * 	- `trailer_Button`: A Button component that plays the film's trailer when clicked.
     * 
     * Note: The output is a JavaFX AnchorPane that contains all the components explained
     * above.
     */
    private AnchorPane createFilmCard(Film film) {
        AnchorPane copyOfAnchorPane = new AnchorPane();
        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(213, 334);
        copyOfAnchorPane.getStyleClass().add("anchorfilm");
        ImageView imageView = new ImageView();
        try {
            if (!film.getImage().isEmpty()) {
                imageView.setImage(new Image(film.getImage()));
                imageView.setLayoutX(45);
                imageView.setLayoutY(7);
                imageView.setFitHeight(193);
                imageView.setFitWidth(132);
                imageView.getStyleClass().addAll("bg-white");
            }
        } catch (Exception e) {
            System.out.println("Image not found LINE 263");
            // e.printStackTrace();
        }
        Label nomFilm = new Label(film.getNom());
        nomFilm.setLayoutX(23);
        nomFilm.setLayoutY(200);
        nomFilm.setPrefSize(176, 32);
        nomFilm.setFont(new Font(18)); // Copy the font size
        nomFilm.getStyleClass().addAll("labeltext");
        Label ratefilm = new Label(film.getNom());
        ratefilm.setLayoutX(15);
        ratefilm.setLayoutY(222);
        ratefilm.setPrefSize(176, 32);
        ratefilm.setFont(new Font(18)); // Copy the font size
        ratefilm.getStyleClass().addAll("labeltext");
        double rate = new RatingFilmService().getavergerating(film.getId());
        ratefilm.setText(rate + "/5");
        RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film.getId(), /* (Client) stage.getUserData() */2);
        filmRate.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);
        FontIcon etoile = new FontIcon();
        etoile.setIconLiteral("fa-star");
        etoile.setLayoutX(128);
        etoile.setLayoutY(247);
        etoile.setFill(Color.web("#f2b604"));
        Button button = new Button("reserve");
        button.setLayoutX(22);
        button.setLayoutY(278);
        button.setPrefSize(172, 42);
        button.getStyleClass().addAll("sale");
        button.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * Is handling an `ActionEvent` and performs a payment related task by calling
             * `switchtopayment()` method, which takes a film title as input, and catches any IO
             * exception that might occur during the payment process and re-throws it as a Runtime
             * Exception.
             * 
             * @param event result of an action event, which is passed to the `handle()` method
             * as an event object.
             * 
             * 	- `event`: an instance of `ActionEvent`, representing an event triggered by the
             * user's action.
             */
            @Override
            public void handle(ActionEvent event) {
                try {
                    switchtopayment(nomFilm.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Hyperlink hyperlink = new Hyperlink("Details");
        hyperlink.setLayoutX(89);
        hyperlink.setLayoutY(251);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * Generates a QR code for a movie's IMDB page, displays it in an image view, and
             * listens for clicks on the image to open the movie's trailer in a web view.
             * 
             * @param event ActionEvent that triggered the function, providing the source of the
             * event and allowing for proper handling of the corresponding action.
             * 
             * 	- `event`: An instance of `ActionEvent`, representing an action event triggered
             * by the user.
             * 	- `movie`: The `Movie` object associated with the event, containing information
             * about the movie being displayed.
             */
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(true);
                anchorPaneFilm.setOpacity(0.26);
                anchorPaneFilm.setDisable(true);
                Film film1 = new Film(film);
                filmId = film.getId();
                filmNomDetail.setText(film1.getNom());
                descriptionDETAILfilm.setText(
                        film1.getDescription() + "\nTime:" + film1.getDuree() + "\nYear:" + film1.getAnnederalisation()
                                + "\nCategories: " + new FilmcategoryService().getCategoryNames(film1.getId())
                                + "\nActors: " + new ActorfilmService().getActorsNames(film1.getId()));
                imagefilmDetail.setImage(new Image(film1.getImage()));
                double rate = new RatingFilmService().getavergerating(film1.getId());
                labelavregeRate.setText(rate + "/5");
                RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film1.getId(), /*
                                                                                             * (Client)
                                                                                             * stage.getUserData()
                                                                                             */2);
                Rating rateFilm = new Rating();
                rateFilm.setLayoutX(103);
                rateFilm.setLayoutY(494);
                rateFilm.setPrefSize(199, 35);
                rateFilm.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);
                // Stage stage = (Stage) hyperlink.getScene().getWindow();
                final String text = film1.getNom();// Créer un objet QRCodeWriter pour générer le QR code
                final String url = FilmService.getIMDBUrlbyNom(text);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix;
                try {
                    bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                // Convertir la matrice de bits en image BufferedImage
                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                qrcode.setOnMouseClicked(e -> {
                    HostServices hostServices = getHostServices();
                    // Open the URL in the default system browser
                    hostServices.showDocument(url);
                }); // HBox qrCodeImgModel = (HBox) ((Node)
                    // event.getSource()).getScene().lookup("#qrCodeImgModel");
                qrcode.setVisible(true);
                rateFilm.ratingProperty().addListener(new ChangeListener<Number>() {
                    /**
                     * Updates the rating of a film based on user input, deleting any existing rating and
                     * creating a new one with the updated average rating.
                     * 
                     * @param observableValue observational value of the film's rating, which is being
                     * changed by the user through the interface.
                     * 
                     * 	- It is an observable value of type `Number`.
                     * 	- The value passed to the function is either a new number or an updated number
                     * from a previous value.
                     * 	- The number can represent any rating value between 0 and 5, inclusive.
                     * 
                     * @param number 2nd rating of the film, which is used to create or update the
                     * corresponding RatingFilm entity in the database.
                     * 
                     * 	- `number`: An instance of `Number`, representing the updated value of the
                     * observable property.
                     * 	- `t1`: A number that represents the previous value of the observable property.
                     * 
                     * @param t1 2nd rating of the film and is used to calculate the average rating.
                     * 
                     * 	- `t1`: A `Number` object representing the second rating value for the given film
                     * ID.
                     */
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        RatingFilmService ratingFilmService = new RatingFilmService();
                        RatingFilm ratingFilm = ratingFilmService.ratingExiste(film1.getId(), 2/*
                                                                                                * (Client)
                                                                                                * stage.getUserData()
                                                                                                */);
                        System.out.println("---------   " + film1.getId());
                        if (ratingFilm != null)
                            ratingFilmService.delete(ratingFilm);
                        ratingFilmService.create(new RatingFilm(film1,
                                /* (Client) stage.getUserData() */(Client) new UserService().getUserById(2),
                                t1.intValue()));
                        double rate = new RatingFilmService().getavergerating(film1.getId());
                        labelavregeRate.setText(rate + "/5");
                        ratefilm.setText(rate + "/5");
                        topthreeVbox.getChildren().clear();
                        for (int i = 0; i < 3; i++) {
                            topthreeVbox.getChildren().add(createtopthree(i));
                        }
                    }
                });
                trailer_Button.setOnAction(new EventHandler<ActionEvent>() {
                    /**
                     * Enables all disabled elements in an anchor pane, loads a web view with a trailer
                     * film based on a film's name, and sets the anchor pane to visible and adds the
                     * loaded web view as its only child element. It also handles the escape key by
                     * disabling all elements again and hiding the anchor pane.
                     * 
                     * @param event ActionEvent object that triggered the function's execution, providing
                     * information about the action that was performed, such as the source of the event
                     * and the key code pressed.
                     * 
                     * 	- `event`: The `ActionEvent` object representing the user's action that triggered
                     * the function.
                     */
                    @Override
                    public void handle(ActionEvent event) {
                        anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(false);
                        });
                        WebView webView = new WebView();
                        System.out.println(film1.getNom());
                        webView.getEngine().load(new FilmService().getTrailerFilm(film1.getNom()));
                        System.out.println("film passed");
                        anchorPane_Trailer.setVisible(true);
                        anchorPane_Trailer.getChildren().add(webView);
                        anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                            /**
                             * Is triggered when the ESCAPE key is pressed. It disables all children nodes in an
                             * anchor pane and hides the anchor pane itself.
                             * 
                             * @param keyEvent event object that contains information about the key that was
                             * pressed, which is used to determine how to handle the event in the `handle()` method.
                             * 
                             * 	- `keyEvent.getCode()`: Returns the key code associated with the event. In this
                             * case, it is `KeyCode.ESCAPE`.
                             */
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                    anchorPane_Trailer.getChildren().forEach(node -> {
                                        node.setDisable(true);
                                    });
                                    anchorPane_Trailer.setVisible(false);
                                }
                            }
                        });
                    }
                });
                detalAnchorPane.getChildren().add(rateFilm);
            }
        });
        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);
        return copyOfAnchorPane;
    }
    /**
     * Creates an AnchorPane that displays an actor's image, name, and biography. The
     * image is set to a predefined size, while the label and TextArea are set to adjustable
     * sizes based on the content.
     * 
     * @param actorPlacement placement of the actor in the scene, which is used to retrieve
     * the corresponding actor details and image from the ActorService.
     * 
     * @returns an AnchorPane containing three components: an image view, a label with
     * actor details, and a text area with actor biography.
     * 
     * 	- The anchor pane (`anchorPane`) is a container that holds the other components.
     * 	- The image view (`imageView`) displays an image related to the actor.
     * 	- The Label (`actorDetails`) shows the actor's name and number of appearances in
     * films.
     * 	- The TextArea (`actorBio`) contains the actor's biography.
     * 	- The anchor pane has a prefSize of 244 x 226 pixels, with a background color of
     * "meilleurfilm".
     * 	- The image view, label, and text area have a layoutX of 0, a layoutY of 0, and
     * a layout width of 167 and height of 122 pixels.
     * 	- The image view and text area have a fit height and width of 167 and 122 pixels,
     * respectively.
     * 	- The label has a font size of 22 pixels.
     */
    public AnchorPane createActorDetails(int actorPlacement) {
        ActorService as = new ActorService();
        Actor actor = as.getActorByPlacement(actorPlacement);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefSize(244, 226);
        anchorPane.getStyleClass().add("meilleurfilm");
        if (actor != null) {
            ImageView imageView = new ImageView();
            try {
                if (!actor.getImage().isEmpty()) {
                    imageView.setImage(new Image(actor.getImage()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            imageView.setLayoutX(21);
            imageView.setLayoutY(21);
            imageView.setFitHeight(167);
            imageView.setFitWidth(122);
            imageView.getStyleClass().addAll("bg-white");
            // Combine actor name and number of appearances in one label
            String actorDetailsText = actor.getNom().trim() + ": " + actor.getNumberOfAppearances() + " Films";
            System.out.println(actorDetailsText);
            Label actorDetails = new Label(actorDetailsText);
            actorDetails.setLayoutX(153);
            actorDetails.setLayoutY(8); // Adjusted to top, similar to imageView
            actorDetails.setPrefSize(500, 70);
            actorDetails.setFont(new Font(22));
            actorDetails.setTextFill(Color.WHITE);
            // Actor biography
            TextArea actorBio = new TextArea(actor.getBiographie());
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
     * Creates an AnchorPane with a rating film, image, and buttons to reserve or view
     * more information about the film.
     * 
     * @param filmRank ranking of the film to be displayed in the AnchorPane, with higher
     * ranks displaying more prominently.
     * 
     * @returns an AnchorPane containing a label, a button, a rating widget, and an image
     * view.
     * 
     * 	- `anchorPane`: A `AnchorPane` object that contains three components - `nomFilm`,
     * `button`, and `rating`.
     * 	- `nomFilm`: A `Label` object that displays the name of the film.
     * 	- `button`: A `Button` object that allows users to reserve the film.
     * 	- `rating`: A `Rating` object that displays the rating of the film.
     * 	- `imageView`: An `ImageView` object that displays an image related to the film.
     * 
     * All these components are added to the `anchorPane` using the `getChildren()` method.
     * The `anchorPane` is created by initializing a new instance of the `AnchorPane`
     * class, and then adding the three components to it using the `getChildren()` method.
     */
    public AnchorPane createtopthree(int filmRank) {
        List<RatingFilm> ratingFilmList = new RatingFilmService().getavergeratingSorted();
        AnchorPane anchorPane = new AnchorPane();
        if (ratingFilmList.size() > filmRank) {
            anchorPane.setLayoutX(0);
            anchorPane.setLayoutY(0);
            anchorPane.setPrefSize(544, 226);
            anchorPane.getStyleClass().add("meilleurfilm");
            RatingFilm ratingFilm = ratingFilmList.get(filmRank);
            ImageView imageView = new ImageView();
            try {
                if (!ratingFilm.getId_film().getImage().isEmpty()) {
                    imageView.setImage(new Image(ratingFilm.getId_film().getImage()));
                    imageView.setLayoutX(21);
                    imageView.setLayoutY(21);
                    imageView.setFitHeight(167);
                    imageView.setFitWidth(122);
                    imageView.getStyleClass().addAll("bg-white");
                }
            } catch (Exception e) {
                System.out.println("no image found line 493");
                //e.printStackTrace();
            }
            try {
                Label nomFilm = new Label(ratingFilm.getId_film().getNom());
                nomFilm.setLayoutX(153);
                nomFilm.setLayoutY(87);
                nomFilm.setPrefSize(205, 35);
                nomFilm.setFont(new Font(22));
                nomFilm.setTextFill(Color.WHITE);// Copy the font size
                Button button = new Button("reserve");
                button.setLayoutX(346);
                button.setLayoutY(154);
                button.setPrefSize(172, 42);
                button.getStyleClass().addAll("sale");
                Rating rating = new Rating();
                rating.setLayoutX(344);
                rating.setLayoutY(38);
                rating.setPrefSize(176, 32);
                rating.setPartialRating(true);
                double rate = new RatingFilmService().getavergerating(ratingFilm.getId_film().getId());
                rating.setRating(rate);
                rating.setDisable(true);
                anchorPane.getChildren().addAll(nomFilm, button, rating, imageView);
            } catch (Exception e) {
                System.out.println("line 522" +e.getMessage());
            }
        }
        return anchorPane;
    }
    /**
     * Retrieves a list of unique film release years from a database using `FilmService`.
     * 
     * @returns a list of unique cinema years obtained from the films' release dates.
     * 
     * 1/ The list contains unique `Integer` objects representing the cinema years.
     * 2/ The list is generated by transforming the original list of films using a series
     * of methods, specifically `map`, `distinct`, and `collect`.
     * 3/ The transformation involves extracting the year of release from each film object
     * using the `getAnnederalisation` method.
     */
    private List<Integer> getCinemaYears() {
        FilmService cinemaService = new FilmService();
        List<Film> cinemas = cinemaService.read();
        // Extraire les années de réalisation uniques des films
        return cinemas.stream()
                .map(Film::getAnnederalisation)
                .distinct()
                .collect(Collectors.toList());
    }
    /**
     * Sets the opacity of a panel to 0.5 and makes a pane visible, clears a list of
     * checkboxes, recieves unique cinema years from a database, creates a VBox for each
     * year, adds the VBox to an anchor pane, and makes the anchor pane visible.
     * 
     * @param event action event that triggered the filtration process.
     * 
     * 	- Event type: `ActionEvent`
     * 	- Target: `Anchore_Pane_filtrage` (a pane in the scene)
     * 	- Command: Unspecified (as the function does not use a specific command)
     */
    @FXML
    void filtrer(ActionEvent event) {
        flowpaneFilm.setOpacity(0.5);
        Anchore_Pane_filtrage.setVisible(true);
        // Nettoyer la liste des cases à cocher
        yearsCheckBoxes.clear();
        // Récupérer les années de réalisation uniques depuis la base de données
        List<Integer> years = getCinemaYears();
        // Créer des VBox pour les années de réalisation
        VBox yearsCheckBoxesVBox = new VBox();
        Label yearLabel = new Label("Années de réalisation");
        yearLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        yearsCheckBoxesVBox.getChildren().add(yearLabel);
        for (Integer year : years) {
            CheckBox checkBox = new CheckBox(String.valueOf(year));
            yearsCheckBoxesVBox.getChildren().add(checkBox);
            yearsCheckBoxes.add(checkBox);
        }
        yearsCheckBoxesVBox.setLayoutX(25);
        yearsCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le FilterAnchor
        Anchore_Pane_filtrage.getChildren().addAll(yearsCheckBoxesVBox);
        Anchore_Pane_filtrage.setVisible(true);
    }
    /**
     * Sets the opacity and visibility of an AnchorPane, and also makes a different
     * AnchorPane visible.
     * 
     * @param event user interaction that triggered the execution of the `closercommets`
     * method.
     * 
     * Event: ActionEvent
     * 
     * 	- Target: detalAnchorPane
     * 	- Action: setOpacity() and setVisible() methods
     */
    @FXML
    void closercommets(ActionEvent event) {
        detalAnchorPane.setOpacity(1);
        AnchorComments.setVisible(false);
        detalAnchorPane.setVisible(true);
    }
    /**
     * Filters a list of cinemas based on user-selected years of release and displays the
     * filtered cinemas in a flow pane.
     * 
     * @param event occurrence of an action event, such as clicking on the "Filtrer"
     * button, that triggers the execution of the `filtrercinema()` method.
     * 
     * 	- `event` is an `ActionEvent`, indicating that the function was called as a result
     * of user action.
     * 	- The `event` object contains information about the source of the action, such
     * as the button or link that was clicked.
     */
    @FXML
    void filtrercinema(ActionEvent event) {
        flowpaneFilm.setOpacity(1);
        Anchore_Pane_filtrage.setVisible(false);
        // Récupérer les années de réalisation sélectionnées
        List<Integer> selectedYears = getSelectedYears();
        // Filtrer les films en fonction des années de réalisation sélectionnées
        List<Film> filteredCinemas = l1.stream()
                .filter(cinema -> selectedYears.isEmpty() || selectedYears.contains(cinema.getAnnederalisation()))
                .collect(Collectors.toList());
        // Afficher les films filtrés
        flowpaneFilm.getChildren().clear();
        createfilmCards(filteredCinemas);
    }
    /**
     * Retrieves the selected years from an `AnchorPane` widget, filters out non-selected
     * years using `filter`, maps the selected check boxes to their corresponding integers
     * using `map`, and collects the list of integers representing the selected years.
     * 
     * @returns a list of integer values representing the selected years.
     * 
     * The output is a list of integers representing the selected years from the check
     * boxes in the AnchorPane.
     * 
     * Each integer in the list corresponds to an individual check box that was selected
     * by the user.
     * 
     * The list contains only the unique years that were selected by the user, without
     * duplicates or invalid input.
     */
    private List<Integer> getSelectedYears() {
        // Récupérer les années de réalisation sélectionnées dans l'AnchorPane de
        // filtrage
        return yearsCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> Integer.parseInt(checkBox.getText()))
                .collect(Collectors.toList());
    }
    /**
     * Loads a FXML file "SeriesClient.fxml" into a stage, replacing the current scene.
     * 
     * @param event event that triggered the execution of the `switchtoajouterCinema()`
     * method.
     * 
     * 	- `event`: An `ActionEvent` object representing a user action.
     */
    public void switchtoajouterCinema(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) product.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an fxml file and displays its content on a stage with specified dimensions.
     * 
     * @param event Event object that triggered the call to the `switchtevent()` method.
     * 
     * 	- `Event`: This is the type of event that triggered the function execution.
     */
    public void switchtevent(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) event_button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads a FXML file using the `FXMLLoader` class, creates a new `AnchorPane` root
     * element, sets it as the scene of a stage, and displays the stage in a window with
     * a specified size.
     * 
     * @param event ActionEvent that triggered the call to the `switchtcinemaaa()` method.
     * 
     * 	- `ActionEvent event`: Represents an action that occurred in the application,
     * carrying information about the source of the action and the type of action performed.
     */
    public void switchtcinemaaa(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DashboardClientCinema.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) Cinema_Button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads an FXML file, creates a Stage and sets the Scene for displaying a user interface.
     * 
     * @param event event that triggered the call to the `switchtoajouterproduct()` method.
     * 
     * 	- Type: ActionEvent - indicates that the event was triggered by an action (e.g.,
     * button click)
     * 	- Target: null - indicates that the event did not originate from a specific
     * component or element
     * 	- Code: 0 - no code is provided with this event
     */
    public void switchtoajouterproduct(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) product.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads and displays a FXML file using the `FXMLLoader` class, replacing the current
     * scene with the new one.
     * 
     * @param event ActionEvent that triggered the call to the `switchtoSerie()` method.
     * 
     * 	- Type: ActionEvent, indicating that the event was triggered by a user action.
     */
    public void switchtoSerie(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) SerieButton.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Allows users to add comments to a film by providing a text input, displaying an
     * alert if the comment is empty, and then creating a new Filmcoment object with the
     * provided message, user ID, and film ID using the FilmcomentService.
     */
    @FXML
    void addCommentaire() {
        String message = txtAreaComments.getText();
        if (message.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        } else {
            Filmcoment commentaire = new Filmcoment(message, (Client) new UserService().getUserById(4),
                    new FilmService().getFilm(filmId));
            System.out.println(commentaire + " " + new UserService().getUserById(4));
            FilmcomentService commentaireCinemaService = new FilmcomentService();
            commentaireCinemaService.create(commentaire);
            txtAreaComments.clear();
        }
    }
    /**
     * Adds a new comment to a film and displays all comments for that film.
     * 
     * @param event mouse event that triggered the `AddComment()` function and provides
     * additional information about the event, such as the location of the click or drag.
     */
    @FXML
    void AddComment(MouseEvent event) {
        addCommentaire();
        displayAllComments(filmId);
    }
    /**
     * Makes the `AnchorComments` component visible and displays all comments for a given
     * film ID.
     * 
     * @param event mouse event that triggered the function, providing the necessary
     * information to display the corresponding comments.
     * 
     * 	- `event`: A `MouseEvent` object representing the mouse event that triggered the
     * function.
     */
    @FXML
    void afficherAnchorComment(MouseEvent event) {
        AnchorComments.setVisible(true);
        displayAllComments(filmId);
    }
    /**
     * Creates an HBox containing an ImageView and a VBox with text, image and card
     * container. It adds the HBox to a ScrollPaneComments.
     * 
     * @param commentaire Filmcoment object containing information about a comment made
     * by a user on a film, which is used to display the commenter's name and comment
     * text in the function's output.
     * 
     * 	- `commentaire`: an object of class `Filmcoment`, which contains information about
     * a user's comment on a film.
     * 	- `User_id`: a field in the `Filmcoment` class that represents the user who made
     * the comment.
     * 	- `Photo_de_profil`: a field in the `Filmcoment` class that represents the user's
     * profile picture URL.
     * 
     * @returns a HBox container that displays an image and text related to a comment.
     * 
     * 	- `HBox contentContainer`: This is the primary container for the image and comment
     * card. It has a pref height of 50 pixels and a style of `-fx-background-color:
     * transparent; -fx-padding: 10px`.
     * 	- `imageBox` and `cardContainer`: These are sub-containers within the `contentContainer`.
     * The `imageBox` contains the image of the user, while the `cardContainer` contains
     * the text box with the user's name and comment.
     */
    private HBox addCommentToView(Filmcoment commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);
        // Image de l'utilisateur
        String imageUrl = commentaire.getUser_id().getPhoto_de_profil();
        Image userImage;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(getClass().getResourceAsStream("/Logo.png"));
        }
        ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);
        // Ajouter l'image au cercle
        Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);
        // Création de la boîte pour l'image et la bordure du cercle
        HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);
        // Création du conteneur pour la carte du commentaire
        HBox cardContainer = new HBox();
        cardContainer.setStyle(
                "-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");
        // Nom de l'utilisateur
        Text userName = new Text(
                commentaire.getUser_id().getFirstName() + " " + commentaire.getUser_id().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");
        // Commentaire
        Text commentText = new Text(commentaire.getComment());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique
        // Création de la boîte pour le texte du commentaire
        VBox textBox = new VBox();
        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);
        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);
        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);
        // Ajouter le conteneur principal au ScrollPane
        ScrollPaneComments.setContent(contentContainer);
        return contentContainer;
    }
    /**
     * Retrieves all comments for a given film ID and filters them to only include those
     * that belong to the corresponding cinema.
     * 
     * @param filmId id of the film for which the comments are to be retrieved.
     * 
     * @returns a list of commentaries for a specific cinema, filtered from all comments
     * based on their film ID.
     * 
     * 	- `List<Filmcoment>` is the type of the returned value, indicating that it is a
     * list of `Filmcoment` objects.
     * 	- The variable `cinemaComments` is initialized as an empty list, indicating that
     * no comments have been filtered yet.
     * 	- The function uses a loop to iterate over all the comments in the `allComments`
     * list and checks if the `film_id` of each comment matches the `filmId` parameter
     * passed to the function. If it does, the comment is added to the `cinemaComments`
     * list.
     */
    private List<Filmcoment> getAllComment(int filmId) {
        FilmcomentService commentaireCinemaService = new FilmcomentService();
        List<Filmcoment> allComments = commentaireCinemaService.read(); // Récupérer tous les commentaires
        List<Filmcoment> cinemaComments = new ArrayList<>();
        // Filtrer les commentaires pour ne conserver que ceux du cinéma correspondant
        for (Filmcoment comment : allComments) {
            if (comment.getFilm_id().getId() == filmId) {
                cinemaComments.add(comment);
            }
        }
        return cinemaComments;
    }
    /**
     * Displays all comments associated with a specific film ID in a scroll pane.
     * 
     * @param filmId identifier of the film to display all comments for.
     */
    private void displayAllComments(int filmId) {
        List<Filmcoment> comments = getAllComment(filmId);
        VBox allCommentsContainer = new VBox();
        for (Filmcoment comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }
        ScrollPaneComments.setContent(allCommentsContainer);
    }
    /**
     * Is called when the Java application begins and sets up the Stage for further interaction.
     * 
     * @param stage Stage object that serves as the root of the JavaFX application's event
     * handling and visual representation, and it is used to initialize the application's
     * UI components and layout when the `start()` method is called.
     */
    @Override
    public void start(Stage stage) throws Exception {
    }
}

package com.esprit.controllers.series;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Feedback;
import com.esprit.models.series.Serie;
import com.esprit.models.users.Client;
import com.esprit.services.series.IServiceEpisode;
import com.esprit.services.series.IServiceEpisodeImpl;
import com.esprit.services.series.IServiceFeedbackImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides functionality for uploading and viewing episodes of a series, as
 * well as
 * adding feedback to the series. It also initializes a list of episodes and
 * displays
 * them in a ListView. Additionally, it provides methods for playing, pausing,
 * and
 * stopping media players for each episode.
 */
public class EpisodeClientController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(EpisodeClientController.class.getName());

    private final IServiceEpisode iServiceEpisode = new IServiceEpisodeImpl();
    @FXML
    private Button uploadButton;
    @FXML
    private FontIcon retour;
    @FXML
    private Label uploadSuccessLabel;
    @FXML
    private ListView<Episode> ListEpisode;
    @FXML
    private ImageView imgsrie;
    @FXML
    private MediaView midiavideo;
    @FXML
    private Label nomlbl;
    @FXML
    private Label payslbl;
    @FXML
    private Label resumelbl;
    @FXML
    private Label rirecteurslbl;
    @FXML
    private Button arreterbtn;
    @FXML
    private Button jouerbtn;
    @FXML
    private Button pausebtn;
    @FXML
    private TextArea txtDescriptionFeedBack;
    @FXML
    private Button btnSend;
    private Serie selectedSerie;
    private int idep;
    private List<Episode> episodes = new ArrayList<>();

    /**
     * Sets up the user interface for a media player, initializing buttons and
     * setting
     * listeners for media playback. It also retrieves episode information from a
     * database
     * and displays it in a list view.
     *
     * @param selectedSerie selected series for which the functions initializes the
     *                      image
     *                      and video components.
     *                      <p>
     *                      - `getImage()`: String representing the image file path
     *                      - `getNom()`: String representing the series name
     *                      - `getResume()`: String representing the series summary
     *                      - `getDirecteur()`: String representing the director's
     *                      name
     *                      - `getPays()`: String representing the country of origin
     *                      <p>
     *                      These properties are used to display the series
     *                      information in various parts of
     *                      the user interface.
     */
    public void initialize(final Serie selectedSerie) {
        this.selectedSerie = selectedSerie;
        final double imageWidth = 250; // Largeur fixe souhaitée
        final double imageHeight = 180; // Hauteur fixe souhaitée
        final String img = selectedSerie.getImage();
        final File file = new File(img);
        final Image image = new Image(file.toURI().toString());
        this.imgsrie.setImage(image);
        this.imgsrie.setFitWidth(imageWidth);
        this.imgsrie.setFitHeight(imageHeight);
        this.imgsrie.setPreserveRatio(true);
        this.nomlbl.setText(selectedSerie.getNom());
        this.resumelbl.setText(selectedSerie.getResume());
        this.rirecteurslbl.setText(selectedSerie.getDirecteur());
        this.payslbl.setText(selectedSerie.getPays());
        try {
            this.episodes = this.iServiceEpisode.recupuerselonSerie(selectedSerie.getIdserie());
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        this.ListEpisode.getItems().addAll(this.episodes);
        this.ListEpisode.setCellFactory(param -> new ListCell<Episode>() {
            /**
             * Updates an episode object's text and image based on whether the object is
             * empty
             * or not, and sets the style of the text to bold Arial font with a specific
             * size and
             * weight.
             *
             * @param item  episode object being updated, which contains information such as
             *              title,
             *              number, season, and image path, that is used to set the text and
             *              graphic properties
             *              of the `ImageView`.
             *
             *              - `item`: The episode object containing information such as
             *              title, number, season,
             *              and image.
             *              - `empty`: A boolean indicating whether the `item` is empty or
             *              not.
             *              - `img`: The image associated with the `item`, represented as a
             *              string.
             *
             * @param empty emptiness of the `Episode` object, and determines whether to set
             *              the
             *              `text` property to null or not.
             */
            @Override
            protected void updateItem(final Episode item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setText(null);
                } else {
                    final double imageWidth = 50; // Largeur fixe souhaitée
                    final double imageHeight = 90; // Hauteur fixe souhaitée
                    final String img = item.getImage();
                    final File file = new File(img);
                    final Image image = new Image(file.toURI().toString());
                    final ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(imageWidth);
                    imageView.setFitHeight(imageHeight);
                    imageView.setPreserveRatio(true);
                    this.setText("\n   Title :" + item.getTitre() + "\n  Number: " + item.getNumeroepisode()
                            + "\n   Season : " + item.getSaison());
                    this.setStyle("-fx-font-size: 14; -fx-font-family: 'Arial'; -fx-font-weight: bold;"); // Police en
                                                                                                          // gras
                    this.setGraphic(imageView);
                    EpisodeClientController.this.idep = item.getIdepisode();
                }
            }
        });
        this.ListEpisode.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (0 <= newValue.intValue()) {
                final Episode selectedepisode = this.ListEpisode.getItems().get(newValue.intValue());
                final String vd = selectedepisode.getVideo();
                final File file1 = new File(vd);
                final Media video = new Media(file1.toURI().toString());
                final MediaPlayer m1 = new MediaPlayer(video);
                this.midiavideo.setMediaPlayer(m1);
                final MediaPlayer mediaPlayer = new MediaPlayer(video);
                this.midiavideo.setMediaPlayer(mediaPlayer);
                this.jouerbtn.setOnAction(event -> mediaPlayer.play());
                this.pausebtn.setOnAction(event -> mediaPlayer.pause());
                this.arreterbtn.setOnAction(event -> mediaPlayer.stop());
            }
        });
    }

    /**
     * Is called when an instance of a class is created and initializes an object's
     * resources, such as loading data from a URL or database, by calling the
     * appropriate
     * methods.
     *
     * @param url            URL of the web application being initialized.
     * @param resourceBundle resource bundle that contains localized data for the
     *                       component
     *                       being initialized.
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
    }

    /**
     * Takes a `txtDescriptionFeedBack` text input and adds it to an instance of
     * `IServiceFeedbackImpl`. The date is calculated using the `LocalDate.now()`
     * method,
     * and the `idep` parameter is included in the feedback object.
     *
     * @param event user's action of clicking the "Add Feedback" button and triggers
     *              the
     *              execution of the function.
     *              <p>
     *              - `txtDescriptionFeedBack`: The text field where the user has
     *              entered the feedback
     *              description.
     */
    @FXML
    void ajouterFeedBack(final ActionEvent event) {
        final String description = this.txtDescriptionFeedBack.getText();
        Date date = null;
        try {
            final LocalDate currentDate = LocalDate.now();
            final ZonedDateTime zonedDateTime = currentDate.atStartOfDay(ZoneId.systemDefault());
            final Instant instant = zonedDateTime.toInstant();
            date = Date.from(instant);
        } catch (final Exception e) {
            EpisodeClientController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        final IServiceFeedbackImpl sf = new IServiceFeedbackImpl();
        final Client client = (Client) this.txtDescriptionFeedBack.getScene().getWindow().getUserData();
        sf.ajouter(new Feedback(client.getId(), description, date, this.idep));
        this.txtDescriptionFeedBack.clear();
    }

    /**
     * Loads a FXML file, creates a scene, and displays it on a stage, when a mouse
     * event
     * occurs.
     *
     * @param event mouse event that triggered the function execution, providing
     *              information
     *              about the location and type of the event on the user interface.
     *              <p>
     *              - `event`: A `javafx.scene.input.MouseEvent` object representing
     *              the mouse event
     *              that triggered the function.
     */
    @FXML
    public void afficherserie(final MouseEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/SeriesClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

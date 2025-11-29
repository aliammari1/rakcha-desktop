package com.esprit.controllers.series;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Feedback;
import com.esprit.models.series.Series;
import com.esprit.models.users.Client;
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

/**
 * Provides functionality for uploading and viewing episodes of a series, as
 * well as adding feedback to the series. It also initializes a list of episodes
 * and displays them in a ListView. Additionally, it provides methods for
 * playing, pausing, and stopping media players for each episode.
 */
public class EpisodeClientController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(EpisodeClientController.class.getName());

    private final IServiceEpisodeImpl iServiceEpisode = new IServiceEpisodeImpl();
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
    private Series selectedSerie;
    private Long idep;
    private List<Episode> episodes = new ArrayList<>();

    /**
     * Populate the controller UI with the provided series and configure episode listing and playback controls.
     *
     * Sets the series image and metadata, loads the series' episodes into the ListView using custom cells,
     * and wires the media playback buttons to control the selected episode's media player.
     *
     * @param selectedSerie the series whose details and episodes should populate the UI
     * @throws RuntimeException if retrieving episodes for the series fails
     */
    public void initialize(final Series selectedSerie) {
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
        this.nomlbl.setText(selectedSerie.getName());
        this.resumelbl.setText(selectedSerie.getSummary());
        this.rirecteurslbl.setText(selectedSerie.getDirector());
        this.payslbl.setText(selectedSerie.getCountry());
        try {
            this.episodes = this.iServiceEpisode.retrieveBySeries(selectedSerie.getId());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        this.ListEpisode.getItems().addAll(this.episodes);
        this.ListEpisode.setCellFactory(param -> new ListCell<Episode>() {
            /**
             * Configure the ListCell display for an Episode, showing a thumbnail and a formatted
             * summary with title, episode number, and season.
             *
             * When `item` is null or `empty` is true, the cell is cleared. Otherwise the cell's
             * graphic is set to the episode thumbnail, the text is set to a short multi-line
             * summary, the text style is set to bold Arial (14px), and the controller's `idep`
             * field is updated with the episode's id.
             *
             * @param item  the Episode to render in this cell
             * @param empty true if this cell should be empty and display no content
             */
            @Override
            protected void updateItem(final Episode item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setText(null);
                }
 else {
                    final double imageWidth = 50; // Largeur fixe souhaitée
                    final double imageHeight = 90; // Hauteur fixe souhaitée
                    final String img = item.getImage();
                    final File file = new File(img);
                    final Image image = new Image(file.toURI().toString());
                    final ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(imageWidth);
                    imageView.setFitHeight(imageHeight);
                    imageView.setPreserveRatio(true);
                    this.setText("\n   Title :" + item.getTitle() + "\n  Number: " + item.getEpisodeNumber()
                            + "\n   Season : " + item.getSeason());
                    this.setStyle("-fx-font-size: 14; -fx-font-family: 'Arial'; -fx-font-weight: bold;"); // Police en
                                                                                                          // gras
                    this.setGraphic(imageView);
                    idep = item.getId();
                }

            }

        }
);
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

        }
);
    }


    /**
     * Perform controller initialization after the FXML is loaded.
     * This implementation performs no initialization.
     *
     * @param url            location used to resolve relative paths for the root object, may be null
     * @param resourceBundle resources for localized strings, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
    }


    /**
     * Create and persist a Feedback for the selected episode using the feedback text field.
     *
     * The Feedback will use the current window user as the client, today's date as the feedback date,
     * the text from {@code txtDescriptionFeedBack} as the description, and {@code idep} as the episode id.
     * After persisting the feedback the feedback input field is cleared.
     *
     * @param event the ActionEvent triggered by the "Add Feedback" button
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
        sf.create(new Feedback(client.getId(), description, date, this.idep));
        this.txtDescriptionFeedBack.clear();
    }


    /**
     * Open the SeriesClient view in the window that originated the mouse event.
     *
     * @param event the MouseEvent whose source Node is used to obtain the current Stage
     * @throws IOException if the FXML resource for the SeriesClient view cannot be loaded
     */
    @FXML
    /**
     * Performs afficherserie operation.
     *
     * @return the result of the operation
     */
    public void afficherserie(final MouseEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/SeriesClient.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
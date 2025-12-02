package com.esprit.controllers.series;

import com.esprit.models.common.Review;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Series;
import com.esprit.models.users.Client;
import com.esprit.services.common.ReviewService;
import com.esprit.services.series.EpisodeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides functionality for uploading and viewing episodes of a series, as
 * well as adding feedback to the series. It also initializes a list of episodes
 * and displays them in a ListView. Additionally, it provides methods for
 * playing, pausing, and stopping media players for each episode.
 */
public class EpisodeClientController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(EpisodeClientController.class.getName());

    private final EpisodeService iServiceEpisode = new EpisodeService();
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
     * Sets up the user interface for a media player, initializing buttons and
     * setting listeners for media playback. It also retrieves episode information
     * from a database and displays it in a list view.
     *
     * @param selectedSerie selected series for which the functions initializes the
     *                      image and video components.
     */
    public void initialize(final Series selectedSerie) {
        // Issue #12: Add null check for series before accessing properties
        if (selectedSerie == null) {
            EpisodeClientController.LOGGER.severe("Selected series is null");
            return;
        }

        this.selectedSerie = selectedSerie;
        final double imageWidth = 250; // Largeur fixe souhaitée
        final double imageHeight = 180; // Hauteur fixe souhaitée

        // Issue #12: Add null check before accessing image
        if (selectedSerie.getImageUrl() != null) {
            try {
                final String img = selectedSerie.getImageUrl();
                final File file = new File(img);
                final Image image = new Image(file.toURI().toString());
                this.imgsrie.setImage(image);
                this.imgsrie.setFitWidth(imageWidth);
                this.imgsrie.setFitHeight(imageHeight);
                this.imgsrie.setPreserveRatio(true);
            } catch (Exception e) {
                EpisodeClientController.LOGGER.warning("Failed to load series image: " + e.getMessage());
            }
        } else {
            EpisodeClientController.LOGGER.warning("Series image is null");
        }

        // Issue #12: Add null checks before setting text labels
        this.nomlbl.setText(selectedSerie.getName() != null ? selectedSerie.getName() : "N/A");
        this.resumelbl.setText(selectedSerie.getSummary() != null ? selectedSerie.getSummary() : "N/A");
        this.rirecteurslbl.setText(selectedSerie.getDirector() != null ? selectedSerie.getDirector() : "N/A");
        this.payslbl.setText(selectedSerie.getCountry() != null ? selectedSerie.getCountry() : "N/A");

        try {
            this.episodes = this.iServiceEpisode.retrieveBySeries(selectedSerie.getId());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        if (this.episodes != null) {
            this.ListEpisode.getItems().addAll(this.episodes);
        }

        this.ListEpisode.setCellFactory(param -> new ListCell<Episode>() {
            @Override
            protected void updateItem(final Episode item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    this.setText(null);
                    this.setGraphic(null);
                } else {
                    final double imageWidth = 50; // Largeur fixe souhaitée
                    final double imageHeight = 90; // Hauteur fixe souhaitée

                    // Add null check for episode image
                    if (item.getImageUrl() != null) {
                        try {
                            final String img = item.getImageUrl();
                            final File file = new File(img);
                            final Image image = new Image(file.toURI().toString());
                            final ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(imageWidth);
                            imageView.setFitHeight(imageHeight);
                            imageView.setPreserveRatio(true);
                            this.setGraphic(imageView);
                        } catch (Exception e) {
                            // Ignore image load error
                        }
                    }

                    this.setText("\n   Title :" + item.getTitle() + "\n  Number: " + item.getEpisodeNumber()
                        + "\n   Season : " + item.getSeasonId());
                    this.setStyle("-fx-font-size: 14; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
                    idep = item.getId();
                }
            }
        });

        this.ListEpisode.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && 0 <= newValue.intValue() && newValue.intValue() < ListEpisode.getItems().size()) {
                final Episode selectedepisode = this.ListEpisode.getItems().get(newValue.intValue());
                final String vd = selectedepisode.getVideoUrl();
                if (vd != null) {
                    try {
                        final File file1 = new File(vd);
                        final Media video = new Media(file1.toURI().toString());
                        final MediaPlayer m1 = new MediaPlayer(video);
                        this.midiavideo.setMediaPlayer(m1);
                        final MediaPlayer mediaPlayer = new MediaPlayer(video);
                        this.midiavideo.setMediaPlayer(mediaPlayer);
                        this.jouerbtn.setOnAction(event -> mediaPlayer.play());
                        this.pausebtn.setOnAction(event -> mediaPlayer.pause());
                        this.arreterbtn.setOnAction(event -> mediaPlayer.stop());
                    } catch (Exception e) {
                        EpisodeClientController.LOGGER.warning("Failed to load video: " + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
    }

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
        final ReviewService sf = new ReviewService();
        final Client client = (Client) this.txtDescriptionFeedBack.getScene().getWindow().getUserData();
        if (client != null) {
            sf.create(new Review(client, this.selectedSerie, description));
        }
        this.txtDescriptionFeedBack.clear();
    }

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

package com.esprit.controllers.series;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Series;
import com.esprit.services.series.IServiceEpisodeImpl;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class EpisodeController {
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private static final Logger LOGGER = Logger.getLogger(EpisodeController.class.getName());
    @FXML
    public ImageView episodeImageView;
    @FXML
    private Label numbercheck;
    @FXML
    private Label picturechek;
    @FXML
    private Label seasoncheck;
    @FXML
    private Label seriecheck;
    @FXML
    private Label titrecheck;
    @FXML
    private Label videocheck;
    @FXML
    private TextField titreF;
    @FXML
    private TextField numeroepisodeF;
    @FXML
    private TextField saisonF;
    @FXML
    private ComboBox<String> serieF;
    private String imgpath;
    private String videopath;
    private String cloudinaryImageUrl;
    private List<Series> serieList;
    @FXML
    private TableView<Episode> tableView;

    /**
     * Clears existing data in the `tableView`, then retrieves new data from an API
     * and displays it in the table with buttons for deleting and editing each
     * episode.
     */
    private void ref() {
        this.tableView.getItems().clear();
        this.tableView.getColumns().clear();
        this.serieF.getItems().clear();
        this.titreF.setText("");
        this.numeroepisodeF.setText("");
        this.saisonF.setText("");
        this.imgpath = "";
        this.videopath = "";
        final IServiceEpisodeImpl iServiceEpisode = new IServiceEpisodeImpl();
        final IServiceSeriesImpl iServiceSerie = new IServiceSeriesImpl();
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            this.serieList = iServiceSerie.read(pageRequest).getContent();
            for (final Series s : this.serieList) {
                this.serieF.getItems().add(s.getName());
            }

        }
 catch (final Exception e) {
            throw new RuntimeException(e);
        }

        ///// affichage du tableau
        final IServiceSeriesImpl serviceSerie = new IServiceSeriesImpl();
        // TableColumn<EpisodeDto, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idepisode"));
        final TableColumn<Episode, String> titreCol = new TableColumn<>("Title");
        titreCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        final TableColumn<Episode, String> numeroepisodeCol = new TableColumn<>("Number");
        numeroepisodeCol.setCellValueFactory(new PropertyValueFactory<>("episodeNumber"));
        final TableColumn<Episode, String> saisonCol = new TableColumn<>("Season");
        saisonCol.setCellValueFactory(new PropertyValueFactory<>("season"));
        final TableColumn<Episode, String> serieCol = new TableColumn<>("Serie");
        serieCol.setCellValueFactory(new PropertyValueFactory<>("seriesName"));
        final TableColumn<Episode, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Delete");

            {
                this.button.setOnAction(event -> {
                    final Episode episode = this.getTableView().getItems().get(this.getIndex());
                    try {
                        iServiceEpisode.delete(episode);
                        tableView.getItems().remove(episode);
                        showAlert("OK", "Deleted successfully !");
                        tableView.refresh();
                    }
 catch (final Exception e) {
                        EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        showAlert("Error", e.getMessage());
                    }

                }
);
            }


            /**
             * Sets the graphic of this cell based on its empty state.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        final TableColumn<Episode, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount;

            {
                this.button.setOnAction(event -> {
                    this.clickCount++;
                    if (2 == clickCount) {
                        final Episode episode = this.getTableView().getItems().get(this.getIndex());
                        modifierEpisode(episode);
                        tableView.refresh();
                        this.clickCount = 0;
                    }

                }
);
            }


            /**
             * Sets the graphic of this cell based on its empty state.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        // tableView.getColumns().addAll(idCol, titreCol, numeroepisodeCol, saisonCol,
        // serieCol, supprimerCol, modifierCol);
        this.tableView.getColumns().addAll(titreCol, numeroepisodeCol, saisonCol, serieCol, supprimerCol, modifierCol);
        // Récupérer les catégories et les ajouter à la TableView
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            this.tableView.getItems().addAll(iServiceEpisode.read(pageRequest).getContent());
        }
 catch (final Exception e) {
            EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Open an edit dialog prefilled from the given episode, allow the user to modify
     * title, episode number, season, image, video, and series, and persist the changes.
     *
     * The dialog is populated with the episode's current values; when the user
     * confirms, the episode is updated via the episode service, the view is
     * refreshed, and a success alert is shown.
     *
     * @param episode the Episode whose values will be edited and persisted
     */
    private void modifierEpisode(final Episode episode) {
        final IServiceEpisodeImpl iServiceEpisode = new IServiceEpisodeImpl();
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Episode ");
        this.imgpath = episode.getImage();
        this.videopath = episode.getVideo();
        final TextField titreFild = new TextField(episode.getTitle());
        final TextField numeroepisodeFild = new TextField(String.valueOf(episode.getEpisodeNumber()));
        final TextField saisonFild = new TextField(String.valueOf(episode.getSeason()));
        final ComboBox<String> serieComboBox = new ComboBox<>();
        for (final Series s : this.serieList) {
            serieComboBox.getItems().add(s.getName());
        }

        serieComboBox.setValue(episode.getSeries().getName());
        final Button Ajouterimage = new Button("Add");
        {
            Ajouterimage.setOnAction(event -> {
                this.addimg(event);
            }
);
        }

        final Button AJouterVideo = new Button("Add");
        {
            AJouterVideo.setOnAction(event -> {
                this.addVideo(event);
            }
);
        }

        dialog.getDialogPane()
                .setContent(new VBox(10, new Label("Title:"), titreFild, new Label("Number:"), numeroepisodeFild,
                        new Label("Season :"), saisonFild, new Label("Add picture :"), Ajouterimage,
                        new Label("Ajouer Video :"), AJouterVideo, serieComboBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(titreFild.getText(), numeroepisodeFild.getText());
            }

            return null;
        }
);
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        final Episode episode1 = new Episode();
        result.ifPresent(pair -> {
            episode1.setId(episode.getId());
            episode1.setTitle(titreFild.getText());
            episode1.setEpisodeNumber(Integer.parseInt(numeroepisodeFild.getText()));
            episode1.setSeason(Integer.parseInt(saisonFild.getText()));
            episode1.setImage(this.imgpath);
            episode1.setVideo(this.videopath);
            for (final Series s : this.serieList) {
                if (s.getName().equals(serieComboBox.getValue())) {
                    episode1.setId(s.getId());
                }

            }

            try {
                EpisodeController.LOGGER.info(episode.toString());
                iServiceEpisode.update(episode);
                this.showAlert("Succes", "Modified successfully !");
                this.ref();
            }
 catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
);
    }


    /**
     * Initializes the controller state and populates UI components (table, form fields, and series list).
     */
    @FXML
    private void initialize() {
        this.ref();
    }


    /**
     * Display an informational alert dialog with the given title and message.
     *
     * @param title   the dialog window title
     * @param message the message text shown in the dialog content area
     */
    @FXML
    private void showAlert(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
         * Opens a file chooser restricted to PNG/JPG/GIF images, stores the selected file's normalized path in `imgpath`,
         * and loads the image.
         *
         * <p>If the user cancels or selects a non-image file, no path is stored and an informational log entry is written.</p>
         */
    @FXML
    void addimg(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a picture");
        // Set file extension filter to only allow image files
        final FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",
                "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        final File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (null != selectedFile && this.isImageFile(selectedFile)) {
            this.imgpath = selectedFile.getAbsolutePath().replace("\\", "/");
            EpisodeController.LOGGER.info("File path stored: " + this.imgpath);
            final Image image = new Image(selectedFile.toURI().toString());
            // imgoeuvre.setImage(image);
        }
 else {
            EpisodeController.LOGGER.info("Please select a valid image file.");
        }

    }


    /**
     * Prompt the user to choose a PNG or JPG file, upload the selected image to Cloudinary, and show it in the episode ImageView.
     *
     * <p>If an image is selected and uploaded successfully, the ImageView is updated and the controller's image path is set to the Cloudinary URL; upload failures are logged.</p>
     *
     * @param event the JavaFX action event that triggered the import (typically a button click)
     */
    @FXML
    void importImage(final ActionEvent event) {
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
                this.episodeImageView.setImage(selectedImage);
                this.imgpath = cloudinaryImageUrl;

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            }
 catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }

        }

    }


    // Method to retrieve the stored file path

    /**
     * Get the stored image file path.
     *
     * @return the stored image file path, or {@code null} if no path is set
     */
    public String getFilePath() {
        return this.imgpath;
    }


    // Method to check if the selected file is an image file

    /**
     * Determines whether the given file can be loaded as a JavaFX image.
     *
     * @param file the file to test
     * @return `true` if the file can be loaded as a JavaFX Image, `false` otherwise
     */
    private boolean isImageFile(final File file) {
        try {
            final Image image = new Image(file.toURI().toString());
            return !image.isError();
        }
 catch (final Exception e) {
            return false;
        }

    }


    /////

    /**
         * Opens a file chooser to select a video file and, if valid, stores its normalized absolute path in {@code videopath}.
         *
         * If the selected file has a supported extension (mp4, avi, mkv) its path is saved; otherwise an informational message is logged.
         *
         * @param event the ActionEvent that triggered the file chooser
         */
    @FXML
    void addVideo(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a video");
        // Set file extension filter to only allow video files
        final FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi",
                "*.mkv");
        fileChooser.getExtensionFilters().add(videoFilter);
        final File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (null != selectedFile && this.isVideoFile(selectedFile)) {
            final String videoPath = selectedFile.getAbsolutePath().replace("\\", "/");
            this.videopath = videoPath;
            EpisodeController.LOGGER.info("File path stored: " + videoPath);
        }
 else {
            EpisodeController.LOGGER.info("Please select a valid video file.");
        }

    }


    // Method to check if the selected file is a video file

    /**
     * Checks whether the file is a video based on its file-name extension.
     * Recognizes `mp4`, `avi`, and `mkv` extensions (case-insensitive).
     *
     * @return `true` if the file extension is `mp4`, `avi`, or `mkv`, `false` otherwise.
     */
    private boolean isVideoFile(final File file) {
        final String fileName = file.getName();
        final String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return "mp4".equals(extension) || "avi".equals(extension) || "mkv".equals(extension);
    }


    /////

    /**
     * Determines whether the provided string represents a valid integer.
     *
     * @param s the string to check; may be null
     * @return true if s can be parsed as an integer, false otherwise
     */
    boolean isStringInt(final String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
 catch (final NumberFormatException e) {
            return false;
        }

    }


    /**
     * Validate that the title field contains text.
     *
     * If the title field is empty, sets the validation label to "Please enter a valid Title".
     *
     * @return `true` if the title field contains non-empty text, `false` otherwise.
     */
    boolean titrecheck() {
        if ("" != titreF.getText()) {
            return true;
        }
 else {
            this.titrecheck.setText("Please enter a valid Title");
            return false;
        }

    }


    /**
     * Validate that the season text field contains an integer value.
     *
     * If the field is empty or not an integer, sets the `seasoncheck` label to
     * "Please enter a valid Season".
     *
     * @return `true` if the season field contains a parsable integer, `false` otherwise.
     */
    boolean seasoncheck() {
        final String numero = this.saisonF.getText();
        if (!numero.isEmpty() && this.isStringInt(numero)) {
            return true;
        }
 else {
            this.seasoncheck.setText("Please enter a valid Season");
            return false;
        }

    }


    /**
     * Validate that an image path has been selected.
     *
     * If no image path is set, updates the `picturechek` label with "Please select a Picture".
     *
     * @return `true` if an image path is present, `false` otherwise.
     */
    boolean picturechek() {
        if ("" != imgpath) {
            return true;
        }
 else {
            this.picturechek.setText("Please select a Picture");
            return false;
        }

    }


    /**
     * Validate that the episode number field contains a non-empty integer.
     *
     * If validation fails, sets the `numbercheck` label to "Please enter a Number ".
     *
     * @return `true` if the episode number field contains an integer, `false` otherwise.
     */
    boolean numbercheck() {
        final String numero = this.numeroepisodeF.getText();
        if (!numero.isEmpty() && this.isStringInt(numero)) {
            return true;
        }
 else {
            this.numbercheck.setText("Please enter a Number ");
            return false;
        }

    }


    /**
     * Validates that a video path has been provided.
     *
     * If no video path is set, updates the `videocheck` label with an error message.
     *
     * @return `true` if a video path is present, `false` otherwise.
     */
    boolean videocheck() {
        if ("" != videopath) {
            return true;
        }
 else {
            this.videocheck.setText("Please enter a valid Summary");
            return false;
        }

    }


    /**
     * Validate that a series is selected in the series ComboBox.
     *
     * If no series is selected, sets the `seriecheck` label text to "Please select a Serie".
     *
     * @return true if a series is selected, false otherwise.
     */
    boolean seriecheck() {
        if (null != serieF.getValue()) {
            return true;
        }
 else {
            this.seriecheck.setText("Please select a Serie");
            return false;
        }

    }


    // Méthode pour envoyer un SMS avec Twilio

    /**
     * Send an SMS message to the specified phone number.
     *
     * @param recipientNumber the recipient's phone number as a dialable string
     * @param messageBody     the text content to send in the SMS
     */
    private void sendSMS(final String recipientNumber, final String messageBody) {
        final PhoneNumber fromPhoneNumber = new PhoneNumber("+17573640849");
        final PhoneNumber toPhoneNumber = new PhoneNumber(recipientNumber);
        final Message message = Message.creator(toPhoneNumber, fromPhoneNumber, messageBody).create();
        EpisodeController.LOGGER.info("SMS sent successfully: " + message.getSid());
    }


    /**
     * Creates a new Episode from the current form values, persists it, and notifies a configured phone number via SMS.
     *
     * Performs form validation before saving and refreshes the view on success.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void ajouterSerie(final ActionEvent event) {
        Twilio.init(EpisodeController.ACCOUNT_SID, EpisodeController.AUTH_TOKEN);
        final IServiceEpisodeImpl episodeserv = new IServiceEpisodeImpl();
        final Episode episode = new Episode();
        this.titrecheck();
        this.numbercheck();
        this.seasoncheck();
        this.videocheck();
        this.seriecheck();
        if (this.titrecheck() && this.numbercheck() && this.seasoncheck() && this.videocheck() && this.seriecheck()) {
            try {
                final String fullPath = this.episodeImageView.getImage().getUrl();
                final String requiredPath = fullPath.substring(fullPath.indexOf("/img/series/"));
                final URI uri = new URI(requiredPath);
                episode.setTitle(this.titreF.getText());
                episode.setEpisodeNumber(Integer.parseInt(this.numeroepisodeF.getText()));
                episode.setSeason(Integer.parseInt(this.saisonF.getText()));
                episode.setImage(uri.getPath());
                episode.setVideo(this.videopath);
                this.titrecheck.setText("");
                this.numbercheck.setText("");
                this.seasoncheck.setText("");
                this.picturechek.setText("");
                this.videocheck.setText("");
                this.seriecheck.setText("");
                for (final Series s : this.serieList) {
                    if (s.getName() == this.serieF.getValue()) {
                        episode.setId(s.getId());
                    }

                }

                episodeserv.create(episode);
                // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                // String message = "A new episode is here : " + episode.getNameSerie();
                // sendSMS("+21653775010", message);
                for (final Series s : this.serieList) {
                    if (Objects.equals(s.getName(), this.serieF.getValue())) {
                        episode.setId(s.getId());
                        // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                        final String message = " Episode " + episode.getEpisodeNumber() + " Season "
                                + episode.getSeason() + "from your series : " + s.getName() + " is now available!";
                        this.sendSMS("+21653775010", message);
                        break; // Sortir de la boucle une fois la série trouvée
                    }

                }

                this.tableView.refresh();
                this.ref();
            }
 catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the episode. : " + e.getMessage());
                EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

    }


    /**
     * Navigates to and displays the Categorie view.
     *
     * Loads the Categorie-view FXML, creates a new scene from it, and replaces the current window's scene.
     *
     * @param event the ActionEvent that triggered this navigation
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void Ocategories(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Categorie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Displays the series view by loading its FXML and setting it as the current window's scene.
     *
     * @param event the action event whose source node provides the current window
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Serie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Opens the episode view UI and replaces the current window's scene with the
     * content from /ui/series/Episode-view.fxml.
     *
     * @param event the action event whose source window will be updated to the new scene
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Episode-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Placeholder event handler invoked when the UI action to show movies is triggered.
     * Currently has no implementation and is reserved for future handling (e.g., navigation
     * to a movies view or loading movie data).
     *
     * @param actionEvent the event that triggered this handler
     */
    public void showmovies(final ActionEvent actionEvent) {
    }


    /**
     * Placeholder event handler for showing products; currently unimplemented.
     *
     * @param actionEvent the UI event that triggered this handler
     */
    public void showproducts(final ActionEvent actionEvent) {
    }


    /**
     * Handler invoked when the user requests the cinema view.
     *
     * @param actionEvent the event that triggered this handler
     */
    public void showcinema(final ActionEvent actionEvent) {
    }


    /**
     * Placeholder handler for the Events UI action; currently performs no operation.
     *
     * Invoked when the associated UI control is activated; retained for future implementation.
     *
     * @param actionEvent the ActionEvent generated by the UI control that invoked this handler
     */
    public void showevent(final ActionEvent actionEvent) {
    }


    /**
     * Switches the UI to the Series view.
     *
     * @param actionEvent the ActionEvent that triggered this handler
     */
    public void showseries(final ActionEvent actionEvent) {
    }

}

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
        } catch (final Exception e) {
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
                    } catch (final Exception e) {
                        EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        showAlert("Error", e.getMessage());
                    }
                });
            }

            /**
             * Updates an item's graphical representation based on its emptiness status.
             *
             * @param item
             *              component being updated, which can be either null or a `Button`
             *              object when the `empty` parameter is false.
             *
             * @param empty
             *              ether value of the item being updated, and determines whether or
             *              not the button's graphic should be set to `null` or the
             *              `button`.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
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
                });
            }

            /**
             * Updates the graphical representation (graphic) associated with an item based
             * on its status as empty or not.
             *
             * @param item
             *              Void item being updated, which is passed to the superclass's
             *              `updateItem` method and then used to set the graphic of the
             *              button
             *              in the function.
             *
             * @param empty
             *              state of the item being updated, and sets the graphic of the
             *              item
             *              accordingly when it is false.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
        // tableView.getColumns().addAll(idCol, titreCol, numeroepisodeCol, saisonCol,
        // serieCol, supprimerCol, modifierCol);
        this.tableView.getColumns().addAll(titreCol, numeroepisodeCol, saisonCol, serieCol, supprimerCol, modifierCol);
        // Récupérer les catégories et les ajouter à la TableView
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            this.tableView.getItems().addAll(iServiceEpisode.read(pageRequest).getContent());
        } catch (final Exception e) {
            EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * /** Modifies an episode's details through a dialog box, including title,
     * number, season, image, and video, and then updates the episode in the
     * database using an IoC container.
     *
     * @param episodeDto
     *                   data of an episode to be edited, containing information
     *                   such as
     *                   title, number, season, image, and video path.
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
            });
        }
        final Button AJouterVideo = new Button("Add");
        {
            AJouterVideo.setOnAction(event -> {
                this.addVideo(event);
            });
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
        });
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
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * References a provided reference.
     */
    @FXML
    private void initialize() {
        this.ref();
    }

    /**
     * /** Creates an `Alert` object and sets its title, header text, and content
     * text using the input parameters. The `Alert.AlertType.INFORMATION` is set to
     * indicate that the alert should be displayed in a neutral manner. Finally, the
     * `alert.showAndWait()` method displays the alert and waits for the user to
     * close it.
     *
     * @param title
     *                title of an Alert that will be displayed to the user when the
     *                function is called.
     * @param message
     *                content text to be displayed within an alert box when the
     *                function
     *                is called.
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
     * Allows users to select an image file using a FileChooser, stores the file
     * path in `imgpath`, and sets the image using `Image`.
     *
     * @param event
     *              action that triggered the function, specifically the opening of
     *              a
     *              file using the FileChooser.
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
        } else {
            EpisodeController.LOGGER.info("Please select a valid image file.");
        }
    }

    /**
     * Allows the user to select an image file from a chosen directory, saves it to
     * two different locations, and displays the image in an `ImageView`.
     *
     * @param event
     *              trigger that initiates the action of importing an image when
     *              clicked by the user.
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
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }
        }
    }

    // Method to retrieve the stored file path

    /**
     * Retrieves and returns the file path of an image.
     *
     * @returns a string representing the path to an image file.
     */
    public String getFilePath() {
        return this.imgpath;
    }

    // Method to check if the selected file is an image file

    /**
     * Takes a `File` object as input and determines if it represents an image file
     * or not. It does this by creating an `Image` object from the file's URI, then
     * checking if the resulting `Image` is not in error. If the image is in error,
     * the function returns `false`.
     *
     * @param file
     *             file to be checked for being an image file.
     * @returns a boolean value indicating whether the provided file is an image
     *          file or not.
     */
    private boolean isImageFile(final File file) {
        try {
            final Image image = new Image(file.toURI().toString());
            return !image.isError();
        } catch (final Exception e) {
            return false;
        }
    }

    /////

    /**
     * Enables the user to select a video file from their computer, and if a valid
     * video file is selected, it stores the file path in a variable called
     * `videopath`.
     *
     * @param event
     *              occurance of a user clicking on the "Choose a video" button and
     *              triggers the execution of the function.
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
        } else {
            EpisodeController.LOGGER.info("Please select a valid video file.");
        }
    }

    // Method to check if the selected file is a video file

    /**
     * Determines if a given File is a video file based on its file name extension,
     * returning `true` if the extension matches "mp4", "avi", or "mkv", and `false`
     * otherwise.
     *
     * @param file
     *             File that needs to be checked for being a video file.
     * @returns a boolean value indicating whether the provided file is an MP4, AVI,
     *          or MKV video file.
     */
    private boolean isVideoFile(final File file) {
        final String fileName = file.getName();
        final String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return "mp4".equals(extension) || "avi".equals(extension) || "mkv".equals(extension);
    }

    /////

    /**
     * Checks if a given string can be converted to an integer using
     * `Integer.parseInt()`. If it can, it returns `true`, otherwise it returns
     * `false`.
     *
     * @param s
     *          String to be parsed as an integer.
     * @returns a boolean value indicating whether the given string can be parsed as
     *          an integer.
     */
    boolean isStringInt(final String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    /**
     * Determines whether a title is provided and returns `true` if it is, else it
     * displays an error message and returns `false`.
     *
     * @returns `true` if a title is provided, otherwise it returns `false` and
     *          provides an error message.
     */
    boolean titrecheck() {
        if ("" != titreF.getText()) {
            return true;
        } else {
            this.titrecheck.setText("Please enter a valid Title");
            return false;
        }
    }

    /**
     * Verifies if the user inputted season value is not empty and it's a numerical
     * string, if both conditions are true, it returns `true`, otherwise it displays
     * an error message and returns `false`.
     *
     * @returns `true` if the input string is not empty and can be converted to an
     *          integer, otherwise it returns `false`.
     */
    boolean seasoncheck() {
        final String numero = this.saisonF.getText();
        if (!numero.isEmpty() && this.isStringInt(numero)) {
            return true;
        } else {
            this.seasoncheck.setText("Please enter a valid Season");
            return false;
        }
    }

    /**
     * Checks if an image file path is provided and returns `true` if yes, otherwise
     * returns `false`.
     *
     * @returns a boolean value indicating whether a picture has been selected or
     *          not.
     */
    boolean picturechek() {
        if ("" != imgpath) {
            return true;
        } else {
            this.picturechek.setText("Please select a Picture");
            return false;
        }
    }

    /**
     * Checks if the user's input is a non-empty, integer-valued string, and returns
     * `true` if it is, else returns `false`.
     *
     * @returns "Please enter a Number".
     */
    boolean numbercheck() {
        final String numero = this.numeroepisodeF.getText();
        if (!numero.isEmpty() && this.isStringInt(numero)) {
            return true;
        } else {
            this.numbercheck.setText("Please enter a Number ");
            return false;
        }
    }

    /**
     * Verifies if a video summary is entered by the user, and returns `true` if it
     * is valid, or `false` otherwise, with an appropriate error message displayed
     * on the UI if it's invalid.
     *
     * @returns a boolean value indicating whether a valid video path has been
     *          provided.
     */
    boolean videocheck() {
        if ("" != videopath) {
            return true;
        } else {
            this.videocheck.setText("Please enter a valid Summary");
            return false;
        }
    }

    /**
     * Checks if the value of `serieF` is not null, then returns `true`. Otherwise,
     * it sets the text of a text field called `seriecheck` to "Please select a
     * Serie" and returns `false`.
     *
     * @returns `true` if a value is provided for `serieF.getValue()`, otherwise it
     *          returns `false` with an error message indicating that a serie must
     *          be selected.
     */
    boolean seriecheck() {
        if (null != serieF.getValue()) {
            return true;
        } else {
            this.seriecheck.setText("Please select a Serie");
            return false;
        }
    }

    // Méthode pour envoyer un SMS avec Twilio

    /**
     * Creates an SMS message, specifies the sender's and recipient's phone numbers,
     * and sends the message using a carrier service.
     *
     * @param recipientNumber
     *                        10-digit phone number of the recipient for whom the
     *                        SMS message is
     *                        being sent.
     * @param messageBody
     *                        text content of the SMS message to be sent.
     */
    private void sendSMS(final String recipientNumber, final String messageBody) {
        final PhoneNumber fromPhoneNumber = new PhoneNumber("+17573640849");
        final PhoneNumber toPhoneNumber = new PhoneNumber(recipientNumber);
        final Message message = Message.creator(toPhoneNumber, fromPhoneNumber, messageBody).create();
        EpisodeController.LOGGER.info("SMS sent successfully: " + message.getSid());
    }

    /**
     * Allows user to add a new episode to their chosen serie by filling in relevant
     * information and saving it to a database. It also sends an SMS to the user's
     * phone with the details of the added episode.
     *
     * @param event
     *              clicked button event on the user interface that triggered the
     *              function execution.
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
            } catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the episode. : " + e.getMessage());
                EpisodeController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Loads a FXML file, creates a scene, and displays it in a Stage, using the
     * given resources.
     *
     * @param event
     *              event that triggered the `Ocategories` function, providing the
     *              necessary information for the function to perform its actions.
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
     * Loads a FXML file, creates a scene, and sets the scene on a stage, displaying
     * the stage in the UI.
     *
     * @param event
     *              Event that triggered the function, and it is used to load the
     *              FXML
     *              file for display in the stage.
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
     * Loads and displays an FXML file named "/ui/series/Episode-view.fxml" in a
     * JavaFX application.
     *
     * @param event
     *              event that triggered the method execution, providing the
     *              necessary
     *              information for displaying the appropriate episode view.
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
     * Is called when the 'ActionEvent' occurs and has no defined functionality as
     * of now.
     *
     * @param actionEvent
     *                    event that triggered the execution of the `showMovies()`
     *                    function.
     */
    public void showmovies(final ActionEvent actionEvent) {
    }

    /**
     * Displays a list of products.
     *
     * @param actionEvent
     *                    event that triggered the execution of the `showProducts`
     *                    function.
     */
    public void showproducts(final ActionEvent actionEvent) {
    }

    /**
     * Likely displays a cinema or movie-related information within an application.
     *
     * @param actionEvent
     *                    event that triggered the function call.
     */
    public void showcinema(final ActionEvent actionEvent) {
    }

    /**
     * Handles an event generated by a user's interaction with a graphical user
     * interface (GUI).
     *
     * @param actionEvent
     *                    occurrence of an event that triggers the function's
     *                    execution.
     */
    public void showevent(final ActionEvent actionEvent) {
    }

    /**
     * Is triggered when an action event occurs and has no inherent meaning or
     * purpose beyond its activation.
     *
     * @param actionEvent
     *                    event that triggered the call to the `showSeries()`
     *                    method.
     */
    public void showseries(final ActionEvent actionEvent) {
    }
}

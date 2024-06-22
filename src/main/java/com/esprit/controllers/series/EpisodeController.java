package com.esprit.controllers.series;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Serie;
import com.esprit.services.series.DTO.EpisodeDto;
import com.esprit.services.series.IServiceEpisodeImpl;
import com.esprit.services.series.IServiceSerieImpl;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
public class EpisodeController {
    public static final String ACCOUNT_SID = "ACd3d2094ef7f546619e892605940f1631";
    public static final String AUTH_TOKEN = "8d56f8a04d84ff2393de4ea888f677a1";
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
    private List<Serie> serieList;
    @FXML
    private TableView<EpisodeDto> tableView;
    /**
     * Clears existing data in the `tableView`, then retrieves new data from an API and
     * displays it in the table with buttons for deleting and editing each episode.
     */
    private void ref() {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        serieF.getItems().clear();
        titreF.setText("");
        numeroepisodeF.setText("");
        saisonF.setText("");
        imgpath = "";
        videopath = "";
        IServiceEpisodeImpl iServiceEpisode = new IServiceEpisodeImpl();
        IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        try {
            serieList = iServiceSerie.recuperers();
            for (Serie s : serieList) {
                serieF.getItems().add(s.getNom());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ///// affichage du tableau
        IServiceSerieImpl serviceSerie = new IServiceSerieImpl();
        // TableColumn<EpisodeDto, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idepisode"));
        TableColumn<EpisodeDto, String> titreCol = new TableColumn<>("Title");
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        TableColumn<EpisodeDto, String> numeroepisodeCol = new TableColumn<>("Number");
        numeroepisodeCol.setCellValueFactory(new PropertyValueFactory<>("numeroepisode"));
        TableColumn<EpisodeDto, String> saisonCol = new TableColumn<>("Season");
        saisonCol.setCellValueFactory(new PropertyValueFactory<>("saison"));
        TableColumn<EpisodeDto, String> serieCol = new TableColumn<>("Serie");
        serieCol.setCellValueFactory(new PropertyValueFactory<>("nomSerie"));
        TableColumn<EpisodeDto, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Delete");
            {
                button.setOnAction(event -> {
                    EpisodeDto episodeDto = getTableView().getItems().get(getIndex());
                    try {
                        iServiceEpisode.supprimer(episodeDto.getIdserie());
                        tableView.getItems().remove(episodeDto);
                        showAlert("OK", "Deleted successfully !");
                        tableView.refresh();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Error", e.getSQLState());
                    }
                });
            }
            /**
             * Updates an item's graphical representation based on its emptiness status.
             * 
             * @param item component being updated, which can be either null or a `Button` object
             * when the `empty` parameter is false.
             * 
             * @param empty ether value of the item being updated, and determines whether or not
             * the button's graphic should be set to `null` or the `button`.
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
        TableColumn<EpisodeDto, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount = 0;
            {
                button.setOnAction(event -> {
                    clickCount++;
                    if (clickCount == 2) {
                        EpisodeDto episode = getTableView().getItems().get(getIndex());
                        modifierEpisode(episode);
                        tableView.refresh();
                        clickCount = 0;
                    }
                });
            }
            /**
             * Updates the graphical representation (graphic) associated with an item based on
             * its status as empty or not.
             * 
             * @param item Void item being updated, which is passed to the superclass's `updateItem`
             * method and then used to set the graphic of the button in the function.
             * 
             * @param empty state of the item being updated, and sets the graphic of the item
             * accordingly when it is false.
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
        // tableView.getColumns().addAll(idCol, titreCol, numeroepisodeCol, saisonCol,
        // serieCol, supprimerCol, modifierCol);
        tableView.getColumns().addAll(titreCol, numeroepisodeCol, saisonCol, serieCol, supprimerCol, modifierCol);
        // Récupérer les catégories et les ajouter à la TableView
        try {
            tableView.getItems().addAll(iServiceEpisode.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
    /**
     * Modifies an episode's details through a dialog box, including title, number, season,
     * image, and video, and then updates the episode in the database using an IoC container.
     * 
     * @param episodeDto data of an episode to be edited, containing information such as
     * title, number, season, image, and video path.
     */
    private void modifierEpisode(EpisodeDto episodeDto) {
        IServiceEpisodeImpl iServiceEpisode = new IServiceEpisodeImpl();
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Episode ");
        imgpath = episodeDto.getImage();
        videopath = episodeDto.getVideo();
        TextField titreFild = new TextField(episodeDto.getTitre());
        TextField numeroepisodeFild = new TextField(String.valueOf(episodeDto.getNumeroepisode()));
        TextField saisonFild = new TextField(String.valueOf(episodeDto.getSaison()));
        ComboBox<String> serieComboBox = new ComboBox<>();
        for (Serie s : serieList) {
            serieComboBox.getItems().add(s.getNom());
        }
        serieComboBox.setValue(episodeDto.getNomSerie());
        Button Ajouterimage = new Button("Add");
        {
            Ajouterimage.setOnAction(event -> {
                addimg(event);
            });
        }
        Button AJouterVideo = new Button("Add");
        {
            AJouterVideo.setOnAction(event -> {
                addVideo(event);
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
        Optional<Pair<String, String>> result = dialog.showAndWait();
        Episode episode = new Episode();
        result.ifPresent(pair -> {
            episode.setIdepisode(episodeDto.getIdepisode());
            episode.setTitre(titreFild.getText());
            episode.setNumeroepisode(Integer.parseInt(numeroepisodeFild.getText()));
            episode.setSaison(Integer.parseInt(saisonFild.getText()));
            episode.setImage(imgpath);
            episode.setVideo(videopath);
            for (Serie s : serieList) {
                if (s.getNom().equals(serieComboBox.getValue())) {
                    episode.setIdserie(s.getIdserie());
                }
            }
            try {
                System.out.println(episode);
                iServiceEpisode.modifier(episode);
                showAlert("Succes", "Modified successfully !");
                ref();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * References a provided reference.
     */
    @FXML
    private void initialize() {
        ref();
    }
    /** 
    /**
     * Creates an `Alert` object and sets its title, header text, and content text using
     * the input parameters. The `Alert.AlertType.INFORMATION` is set to indicate that
     * the alert should be displayed in a neutral manner. Finally, the `alert.showAndWait()`
     * method displays the alert and waits for the user to close it.
     * 
     * @param title title of an Alert that will be displayed to the user when the function
     * is called.
     * 
     * @param message content text to be displayed within an alert box when the function
     * is called.
     */
    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Allows users to select an image file using a FileChooser, stores the file path in
     * `imgpath`, and sets the image using `Image`.
     * 
     * @param event action that triggered the function, specifically the opening of a
     * file using the FileChooser.
     */
    @FXML
    void addimg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a picture");
        // Set file extension filter to only allow image files
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",
                "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null && isImageFile(selectedFile)) {
            imgpath = selectedFile.getAbsolutePath().replace("\\", "/");
            System.out.println("File path stored: " + imgpath);
            Image image = new Image(selectedFile.toURI().toString());
            // imgoeuvre.setImage(image);
        } else {
            System.out.println("Please select a valid image file.");
        }
    }
    /**
     * Allows the user to select an image file from a chosen directory, saves it to two
     * different locations, and displays the image in an `ImageView`.
     * 
     * @param event trigger that initiates the action of importing an image when clicked
     * by the user.
     */
    @FXML
    void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/series/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\series\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                episodeImageView.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
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
        return imgpath;
    }
    // Method to check if the selected file is an image file
    /**
     * Takes a `File` object as input and determines if it represents an image file or
     * not. It does this by creating an `Image` object from the file's URI, then checking
     * if the resulting `Image` is not in error. If the image is in error, the function
     * returns `false`.
     * 
     * @param file file to be checked for being an image file.
     * 
     * @returns a boolean value indicating whether the provided file is an image file or
     * not.
     */
    private boolean isImageFile(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            return !image.isError();
        } catch (Exception e) {
            return false;
        }
    }
    /////
    /**
     * Enables the user to select a video file from their computer, and if a valid video
     * file is selected, it stores the file path in a variable called `videopath`.
     * 
     * @param event occurance of a user clicking on the "Choose a video" button and
     * triggers the execution of the function.
     */
    @FXML
    void addVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a video");
        // Set file extension filter to only allow video files
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi",
                "*.mkv");
        fileChooser.getExtensionFilters().add(videoFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null && isVideoFile(selectedFile)) {
            String videoPath = selectedFile.getAbsolutePath().replace("\\", "/");
            videopath = videoPath;
            System.out.println("File path stored: " + videoPath);
        } else {
            System.out.println("Please select a valid video file.");
        }
    }
    // Method to check if the selected file is a video file
    /**
     * Determines if a given File is a video file based on its file name extension,
     * returning `true` if the extension matches "mp4", "avi", or "mkv", and `false` otherwise.
     * 
     * @param file File that needs to be checked for being a video file.
     * 
     * @returns a boolean value indicating whether the provided file is an MP4, AVI, or
     * MKV video file.
     */
    private boolean isVideoFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv");
    }
    /////
    /**
     * Checks if a given string can be converted to an integer using `Integer.parseInt()`.
     * If it can, it returns `true`, otherwise it returns `false`.
     * 
     * @param s String to be parsed as an integer.
     * 
     * @returns a boolean value indicating whether the given string can be parsed as an
     * integer.
     */
    boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Determines whether a title is provided and returns `true` if it is, else it displays
     * an error message and returns `false`.
     * 
     * @returns `true` if a title is provided, otherwise it returns `false` and provides
     * an error message.
     */
    boolean titrecheck() {
        if (titreF.getText() != "") {
            return true;
        } else {
            titrecheck.setText("Please enter a valid Title");
            return false;
        }
    }
    /**
     * Verifies if the user inputted season value is not empty and it's a numerical string,
     * if both conditions are true, it returns `true`, otherwise it displays an error
     * message and returns `false`.
     * 
     * @returns `true` if the input string is not empty and can be converted to an integer,
     * otherwise it returns `false`.
     */
    boolean seasoncheck() {
        String numero = saisonF.getText();
        if (!numero.isEmpty() && isStringInt(numero)) {
            return true;
        } else {
            seasoncheck.setText("Please enter a valid Season");
            return false;
        }
    }
    /**
     * Checks if an image file path is provided and returns `true` if yes, otherwise
     * returns `false`.
     * 
     * @returns a boolean value indicating whether a picture has been selected or not.
     */
    boolean picturechek() {
        if (imgpath != "") {
            return true;
        } else {
            picturechek.setText("Please select a Picture");
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
        String numero = numeroepisodeF.getText();
        if (!numero.isEmpty() && isStringInt(numero)) {
            return true;
        } else {
            numbercheck.setText("Please enter a Number ");
            return false;
        }
    }
    /**
     * Verifies if a video summary is entered by the user, and returns `true` if it is
     * valid, or `false` otherwise, with an appropriate error message displayed on the
     * UI if it's invalid.
     * 
     * @returns a boolean value indicating whether a valid video path has been provided.
     */
    boolean videocheck() {
        if (videopath != "") {
            return true;
        } else {
            videocheck.setText("Please enter a valid Summary");
            return false;
        }
    }
    /**
     * Checks if the value of `serieF` is not null, then returns `true`. Otherwise, it
     * sets the text of a text field called `seriecheck` to "Please select a Serie" and
     * returns `false`.
     * 
     * @returns `true` if a value is provided for `serieF.getValue()`, otherwise it returns
     * `false` with an error message indicating that a serie must be selected.
     */
    boolean seriecheck() {
        if (serieF.getValue() != null) {
            return true;
        } else {
            seriecheck.setText("Please select a Serie");
            return false;
        }
    }
    // Méthode pour envoyer un SMS avec Twilio
    /**
     * Creates an SMS message, specifies the sender's and recipient's phone numbers, and
     * sends the message using a carrier service.
     * 
     * @param recipientNumber 10-digit phone number of the recipient for whom the SMS
     * message is being sent.
     * 
     * @param messageBody text content of the SMS message to be sent.
     */
    private void sendSMS(String recipientNumber, String messageBody) {
        PhoneNumber fromPhoneNumber = new PhoneNumber("+17573640849");
        PhoneNumber toPhoneNumber = new PhoneNumber(recipientNumber);
        Message message = Message.creator(toPhoneNumber, fromPhoneNumber, messageBody).create();
        System.out.println("SMS sent successfully: " + message.getSid());
    }
    /**
     * Allows user to add a new episode to their chosen serie by filling in relevant
     * information and saving it to a database. It also sends an SMS to the user's phone
     * with the details of the added episode.
     * 
     * @param event clicked button event on the user interface that triggered the function
     * execution.
     */
    @FXML
    void ajouterSerie(ActionEvent event) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        IServiceEpisodeImpl episodeserv = new IServiceEpisodeImpl();
        Episode episode = new Episode();
        titrecheck();
        numbercheck();
        seasoncheck();
        videocheck();
        seriecheck();
        if (titrecheck() && numbercheck() && seasoncheck() && videocheck() && seriecheck()) {
            try {
                String fullPath = episodeImageView.getImage().getUrl();
                String requiredPath = fullPath.substring(fullPath.indexOf("/img/series/"));
                URI uri = new URI(requiredPath);
                episode.setTitre(titreF.getText());
                episode.setNumeroepisode(Integer.parseInt(numeroepisodeF.getText()));
                episode.setSaison(Integer.parseInt(saisonF.getText()));
                episode.setImage(uri.getPath());
                episode.setVideo(videopath);
                titrecheck.setText("");
                numbercheck.setText("");
                seasoncheck.setText("");
                picturechek.setText("");
                videocheck.setText("");
                seriecheck.setText("");
                for (Serie s : serieList) {
                    if (s.getNom() == serieF.getValue()) {
                        episode.setIdserie(s.getIdserie());
                    }
                }
                episodeserv.ajouter(episode);
                // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                // String message = "A new episode is here : " + episode.getNomSerie();
                // sendSMS("+21653775010", message);
                for (Serie s : serieList) {
                    if (Objects.equals(s.getNom(), serieF.getValue())) {
                        episode.setIdserie(s.getIdserie());
                        // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                        String message = " Episode " + episode.getNumeroepisode() + " Season " + episode.getSaison()
                                + "from your series : " + s.getNom() + " is now available!";
                        sendSMS("+21653775010", message);
                        break; // Sortir de la boucle une fois la série trouvée
                    }
                }
                tableView.refresh();
                ref();
            } catch (Exception e) {
                showAlert("Error", "An error occurred while saving the episode. : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * Loads a FXML file, creates a scene, and displays it in a Stage, using the given resources.
     * 
     * @param event event that triggered the `Ocategories` function, providing the necessary
     * information for the function to perform its actions.
     */
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Categorie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads a FXML file, creates a scene, and sets the scene on a stage, displaying the
     * stage in the UI.
     * 
     * @param event Event that triggered the function, and it is used to load the FXML
     * file for display in the stage.
     */
    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Serie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Loads and displays an FXML file named "Episode-view.fxml" in a JavaFX application.
     * 
     * @param event event that triggered the method execution, providing the necessary
     * information for displaying the appropriate episode view.
     */
    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Episode-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Is called when the 'ActionEvent' occurs and has no defined functionality as of now.
     * 
     * @param actionEvent event that triggered the execution of the `showMovies()` function.
     */
    public void showmovies(ActionEvent actionEvent) {
    }
    /**
     * Displays a list of products.
     * 
     * @param actionEvent event that triggered the execution of the `showProducts` function.
     */
    public void showproducts(ActionEvent actionEvent) {
    }
    /**
     * Likely displays a cinema or movie-related information within an application.
     * 
     * @param actionEvent event that triggered the function call.
     */
    public void showcinema(ActionEvent actionEvent) {
    }
    /**
     * Handles an event generated by a user's interaction with a graphical user interface
     * (GUI).
     * 
     * @param actionEvent occurrence of an event that triggers the function's execution.
     */
    public void showevent(ActionEvent actionEvent) {
    }
    /**
     * Is triggered when an action event occurs and has no inherent meaning or purpose
     * beyond its activation.
     * 
     * @param actionEvent event that triggered the call to the `showSeries()` method.
     */
    public void showseries(ActionEvent actionEvent) {
    }
}

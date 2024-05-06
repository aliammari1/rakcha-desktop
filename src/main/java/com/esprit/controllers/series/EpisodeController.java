package com.esprit.controllers.series;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Serie;
import com.esprit.models.users.Client;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EpisodeController {
    public static final String ACCOUNT_SID = "ACd3d2094ef7f546619e892605940f1631";
    public static final String AUTH_TOKEN = "8d56f8a04d84ff2393de4ea888f677a1";

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
            for (Serie s : serieList
            ) {
                serieF.getItems().add(s.getNom());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ///// affichage du tableau
        IServiceSerieImpl serviceSerie = new IServiceSerieImpl();
        //TableColumn<EpisodeDto, Integer> idCol = new TableColumn<>("ID");
        //idCol.setCellValueFactory(new PropertyValueFactory<>("idepisode"));

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
        // tableView.getColumns().addAll(idCol, titreCol, numeroepisodeCol, saisonCol, serieCol, supprimerCol, modifierCol);
        tableView.getColumns().addAll(titreCol, numeroepisodeCol, saisonCol, serieCol, supprimerCol, modifierCol);

        // Récupérer les catégories et les ajouter à la TableView
        try {
            tableView.getItems().addAll(iServiceEpisode.recuperer());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        for (Serie s : serieList
        ) {
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


        dialog.getDialogPane().setContent(new VBox(10, new Label("Title:"), titreFild, new Label("Number:"), numeroepisodeFild, new Label("Season :"), saisonFild, new Label("Add picture :"), Ajouterimage, new Label("Ajouer Video :"), AJouterVideo, serieComboBox));

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
            for (Serie s : serieList
            ) {
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

    @FXML
    private void initialize() {
        ref();
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void addimg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a picture");
        // Set file extension filter to only allow image files
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
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

    // Method to retrieve the stored file path
    public String getFilePath() {
        return imgpath;
    }

    // Method to check if the selected file is an image file
    private boolean isImageFile(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            return !image.isError();
        } catch (Exception e) {
            return false;
        }
    }

    /////
    @FXML
    void addVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a video");
        // Set file extension filter to only allow video files
        FileChooser.ExtensionFilter videoFilter =
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv");
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
    private boolean isVideoFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv");
    }

    /////
    boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    boolean titrecheck() {
        if (titreF.getText() != "") {

            return true;
        } else {
            titrecheck.setText("Please enter a valid Title");
            return false;
        }
    }

    boolean seasoncheck() {
        String numero = saisonF.getText();
        if (!numero.isEmpty() && isStringInt(numero)) {
            return true;
        } else {
            seasoncheck.setText("Please enter a valid Season");
            return false;
        }
    }

    boolean picturechek() {
        if (imgpath != "") {

            return true;
        } else {
            picturechek.setText("Please select a Picture");
            return false;
        }
    }

    boolean numbercheck() {
        String numero = numeroepisodeF.getText();
        if (!numero.isEmpty() && isStringInt(numero)) {

            return true;
        } else {
            numbercheck.setText("Please enter a Number ");
            return false;
        }
    }

    boolean videocheck() {
        if (videopath != "") {

            return true;
        } else {
            videocheck.setText("Please enter a valid Summary");
            return false;
        }
    }

    boolean seriecheck() {
        if (serieF.getValue() != null) {

            return true;
        } else {
            seriecheck.setText("Please select a Serie");
            return false;
        }
    }

    // Méthode pour envoyer un SMS avec Twilio
    private void sendSMS(String recipientNumber, String messageBody) {
        PhoneNumber fromPhoneNumber = new PhoneNumber("+17573640849");
        PhoneNumber toPhoneNumber = new PhoneNumber(recipientNumber);

        Message message = Message.creator(toPhoneNumber, fromPhoneNumber, messageBody).create();

        System.out.println("SMS sent successfully: " + message.getSid());
    }

    @FXML
    void ajouterSerie(ActionEvent event) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        IServiceEpisodeImpl episodeserv = new IServiceEpisodeImpl();
        Episode episode = new Episode();
        titrecheck();
        numbercheck();
        seasoncheck();
        picturechek();
        videocheck();
        seriecheck();
        if (titrecheck() && numbercheck() && seasoncheck() && picturechek() && videocheck() && seriecheck()) {
            try {
                episode.setTitre(titreF.getText());
                episode.setNumeroepisode(Integer.parseInt(numeroepisodeF.getText()));
                episode.setSaison(Integer.parseInt(saisonF.getText()));
                episode.setImage(imgpath);
                episode.setVideo(videopath);
                titrecheck.setText("");
                numbercheck.setText("");
                seasoncheck.setText("");
                picturechek.setText("");
                videocheck.setText("");
                seriecheck.setText("");
                for (Serie s : serieList
                ) {
                    if (s.getNom() == serieF.getValue()) {
                        episode.setIdserie(s.getIdserie());
                    }
                }
                episodeserv.ajouter(episode);
                // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                //String message = "A new episode is here : " + episode.getNomSerie();
                //sendSMS("+21653775010", message);
                for (Serie s : serieList) {
                    if (Objects.equals(s.getNom(), serieF.getValue())) {
                        episode.setIdserie(s.getIdserie());
                        // Envoi d'un SMS après avoir ajouté l'épisode avec succès
                        String message = " Episode " + episode.getNumeroepisode() + " Season " + episode.getSaison() + "from your series : " + s.getNom() + " is now available!";
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

    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Categorie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void Oseries(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Serie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Episode-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    public void showmovies(ActionEvent actionEvent) {
    }

    public void showproducts(ActionEvent actionEvent) {
    }

    public void showcinema(ActionEvent actionEvent) {
    }

    public void showevent(ActionEvent actionEvent) {
    }

    public void showseries(ActionEvent actionEvent) {
    }
}

package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import com.esprit.utils.DataSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardResponsableController implements Initializable {

    @FXML
    private File selectedFile;

    @FXML
    private ImageView image;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfResponsable;

    @FXML
    private FlowPane cinemaFlowPane;

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void addCinema(ActionEvent event) {
        if (selectedFile != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                // Convertir le fichier en un objet Blob
                FileInputStream fis = new FileInputStream(selectedFile);
                connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();

                // Définir le flux d'entrée de l'image dans l'objet Blob
                try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                String defaultStatut = "En_Attente";

                Cinema cinema = new Cinema(tfNom.getText(), tfAdresse.getText(), tfResponsable.getText(), imageBlob, defaultStatut);

                CinemaService cs = new CinemaService();
                cs.create(cinema);
                showAlert("Cinéma ajouté avec succès !");
            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de l'ajout du cinéma : " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        showAlert("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                    }
                }
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }

    }



    @FXML
    void selectImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            image.setImage(selectedImage);
        }

    }

    @FXML
    void listerCinemas(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListCinemaResponsable.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedCinemas();
    }

    private void loadAcceptedCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        List<Cinema> acceptedCinemas = cinemas.stream()
                .filter(cinema -> cinema.getStatut().equals("Acceptée"))
                .collect(Collectors.toList());

        if (acceptedCinemas.isEmpty()) {
            showAlert("Aucun cinéma accepté n'est disponible.");
            return;
        }

        for (Cinema cinema : acceptedCinemas) {
            HBox cardContainer = createCinemaCard(cinema);
            cinemaFlowPane.getChildren().add(cardContainer);
        }
    }

    private HBox createCinemaCard(Cinema cinema) {
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  25px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #000000; -fx-background-radius: 10px; -fx-border-width: 2px;");
        card.setPrefWidth(400);

        ImageView logoImageView = new ImageView();
        logoImageView.setFitWidth(70); // la largeur de l'image
        logoImageView.setFitHeight(70);
        logoImageView.setLayoutX(15); // Padding à droite
        logoImageView.setLayoutY(15); // Padding en haut
        logoImageView.setStyle("-fx-border-color: #000000 ; -fx-border-width: 2px; -fx-border-radius: 5px;");

        try {
            Blob logoBlob = cinema.getLogo();
            if (logoBlob != null) {
                byte[] logoBytes = logoBlob.getBytes(1, (int) logoBlob.length());
                Image logoImage = new Image(new ByteArrayInputStream(logoBytes));
                logoImageView.setImage(logoImage);
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("default_logo.png"));
                logoImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.getChildren().add(logoImageView);


        Label nameLabel = new Label("Name: " + cinema.getNom());
        nameLabel.setLayoutX(115);
        nameLabel.setLayoutY(25);
        nameLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(nameLabel);


        Label adresseLabel = new Label("Address: " + cinema.getAdresse());
        adresseLabel.setLayoutX(115);
        adresseLabel.setLayoutY(50);
        adresseLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        card.getChildren().add(adresseLabel);

        Line verticalLine = new Line();
        verticalLine.setStartX(240);
        verticalLine.setStartY(10);
        verticalLine.setEndX(240);
        verticalLine.setEndY(90);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(3);

        card.getChildren().add(verticalLine);


        Circle circle = new Circle();
        circle.setRadius(30);
        circle.setLayoutX(285);
        circle.setLayoutY(45);
        circle.setFill(Color.web("#ae2d3c"));


        FontAwesomeIconView deleteIcon = new FontAwesomeIconView();
        deleteIcon.setGlyphName("TRASH");
        deleteIcon.setSize("3.5em");
        deleteIcon.setLayoutX(270);
        deleteIcon.setLayoutY(57);
        deleteIcon.setFill(Color.WHITE);

        deleteIcon.setOnMouseClicked(event -> {
            CinemaService cinemaService = new CinemaService();
            cinemaService.delete(cinema);
            cinemaFlowPane.getChildren().remove(card);
        });

        card.getChildren().addAll(circle, deleteIcon);

        Circle SalleCircle = new Circle();
        SalleCircle.setRadius(30);
        SalleCircle.setLayoutX(360);
        SalleCircle.setLayoutY(45);
        SalleCircle.setFill(Color.web("#ae2d3c"));

        FontAwesomeIconView salleIcon = new FontAwesomeIconView();
        salleIcon.setGlyphName("GROUP");
        salleIcon.setSize("3em");
        salleIcon.setLayoutX(340);
        salleIcon.setLayoutY(57);
        salleIcon.setFill(Color.WHITE);

        salleIcon.setOnMouseClicked(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListSalleResponsable.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                ListSalleResponsableController controller = loader.getController();
                controller.initData(cinema.getId_cinema());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });

        card.getChildren().addAll(SalleCircle, salleIcon);
        cardContainer.getChildren().add(card);
        return cardContainer;
    }

}

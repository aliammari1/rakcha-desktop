package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.services.cinemas.CinemaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ModifierCinemaController implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfAdresse;

    @FXML
    private ImageView tfLogo;

    private Cinema cinema;

    private File selectedFile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initData(Cinema cinema) {
        this.cinema = cinema;
        tfNom.setText(cinema.getNom());
        tfAdresse.setText(cinema.getAdresse());

        String logo = cinema.getLogo();
        Image image = new Image(logo);
        tfLogo.setImage(image);
    }

    @FXML
    void modifier(ActionEvent event) throws IOException {
        if (cinema == null) {
            showAlert("Veuillez sélectionner un cinéma.");
            return;
        }

        // Récupérer les nouvelles valeurs des champs
        String nouveauNom = tfNom.getText();
        String nouvelleAdresse = tfAdresse.getText();

        // Vérifier si les champs obligatoires sont remplis
        if (nouveauNom.isEmpty() || nouvelleAdresse.isEmpty()) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Mettre à jour les informations du cinéma
        cinema.setNom(nouveauNom);
        cinema.setAdresse(nouvelleAdresse);
        cinema.setLogo("");


        // Mettre à jour le cinéma dans la base de données
        CinemaService cinemaService = new CinemaService();
        cinemaService.update(cinema);

        showAlert("Les modifications ont été enregistrées avec succès.");

        // Charger la nouvelle interface ListCinemaAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListCinemaResponsable.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void select(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            tfLogo.setImage(selectedImage);
        }
    }


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}



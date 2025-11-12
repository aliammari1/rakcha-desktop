package com.esprit.controllers.cinemas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

/**
 * Controller for modifying cinema details in a GUI application using JavaFX.
 * 
 * <p>
 * This controller provides functionality to modify cinema information such as
 * name,
 * address, and logo. It handles form validation, file selection for logos, and
 * database updates through the CinemaService.
 * </p>
 * 
 * <p>
 * The controller implements the Initializable interface to set up the UI
 * components
 * when the FXML is loaded.
 * </p>
 * 
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 * @see javafx.fxml.Initializable
 * @see com.esprit.models.cinemas.Cinema
 * @see com.esprit.services.cinemas.CinemaService
 */
public class ModifierCinemaController implements Initializable {
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfAdresse;
    @FXML
    private ImageView tfLogo;
    private Cinema cinema;
    private File selectedFile;

    /**
     * Called by the JavaFX runtime after the FXML is loaded; performs no additional initialization.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    /**
     * Populate the form fields and logo ImageView from the provided Cinema.
     *
     * Sets the name and address text fields to the cinema's values and updates the logo ImageView
     * when the cinema's logo path is non-null and non-empty.
     *
     * @param cinema the Cinema whose data will be used to populate the form
     */
    public void initData(Cinema cinema) {
        this.cinema = cinema;
        tfNom.setText(cinema.getName());
        tfAdresse.setText(cinema.getAddress());
        String logo = cinema.getLogoPath();
        if (logo != null && !logo.isEmpty()) {
            Image image = new Image(logo);
            tfLogo.setImage(image);
        }

    }


    /**
     * Saves edits made to the currently selected cinema and opens the cinema dashboard.
     *
     * <p>Validates that a cinema is selected and that required fields (name and address)
     * are filled; shows an informational alert on validation failure or success. Updates
     * the cinema's name and address, clears its logo path, persists the change via
     * CinemaService, and loads the DashboardResponsableCinema.fxml interface.</p>
     *
     * @throws IOException if loading the dashboard FXML fails
     * @since 1.0
     */
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
        cinema.setName(nouveauNom);
        cinema.setAddress(nouvelleAdresse);
        cinema.setLogoPath("");
        // Mettre à jour le cinéma dans la base de données
        CinemaService cinemaService = new CinemaService();
        cinemaService.update(cinema);
        showAlert("Les modifications ont été enregistrées avec succès.");
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
        final Parent root = loader.load();
        // Créer une nouvelle scène avec la nouvelle interface
        final Scene scene = new Scene(root);
        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Handles file selection for cinema logo images.
     * 
     * <p>
     * This method opens a file chooser dialog allowing users to select an image
     * file
     * for the cinema logo. The selected image is then displayed in the logo
     * ImageView.
     * </p>
     *
     * @param event the ActionEvent that triggered this method
     * @since 1.0
     */
    @FXML
    void select(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        this.selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            final Image selectedImage = new Image(this.selectedFile.toURI().toString());
            this.tfLogo.setImage(selectedImage);
        }

    }


    /**
     * Displays an information alert containing the provided message.
     *
     * @param message the text to display in the alert dialog
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}

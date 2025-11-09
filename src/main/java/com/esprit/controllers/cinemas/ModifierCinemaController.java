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
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     * 
     * <p>
     * This method is a no-op implementation as no specific initialization is
     * required.
     * </p>
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object,
     *                  or null if the location is not known
     * @param resources the resources used to localize the root object,
     *                  or null if the root object was not localized
     * @since 1.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Initializes the form with cinema data for editing.
     * 
     * <p>
     * This method sets the text fields and image view with the current cinema's
     * information including name, address, and logo image.
     * </p>
     *
     * @param cinema the Cinema object containing the data to populate the form
     * @throws IllegalArgumentException if cinema is null
     * @since 1.0
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
     * Allows users to edit the details of a cinema, including its name and address.
     * It updates the cinema's information in the database and displays an alert
     * message upon successful completion.
     *
     * @param event ActionEvent object that triggered the method execution,
     *              providing
     *              the source of the event and any related data
     * @throws IOException if an I/O error occurs during FXML loading
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
     * Creates an Alert dialog with an information message.
     *
     * @param message
     *                text to be displayed as an information message when the
     *                `showAlert()` method is called.
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

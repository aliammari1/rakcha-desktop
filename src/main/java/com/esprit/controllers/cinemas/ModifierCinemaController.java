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
/**
 * Is used to modify the details of a cinema object in a GUI application using JavaFX.
 * It has fields for entering cinema name, address, and logo, and methods for updating
 * the cinema details and displaying an alert message. The class also includes an
 * initialize method and event handlers for the select and modifier buttons.
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
     * Is called when an instance of a class is created and initializes its resources by
     * performing no-op actions.
     * 
     * @param location URL of the web application's root document, which is used to locate
     * the necessary resources for its proper operation.
     * 
     * @param resources ResourceBundle that contains keys for localization of the
     * application's user interface and other textual content.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    /**
     * Sets text fields and displays an image based on input cinema object's properties:
     * nom, adresse, logo.
     * 
     * @param cinema Cinema object that contains the name, address, and logo of the cinema,
     * which are then set as text values for the `tfNom`, `tfAdresse`, and `tfLogo` fields,
     * respectively, within the function's body.
     * 
     * 	- `cinema`: A `Cinema` object representing a movie theater with name, address,
     * and logo.
     */
    public void initData(Cinema cinema) {
        this.cinema = cinema;
        tfNom.setText(cinema.getNom());
        tfAdresse.setText(cinema.getAdresse());
        String logo = cinema.getLogo();
        Image image = new Image(logo);
        tfLogo.setImage(image);
    }
    /**
     * Allows users to edit the details of a cinema, including its name and address. It
     * updates the cinema's information in the database and displays an alert message
     * upon successful completion.
     * 
     * @param event ActionEvent object that triggered the method execution, providing the
     * source of the event and any related data.
     * 
     * 	- `event` is an instance of `ActionEvent`, which represents a user action related
     * to a UI component.
     * 	- The `event` object contains information about the action that triggered the
     * function, such as the source of the action (e.g., a button or a menu item) and the
     * state of the component at the time of the action.
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
    /**
     * Is used to select an image file from a file chooser and set it as the logo for the
     * FXML stage.
     * 
     * @param event selection event that triggered the function execution.
     * 
     * 	- Event type: `ActionEvent`
     * 	- Target: `null` (no specific component is associated with the event)
     */
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
    /**
     * Creates an Alert dialog with an information message.
     * 
     * @param message text to be displayed as an information message when the `showAlert()`
     * method is called.
     */
    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

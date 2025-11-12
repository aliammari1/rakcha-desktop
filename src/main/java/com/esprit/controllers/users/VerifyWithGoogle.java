package com.esprit.controllers.users;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.esprit.utils.SignInGoogle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerifyWithGoogle {
    private static final Logger LOGGER = Logger.getLogger(VerifyWithGoogle.class.getName());

    @FXML
    private TextField authTextField;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Hyperlink hyperlink;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Button sendButton;

    /**
     * Initializes the controller by launching the Google sign-in page in the system default browser.
     *
     * @throws IOException if the system default browser cannot be launched or the URI cannot be accessed
     * @throws ExecutionException if obtaining the sign-in URL fails
     * @throws InterruptedException if the thread is interrupted while obtaining the sign-in URL
     */
    @FXML
    void initialize() throws IOException, ExecutionException, InterruptedException {
        final String link = SignInGoogle.signInWithGoogle();
        Desktop.getDesktop().browse(URI.create(link));
    }


    /**
     * Verifies the authorization code entered in the authTextField and, if valid, replaces the current window's scene with the Profile view.
     *
     * If verification fails, the scene is not changed.
     *
     * @param event the ActionEvent that triggered the verification
     */
    @FXML
    void verifyAuthCode(final ActionEvent event) {
        try {
            SignInGoogle.verifyAuthUrl(this.authTextField.getText());
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.sendButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            VerifyWithGoogle.LOGGER.info("the auth is wrong");
        }

    }

}

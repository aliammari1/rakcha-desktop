package com.esprit.controllers.users;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.esprit.utils.SignInMicrosoft;

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
public class VerifyWithMicrosoft {
    private static final Logger LOGGER = Logger.getLogger(VerifyWithMicrosoft.class.getName());

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
     * Initializes the controller by obtaining a Microsoft sign-in URL and opening it in the system default browser.
     *
     * @throws IOException if the system browser cannot be launched or the URL cannot be accessed
     * @throws ExecutionException if obtaining the sign-in URL fails with an execution error
     * @throws InterruptedException if the thread is interrupted while obtaining the sign-in URL
     */
    @FXML
    void initialize() throws IOException, ExecutionException, InterruptedException {
        final String link = SignInMicrosoft.SignInWithMicrosoft();
        Desktop.getDesktop().browse(URI.create(link));
    }


    /**
     * Verify the authentication code entered and navigate to the user's profile on success.
     *
     * Attempts to validate the code from the authTextField; if validation succeeds, replaces the current window's scene with the Profile view. If validation fails, the method logs the failure and leaves the UI unchanged.
     *
     * @param event the UI action that triggered verification
     */
    @FXML
    void verifyAuthCode(final ActionEvent event) {
        try {
            SignInMicrosoft.verifyAuthUrl(this.authTextField.getText());
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.sendButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            VerifyWithMicrosoft.LOGGER.info("the auth is wrong");
        }

    }

}

package com.esprit.controllers.users;

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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

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
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void initialize() throws IOException, ExecutionException, InterruptedException {
        final String link = SignInGoogle.signInWithGoogle();
        Desktop.getDesktop().browse(URI.create(link));
    }

    /**
     * @param event
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

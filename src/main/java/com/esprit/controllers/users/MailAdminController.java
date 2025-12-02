package com.esprit.controllers.users;

import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import com.esprit.utils.UserMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MailAdminController {

    private static final Logger LOGGER = Logger.getLogger(MailAdminController.class.getName());

    @FXML
    private Label emailErrorLabel;
    @FXML
    private TextField mailTextField;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Button sendButton;

    /**
     * Sends verification code to email.
     *
     * @param event the action event triggered by the send button
     */
    @FXML
    void sendMail(final ActionEvent event) {
        final String email = this.mailTextField.getText().trim();

        if (email.isEmpty()) {
            this.emailErrorLabel.setText("Please enter an email address");
            return;
        }

        try {
            // Check if email exists in database
            final UserService userService = new UserService();
            if (!userService.checkEmailFound(email)) {
                this.emailErrorLabel.setText("Email not found in our system");
                return;
            }

            final SecureRandom random = new SecureRandom();
            final String verificationCode = String.format("%06d",
                random.nextInt(1000000));

            // Store code and email in session
            SessionManager.setVerificationData(verificationCode, email);

            // Send verification code email using the professional template
            UserMail.sendVerificationCode(email, "User", verificationCode, 10);

            // Clear error labels
            this.emailErrorLabel.setText("");
            this.passwordErrorLabel.setText("");

            // Navigate to verification code screen
            navigateToVerificationScreen();

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending email: " + e.getMessage(), e);
            this.emailErrorLabel.setText("Failed to send email. Please try again.");
        }
    }

    /**
     * Navigates to the verification code screen.
     *
     * @throws IOException if FXML file cannot be loaded
     */
    private void navigateToVerificationScreen() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/users/VerificationCode.fxml"));
        final Parent root = loader.load();
        final Stage stage = (Stage) this.sendButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}

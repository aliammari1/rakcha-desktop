package com.esprit.controllers.users;

import com.esprit.utils.SessionManager;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller for email verification code screen.
 * Handles user verification code input and navigation to reset password screen.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerificationCodeController {

    private static final Logger LOGGER = Logger.getLogger(VerificationCodeController.class.getName());

    @FXML
    private TextField codeTextField;
    @FXML
    private Label codeErrorLabel;
    @FXML
    private Button verifyButton;

    /**
     * Verifies the code entered by user.
     *
     * @param event the action event triggered by the verify button
     */
    @FXML
    void verifyCode(final ActionEvent event) {
        final String code = this.codeTextField.getText().trim();

        if (code.isEmpty()) {
            this.codeErrorLabel.setText("Please enter the verification code");
            return;
        }

        if (code.length() != 6) {
            this.codeErrorLabel.setText("Code must be exactly 6 digits");
            return;
        }

        if (!code.matches("\\d{6}")) {
            this.codeErrorLabel.setText("Code must contain only numbers");
            return;
        }

        // Check if code has expired
        if (SessionManager.isCodeExpired()) {
            this.codeErrorLabel.setText("Verification code has expired. Please request a new one.");
            SessionManager.clearVerificationData();
            return;
        }

        // Verify the code
        if (!SessionManager.verifyCode(code)) {
            this.codeErrorLabel.setText("Invalid verification code. Please try again.");
            return;
        }

        try {
            // Code is valid, navigate to reset password screen
            navigateToResetPassword();

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to reset password: " + e.getMessage(), e);
            this.codeErrorLabel.setText("Error proceeding. Please try again.");
        }
    }

    /**
     * Navigates to the reset password screen.
     *
     * @throws IOException if FXML file cannot be loaded
     */
    private void navigateToResetPassword() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/users/ResetPasswordlogin.fxml"));
        final Parent root = loader.load();
        final ResetPasswordController controller = loader.getController();

        // Pass the email to the reset password controller
        controller.setUserEmail(SessionManager.getUserEmail());

        final Stage stage = (Stage) this.verifyButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}

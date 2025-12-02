package com.esprit.controllers.users;

import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

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
public class ResetPasswordController {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());

    @FXML
    private TextField newPass;
    @FXML
    private TextField pass;
    @FXML
    private Label passwordErrorLabel;

    private String userEmail;

    /**
     * Sets the user email for password reset.
     *
     * @param email the user's email address
     */
    public void setUserEmail(final String email) {
        this.userEmail = email;
    }

    /**
     * Resets the user's password.
     *
     * @param event the action event triggered by the reset button
     */
    @FXML
    void resetPassword(final ActionEvent event) {
        final String newPassword = this.newPass.getText().trim();
        final String confirmPassword = this.pass.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            this.passwordErrorLabel.setText("Please fill in all password fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            this.passwordErrorLabel.setText("Passwords do not match");
            return;
        }

        if (newPassword.length() < 6) {
            this.passwordErrorLabel.setText("Password must be at least 6 characters");
            return;
        }

        try {
            // Hash the new password using BCrypt
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            // Update password in database for this.userEmail
            final UserService userService = new UserService();
            userService.updatePassword(this.userEmail, hashedPassword);

            LOGGER.log(Level.INFO, "Password reset successfully for email: " + this.userEmail);

            // Clear session data
            SessionManager.clearVerificationData();

            // Navigate to login screen
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.newPass.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (final Exception e) {
            ResetPasswordController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            this.passwordErrorLabel.setText("Error resetting password. Please try again.");
        }
    }
}

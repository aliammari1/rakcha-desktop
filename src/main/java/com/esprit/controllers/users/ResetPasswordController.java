package com.esprit.controllers.users;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
public class ResetPasswordController {
    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());

    @FXML
    private TextField newPass;
    @FXML
    private TextField pass;
    @FXML
    private Label passwordErrorLabel;

    /**
     * Attempts to switch the UI to the login view when the new password and confirmation match.
     *
     * If the two password fields contain identical text, replaces the current stage's scene with the login scene;
     * otherwise no action is taken. Exceptions raised while loading or setting the view are caught and logged.
     *
     * @param event the ActionEvent from the UI control that invoked this handler
     */
    @FXML
    void resetPassword(final ActionEvent event) {
        if (this.newPass.getText().equals(this.pass.getText())) {
            try {
                // new UserService().forgetPassword();
                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
                final Parent root = loader.load();
                final Stage stage = (Stage) this.newPass.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
 catch (final Exception e) {
                ResetPasswordController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

    }

}

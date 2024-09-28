package com.esprit.controllers.users;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResetPasswordController {
    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());

    @FXML
    private TextField newPass;
    @FXML
    private TextField pass;
    @FXML
    private Label passwordErrorLabel;

    /**
     * @param event
     */
    @FXML
    void resetPassword(final ActionEvent event) {
        if (this.newPass.getText().equals(this.pass.getText())) {
            try {
                // new UserService().forgetPassword();
                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Login.fxml"));
                final Parent root = loader.load();
                final Stage stage = (Stage) this.newPass.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (final Exception e) {
                ResetPasswordController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}

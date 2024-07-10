package com.esprit.controllers.users;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.services.produits.AvisService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    void resetPassword(ActionEvent event) {
        if (newPass.getText().equals(pass.getText())) {
            try {
                // new UserService().forgetPassword();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) newPass.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}

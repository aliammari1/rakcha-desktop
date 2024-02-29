package com.esprit.controllers;

import com.esprit.models.User;
import com.esprit.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    void login(ActionEvent event) throws IOException {
        UserService userService = new UserService();
        User user = userService.login(emailTextField.getText(), passwordTextField.getText());
        System.out.println(user.getRole());
        if (user != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "the user was found", ButtonType.CLOSE);
            alert.show();
            Stage stage = (Stage) emailTextField.getScene().getWindow();
            if (user.getRole().equals("admin")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
                Parent root = loader.load();
                stage.setUserData(user);
                stage.setScene(new Scene(root));
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
            alert.show();
        }
    }

}

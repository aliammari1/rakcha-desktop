package com.esprit.controllers;

import com.esprit.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AuthController {


    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passwordTextField;


    @FXML
    void login(ActionEvent event) {
        UserService userService = new UserService();
        userService.login(emailTextField.getText(), passwordTextField.getText());
    }

}

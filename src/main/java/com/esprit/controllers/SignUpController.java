package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.models.Responsable_de_cinema;
import com.esprit.models.User;
import com.esprit.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SignUpController {

    @FXML
    private TextField adresseTextField;

    @FXML
    private DatePicker dateDeNaissanceDatePicker;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nomTextField;

    @FXML
    private TextField num_telephoneTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private ImageView photoDeProfilImageView;

    @FXML
    private TextField prenomTextField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    void initialize() {
        List<String> roleList = new ArrayList<>() {
            {
                add("responsable de cinema");
                add("client");
            }
        };
        for (String role : roleList)
            roleComboBox.getItems().add(role);
    }

    @FXML
    void importImage(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg")
            );
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                Image image = new Image(file.toURI().toURL().toString());
                photoDeProfilImageView.setImage(image);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void signup(ActionEvent event) {
        try {
            String role = roleComboBox.getValue();
            User user = null;
            URI uri = null;
            if (role.equals("responsable de cinema")) {
                uri = new URI(photoDeProfilImageView.getImage().getUrl());
                user = new Responsable_de_cinema(nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), uri.getPath());
            } else if (role.equals("client")) {
                uri = new URI(photoDeProfilImageView.getImage().getUrl());
                user = new Client(nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), uri.getPath());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
                alert.show();
                return;
            }
            UserService userService = new UserService();
            userService.create(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

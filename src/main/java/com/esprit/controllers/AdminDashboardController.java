package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import com.esprit.models.Responsable_de_cinema;
import com.esprit.models.User;
import com.esprit.services.UserService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController {

    boolean isImageImported = false;
    File imageFile = null;
    @FXML
    private TableColumn<User, String> adresseTableColumn;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TableColumn<User, Date> dateDeNaissanceTableColumn;
    @FXML
    private TableColumn<User, String> emailTableColumn;
    @FXML
    private TextField emailTextField;
    @FXML
    private TableColumn<User, Integer> idTableColumn;
    @FXML
    private TextField idTextField;
    @FXML
    private TableColumn<User, String> nomTableColumn;
    @FXML
    private TextField nomTextField;
    @FXML
    private TableColumn<User, Integer> numTelTableColumn;
    @FXML
    private TextField num_telephoneTextField;
    @FXML
    private TableColumn<User, String> passwordTableColumn;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TableColumn<User, ImageView> photoDeProfilTableColumn;
    @FXML
    private TableColumn<User, String> prenomTableColumn;
    @FXML
    private TextField prenomTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TableColumn<User, String> roleTableColumn;
    @FXML
    private TableView<User> userTableView;

    @FXML
    void initialize() {
        idTableColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        nomTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nom"));
        prenomTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("prenom"));
        numTelTableColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("num_telephone"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
        adresseTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("adresse"));
        dateDeNaissanceTableColumn.setCellValueFactory(new PropertyValueFactory<User, Date>("date_de_naissance"));
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        photoDeProfilTableColumn.setCellValueFactory(new PropertyValueFactory<User, ImageView>("photo_de_profil_imageView"));
        List<String> roleList = new ArrayList<>() {
            {
                add("admin");
            }
        };
        for (String role : roleList)
            roleComboBox.getItems().add(role);
        readUserTable();
    }

    @FXML
    void readUserTable() {
        try {
            UserService userService = new UserService();
            List<User> userList = userService.read();
            userTableView.setItems(FXCollections.observableArrayList(userList));
            userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                User user = userTableView.getSelectionModel().getSelectedItem();
                if (user != null) {
                    System.out.println(user);
                    user = switch (user.getRole()) {
                        case "admin":
                            yield new Admin(user.getId(), user.getNom(), user.getPrenom(), user.getNum_telephone(), user.getPassword(), user.getRole(), user.getEmail(), user.getDate_de_naissance(), user.getEmail(), user.getPhoto_de_profil());
                        case "responsable de cinema":
                            yield new Responsable_de_cinema(user.getId(), user.getNom(), user.getPrenom(), user.getNum_telephone(), user.getPassword(), user.getRole(), user.getEmail(), user.getDate_de_naissance(), user.getEmail(), user.getPhoto_de_profil());
                        case "client":
                            yield new Client(user.getId(), user.getNom(), user.getPrenom(), user.getNum_telephone(), user.getPassword(), user.getRole(), user.getEmail(), user.getDate_de_naissance(), user.getEmail(), user.getPhoto_de_profil());
                        default:
                            yield null;
                    };
                    if (user == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
                        alert.show();
                        return;
                    }
                    idTextField.setText(String.valueOf(user.getId()));
                    nomTextField.setText(user.getNom());
                    prenomTextField.setText(user.getPrenom());
                    num_telephoneTextField.setText(String.valueOf(user.getNum_telephone()));
                    passwordTextField.setText(user.getPassword());
                    roleComboBox.setValue(user.getRole());
                    adresseTextField.setText(user.getAdresse());
                    dateDeNaissanceDatePicker.setValue(user.getDate_de_naissance().toLocalDate());
                    emailTextField.setText(user.getEmail());
                    photoDeProfilImageView.setImage(user.getPhoto_de_profil_imageView().getImage());
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @FXML
    void addAdmin(ActionEvent event) {
        try {
            String role = roleComboBox.getValue();
            User user = null;
            URI uri = null;
            if (role.equals("admin")) {
                uri = new URI(photoDeProfilImageView.getImage().getUrl());
                user = new Admin(nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), uri.getPath());
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

    @FXML
    void clearTextFields(ActionEvent event) {
        idTextField.setText("");
        nomTextField.setText("");
        prenomTextField.setText("");
        num_telephoneTextField.setText("");
        passwordTextField.setText("");
        roleComboBox.setValue("");
        adresseTextField.setText("");
        dateDeNaissanceDatePicker.setValue(new Date(0, 0, 0).toLocalDate());
        emailTextField.setText("");
        photoDeProfilImageView.setImage(null);
    }

    @FXML
    void delete(ActionEvent event) {
        String role = roleComboBox.getValue();
        User user = null;
        if (role.equals("admin")) {
            user = new Admin(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
        } else if (role.equals("responsable de cinema")) {
            user = new Responsable_de_cinema(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
        } else if (role.equals("client")) {
            user = new Client(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
            alert.show();
            return;
        }
        UserService userService = new UserService();
        userService.delete(user);
        isImageImported = false;
        imageFile = null;
        readUserTable();
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
                isImageImported = true;
                imageFile = file;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void update(ActionEvent event) {
        try {
            String role = roleComboBox.getValue();
            User user = null;
            if (isImageImported) {
                if (role.equals("admin")) {
                    user = new Admin(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), imageFile);
                } else if (role.equals("responsable de cinema")) {
                    user = new Responsable_de_cinema(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), imageFile);
                } else if (role.equals("client")) {
                    user = new Client(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), imageFile);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
            } else {
                if (role.equals("admin")) {
                    user = new Admin(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
                } else if (role.equals("responsable de cinema")) {
                    user = new Responsable_de_cinema(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
                } else if (role.equals("client")) {
                    user = new Client(Integer.parseInt(idTextField.getText()), nomTextField.getText(), prenomTextField.getText(), Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(), roleComboBox.getValue(), emailTextField.getText(), Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
                    alert.show();
                    return;
                }
            }
            UserService userService = new UserService();
            userService.update(user);
            isImageImported = false;
            imageFile = null;
            readUserTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

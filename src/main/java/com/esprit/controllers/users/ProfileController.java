package com.esprit.controllers.users;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;

public class ProfileController {
    @FXML
    public AnchorPane leftPane;
    User user;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private Circle imageCircle;

    @FXML
    public void initialize() {

    }

    /**
     * @param user
     */
    @FXML
    public void setData(final User user) {
        this.user = user;
        if (null != user.getFirstName()) {
            this.firstNameTextField.setText(user.getFirstName());
        }
        if (null != user.getLastName()) {
            this.lastNameTextField.setText(user.getLastName());
        }
        if (null != user.getAddress()) {
            this.adresseTextField.setText(user.getAddress());
        }
        if (null != user.getEmail()) {
            this.emailTextField.setText(user.getEmail());
        }
        if (0 != user.getPhoneNumber()) {
            this.phoneNumberTextField.setText(String.valueOf(user.getPhoneNumber()));
        }
        if (null != user.getBirthDate()) {
            this.dateDeNaissanceDatePicker.setValue(user.getBirthDate().toLocalDate());
        }
        if (null != user.getPhoto_de_profil()) {
            this.imageCircle.setFill(new ImagePattern(new Image(user.getPhoto_de_profil())));
        }
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    public void deleteAccount(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.firstNameTextField.getScene().getWindow();
        final User user = (User) stage.getUserData();
        final UserService userService = new UserService();
        userService.delete(user);
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void modifyAccount(final ActionEvent event) {
        final Stage stage = (Stage) this.firstNameTextField.getScene().getWindow();
        final User user = (User) stage.getUserData();
        user.setFirstName(this.firstNameTextField.getText());
        user.setLastName(this.lastNameTextField.getText());
        user.setAddress(this.adresseTextField.getText());
        user.setEmail(this.emailTextField.getText());
        user.setPassword(this.passwordTextField.getText());
        user.setBirthDate(Date.valueOf(this.dateDeNaissanceDatePicker.getValue()));
        user.setPhoneNumber(Integer.parseInt(this.phoneNumberTextField.getText()));
        stage.setUserData(user);
        final UserService userService = new UserService();
        userService.update(user);
    }

    @FXML
    public void signOut(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.emailTextField.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    public void setLeftPane(final AnchorPane leftPane) {
        this.leftPane.getChildren().clear();
        this.leftPane.getChildren().add(leftPane);
    }
}

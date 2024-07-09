package com.esprit.controllers.users;
import java.io.IOException;
import java.sql.Date;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
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
    public void setData(User user) {
        this.user = user;
        if (user.getFirstName() != null) {
            firstNameTextField.setText(user.getFirstName());
        }
        if (user.getLastName() != null) {
            lastNameTextField.setText(user.getLastName());
        }
        if (user.getAddress() != null) {
            adresseTextField.setText(user.getAddress());
        }
        if (user.getEmail() != null) {
            emailTextField.setText(user.getEmail());
        }
        if (user.getPhoneNumber() != 0) {
            phoneNumberTextField.setText(String.valueOf(user.getPhoneNumber()));
        }
        if (user.getBirthDate() != null) {
            dateDeNaissanceDatePicker.setValue(user.getBirthDate().toLocalDate());
        }
        if (user.getPhoto_de_profil() != null) {
            photoDeProfilImageView.setImage(new Image(user.getPhoto_de_profil()));
        }
    }
    /** 
     * @param event
     * @throws IOException
     */
    @FXML
    public void deleteAccount(ActionEvent event) throws IOException {
        Stage stage = (Stage) firstNameTextField.getScene().getWindow();
        User user = (User) stage.getUserData();
        UserService userService = new UserService();
        userService.delete(user);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }
    @FXML
    public void modifyAccount(ActionEvent event) {
        Stage stage = (Stage) firstNameTextField.getScene().getWindow();
        User user = (User) stage.getUserData();
        user.setFirstName(firstNameTextField.getText());
        user.setLastName(lastNameTextField.getText());
        user.setAddress(adresseTextField.getText());
        user.setEmail(emailTextField.getText());
        user.setPassword(passwordTextField.getText());
        user.setBirthDate(Date.valueOf(dateDeNaissanceDatePicker.getValue()));
        user.setPhoneNumber(Integer.parseInt(phoneNumberTextField.getText()));
        stage.setUserData(user);
        UserService userService = new UserService();
        userService.update(user);
    }
    @FXML
    public void signOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }
    public void setLeftPane(AnchorPane leftPane) {
        this.leftPane.getChildren().clear();
        this.leftPane.getChildren().add(leftPane);
    }
}

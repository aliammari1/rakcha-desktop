package com.esprit.controllers.users;
import com.esprit.utils.UserSMSAPI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
public class SMSAdminController implements Initializable {
    int verificationCode = 0;
    @FXML
    private TextField codeTextField;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private TextField phoneNumberTextfield;
    @FXML
    private Button sendSMS;
    /** 
     * @param event
     */
    public void sendSMS(ActionEvent event) {
        if (verificationCode == Integer.parseInt(codeTextField.getText())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPasswordlogin.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) codeTextField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /** 
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        phoneNumberTextfield.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.trim().length() == 8) {
                    Random random = new Random();
                    verificationCode = random.nextInt(999999 - 100000) + 100000;
                    UserSMSAPI.sendSMS(Integer.parseInt(phoneNumberTextfield.getText()), "Rakcha Admin", "your code is " + verificationCode);
                }
            }
        });
    }
}

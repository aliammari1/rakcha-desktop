package com.esprit.controllers.users;

import com.esprit.utils.UserSMSAPI;
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
import java.security.SecureRandom;
import java.util.ResourceBundle;

public class SMSAdminController implements Initializable {
    int verificationCode;
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
    public void sendSMS(final ActionEvent event) {
        if (this.verificationCode == Integer.parseInt(this.codeTextField.getText())) {
            try {
                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/ResetPasswordlogin.fxml"));
                final Parent root = loader.load();
                final Stage stage = (Stage) this.codeTextField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param location
     * @param resources
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.phoneNumberTextfield.textProperty()
                .addListener((final ObservableValue<? extends String> observable, final String oldValue,
                        final String newValue) -> {
                    if (8 == newValue.trim().length()) {
                        final SecureRandom random = new SecureRandom();
                        this.verificationCode = random.nextInt(999999 - 100000) + 100000;
                        UserSMSAPI.sendSMS(Integer.parseInt(this.phoneNumberTextfield.getText()), "Rakcha Admin",
                                "your code is " + this.verificationCode);
                    }
                });
    }
}

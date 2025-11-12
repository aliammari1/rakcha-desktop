package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;

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

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
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
     * Handle the verification-code submission; if the entered code matches the generated code,
     * replace the current scene with the ResetPasswordlogin view.
     *
     * @param event the UI action event that triggered this handler
     * @throws RuntimeException if loading the ResetPasswordlogin view fails
     */
    public void sendSMS(final ActionEvent event) {
        if (this.verificationCode == Integer.parseInt(this.codeTextField.getText())) {
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/users/ResetPasswordlogin.fxml"));
                final Parent root = loader.load();
                final Stage stage = (Stage) this.codeTextField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

        }

    }


    /**
     * Initialize the controller and attach a listener to the phone number field.
     *
     * <p>When the phoneNumberTextfield's trimmed text reaches exactly 8 characters, a 6-digit
     * verification code is generated, stored in the controller's {@code verificationCode}
     * field, and sent via {@code UserSMSAPI.sendSMS} to the entered phone number.</p>
     *
     * @param location  the location used to resolve relative paths for the root object, may be null
     * @param resources the resources used to localize the root object, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.phoneNumberTextfield.textProperty().addListener(
                (final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
                    if (8 == newValue.trim().length()) {
                        final SecureRandom random = new SecureRandom();
                        this.verificationCode = random.nextInt(999999 - 100000) + 100000;
                        UserSMSAPI.sendSMS(Integer.parseInt(this.phoneNumberTextfield.getText()), "Rakcha Admin",
                                "your code is " + this.verificationCode);
                    }

                }
);
    }

}

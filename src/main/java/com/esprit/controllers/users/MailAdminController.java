package com.esprit.controllers.users;

import java.security.SecureRandom;

import com.esprit.utils.UserMail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MailAdminController {
    @FXML
    private Label emailErrorLabel;
    @FXML
    private TextField mailTextField;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Button sendButton;

    /**
     * @param event
     */
    @FXML
    void sendMail(final ActionEvent event) {
        final SecureRandom random = new SecureRandom();
        final int verificationCode = random.nextInt(999999 - 100000) + 100000;
        UserMail.send(this.mailTextField.getText(), "the mail verification code is " + verificationCode);
    }

}


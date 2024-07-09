package com.esprit.controllers.users;
import com.esprit.utils.UserMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.Random;
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
    void sendMail(ActionEvent event) {
        Random random = new Random();
        int verificationCode = random.nextInt(999999 - 100000) + 100000;
        UserMail.send(mailTextField.getText(), "the mail verification code is " + verificationCode);
    }
}

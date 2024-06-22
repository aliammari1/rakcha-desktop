package com.esprit.controllers.users;
import com.esprit.utils.SignInGoogle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
public class VerifyWithGoogle {
    @FXML
    private TextField authTextField;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Hyperlink hyperlink;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Button sendButton;
    /** 
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void initialize() throws IOException, ExecutionException, InterruptedException {
        String link = SignInGoogle.signInWithGoogle();
        Desktop.getDesktop().browse(URI.create(link));
    }
    /** 
     * @param event
     */
    @FXML
    void verifyAuthCode(ActionEvent event) {
        try {
            SignInGoogle.verifyAuthUrl(authTextField.getText());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println("the auth is wrong");
        }
    }
}

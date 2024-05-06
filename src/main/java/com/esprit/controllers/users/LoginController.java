package com.esprit.controllers.users;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class LoginController implements Initializable {

    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private Hyperlink forgetPasswordEmailHyperlink;

    @FXML
    private Hyperlink forgetPasswordHyperlink;
    @FXML
    private WebView fireAnimationWebView;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button signInButton;

    @FXML
    private Button signUpButton;
    @FXML
    private Button googleSIgnInButton;
    @FXML
    private Button microsoftSignInButton;

    @FXML
    void signInWithGoogle(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VerifyWithGoogle.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) googleSIgnInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void signInWithMicrosoft(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VerifyWithMicrosoft.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) microsoftSignInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void login(ActionEvent event) throws IOException {
        UserService userService = new UserService();
        User user = userService.login(emailTextField.getText(), passwordTextField.getText());
        if (user != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "the user was found", ButtonType.CLOSE);
            alert.show();
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setUserData(user);
            if (user.getRole().equals("admin")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileAdmin.fxml"));
                Parent root = loader.load();
                ProfileController profileController = loader.getController();
                profileController.setData(user);
                stage.setScene(new Scene(root));
            } else if (user.getRole().equals("client")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileClient.fxml"));
                Parent root = loader.load();
                ProfileController profileController = loader.getController();
                profileController.setData(user);
                stage.setScene(new Scene(root));
            } else if (user.getRole().equals("responsable de cinema")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileResp.fxml"));
                Parent root = loader.load();
                ProfileController profileController = loader.getController();
                profileController.setData(user);
                stage.setScene(new Scene(root));
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
            alert.show();
        }
    }

    @FXML
    void switchToSignUp(ActionEvent event) throws IOException {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fireAnimationWebView.getEngine().load("https://particles.js.org/samples/presets/fire.html");
        forgetPasswordHyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/smsadmin.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) forgetPasswordHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        forgetPasswordEmailHyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/maillogin.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) forgetPasswordEmailHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

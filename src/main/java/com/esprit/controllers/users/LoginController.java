package com.esprit.controllers.users;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
public class LoginController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Hyperlink forgetPasswordEmailHyperlink;
    @FXML
    private Hyperlink forgetPasswordHyperlink;
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
    private AnchorPane loginAnchorPane;
    /** 
     * @param event
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
    /** 
     * @param event
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
            TrayNotification trayNotification = new TrayNotification("users", "the user found", Notifications.SUCCESS);
            trayNotification.showAndDismiss(new Duration(3000));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "the user was found", ButtonType.CLOSE);
            alert.show();
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setUserData(user);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent root = loader.load();
            FXMLLoader loaderSideBar = null;
            ProfileController profileController = loader.getController();
            if (user.getRole().equals("admin")) {
                loaderSideBar = new FXMLLoader(getClass().getResource("/adminSideBar.fxml"));
            } else if (user.getRole().equals("client")) {
                loaderSideBar = new FXMLLoader(getClass().getResource("/clientSideBar.fxml"));
            } else if (user.getRole().equals("responsable de cinema")) {
                loaderSideBar = new FXMLLoader(getClass().getResource("/responsableDeCinemaSideBar.fxml"));
            }
            if (loaderSideBar != null) {
                profileController.setLeftPane(loaderSideBar.load());
            }
            profileController.setData(user);
            stage.setScene(new Scene(root));
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

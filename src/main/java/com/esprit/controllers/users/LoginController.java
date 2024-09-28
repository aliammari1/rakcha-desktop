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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
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
    void signInWithGoogle(final ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/VerifyWithGoogle.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.googleSIgnInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            LoginController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param event
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void signInWithMicrosoft(final ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/VerifyWithMicrosoft.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.microsoftSignInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            LoginController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    void login(final ActionEvent event) throws IOException {
        final UserService userService = new UserService();
        final User user = userService.login(this.emailTextField.getText(), this.passwordTextField.getText());
        if (null != user) {
            final TrayNotification trayNotification = new TrayNotification("users", "the user found", Notifications.SUCCESS);
            trayNotification.showAndDismiss(new Duration(3000));
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "the user was found", ButtonType.CLOSE);
            alert.show();
            final Stage stage = (Stage) this.signInButton.getScene().getWindow();
            stage.setUserData(user);
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Profile.fxml"));
            final Parent root = loader.load();
            FXMLLoader loaderSideBar = null;
            final ProfileController profileController = loader.getController();
            if ("admin".equals(user.getRole())) {
                loaderSideBar = new FXMLLoader(this.getClass().getResource("/adminSideBar.fxml"));
            } else if ("client".equals(user.getRole())) {
                loaderSideBar = new FXMLLoader(this.getClass().getResource("/clientSideBar.fxml"));
            } else if ("responsable de cinema".equals(user.getRole())) {
                loaderSideBar = new FXMLLoader(this.getClass().getResource("/responsableDeCinemaSideBar.fxml"));
            }
            if (null != loaderSideBar) {
                profileController.setLeftPane(loaderSideBar.load());
            }
            profileController.setData(user);
            stage.setScene(new Scene(root));
        } else {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
            alert.show();
        }
    }

    @FXML
    void switchToSignUp(final ActionEvent event) throws IOException {
        final Stage stage = (Stage) this.signUpButton.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/SignUp.fxml"));
        final Parent root = loader.load();
        stage.setScene(new Scene(root));
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.forgetPasswordHyperlink.setOnAction(new EventHandler<>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/smsadmin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) LoginController.this.forgetPasswordHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.forgetPasswordEmailHyperlink.setOnAction(new EventHandler<>() {
            @Override
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/maillogin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) LoginController.this.forgetPasswordEmailHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

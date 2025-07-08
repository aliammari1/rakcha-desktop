package com.esprit.controllers.users;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
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
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/VerifyWithGoogle.fxml"));
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
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/VerifyWithMicrosoft.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.microsoftSignInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            LoginController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /** 
     * @param event
     * @throws IOException
     */
    @FXML
    void login(final ActionEvent event) throws IOException {
        final UserService userService = new UserService();
        final User user = userService.login(this.emailTextField.getText(), this.passwordTextField.getText());
        if (null != user) {
            try {
                final TrayNotification trayNotification = new TrayNotification("users", "the user found",
                        Notifications.SUCCESS);
                trayNotification.showAndDismiss(new Duration(3000));

                final Stage stage = (Stage) this.signInButton.getScene().getWindow();
                stage.setUserData(user);

                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
                final Parent root = loader.load();

                FXMLLoader loaderSideBar = null;
                final ProfileController profileController = loader.getController();

                // Load appropriate sidebar based on user role
                if ("admin".equals(user.getRole())) {
                    loaderSideBar = new FXMLLoader(this.getClass().getResource("/ui/adminSideBar.fxml"));
                } else if ("client".equals(user.getRole())) {
                    loaderSideBar = new FXMLLoader(this.getClass().getResource("/ui/clientSideBar.fxml"));
                } else if ("responsable de cinema".equals(user.getRole())) {
                    loaderSideBar = new FXMLLoader(this.getClass().getResource("/ui/cinemaManagerSideBar.fxml"));
                }

                if (null != loaderSideBar) {
                    try {
                        profileController.setLeftPane(loaderSideBar.load());
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error loading sidebar", e);
                        throw e;
                    }
                }

                try {
                    profileController.setData(user);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Error setting user data: " + e.getMessage());
                    // Continue even if profile image fails to load
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during login process", e);
                throw new IOException("Failed to complete login process", e);
            }
        } else {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "the user was not found", ButtonType.CLOSE);
            alert.show();
        }
    }

    /** 
     * @param event
     * @throws IOException
     */
    @FXML
    void switchToSignUp(ActionEvent event) throws IOException {
        try {
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/SignUp.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error switching to signup view", e);
            throw e;
        }
    }

    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.forgetPasswordHyperlink.setOnAction(new EventHandler<>() {
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/smsadmin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) forgetPasswordHyperlink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.forgetPasswordEmailHyperlink.setOnAction(new EventHandler<>() {
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final ActionEvent event) {
                try {
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/maillogin.fxml"));
                    final Parent root = loader.load();
                    final Stage stage = (Stage) forgetPasswordEmailHyperlink.getScene()
                            .getWindow();
                    stage.setScene(new Scene(root));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Ensure signUpButton has its event handler
        signUpButton.setOnAction(event -> {
            try {
                switchToSignUp(event);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error switching to signup view", e);
            }
        });
    }
}

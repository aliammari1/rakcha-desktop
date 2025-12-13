package com.esprit.controllers.users;

import com.esprit.enums.UserRole;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import com.esprit.utils.SignInGoogle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerifyWithGoogle {

    private static final Logger LOGGER = Logger.getLogger(VerifyWithGoogle.class.getName());

    @FXML
    private TextField verification_code_textField;
    @FXML
    private Label verification_error_label;
    @FXML
    private Button verifyButton;

    /**
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @FXML
    void initialize() throws IOException, ExecutionException, InterruptedException {
        final String link = SignInGoogle.signInWithGoogle();
        openWebpage(link);
    }

    /**
     * Opens a URL in the system browser with cross-platform support
     */
    private void openWebpage(String url) {
        try {
            // Try Desktop API first (Windows/Mac/Some Linux)
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                return;
            }
        } catch (Exception e) {
            // Ignore and fall back
        }

        // Fallback for Linux/Unix (xdg-open)
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("xdg-open", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                VerifyWithGoogle.LOGGER.warning("Could not open browser: Desktop not supported and no fallback found.");
                VerifyWithGoogle.LOGGER.info("Please open the following URL manually: " + url);
                System.out.println("Please open the following URL manually: " + url);
            }
        } catch (IOException e) {
            VerifyWithGoogle.LOGGER.warning(
                    "Browser automation failed (xdg-open/open not found). This is expected on some Linux environments.");
            VerifyWithGoogle.LOGGER.info("Please open the following URL manually: " + url);
            System.out.println("Please open the following URL manually: " + url);
        }
    }

    /**
     * @param event
     */
    @FXML
    void verifyAuthCode(final ActionEvent event) {
        try {
            String authCode = this.verification_code_textField.getText().trim();

            if (authCode.isEmpty()) {
                VerifyWithGoogle.LOGGER.warning("Authorization code is empty");
                this.verification_error_label.setText("Please enter the authorization code");
                return;
            }

            boolean verified = SignInGoogle.verifyAuthUrl(authCode);

            if (verified) {
                // Get the user information from Google
                Map<String, Object> googleUserInfo = SignInGoogle.getLastUserInfo();

                if (googleUserInfo != null) {
                    // Create or get user from database
                    User user = createOrGetUserFromGoogle(googleUserInfo);

                    // Set the session user
                    SessionManager.setCurrentUser(user);

                    // Load the Profile FXML
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
                    final Parent root = loader.load();

                    // Get the controller and set the user data
                    ProfileController controller = loader.getController();
                    controller.setData(user);

                    final Stage stage = (Stage) this.verifyButton.getScene().getWindow();
                    stage.setScene(new Scene(root));

                    // Clear the stored user info
                    SignInGoogle.clearUserInfo();
                } else {
                    VerifyWithGoogle.LOGGER.warning("No user information retrieved from Google");
                    this.verification_error_label.setText("Could not retrieve user information. Please try again.");
                }
            } else {
                VerifyWithGoogle.LOGGER.warning("Authentication verification failed");
                this.verification_error_label.setText("Invalid authorization code. Please try again.");
            }
        } catch (final Exception e) {
            VerifyWithGoogle.LOGGER.log(Level.SEVERE, "Error during authentication", e);
            this.verification_error_label.setText("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Creates a new user or retrieves an existing user from the database based on
     * Google info
     *
     * @param googleUserInfo The user information from Google
     * @return The User object
     */
    private User createOrGetUserFromGoogle(Map<String, Object> googleUserInfo) {
        String email = (String) googleUserInfo.get("email");
        String name = (String) googleUserInfo.get("name");
        String picture = (String) googleUserInfo.get("picture");

        UserService userService = new UserService();

        // Try to find existing user by email
        User user = userService.getUserByEmail(email);

        if (user == null) {
            // Create new client user
            user = new Client();
            user.setEmail(email);
            user.setRole(UserRole.CLIENT); // Set default role for Google OAuth users

            // Split name into first and last name
            if (name != null && !name.isEmpty()) {
                String[] nameParts = name.split(" ", 2);
                user.setFirstName(nameParts[0]);
                if (nameParts.length > 1) {
                    user.setLastName(nameParts[1]);
                } else {
                    user.setLastName(""); // Set empty last name if not provided
                }
            } else {
                user.setFirstName("");
                user.setLastName("");
            }

            // Set profile picture from Google
            if (picture != null) {
                user.setProfilePictureUrl(picture);
            }

            // Generate a temporary password (user authenticated via Google, so they don't
            // need this)
            user.setPasswordHash("google-oauth-" + System.currentTimeMillis());

            // Set default phone number (required by schema)
            user.setPhoneNumber("12345678");

            // Save the new user
            try {
                userService.create(user);
                VerifyWithGoogle.LOGGER.info("New user created from Google login: " + email);

                // Retrieve the newly created user to get the generated ID
                user = userService.getUserByEmail(email);
                if (user != null) {
                    VerifyWithGoogle.LOGGER.info("Retrieved user ID: " + user.getId());
                }
            } catch (Exception e) {
                VerifyWithGoogle.LOGGER.log(Level.WARNING, "Failed to save new user", e);
            }
        } else {
            // Update existing user with latest Google profile picture if available
            if (picture != null && (user.getProfilePictureUrl() == null || user.getProfilePictureUrl().isEmpty())) {
                user.setProfilePictureUrl(picture);
                try {
                    userService.update(user);
                } catch (Exception e) {
                    VerifyWithGoogle.LOGGER.log(Level.WARNING, "Failed to update user profile picture", e);
                }
            }
            VerifyWithGoogle.LOGGER.info("Existing user found: " + email);
        }

        return user;
    }
}

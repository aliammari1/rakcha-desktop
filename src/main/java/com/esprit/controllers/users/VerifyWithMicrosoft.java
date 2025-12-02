package com.esprit.controllers.users;

import com.esprit.enums.UserRole;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.SignInMicrosoft;
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
public class VerifyWithMicrosoft {

    private static final Logger LOGGER = Logger.getLogger(VerifyWithMicrosoft.class.getName());

    @FXML
    private TextField authTextField;
    @FXML
    private Label emailErrorLabel;
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
        final String link = SignInMicrosoft.SignInWithMicrosoft();
        Desktop.getDesktop().browse(URI.create(link));
    }

    /**
     * @param event
     */
    @FXML
    void verifyAuthCode(final ActionEvent event) {
        try {
            String authCode = this.authTextField.getText().trim();

            if (authCode.isEmpty()) {
                VerifyWithMicrosoft.LOGGER.warning("Authorization code is empty");
                this.emailErrorLabel.setText("Please enter the authorization code");
                return;
            }

            boolean verified = SignInMicrosoft.verifyAuthUrl(authCode);

            if (verified) {
                // Get the user information from Microsoft
                Map<String, Object> microsoftUserInfo = SignInMicrosoft.getLastUserInfo();

                if (microsoftUserInfo != null) {
                    // Create or get user from database
                    User user = createOrGetUserFromMicrosoft(microsoftUserInfo);

                    // Load the Profile FXML
                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
                    final Parent root = loader.load();

                    // Get the controller and set the user data
                    ProfileController controller = loader.getController();
                    controller.setData(user);

                    final Stage stage = (Stage) this.sendButton.getScene().getWindow();
                    stage.setScene(new Scene(root));

                    // Clear the stored user info
                    SignInMicrosoft.clearUserInfo();
                } else {
                    VerifyWithMicrosoft.LOGGER.warning("No user information retrieved from Microsoft");
                    this.emailErrorLabel.setText("Could not retrieve user information. Please try again.");
                }
            } else {
                VerifyWithMicrosoft.LOGGER.warning("Authentication verification failed");
                this.emailErrorLabel.setText("Invalid authorization code. Please try again.");
            }
        } catch (final Exception e) {
            VerifyWithMicrosoft.LOGGER.log(Level.SEVERE, "Error during authentication", e);
            this.emailErrorLabel.setText("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Creates a new user or retrieves an existing user from the database based on Microsoft info
     *
     * @param microsoftUserInfo The user information from Microsoft
     * @return The User object
     */
    private User createOrGetUserFromMicrosoft(Map<String, Object> microsoftUserInfo) {
        // Microsoft returns emails as an object with 'preferred' and 'account' fields
        String email = null;
        Object emailsObj = microsoftUserInfo.get("emails");
        if (emailsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> emails = (Map<String, Object>) emailsObj;
            email = (String) emails.get("preferred");
            if (email == null) {
                email = (String) emails.get("account");
            }
        }

        String name = (String) microsoftUserInfo.get("name");
        String id = (String) microsoftUserInfo.get("id");

        if (email == null) {
            // Fallback to using id as email if no email provided
            email = id + "@microsoft.live.com";
            VerifyWithMicrosoft.LOGGER.warning("No email found in Microsoft response, using ID: " + email);
        }

        UserService userService = new UserService();

        // Try to find existing user by email
        User user = userService.getUserByEmail(email);

        if (user == null) {
            // Create new client user
            user = new Client();
            user.setEmail(email);
            user.setRole(UserRole.CLIENT); // Set default role for Microsoft OAuth users

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

            // Generate a temporary password (user authenticated via Microsoft, so they don't need this)
            user.setPasswordHash("microsoft-oauth-" + System.currentTimeMillis());

            // Set default phone number (required by schema)
            user.setPhoneNumber("12345678");

            // Save the new user
            try {
                userService.create(user);
                VerifyWithMicrosoft.LOGGER.info("New user created from Microsoft login: " + email);

                // Retrieve the newly created user to get the generated ID
                user = userService.getUserByEmail(email);
                if (user != null) {
                    VerifyWithMicrosoft.LOGGER.info("Retrieved user ID: " + user.getId());
                }
            } catch (Exception e) {
                VerifyWithMicrosoft.LOGGER.log(Level.WARNING, "Failed to save new user", e);
            }
        } else {
            VerifyWithMicrosoft.LOGGER.info("Existing user found: " + email);
        }

        return user;
    }
}

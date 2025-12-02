package com.esprit.controllers.users;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for user account settings and profile management.
 */
public class AccountSettingsController {

    private static final Logger LOGGER = Logger.getLogger(AccountSettingsController.class.getName());
    private final UserService userService;
    @FXML
    private VBox settingsContainer;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label usernameDisplayLabel;
    @FXML
    private Label emailDisplayLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker birthdatePicker;
    @FXML
    private TextArea bioField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ProgressBar passwordStrength;
    @FXML
    private Label passwordHint;
    @FXML
    private CheckBox emailNotificationsCheck;
    @FXML
    private CheckBox pushNotificationsCheck;
    @FXML
    private CheckBox smsNotificationsCheck;
    @FXML
    private CheckBox marketingEmailsCheck;
    @FXML
    private ComboBox<String> languageCombo;
    @FXML
    private ComboBox<String> themeCombo;
    @FXML
    private CheckBox autoPlayTrailersCheck;
    @FXML
    private CheckBox showMatureContentCheck;
    @FXML
    private CheckBox twoFactorAuthCheck;
    @FXML
    private CheckBox loginAlertsCheck;
    @FXML
    private Button saveProfileButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private ProgressIndicator loadingIndicator;
    private User currentUser;
    private File selectedProfileImage;

    public AccountSettingsController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing AccountSettingsController");

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupComboBoxes();
        setupPasswordStrengthChecker();
        loadUserData();
    }

    private void setupComboBoxes() {
        if (languageCombo != null) {
            languageCombo.getItems().addAll("English", "French", "Arabic", "Spanish", "German");
        }
    }

    private void setupPasswordStrengthChecker() {
        if (newPasswordField != null) {
            newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                updatePasswordStrength(newVal);
            });
        }
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrength.setProgress(0);
            passwordHint.setText("Enter a password");
            return;
        }

        double strength = 0;
        if (password.length() >= 8) strength += 0.25;
        if (password.matches(".*[A-Z].*")) strength += 0.25;
        if (password.matches(".*[0-9].*")) strength += 0.25;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength += 0.25;

        passwordStrength.setProgress(strength);

        if (strength <= 0.25) {
            passwordHint.setText("Weak");
            passwordStrength.setStyle("-fx-accent: #ef4444;");
        } else if (strength <= 0.5) {
            passwordHint.setText("Fair");
            passwordStrength.setStyle("-fx-accent: #f59e0b;");
        } else if (strength <= 0.75) {
            passwordHint.setText("Good");
            passwordStrength.setStyle("-fx-accent: #3b82f6;");
        } else {
            passwordHint.setText("Strong");
            passwordStrength.setStyle("-fx-accent: #22c55e;");
        }
    }

    private void loadUserData() {
        if (currentUser == null) return;

        if (usernameDisplayLabel != null) usernameDisplayLabel.setText(currentUser.getUsername());
        if (emailDisplayLabel != null) emailDisplayLabel.setText(currentUser.getEmail());

        if (firstNameField != null) firstNameField.setText(currentUser.getFirstName());
        if (lastNameField != null) lastNameField.setText(currentUser.getLastName());
        if (emailField != null) emailField.setText(currentUser.getEmail());
        if (phoneField != null) phoneField.setText(currentUser.getPhoneNumber());

        // Load profile image
        if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
            try {
                if (profileImage != null) profileImage.setImage(new Image(currentUser.getProfilePicture()));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load profile image", e);
            }
        }

        // Load preferences with defaults
        if (languageCombo != null) languageCombo.setValue("English");
    }

    @FXML
    private void handleChangeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(settingsContainer.getScene().getWindow());

        if (file != null) {
            selectedProfileImage = file;
            profileImage.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleSaveProfile() {
        if (!validateProfileForm()) return;

        showLoading(true);

        new Thread(() -> {
            try {
                currentUser.setFirstName(firstNameField.getText().trim());
                currentUser.setLastName(lastNameField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                if (phoneField != null) currentUser.setPhoneNumber(phoneField.getText().trim());

                userService.update(currentUser);

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Profile updated successfully!");
                    SessionManager.setCurrentUser(currentUser);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating profile", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to update profile. Please try again.");
                });
            }
        }).start();
    }

    private boolean validateProfileForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required.");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required.");
            return false;
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showError("Valid email is required.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleChangePassword() {
        String currentPass = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("All password fields are required.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("New passwords do not match.");
            return;
        }

        if (newPass.length() < 8) {
            showError("Password must be at least 8 characters.");
            return;
        }

        showLoading(true);

        new Thread(() -> {
            try {
                boolean success = userService.changePassword(currentUser.getId(), currentPass, newPass);

                Platform.runLater(() -> {
                    showLoading(false);
                    if (success) {
                        showSuccess("Password changed successfully!");
                        currentPasswordField.clear();
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                    } else {
                        showError("Current password is incorrect.");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error changing password", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to change password.");
                });
            }
        }).start();
    }

    @FXML
    private void handleSaveNotifications() {
        showSuccess("Notification preferences saved!");
    }

    @FXML
    private void handleSavePreferences() {
        showSuccess("Preferences saved!");
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Are you sure you want to delete your account?");
        confirm.setContentText("This action cannot be undone. All your data will be permanently deleted.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Show password confirmation dialog
                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Confirm Deletion");
                passwordDialog.setHeaderText("Enter your password to confirm");
                passwordDialog.showAndWait().ifPresent(password -> {
                    deleteAccount(password);
                });
            }
        });
    }

    private void deleteAccount(String password) {
        showLoading(true);

        new Thread(() -> {
            try {
                boolean success = userService.deleteAccount(currentUser.getId(), password);

                Platform.runLater(() -> {
                    showLoading(false);
                    if (success) {
                        SessionManager.getInstance().logout();
                        navigateToLogin();
                    } else {
                        showError("Incorrect password or deletion failed.");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting account", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to delete account.");
                });
            }
        }).start();
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) settingsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to login", e);
        }
    }

    @FXML
    private void showProfile() {
        // Show profile section
        LOGGER.info("Showing profile section");
    }

    @FXML
    private void showSecurity() {
        // Show security section
        LOGGER.info("Showing security section");
    }

    @FXML
    private void showNotifications() {
        // Show notifications section
        LOGGER.info("Showing notifications section");
    }

    @FXML
    private void showPrivacy() {
        // Show privacy section
        LOGGER.info("Showing privacy section");
    }

    @FXML
    private void showConnected() {
        // Show connected accounts section
        LOGGER.info("Showing connected accounts section");
    }

    @FXML
    private void showPreferences() {
        // Show preferences section
        LOGGER.info("Showing preferences section");
    }

    @FXML
    private void showData() {
        // Show data section
        LOGGER.info("Showing data section");
    }

    @FXML
    private void deleteAccount() {
        // Delete account
        LOGGER.info("Delete account");
    }

    @FXML
    private void changeAvatar() {
        LOGGER.info("Change avatar");
    }

    @FXML
    private void verifyEmail() {
        LOGGER.info("Verify email");
    }

    @FXML
    private void cancelChanges() {
        LOGGER.info("Cancel changes");
    }

    @FXML
    private void saveProfile() {
        LOGGER.info("Save profile");
    }

    @FXML
    private void updatePassword() {
        LOGGER.info("Update password");
    }

    @FXML
    private void logoutAllSessions() {
        LOGGER.info("Logout all sessions");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) settingsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

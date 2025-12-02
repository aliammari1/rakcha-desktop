package com.esprit.controllers.users;

import com.esprit.utils.UserMail;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Controller for the Cinematic Email Composer UI.
 * Provides a film-like interface for composing and sending professional emails.
 * Supports both HTML and plain text email formats with real-time preview.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 2.0.0
 */
public class CinematicEmailComposerController {

    private static final Logger LOGGER = Logger.getLogger(CinematicEmailComposerController.class.getName());
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final int MAX_CHARACTERS = 5000;

    @FXML
    private TextField recipientEmailField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageContentArea;
    @FXML
    private ComboBox<String> emailTypeCombo;
    @FXML
    private TextArea previewArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label recipientErrorLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label charCountLabel;

    /**
     * Initializes the controller and sets up event listeners.
     */
    @FXML
    public void initialize() {
        setupEmailTypeCombo();
        setupEventListeners();
        applyAnimations();
    }

    /**
     * Sets up the email type combo box with available formats.
     */
    private void setupEmailTypeCombo() {
        emailTypeCombo.getItems().addAll("Plain Text", "HTML");
        emailTypeCombo.setValue("Plain Text");
        emailTypeCombo.setOnAction(e -> updatePreview());
    }

    /**
     * Sets up event listeners for real-time validation and preview updates.
     */
    private void setupEventListeners() {
        // Real-time character count
        messageContentArea.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCharCount();
            updatePreview();
        });

        // Subject preview update
        subjectField.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());

        // Email type change
        emailTypeCombo.setOnAction(e -> updatePreview());

        // Clear button action
        clearButton.setOnAction(this::clearForm);

        // Send button action
        sendButton.setOnAction(this::sendEmail);

        // Email validation on focus lost
        recipientEmailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateEmail();
            }
        });
    }

    /**
     * Updates the character count display.
     */
    private void updateCharCount() {
        int count = messageContentArea.getText().length();
        charCountLabel.setText(count + " / " + MAX_CHARACTERS + " characters");

        if (count > MAX_CHARACTERS) {
            messageContentArea.deleteText(MAX_CHARACTERS, count);
            charCountLabel.setStyle("-fx-text-fill: #ff4444;");
        } else if (count > MAX_CHARACTERS * 0.8) {
            charCountLabel.setStyle("-fx-text-fill: #ffaa00;");
        } else {
            charCountLabel.setStyle("-fx-text-fill: #666666;");
        }
    }

    /**
     * Updates the preview area with formatted content.
     */
    private void updatePreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("TO: ").append(recipientEmailField.getText().isEmpty() ? "[recipient email]" : recipientEmailField.getText()).append("\n\n");
        preview.append("SUBJECT: ").append(subjectField.getText().isEmpty() ? "[subject line]" : subjectField.getText()).append("\n\n");
        preview.append("FORMAT: ").append(emailTypeCombo.getValue()).append("\n\n");
        preview.append("---\n\n");
        preview.append(messageContentArea.getText().isEmpty() ? "[message content]" : messageContentArea.getText());

        previewArea.setText(preview.toString());
    }

    /**
     * Validates the recipient email address format.
     *
     * @return true if email is valid, false otherwise
     */
    private boolean validateEmail() {
        String email = recipientEmailField.getText().trim();

        if (email.isEmpty()) {
            recipientErrorLabel.setText("Email address is required");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            recipientErrorLabel.setText("Invalid email format");
            return false;
        }

        recipientErrorLabel.setText("");
        return true;
    }

    /**
     * Validates all form fields before sending.
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        // Validate recipient email
        if (!validateEmail()) {
            return false;
        }

        // Validate subject
        if (subjectField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a subject line");
            return false;
        }

        // Validate message content
        if (messageContentArea.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter message content");
            return false;
        }

        return true;
    }

    /**
     * Clears all form fields and resets the form.
     *
     * @param event the action event triggered by the clear button
     */
    @FXML
    private void clearForm(final ActionEvent event) {
        // Animate the clear action
        FadeTransition fade = new FadeTransition(Duration.millis(200), messageContentArea);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setOnFinished(e -> {
            recipientEmailField.clear();
            subjectField.clear();
            messageContentArea.clear();
            previewArea.clear();
            emailTypeCombo.setValue("Plain Text");
            recipientErrorLabel.setText("");
            statusLabel.setText("");
            charCountLabel.setText("0 / " + MAX_CHARACTERS + " characters");

            // Fade back in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), messageContentArea);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fade.play();
    }

    /**
     * Sends the email with the current form data.
     *
     * @param event the action event triggered by the send button
     */
    @FXML
    private void sendEmail(final ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        // Disable send button and show loading state
        sendButton.setDisable(true);
        statusLabel.setText("Sending email...");
        statusLabel.setStyle("-fx-text-fill: #ffaa00;");

        // Send email in a background thread
        new Thread(() -> {
            try {
                String recipient = recipientEmailField.getText().trim();
                String subject = subjectField.getText().trim();
                String message = messageContentArea.getText();
                boolean isHtml = "HTML".equals(emailTypeCombo.getValue());

                // Send the email
                if (isHtml) {
                    UserMail.sendHtml(recipient, subject, message);
                } else {
                    UserMail.send(recipient, subject, message);
                }

                // Update UI on success
                Platform.runLater(() -> {
                    showSuccessAnimation();
                    statusLabel.setText("✓ Email sent successfully!");
                    statusLabel.setStyle("-fx-text-fill: #00cc00;");

                    // Show success alert
                    showAlert("Success", "Email sent successfully to " + recipient);

                    // Clear the form after successful send
                    clearForm(null);
                    sendButton.setDisable(false);
                });

            } catch (Exception e) {
                // Update UI on error
                Platform.runLater(() -> {
                    LOGGER.log(Level.SEVERE, "Error sending email: " + e.getMessage(), e);
                    statusLabel.setText("✗ Failed to send email: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #ff4444;");
                    showAlert("Error", "Failed to send email:\n" + e.getMessage());
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }

    /**
     * Displays a success animation on the send button.
     */
    private void showSuccessAnimation() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), sendButton);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    /**
     * Shows an alert dialog to the user.
     *
     * @param title   the alert title
     * @param message the alert message
     */
    private void showAlert(final String title, final String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Applies cinematic animations to UI elements.
     */
    private void applyAnimations() {
        // Add hover effect to send button (optional enhancement)
        sendButton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), sendButton);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        sendButton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), sendButton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
}

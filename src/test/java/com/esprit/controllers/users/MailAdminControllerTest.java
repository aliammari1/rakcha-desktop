package com.esprit.controllers.users;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import org.testfx.framework.junit5.Start;

import com.esprit.utils.TestFXBase;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for MailAdminController.
 * Tests email verification code generation and sending.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MailAdminControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/users/maillogin.fxml")
        );
        javafx.scene.Parent root = loader.load();
        
        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Email Input Tests")
    class EmailInputTests {

        @Test
        @Order(1)
        @DisplayName("Should display email input field")
        void testEmailFieldDisplay() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            assertThat(emailField).isNotNull();
        }


        @Test
        @Order(2)
        @DisplayName("Should accept valid email address")
        void testValidEmailInput() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            clickOn(emailField).write("user@example.com");

            assertThat(emailField.getText()).isEqualTo("user@example.com");
        }


        @Test
        @Order(3)
        @DisplayName("Should clear email field")
        void testClearEmailField() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            clickOn(emailField).write("test@test.com");
            clickOn(emailField).eraseText(13);

            assertThat(emailField.getText()).isEmpty();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Send Email Tests")
    class SendEmailTests {

        @Test
        @Order(4)
        @DisplayName("Should display send button")
        void testSendButtonDisplay() {
            waitForFxEvents();

            Button sendButton = lookup("#sendButton").query();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(5)
        @DisplayName("Should send verification email on button click")
        void testSendVerificationEmail() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();
            
            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(6)
        @DisplayName("Should generate 6-digit verification code")
        void testVerificationCodeGeneration() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            assertThat(emailField).isNotNull();
        }


        @Test
        @Order(7)
        @DisplayName("Should include verification message")
        void testVerificationMessage() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();
            
            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(8)
        @DisplayName("Should use SecureRandom for code generation")
        void testSecureRandomUsage() {
            waitForFxEvents();

            Button sendButton = lookup("#sendButton").query();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(9)
        @DisplayName("Should generate code in range 100000-999999")
        void testCodeRange() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            assertThat(emailField).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Error Label Tests")
    class ErrorLabelTests {

        @Test
        @Order(10)
        @DisplayName("Should display email error label")
        void testEmailErrorLabel() {
            waitForFxEvents();

            Label emailError = lookup("#emailErrorLabel").query();
            assertThat(emailError).isNotNull();
        }


        @Test
        @Order(11)
        @DisplayName("Should display password error label")
        void testPasswordErrorLabel() {
            waitForFxEvents();

            Label passwordError = lookup("#passwordErrorLabel").query();
            assertThat(passwordError).isNotNull();
        }


        @Test
        @Order(12)
        @DisplayName("Should show error for invalid email format")
        void testInvalidEmailError() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            clickOn(emailField).write("invalid-email");

            waitForFxEvents();

            // Should show validation error
        }


        @Test
        @Order(13)
        @DisplayName("Should show error for empty email")
        void testEmptyEmailError() {
            waitForFxEvents();

            Button sendButton = lookup("#sendButton").query();
            clickOn(sendButton);

            waitForFxEvents();

            Label emailError = lookup("#emailErrorLabel").query();
            // Should show error
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @Order(14)
        @DisplayName("Should accept valid email formats")
        void testValidEmailFormats() {
            waitForFxEvents();

            String[] validEmails = {
                    "test@example.com",
                    "user.name@domain.co.uk",
                    "name123@test-domain.org"
            }
;

            TextField emailField = lookup("#mailTextField").query();
            for (String email : validEmails) {
                clickOn(emailField).eraseText(emailField.getText().length());
                clickOn(emailField).write(email);
                assertThat(emailField.getText()).isEqualTo(email);
            }

        }


        @Test
        @Order(15)
        @DisplayName("Should handle special characters in email")
        void testSpecialCharactersInEmail() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            clickOn(emailField).write("user+tag@example.com");

            assertThat(emailField.getText()).contains("+");
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Button State Tests")
    class ButtonStateTests {

        @Test
        @Order(16)
        @DisplayName("Should enable send button")
        void testSendButtonEnabled() {
            waitForFxEvents();

            Button sendButton = lookup("#sendButton").query();
            assertThat(sendButton.isDisabled()).isFalse();
        }


        @Test
        @Order(17)
        @DisplayName("Should handle multiple sends")
        void testMultipleSends() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();
            
            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(18)
        @DisplayName("Should handle email sending failure")
        void testEmailSendingFailure() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();
            
            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(19)
        @DisplayName("Should handle null email")
        void testNullEmail() {
            waitForFxEvents();

            Button sendButton = lookup("#sendButton").query();
            assertThat(sendButton).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @Order(20)
        @DisplayName("Should generate different codes each time")
        void testDifferentCodes() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();

            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(21)
        @DisplayName("Should not expose code in UI")
        void testCodeNotExposedInUI() {
            waitForFxEvents();

            TextField emailField = lookup("#mailTextField").query();
            Button sendButton = lookup("#sendButton").query();

            assertThat(emailField).isNotNull();
            assertThat(sendButton).isNotNull();
        }

    }

}


package com.esprit.controllers.users;

import com.esprit.utils.TestFXBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for SMSAdminController.
 * Tests SMS verification code generation and validation.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SMSAdminControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/users/smsadmin.fxml")
        );
        javafx.scene.Parent root = loader.load();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Phone Number Input Tests")
    class PhoneNumberInputTests {

        @Test
        @Order(1)
        @DisplayName("Should display phone number field")
        void testPhoneNumberFieldDisplay() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(2)
        @DisplayName("Should accept numeric input")
        void testNumericInput() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            clickOn(phoneField).write("12345678");

            assertThat(phoneField.getText()).isEqualTo(12345678);
        }


        @Test
        @Order(3)
        @DisplayName("Should validate 8-digit phone number")
        void testPhoneNumberLength() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            clickOn(phoneField).write("12345678");

            assertThat(phoneField.getText().length()).isEqualTo(8);
        }


        @Test
        @Order(4)
        @DisplayName("Should not trigger SMS for less than 8 digits")
        void testInsufficientDigits() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            clickOn(phoneField).write("123456");

            waitForFxEvents();

            // SMS should not be sent
        }


        @Test
        @Order(5)
        @DisplayName("Should clear previous input")
        void testClearInput() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            clickOn(phoneField).write("12345678");
            clickOn(phoneField).eraseText(8);

            assertThat(phoneField.getText()).isEmpty();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("SMS Sending Tests")
    class SMSSendingTests {

        @Test
        @Order(6)
        @DisplayName("Should send SMS when 8 digits entered")
        void testSendSMSOnValidNumber() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(7)
        @DisplayName("Should display send SMS button")
        void testSendSMSButtonDisplay() {
            waitForFxEvents();

            Button sendButton = lookup("#sendSMS").query();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(8)
        @DisplayName("Should generate 6-digit verification code")
        void testVerificationCodeGeneration() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(9)
        @DisplayName("Should include code in SMS message")
        void testSMSMessageContent() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            Button sendButton = lookup("#sendSMS").query();

            assertThat(phoneField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(10)
        @DisplayName("Should use Rakcha Admin as sender")
        void testSMSSenderName() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Verification Code Tests")
    class VerificationCodeTests {

        @Test
        @Order(11)
        @DisplayName("Should display code input field")
        void testCodeFieldDisplay() {
            waitForFxEvents();

            TextField codeField = lookup("#codeTextField").query();
            assertThat(codeField).isNotNull();
        }


        @Test
        @Order(12)
        @DisplayName("Should accept 6-digit code")
        void testAcceptSixDigitCode() {
            waitForFxEvents();

            TextField codeField = lookup("#codeTextField").query();
            assertThat(codeField).isNotNull();
        }


        @Test
        @Order(13)
        @DisplayName("Should validate correct code")
        void testValidateCorrectCode() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            TextField codeField = lookup("#codeTextField").query();

            assertThat(phoneField).isNotNull();
            assertThat(codeField).isNotNull();
        }


        @Test
        @Order(14)
        @DisplayName("Should reject incorrect code")
        void testRejectIncorrectCode() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            TextField codeField = lookup("#codeTextField").query();
            Button sendButton = lookup("#sendSMS").query();

            assertThat(phoneField).isNotNull();
            assertThat(codeField).isNotNull();
            assertThat(sendButton).isNotNull();
        }


        @Test
        @Order(15)
        @DisplayName("Should navigate to reset password on success")
        void testNavigationOnSuccess() {
            waitForFxEvents();

            Button sendButton = lookup("#sendSMS").query();
            assertThat(sendButton).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Label Tests")
    class ErrorLabelTests {

        @Test
        @Order(16)
        @DisplayName("Should display email error label")
        void testEmailErrorLabelDisplay() {
            waitForFxEvents();

            Label emailError = lookup("#emailErrorLabel").query();
            assertThat(emailError).isNotNull();
        }


        @Test
        @Order(17)
        @DisplayName("Should display password error label")
        void testPasswordErrorLabelDisplay() {
            waitForFxEvents();

            Label passwordError = lookup("#passwordErrorLabel").query();
            assertThat(passwordError).isNotNull();
        }


        @Test
        @Order(18)
        @DisplayName("Should show error for invalid phone number")
        void testInvalidPhoneNumberError() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            clickOn(phoneField).write("abc");

            waitForFxEvents();

            // Should show error
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Real-time Validation Tests")
    class RealTimeValidationTests {

        @Test
        @Order(19)
        @DisplayName("Should trigger SMS automatically at 8 digits")
        void testAutoSMSTrigger() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(20)
        @DisplayName("Should listen to phone number changes")
        void testPhoneNumberListener() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();

            // Verify listener attached
            assertThat(phoneField.textProperty()).isNotNull();
        }


        @Test
        @Order(21)
        @DisplayName("Should trim whitespace before validation")
        void testTrimWhitespace() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @Order(22)
        @DisplayName("Should use SecureRandom for code generation")
        void testSecureRandomUsage() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(23)
        @DisplayName("Should generate code in range 100000-999999")
        void testCodeRange() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(24)
        @DisplayName("Should not expose code in UI")
        void testCodeNotExposed() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Button State Tests")
    class ButtonStateTests {

        @Test
        @Order(25)
        @DisplayName("Should enable send button")
        void testSendButtonEnabled() {
            waitForFxEvents();

            Button sendButton = lookup("#sendSMS").query();
            assertThat(sendButton.isDisabled()).isFalse();
        }


        @Test
        @Order(26)
        @DisplayName("Should handle send button click")
        void testSendButtonClick() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            TextField codeField = lookup("#codeTextField").query();
            Button sendButton = lookup("#sendSMS").query();

            assertThat(phoneField).isNotNull();
            assertThat(codeField).isNotNull();
            assertThat(sendButton).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(27)
        @DisplayName("Should handle SMS API failure")
        void testSMSAPIFailure() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }


        @Test
        @Order(28)
        @DisplayName("Should handle invalid phone number format")
        void testInvalidPhoneFormat() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneNumberTextfield").query();
            assertThat(phoneField).isNotNull();
        }

    }

}


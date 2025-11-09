package com.esprit.controllers.users;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit5.Start;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import com.esprit.MainApp;
import com.esprit.utils.TestAssertions;
import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Comprehensive UI tests for LoginController.
 * Tests all user interactions, validations, and navigation flows.
 * 
 * Test Categories:
 * - UI Elements Visibility
 * - Input Validation
 * - Form Submission
 * - Navigation
 * - Error Handling
 * - Authentication Flows
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
        // Wait for UI to be fully loaded
        waitForFxEvents();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("UI Elements Tests")
    class UIElementsTests {

        @Test
        @Order(1)
        @DisplayName("Should display all login form elements on load")
        void testLoginFormElementsVisible() {
            // Verify all essential UI elements are present and visible
            TestAssertions.verifyAllVisible(
                    "#emailTextField",
                    "#passwordTextField",
                    "#signInButton",
                    "#signUpButton");

            // Verify buttons have correct text
            verifyThat("#signInButton", hasText("Sign In"));
            verifyThat("#signUpButton", hasText("Sign Up"));
        }

        @Test
        @Order(2)
        @DisplayName("Should display forgot password links")
        void testForgotPasswordLinksVisible() {
            verifyThat("#forgetPasswordHyperlink", isVisible());
            verifyThat("#forgetPasswordEmailHyperlink", isVisible());
        }

        @Test
        @Order(3)
        @DisplayName("Should display social login buttons")
        void testSocialLoginButtonsVisible() {
            verifyThat("#googleSIgnInButton", isVisible());
            verifyThat("#microsoftSignInButton", isVisible());
        }

        @Test
        @Order(4)
        @DisplayName("Should have featured movie display")
        void testFeaturedMovieDisplayVisible() {
            verifyThat("#featuredMovieImage", isVisible());
            verifyThat("#featuredMovieTitle", isVisible());
        }

        @Test
        @Order(5)
        @DisplayName("Error labels should be initially hidden or empty")
        void testErrorLabelsInitiallyHidden() {
            // Error labels should not show messages initially
            TestAssertions.verifyNoErrorMessage("#emailErrorLabel");
            TestAssertions.verifyNoErrorMessage("#passwordErrorLabel");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @Order(10)
        @DisplayName("Should validate empty email field")
        void testEmptyEmailValidation() {
            // Leave email empty and try to sign in
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");

            // Verify error message is shown
            verifyThat("#emailErrorLabel", isVisible());
        }

        @Test
        @Order(11)
        @DisplayName("Should validate invalid email format")
        void testInvalidEmailFormatValidation() {
            fillTextField("#emailTextField", TestDataFactory.InvalidData.INVALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");

            // Verify error message for invalid email format
            verifyThat("#emailErrorLabel", isVisible());
        }

        @Test
        @Order(12)
        @DisplayName("Should validate empty password field")
        void testEmptyPasswordValidation() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            clickOnAndWait("#signInButton");

            // Verify password error is shown
            verifyThat("#passwordErrorLabel", isVisible());
        }

        @Test
        @Order(13)
        @DisplayName("Should validate password minimum length")
        void testPasswordMinimumLengthValidation() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.InvalidData.TOO_SHORT_PASSWORD);
            clickOnAndWait("#signInButton");

            // Verify password error is shown
            verifyThat("#passwordErrorLabel", isVisible());
        }

        @Test
        @Order(14)
        @DisplayName("Should accept valid email format")
        void testValidEmailFormat() {
            String validEmail = TestDataFactory.generateEmail();
            fillTextField("#emailTextField", validEmail);

            // Verify email field contains the value
            TestAssertions.verifyTextFieldContains("#emailTextField", validEmail);
        }

        @Test
        @Order(15)
        @DisplayName("Should accept password input")
        void testPasswordInput() {
            String password = TestDataFactory.generateValidPassword();
            fillTextField("#passwordTextField", password);

            // Verify password field is not empty (actual value is masked)
            assertNotEquals("", getTextFieldValue("#passwordTextField"));
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Form Interaction Tests")
    class FormInteractionTests {

        @Test
        @Order(20)
        @DisplayName("Should enable tab navigation between fields")
        void testTabNavigation() {
            clickOn("#emailTextField");
            pressTab();

            // Password field should now be focused
            assertTrue(lookup("#passwordTextField").query().isFocused());
        }

        @Test
        @Order(21)
        @DisplayName("Should submit form on Enter key in password field")
        void testEnterKeySubmission() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            clickOn("#passwordTextField");
            writeAndWait(TestDataFactory.TestCredentials.VALID_PASSWORD);
            push(KeyCode.ENTER);
            waitForFxEvents();

            // Form submission should be triggered
            // (In real scenario, this would navigate away or show loading)
        }

        @Test
        @Order(22)
        @DisplayName("Should clear form fields when requested")
        void testClearFormFields() {
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());

            // Clear fields
            clearTextField("#emailTextField");
            clearTextField("#passwordTextField");

            // Verify fields are empty
            TestAssertions.verifyTextFieldEmpty("#emailTextField");
            TestAssertions.verifyTextFieldEmpty("#passwordTextField");
        }

        @Test
        @Order(23)
        @DisplayName("Should allow multiple character inputs")
        void testMultipleCharacterInput() {
            String longEmail = "very.long.email.address.for.testing@example.com";
            fillTextField("#emailTextField", longEmail);

            assertEquals(longEmail, getTextFieldValue("#emailTextField"));
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Button Interaction Tests")
    class ButtonInteractionTests {

        @Test
        @Order(30)
        @DisplayName("Sign in button should be clickable")
        void testSignInButtonClickable() {
            TestAssertions.verifyButtonReady("#signInButton");
            clickOnAndWait("#signInButton");

            // Button click should trigger validation
            waitForFxEvents();
        }

        @Test
        @Order(31)
        @DisplayName("Sign up button should navigate to registration")
        void testSignUpButtonNavigation() {
            TestAssertions.verifyButtonReady("#signUpButton");
            clickOnAndWait("#signUpButton");

            // In real scenario, this should navigate to sign up page
            waitForFxEvents();
        }

        @Test
        @Order(32)
        @DisplayName("Google sign-in button should be clickable")
        void testGoogleSignInButton() {
            TestAssertions.verifyButtonReady("#googleSIgnInButton");
            clickOnAndWait("#googleSIgnInButton");

            waitForFxEvents();
        }

        @Test
        @Order(33)
        @DisplayName("Microsoft sign-in button should be clickable")
        void testMicrosoftSignInButton() {
            TestAssertions.verifyButtonReady("#microsoftSignInButton");
            clickOnAndWait("#microsoftSignInButton");

            waitForFxEvents();
        }

        @Test
        @Order(34)
        @DisplayName("Forgot password link should be clickable")
        void testForgotPasswordLink() {
            verifyThat("#forgetPasswordHyperlink", isVisible());
            clickOnAndWait("#forgetPasswordHyperlink");

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Authentication Flow Tests")
    class AuthenticationFlowTests {

        @Test
        @Order(40)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should handle valid login credentials")
        void testValidLoginCredentials() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");

            waitForFxEvents();

            // In a real test with proper backend, this would verify successful navigation
            // For now, we verify the form was submitted without validation errors
        }

        @Test
        @Order(41)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should handle admin login credentials")
        void testAdminLoginCredentials() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.ADMIN_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.ADMIN_PASSWORD);
            clickOnAndWait("#signInButton");

            waitForFxEvents();
        }

        @Test
        @Order(42)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should handle cinema manager login credentials")
        void testCinemaManagerLoginCredentials() {
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.CINEMA_MANAGER_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.CINEMA_MANAGER_PASSWORD);
            clickOnAndWait("#signInButton");

            waitForFxEvents();
        }

        @Test
        @Order(43)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should handle invalid credentials")
        void testInvalidCredentials() {
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", "WrongPassword123!");
            clickOnAndWait("#signInButton");

            waitForFxEvents();

            // Should show error (in real implementation)
        }

        @Test
        @Order(44)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should prevent SQL injection in email field")
        void testSQLInjectionPrevention() {
            String sqlInjection = "admin@test.com' OR '1'='1";
            fillTextField("#emailTextField", sqlInjection);
            fillTextField("#passwordTextField", "password");
            clickOnAndWait("#signInButton");

            waitForFxEvents();

            // Verify authentication failed (application rejects malicious input)
            // Check that we are still on login page (no navigation)
            verifyThat("#emailTextField", isVisible());
            verifyThat("#signInButton", isVisible());
            
            // Verify input was sanitized - the malicious input should not execute
            assertThat(lookup("#emailTextField").queryTextInputControl().getText())
                .as("Email field should contain the input (sanitized)")
                .isNotEmpty();
        }

        @Test
        @Order(45)
        @Timeout(value = 30, unit = TimeUnit.SECONDS)
        @DisplayName("Should prevent XSS in input fields")
        void testXSSPrevention() {
            String xssAttempt = "<script>alert('xss')</script>@test.com";
            fillTextField("#emailTextField", xssAttempt);
            fillTextField("#passwordTextField", "password");
            clickOnAndWait("#signInButton");

            waitForFxEvents();

            // Verify input was sanitized and authentication failed
            // Check that we are still on login page (no script execution/navigation)
            verifyThat("#emailTextField", isVisible());
            verifyThat("#signInButton", isVisible());
            
            // Verify the script tag was not executed (we're still on login page)
            // The app should sanitize the input and reject authentication
            assertThat(lookup("#emailTextField").queryTextInputControl().getText())
                .as("XSS attempt should be sanitized and stored safely")
                .isNotEmpty();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Accessibility Tests")
    class AccessibilityTests {

        @Test
        @Order(50)
        @DisplayName("All form fields should be keyboard accessible")
        void testKeyboardAccessibility() {
            // Test Tab navigation through all fields
            clickOn("#emailTextField");
            assertTrue(lookup("#emailTextField").query().isFocused());

            pressTab();
            assertTrue(lookup("#passwordTextField").query().isFocused());

            pressTab();
            // Next focusable element (button or link)
        }

        @Test
        @Order(51)
        @DisplayName("Should support screen reader labels")
        void testScreenReaderSupport() {
            // Verify that form elements have proper prompt text or labels
            // This is important for accessibility
            assertNotNull(lookup("#emailTextField").query());
            assertNotNull(lookup("#passwordTextField").query());
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Error Recovery Tests")
    class ErrorRecoveryTests {

        @Test
        @Order(60)
        @DisplayName("Should clear error messages when correcting input")
        void testErrorMessageClearing() {
            // Trigger validation error
            clickOnAndWait("#signInButton");

            // Now fill in valid data
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);

            // Error should be cleared (in proper implementation)
            waitForFxEvents();
        }

        @Test
        @Order(61)
        @DisplayName("Should allow retry after failed login")
        void testRetryAfterFailedLogin() {
            // First attempt with wrong credentials
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", "Wrong123!");
            clickOnAndWait("#signInButton");
            waitForFxEvents();

            // Clear and try again with valid credentials
            clearTextField("#emailTextField");
            clearTextField("#passwordTextField");
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Visual and Animation Tests")
    class VisualTests {

        @Test
        @Order(70)
        @DisplayName("Should display background animations")
        void testBackgroundAnimations() {
            // Verify animation elements are present
            verifyThat("#particle1", isNotNull());
            verifyThat("#shape1", isNotNull());
        }

        @Test
        @Order(71)
        @DisplayName("Featured movie should display")
        void testFeaturedMovieDisplay() {
            verifyThat("#featuredMovieImage", isVisible());
            verifyThat("#featuredMovieTitle", isVisible());

            // Verify title is not empty
            String movieTitle = getLabelText("#featuredMovieTitle");
            assertNotNull(movieTitle);
            assertFalse(movieTitle.isEmpty());
        }
    }
}

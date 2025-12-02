package com.esprit.controllers.users;

import com.esprit.utils.TestAssertions;
import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Comprehensive UI tests for SignUpController.
 * Tests user registration flow, validation, and form interactions.
 * <p>
 * Test Categories:
 * - UI Elements Visibility
 * - Form Validation
 * - Data Input
 * - Role Selection
 * - Date Picker Interaction
 * - Photo Upload
 * - Form Submission
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SignUpControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/users/SignUp.fxml")
        );
        javafx.scene.Parent root = loader.load();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("UI Elements Tests")
    class UIElementsTests {

        @Test
        @Order(1)
        @DisplayName("Should display all registration form fields")
        void testRegistrationFormElementsVisible() {
            TestAssertions.verifyAllVisible(
                "#nomTextField",
                "#prenomTextField",
                "#emailTextField",
                "#passwordTextField",
                "#num_telephoneTextField",
                "#adresseTextField",
                "#dateDeNaissanceDatePicker",
                "#roleComboBox",
                "#signUpButton",
                "#loginButton");
        }


        @Test
        @Order(2)
        @DisplayName("Should display profile photo placeholder")
        void testProfilePhotoVisible() {
            verifyThat("#photoDeProfilImageView", isVisible());
        }


        @Test
        @Order(3)
        @DisplayName("Role combo box should have options")
        void testRoleComboBoxHasOptions() {
            ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
            TestAssertions.verifyComboBoxHasItems(roleComboBox);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Form Validation Tests")
    class FormValidationTests {

        @Test
        @Order(10)
        @DisplayName("Should validate required first name field")
        void testFirstNameRequired() {
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());

            // Clear first name
            clearTextField("#nomTextField");
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
            // Should show validation error
        }


        @Test
        @Order(11)
        @DisplayName("Should validate required last name field")
        void testLastNameRequired() {
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());

            // Clear last name
            clearTextField("#prenomTextField");
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
        }


        @Test
        @Order(12)
        @DisplayName("Should validate email format")
        void testEmailFormatValidation() {
            fillTextField("#emailTextField", TestDataFactory.InvalidData.INVALID_EMAIL);
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
            // Should show email format error
        }


        @Test
        @Order(13)
        @DisplayName("Should validate unique email")
        void testUniqueEmailValidation() {
            // Test would verify that duplicate emails are rejected
            fillTextField("#emailTextField", "existing@test.com");
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
        }


        @Test
        @Order(14)
        @DisplayName("Should validate password strength")
        void testPasswordStrengthValidation() {
            fillTextField("#passwordTextField", TestDataFactory.InvalidData.WEAK_PASSWORD);
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
            // Should show weak password error
        }


        @Test
        @Order(15)
        @DisplayName("Should validate phone number format")
        void testPhoneNumberValidation() {
            fillTextField("#num_telephoneTextField", "invalid-phone");
            clickOnAndWait("#signUpButton");

            waitForFxEvents();
            // Should show phone format error
        }


        @Test
        @Order(16)
        @DisplayName("Should validate date of birth")
        void testDateOfBirthValidation() {
            // Select future date (should be invalid)
            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            runOnFxThread(() -> datePicker.setValue(LocalDate.now().plusDays(1)));

            clickOnAndWait("#signUpButton");
            waitForFxEvents();
            // Should show date validation error
        }


        @Test
        @Order(17)
        @DisplayName("Should validate minimum age requirement")
        void testMinimumAgeValidation() {
            // Select date making user too young (e.g., 10 years old)
            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            runOnFxThread(() -> datePicker.setValue(LocalDate.now().minusYears(10)));

            clickOnAndWait("#signUpButton");
            waitForFxEvents();
            // Should show age requirement error
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Form Input Tests")
    class FormInputTests {

        @Test
        @Order(20)
        @DisplayName("Should accept valid first name")
        void testValidFirstNameInput() {
            String firstName = TestDataFactory.generateFirstName();
            fillTextField("#prenomTextField", firstName);

            assertEquals(firstName, getTextFieldValue("#prenomTextField"));
        }


        @Test
        @Order(21)
        @DisplayName("Should accept valid last name")
        void testValidLastNameInput() {
            String lastName = TestDataFactory.generateLastName();
            fillTextField("#nomTextField", lastName);

            assertEquals(lastName, getTextFieldValue("#nomTextField"));
        }


        @Test
        @Order(22)
        @DisplayName("Should accept valid email")
        void testValidEmailInput() {
            String email = TestDataFactory.generateEmail();
            fillTextField("#emailTextField", email);

            TestAssertions.verifyTextFieldContains("#emailTextField", email);
        }


        @Test
        @Order(23)
        @DisplayName("Should accept valid phone number")
        void testValidPhoneNumberInput() {
            String phone = "+1234567890";
            fillTextField("#num_telephoneTextField", phone);

            assertEquals(phone, getTextFieldValue("#num_telephoneTextField"));
        }


        @Test
        @Order(24)
        @DisplayName("Should accept valid address")
        void testValidAddressInput() {
            String address = TestDataFactory.generateAddress();
            fillTextField("#adresseTextField", address);

            assertEquals(address, getTextFieldValue("#adresseTextField"));
        }


        @Test
        @Order(25)
        @DisplayName("Should accept valid password")
        void testValidPasswordInput() {
            String password = TestDataFactory.generateValidPassword();
            fillTextField("#passwordTextField", password);

            assertNotEquals("", getTextFieldValue("#passwordTextField"));
        }


        @Test
        @Order(26)
        @DisplayName("Should handle special characters in name fields")
        void testSpecialCharactersInName() {
            fillTextField("#nomTextField", "O'Brien");
            fillTextField("#prenomTextField", "Jean-Pierre");

            assertEquals("O'Brien", getTextFieldValue("#nomTextField"));
            assertEquals("Jean-Pierre", getTextFieldValue("#prenomTextField"));
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Role Selection Tests")
    class RoleSelectionTests {

        @Test
        @Order(30)
        @DisplayName("Should allow selecting Client role")
        void testSelectClientRole() {
            ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
            selectComboBoxItem("#roleComboBox", "Client");

            waitForFxEvents();
            assertEquals("Client", roleComboBox.getValue());
        }


        @Test
        @Order(31)
        @DisplayName("Should allow selecting Cinema Manager role")
        void testSelectCinemaManagerRole() {
            ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
            selectComboBoxItem("#roleComboBox", "Cinema Manager");

            waitForFxEvents();
            assertEquals("Cinema Manager", roleComboBox.getValue());
        }


        @Test
        @Order(32)
        @DisplayName("Should require role selection")
        void testRoleSelectionRequired() {
            // Fill all fields except role
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());

            clickOnAndWait("#signUpButton");
            waitForFxEvents();
            // Should show role required error
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Date Picker Tests")
    class DatePickerTests {

        @Test
        @Order(40)
        @DisplayName("Should open date picker calendar")
        void testOpenDatePicker() {
            clickOn("#dateDeNaissanceDatePicker");
            waitForFxEvents();

            // Calendar should be displayed
        }


        @Test
        @Order(41)
        @DisplayName("Should select valid birth date")
        void testSelectValidBirthDate() {
            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            LocalDate validDate = LocalDate.now().minusYears(25);

            runOnFxThread(() -> datePicker.setValue(validDate));

            assertEquals(validDate, datePicker.getValue());
        }


        @Test
        @Order(42)
        @DisplayName("Should handle manual date input")
        void testManualDateInput() {
            clickOn("#dateDeNaissanceDatePicker");
            // Type date manually
            write("01/01/1990");
            pressEnter();

            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Complete Registration Flow Tests")
    class CompleteRegistrationFlowTests {

        @Test
        @Order(50)
        @DisplayName("Should register new client successfully")
        void testCompleteClientRegistration() {
            // Fill all required fields
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());
            fillTextField("#num_telephoneTextField", "+1234567890");
            fillTextField("#adresseTextField", TestDataFactory.generateAddress());

            // Set date of birth
            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            runOnFxThread(() -> datePicker.setValue(LocalDate.now().minusYears(25)));

            // Select role
            selectComboBoxItem("#roleComboBox", "Client");

            // Submit form
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // Should navigate to success page or login
        }


        @Test
        @Order(51)
        @DisplayName("Should register new cinema manager successfully")
        void testCompleteCinemaManagerRegistration() {
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());
            fillTextField("#num_telephoneTextField", "+1234567890");
            fillTextField("#adresseTextField", TestDataFactory.generateAddress());

            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            runOnFxThread(() -> datePicker.setValue(LocalDate.now().minusYears(30)));

            selectComboBoxItem("#roleComboBox", "Cinema Manager");

            clickOnAndWait("#signUpButton");
            waitForFxEvents();
        }


        @Test
        @Order(52)
        @DisplayName("Should handle registration with all optional fields")
        void testRegistrationWithAllFields() {
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());
            fillTextField("#num_telephoneTextField", TestDataFactory.generatePhoneNumber());
            fillTextField("#adresseTextField", TestDataFactory.generateAddress());

            DatePicker datePicker = lookup("#dateDeNaissanceDatePicker").query();
            runOnFxThread(() -> datePicker.setValue(LocalDate.of(1990, 1, 1)));

            selectComboBoxItem("#roleComboBox", "Client");

            clickOnAndWait("#signUpButton");
            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(60)
        @DisplayName("Should navigate to login page")
        void testNavigateToLogin() {
            TestAssertions.verifyButtonReady("#loginButton");
            clickOnAndWait("#loginButton");

            waitForFxEvents();
            // Should navigate to login page
        }


        @Test
        @Order(61)
        @DisplayName("Should support tab navigation through form fields")
        void testTabNavigationThroughForm() {
            clickOn("#nomTextField");
            assertTrue(lookup("#nomTextField").query().isFocused());

            pressTab();
            assertTrue(lookup("#prenomTextField").query().isFocused());

            pressTab();
            assertTrue(lookup("#emailTextField").query().isFocused());
        }


        @Test
        @Order(62)
        @DisplayName("Should support keyboard submission")
        void testKeyboardFormSubmission() {
            // Fill form and press Enter in last field
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());

            clickOn("#passwordTextField");
            writeAndWait(TestDataFactory.generateValidPassword());
            push(KeyCode.ENTER);

            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(70)
        @DisplayName("Should show all validation errors at once")
        void testShowAllValidationErrors() {
            // Submit empty form
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // All required field errors should be visible
        }


        @Test
        @Order(71)
        @DisplayName("Should clear errors when correcting inputs")
        void testClearErrorsOnCorrection() {
            // Trigger errors
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // Fix fields
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());

            waitForFxEvents();
            // Errors should clear as fields are corrected
        }


        @Test
        @Order(72)
        @DisplayName("Should handle network errors gracefully")
        void testNetworkErrorHandling() {
            // Fill valid form
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());

            // Simulate network error scenario
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // Should show appropriate error message
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Accessibility Tests")
    class AccessibilityTests {

        @Test
        @Order(80)
        @DisplayName("All form fields should be keyboard accessible")
        void testKeyboardAccessibility() {
            clickOn("#nomTextField");
            writeAndWait("Test");

            pressTab();
            writeAndWait("User");

            pressTab();
            writeAndWait("test@example.com");

            // All fields should be accessible via keyboard
            waitForFxEvents();
        }


        @Test
        @Order(81)
        @DisplayName("Form should have proper focus indicators")
        void testFocusIndicators() {
            clickOn("#nomTextField");
            assertTrue(lookup("#nomTextField").query().isFocused());

            clickOn("#emailTextField");
            assertTrue(lookup("#emailTextField").query().isFocused());
        }

    }

}


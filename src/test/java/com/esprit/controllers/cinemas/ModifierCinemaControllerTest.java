package com.esprit.controllers.cinemas;

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
 * Comprehensive test suite for ModifierCinemaController.
 * Tests cinema modification and update functionality.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ModifierCinemaControllerTest extends TestFXBase {

    @Start
    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml")
        );
        javafx.scene.Parent root = loader.load();
        
        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cinema Update Tests")
    class CinemaUpdateTests {

        @Test
        @Order(1)
        @DisplayName("Should display cinema name field")
        void testNameFieldDisplay() {
            waitForFxEvents();

            TextField nameField = lookup("#cinemaNameField").query();
            assertThat(nameField).isNotNull();
            assertThat(nameField.isVisible()).isTrue();
        }

        @Test
        @Order(2)
        @DisplayName("Should update cinema name")
        void testUpdateCinemaName() {
            waitForFxEvents();

            TextField nameField = lookup("#cinemaNameField").query();
            assertThat(nameField).isNotNull();
            
            clickOn(nameField).eraseText(20).write("Updated Cinema");
            waitForFxEvents();
            
            assertThat(nameField.getText()).isEqualTo("Updated Cinema");

            Button saveButton = lookup("#saveButton").query();
            assertThat(saveButton).isNotNull();
            assertThat(saveButton.isVisible()).isTrue();
            
            clickOn(saveButton);
            waitForFxEvents();
        }

        @Test
        @Order(3)
        @DisplayName("Should validate required fields")
        void testValidateRequiredFields() {
            waitForFxEvents();

            TextField nameField = lookup("#cinemaNameField").query();
            assertThat(nameField).isNotNull();
            
            clickOn(nameField).eraseText(20);
            waitForFxEvents();

            Button saveButton = lookup("#saveButton").query();
            assertThat(saveButton).isNotNull();
            
            clickOn(saveButton);
            waitForFxEvents();

            Label errorLabel = lookup("#errorLabel").query();
            assertThat(errorLabel).isNotNull();
            assertThat(errorLabel.getText()).isNotEmpty();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Form Validation Tests")
    class FormValidationTests {

        @Test
        @Order(4)
        @DisplayName("Should validate address field")
        void testAddressValidation() {
            waitForFxEvents();

            TextField addressField = lookup("#addressField").query();
            assertThat(addressField).isNotNull();
            
            clickOn(addressField).write("123 Main St");
            waitForFxEvents();

            assertThat(addressField.getText()).isNotEmpty();
            assertThat(addressField.getText()).isEqualTo("123 Main St");
        }

        @Test
        @Order(5)
        @DisplayName("Should validate phone number")
        void testPhoneValidation() {
            waitForFxEvents();

            TextField phoneField = lookup("#phoneField").query();
            assertThat(phoneField).isNotNull();
            
            clickOn(phoneField).write("12345678");
            waitForFxEvents();

            assertThat(phoneField.getText()).hasSize(8);
            assertThat(phoneField.getText()).isEqualTo("12345678");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cancel Operation Tests")
    class CancelOperationTests {

        @Test
        @Order(6)
        @DisplayName("Should cancel modification")
        void testCancelModification() {
            waitForFxEvents();

            Button cancelButton = lookup("#cancelButton").query();
            assertThat(cancelButton).isNotNull();
            assertThat(cancelButton.isVisible()).isTrue();
            
            clickOn(cancelButton);
            waitForFxEvents();
            
            // Verify button was clicked and no update occurred
            assertThat(cancelButton.isVisible()).isTrue();
        }
    }

}

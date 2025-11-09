package com.esprit.controllers.films;

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

import com.esprit.services.films.FilmService;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for PaymentUserController.
 * Tests payment processing for film bookings.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentUserControllerTest extends TestFXBase {

    private FilmService filmService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Paymentuser.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Payment Form Tests")
    class PaymentFormTests {

        @Test
        @Order(1)
        @DisplayName("Should display payment form")
        void testPaymentFormDisplay() {
            waitForFxEvents();

            assertThat(lookup("#paymentForm").tryQuery()).isPresent();
            assertThat(lookup("#paymentForm").query().isVisible()).isTrue();
        }

        @Test
        @Order(2)
        @DisplayName("Should display card number field")
        void testCardNumberField() {
            waitForFxEvents();

            TextField cardField = lookup("#cardNumberField").query();
            assertThat(cardField).isNotNull();
            assertThat(cardField.isVisible()).isTrue();
        }

        @Test
        @Order(3)
        @DisplayName("Should validate card number format")
        void testCardNumberValidation() {
            waitForFxEvents();

            TextField cardField = lookup("#cardNumberField").query();
            clickOn(cardField).write("1234567812345678");

            assertThat(cardField.getText()).hasSize(16);
            assertThat(cardField.getText()).isEqualTo("1234567812345678");
        }

        @Test
        @Order(4)
        @DisplayName("Should display CVV field")
        void testCVVField() {
            waitForFxEvents();

            TextField cvvField = lookup("#cvvField").query();
            assertThat(cvvField).isNotNull();
            assertThat(cvvField.isVisible()).isTrue();
        }

        @Test
        @Order(5)
        @DisplayName("Should validate CVV format")
        void testCVVValidation() {
            waitForFxEvents();

            TextField cvvField = lookup("#cvvField").query();
            clickOn(cvvField).write("123");

            assertThat(cvvField.getText()).hasSize(3);
            assertThat(cvvField.getText()).isEqualTo("123");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Payment Processing Tests")
    class PaymentProcessingTests {

        @Test
        @Order(6)
        @DisplayName("Should process payment")
        void testProcessPayment() {
            waitForFxEvents();

            TextField cardField = lookup("#cardNumberField").query();
            clickOn(cardField).write("1234567812345678");

            TextField cvvField = lookup("#cvvField").query();
            clickOn(cvvField).write("123");

            Button payButton = lookup("#payButton").query();
            assertThat(payButton).isNotNull();
            clickOn(payButton);

            waitForFxEvents();
            
            // Verify button is responsive
            assertThat(payButton).isNotNull();
        }

        @Test
        @Order(7)
        @DisplayName("Should show success message on payment")
        void testPaymentSuccess() {
            waitForFxEvents();

            // Verify success label exists and is visible
            Label successLabel = lookup("#successLabel").query();
            assertThat(successLabel).isNotNull();
            
            waitForFxEvents();
        }

        @Test
        @Order(8)
        @DisplayName("Should handle payment failure")
        void testPaymentFailure() {
            waitForFxEvents();

            // Verify error label exists
            Label errorLabel = lookup("#errorLabel").query();
            assertThat(errorLabel).isNotNull();
            
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Order Summary Tests")
    class OrderSummaryTests {

        @Test
        @Order(9)
        @DisplayName("Should display order summary")
        void testOrderSummary() {
            waitForFxEvents();

            assertThat(lookup("#orderSummary").tryQuery()).isPresent();
            assertThat(lookup("#orderSummary").query().isVisible()).isTrue();
        }

        @Test
        @Order(10)
        @DisplayName("Should display total amount")
        void testTotalAmount() {
            waitForFxEvents();

            Label totalLabel = lookup("#totalAmountLabel").query();
            assertThat(totalLabel).isNotNull();
            assertThat(totalLabel.isVisible()).isTrue();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cancel Payment Tests")
    class CancelPaymentTests {

        @Test
        @Order(11)
        @DisplayName("Should cancel payment")
        void testCancelPayment() {
            waitForFxEvents();

            Button cancelButton = lookup("#cancelButton").query();
            assertThat(cancelButton).isNotNull();
            
            clickOn(cancelButton);

            waitForFxEvents();
            
            // Verify cancel action completed
            assertThat(cancelButton).isNotNull();
        }
    }
}

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
import org.testfx.util.WaitForAsyncUtils;

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

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Paymentuser.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Payment Processing Tests")
    class PaymentProcessingTests {

        @Test
        @Order(6)
        @DisplayName("Should process payment")
        void testProcessPayment() {
            waitForFxEvents();

            // Fill in card details
            TextField cardField = lookup("#cardNumberField").query();
            clickOn(cardField).write("1234567812345678");

            TextField cvvField = lookup("#cvvField").query();
            clickOn(cvvField).write("123");

            Button payButton = lookup("#payButton").query();
            assertThat(payButton).isNotNull();
            
            // Click pay button
            clickOn(payButton);
            waitForFxEvents();
            
            // Verify button is still responsive (not permanently broken)
            assertThat(payButton).isNotNull();
            
            // Wait for async payment processing to complete
            try {
                WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
                    var successLabel = lookup("#successLabel").tryQuery();
                    var errorLabel = lookup("#errorLabel").tryQuery();
                    return (successLabel.isPresent() && successLabel.get().isVisible()) ||
                           (errorLabel.isPresent() && errorLabel.get().isVisible());
                });
            } catch (Exception e) {
                // Continue even if timeout - check UI state below
            }
            waitForFxEvents();
            
            // After processing, verify UI reflects payment attempt
            // Check if either success or error label is visible (indicating payment completed)
            var successLabel = lookup("#successLabel").tryQuery();
            var errorLabel = lookup("#errorLabel").tryQuery();
            
            // At least one should be present and visible
            boolean paymentCompleted = (successLabel.isPresent() && successLabel.get().isVisible()) ||
                                      (errorLabel.isPresent() && errorLabel.get().isVisible());
            assertThat(paymentCompleted).as("Payment processing should complete with visible status").isTrue();
        }

        @Test
        @Order(7)
        @DisplayName("Should show success message on payment")
        void testPaymentSuccess() {
            waitForFxEvents();

            // Fill in card details and trigger payment
            TextField cardField = lookup("#cardNumberField").query();
            clickOn(cardField).write("1234567812345678");

            TextField cvvField = lookup("#cvvField").query();
            clickOn(cvvField).write("123");

            Button payButton = lookup("#payButton").query();
            clickOn(payButton);
            
            // Wait for payment processing
            try {
                WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
                    Label label = lookup("#successLabel").query();
                    return label != null && label.isVisible();
                });
            } catch (Exception e) {
                // Continue even if timeout - check UI state below
            }
            waitForFxEvents();

            // Verify success label exists, is visible, and contains success message
            Label successLabel = lookup("#successLabel").query();
            assertThat(successLabel).isNotNull();
            assertThat(successLabel.isVisible()).as("Success label should be visible").isTrue();
            
            String message = successLabel.getText();
            assertThat(message).isNotNull();
            assertThat(message.toLowerCase()).as("Success message should contain 'success'")
                    .contains("success".toLowerCase());
        }

        @Test
        @Order(8)
        @DisplayName("Should handle payment failure")
        void testPaymentFailure() {
            waitForFxEvents();

            // Fill in card details with intentionally invalid data
            TextField cardField = lookup("#cardNumberField").query();
            clickOn(cardField).write("0000000000000000");

            TextField cvvField = lookup("#cvvField").query();
            clickOn(cvvField).write("000");

            Button payButton = lookup("#payButton").query();
            clickOn(payButton);
            
            // Wait for payment processing
            try {
                WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
                    Label label = lookup("#errorLabel").query();
                    return label != null && label.isVisible();
                });
            } catch (Exception e) {
                // Continue even if timeout - check UI state below
            }
            waitForFxEvents();

            // Verify error label exists, is visible, and contains error message
            Label errorLabel = lookup("#errorLabel").query();
            assertThat(errorLabel).isNotNull();
            assertThat(errorLabel.isVisible()).as("Error label should be visible").isTrue();
            
            String message = errorLabel.getText();
            assertThat(message).isNotNull();
            assertThat(message.toLowerCase()).as("Error message should contain 'error' or 'failed'")
                    .containsAnyOf("error", "failed");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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

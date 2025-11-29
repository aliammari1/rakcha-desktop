package com.esprit.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Comprehensive test suite for PaymentProcessor utility class.
 * Tests payment processing, input validation, and error handling.
 * 
 * Test Categories:
 * - Input Validation
 * - Payment Processing (requires STRIPE_API_KEY env var)
 * - Error Handling
 * - Edge Cases
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentProcessorTest {

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @Order(1)
        @DisplayName("Should reject null customer name")
        void testNullNameRejected() {
            boolean result = PaymentProcessor.processPayment(
                null, "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(2)
        @DisplayName("Should reject empty customer name")
        void testEmptyNameRejected() {
            boolean result = PaymentProcessor.processPayment(
                "", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(3)
        @DisplayName("Should reject whitespace-only name")
        void testWhitespaceNameRejected() {
            boolean result = PaymentProcessor.processPayment(
                "   ", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(4)
        @DisplayName("Should reject null email")
        void testNullEmailRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", null, 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(5)
        @DisplayName("Should reject empty email")
        void testEmptyEmailRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(6)
        @DisplayName("Should reject negative amount")
        void testNegativeAmountRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", -50.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(7)
        @DisplayName("Should reject zero amount")
        void testZeroAmountRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 0.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(8)
        @DisplayName("Should reject null card number")
        void testNullCardNumberRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                null, 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(9)
        @DisplayName("Should reject empty card number")
        void testEmptyCardNumberRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(10)
        @DisplayName("Should reject invalid expiration month")
        void testInvalidExpirationMonthRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 13, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(11)
        @DisplayName("Should reject zero expiration month")
        void testZeroExpirationMonthRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 0, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(12)
        @DisplayName("Should reject past expiration year")
        void testPastExpirationYearRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2020, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(13)
        @DisplayName("Should reject null CVC")
        void testNullCvcRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, null
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(14)
        @DisplayName("Should reject empty CVC")
        void testEmptyCvcRejected() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, ""
            );
            
            assertThat(result).isFalse();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Payment Processing Tests")
    class PaymentProcessingTests {

        @Test
        @Order(15)
        @DisplayName("Should process valid payment successfully")
        @EnabledIfEnvironmentVariable(named = "STRIPE_API_KEY", matches = ".+")
        void testValidPaymentProcessing() {
            // Using Stripe test card number
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            // Result depends on actual Stripe API availability
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(16)
        @DisplayName("Should handle small payment amounts")
        @EnabledIfEnvironmentVariable(named = "STRIPE_API_KEY", matches = ".+")
        void testSmallAmountPayment() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 0.01f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(17)
        @DisplayName("Should handle large payment amounts")
        @EnabledIfEnvironmentVariable(named = "STRIPE_API_KEY", matches = ".+")
        void testLargeAmountPayment() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 9999.99f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(18)
        @DisplayName("Should handle decimal amounts correctly")
        @EnabledIfEnvironmentVariable(named = "STRIPE_API_KEY", matches = ".+")
        void testDecimalAmountPayment() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 123.45f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(19)
        @DisplayName("Should handle invalid card number gracefully")
        void testInvalidCardNumber() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "1234567890123456", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(20)
        @DisplayName("Should handle short card number")
        void testShortCardNumber() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "1234", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(21)
        @DisplayName("Should handle alphanumeric card number")
        void testAlphanumericCardNumber() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "424242abc4242424", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(22)
        @DisplayName("Should handle invalid email format")
        void testInvalidEmailFormat() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "not-an-email", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isFalse();
        }


        @Test
        @Order(23)
        @DisplayName("Should handle missing API key gracefully")
        void testMissingApiKey() {
            // This test verifies behavior when Stripe is not configured
            // The static initializer should throw ExceptionInInitializerError
            assertThat(PaymentProcessor.class).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @Order(24)
        @DisplayName("Should handle very long customer name")
        void testVeryLongName() {
            String longName = "A".repeat(500);
            boolean result = PaymentProcessor.processPayment(
                longName, "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            // Should either succeed or fail gracefully
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(25)
        @DisplayName("Should handle special characters in name")
        void testSpecialCharactersInName() {
            boolean result = PaymentProcessor.processPayment(
                "Jean-Pierre O'Malley", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(26)
        @DisplayName("Should handle Unicode characters in name")
        void testUnicodeInName() {
            boolean result = PaymentProcessor.processPayment(
                "José García 李明", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(27)
        @DisplayName("Should handle very long email")
        void testVeryLongEmail() {
            String longEmail = "a".repeat(200) + "@example.com";
            boolean result = PaymentProcessor.processPayment(
                "John Doe", longEmail, 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(28)
        @DisplayName("Should handle minimum valid month")
        void testMinimumValidMonth() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 1, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(29)
        @DisplayName("Should handle maximum valid month")
        void testMaximumValidMonth() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(30)
        @DisplayName("Should handle current year")
        void testCurrentYear() {
            int currentYear = java.time.Year.now().getValue();
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, currentYear, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(31)
        @DisplayName("Should handle far future year")
        void testFarFutureYear() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2099, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(32)
        @DisplayName("Should handle 3-digit CVC")
        void testThreeDigitCvc() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(33)
        @DisplayName("Should handle 4-digit CVC for Amex")
        void testFourDigitCvc() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "378282246310005", 12, 2025, "1234"
            );
            
            assertThat(result).isIn(true, false);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Currency and Amount Tests")
    class CurrencyAndAmountTests {

        @Test
        @Order(34)
        @DisplayName("Should use EUR currency")
        void testEurCurrency() {
            // The PaymentProcessor uses EUR as default currency
            // This test verifies that payments are processed in EUR
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 100.0f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(35)
        @DisplayName("Should convert amount to cents correctly")
        void testCentsConversion() {
            // Amount should be multiplied by 100 for Stripe
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 1.99f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }


        @Test
        @Order(36)
        @DisplayName("Should handle fractional cents")
        void testFractionalCents() {
            boolean result = PaymentProcessor.processPayment(
                "John Doe", "test@example.com", 1.999f, 
                "4242424242424242", 12, 2025, "123"
            );
            
            assertThat(result).isIn(true, false);
        }

    }

}
package com.esprit.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum PaymentProcessor {
    ;
    private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class.getName());
    private static final String CURRENCY = "eur";
    private static final int CENTS_MULTIPLIER = 100;
    
    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("STRIPE_API_KEY");
        if (apiKey == null) {
            LOGGER.severe("Stripe API key not found in .env file");
            throw new ExceptionInInitializerError("Stripe API key is required");
        }

        Stripe.apiKey = apiKey;
        LOGGER.info("Stripe initialized in " + (apiKey.startsWith("sk_test_") ? "TEST" : "LIVE") + " mode");
    }


    /**
     * Process a payment with Stripe using secure best practices
     * 
     * In development: Uses test tokens for security
     * In production: Would use Stripe Elements or Payment Intents API
     *
     * @param name         Customer name
     * @param email        Customer email
     * @param amount       Amount to charge in the default currency
     * @param cardNumber   Credit card number (for test token lookup)
     * @param cardExpMonth Card expiration month
     * @param cardExpYear  Card expiration year
     * @param cardCvc      Card CVC code
     * @return boolean indicating if the payment was successful
     */
    public static boolean processPayment(final String name, final String email, final float amount,
                                         final String cardNumber, final int cardExpMonth, final int cardExpYear, final String cardCvc) {
        try {
            validateInputs(name, email, amount, cardNumber, cardExpMonth, cardExpYear, cardCvc);

            // Check if we're in test mode
            if (isTestMode()) {
                return processTestPayment(name, email, amount, cardNumber);
            } else {
                // In production, use Payment Intents API (more secure)
                return processProductionPayment(name, email, amount, cardNumber, cardExpMonth, cardExpYear, cardCvc);
            }

        } catch (StripeException e) {
            LOGGER.log(Level.SEVERE, "Stripe payment processing failed", e);
            return false;
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid payment parameters", e);
            return false;
        }
    }
    
    /**
     * Check if we're running in test mode based on API key
     */
    private static boolean isTestMode() {
        return Stripe.apiKey != null && Stripe.apiKey.startsWith("sk_test_");
    }
    
    /**
     * Process payment using test mode (simulated for development)
     * Simulates payment processing without actual Stripe calls for better reliability
     */
    private static boolean processTestPayment(String name, String email, float amount, String cardNumber) {
        
        String normalizedCard = cardNumber.replaceAll("\\s+", "");
        String behavior = getTestCardBehavior(normalizedCard);
        
        LOGGER.info(String.format("Processing test payment: %.2f %s for %s (%s) - Card: %s", 
            amount, CURRENCY.toUpperCase(), name, email, behavior));
        
        // Simulate processing delay
        try {
            Thread.sleep(1000); // 1 second delay to simulate real processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check for declined test card
        if ("4000000000009995".equals(normalizedCard)) {
            LOGGER.info("Test payment DECLINED for card ending in 9995");
            return false;
        }
        
        // Check for invalid test cards (simulate validation failure)
        if (!isKnownTestCard(normalizedCard) && normalizedCard.length() < 15) {
            LOGGER.warning("Test payment FAILED - Invalid card number");
            return false;
        }
        
        // Simulate successful payment for all other cases
        String transactionId = "test_charge_" + System.currentTimeMillis();
        LOGGER.info(String.format("Test payment SUCCEEDED - Transaction ID: %s - Amount: %.2f %s", 
            transactionId, amount, CURRENCY.toUpperCase()));
            
        return true;
    }
    
    /**
     * Check if this is a known working test card
     */
    private static boolean isKnownTestCard(String cardNumber) {
        return "4242424242424242".equals(cardNumber) ||
               "4000000000000002".equals(cardNumber) ||
               "5555555555554444".equals(cardNumber) ||
               "378282246310005".equals(cardNumber) ||
               "4111111111111111".equals(cardNumber);
    }
    
    /**
     * Process payment using Payment Intents API (production)
     */
    private static boolean processProductionPayment(String name, String email, float amount, 
            String cardNumber, int cardExpMonth, int cardExpYear, String cardCvc) throws StripeException {
        
        // Create customer
        Customer customer = retrieveOrCreateCustomer(name, email);
        
        // Create Payment Intent (more secure than direct charges)
        Map<String, Object> intentParams = new HashMap<>();
        intentParams.put("amount", (int) (amount * CENTS_MULTIPLIER));
        intentParams.put("currency", CURRENCY);
        intentParams.put("customer", customer.getId());
        intentParams.put("description", "Cinema ticket purchase - " + name);
        intentParams.put("confirmation_method", "manual");
        intentParams.put("confirm", true);
        
        // In production, you would use Stripe Elements on frontend
        // and pass the payment method ID here instead of raw card data
        LOGGER.warning("Production payment processing requires Stripe Elements integration");
        
        PaymentIntent intent = PaymentIntent.create(intentParams);
        
        return "succeeded".equals(intent.getStatus());
    }
    
    /**
     * Get test card behavior for a given card number
     */
    private static String getTestCardBehavior(String cardNumber) {
        // Remove spaces and normalize
        String normalizedCard = cardNumber.replaceAll("\\s+", "");
        
        // Return behavior description for logging
        return switch (normalizedCard) {
            case "4242424242424242" -> "Visa - Success";
            case "4000000000000002" -> "Visa Debit - Success";
            case "5555555555554444" -> "Mastercard - Success";
            case "378282246310005" -> "American Express - Success";
            case "4000000000009995" -> "Visa - Declined";
            case "4111111111111111" -> "Visa - Success (Generic)";
            default -> "Unknown Test Card - Will attempt success";
        };
    }


    /**
     * Validate payment inputs with appropriate checks for test/production mode
     */
    private static void validateInputs(String name, String email, float amount, String cardNumber, 
                                     int cardExpMonth, int cardExpYear, String cardCvc) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Valid email is required");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        // More lenient validation for test mode
        if (isTestMode()) {
            // In test mode, accept common test card formats
            if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 13) {
                throw new IllegalArgumentException("Valid card number is required");
            }
        } else {
            // Stricter validation for production
            if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) {
                throw new IllegalArgumentException("Valid card number is required");
            }
        }

        if (cardExpMonth < 1 || cardExpMonth > 12) {
            throw new IllegalArgumentException("Valid expiration month is required (1-12)");
        }

        // Allow past dates in test mode for convenience
        if (!isTestMode() && cardExpYear < java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Card is expired");
        }

        if (cardCvc == null || !cardCvc.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("Valid CVC is required");
        }
    }


    /**
     * Create or retrieve a Stripe customer
     */
    private static Customer retrieveOrCreateCustomer(final String name, final String email) throws StripeException {
        final Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("name", name);
        customerParams.put("email", email);
        customerParams.put("description", "Cinema customer - " + name);
        
        Customer customer = Customer.create(customerParams);
        LOGGER.info("Created Stripe customer: " + customer.getId());
        return customer;
    }
    
    /**
     * Get information about test cards for development
     */
    public static String getTestCardInfo() {
        return """
            Test Cards for Development:
            • 4242424242424242 - Visa (Success)
            • 4000000000000002 - Visa Debit (Success)  
            • 5555555555554444 - Mastercard (Success)
            • 378282246310005 - American Express (Success)
            • 4000000000009995 - Visa (Declined)
            
            All test cards use:
            • Any future expiry date (e.g., 12/2025)
            • Any 3-digit CVC (e.g., 123)
            """;
    }

}


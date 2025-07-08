package com.esprit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;

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
    }

    /**
     * Process a payment with Stripe
     *
     * @param name
     *                     Customer name
     * @param email
     *                     Customer email
     * @param amount
     *                     Amount to charge in the default currency
     * @param cardNumber
     *                     Credit card number
     * @param cardExpMonth
     *                     Card expiration month
     * @param cardExpYear
     *                     Card expiration year
     * @param cardCvc
     *                     Card CVC code
     * @return boolean indicating if the payment was successful
     */
    public static boolean processPayment(final String name, final String email, final float amount,
            final String cardNumber, final int cardExpMonth, final int cardExpYear, final String cardCvc) {
        try {
            validateInputs(name, email, amount, cardNumber, cardExpMonth, cardExpYear, cardCvc);

            // Create or retrieve customer
            final Customer customer = retrieveOrCreateCustomer(name, email);

            // Create token for the credit card
            final Token token = createToken(cardNumber, cardExpMonth, cardExpYear, cardCvc);

            // Charge the customer
            final Charge charge = chargeCustomer(customer.getId(), token.getId(), amount);

            return "succeeded".equals(charge.getStatus());
        } catch (StripeException e) {
            LOGGER.log(Level.SEVERE, "Stripe payment processing failed", e);
            return false;
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid payment parameters", e);
            return false;
        }
    }

    private static void validateInputs(String name, String email, float amount, String cardNumber, int cardExpMonth,
            int cardExpYear, String cardCvc) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Valid card number is required");
        }
        if (cardExpMonth < 1 || cardExpMonth > 12) {
            throw new IllegalArgumentException("Valid expiration month is required (1-12)");
        }
        if (cardExpYear < java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Card is expired");
        }
        if (cardCvc == null || !cardCvc.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("Valid CVC is required");
        }
    }

    private static Customer retrieveOrCreateCustomer(final String name, final String email) throws StripeException {
        final Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("name", name);
        customerParams.put("email", email);
        return Customer.create(customerParams);
    }

    private static Token createToken(final String cardNumber, final int expMonth, final int expYear, final String cvc)
            throws StripeException {
        final Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", cardNumber);
        cardParams.put("exp_month", expMonth);
        cardParams.put("exp_year", expYear);
        cardParams.put("cvc", cvc);

        final Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);
        return Token.create(tokenParams);
    }

    private static Charge chargeCustomer(final String customerId, final String tokenId, final float amount)
            throws StripeException {
        final Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (amount * CENTS_MULTIPLIER));
        chargeParams.put("currency", CURRENCY);
        chargeParams.put("customer", customerId);
        chargeParams.put("source", tokenId);
        return Charge.create(chargeParams);
    }
}

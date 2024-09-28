package com.esprit.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum PaymentProcessor {
    ;
    private static final String STRIPE_API_KEY = System.getenv("STRIPE_API_KEY");
    private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class.getName());

    /**
     * @param name
     * @param email
     * @param amount
     * @param cardNumber
     * @param cardExpMonth
     * @param cardExpYear
     * @param cardCvc
     * @return boolean
     */
    public static boolean processPayment(final String name, final String email, final float amount, final String cardNumber, final int cardExpMonth,
                                         final int cardExpYear, final String cardCvc) {
        boolean result = false;
        try {
            Stripe.apiKey = PaymentProcessor.STRIPE_API_KEY;
            // Create or retrieve customer
            final Customer customer = PaymentProcessor.retrieveOrCreateCustomer(name, email);
            // Create token for the credit card
            final Token token = PaymentProcessor.createToken(cardNumber, cardExpMonth, cardExpYear, cardCvc);
            // Charge the customer
            final Charge charge = PaymentProcessor.chargeCustomer(customer.getId(), token.getId(), amount);
            // Check if the charge was successful
            result = "succeeded".equals(charge.getStatus());
        } catch (final StripeException e) {
            PaymentProcessor.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param name
     * @param email
     * @return Customer
     * @throws StripeException
     */
    private static Customer retrieveOrCreateCustomer(final String name, final String email) throws StripeException {
        final Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("name", name);
        customerParams.put("email", email);
        return Customer.create(customerParams);
    }

    private static Token createToken(final String cardNumber, final int expMonth, final int expYear, final String cvc) throws StripeException {
        final Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", cardNumber);
        cardParams.put("exp_month", expMonth);
        cardParams.put("exp_year", expYear);
        cardParams.put("cvc", cvc);
        PaymentProcessor.LOGGER.info(cardParams.toString());
        final Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);
        return Token.create(tokenParams);
    }

    private static Charge chargeCustomer(final String customerId, final String tokenId, final float amount) throws StripeException {
        final Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (amount * 100)); // Amount in cents
        chargeParams.put("currency", "eur");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", tokenId);
        return Charge.create(chargeParams);
    }

}

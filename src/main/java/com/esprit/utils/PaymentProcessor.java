package com.esprit.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;

import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {

    private static final String STRIPE_API_KEY = "sk_test_51M9YqwA2tc9VjbDkLO3AcupMJW2tJquATnN2jize1vg7O2VZkqDssPzeSEjFviA1rQ076mRxqbKbhsWVZtwUOkjA00y3GKCfsy";

    public static boolean processPayment(String name, String email, float amount, String cardNumber, int cardExpMonth, int cardExpYear, String cardCvc) {
        boolean result = false;
        try {
            Stripe.apiKey = STRIPE_API_KEY;

            // Create or retrieve customer
            Customer customer = retrieveOrCreateCustomer(name, email);

            // Create token for the credit card
            Token token = createToken(cardNumber, cardExpMonth, cardExpYear, cardCvc);

            // Charge the customer
            Charge charge = chargeCustomer(customer.getId(), token.getId(), amount);

            // Check if the charge was successful
            result = charge.getStatus().equals("succeeded");
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Customer retrieveOrCreateCustomer(String name, String email) throws StripeException {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("name", name);
        customerParams.put("email", email);

        return Customer.create(customerParams);
    }

    private static Token createToken(String cardNumber, int expMonth, int expYear, String cvc) throws StripeException {
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", cardNumber);
        cardParams.put("exp_month", expMonth);
        cardParams.put("exp_year", expYear);
        cardParams.put("cvc", cvc);
        System.out.println(cardParams);

        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);

        return Token.create(tokenParams);
    }

    private static Charge chargeCustomer(String customerId, String tokenId, float amount) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (amount * 100)); // Amount in cents
        chargeParams.put("currency", "eur");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", tokenId);

        return Charge.create(chargeParams);
    }
}

package com.esprit.utils;

import com.esprit.services.produits.AvisService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Paymentuser {
    private static final Logger LOGGER = Logger.getLogger(Paymentuser.class.getName());

    /**
     * @param f
     * @return String
     * @throws StripeException
     */
    public static String pay(int f) throws StripeException {
        Stripe.apiKey = "sk_test_51M9YqwA2tc9VjbDkLO3AcupMJW2tJquATnN2jize1vg7O2VZkqDssPzeSEjFviA1rQ076mRxqbKbhsWVZtwUOkjA00y3GKCfsy";
        Map<String, Object> params = new HashMap<>();
        params.put("amount", f);
        params.put("currency", "usd");
        params.put("customer", "cus_PgYn51DmhdjzAw");
        // Get the client's Payment Page URL
        Charge charge = Charge.create(params);
        LOGGER.info(charge.toString());
        return charge.getReceiptUrl();
    }
}

package com.esprit.models;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;

public class StripeFilm {
    public static void main(String[] args) {
// Set your secret key here
        Stripe.apiKey = "sk_test_51M9YqwA2tc9VjbDkLO3AcupMJW2tJquATnN2jize1vg7O2VZkqDssPzeSEjFviA1rQ076mRxqbKbhsWVZtwUOkjA00y3GKCfsy";

        try {
// Retrieve your account information
            Account account = Account.retrieve();
            System.out.println("Account ID: " + account.getId());
// Print other account information as needed
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}

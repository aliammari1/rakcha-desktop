package com.esprit.utils.validators;

import java.util.regex.Pattern;

/**
 * Email validation utility for the RAKCHA application.
 * Provides RFC 5322 compliant email validation.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class EmailValidator {

    // RFC 5322 compliant email regex pattern
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    // Common disposable email domains to block
    private static final String[] DISPOSABLE_DOMAINS = {
        "tempmail.com", "throwaway.email", "guerrillamail.com",
        "mailinator.com", "10minutemail.com", "temp-mail.org"
    };

    /**
     * Validates if an email address is in valid format.
     *
     * @param email the email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim().toLowerCase();

        // Check pattern match
        if (!pattern.matcher(email).matches()) {
            return false;
        }

        // Check for common issues
        if (email.contains("..") || email.startsWith(".") || email.endsWith(".")) {
            return false;
        }

        return true;
    }

    /**
     * Validates email and checks for disposable email domains.
     *
     * @param email the email address to validate
     * @return true if email is valid and not disposable, false otherwise
     */
    public static boolean isValidWithDomain(String email) {
        if (!isValid(email)) {
            return false;
        }

        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();

        for (String disposableDomain : DISPOSABLE_DOMAINS) {
            if (domain.equals(disposableDomain)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a user-friendly validation message for an email.
     *
     * @param email the email address to validate
     * @return validation message, or null if email is valid
     */
    public static String getValidationMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required";
        }

        email = email.trim().toLowerCase();

        if (email.contains("..") || email.startsWith(".") || email.endsWith(".")) {
            return "Email address contains invalid dot placement";
        }

        if (!email.contains("@")) {
            return "Email address must contain '@' symbol";
        }

        if (email.indexOf('@') != email.lastIndexOf('@')) {
            return "Email address cannot contain multiple '@' symbols";
        }

        if (!pattern.matcher(email).matches()) {
            return "Email address format is invalid";
        }

        // Check disposable domains
        String domain = email.substring(email.indexOf('@') + 1);
        for (String disposableDomain : DISPOSABLE_DOMAINS) {
            if (domain.equals(disposableDomain)) {
                return "Disposable email addresses are not allowed";
            }
        }

        return null; // Valid email
    }

    /**
     * Sanitizes an email address by trimming and converting to lowercase.
     *
     * @param email the email address to sanitize
     * @return sanitized email, or null if input is null
     */
    public static String sanitize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}

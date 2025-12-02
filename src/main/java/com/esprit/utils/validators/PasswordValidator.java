package com.esprit.utils.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Password strength validation utility for the RAKCHA application.
 * Validates password strength based on configurable security requirements.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PasswordValidator {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 128;

    // Common weak passwords to blacklist
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "12345678", "qwerty", "abc123",
        "monkey", "1234567", "letmein", "trustno1", "dragon",
        "baseball", "iloveyou", "master", "sunshine", "ashley",
        "bailey", "passw0rd", "shadow", "123123", "654321",
        "superman", "qazwsx", "michael", "football"
    };

    /**
     * Checks if a password meets strength requirements.
     *
     * @param password the password to validate
     * @return true if password is strong, false otherwise
     */
    public static boolean isStrong(String password) {
        return getValidationErrors(password).isEmpty();
    }

    /**
     * Calculates password strength score from 0-100.
     *
     * @param password the password to score
     * @return strength score (0-100)
     */
    public static int getStrengthScore(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Length score (max 30 points)
        if (password.length() >= MIN_LENGTH) {
            score += Math.min(30, password.length() * 2);
        }

        // Character variety (max 40 points)
        if (hasLowerCase(password))
            score += 10;
        if (hasUpperCase(password))
            score += 10;
        if (hasDigit(password))
            score += 10;
        if (hasSpecialChar(password))
            score += 10;

        // Complexity bonus (max 30 points)
        int uniqueChars = (int) password.chars().distinct().count();
        score += Math.min(15, uniqueChars);

        if (!isCommonPassword(password)) {
            score += 15;
        }

        return Math.min(100, score);
    }

    /**
     * Gets a list of validation errors for a password.
     *
     * @param password the password to validate
     * @return list of error messages, empty if valid
     */
    public static List<String> getValidationErrors(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            errors.add("Password must not exceed " + MAX_LENGTH + " characters");
        }

        if (!hasLowerCase(password)) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!hasUpperCase(password)) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!hasDigit(password)) {
            errors.add("Password must contain at least one digit");
        }

        if (!hasSpecialChar(password)) {
            errors.add("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)");
        }

        if (isCommonPassword(password)) {
            errors.add("Password is too common, please choose a more unique password");
        }

        if (hasRepeatedCharacters(password)) {
            errors.add("Password should not contain more than 3 repeated characters in a row");
        }

        return errors;
    }

    /**
     * Generates a strong random password.
     *
     * @return a randomly generated strong password
     */
    public static String generateStrongPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        // Ensure at least one of each required character type
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining characters randomly
        String allChars = lowercase + uppercase + digits + special;
        for (int i = 4; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    /**
     * Gets a user-friendly strength description.
     *
     * @param password the password to evaluate
     * @return strength description (Weak, Fair, Good, Strong)
     */
    public static String getStrengthDescription(String password) {
        int score = getStrengthScore(password);

        if (score < 30)
            return "Weak";
        if (score < 60)
            return "Fair";
        if (score < 80)
            return "Good";
        return "Strong";
    }

    // Private helper methods

    private static boolean hasLowerCase(String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

    private static boolean hasUpperCase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean hasDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private static boolean hasSpecialChar(String password) {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        return password.chars().anyMatch(ch -> specialChars.indexOf(ch) >= 0);
    }

    private static boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        return Arrays.asList(COMMON_PASSWORDS).contains(lowerPassword);
    }

    private static boolean hasRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 3; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                password.charAt(i) == password.charAt(i + 2) &&
                password.charAt(i) == password.charAt(i + 3)) {
                return true;
            }
        }
        return false;
    }
}

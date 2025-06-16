package com.esprit.utils.validation;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import net.synedra.validatorfx.Validator;

/**
 * Utility class for common validation patterns and UI validation helpers.
 * Reduces boilerplate validation code across controllers.
 */
public class ValidationUtils {

    // Common regex patterns
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$"; // International phone format
    public static final String NAME_REGEX = "^[a-zA-ZÀ-ÿ\\s'-]{2,50}$"; // Names with accents and common punctuation
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String NUMBER_REGEX = "^\\d+$";
    public static final String DECIMAL_REGEX = "^\\d*\\.?\\d+$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
    private static final Pattern DECIMAL_PATTERN = Pattern.compile(DECIMAL_REGEX);

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate name format (allows letters, spaces, apostrophes, hyphens)
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validate strong password (at least 8 chars, uppercase, lowercase, digit,
     * special char)
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate numeric string
     */
    public static boolean isValidNumber(String number) {
        return number != null && NUMBER_PATTERN.matcher(number).matches();
    }

    /**
     * Validate decimal number
     */
    public static boolean isValidDecimal(String decimal) {
        return decimal != null && DECIMAL_PATTERN.matcher(decimal).matches();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if string length is within range
     */
    public static boolean isLengthValid(String value, int min, int max) {
        return value != null && value.length() >= min && value.length() <= max;
    }

    /**
     * Create a predicate for email validation
     */
    public static Predicate<String> emailValidator() {
        return ValidationUtils::isValidEmail;
    }

    /**
     * Create a predicate for phone validation
     */
    public static Predicate<String> phoneValidator() {
        return ValidationUtils::isValidPhone;
    }

    /**
     * Create a predicate for name validation
     */
    public static Predicate<String> nameValidator() {
        return ValidationUtils::isValidName;
    }

    /**
     * Create a predicate for password validation
     */
    public static Predicate<String> passwordValidator() {
        return ValidationUtils::isValidPassword;
    }

    /**
     * Create a predicate for required field validation
     */
    public static Predicate<String> requiredValidator() {
        return ValidationUtils::isNotEmpty;
    }

    /**
     * Create a predicate for length validation
     */
    public static Predicate<String> lengthValidator(int min, int max) {
        return value -> isLengthValid(value, min, max);
    }

    /**
     * Add real-time validation to a TextField
     */
    public static void addValidation(TextField textField, Predicate<String> validator, String errorMessage) {
        if (textField == null)
            return;

        Tooltip tooltip = new Tooltip();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !validator.test(newValue)) {
                tooltip.setText(errorMessage);
                textField.setTooltip(tooltip);
                textField.getStyleClass().removeAll("valid-field");
                textField.getStyleClass().add("invalid-field");
            } else {
                textField.setTooltip(null);
                textField.getStyleClass().removeAll("invalid-field");
                textField.getStyleClass().add("valid-field");
            }
        });
    }

    /**
     * Add email validation to a TextField
     */
    public static void addEmailValidation(TextField textField) {
        addValidation(textField, emailValidator(), "Please enter a valid email address");
    }

    /**
     * Add phone validation to a TextField
     */
    public static void addPhoneValidation(TextField textField) {
        addValidation(textField, phoneValidator(), "Please enter a valid phone number");
    }

    /**
     * Add name validation to a TextField
     */
    public static void addNameValidation(TextField textField) {
        addValidation(textField, nameValidator(), "Please enter a valid name (2-50 characters, letters only)");
    }

    /**
     * Add password validation to a TextField
     */
    public static void addPasswordValidation(TextField textField) {
        addValidation(textField, passwordValidator(),
                "Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
    }

    /**
     * Add required field validation to a TextField
     */
    public static void addRequiredValidation(TextField textField, String fieldName) {
        addValidation(textField, requiredValidator(), fieldName + " is required");
    }

    /**
     * Validate multiple fields and return true if all are valid
     */
    public static boolean validateFields(TextField... fields) {
        for (TextField field : fields) {
            if (field.getStyleClass().contains("invalid-field")
                    || (field.getText() == null || field.getText().trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a Validator instance with common validation rules
     */
    public static Validator createValidator() {
        return new Validator();
    }

    /**
     * Validation builder for fluent validation setup
     */
    public static class ValidationBuilder {
        private final TextField textField;
        private Predicate<String> validator = value -> true;
        private String errorMessage = "Invalid input";

        /**
         * Performs ValidationBuilder operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder(TextField textField) {
            this.textField = textField;
        }

        /**
         * Performs email operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder email() {
            this.validator = this.validator.and(emailValidator());
            this.errorMessage = "Please enter a valid email address";
            return this;
        }

        /**
         * Performs phone operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder phone() {
            this.validator = this.validator.and(phoneValidator());
            this.errorMessage = "Please enter a valid phone number";
            return this;
        }

        /**
         * Performs name operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder name() {
            this.validator = this.validator.and(nameValidator());
            this.errorMessage = "Please enter a valid name";
            return this;
        }

        /**
         * Performs password operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder password() {
            this.validator = this.validator.and(passwordValidator());
            this.errorMessage = "Password must meet security requirements";
            return this;
        }

        /**
         * Performs required operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder required() {
            this.validator = this.validator.and(requiredValidator());
            this.errorMessage = "This field is required";
            return this;
        }

        /**
         * Performs length operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder length(int min, int max) {
            this.validator = this.validator.and(lengthValidator(min, max));
            this.errorMessage = String.format("Length must be between %d and %d characters", min, max);
            return this;
        }

        /**
         * Performs custom operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder custom(Predicate<String> customValidator, String customMessage) {
            this.validator = this.validator.and(customValidator);
            this.errorMessage = customMessage;
            return this;
        }

        /**
         * Performs message operation.
         *
         * @return the result of the operation
         */
        public ValidationBuilder message(String message) {
            this.errorMessage = message;
            return this;
        }

        /**
         * Performs apply operation.
         *
         * @return the result of the operation
         */
        public void apply() {
            addValidation(textField, validator, errorMessage);
        }
    }

    /**
     * Create a validation builder for a TextField
     */
    public static ValidationBuilder forField(TextField textField) {
        return new ValidationBuilder(textField);
    }
}

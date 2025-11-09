package com.esprit.utils;

import com.esprit.models.users.User;
import com.esprit.models.users.Client;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory class for creating test data for RAKCHA UI tests.
 * Uses JavaFaker to generate realistic test data.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestDataFactory {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    // Valid test credentials
    public static class TestCredentials {
        public static final String VALID_EMAIL = "test.user@rakcha.com";
        public static final String VALID_PASSWORD = "Test@1234";
        public static final String ADMIN_EMAIL = "admin@rakcha.com";
        public static final String ADMIN_PASSWORD = "Admin@1234";
        public static final String CINEMA_MANAGER_EMAIL = "manager@rakcha.com";
        public static final String CINEMA_MANAGER_PASSWORD = "Manager@1234";
    }

    // Invalid test data
    public static class InvalidData {
        public static final String INVALID_EMAIL = "invalid-email";
        public static final String EMPTY_STRING = "";
        public static final String TOO_SHORT_PASSWORD = "123";
        public static final String WEAK_PASSWORD = "password";
        public static final String MISSING_SPECIAL_CHAR_PASSWORD = "Password123";
    }

    /**
     * Generate a random valid email
     * 
     * @return Valid email address
     */
    public static String generateEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * Generate a random valid password
     * Includes uppercase, lowercase, numbers, and special characters
     * 
     * @return Valid password
     */
    public static String generateValidPassword() {
        String upper = faker.regexify("[A-Z]{2}");
        String lower = faker.regexify("[a-z]{4}");
        String digits = faker.regexify("[0-9]{2}");
        String special = faker.regexify("[!@#$%^&*]{2}");
        return upper + lower + digits + special;
    }

    /**
     * Generate a random username
     * 
     * @return Username
     */
    public static String generateUsername() {
        return faker.name().username();
    }

    /**
     * Generate a random first name
     * 
     * @return First name
     */
    public static String generateFirstName() {
        return faker.name().firstName();
    }

    /**
     * Generate a random last name
     * 
     * @return Last name
     */
    public static String generateLastName() {
        return faker.name().lastName();
    }

    /**
     * Generate a random phone number
     * 
     * @return Phone number
     */
    public static String generatePhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    /**
     * Generate a random address
     * 
     * @return Address
     */
    public static String generateAddress() {
        return faker.address().fullAddress();
    }

    /**
     * Generate a random cinema name
     * 
     * @return Cinema name
     */
    public static String generateCinemaName() {
        return faker.company().name() + " Cinema";
    }

    /**
     * Generate a random movie title
     * 
     * @return Movie title
     */
    public static String generateMovieTitle() {
        return faker.book().title();
    }

    /**
     * Generate a random movie description
     * 
     * @return Movie description
     */
    public static String generateMovieDescription() {
        return faker.lorem().paragraph();
    }

    /**
     * Generate a random year between 1990 and current year
     * 
     * @return Year
     */
    public static int generateMovieYear() {
        return faker.number().numberBetween(1990, 2025);
    }

    /**
     * Generate a random duration in minutes
     * 
     * @return Duration in minutes
     */
    public static int generateMovieDuration() {
        return faker.number().numberBetween(80, 180);
    }

    /**
     * Generate a random rating between 1 and 5
     * 
     * @return Rating
     */
    public static double generateRating() {
        return 1 + (random.nextDouble() * 4); // 1.0 to 5.0
    }

    /**
     * Generate a random price
     * 
     * @return Price
     */
    public static double generatePrice() {
        return faker.number().randomDouble(2, 5, 50);
    }

    /**
     * Generate a list of random emails
     * 
     * @param count Number of emails to generate
     * @return List of email addresses
     */
    public static List<String> generateEmails(int count) {
        List<String> emails = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            emails.add(generateEmail());
        }
        return emails;
    }

    /**
     * Generate a random cinema capacity
     * 
     * @return Capacity
     */
    public static int generateCinemaCapacity() {
        return faker.number().numberBetween(50, 500);
    }

    /**
     * Generate a random seat number
     * 
     * @return Seat number (e.g., "A12")
     */
    public static String generateSeatNumber() {
        char row = (char) ('A' + random.nextInt(15)); // A-O
        int number = random.nextInt(20) + 1; // 1-20
        return row + String.valueOf(number);
    }

    /**
     * Generate a random genre
     * 
     * @return Movie genre
     */
    public static String generateGenre() {
        String[] genres = {
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi",
                "Thriller", "Romance", "Animation", "Documentary", "Fantasy"
        };
        return genres[random.nextInt(genres.length)];
    }

    /**
     * Generate a random actor name
     * 
     * @return Actor name
     */
    public static String generateActorName() {
        return faker.name().fullName();
    }

    /**
     * Generate a random director name
     * 
     * @return Director name
     */
    public static String generateDirectorName() {
        return faker.name().fullName();
    }

    /**
     * Generate test User object
     * 
     * @return User object for testing
     */
    public static Client generateTestUser() {
        // Note: Adjust this based on your User model constructor
        // This is a placeholder implementation
        return Client.builder()
                .lastName(generateLastName())
                .firstName(generateFirstName())
                .phoneNumber("12345678")
                .password(generateValidPassword())
                .role("CLIENT")
                .address("Test Address")
                .birthDate(new java.sql.Date(System.currentTimeMillis()))
                .email(generateEmail())
                .photoDeProfil("default.png")
                .build();
    }

    /**
     * Generate a random credit card number (test only)
     * 
     * @return Credit card number
     */
    public static String generateCreditCardNumber() {
        return faker.business().creditCardNumber();
    }

    /**
     * Generate a random CVV
     * 
     * @return CVV code
     */
    public static String generateCVV() {
        return String.format("%03d", random.nextInt(1000));
    }

    /**
     * Generate a random booking reference
     * 
     * @return Booking reference
     */
    public static String generateBookingReference() {
        return "BK" + faker.number().digits(8);
    }

    /**
     * Generate a random comment/review
     * 
     * @return Comment text
     */
    public static String generateComment() {
        return faker.lorem().sentence();
    }

    /**
     * Generate a random URL
     * 
     * @return URL
     */
    public static String generateUrl() {
        return faker.internet().url();
    }
}

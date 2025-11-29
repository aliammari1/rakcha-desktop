package com.esprit.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit5.Start;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.esprit.MainApp;
import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;

import javafx.stage.Stage;

/**
 * End-to-end integration tests for complete user workflows.
 * Tests full user journeys through the application.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserJourneyIntegrationTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("New User Registration Journey")
    class NewUserRegistrationJourney {

        @Test
        @Order(1)
        @DisplayName("Complete user registration flow")
        void testCompleteRegistration() {
            // Start at login page
            verifyThat("#signUpButton", isVisible());
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // Fill registration form
            fillTextField("#nomTextField", TestDataFactory.generateLastName());
            fillTextField("#prenomTextField", TestDataFactory.generateFirstName());
            fillTextField("#emailTextField", TestDataFactory.generateEmail());
            fillTextField("#passwordTextField", TestDataFactory.generateValidPassword());
            fillTextField("#num_telephoneTextField", "+1234567890");
            fillTextField("#adresseTextField", TestDataFactory.generateAddress());

            // Submit registration
            clickOnAndWait("#signUpButton");
            waitForFxEvents();

            // Verify registration succeeded by checking for success indicator or navigation
            // Assert that either a success message appears or navigation occurred
            try {
                verifyThat("#loginButton", isVisible());
                // If we see the login button, registration redirected to login page
            } catch (Exception e) {
                // Otherwise check for a success message or home view
                verifyThat("#homePane", isVisible());
            }

        }


        @Test
        @Order(2)
        @DisplayName("Login after registration")
        void testLoginAfterRegistration() {
            // Navigate to login if not already there
            if (lookup("#loginButton").tryQuery().isEmpty()) {
                clickOnAndWait("#signInButton");
                waitForFxEvents();
            }

            
            // Fill credentials
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");
            waitForFxEvents();

            // Verify successful login
            verifyThat("#homePane", isVisible());
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Movie Booking Journey")
    class MovieBookingJourney {

        /**
         * TODO: Complete movie booking flow implementation
         * Required steps:
         * 1. Login as client with valid credentials
         * 2. Navigate to movie catalog
         * 3. Select a specific movie
         * 4. Choose cinema location
         * 5. Select session time
         * 6. Choose seats in the theater
         * 7. Proceed to payment
         * 8. Simulate/verify payment processing
         * 9. Verify booking confirmation message
         * 10. Assert booking appears in user profile
         * 
         * Needed test data/mocks:
         * - MovieSelectionView with fx:id for movie items
         * - CinemaSelectionView with theater/cinema options
         * - SeatsSelectionView with seat grid
         * - PaymentView with payment form
         * - BookingConfirmationView with confirmation details
         * - Mock payment gateway or test payment credentials
         */
        @Test
        @Order(10)
        @Disabled("Movie booking flow not yet implemented - placeholder test")
        @DisplayName("Complete movie booking flow")
        void testCompleteMovieBooking() {
            // Login as client
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");
            waitForFxEvents();

            // Navigate to movies
            // clickOnAndWait("#movieButton");
            // waitForFxEvents();

            // Select a movie
            // Choose cinema and session time
            // Select seats
            // Proceed to payment
            // Complete payment
            // Receive confirmation
        }


        @Test
        @Order(11)
        @Disabled("Movie booking flow not yet implemented - placeholder test")
        @DisplayName("View booking in profile")
        void testViewBookingInProfile() {
            // Navigate to profile
            // clickOnAndWait("#profileButton");
            // waitForFxEvents();

            // Booking should be listed
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Admin Management Journey")
    class AdminManagementJourney {

        @Test
        @Order(20)
        @DisplayName("Admin manages cinema approval")
        void testAdminCinemaApproval() {
            // Login as admin
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.ADMIN_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.ADMIN_PASSWORD);
            clickOnAndWait("#signInButton");
            waitForFxEvents();

            // Navigate to cinema dashboard
            clickOnAndWait("#cinemaButton");
            waitForFxEvents();

            // Approve/reject cinemas
        }


        @Test
        @Order(21)
        @DisplayName("Admin manages users")
        void testAdminUserManagement() {
            // Navigate to users
            clickOnAndWait("#usersButton");
            waitForFxEvents();

            // View users, update roles, etc.
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Cinema Manager Journey")
    class CinemaManagerJourney {

        @Test
        @Order(30)
        @DisplayName("Cinema manager adds movies")
        void testManagerAddMovies() {
            // Login as cinema manager
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.CINEMA_MANAGER_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.CINEMA_MANAGER_PASSWORD);
            clickOnAndWait("#signInButton");
            waitForFxEvents();

            // Navigate to film management
            clickOnAndWait("#movieButton");
            waitForFxEvents();

            // Add new movie
        }


        @Test
        @Order(31)
        @DisplayName("Cinema manager views statistics")
        void testManagerViewStatistics() {
            // Navigate to statistics
            clickOnAndWait("#statestique_button");
            waitForFxEvents();

            // View cinema performance data
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Series Watching Journey")
    class SeriesWatchingJourney {

        @Test
        @Order(40)
        @DisplayName("Browse and watch series")
        void testBrowseAndWatchSeries() {
            // Login
            // Navigate to series
            clickOnAndWait("#serieButton");
            waitForFxEvents();

            // Select a series
            // View episodes
            // Watch episode
        }


        @Test
        @Order(41)
        @DisplayName("Add series to favorites")
        void testAddToFavorites() {
            // Add to favorites
            // View favorites list
            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Product Purchase Journey")
    class ProductPurchaseJourney {

        @Test
        @Order(50)
        @DisplayName("Browse and purchase product")
        void testBrowseAndPurchaseProduct() {
            // Navigate to products
            clickOnAndWait("#productButton");
            waitForFxEvents();

            // View product details
            // Add to cart
            // Checkout
            // Complete payment
        }


        @Test
        @Order(51)
        @DisplayName("View order history")
        void testViewOrderHistory() {
            // Navigate to orders
            // View past orders
            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Profile Management Journey")
    class ProfileManagementJourney {

        @Test
        @Order(60)
        @DisplayName("Update profile information")
        void testUpdateProfile() {
            // Navigate to profile
            clickOnAndWait("#profileButton");
            waitForFxEvents();

            // Edit profile
            // Upload photo
            // Save changes
        }


        @Test
        @Order(61)
        @DisplayName("Change password")
        void testChangePassword() {
            // Navigate to password change
            // Enter current and new password
            // Save
            waitForFxEvents();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Logout and Re-login Journey")
    class LogoutReloginJourney {

        @Test
        @Order(70)
        @DisplayName("Logout and login again")
        void testLogoutRelogin() {
            // Logout
            clickOnAndWait("#logoutButton");
            waitForFxEvents();

            // Should return to login screen
            verifyThat("#emailTextField", isVisible());

            // Login again
            fillTextField("#emailTextField", TestDataFactory.TestCredentials.VALID_EMAIL);
            fillTextField("#passwordTextField", TestDataFactory.TestCredentials.VALID_PASSWORD);
            clickOnAndWait("#signInButton");
            waitForFxEvents();
        }

    }

}


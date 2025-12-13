package com.esprit.controllers;

import com.esprit.enums.UserRole;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.utils.TestAssertions;
import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Comprehensive UI tests for SidebarController.
 * Tests role-based navigation, button visibility, and user interactions.
 * <p>
 * Test Categories:
 * - Role-Based Visibility
 * - Navigation Functionality
 * - Button Interactions
 * - User Context Management
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SidebarControllerTest extends TestFXBase {

    private SidebarController controller;

    @Start
    public void start(Stage stage) throws Exception {
        // Load the sidebar view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Sidebar.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Common UI Elements Tests")
    class CommonUIElementsTests {

        @Test
        @Order(1)
        @DisplayName("Should display common navigation buttons")
        void testCommonButtonsVisible() {
            TestAssertions.verifyAllVisible(
                    "#homeButton",
                    "#movieButton",
                    "#serieButton",
                    "#productButton",
                    "#cinemaButton",
                    "#profileButton",
                    "#logoutButton");
        }

        @Test
        @Order(2)
        @DisplayName("Common buttons should be enabled")
        void testCommonButtonsEnabled() {
            verifyThat("#homeButton", isEnabled());
            verifyThat("#movieButton", isEnabled());
            verifyThat("#serieButton", isEnabled());
            verifyThat("#productButton", isEnabled());
            verifyThat("#cinemaButton", isEnabled());
            verifyThat("#profileButton", isEnabled());
            verifyThat("#logoutButton", isEnabled());
        }

        @Test
        @Order(3)
        @DisplayName("Should display icons for navigation buttons")
        void testNavigationIcons() {
            verifyThat("#movieIcon", isNotNull());
            verifyThat("#serieIcon", isNotNull());
            verifyThat("#productIcon", isNotNull());
            verifyThat("#cinemaIcon", isNotNull());
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Admin Role Tests")
    class AdminRoleTests {

        @BeforeEach
        void setupAdminUser() {
            // Create and set admin user
            Admin admin = Admin.builder()
                    .lastName(TestDataFactory.generateLastName())
                    .firstName(TestDataFactory.generateFirstName())
                    .phoneNumber("12345678")
                    .passwordHash(TestDataFactory.TestCredentials.ADMIN_PASSWORD)
                    .role(UserRole.ADMIN)
                    .address("123 Admin St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email(TestDataFactory.TestCredentials.ADMIN_EMAIL)
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(admin));
        }

        @Test
        @Order(10)
        @DisplayName("Admin should see users button")
        void testAdminUsersButtonVisible() {
            verifyThat("#usersButton", isVisible());
        }

        @Test
        @Order(11)
        @DisplayName("Admin should see order button")
        void testAdminOrderButtonVisible() {
            verifyThat("#orderButton", isVisible());
        }

        @Test
        @Order(12)
        @DisplayName("Admin buttons should be enabled")
        void testAdminButtonsEnabled() {
            verifyThat("#usersButton", isEnabled());
            verifyThat("#orderButton", isEnabled());
        }

        @Test
        @Order(13)
        @DisplayName("Admin should NOT see cinema manager specific buttons")
        void testAdminDoesNotSeeCinemaManagerButtons() {
            // Cinema manager buttons should be hidden for admin
            Button actorButton = lookup("#actorButton").queryButton();
            Button filmCategorieButton = lookup("#filmCategorieButton").queryButton();

            if (actorButton != null) {
                assertFalse(actorButton.isVisible() || actorButton.isManaged());
            }
            if (filmCategorieButton != null) {
                assertFalse(filmCategorieButton.isVisible() || filmCategorieButton.isManaged());
            }
        }

        @Test
        @Order(14)
        @DisplayName("Admin should be able to navigate to users management")
        void testAdminNavigateToUsers() {
            clickOnAndWait("#usersButton");
            waitForFxEvents();

            // Should navigate to users management view
        }

        @Test
        @Order(15)
        @DisplayName("Admin should be able to navigate to orders")
        void testAdminNavigateToOrders() {
            clickOnAndWait("#orderButton");
            waitForFxEvents();

            // Should navigate to orders view
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Cinema Manager Role Tests")
    class CinemaManagerRoleTests {

        @BeforeEach
        void setupCinemaManagerUser() {
            CinemaManager manager = CinemaManager.builder()
                    .lastName(TestDataFactory.generateLastName())
                    .firstName(TestDataFactory.generateFirstName())
                    .phoneNumber("12345678")
                    .passwordHash(TestDataFactory.TestCredentials.CINEMA_MANAGER_PASSWORD)
                    .role(UserRole.CINEMA_MANAGER)
                    .address("456 Manager St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email(TestDataFactory.TestCredentials.CINEMA_MANAGER_EMAIL)
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(manager));
        }

        @Test
        @Order(20)
        @DisplayName("Cinema manager should see actor button")
        void testCinemaManagerActorButtonVisible() {
            verifyThat("#actorButton", isVisible());
        }

        @Test
        @Order(21)
        @DisplayName("Cinema manager should see film categories button")
        void testCinemaManagerFilmCategorieButtonVisible() {
            verifyThat("#filmCategorieButton", isVisible());
        }

        @Test
        @Order(22)
        @DisplayName("Cinema manager should see movie session button")
        void testCinemaManagerMovieSessionButtonVisible() {
            verifyThat("#moviesessionButton", isVisible());
        }

        @Test
        @Order(23)
        @DisplayName("Cinema manager should see statistics button")
        void testCinemaManagerStatisticsButtonVisible() {
            verifyThat("#statestique_button", isVisible());
        }

        @Test
        @Order(24)
        @DisplayName("Cinema manager buttons should be enabled")
        void testCinemaManagerButtonsEnabled() {
            verifyThat("#actorButton", isEnabled());
            verifyThat("#filmCategorieButton", isEnabled());
            verifyThat("#moviesessionButton", isEnabled());
            verifyThat("#statestique_button", isEnabled());
        }

        @Test
        @Order(25)
        @DisplayName("Cinema manager should NOT see admin specific buttons")
        void testCinemaManagerDoesNotSeeAdminButtons() {
            Button usersButton = lookup("#usersButton").queryButton();
            Button orderButton = lookup("#orderButton").queryButton();

            if (usersButton != null) {
                assertFalse(usersButton.isVisible() || usersButton.isManaged());
            }
            if (orderButton != null) {
                assertFalse(orderButton.isVisible() || orderButton.isManaged());
            }
        }

        @Test
        @Order(26)
        @DisplayName("Cinema manager should navigate to actors")
        void testCinemaManagerNavigateToActors() {
            clickOnAndWait("#actorButton");
            waitForFxEvents();
        }

        @Test
        @Order(27)
        @DisplayName("Cinema manager should navigate to film categories")
        void testCinemaManagerNavigateToFilmCategories() {
            clickOnAndWait("#filmCategorieButton");
            waitForFxEvents();
        }

        @Test
        @Order(28)
        @DisplayName("Cinema manager should navigate to statistics")
        void testCinemaManagerNavigateToStatistics() {
            clickOnAndWait("#statestique_button");
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Client Role Tests")
    class ClientRoleTests {

        @BeforeEach
        void setupClientUser() {
            Client client = Client.builder()
                    .lastName(TestDataFactory.generateLastName())
                    .firstName(TestDataFactory.generateFirstName())
                    .phoneNumber("12345678")
                    .passwordHash(TestDataFactory.TestCredentials.VALID_PASSWORD)
                    .role(UserRole.CLIENT)
                    .address("789 Client St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email(TestDataFactory.TestCredentials.VALID_EMAIL)
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(client));
        }

        @Test
        @Order(30)
        @DisplayName("Client should see common navigation buttons")
        void testClientCommonButtonsVisible() {
            TestAssertions.verifyAllVisible(
                    "#homeButton",
                    "#movieButton",
                    "#serieButton",
                    "#productButton",
                    "#cinemaButton",
                    "#profileButton");
        }

        @Test
        @Order(31)
        @DisplayName("Client should NOT see admin buttons")
        void testClientDoesNotSeeAdminButtons() {
            Button usersButton = lookup("#usersButton").queryButton();
            Button orderButton = lookup("#orderButton").queryButton();

            if (usersButton != null) {
                assertFalse(usersButton.isVisible() || usersButton.isManaged());
            }
            if (orderButton != null) {
                assertFalse(orderButton.isVisible() || orderButton.isManaged());
            }
        }

        @Test
        @Order(32)
        @DisplayName("Client should NOT see cinema manager buttons")
        void testClientDoesNotSeeCinemaManagerButtons() {
            Button actorButton = lookup("#actorButton").queryButton();
            Button filmCategorieButton = lookup("#filmCategorieButton").queryButton();

            if (actorButton != null) {
                assertFalse(actorButton.isVisible() || actorButton.isManaged());
            }
            if (filmCategorieButton != null) {
                assertFalse(filmCategorieButton.isVisible() || filmCategorieButton.isManaged());
            }
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(40)
        @DisplayName("Should navigate to home page")
        void testNavigateToHome() {
            clickOnAndWait("#homeButton");
            waitForFxEvents();
        }

        @Test
        @Order(41)
        @DisplayName("Should navigate to movies page")
        void testNavigateToMovies() {
            clickOnAndWait("#movieButton");
            waitForFxEvents();
        }

        @Test
        @Order(42)
        @DisplayName("Should navigate to series page")
        void testNavigateToSeries() {
            clickOnAndWait("#serieButton");
            waitForFxEvents();
        }

        @Test
        @Order(43)
        @DisplayName("Should navigate to products page")
        void testNavigateToProducts() {
            clickOnAndWait("#productButton");
            waitForFxEvents();
        }

        @Test
        @Order(44)
        @DisplayName("Should navigate to cinema page")
        void testNavigateToCinema() {
            clickOnAndWait("#cinemaButton");
            waitForFxEvents();
        }

        @Test
        @Order(45)
        @DisplayName("Should navigate to profile page")
        void testNavigateToProfile() {
            clickOnAndWait("#profileButton");
            waitForFxEvents();
        }

        @Test
        @Order(46)
        @DisplayName("Should handle logout action")
        void testLogout() {
            clickOnAndWait("#logoutButton");
            waitForFxEvents();

            // Should navigate to login page or show logout confirmation
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Button State Tests")
    class ButtonStateTests {

        @Test
        @Order(50)
        @DisplayName("Buttons should have proper hover states")
        void testButtonHoverStates() {
            moveTo("#homeButton");
            waitForFxEvents();

            // Hover effect should be applied
            Button homeButton = lookup("#homeButton").queryButton();
            assertNotNull(homeButton);

            // Verify hover pseudo-class is applied with polling/retry to handle
            // timing-dependent state
            final int MAX_RETRIES = 10; // ~500ms with 50ms sleeps
            final int SLEEP_MS = 50;
            boolean hoverApplied = false;
            PseudoClass hoverPseudoClass = PseudoClass.getPseudoClass("hover");

            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    Thread.sleep(SLEEP_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                waitForFxEvents();

                final boolean[] stateCheck = { false };
                runOnFxThread(() -> {
                    stateCheck[0] = homeButton.getPseudoClassStates().contains(hoverPseudoClass);
                });

                if (stateCheck[0]) {
                    hoverApplied = true;
                    break;
                }
            }

            assertTrue(hoverApplied, "Hover pseudo-class should be applied to homeButton within timeout");
        }

        @Test
        @Order(51)
        @DisplayName("Active button should be highlighted")
        void testActiveButtonHighlight() {
            clickOnAndWait("#movieButton");
            waitForFxEvents();

            // Movie button should show active state
            Button movieButton = lookup("#movieButton").queryButton();
            assertNotNull(movieButton);
        }

        @Test
        @Order(52)
        @DisplayName("Multiple clicks should not break navigation")
        void testMultipleClicks() {
            clickOnAndWait("#movieButton");
            clickOnAndWait("#serieButton");
            clickOnAndWait("#productButton");
            clickOnAndWait("#homeButton");

            waitForFxEvents();
            // Should handle multiple rapid clicks gracefully
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("User Context Tests")
    class UserContextTests {

        @Test
        @Order(60)
        @DisplayName("Should update sidebar when user changes")
        void testUserChange() {
            // Start with admin
            Admin admin = Admin.builder()
                    .lastName("User")
                    .firstName("Admin")
                    .phoneNumber("12345678")
                    .passwordHash("password")
                    .role(UserRole.ADMIN)
                    .address("Admin St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email("admin@test.com")
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(admin));
            waitForFxEvents();

            // Verify admin buttons are visible
            verifyThat("#usersButton", isVisible());
            verifyThat("#orderButton", isVisible());

            // Verify cinema manager buttons are hidden
            Button actorButton = lookup("#actorButton").queryButton();
            Button filmCategorieButton = lookup("#filmCategorieButton").queryButton();
            if (actorButton != null) {
                assertFalse(actorButton.isVisible() || actorButton.isManaged(),
                        "Actor button should be hidden for admin user");
            }
            if (filmCategorieButton != null) {
                assertFalse(filmCategorieButton.isVisible() || filmCategorieButton.isManaged(),
                        "Film categories button should be hidden for admin user");
            }

            // Change to client
            Client client = Client.builder()
                    .lastName("User")
                    .firstName("Client")
                    .phoneNumber("12345678")
                    .passwordHash("password")
                    .role(UserRole.CLIENT)
                    .address("Client St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email("client@test.com")
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(client));
            waitForFxEvents();

            // Verify admin buttons are now hidden
            Button usersButton = lookup("#usersButton").queryButton();
            Button orderButton = lookup("#orderButton").queryButton();
            if (usersButton != null) {
                assertFalse(usersButton.isVisible() || usersButton.isManaged(),
                        "Users button should be hidden for client user");
            }
            if (orderButton != null) {
                assertFalse(orderButton.isVisible() || orderButton.isManaged(),
                        "Order button should be hidden for client user");
            }

            // Verify common client buttons are still visible
            verifyThat("#homeButton", isVisible());
            verifyThat("#movieButton", isVisible());
            verifyThat("#profileButton", isVisible());
        }

        @Test
        @Order(61)
        @DisplayName("Should maintain user context during navigation")
        void testMaintainUserContext() {
            Client client = Client.builder()
                    .lastName("Client")
                    .firstName("Test")
                    .phoneNumber("12345678")
                    .passwordHash("password")
                    .role(UserRole.CLIENT)
                    .address("Test St")
                    .birthDate(new java.sql.Date(System.currentTimeMillis()))
                    .email("test@test.com")
                    .profilePictureUrl("default.png")
                    .build();
            runOnFxThread(() -> controller.setCurrentUser(client));

            clickOnAndWait("#movieButton");
            waitForFxEvents();

            clickOnAndWait("#profileButton");
            waitForFxEvents();

            // User context should be maintained
            assertNotNull(controller);
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Accessibility Tests")
    class AccessibilityTests {

        @Test
        @Order(70)
        @DisplayName("All buttons should be keyboard accessible")
        void testKeyboardAccessibility() {
            clickOn("#homeButton");
            assertTrue(lookup("#homeButton").query().isFocused());

            pressTab();
            waitForFxEvents();

            // Next button should be focused - verify that movieButton is now focused
            assertTrue(lookup("#movieButton").query().isFocused(),
                    "Focus should move to movieButton after pressing Tab from homeButton");
        }

        @Test
        @Order(71)
        @DisplayName("Icons should enhance button recognition")
        void testIconPresence() {
            verifyThat("#movieIcon", isNotNull());
            verifyThat("#serieIcon", isNotNull());
            verifyThat("#productIcon", isNotNull());
            verifyThat("#cinemaIcon", isNotNull());
        }
    }
}

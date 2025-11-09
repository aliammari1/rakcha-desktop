package com.esprit.controllers.users;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import org.testfx.framework.junit5.Start;

import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for HomeCinemaManagerController.
 * Tests cinema manager dashboard and cinema management features.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeCinemaManagerControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/HomeCinemaManager.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Manager Dashboard Tests")
    class ManagerDashboardTests {

        @Test
        @Order(1)
        @DisplayName("Should display cinema manager dashboard")
        void testDashboardDisplay() {
            waitForFxEvents();

            VBox dashboard = lookup("#managerDashboard").query();
            assertThat(dashboard).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should display welcome message")
        void testWelcomeMessage() {
            waitForFxEvents();

            Label welcomeLabel = lookup("#welcomeLabel").query();
            assertThat(welcomeLabel).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cinema Overview Tests")
    class CinemaOverviewTests {

        @Test
        @Order(3)
        @DisplayName("Should display cinema overview")
        void testCinemaOverview() {
            waitForFxEvents();

            assertThat(lookup("#cinemaOverview").tryQuery()).isPresent();
        }

        @Test
        @Order(4)
        @DisplayName("Should navigate to cinema details")
        void testNavigateToCinemaDetails() {
            waitForFxEvents();

            Button detailsButton = lookup("#cinemaDetailsButton").query();
            assertThat(detailsButton).isNotNull();
            clickOn(detailsButton);

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Session Management Tests")
    class SessionManagementTests {

        @Test
        @Order(5)
        @DisplayName("Should display session management section")
        void testSessionManagementSection() {
            waitForFxEvents();

            assertThat(lookup("#sessionManagementSection").tryQuery()).isPresent();
        }

        @Test
        @Order(6)
        @DisplayName("Should navigate to add session")
        void testNavigateToAddSession() {
            waitForFxEvents();

            Button addSessionButton = lookup("#addSessionButton").query();
            assertThat(addSessionButton).isNotNull();
            clickOn(addSessionButton);

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @Order(7)
        @DisplayName("Should display cinema statistics")
        void testStatisticsDisplay() {
            waitForFxEvents();

            assertThat(lookup("#statisticsPanel").tryQuery()).isPresent();
        }

        @Test
        @Order(8)
        @DisplayName("Should show total bookings")
        void testTotalBookings() {
            waitForFxEvents();

            Label bookingsLabel = lookup("#totalBookingsLabel").query();
            assertThat(bookingsLabel).isNotNull();
        }
    }
}

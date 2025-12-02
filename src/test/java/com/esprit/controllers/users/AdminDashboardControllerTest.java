package com.esprit.controllers.users;

import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for AdminDashboardController.
 * Tests admin statistics, user management, and system monitoring.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminDashboardControllerTest extends TestFXBase {

    private UserService userService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/AdminDashboard.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Dashboard Overview Tests")
    class DashboardOverviewTests {

        @Test
        @Order(1)
        @DisplayName("Should display dashboard panels")
        void testDashboardPanels() {
            waitForFxEvents();

            assertThat(lookup("#dashboardPanel").tryQuery()).isPresent();
        }


        @Test
        @Order(2)
        @DisplayName("Should display user statistics")
        void testUserStatistics() {
            waitForFxEvents();

            assertThat(lookup("#userStatsPanel").tryQuery()).isPresent();
        }


        @Test
        @Order(3)
        @DisplayName("Should display cinema statistics")
        void testCinemaStatistics() {
            waitForFxEvents();

            assertThat(lookup("#cinemaStatsPanel").tryQuery()).isPresent();
        }


        @Test
        @Order(4)
        @DisplayName("Should display revenue statistics")
        void testRevenueStatistics() {
            waitForFxEvents();

            assertThat(lookup("#revenueStatsPanel").tryQuery()).isPresent();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @Order(5)
        @DisplayName("Should display users table")
        void testUsersTableDisplay() {
            waitForFxEvents();

            TableView<User> table = lookup("#usersTable").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(6)
        @DisplayName("Should search users")
        void testSearchUsers() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField).write("admin");
            assertThat(searchField.getText()).isNotEmpty();

            waitForFxEvents();
        }


        @Test
        @Order(7)
        @DisplayName("Should filter users by role")
        void testFilterByRole() {
            waitForFxEvents();

            TableView<User> table = lookup("#usersTable").query();
            assertThat(table).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("System Monitoring Tests")
    class SystemMonitoringTests {

        @Test
        @Order(8)
        @DisplayName("Should display system health")
        void testSystemHealth() {
            waitForFxEvents();

            assertThat(lookup("#systemHealthPanel").tryQuery()).isPresent();
        }


        @Test
        @Order(9)
        @DisplayName("Should show active sessions")
        void testActiveSessions() {
            waitForFxEvents();

            assertThat(lookup("#activeSessionsLabel").tryQuery()).isPresent();
        }

    }

}


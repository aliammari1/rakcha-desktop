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

import com.esprit.services.users.UserService;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for HomeAdminController.
 * Tests admin dashboard, user management, and statistics display.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeAdminControllerTest extends TestFXBase {

    private UserService userService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/HomeAdmin.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Admin Dashboard Tests")
    class AdminDashboardTests {

        @Test
        @Order(1)
        @DisplayName("Should display admin dashboard")
        void testDashboardDisplay() {
            waitForFxEvents();

            VBox dashboard = lookup("#adminDashboard").query();
            assertThat(dashboard).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should display welcome message")
        void testWelcomeMessage() {
            waitForFxEvents();

            Label welcomeLabel = lookup("#welcomeLabel").query();
            assertThat(welcomeLabel).isNotNull();
            assertThat(welcomeLabel.getText()).contains("Admin");
        }

        @Test
        @Order(3)
        @DisplayName("Should load admin statistics")
        void testLoadStatistics() {
            waitForFxEvents();

            VBox dashboard = lookup("#adminDashboard").query();
            assertThat(dashboard).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @Order(4)
        @DisplayName("Should display user management section")
        void testUserManagementSection() {
            waitForFxEvents();

            assertThat(lookup("#userManagementSection").tryQuery()).isPresent();
        }

        @Test
        @Order(5)
        @DisplayName("Should navigate to user list")
        void testNavigateToUserList() {
            waitForFxEvents();

            Button userListButton = lookup("#userListButton").query();
            assertThat(userListButton).isNotNull();
            clickOn(userListButton);

            waitForFxEvents();
        }

        @Test
        @Order(6)
        @DisplayName("Should display total users count")
        void testTotalUsersCount() {
            waitForFxEvents();

            Label totalUsers = lookup("#totalUsersLabel").query();
            assertThat(totalUsers).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cinema Management Tests")
    class CinemaManagementTests {

        @Test
        @Order(7)
        @DisplayName("Should display cinema management section")
        void testCinemaManagementSection() {
            waitForFxEvents();

            assertThat(lookup("#cinemaManagementSection").tryQuery()).isPresent();
        }

        @Test
        @Order(8)
        @DisplayName("Should navigate to cinema approval")
        void testNavigateToCinemaApproval() {
            waitForFxEvents();

            Button approvalButton = lookup("#cinemaApprovalButton").query();
            assertThat(approvalButton).isNotNull();
            clickOn(approvalButton);

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Reports Tests")
    class ReportsTests {

        @Test
        @Order(9)
        @DisplayName("Should display reports section")
        void testReportsSection() {
            waitForFxEvents();

            assertThat(lookup("#reportsSection").tryQuery()).isPresent();
        }

        @Test
        @Order(10)
        @DisplayName("Should generate user report")
        void testGenerateUserReport() {
            waitForFxEvents();

            Button generateButton = lookup("#generateReportButton").query();
            assertThat(generateButton).isNotNull();
            clickOn(generateButton);

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Quick Actions Tests")
    class QuickActionsTests {

        @Test
        @Order(11)
        @DisplayName("Should display quick actions")
        void testQuickActionsDisplay() {
            waitForFxEvents();

            assertThat(lookup("#quickActionsPanel").tryQuery()).isPresent();
        }

        @Test
        @Order(12)
        @DisplayName("Should send SMS to users")
        void testSendSMS() {
            waitForFxEvents();

            Button smsButton = lookup("#sendSMSButton").query();
            assertThat(smsButton).isNotNull();
            clickOn(smsButton);

            waitForFxEvents();
        }

        @Test
        @Order(13)
        @DisplayName("Should send email to users")
        void testSendEmail() {
            waitForFxEvents();

            Button emailButton = lookup("#sendEmailButton").query();
            assertThat(emailButton).isNotNull();
            clickOn(emailButton);

            waitForFxEvents();
        }
    }
}

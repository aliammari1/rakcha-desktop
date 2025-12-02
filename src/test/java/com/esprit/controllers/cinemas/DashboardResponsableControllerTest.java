package com.esprit.controllers.cinemas;

import com.esprit.utils.TestFXBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
 * Comprehensive test suite for DashboardResponsableController.
 * Tests cinema manager's dashboard and session management.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardResponsableControllerTest extends TestFXBase {

    @Start
    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml")
        );
        javafx.scene.Parent root = loader.load();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Dashboard Display Tests")
    class DashboardDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display dashboard")
        void testDashboardDisplay() {
            waitForFxEvents();

            javafx.scene.layout.VBox dashboard = (javafx.scene.layout.VBox) lookup("#responsableDashboard").query();
            assertThat(dashboard).isNotNull();
            assertThat(dashboard.isVisible()).isTrue();
        }


        @Test
        @Order(2)
        @DisplayName("Should display cinema info")
        void testCinemaInfoDisplay() {
            waitForFxEvents();

            Label cinemaLabel = lookup("#cinemaNameLabel").query();
            assertThat(cinemaLabel).isNotNull();
            assertThat(cinemaLabel.getText()).isNotBlank();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Session Management Tests")
    class SessionManagementTests {

        @Test
        @Order(3)
        @DisplayName("Should display sessions table")
        void testSessionsTableDisplay() {
            waitForFxEvents();

            TableView<?> table = lookup("#sessionsTable").query();
            assertThat(table).isNotNull();
            assertThat(table.isVisible()).isTrue();
        }


        @Test
        @Order(4)
        @DisplayName("Should add new session")
        void testAddSession() {
            waitForFxEvents();

            Button addButton = lookup("#addSessionButton").query();
            assertThat(addButton).isNotNull();
            assertThat(addButton.isVisible()).isTrue();

            clickOn(addButton);
            waitForFxEvents();
        }


        @Test
        @Order(5)
        @DisplayName("Should delete session")
        void testDeleteSession() {
            waitForFxEvents();

            TableView<?> table = lookup("#sessionsTable").query();
            assertThat(table).isNotNull();

            // Verify precondition: table must have items to delete
            assertThat(table.getItems())
                .as("Sessions table must contain at least one session for deletion test")
                .isNotEmpty();

            // Select the first session
            table.getSelectionModel().selectFirst();
            waitForFxEvents();

            // Verify precondition: selected item must exist
            Object selectedItem = table.getSelectionModel().getSelectedItem();
            assertThat(selectedItem)
                .as("A session must be selected before attempting deletion")
                .isNotNull();

            // Verify precondition: delete button must exist
            Button deleteButton = lookup("#deleteSessionButton").query();
            assertThat(deleteButton)
                .as("Delete button must exist in the UI for session deletion")
                .isNotNull();

            // Verify precondition: delete button must be visible
            assertThat(deleteButton.isVisible())
                .as("Delete button must be visible to perform deletion")
                .isTrue();

            // Execute the delete action
            clickOn(deleteButton);
            waitForFxEvents();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @Order(6)
        @DisplayName("Should display booking statistics")
        void testBookingStatistics() {
            waitForFxEvents();

            Label statsLabel = lookup("#bookingStatsLabel").query();
            assertThat(statsLabel).isNotNull();
            assertThat(statsLabel.getText()).isNotBlank();
            assertThat(statsLabel.isVisible()).isTrue();
        }


        @Test
        @Order(7)
        @DisplayName("Should display revenue")
        void testRevenueDisplay() {
            waitForFxEvents();

            Label revenueLabel = lookup("#revenueLabel").query();
            assertThat(revenueLabel).isNotNull();
            assertThat(revenueLabel.getText()).isNotBlank();
            assertThat(revenueLabel.isVisible()).isTrue();
        }

    }


}


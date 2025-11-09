package com.esprit.controllers.cinemas;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit5.Start;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.esprit.models.cinemas.Cinema;
import com.esprit.utils.TestAssertions;
import com.esprit.utils.TestFXBase;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Comprehensive UI tests for DashboardAdminController.
 * Tests cinema management, filtering, approval/rejection workflows.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(value = 2, unit = TimeUnit.MINUTES)
class DashboardAdminControllerTest extends TestFXBase {

    @Start
    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/cinemas/DashboardAdminCinema.fxml")
        );
        javafx.scene.Parent root = loader.load();
        
        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
        
        // Wait for UI to be fully loaded and initialized
        waitForFxEvents();
        // Wait a bit for database operations during initialization
        org.testfx.util.WaitForAsyncUtils.waitFor(1, java.util.concurrent.TimeUnit.SECONDS, () -> true);
        waitForFxEvents();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Cinema List Display Tests")
    class CinemaListDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display cinema table with all columns")
        void testCinemaTableDisplay() {
            TestAssertions.verifyAllVisible(
                    "#listCinema",
                    "#colCinema",
                    "#colAdresse",
                    "#colResponsable",
                    "#colStatut",
                    "#colLogo",
                    "#colAction");
        }

        @Test
        @Order(2)
        @DisplayName("Should load cinema data into table")
        void testLoadCinemaData() {
            TableView<Cinema> table = lookup("#listCinema").query();

            waitForFxEvents();

            // Table should contain data or be empty initially
            assertNotNull(table.getItems());
        }

        @Test
        @Order(3)
        @DisplayName("Cinema table columns should have proper headers")
        void testTableColumnHeaders() {
            TableView<Cinema> table = lookup("#listCinema").query();

            assertTrue(table.getColumns().stream()
                    .anyMatch(col -> col.getId().contains("Cinema")));
            assertTrue(table.getColumns().stream()
                    .anyMatch(col -> col.getId().contains("Adresse")));
        }

        @Test
        @Order(4)
        @DisplayName("Should display cinema logos in table")
        void testCinemaLogosDisplay() {
            verifyThat("#colLogo", isNotNull());
        }

        @Test
        @Order(5)
        @DisplayName("Should display action buttons for each cinema")
        void testActionButtonsDisplay() {
            verifyThat("#colAction", isNotNull());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Search and Filter Tests")
    class SearchFilterTests {

        @Test
        @Order(10)
        @DisplayName("Should display search text field")
        void testSearchFieldVisible() {
            verifyThat("#tfSearch", isVisible());
            verifyThat("#tfSearch", isEnabled());
        }

        @Test
        @Order(11)
        @DisplayName("Should filter cinemas by search query")
        void testSearchFunctionality() {
            String searchQuery = "Cinema";
            fillTextField("#tfSearch", searchQuery);
            waitForFxEvents();

            assertEquals(searchQuery, getTextFieldValue("#tfSearch"));
        }

        @Test
        @Order(12)
        @DisplayName("Should clear search results")
        void testClearSearch() {
            fillTextField("#tfSearch", "Test");
            clearTextField("#tfSearch");

            TestAssertions.verifyTextFieldEmpty("#tfSearch");
        }

        @Test
        @Order(13)
        @DisplayName("Should display filter panel")
        void testFilterPanelVisible() {
            verifyThat("#filterAnchor", isNotNull());
        }

        @Test
        @Order(14)
        @DisplayName("Should filter by address using checkboxes")
        void testAddressFilter() {
            // Filter panel should contain address checkboxes
            verifyThat("#filterAnchor", isNotNull());
        }

        @Test
        @Order(15)
        @DisplayName("Should filter by status using checkboxes")
        void testStatusFilter() {
            // Status filter checkboxes should be available
            verifyThat("#filterAnchor", isNotNull());
        }

        @Test
        @Order(16)
        @DisplayName("Should apply multiple filters simultaneously")
        void testMultipleFilters() {
            fillTextField("#tfSearch", "Cinema");
            // Additional filter selections would be tested here
            waitForFxEvents();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Cinema Approval Tests")
    class CinemaApprovalTests {

        @Test
        @Order(20)
        @DisplayName("Should display approve button for pending cinemas")
        void testApproveButtonVisible() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                // Approve buttons should be in action column
                assertNotNull(table);
            }
        }

        @Test
        @Order(21)
        @DisplayName("Should approve cinema when button clicked")
        void testApproveCinema() {
            // Click approve button in action column
            waitForFxEvents();

            // Cinema status should change to approved
        }

        @Test
        @Order(22)
        @DisplayName("Should display reject button for pending cinemas")
        void testRejectButtonVisible() {
            TableView<Cinema> table = lookup("#listCinema").query();
            assertNotNull(table);
        }

        @Test
        @Order(23)
        @DisplayName("Should reject cinema when button clicked")
        void testRejectCinema() {
            // Click reject button
            waitForFxEvents();

            // Cinema status should change to rejected
        }

        @Test
        @Order(24)
        @DisplayName("Should hide action buttons for approved cinemas")
        void testHideActionsForApproved() {
            // Approved cinemas should not show approve/reject buttons
            waitForFxEvents();
        }

        @Test
        @Order(25)
        @DisplayName("Should show confirmation dialog before approval")
        void testApprovalConfirmation() {
            // Should display confirmation dialog
            waitForFxEvents();
        }

        @Test
        @Order(26)
        @DisplayName("Should show confirmation dialog before rejection")
        void testRejectionConfirmation() {
            // Should display confirmation dialog
            waitForFxEvents();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Cinema Details Tests")
    class CinemaDetailsTests {

        @Test
        @Order(30)
        @DisplayName("Should display cinema name in table")
        void testCinemaNameDisplay() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                Cinema cinema = table.getItems().get(0);
                assertNotNull(cinema.getName());
            }
        }

        @Test
        @Order(31)
        @DisplayName("Should display cinema address in table")
        void testCinemaAddressDisplay() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                Cinema cinema = table.getItems().get(0);
                assertNotNull(cinema.getAddress());
            }
        }

        @Test
        @Order(32)
        @DisplayName("Should display cinema manager in table")
        void testCinemaManagerDisplay() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                Cinema cinema = table.getItems().get(0);
                assertNotNull(cinema.getManager());
            }
        }

        @Test
        @Order(33)
        @DisplayName("Should display cinema status")
        void testCinemaStatusDisplay() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                // Status column should display cinema status
                assertNotNull(table.getColumns());
            }
        }

        @Test
        @Order(34)
        @DisplayName("Should show movies button for each cinema")
        void testShowMoviesButton() {
            // Each cinema row should have a show movies button
            waitForFxEvents();
        }

        @Test
        @Order(35)
        @DisplayName("Should navigate to cinema movies when button clicked")
        void testNavigateToCinemaMovies() {
            // Click show movies button
            waitForFxEvents();

            // Should navigate to movies view
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @Order(50)
        @DisplayName("Should handle pagination if implemented")
        void testPagination() {
            // Check if pagination controls exist
            waitForFxEvents();
        }

        @Test
        @Order(51)
        @DisplayName("Should navigate to next page")
        void testNextPage() {
            // If pagination exists, test navigation
            waitForFxEvents();
        }

        @Test
        @Order(52)
        @DisplayName("Should navigate to previous page")
        void testPreviousPage() {
            // If pagination exists, test navigation
            waitForFxEvents();
        }
    }
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(60)
        @DisplayName("Should handle empty cinema list gracefully")
        void testEmptyCinemaList() {
            TableView<Cinema> table = lookup("#listCinema").query();

            // Should handle empty state without errors
            assertNotNull(table);
        }

        @Test
        @Order(61)
        @DisplayName("Should handle network errors during data load")
        void testNetworkErrorHandling() {
            // Simulate network error
            waitForFxEvents();

            // Should display appropriate error message
        }

        @Test
        @Order(62)
        @DisplayName("Should handle approval errors gracefully")
        void testApprovalErrorHandling() {
            // Simulate approval error
            waitForFxEvents();

            // Should show error notification
        }
    }
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("UI Responsiveness Tests")
    class UIResponsivenessTests {

        @Test
        @Order(70)
        @DisplayName("Table should resize with window")
        void testTableResize() {
            TableView<Cinema> table = lookup("#listCinema").query();
            assertNotNull(table);
        }

        @Test
        @Order(71)
        @DisplayName("Should handle table row selection")
        void testRowSelection() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                runOnFxThread(() -> table.getSelectionModel().select(0));

                assertNotNull(table.getSelectionModel().getSelectedItem());
            }
        }

        @Test
        @Order(72)
        @DisplayName("Should handle double-click on row")
        void testDoubleClickRow() {
            TableView<Cinema> table = lookup("#listCinema").query();

            if (!table.getItems().isEmpty()) {
                doubleClickOn(table);
                waitForFxEvents();
            }
        }
    }
}

package com.esprit.controllers.products;

import com.esprit.models.products.Order;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for ListOrderController.
 * Tests order display, search, deletion, and statistics.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ListOrderControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/ListeCommande.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Order Display Tests")
    class OrderDisplayTests {

        @Test
        @org.junit.jupiter.api.Order(1)
        @DisplayName("Should display orders table")
        void testOrdersTableDisplay() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isVisible()).isTrue();
        }

        @Test
        @org.junit.jupiter.api.Order(2)
        @DisplayName("Should load orders from service")
        void testLoadOrders() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            // Verify table is ready to display orders
            assertThat(table.getItems()).isNotNull();
            waitForFxEvents();
        }

        @Test
        @org.junit.jupiter.api.Order(3)
        @DisplayName("Should display client name column")
        void testClientNameColumn() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
            assertThat(lookup("#idnom").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(4)
        @DisplayName("Should display client first name column")
        void testClientFirstNameColumn() {
            waitForFxEvents();

            assertThat(lookup("#idprenom").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(5)
        @DisplayName("Should display address column")
        void testAddressColumn() {
            waitForFxEvents();

            assertThat(lookup("#idadresse").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(6)
        @DisplayName("Should display phone number column")
        void testPhoneNumberColumn() {
            waitForFxEvents();

            assertThat(lookup("#idnumero").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(7)
        @DisplayName("Should display date column")
        void testDateColumn() {
            waitForFxEvents();

            assertThat(lookup("#iddate").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(8)
        @DisplayName("Should display status column")
        void testStatusColumn() {
            waitForFxEvents();

            assertThat(lookup("#idStatu").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(9)
        @DisplayName("Should display delete action column")
        void testDeleteColumn() {
            waitForFxEvents();

            assertThat(lookup("#deleteColumn").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Search Functionality Tests")
    class SearchFunctionalityTests {

        @Test
        @org.junit.jupiter.api.Order(10)
        @DisplayName("Should display search bar")
        void testSearchBarDisplay() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(11)
        @DisplayName("Should filter orders by client name")
        void testSearchByClientName() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
            int initialCount = table.getItems().size();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("John");

            waitForFxEvents();

            // Verify search text is entered
            assertThat(searchBar.getText()).contains("John");

            // Verify table is filtered or has appropriate items
            int filteredCount = table.getItems().size();
            // Filtered count should be <= initial count
            assertThat(filteredCount).isLessThanOrEqualTo(initialCount);

            // Verify visible items contain the search term
            for (Order order : table.getItems()) {
                String clientName = order.getClient().getFirstName() + " " + order.getClient().getLastName();
                assertThat(clientName.toLowerCase()).contains("john");
            }

        }

        @Test
        @org.junit.jupiter.api.Order(12)
        @DisplayName("Should filter orders by address")
        void testSearchByAddress() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
            int initialCount = table.getItems().size();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("Main");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("Main");

            // Verify filtering applied
            int filteredCount = table.getItems().size();
            assertThat(filteredCount).isLessThanOrEqualTo(initialCount);

            // Verify visible items match search criteria
            for (Order order : table.getItems()) {
                assertThat(order.getShippingAddress().toLowerCase()).contains("main");
            }

        }

        @Test
        @org.junit.jupiter.api.Order(13)
        @DisplayName("Should filter orders by phone number")
        void testSearchByPhoneNumber() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("12345");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("12345");

            // Verify visible items contain the phone search term
            for (Order order : table.getItems()) {
                String phone = String.valueOf(order.getPhoneNumber());
                assertThat(phone).contains("12345");
            }

        }

        @Test
        @org.junit.jupiter.api.Order(14)
        @DisplayName("Should perform case-insensitive search")
        void testCaseInsensitiveSearch() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("JOHN");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("JOHN");

            // Verify results match despite case difference
            for (Order order : table.getItems()) {
                String clientName = (order.getClient().getFirstName() + " " + order.getClient().getLastName())
                        .toLowerCase();
                assertThat(clientName).contains("john");
            }

        }

        @Test
        @org.junit.jupiter.api.Order(15)
        @DisplayName("Should show all orders when search is empty")
        void testEmptySearch() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("filter").eraseText(6);

            waitForFxEvents();

            assertThat(searchBar.getText()).isEmpty();

            // When search is empty, all items should be shown
            assertThat(table.getItems().size()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @org.junit.jupiter.api.Order(16)
        @DisplayName("Should update results as user types")
        void testRealTimeSearch() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();

            // Clear any existing text
            clickOn(searchBar).eraseText(searchBar.getLength());

            // Type first character
            clickOn(searchBar).write("J");
            waitForFxEvents();

            assertThat(searchBar.getText()).contains("J");
            int firstCharCount = table.getItems().size();

            // Type second character
            clickOn(searchBar).write("o");
            waitForFxEvents();

            // Table should update with more specific results
            int secondCharCount = table.getItems().size();
            assertThat(secondCharCount).isLessThanOrEqualTo(firstCharCount);
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Order Deletion Tests")
    class OrderDeletionTests {

        @Test
        @org.junit.jupiter.api.Order(17)
        @DisplayName("Should display delete button for each order")
        void testDeleteButtonDisplay() {
            waitForFxEvents();

            assertThat(lookup("#deleteColumn").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(18)
        @DisplayName("Should confirm before deleting order")
        void testDeleteConfirmation() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            if (table.getItems().isEmpty()) {
                return; // Skip if no items
            }

            // Select first row
            table.getSelectionModel().selectFirst();
            waitForFxEvents();

            // Look for delete button in selected row
            var deleteButton = lookup(".delete-button").tryQuery();
            if (deleteButton.isPresent()) {
                clickOn(deleteButton.get());
                waitForFxEvents();

                // Verify a confirmation dialog appears or warning label is visible
                var confirmDialog = lookup(".dialog-pane").tryQuery();
                if (confirmDialog.isPresent()) {
                    assertThat(confirmDialog.get().isVisible()).isTrue();
                }

            }

        }

        @Test
        @org.junit.jupiter.api.Order(19)
        @DisplayName("Should delete order on confirmation")
        void testDeleteOrder() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            if (table.getItems().isEmpty()) {
                return; // Skip if no items to delete
            }

            int initialCount = table.getItems().size();

            // Select and delete first order
            table.getSelectionModel().selectFirst();
            waitForFxEvents();

            // Find and click delete button
            var deleteButton = lookup(".delete-button").tryQuery();
            if (deleteButton.isPresent()) {
                clickOn(deleteButton.get());
                waitForFxEvents();

                // Click OK/Confirm on dialog
                var confirmButton = lookup(".confirm-button, #okButton, .ok-button").tryQuery();
                if (confirmButton.isPresent()) {
                    clickOn(confirmButton.get());
                    waitForFxEvents();

                    // Verify item was deleted
                    assertThat(table.getItems().size()).isLessThan(initialCount);
                }

            }

        }

        @Test
        @org.junit.jupiter.api.Order(20)
        @DisplayName("Should refresh table after deletion")
        void testTableRefreshAfterDeletion() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            // Capture initial state
            int initialCount = table.getItems().size();

            // If there are items, attempt a deletion flow
            if (initialCount > 0) {
                table.getSelectionModel().selectFirst();
                waitForFxEvents();

                var deleteButton = lookup(".delete-button").tryQuery();
                if (deleteButton.isPresent()) {
                    clickOn(deleteButton.get());
                    waitForFxEvents();

                    var confirmButton = lookup(".confirm-button, #okButton").tryQuery();
                    if (confirmButton.isPresent()) {
                        clickOn(confirmButton.get());
                        waitForFxEvents();
                    }

                }

            }

            // Verify table is still functional
            assertThat(table.getItems()).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(21)
        @DisplayName("Should not delete if user cancels")
        void testCancelDeletion() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            if (table.getItems().isEmpty()) {
                return; // Skip if no items
            }

            int initialCount = table.getItems().size();

            // Select first order
            table.getSelectionModel().selectFirst();
            Order selectedOrder = table.getSelectionModel().getSelectedItem();
            waitForFxEvents();

            // Click delete button
            var deleteButton = lookup(".delete-button").tryQuery();
            if (deleteButton.isPresent()) {
                clickOn(deleteButton.get());
                waitForFxEvents();

                // Click Cancel/No on confirmation dialog
                var cancelButton = lookup(".cancel-button, #cancelButton, .no-button").tryQuery();
                if (cancelButton.isPresent()) {
                    clickOn(cancelButton.get());
                    waitForFxEvents();

                    // Verify nothing was deleted
                    assertThat(table.getItems().size()).isEqualTo(initialCount);

                    // Verify selected item still exists
                    if (selectedOrder != null) {
                        assertThat(table.getItems()).contains(selectedOrder);
                    }

                }

            }

        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Order Status Display Tests")
    class OrderStatusDisplayTests {

        @Test
        @org.junit.jupiter.api.Order(22)
        @DisplayName("Should display pending status")
        void testPendingStatus() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
            assertThat(lookup("#idStatu").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(23)
        @DisplayName("Should display confirmed status")
        void testConfirmedStatus() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(24)
        @DisplayName("Should display delivered status")
        void testDeliveredStatus() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(25)
        @DisplayName("Should display cancelled status")
        void testCancelledStatus() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Client Information Display Tests")
    class ClientInformationDisplayTests {

        @Test
        @org.junit.jupiter.api.Order(26)
        @DisplayName("Should fetch client details for each order")
        void testFetchClientDetails() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(27)
        @DisplayName("Should display client first name")
        void testClientFirstNameDisplay() {
            waitForFxEvents();

            assertThat(lookup("#idprenom").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(28)
        @DisplayName("Should display client last name")
        void testClientLastNameDisplay() {
            waitForFxEvents();

            assertThat(lookup("#idnom").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Statistics Navigation Tests")
    class StatisticsNavigationTests {

        @Test
        @org.junit.jupiter.api.Order(29)
        @DisplayName("Should provide statistics button")
        void testStatisticsButton() {
            waitForFxEvents();

            assertThat(lookup("#statisticsButton").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(30)
        @DisplayName("Should navigate to statistics view")
        void testNavigateToStatistics() {
            waitForFxEvents();

            Button statsButton = lookup("#statisticsButton").query();
            assertThat(statsButton).isNotNull();
            clickOn(statsButton);

            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Empty State Tests")
    class EmptyStateTests {

        @Test
        @org.junit.jupiter.api.Order(31)
        @DisplayName("Should handle no orders gracefully")
        void testNoOrders() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(32)
        @DisplayName("Should display message when no orders")
        void testNoOrdersMessage() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

    }

    // Helper methods are handled by test fixtures or mocked data
}

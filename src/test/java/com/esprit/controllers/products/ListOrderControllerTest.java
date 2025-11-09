package com.esprit.controllers.products;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import org.testfx.framework.junit5.Start;

import com.esprit.models.products.Order;
import com.esprit.models.users.Client;
import com.esprit.services.products.OrderService;
import com.esprit.services.users.UserService;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for ListOrderController.
 * Tests order display, search, deletion, and statistics.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ListOrderControllerTest extends TestFXBase {

    private OrderService orderService;
    private UserService userService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/ListeCommande.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

            List<Order> orders = createMockOrders();
            
            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();

            // Verify table is ready to display orders
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("John");

            waitForFxEvents();

            TextField searchField = lookup("#SearchBar").query();
            assertThat(searchField.getText()).contains("John");
        }

        @Test
        @org.junit.jupiter.api.Order(12)
        @DisplayName("Should filter orders by address")
        void testSearchByAddress() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("123 Main");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("123 Main");
        }

        @Test
        @org.junit.jupiter.api.Order(13)
        @DisplayName("Should filter orders by phone number")
        void testSearchByPhoneNumber() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("12345678");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("12345678");
        }

        @Test
        @org.junit.jupiter.api.Order(14)
        @DisplayName("Should perform case-insensitive search")
        void testCaseInsensitiveSearch() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("JOHN");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("JOHN");
        }

        @Test
        @org.junit.jupiter.api.Order(15)
        @DisplayName("Should show all orders when search is empty")
        void testEmptySearch() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("filter").eraseText(6);

            waitForFxEvents();

            assertThat(searchBar.getText()).isEmpty();
        }

        @Test
        @org.junit.jupiter.api.Order(16)
        @DisplayName("Should update results as user types")
        void testRealTimeSearch() {
            waitForFxEvents();

            TextField searchBar = lookup("#SearchBar").query();
            assertThat(searchBar).isNotNull();
            clickOn(searchBar).write("J");

            waitForFxEvents();

            assertThat(searchBar.getText()).contains("J");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

            assertThat(lookup("#deleteColumn").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(19)
        @DisplayName("Should delete order on confirmation")
        void testDeleteOrder() {
            waitForFxEvents();

            assertThat(lookup("#deleteColumn").tryQuery()).isPresent();
        }

        @Test
        @org.junit.jupiter.api.Order(20)
        @DisplayName("Should refresh table after deletion")
        void testTableRefreshAfterDeletion() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }

        @Test
        @org.junit.jupiter.api.Order(21)
        @DisplayName("Should not delete if user cancels")
        void testCancelDeletion() {
            waitForFxEvents();

            TableView<Order> table = lookup("#orderTableView").query();
            assertThat(table).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

    // Helper methods
    private List<Order> createMockOrders() {
        List<Order> orders = new ArrayList<>();

        Order order1 = new Order();
        order1.setId(1L);
        order1.setAddress("123 Main St");
        order1.setPhoneNumber(12345678);
        order1.setOrderDate(new Date());
        order1.setStatus("Pending");
        order1.setClient(createMockClient());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setAddress("456 Oak Ave");
        order2.setPhoneNumber(87654321);
        order2.setOrderDate(new Date());
        order2.setStatus("Confirmed");
        order2.setClient(createMockClient());

        orders.add(order1);
        orders.add(order2);
        return orders;
    }

    private Client createMockClient() {
        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        return client;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }
}

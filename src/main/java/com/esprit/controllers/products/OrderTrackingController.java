package com.esprit.controllers.products;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.users.User;
import com.esprit.services.products.OrderService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for order tracking and history.
 */
public class OrderTrackingController {

    private static final Logger LOGGER = Logger.getLogger(OrderTrackingController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private final OrderService orderService;
    @FXML
    private VBox trackingContainer;
    @FXML
    private VBox ordersList;
    @FXML
    private VBox orderDetailsBox;
    @FXML
    private Label orderIdLabel;
    @FXML
    private Label orderDateLabel;
    @FXML
    private Label orderStatusLabel;
    @FXML
    private Label orderTotalLabel;
    @FXML
    private Label shippingAddressLabel;
    @FXML
    private Label paymentMethodLabel;
    @FXML
    private VBox orderItemsList;
    @FXML
    private VBox trackingTimeline;
    @FXML
    private Label estimatedDeliveryLabel;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private TextField searchField;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox noOrdersBox;
    private ObservableList<Order> orders;
    private User currentUser;
    private Order selectedOrder;
    private Integer preSelectedOrderId;

    public OrderTrackingController() {
        this.orderService = new OrderService();
        this.orders = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing OrderTrackingController");

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupFilters();
        loadOrders();
    }

    /**
     * Pre-select an order to display (from checkout confirmation).
     */
    public void setOrderId(int orderId) {
        this.preSelectedOrderId = orderId;
    }

    private void setupFilters() {
        filterCombo.getItems().addAll("All Orders", "Pending", "Processing", "Shipped", "Delivered", "Cancelled");
        filterCombo.setValue("All Orders");
        filterCombo.setOnAction(e -> filterOrders());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterOrders());
        }
    }

    private void loadOrders() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Order> orderList = orderService.getOrdersByUser(currentUser.getId());

                Platform.runLater(() -> {
                    orders.setAll(orderList);
                    displayOrders();
                    showLoading(false);

                    // Select pre-selected order if set
                    if (preSelectedOrderId != null) {
                        orders.stream()
                            .filter(o -> o.getId() != null && o.getId().equals(preSelectedOrderId))
                            .findFirst()
                            .ifPresent(this::selectOrder);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading orders", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load orders.");
                });
            }
        }).start();
    }

    private void displayOrders() {
        ordersList.getChildren().clear();

        if (orders.isEmpty()) {
            noOrdersBox.setVisible(true);
            orderDetailsBox.setVisible(false);
        } else {
            noOrdersBox.setVisible(false);

            for (Order order : orders) {
                ordersList.getChildren().add(createOrderCard(order));
            }

            // Select first order if none selected
            if (selectedOrder == null && !orders.isEmpty()) {
                selectOrder(orders.get(0));
            }
        }
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox(8);
        card.getStyleClass().add("order-card");

        if (selectedOrder != null && selectedOrder.getId() == order.getId()) {
            card.getStyleClass().add("selected");
        }

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("order-card-header");

        Label orderNumLabel = new Label("Order #" + order.getId());
        orderNumLabel.getStyleClass().add("order-number");
        HBox.setHgrow(orderNumLabel, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatus());
        statusLabel.getStyleClass().addAll("order-status-badge",
            "status-" + order.getStatus().toLowerCase().replace(" ", "-"));

        header.getChildren().addAll(orderNumLabel, statusLabel);

        // Date
        Label dateLabel = new Label(order.getCreatedAt() != null ?
            order.getCreatedAt().format(DATE_FORMAT) : "N/A");
        dateLabel.getStyleClass().add("order-date");

        // Summary
        HBox summary = new HBox();
        summary.getStyleClass().add("order-summary");

        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        Label itemsLabel = new Label(itemCount + " item" + (itemCount != 1 ? "s" : ""));
        itemsLabel.getStyleClass().add("order-items-count");
        HBox.setHgrow(itemsLabel, Priority.ALWAYS);

        Label totalLabel = new Label(String.format("$%.2f", order.getTotal()));
        totalLabel.getStyleClass().add("order-total");

        summary.getChildren().addAll(itemsLabel, totalLabel);

        card.getChildren().addAll(header, dateLabel, summary);

        card.setOnMouseClicked(e -> selectOrder(order));

        return card;
    }

    private void selectOrder(Order order) {
        selectedOrder = order;
        displayOrders(); // Refresh to show selection
        displayOrderDetails(order);
    }

    private void displayOrderDetails(Order order) {
        orderDetailsBox.setVisible(true);

        orderIdLabel.setText("#" + order.getId());
        orderDateLabel.setText(order.getCreatedAt() != null ?
            order.getCreatedAt().format(DATE_FORMAT) : "N/A");
        orderStatusLabel.setText(order.getStatus());
        orderStatusLabel.getStyleClass().setAll("order-status-detail",
            "status-" + order.getStatus().toLowerCase().replace(" ", "-"));
        orderTotalLabel.setText(String.format("$%.2f", order.getTotal()));

        if (shippingAddressLabel != null) {
            shippingAddressLabel.setText(order.getShippingAddress());
        }
        if (paymentMethodLabel != null) {
            paymentMethodLabel.setText(order.getPaymentMethod());
        }

        // Display order items
        displayOrderItems(order);

        // Display tracking timeline
        displayTrackingTimeline(order);

        // Estimated delivery
        if (estimatedDeliveryLabel != null) {
            LocalDateTime estimated = order.getCreatedAt() != null ?
                order.getCreatedAt().plusDays(5) : LocalDateTime.now().plusDays(5);
            estimatedDeliveryLabel.setText(estimated.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }
    }

    private void displayOrderItems(Order order) {
        orderItemsList.getChildren().clear();

        if (order.getItems() == null || order.getItems().isEmpty()) {
            Label noItems = new Label("No items");
            orderItemsList.getChildren().add(noItems);
            return;
        }

        for (OrderItem item : order.getItems()) {
            HBox itemRow = new HBox(12);
            itemRow.getStyleClass().add("order-item-row");

            Label nameLabel = new Label(item.getProductName());
            nameLabel.getStyleClass().add("item-name");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label qtyLabel = new Label("×" + item.getQuantity());
            qtyLabel.getStyleClass().add("item-quantity");

            Label priceLabel = new Label(String.format("$%.2f", item.getSubtotal()));
            priceLabel.getStyleClass().add("item-price");

            itemRow.getChildren().addAll(nameLabel, qtyLabel, priceLabel);
            orderItemsList.getChildren().add(itemRow);
        }
    }

    private void displayTrackingTimeline(Order order) {
        trackingTimeline.getChildren().clear();

        String status = order.getStatus() != null ? order.getStatus() : "Pending";
        LocalDateTime orderDate = order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now();

        // Define tracking steps
        String[][] steps = {
            {"Order Placed", "Your order has been received", orderDate.format(DATE_FORMAT)},
            {"Processing", "Your order is being processed", ""},
            {"Shipped", "Your order is on its way", ""},
            {"Out for Delivery", "Your order is out for delivery", ""},
            {"Delivered", "Your order has been delivered", ""}
        };

        int currentStep = getStepIndex(status);

        for (int i = 0; i < steps.length; i++) {
            VBox stepBox = new VBox(4);
            stepBox.getStyleClass().add("timeline-step");

            HBox stepHeader = new HBox(8);

            // Status indicator
            Label indicator = new Label("●");
            if (i < currentStep) {
                indicator.getStyleClass().add("step-completed");
            } else if (i == currentStep) {
                indicator.getStyleClass().add("step-current");
            } else {
                indicator.getStyleClass().add("step-pending");
            }

            Label stepTitle = new Label(steps[i][0]);
            stepTitle.getStyleClass().add("step-title");
            if (i <= currentStep) {
                stepTitle.getStyleClass().add("step-active");
            }

            stepHeader.getChildren().addAll(indicator, stepTitle);

            Label stepDesc = new Label(steps[i][1]);
            stepDesc.getStyleClass().add("step-description");

            stepBox.getChildren().addAll(stepHeader, stepDesc);

            if (!steps[i][2].isEmpty()) {
                Label stepTime = new Label(steps[i][2]);
                stepTime.getStyleClass().add("step-time");
                stepBox.getChildren().add(stepTime);
            }

            trackingTimeline.getChildren().add(stepBox);

            // Add connector line (except for last step)
            if (i < steps.length - 1) {
                Label connector = new Label("|");
                connector.getStyleClass().add("timeline-connector");
                if (i < currentStep) {
                    connector.getStyleClass().add("connector-active");
                }
                trackingTimeline.getChildren().add(connector);
            }
        }
    }

    private int getStepIndex(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return 0;
            case "processing":
                return 1;
            case "shipped":
                return 2;
            case "out for delivery":
                return 3;
            case "delivered":
                return 4;
            case "cancelled":
                return -1;
            default:
                return 0;
        }
    }

    private void filterOrders() {
        String filter = filterCombo.getValue();
        String search = searchField != null ? searchField.getText().toLowerCase() : "";

        new Thread(() -> {
            try {
                List<Order> allOrders = orderService.getOrdersByUser(currentUser.getId());

                List<Order> filtered = allOrders.stream()
                    .filter(order -> {
                        // Status filter
                        if (!"All Orders".equals(filter)) {
                            if (!filter.equalsIgnoreCase(order.getStatus())) {
                                return false;
                            }
                        }

                        // Search filter
                        if (!search.isEmpty()) {
                            String orderId = String.valueOf(order.getId());
                            if (!orderId.contains(search)) {
                                return false;
                            }
                        }

                        return true;
                    })
                    .toList();

                Platform.runLater(() -> {
                    orders.setAll(filtered);
                    selectedOrder = null;
                    displayOrders();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error filtering orders", e);
            }
        }).start();
    }

    @FXML
    private void handleReorder() {
        if (selectedOrder == null) {
            showError("Please select an order first.");
            return;
        }

        showInfo("Reorder functionality coming soon!");
    }

    @FXML
    private void handleCancelOrder() {
        if (selectedOrder == null) {
            showError("Please select an order first.");
            return;
        }

        if ("Delivered".equalsIgnoreCase(selectedOrder.getStatus()) ||
            "Cancelled".equalsIgnoreCase(selectedOrder.getStatus())) {
            showError("This order cannot be cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Order");
        confirm.setHeaderText("Cancel Order #" + selectedOrder.getId() + "?");
        confirm.setContentText("This action cannot be undone. You will receive a refund if applicable.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancelOrder();
            }
        });
    }

    private void cancelOrder() {
        showLoading(true);

        new Thread(() -> {
            try {
                orderService.cancelOrder(selectedOrder.getId());

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Order cancelled successfully.");
                    loadOrders();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error cancelling order", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to cancel order.");
                });
            }
        }).start();
    }

    @FXML
    private void handleContactSupport() {
        showInfo("Contact support: support@rakcha.com");
    }

    @FXML
    private void handleDownloadInvoice() {
        if (selectedOrder == null) {
            showError("Please select an order first.");
            return;
        }

        showInfo("Invoice download coming soon!");
    }

    @FXML
    private void handleTrackShipment() {
        if (selectedOrder == null) {
            showError("Please select an order first.");
            return;
        }

        if (!"Shipped".equalsIgnoreCase(selectedOrder.getStatus()) &&
            !"Out for Delivery".equalsIgnoreCase(selectedOrder.getStatus())) {
            showInfo("Tracking available once order is shipped.");
            return;
        }

        showInfo("Tracking number: TRK" + selectedOrder.getId() + "2025");
    }

    @FXML
    private void handleStartShopping() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) trackingContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to products", e);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) trackingContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

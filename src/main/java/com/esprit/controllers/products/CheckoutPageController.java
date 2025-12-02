package com.esprit.controllers.products;

import com.esprit.models.users.User;
import com.esprit.models.products.Product;
import com.esprit.models.products.ShoppingCart;
import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Payment;
import com.esprit.services.products.CartService;
import com.esprit.services.products.OrderService;
import com.esprit.services.products.PaymentService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the checkout page with cart management and payment processing.
 */
public class CheckoutPageController {
    
    private static final Logger LOGGER = Logger.getLogger(CheckoutPageController.class.getName());
    
    @FXML private VBox checkoutContainer;
    @FXML private VBox OrderItemsList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label shippingLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private TextField promoCodeField;
    @FXML private Label promoStatusLabel;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField zipCodeField;
    @FXML private ComboBox<String> countryCombo;
    @FXML private RadioButton creditCardRadio;
    @FXML private RadioButton paypalRadio;
    @FXML private RadioButton cashRadio;
    @FXML private VBox creditCardForm;
    @FXML private TextField cardNumberField;
    @FXML private TextField cardHolderField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private CheckBox saveCardCheck;
    @FXML private CheckBox termsCheck;
    @FXML private Button placeOrderButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label emptyCartLabel;
    
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private ObservableList<OrderItem> OrderItems;
    private User currentUser;
    private double subtotal = 0;
    private double discount = 0;
    private double tax = 0;
    private double shipping = 0;
    
    public CheckoutPageController() {
        this.cartService = new CartService();
        this.orderService = new OrderService();
        this.paymentService = new PaymentService();
        this.OrderItems = FXCollections.observableArrayList();
    }
    
    @FXML
    public void initialize() {
        LOGGER.info("Initializing CheckoutPageController");
        
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }
        
        setupPaymentMethods();
        setupCountries();
        prefillUserInfo();
        loadCart();
    }
    
    private void setupPaymentMethods() {
        ToggleGroup paymentGroup = new ToggleGroup();
        creditCardRadio.setToggleGroup(paymentGroup);
        paypalRadio.setToggleGroup(paymentGroup);
        cashRadio.setToggleGroup(paymentGroup);
        creditCardRadio.setSelected(true);
        
        paymentGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            creditCardForm.setVisible(newToggle == creditCardRadio);
        });
    }
    
    private void setupCountries() {
        countryCombo.getItems().addAll(
            "Tunisia", "France", "Germany", "United States", "United Kingdom", 
            "Canada", "Spain", "Italy", "Morocco", "Algeria"
        );
        countryCombo.setValue("Tunisia");
    }
    
    private void prefillUserInfo() {
        if (currentUser != null) {
            if (fullNameField != null) {
                fullNameField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            }
            if (emailField != null) {
                emailField.setText(currentUser.getEmail());
            }
            if (phoneField != null) {
                phoneField.setText(currentUser.getPhoneNumber());
            }
        }
    }
    
    private void loadCart() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<ShoppingCart> cartItems = cartService.getShoppingCartByUserId(currentUser.getId());
                List<OrderItem> items = new ArrayList<>();
                
                // Convert ShoppingCart items to OrderItem objects
                for (ShoppingCart cart : cartItems) {
                    if (cart.getProduct() != null) {
                        OrderItem orderItem = new OrderItem(null, cart.getProduct(), cart.getQuantity(), 
                            cart.getProduct().getPrix() != null ? cart.getProduct().getPrix().doubleValue() : 0.0);
                        orderItem.setId(cart.getId());
                        items.add(orderItem);
                    }
                }
                
                Platform.runLater(() -> {
                    OrderItems.setAll(items);
                    displayOrderItems();
                    calculateTotals();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading cart", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load cart.");
                });
            }
        }).start();
    }
    
    private void displayOrderItems() {
        OrderItemsList.getChildren().clear();
        
        if (OrderItems.isEmpty()) {
            emptyCartLabel.setVisible(true);
            placeOrderButton.setDisable(true);
        } else {
            emptyCartLabel.setVisible(false);
            placeOrderButton.setDisable(false);
            
            for (OrderItem item : OrderItems) {
                OrderItemsList.getChildren().add(createOrderItemRow(item));
            }
        }
    }
    
    private HBox createOrderItemRow(OrderItem item) {
        HBox row = new HBox(12);
        row.getStyleClass().add("cart-item-row");
        
        Product product = item.getProduct();
        
        // Product image
        ImageView productImage = new ImageView();
        productImage.setFitWidth(60);
        productImage.setFitHeight(60);
        productImage.getStyleClass().add("cart-item-image");
        
        if (product != null && product.getImage() != null) {
            try {
                productImage.setImage(new Image(product.getImage(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load product image", e);
            }
        }
        
        // Product info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Label nameLabel = new Label(product != null ? product.getNom() : "Unknown Product");
        nameLabel.getStyleClass().add("cart-item-name");
        
        Label priceLabel = new Label(String.format("$%.2f", 
            product != null ? product.getPrix() : 0));
        priceLabel.getStyleClass().add("cart-item-price");
        
        info.getChildren().addAll(nameLabel, priceLabel);
        
        // Quantity controls
        HBox quantityBox = new HBox(4);
        quantityBox.getStyleClass().add("quantity-controls");
        
        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("quantity-btn");
        minusBtn.setOnAction(e -> updateQuantity(item, item.getQuantity() - 1));
        
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.getStyleClass().add("quantity-label");
        quantityLabel.setMinWidth(30);
        
        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("quantity-btn");
        plusBtn.setOnAction(e -> updateQuantity(item, item.getQuantity() + 1));
        
        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
        
        // Item total
        double itemTotal = (product != null ? product.getPrix() : 0) * item.getQuantity();
        Label totalLabel = new Label(String.format("$%.2f", itemTotal));
        totalLabel.getStyleClass().add("cart-item-total");
        totalLabel.setMinWidth(80);
        
        // Remove button
        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-item-btn");
        removeBtn.setOnAction(e -> removeItem(item));
        
        row.getChildren().addAll(productImage, info, quantityBox, totalLabel, removeBtn);
        
        return row;
    }
    
    private void updateQuantity(OrderItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(item);
            return;
        }
        
        item.setQuantity(newQuantity);
        
        new Thread(() -> {
            try {
                cartService.updateOrderItem(item);
                Platform.runLater(() -> {
                    displayOrderItems();
                    calculateTotals();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating quantity", e);
                Platform.runLater(() -> showError("Failed to update quantity."));
            }
        }).start();
    }
    
    private void removeItem(OrderItem item) {
        new Thread(() -> {
            try {
                cartService.removeFromCart(item.getId());
                
                Platform.runLater(() -> {
                    OrderItems.remove(item);
                    displayOrderItems();
                    calculateTotals();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error removing item", e);
                Platform.runLater(() -> showError("Failed to remove item."));
            }
        }).start();
    }
    
    private void calculateTotals() {
        subtotal = OrderItems.stream()
            .mapToDouble(item -> {
                double price = item.getProduct() != null ? item.getProduct().getPrix() : 0;
                return price * item.getQuantity();
            })
            .sum();
        
        tax = subtotal * 0.19; // 19% VAT
        shipping = subtotal > 50 ? 0 : 5.99; // Free shipping over $50
        
        double total = subtotal + tax + shipping - discount;
        
        if (subtotalLabel != null) subtotalLabel.setText(String.format("$%.2f", subtotal));
        if (taxLabel != null) taxLabel.setText(String.format("$%.2f", tax));
        if (shippingLabel != null) shippingLabel.setText(shipping == 0 ? "FREE" : String.format("$%.2f", shipping));
        if (discountLabel != null) discountLabel.setText(String.format("-$%.2f", discount));
        if (totalLabel != null) totalLabel.setText(String.format("$%.2f", total));
    }
    
    @FXML
    private void handleApplyPromo() {
        String code = promoCodeField.getText().trim().toUpperCase();
        
        if (code.isEmpty()) {
            promoStatusLabel.setText("Enter a promo code");
            promoStatusLabel.getStyleClass().setAll("promo-status", "error");
            return;
        }
        
        // Simulate promo code validation
        switch (code) {
            case "SAVE10":
                discount = subtotal * 0.10;
                promoStatusLabel.setText("10% discount applied!");
                promoStatusLabel.getStyleClass().setAll("promo-status", "success");
                break;
            case "SAVE20":
                discount = subtotal * 0.20;
                promoStatusLabel.setText("20% discount applied!");
                promoStatusLabel.getStyleClass().setAll("promo-status", "success");
                break;
            case "FREESHIP":
                shipping = 0;
                promoStatusLabel.setText("Free shipping applied!");
                promoStatusLabel.getStyleClass().setAll("promo-status", "success");
                break;
            default:
                discount = 0;
                promoStatusLabel.setText("Invalid promo code");
                promoStatusLabel.getStyleClass().setAll("promo-status", "error");
        }
        
        calculateTotals();
    }
    
    @FXML
    private void handlePlaceOrder() {
        if (!validateForm()) return;
        
        if (!termsCheck.isSelected()) {
            showError("Please accept the terms and conditions.");
            return;
        }
        
        showLoading(true);
        placeOrderButton.setDisable(true);
        
        new Thread(() -> {
            try {
                // Process payment
                String paymentMethod = getSelectedPaymentMethod();
                double total = subtotal + tax + shipping - discount;
                
                boolean paymentSuccess;
                if ("Credit Card".equals(paymentMethod)) {
                    paymentSuccess = paymentService.processCardPayment(
                        cardNumberField.getText(),
                        cardHolderField.getText(),
                        expiryField.getText(),
                        cvvField.getText(),
                        total
                    );
                } else if ("PayPal".equals(paymentMethod)) {
                    paymentSuccess = paymentService.processPayPalPayment(
                        currentUser.getEmail(),
                        total
                    );
                } else {
                    paymentSuccess = true; // Cash on delivery
                }
                
                if (paymentSuccess) {
                    // Create order object
                    Order newOrder = new Order();
                    newOrder.setUser(currentUser);
                    newOrder.setOrderItems(OrderItems);
                    newOrder.setDeliveryAddress(addressField.getText());
                    newOrder.setCity(cityField.getText());
                    newOrder.setPostalCode(zipCodeField.getText());
                    newOrder.setCountry(countryCombo.getValue());
                    newOrder.setPaymentMethod(paymentMethod);
                    newOrder.setTotalAmount(total);
                    
                    // Create order
                    orderService.create(newOrder);
                    Long orderId = newOrder.getId();
                    
                    // Clear cart
                    cartService.clearCart(currentUser.getId());
                    
                    Platform.runLater(() -> {
                        showLoading(false);
                        showOrderConfirmation(orderId.intValue());
                    });
                } else {
                    Platform.runLater(() -> {
                        showLoading(false);
                        placeOrderButton.setDisable(false);
                        showError("Payment failed. Please try again.");
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error placing order", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    placeOrderButton.setDisable(false);
                    showError("Failed to place order. Please try again.");
                });
            }
        }).start();
    }
    
    private String getSelectedPaymentMethod() {
        if (creditCardRadio.isSelected()) return "Credit Card";
        if (paypalRadio.isSelected()) return "PayPal";
        return "Cash on Delivery";
    }
    
    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Please enter your full name.");
            return false;
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showError("Please enter a valid email.");
            return false;
        }
        if (addressField.getText().trim().isEmpty()) {
            showError("Please enter your address.");
            return false;
        }
        if (cityField.getText().trim().isEmpty()) {
            showError("Please enter your city.");
            return false;
        }
        if (zipCodeField.getText().trim().isEmpty()) {
            showError("Please enter your ZIP code.");
            return false;
        }
        
        if (creditCardRadio.isSelected()) {
            if (cardNumberField.getText().trim().length() < 16) {
                showError("Please enter a valid card number.");
                return false;
            }
            if (cardHolderField.getText().trim().isEmpty()) {
                showError("Please enter the card holder name.");
                return false;
            }
            if (expiryField.getText().trim().isEmpty()) {
                showError("Please enter the card expiry date.");
                return false;
            }
            if (cvvField.getText().trim().length() < 3) {
                showError("Please enter a valid CVV.");
                return false;
            }
        }
        
        return true;
    }
    
    private void showOrderConfirmation(int orderId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed!");
        alert.setHeaderText("Thank you for your order!");
        alert.setContentText("Your order #" + orderId + " has been placed successfully.\n" +
            "You will receive a confirmation email shortly.");
        
        alert.showAndWait();
        
        // Navigate to order tracking
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/shop/OrderTracking.fxml"));
            Parent root = loader.load();
            
            OrderTrackingController controller = loader.getController();
            controller.setOrderId(orderId);
            
            Stage stage = (Stage) checkoutContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/shop.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to order tracking", e);
            handleBack();
        }
    }
    
    @FXML
    private void handleContinueShopping() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) checkoutContainer.getScene().getWindow();
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
            Stage stage = (Stage) checkoutContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

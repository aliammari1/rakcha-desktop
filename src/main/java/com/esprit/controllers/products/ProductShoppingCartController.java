package com.esprit.controllers.products;

import com.esprit.models.products.Product;
import com.esprit.models.products.ShoppingCart;
import com.esprit.models.users.User;
import com.esprit.services.products.CartService;
import com.esprit.services.products.ProductService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for product ShoppingCart management.
 */
public class ProductShoppingCartController {

    private static final Logger LOGGER = Logger.getLogger(ProductShoppingCartController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final CartService CartService;
    private final ProductService productService;
    private final CartService cartService;
    @FXML
    private VBox ShoppingCartContainer;
    @FXML
    private FlowPane ShoppingCartGrid;
    @FXML
    private Label ShoppingCartCountLabel;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private TextField searchField;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox emptyShoppingCartBox;
    @FXML
    private Button selectAllBtn;
    @FXML
    private Button moveToCartBtn;
    @FXML
    private Button removeSelectedBtn;
    @FXML
    private Label selectedCountLabel;
    @FXML
    private HBox bulkActionsBox;
    private ObservableList<ShoppingCart> ShoppingOrderItems;
    private ObservableList<ShoppingCart> selectedItems;
    private User currentUser;

    public ProductShoppingCartController() {
        this.CartService = new CartService();
        this.productService = new ProductService();
        this.cartService = new CartService();
        this.ShoppingOrderItems = FXCollections.observableArrayList();
        this.selectedItems = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing ProductShoppingCartController");

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupSort();
        setupSearch();
        updateBulkActionsVisibility();
        loadShoppingCart();
    }

    private void setupSort() {
        sortCombo.getItems().addAll("Date Added (Newest)", "Date Added (Oldest)",
            "Price: Low to High", "Price: High to Low", "Name A-Z");
        sortCombo.setValue("Date Added (Newest)");
        sortCombo.setOnAction(e -> sortShoppingCart());
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterShoppingCart(newVal));
        }
    }

    private void loadShoppingCart() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<ShoppingCart> items = CartService.getShoppingCartByUser(currentUser);

                Platform.runLater(() -> {
                    ShoppingOrderItems.setAll(items);
                    displayShoppingCart();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading ShoppingCart", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load ShoppingCart.");
                });
            }
        }).start();
    }

    private void displayShoppingCart() {
        ShoppingCartGrid.getChildren().clear();
        selectedItems.clear();
        updateBulkActionsVisibility();

        if (ShoppingOrderItems.isEmpty()) {
            emptyShoppingCartBox.setVisible(true);
            ShoppingCartGrid.setVisible(false);
            ShoppingCartCountLabel.setText("0 items");
        } else {
            emptyShoppingCartBox.setVisible(false);
            ShoppingCartGrid.setVisible(true);
            ShoppingCartCountLabel.setText(ShoppingOrderItems.size() + " item" + (ShoppingOrderItems.size() != 1 ? "s" : ""));

            for (ShoppingCart item : ShoppingOrderItems) {
                ShoppingCartGrid.getChildren().add(createShoppingCartCard(item));
            }
        }
    }

    private VBox createShoppingCartCard(ShoppingCart item) {
        VBox card = new VBox(12);
        card.getStyleClass().add("ShoppingCart-card");
        card.setPrefWidth(280);

        // Selection checkbox
        CheckBox selectBox = new CheckBox();
        selectBox.getStyleClass().add("ShoppingCart-select");
        selectBox.setOnAction(e -> {
            if (selectBox.isSelected()) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
            updateBulkActionsVisibility();
        });

        // Product image
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("product-image-container");
        imageContainer.setPrefHeight(200);

        ImageView productImage = new ImageView();
        productImage.setFitWidth(260);
        productImage.setFitHeight(180);
        productImage.setPreserveRatio(true);

        Product product = item.getProduct();
        if (product != null && product.getImageUrl() != null) {
            try {
                productImage.setImage(new Image(product.getImageUrl(), true));
            } catch (Exception e) {
                productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        }

        // Stock badge
        Label stockBadge = new Label();
        stockBadge.getStyleClass().add("stock-badge");
        if (product != null) {
            if (product.getStock() > 0) {
                stockBadge.setText("In Stock");
                stockBadge.getStyleClass().add("in-stock");
            } else {
                stockBadge.setText("Out of Stock");
                stockBadge.getStyleClass().add("out-of-stock");
            }
        }
        StackPane.setAlignment(stockBadge, Pos.TOP_RIGHT);

        // Remove button overlay
        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-overlay-btn");
        removeBtn.setOnAction(e -> removeFromShoppingCart(item));
        StackPane.setAlignment(removeBtn, Pos.TOP_LEFT);

        imageContainer.getChildren().addAll(productImage, stockBadge, removeBtn);

        // Product info
        VBox infoBox = new VBox(6);
        infoBox.getStyleClass().add("product-info");

        Label nameLabel = new Label(product != null ? product.getName() : "Unknown");
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        Label categoryLabel = new Label(product != null && product.getCategory() != null ?
            product.getCategory() : "");
        categoryLabel.getStyleClass().add("product-category");

        HBox priceBox = new HBox(8);
        priceBox.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label(String.format("$%.2f", product != null ? product.getPrice() : 0));
        priceLabel.getStyleClass().add("product-price");

        // Original price if on sale
        if (product != null && product.getOriginalPrice() > product.getPrice()) {
            Label originalPrice = new Label(String.format("$%.2f", product.getOriginalPrice()));
            originalPrice.getStyleClass().add("original-price");

            double discount = ((product.getOriginalPrice() - product.getPrice()) / product.getOriginalPrice()) * 100;
            Label discountLabel = new Label(String.format("-%.0f%%", discount));
            discountLabel.getStyleClass().add("discount-badge");

            priceBox.getChildren().addAll(priceLabel, originalPrice, discountLabel);
        } else {
            priceBox.getChildren().add(priceLabel);
        }

        Label addedLabel = new Label("Added " + (item.getAddedAt() != null ?
            item.getAddedAt().format(DATE_FORMAT) : "recently"));
        addedLabel.getStyleClass().add("added-date");

        infoBox.getChildren().addAll(nameLabel, categoryLabel, priceBox, addedLabel);

        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER);

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.getStyleClass().addAll("btn", "btn-primary");
        addToCartBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addToCartBtn, Priority.ALWAYS);
        addToCartBtn.setOnAction(e -> addToCart(item));
        addToCartBtn.setDisable(product == null || product.getStock() <= 0);

        Button viewBtn = new Button("👁");
        viewBtn.getStyleClass().addAll("btn", "btn-secondary", "icon-btn");
        viewBtn.setOnAction(e -> viewProduct(item));

        actionBox.getChildren().addAll(addToCartBtn, viewBtn);

        // Assemble card
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.getChildren().add(selectBox);

        card.getChildren().addAll(topRow, imageContainer, infoBox, actionBox);

        return card;
    }

    private void updateBulkActionsVisibility() {
        if (bulkActionsBox != null) {
            bulkActionsBox.setVisible(!selectedItems.isEmpty());
        }
        if (selectedCountLabel != null) {
            selectedCountLabel.setText(selectedItems.size() + " selected");
        }
    }

    @FXML
    private void handleSelectAll() {
        boolean selectAll = selectedItems.size() < ShoppingOrderItems.size();

        if (selectAll) {
            selectedItems.setAll(ShoppingOrderItems);
        } else {
            selectedItems.clear();
        }

        // Refresh display
        displayShoppingCart();

        if (selectAllBtn != null) {
            selectAllBtn.setText(selectAll ? "Deselect All" : "Select All");
        }
    }

    @FXML
    private void handleMoveSelectedToCart() {
        if (selectedItems.isEmpty()) {
            showInfo("No items selected.");
            return;
        }

        showLoading(true);

        new Thread(() -> {
            int successCount = 0;

            for (ShoppingCart item : selectedItems) {
                try {
                    Product product = item.getProduct();
                    if (product != null && product.getStock() > 0) {
                        cartService.addToCart(currentUser.getId(), product.getId(), 1);
                        cartService.removeFromCart(item.getId());
                        successCount++;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error moving item to cart", e);
                }
            }

            final int count = successCount;
            Platform.runLater(() -> {
                showLoading(false);
                if (count > 0) {
                    showSuccess(count + " item(s) moved to cart.");
                    loadShoppingCart();
                } else {
                    showError("Failed to move items to cart.");
                }
            });
        }).start();
    }

    @FXML
    private void handleRemoveSelected() {
        if (selectedItems.isEmpty()) {
            showInfo("No items selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Items");
        confirm.setHeaderText("Remove " + selectedItems.size() + " item(s) from ShoppingCart?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                removeSelectedItems();
            }
        });
    }

    private void removeSelectedItems() {
        showLoading(true);

        new Thread(() -> {
            for (ShoppingCart item : selectedItems) {
                try {
                    cartService.removeFromCart(item.getId());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error removing ShoppingCart item", e);
                }
            }

            Platform.runLater(() -> {
                showLoading(false);
                loadShoppingCart();
            });
        }).start();
    }

    private void addToCart(ShoppingCart item) {
        Product product = item.getProduct();
        if (product == null || product.getStock() <= 0) {
            showError("Product is out of stock.");
            return;
        }

        new Thread(() -> {
            try {
                cartService.addToCart(currentUser.getId(), product.getId(), 1);

                Platform.runLater(() -> {
                    showSuccess("Added to cart!");

                    // Optionally remove from ShoppingCart
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Remove from ShoppingCart?");
                    confirm.setHeaderText("Remove from ShoppingCart?");
                    confirm.setContentText("Do you want to remove this item from your ShoppingCart?");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            removeFromShoppingCart(item);
                        }
                    });
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error adding to cart", e);
                Platform.runLater(() -> showError("Failed to add to cart."));
            }
        }).start();
    }

    private void removeFromShoppingCart(ShoppingCart item) {
        new Thread(() -> {
            try {
                cartService.removeFromCart(item.getId());
                Platform.runLater(() -> loadShoppingCart());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error removing from ShoppingCart", e);
                Platform.runLater(() -> showError("Failed to remove item."));
            }
        }).start();
    }

    private void viewProduct(ShoppingCart item) {
        Product product = item.getProduct();
        if (product == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ProductDetails.fxml"));
            Parent root = loader.load();

            // Pass product to controller if method exists
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setProduct", Product.class).invoke(controller, product);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "No setProduct method", e);
                }
            }

            Stage stage = (Stage) ShoppingCartContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error viewing product", e);
        }
    }

    private void sortShoppingCart() {
        String sortBy = sortCombo.getValue();

        ShoppingOrderItems.sort((a, b) -> {
            Product pA = a.getProduct();
            Product pB = b.getProduct();

            switch (sortBy) {
                case "Date Added (Newest)":
                    return b.getAddedAt().compareTo(a.getAddedAt());
                case "Date Added (Oldest)":
                    return a.getAddedAt().compareTo(b.getAddedAt());
                case "Price: Low to High":
                    double priceA = pA != null ? pA.getPrice() : 0;
                    double priceB = pB != null ? pB.getPrice() : 0;
                    return Double.compare(priceA, priceB);
                case "Price: High to Low":
                    priceA = pA != null ? pA.getPrice() : 0;
                    priceB = pB != null ? pB.getPrice() : 0;
                    return Double.compare(priceB, priceA);
                case "Name A-Z":
                    String nameA = pA != null ? pA.getName() : "";
                    String nameB = pB != null ? pB.getName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                default:
                    return 0;
            }
        });

        displayShoppingCart();
    }

    private void filterShoppingCart(String query) {
        if (query == null || query.isEmpty()) {
            displayShoppingCart();
            return;
        }

        String lowerQuery = query.toLowerCase();

        ShoppingCartGrid.getChildren().clear();

        for (ShoppingCart item : ShoppingOrderItems) {
            Product product = item.getProduct();
            if (product != null) {
                String name = product.getName() != null ? product.getName().toLowerCase() : "";
                String category = product.getCategory() != null ? product.getCategory().toLowerCase() : "";

                if (name.contains(lowerQuery) || category.contains(lowerQuery)) {
                    ShoppingCartGrid.getChildren().add(createShoppingCartCard(item));
                }
            }
        }
    }

    @FXML
    private void handleClearShoppingCart() {
        if (ShoppingOrderItems.isEmpty()) {
            showInfo("ShoppingCart is already empty.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear ShoppingCart");
        confirm.setHeaderText("Clear entire ShoppingCart?");
        confirm.setContentText("This will remove all " + ShoppingOrderItems.size() + " items. This cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearShoppingCart();
            }
        });
    }

    private void clearShoppingCart() {
        showLoading(true);

        new Thread(() -> {
            try {
                CartService.clearShoppingCart(currentUser.getId());
                Platform.runLater(() -> {
                    showLoading(false);
                    loadShoppingCart();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error clearing ShoppingCart", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to clear ShoppingCart.");
                });
            }
        }).start();
    }

    @FXML
    private void handleStartShopping() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ShoppingCartContainer.getScene().getWindow();
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
            Stage stage = (Stage) ShoppingCartContainer.getScene().getWindow();
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

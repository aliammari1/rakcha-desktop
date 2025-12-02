package com.esprit.controllers.products;

import com.esprit.models.users.User;
import com.esprit.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ProductWishlist.fxml screen.
 * Manages user's product wishlist functionality.
 */
@Slf4j
public class ProductWishlistController implements Initializable {

    // Header elements
    @FXML
    private Label wishlistCountBadge;
    @FXML
    private Button addAllToCartBtn;

    // Category filters
    @FXML
    private ToggleButton allCategoryBtn;
    @FXML
    private ToggleButton snacksCategoryBtn;
    @FXML
    private ToggleButton drinksCategoryBtn;
    @FXML
    private ToggleButton combosCategoryBtn;
    @FXML
    private ToggleButton merchandiseCategoryBtn;
    @FXML
    private ComboBox<String> sortCombo;

    // Content area
    @FXML
    private ScrollPane wishlistScrollPane;
    @FXML
    private FlowPane wishlistGrid;
    @FXML
    private VBox emptyState;

    // Summary bar
    @FXML
    private HBox summaryBar;
    @FXML
    private Label selectedCountLabel;
    @FXML
    private Label totalValueLabel;

    // Quick view dialog
    @FXML
    private VBox quickViewDialog;
    @FXML
    private ImageView quickViewImage;
    @FXML
    private HBox thumbnailsContainer;
    @FXML
    private Label quickViewCategory;
    @FXML
    private Label quickViewName;
    @FXML
    private Label quickViewPrice;
    @FXML
    private Label quickViewOriginalPrice;
    @FXML
    private Label discountBadge;
    @FXML
    private Label quickViewDescription;
    @FXML
    private VBox sizeSection;
    @FXML
    private HBox sizeOptionsContainer;
    @FXML
    private Label quantityLabel;
    @FXML
    private HBox stockStatus;
    @FXML
    private Label stockLabel;

    // Remove dialog
    @FXML
    private VBox removeDialog;
    @FXML
    private Label removeProductName;

    // Toast
    @FXML
    private HBox cartToast;
    @FXML
    private Label toastMessage;

    private User currentUser;
    private int quantity = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SessionManager.getCurrentUser();
        loadWishlist();
        setupFilters();
        log.info("ProductWishlistController initialized");
    }

    private void loadWishlist() {
        // TODO: Load wishlist items from service
        updateEmptyState();
    }

    private void setupFilters() {
        if (sortCombo != null) {
            sortCombo.setOnAction(e -> sortWishlist());
        }
    }

    private void updateEmptyState() {
        boolean isEmpty = wishlistGrid == null || wishlistGrid.getChildren().isEmpty();
        if (emptyState != null) {
            emptyState.setVisible(isEmpty);
            emptyState.setManaged(isEmpty);
        }
        if (wishlistScrollPane != null) {
            wishlistScrollPane.setVisible(!isEmpty);
        }
    }

    private void sortWishlist() {
        // TODO: Implement sorting logic
        log.debug("Sorting wishlist");
    }

    // Filter methods
    @FXML
    void filterAll(ActionEvent event) {
        log.debug("Filter: All");
        loadWishlist();
    }

    @FXML
    void filterSnacks(ActionEvent event) {
        log.debug("Filter: Snacks");
        // TODO: Filter by snacks category
    }

    @FXML
    void filterDrinks(ActionEvent event) {
        log.debug("Filter: Drinks");
        // TODO: Filter by drinks category
    }

    @FXML
    void filterCombos(ActionEvent event) {
        log.debug("Filter: Combos");
        // TODO: Filter by combos category
    }

    @FXML
    void filterMerchandise(ActionEvent event) {
        log.debug("Filter: Merchandise");
        // TODO: Filter by merchandise category
    }

    // Cart actions
    @FXML
    void addAllToCart(ActionEvent event) {
        log.info("Adding all wishlist items to cart");
        // TODO: Implement add all to cart
        showToast("All items added to cart");
    }

    @FXML
    void addSelectedToCart(ActionEvent event) {
        log.info("Adding selected items to cart");
        // TODO: Implement add selected to cart
        showToast("Selected items added to cart");
    }

    @FXML
    void addToCartFromQuickView(ActionEvent event) {
        log.info("Adding item from quick view to cart");
        // TODO: Add current quick view item to cart
        closeQuickView(event);
        showToast("Added to cart");
    }

    @FXML
    void clearSelected(ActionEvent event) {
        log.info("Clearing selected items");
        // TODO: Clear selection
    }

    @FXML
    void browseProducts(ActionEvent event) {
        log.info("Navigate to products");
        // TODO: Navigate to products page
    }

    @FXML
    void viewCart(ActionEvent event) {
        log.info("Navigate to cart");
        // TODO: Navigate to shopping cart
    }

    // Quick view methods
    @FXML
    void closeQuickView(ActionEvent event) {
        if (quickViewDialog != null) {
            quickViewDialog.setVisible(false);
            quickViewDialog.setManaged(false);
        }
    }

    @FXML
    void decreaseQuantity(ActionEvent event) {
        if (quantity > 1) {
            quantity--;
            updateQuantityLabel();
        }
    }

    @FXML
    void increaseQuantity(ActionEvent event) {
        quantity++;
        updateQuantityLabel();
    }

    private void updateQuantityLabel() {
        if (quantityLabel != null) {
            quantityLabel.setText(String.valueOf(quantity));
        }
    }

    @FXML
    void removeFromWishlistQuickView(ActionEvent event) {
        // Show remove confirmation dialog
        if (removeDialog != null) {
            removeDialog.setVisible(true);
            removeDialog.setManaged(true);
        }
        closeQuickView(event);
    }

    // Remove dialog methods
    @FXML
    void cancelRemove(ActionEvent event) {
        if (removeDialog != null) {
            removeDialog.setVisible(false);
            removeDialog.setManaged(false);
        }
    }

    @FXML
    void confirmRemove(ActionEvent event) {
        log.info("Removing item from wishlist");
        // TODO: Remove item from wishlist
        cancelRemove(event);
        loadWishlist();
    }

    // Toast helper
    private void showToast(String message) {
        if (cartToast != null && toastMessage != null) {
            toastMessage.setText(message);
            cartToast.setVisible(true);
            cartToast.setManaged(true);
            
            // Auto-hide after 3 seconds
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            if (cartToast != null) {
                                cartToast.setVisible(false);
                                cartToast.setManaged(false);
                            }
                        });
                    }
                },
                3000
            );
        }
    }
}

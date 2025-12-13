package com.esprit.utils;

import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing navigation throughout the application.
 * Provides methods to navigate between screens based on user role and context.
 * Centralizes navigation logic to ensure consistent navigation patterns.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class NavigationManager {

    private static final Logger LOGGER = Logger.getLogger(NavigationManager.class.getName());
    private static final int DEFAULT_WINDOW_WIDTH = 1600;
    private static final int DEFAULT_WINDOW_HEIGHT = 900;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private NavigationManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Navigates to a screen based on FXML path.
     *
     * @param stage    the Stage to set the scene on
     * @param fxmlPath the path to the FXML file (e.g., "/ui/users/Profile.fxml")
     * @return true if navigation was successful, false otherwise
     */
    public static boolean navigate(Stage stage, String fxmlPath) {
        return navigate(stage, fxmlPath, null);
    }

    /**
     * Navigates to a screen and passes data to the controller.
     *
     * @param stage      the Stage to set the scene on
     * @param fxmlPath   the path to the FXML file
     * @param controller callback to configure the loaded controller
     * @return true if navigation was successful, false otherwise
     */
    public static boolean navigate(Stage stage, String fxmlPath, ControllerCallback controller) {
        if (stage == null) {
            LOGGER.warning("Cannot navigate: Stage is null");
            return false;
        }

        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Configure controller if callback provided
            if (controller != null) {
                controller.configure(loader.getController());
            }

            Scene scene = new Scene(root, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();

            LOGGER.info("Successfully navigated to: " + fxmlPath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML: " + fxmlPath, e);
            return false;
        }
    }

    /**
     * Navigates to the home screen based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToHome(Stage stage, User user) {
        if (user == null) {
            LOGGER.warning("Cannot navigate to home: user is null");
            return false;
        }

        String fxmlPath;
        if (user instanceof Admin) {
            fxmlPath = "/ui/users/HomeAdmin.fxml";
        } else if (user instanceof CinemaManager) {
            fxmlPath = "/ui/users/HomeCinemaManager.fxml";
        } else {
            fxmlPath = "/ui/users/HomeClient.fxml";
        }

        return navigate(stage, fxmlPath);
    }

    /**
     * Navigates to the dashboard based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToDashboard(Stage stage, User user) {
        if (user == null) {
            LOGGER.warning("Cannot navigate to dashboard: user is null");
            return false;
        }

        if (user instanceof Admin) {
            return navigate(stage, "/ui/users/AdminDashboard.fxml");
        } else if (user instanceof CinemaManager) {
            return navigate(stage, "/ui/cinemas/DashboardResponsableCinema.fxml");
        } else {
            return navigate(stage, "/ui/users/ClientDashboard.fxml");
        }
    }

    /**
     * Navigates to the profile screen.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToProfile(Stage stage, User user) {
        return navigate(stage, "/ui/users/Profile.fxml", controller -> {
            if (controller != null && controller instanceof IDataProvider) {
                ((IDataProvider) controller).setData(user);
            }
        });
    }

    /**
     * Navigates to movies screen based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToMovies(Stage stage, User user) {
        String fxmlPath;
        if (user instanceof Admin || user instanceof CinemaManager) {
            fxmlPath = "/ui/films/InterfaceFilm.fxml";
        } else {
            fxmlPath = "/ui/films/filmuser.fxml";
        }
        return navigate(stage, fxmlPath);
    }

    /**
     * Navigates to series screen based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToSeries(Stage stage, User user) {
        String fxmlPath;
        if (user instanceof Admin || user instanceof CinemaManager) {
            fxmlPath = "/ui/series/Serie-view.fxml";
        } else {
            fxmlPath = "/ui/series/SeriesClient.fxml";
        }
        return navigate(stage, fxmlPath);
    }

    /**
     * Navigates to products screen based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToProducts(Stage stage, User user) {
        String fxmlPath;
        if (user instanceof Admin) {
            fxmlPath = "/ui/products/DesignProduitAdmin.fxml";
        } else {
            fxmlPath = "/ui/products/AfficherProduitClient.fxml";
        }
        return navigate(stage, fxmlPath);
    }

    /**
     * Navigates to cinema screen based on user role.
     *
     * @param stage the Stage to navigate on
     * @param user  the current user
     * @return true if navigation was successful
     */
    public static boolean navigateToCinema(Stage stage, User user) {
        String fxmlPath;
        if (user instanceof Admin) {
            fxmlPath = "/ui/cinemas/DashboardAdminCinema.fxml";
        } else if (user instanceof CinemaManager) {
            fxmlPath = "/ui/cinemas/DashboardResponsableCinema.fxml";
        } else {
            fxmlPath = "/ui/cinemas/DashboardClientCinema.fxml";
        }
        return navigate(stage, fxmlPath);
    }

    /**
     * Navigates to shopping cart (Client only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToShoppingCart(Stage stage) {
        return navigate(stage, "/ui/products/PanierProduit.fxml");
    }

    /**
     * Navigates to favorites list (Client only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToFavorites(Stage stage) {
        return navigate(stage, "/ui/series/ListFavoris.fxml");
    }

    /**
     * Navigates to orders list (Client only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToMyOrders(Stage stage) {
        return navigate(stage, "/ui/products/CommandeClient.fxml");
    }

    /**
     * Navigates to admin orders list (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToOrders(Stage stage) {
        return navigate(stage, "/ui/products/ListeCommande.fxml");
    }

    /**
     * Navigates to users management (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToUsers(Stage stage) {
        return navigate(stage, "/ui/users/AdminDashboard.fxml");
    }

    /**
     * Navigates to category management.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToCategories(Stage stage) {
        return navigate(stage, "/ui/common/CategoryManagement.fxml");
    }

    /**
     * Navigates to order analytics (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToOrderAnalytics(Stage stage) {
        return navigate(stage, "/ui/products/AnalyseCommande.fxml");
    }

    /**
     * Navigates to series statistics (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToSeriesStatistics(Stage stage) {
        return navigate(stage, "/ui/series/StatistiquesView.fxml");
    }

    /**
     * Navigates to email composer (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToEmailComposer(Stage stage) {
        return navigate(stage, "/ui/users/CinematicEmailComposer.fxml");
    }

    /**
     * Navigates to SMS sender (Admin only).
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToSMSComposer(Stage stage) {
        return navigate(stage, "/ui/users/smsadmin.fxml");
    }

    /**
     * Navigates to Account Settings.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToAccountSettings(Stage stage) {
        return navigate(stage, "/ui/users/AccountSettings.fxml");
    }

    /**
     * Navigates to User Notifications.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToNotifications(Stage stage) {
        return navigate(stage, "/ui/users/UserNotifications.fxml");
    }

    /**
     * Navigates to Watchlist.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToWatchlist(Stage stage) {
        return navigate(stage, "/ui/users/Watchlist.fxml");
    }

    /**
     * Navigates to Product Wishlist.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToProductWishlist(Stage stage) {
        return navigate(stage, "/ui/shop/ProductWishlist.fxml");
    }

    /**
     * Navigates to Ticket History.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToTicketHistory(Stage stage) {
        return navigate(stage, "/ui/tickets/TicketHistory.fxml");
    }

    /**
     * Navigates to User Review History.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToReviewHistory(Stage stage) {
        return navigate(stage, "/ui/reviews/UserReviewHistory.fxml");
    }

    /**
     * Navigates to Series Watch Progress.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToWatchProgress(Stage stage) {
        return navigate(stage, "/ui/series/SeriesWatchProgress.fxml");
    }

    /**
     * Navigates to Movie Session Browser.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToMovieSessionBrowser(Stage stage) {
        return navigate(stage, "/ui/booking/MovieSessionBrowser.fxml");
    }

    /**
     * Navigates to login screen and clears user session.
     *
     * @param stage the Stage to navigate on
     * @return true if navigation was successful
     */
    public static boolean navigateToLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/ui/users/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Clear session
            SessionManager.getInstance().logout();

            LOGGER.info("Successfully navigated to login");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login screen", e);
            return false;
        }
    }

    /**
     * Callback interface for configuring controllers during navigation.
     */
    @FunctionalInterface
    public interface ControllerCallback {

        /**
         * Called to configure the loaded controller.
         *
         * @param controller the loaded controller instance
         */
        void configure(Object controller);
    }

    /**
     * Interface for controllers that accept data during navigation.
     */
    public interface IDataProvider {

        /**
         * Sets data on the controller.
         *
         * @param data the data to set
         */
        void setData(Object data);
    }
}

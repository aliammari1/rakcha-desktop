package com.esprit.controllers.users;

import com.esprit.models.users.Notification;
import com.esprit.models.users.User;
import com.esprit.services.users.NotificationService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing user notifications and preferences.
 */
public class UserNotificationsController {

    private static final Logger LOGGER = Logger.getLogger(UserNotificationsController.class.getName());
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final NotificationService notificationService;
    @FXML
    private VBox notificationsContainer;
    @FXML
    private VBox notificationsList;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label unreadCountLabel;
    @FXML
    private ToggleButton allToggle;
    @FXML
    private ToggleButton unreadToggle;
    @FXML
    private CheckBox bookingNotificationsCheck;
    @FXML
    private CheckBox promotionsNotificationsCheck;
    @FXML
    private CheckBox newReleasesNotificationsCheck;
    @FXML
    private CheckBox friendActivityNotificationsCheck;
    @FXML
    private CheckBox systemNotificationsCheck;
    @FXML
    private CheckBox emailNotificationsCheck;
    @FXML
    private CheckBox pushNotificationsCheck;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox emptyStateBox;
    private ObservableList<Notification> notifications;
    private User currentUser;
    private boolean showUnreadOnly = false;

    public UserNotificationsController() {
        this.notificationService = new NotificationService();
        this.notifications = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing UserNotificationsController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupFilters();
        setupToggleGroup();
        loadNotifications();
    }

    private void setupFilters() {
        if (filterCombo != null) {
            filterCombo.getItems().addAll("All Types", "Bookings", "Promotions", "New Releases", "Friends", "System");
            filterCombo.setValue("All Types");
            filterCombo.setOnAction(e -> filterNotifications());
        }
    }

    private void setupToggleGroup() {
        ToggleGroup toggleGroup = new ToggleGroup();
        if (allToggle != null) {
            allToggle.setToggleGroup(toggleGroup);
            allToggle.setSelected(true);
            allToggle.setOnAction(e -> {
                showUnreadOnly = false;
                filterNotifications();
            });
        }
        if (unreadToggle != null) {
            unreadToggle.setToggleGroup(toggleGroup);
            unreadToggle.setOnAction(e -> {
                showUnreadOnly = true;
                filterNotifications();
            });
        }
    }

    private void loadNotifications() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Notification> notifList = notificationService.getNotificationsByUserId(currentUser.getId());

                Platform.runLater(() -> {
                    notifications.setAll(notifList);
                    updateStatistics();
                    displayNotifications();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading notifications", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load notifications.");
                });
            }
        }).start();
    }

    private void filterNotifications() {
        displayNotifications();
    }

    private void displayNotifications() {
        if (notificationsList == null) {
            LOGGER.warning("notificationsList is null, cannot display notifications");
            return;
        }
        notificationsList.getChildren().clear();

        String filter = filterCombo != null ? filterCombo.getValue() : "All Types";

        List<Notification> filtered = notifications.stream()
            .filter(n -> {
                if (showUnreadOnly && n.isRead()) return false;
                if (filter != null && !"All Types".equals(filter) && !matchesType(n, filter)) return false;
                return true;
            })
            .toList();

        if (filtered.isEmpty()) {
            emptyStateBox.setVisible(true);
            notificationsList.setVisible(false);
        } else {
            emptyStateBox.setVisible(false);
            notificationsList.setVisible(true);

            // Group by date
            LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime yesterday = today.minusDays(1);

            VBox todayBox = new VBox(8);
            VBox yesterdayBox = new VBox(8);
            VBox olderBox = new VBox(8);

            for (Notification notif : filtered) {
                HBox card = createNotificationCard(notif);

                LocalDateTime notifDate = notif.getCreatedAt().toLocalDateTime().truncatedTo(ChronoUnit.DAYS);

                if (notifDate.equals(today)) {
                    todayBox.getChildren().add(card);
                } else if (notifDate.equals(yesterday)) {
                    yesterdayBox.getChildren().add(card);
                } else {
                    olderBox.getChildren().add(card);
                }
            }

            if (!todayBox.getChildren().isEmpty()) {
                Label header = new Label("Today");
                header.getStyleClass().add("date-header");
                notificationsList.getChildren().addAll(header, todayBox);
            }

            if (!yesterdayBox.getChildren().isEmpty()) {
                Label header = new Label("Yesterday");
                header.getStyleClass().add("date-header");
                notificationsList.getChildren().addAll(header, yesterdayBox);
            }

            if (!olderBox.getChildren().isEmpty()) {
                Label header = new Label("Earlier");
                header.getStyleClass().add("date-header");
                notificationsList.getChildren().addAll(header, olderBox);
            }
        }
    }

    private boolean matchesType(Notification notif, String filter) {
        String type = notif.getType() != null ? notif.getType().toLowerCase() : "";

        switch (filter) {
            case "Bookings":
                return type.contains("booking") || type.contains("ticket");
            case "Promotions":
                return type.contains("promo") || type.contains("offer");
            case "New Releases":
                return type.contains("release") || type.contains("new");
            case "Friends":
                return type.contains("friend") || type.contains("social");
            case "System":
                return type.contains("system") || type.contains("account");
            default:
                return true;
        }
    }

    private HBox createNotificationCard(Notification notif) {
        HBox card = new HBox(12);
        card.getStyleClass().add("notification-card");
        if (!notif.isRead()) {
            card.getStyleClass().add("unread");
        }

        // Icon
        Label iconLabel = new Label(getNotificationIcon(notif.getType()));
        iconLabel.getStyleClass().add("notification-icon");

        // Content
        VBox content = new VBox(4);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label titleLabel = new Label(notif.getTitle());
        titleLabel.getStyleClass().add("notification-title");

        Label messageLabel = new Label(notif.getMessage());
        messageLabel.getStyleClass().add("notification-message");
        messageLabel.setWrapText(true);

        Label timeLabel = new Label(formatNotificationTime(notif.getCreatedAt().toLocalDateTime()));
        timeLabel.getStyleClass().add("notification-time");

        content.getChildren().addAll(titleLabel, messageLabel, timeLabel);

        // Actions
        VBox actions = new VBox(4);

        if (!notif.isRead()) {
            Button markReadBtn = new Button("Mark Read");
            markReadBtn.getStyleClass().add("mark-read-btn");
            markReadBtn.setOnAction(e -> markAsRead(notif));
            actions.getChildren().add(markReadBtn);
        }

        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> deleteNotification(notif));
        actions.getChildren().add(deleteBtn);

        card.getChildren().addAll(iconLabel, content, actions);

        // Click to view details
        card.setOnMouseClicked(e -> {
            if (!notif.isRead()) {
                markAsRead(notif);
            }
            handleNotificationAction(notif);
        });

        return card;
    }

    private String getNotificationIcon(String type) {
        if (type == null) return "🔔";

        String typeLower = type.toLowerCase();
        if (typeLower.contains("booking") || typeLower.contains("ticket")) return "🎫";
        if (typeLower.contains("promo") || typeLower.contains("offer")) return "🎁";
        if (typeLower.contains("release") || typeLower.contains("new")) return "🎬";
        if (typeLower.contains("friend") || typeLower.contains("social")) return "👥";
        if (typeLower.contains("system") || typeLower.contains("account")) return "⚙️";
        return "🔔";
    }

    private String formatNotificationTime(LocalDateTime time) {
        if (time == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(time, now);
        long hours = ChronoUnit.HOURS.between(time, now);
        long days = ChronoUnit.DAYS.between(time, now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24) return hours + "h ago";
        if (days < 7) return days + "d ago";
        return time.format(DATE_FORMAT);
    }

    private void updateStatistics() {
        int total = notifications.size();
        long unread = notifications.stream().filter(n -> !n.isRead()).count();

        if (totalCountLabel != null) totalCountLabel.setText(String.valueOf(total));
        if (unreadCountLabel != null) unreadCountLabel.setText(String.valueOf(unread));
    }

    private void markAsRead(Notification notif) {
        new Thread(() -> {
            try {
                notificationService.markAsRead(notif.getId());

                Platform.runLater(() -> {
                    notif.setRead(true);
                    updateStatistics();
                    displayNotifications();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error marking notification as read", e);
            }
        }).start();
    }

    private void deleteNotification(Notification notif) {
        new Thread(() -> {
            try {
                notificationService.deleteNotification(notif.getId());

                Platform.runLater(() -> {
                    notifications.remove(notif);
                    updateStatistics();
                    displayNotifications();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting notification", e);
                Platform.runLater(() -> showError("Failed to delete notification."));
            }
        }).start();
    }

    private void handleNotificationAction(Notification notif) {
        // Navigate based on notification type
        String type = notif.getType() != null ? notif.getType().toLowerCase() : "";

        try {
            String viewPath = null;

            if (type.contains("booking") || type.contains("ticket")) {
                viewPath = "/com/esprit/views/tickets/TicketHistory.fxml";
            } else if (type.contains("promo")) {
                viewPath = "/com/esprit/views/shop/CheckoutPage.fxml";
            } else if (type.contains("release")) {
                viewPath = "/com/esprit/views/FilmUser.fxml";
            }

            if (viewPath != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
                Parent root = loader.load();
                Stage stage = (Stage) notificationsContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating from notification", e);
        }
    }

    @FXML
    private void handleMarkAllRead() {
        new Thread(() -> {
            try {
                notificationService.markAllAsRead(currentUser.getId());

                Platform.runLater(() -> {
                    notifications.forEach(n -> n.setRead(true));
                    updateStatistics();
                    displayNotifications();
                    showSuccess("All notifications marked as read.");
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error marking all as read", e);
                Platform.runLater(() -> showError("Failed to mark all as read."));
            }
        }).start();
    }

    @FXML
    private void markAllRead() {
        handleMarkAllRead();
    }

    @FXML
    private void openSettings() {
        // Navigate to notification settings
        LOGGER.info("Opening notification settings");
    }

    @FXML
    private void clearAll() {
        handleClearAll();
    }

    @FXML
    private void filterAll() {
        if (filterCombo != null) {
            filterCombo.setValue("All Types");
            filterNotifications();
        }
    }

    @FXML
    private void filterBookings() {
        if (filterCombo != null) {
            filterCombo.setValue("Bookings");
            filterNotifications();
        }
    }

    @FXML
    private void filterSocial() {
        if (filterCombo != null) {
            filterCombo.setValue("Friends");
            filterNotifications();
        }
    }

    @FXML
    private void filterPromotions() {
        if (filterCombo != null) {
            filterCombo.setValue("Promotions");
            filterNotifications();
        }
    }

    @FXML
    private void filterSystem() {
        if (filterCombo != null) {
            filterCombo.setValue("System");
            filterNotifications();
        }
    }

    @FXML
    private void handleClearAll() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear Notifications");
        confirm.setHeaderText("Delete all notifications?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        notificationService.clearAllNotifications(currentUser.getId());

                        Platform.runLater(() -> {
                            notifications.clear();
                            updateStatistics();
                            displayNotifications();
                            showSuccess("All notifications cleared.");
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error clearing notifications", e);
                        Platform.runLater(() -> showError("Failed to clear notifications."));
                    }
                }).start();
            }
        });
    }

    @FXML
    private void handleSavePreferences() {
        showSuccess("Notification preferences saved!");
    }

    @FXML
    private void closeDetail() {
        LOGGER.info("Close detail");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) notificationsContainer.getScene().getWindow();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

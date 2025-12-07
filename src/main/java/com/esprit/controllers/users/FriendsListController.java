package com.esprit.controllers.users;

import com.esprit.models.users.User;
import com.esprit.services.users.FriendService;
import com.esprit.services.users.UserService;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing user's friends list and friend requests.
 */
public class FriendsListController {

    private static final Logger LOGGER = Logger.getLogger(FriendsListController.class.getName());
    private final FriendService friendService;
    private final UserService userService;
    @FXML
    private VBox friendsContainer;
    @FXML
    private VBox friendsList;
    @FXML
    private VBox requestsList;
    @FXML
    private VBox suggestedList;
    @FXML
    private TextField searchField;
    @FXML
    private TabPane friendsTabPane;
    @FXML
    private Tab friendsTab;
    @FXML
    private Tab requestsTab;
    @FXML
    private Tab suggestedTab;
    @FXML
    private Label friendsCountLabel;
    @FXML
    private Label requestsCountLabel;
    @FXML
    private Label onlineCountLabel;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox emptyStateBox;
    private ObservableList<User> friends;
    private ObservableList<User> pendingRequests;
    private ObservableList<User> suggestions;
    private User currentUser;

    public FriendsListController() {
        this.friendService = new FriendService();
        this.userService = new UserService();
        this.friends = FXCollections.observableArrayList();
        this.pendingRequests = FXCollections.observableArrayList();
        this.suggestions = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing FriendsListController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupControls();
        loadFriends();
        loadPendingRequests();
        loadSuggestions();
    }

    private void setupControls() {
        sortCombo.getItems().addAll("Name A-Z", "Name Z-A", "Recently Added", "Online First");
        sortCombo.setValue("Name A-Z");
        sortCombo.setOnAction(e -> sortFriends());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterFriends(newVal));
        }

        if (friendsTabPane != null) {
            friendsTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab == requestsTab) {
                    loadPendingRequests();
                } else if (newTab == suggestedTab) {
                    loadSuggestions();
                }
            });
        }
    }

    private void loadFriends() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<User> friendList = friendService.getFriends(currentUser.getId());

                Platform.runLater(() -> {
                    friends.setAll(friendList);
                    displayFriends();
                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading friends", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load friends.");
                });
            }
        }).start();
    }

    private void loadPendingRequests() {
        new Thread(() -> {
            try {
                List<User> requests = friendService.getPendingRequests(currentUser.getId());

                Platform.runLater(() -> {
                    pendingRequests.setAll(requests);
                    displayRequests();
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading friend requests", e);
            }
        }).start();
    }

    private void loadSuggestions() {
        new Thread(() -> {
            try {
                List<User> suggested = friendService.getSuggestedFriends(currentUser.getId());

                Platform.runLater(() -> {
                    suggestions.setAll(suggested);
                    displaySuggestions();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading suggestions", e);
            }
        }).start();
    }

    private void displayFriends() {
        friendsList.getChildren().clear();

        if (friends.isEmpty()) {
            Label empty = new Label("No friends yet. Start connecting!");
            empty.getStyleClass().add("empty-state-label");
            friendsList.getChildren().add(empty);
        } else {
            for (User friend : friends) {
                friendsList.getChildren().add(createFriendCard(friend));
            }
        }
    }

    private void displayRequests() {
        requestsList.getChildren().clear();

        if (pendingRequests.isEmpty()) {
            Label empty = new Label("No pending friend requests.");
            empty.getStyleClass().add("empty-state-label");
            requestsList.getChildren().add(empty);
        } else {
            for (User request : pendingRequests) {
                requestsList.getChildren().add(createRequestCard(request));
            }
        }
    }

    private void displaySuggestions() {
        suggestedList.getChildren().clear();

        if (suggestions.isEmpty()) {
            Label empty = new Label("No suggestions available.");
            empty.getStyleClass().add("empty-state-label");
            suggestedList.getChildren().add(empty);
        } else {
            for (User suggested : suggestions) {
                suggestedList.getChildren().add(createSuggestionCard(suggested));
            }
        }
    }

    private HBox createFriendCard(User friend) {
        HBox card = new HBox(12);
        card.getStyleClass().add("friend-card");

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.getStyleClass().add("friend-avatar");

        if (friend.getProfilePicture() != null && !friend.getProfilePicture().isEmpty()) {
            try {
                avatar.setImage(new Image(friend.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load avatar", e);
            }
        }

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(friend.getFirstName() + " " + friend.getLastName());
        nameLabel.getStyleClass().add("friend-name");

        Label usernameLabel = new Label("@" + friend.getUsername());
        usernameLabel.getStyleClass().add("friend-username");

        // Online status
        HBox statusBox = new HBox(6);
        Label statusDot = new Label("●");
        statusDot.getStyleClass().add("status-dot-offline");
        Label statusLabel = new Label("Offline");
        statusLabel.getStyleClass().add("status-label");
        statusBox.getChildren().addAll(statusDot, statusLabel);

        info.getChildren().addAll(nameLabel, usernameLabel, statusBox);

        // Actions
        HBox actions = new HBox(8);

        Button messageBtn = new Button("Message");
        messageBtn.getStyleClass().add("message-btn");
        messageBtn.setOnAction(e -> openChat(friend));

        Button profileBtn = new Button("Profile");
        profileBtn.getStyleClass().add("profile-btn");
        profileBtn.setOnAction(e -> viewProfile(friend));

        MenuButton moreBtn = new MenuButton("⋮");
        moreBtn.getStyleClass().add("more-btn");

        MenuItem unfriendItem = new MenuItem("Unfriend");
        unfriendItem.setOnAction(e -> unfriend(friend));

        MenuItem blockItem = new MenuItem("Block");
        blockItem.setOnAction(e -> blockUser(friend));

        moreBtn.getItems().addAll(unfriendItem, blockItem);

        actions.getChildren().addAll(messageBtn, profileBtn, moreBtn);

        card.getChildren().addAll(avatar, info, actions);

        return card;
    }

    private HBox createRequestCard(User request) {
        HBox card = new HBox(12);
        card.getStyleClass().add("request-card");

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.getStyleClass().add("friend-avatar");

        if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            try {
                avatar.setImage(new Image(request.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load avatar", e);
            }
        }

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(request.getFirstName() + " " + request.getLastName());
        nameLabel.getStyleClass().add("friend-name");

        Label usernameLabel = new Label("@" + request.getUsername());
        usernameLabel.getStyleClass().add("friend-username");

        info.getChildren().addAll(nameLabel, usernameLabel);

        // Actions
        HBox actions = new HBox(8);

        Button acceptBtn = new Button("Accept");
        acceptBtn.getStyleClass().add("accept-btn");
        acceptBtn.setOnAction(e -> acceptRequest(request));

        Button declineBtn = new Button("Decline");
        declineBtn.getStyleClass().add("decline-btn");
        declineBtn.setOnAction(e -> declineRequest(request));

        actions.getChildren().addAll(acceptBtn, declineBtn);

        card.getChildren().addAll(avatar, info, actions);

        return card;
    }

    private HBox createSuggestionCard(User suggested) {
        HBox card = new HBox(12);
        card.getStyleClass().add("suggestion-card");

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.getStyleClass().add("friend-avatar");

        if (suggested.getProfilePicture() != null && !suggested.getProfilePicture().isEmpty()) {
            try {
                avatar.setImage(new Image(suggested.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load avatar", e);
            }
        }

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(suggested.getFirstName() + " " + suggested.getLastName());
        nameLabel.getStyleClass().add("friend-name");

        Label usernameLabel = new Label("@" + suggested.getUsername());
        usernameLabel.getStyleClass().add("friend-username");

        Label mutualLabel = new Label("3 mutual friends");
        mutualLabel.getStyleClass().add("mutual-friends");

        info.getChildren().addAll(nameLabel, usernameLabel, mutualLabel);

        // Actions
        Button addBtn = new Button("Add Friend");
        addBtn.getStyleClass().add("add-friend-btn");
        addBtn.setOnAction(e -> sendFriendRequest(suggested));

        card.getChildren().addAll(avatar, info, addBtn);

        return card;
    }

    private void filterFriends(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            displayFriends();
            return;
        }

        String search = searchText.toLowerCase();
        friendsList.getChildren().clear();

        for (User friend : friends) {
            String fullName = (friend.getFirstName() + " " + friend.getLastName()).toLowerCase();
            String username = friend.getUsername().toLowerCase();

            if (fullName.contains(search) || username.contains(search)) {
                friendsList.getChildren().add(createFriendCard(friend));
            }
        }
    }

    private void sortFriends() {
        String sortBy = sortCombo.getValue();

        switch (sortBy) {
            case "Name A-Z":
                FXCollections.sort(friends, (u1, u2) ->
                    (u1.getFirstName() + " " + u1.getLastName())
                        .compareToIgnoreCase(u2.getFirstName() + " " + u2.getLastName()));
                break;
            case "Name Z-A":
                FXCollections.sort(friends, (u1, u2) ->
                    (u2.getFirstName() + " " + u2.getLastName())
                        .compareToIgnoreCase(u1.getFirstName() + " " + u1.getLastName()));
                break;
            default:
                break;
        }

        displayFriends();
    }

    private void updateStatistics() {
        if (friendsCountLabel != null) friendsCountLabel.setText(String.valueOf(friends.size()));
        if (requestsCountLabel != null) requestsCountLabel.setText(String.valueOf(pendingRequests.size()));
        if (onlineCountLabel != null) onlineCountLabel.setText("0"); // TODO: Implement online status
    }

    private void acceptRequest(User request) {
        new Thread(() -> {
            try {
                friendService.acceptFriendRequest(currentUser.getId(), request.getId());

                Platform.runLater(() -> {
                    pendingRequests.remove(request);
                    friends.add(request);
                    displayRequests();
                    displayFriends();
                    updateStatistics();
                    showSuccess("Friend request accepted!");
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error accepting friend request", e);
                Platform.runLater(() -> showError("Failed to accept request."));
            }
        }).start();
    }

    private void declineRequest(User request) {
        new Thread(() -> {
            try {
                friendService.declineFriendRequest(currentUser.getId(), request.getId());

                Platform.runLater(() -> {
                    pendingRequests.remove(request);
                    displayRequests();
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error declining friend request", e);
                Platform.runLater(() -> showError("Failed to decline request."));
            }
        }).start();
    }

    private void sendFriendRequest(User user) {
        new Thread(() -> {
            try {
                friendService.sendFriendRequest(currentUser.getId(), user.getId());

                Platform.runLater(() -> {
                    suggestions.remove(user);
                    displaySuggestions();
                    showSuccess("Friend request sent!");
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending friend request", e);
                Platform.runLater(() -> showError("Failed to send request."));
            }
        }).start();
    }

    private void unfriend(User friend) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unfriend");
        confirm.setHeaderText("Unfriend " + friend.getFirstName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        friendService.unfriend(currentUser.getId(), friend.getId());

                        Platform.runLater(() -> {
                            friends.remove(friend);
                            displayFriends();
                            updateStatistics();
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error unfriending", e);
                        Platform.runLater(() -> showError("Failed to unfriend."));
                    }
                }).start();
            }
        });
    }

    private void blockUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Block User");
        confirm.setHeaderText("Block " + user.getFirstName() + "?");
        confirm.setContentText("They won't be able to see your profile or message you.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        friendService.blockUser(currentUser.getId(), user.getId());

                        Platform.runLater(() -> {
                            friends.remove(user);
                            displayFriends();
                            updateStatistics();
                            showSuccess("User blocked.");
                        });
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error blocking user", e);
                        Platform.runLater(() -> showError("Failed to block user."));
                    }
                }).start();
            }
        });
    }

    private void openChat(User friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/social/ChatMessages.fxml"));
            Parent root = loader.load();

            ChatMessagesController controller = loader.getController();
            controller.setRecipient(friend);

            Stage stage = (Stage) friendsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/social.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening chat", e);
            showError("Could not open chat.");
        }
    }

    private void viewProfile(User user) {
        // TODO: Navigate to user profile view
        showInfo("Profile view coming soon!");
    }

    @FXML
    private void handleFindFriends() {
        friendsTabPane.getSelectionModel().select(suggestedTab);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ClientDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) friendsContainer.getScene().getWindow();
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

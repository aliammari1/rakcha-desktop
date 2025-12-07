package com.esprit.controllers.users;

import com.esprit.models.users.ChatMessage;
import com.esprit.models.users.User;
import com.esprit.models.users.Message;
import com.esprit.services.users.MessageService;
import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for in-app messaging between users.
 */
public class ChatMessagesController {

    private static final Logger LOGGER = Logger.getLogger(ChatMessagesController.class.getName());
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final MessageService messageService;
    private final UserService userService;
    @FXML
    private VBox chatContainer;
    @FXML
    private VBox conversationsList;
    @FXML
    private VBox messagesList;
    @FXML
    private ScrollPane messagesScrollPane;
    @FXML
    private TextField searchConversationsField;
    @FXML
    private TextArea messageInput;
    @FXML
    private ImageView recipientAvatar;
    @FXML
    private Label recipientNameLabel;
    @FXML
    private Label recipientStatusLabel;
    @FXML
    private Label typingIndicatorLabel;
    @FXML
    private Button sendButton;
    @FXML
    private Button attachButton;
    @FXML
    private Button emojiButton;
    @FXML
    private VBox noConversationBox;
    @FXML
    private ProgressIndicator loadingIndicator;
    private ObservableList<User> conversations;
    private ObservableList<Message> messages;
    private User currentUser;
    private User selectedRecipient;
    private Timer refreshTimer;

    public ChatMessagesController() {
        this.messageService = new MessageService();
        this.userService = new UserService();
        this.conversations = FXCollections.observableArrayList();
        this.messages = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing ChatMessagesController");

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("No user logged in");
            return;
        }

        setupControls();
        loadConversations();
        startMessageRefresh();
    }

    private void setupControls() {
        // Enter to send
        if (messageInput != null) {
            messageInput.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                    event.consume();
                    handleSendMessage();
                }
            });
        }

        // Search conversations
        if (searchConversationsField != null) {
            searchConversationsField.textProperty().addListener((obs, oldVal, newVal) ->
                filterConversations(newVal));
        }

        // Initially hide chat area
        if (noConversationBox != null) {
            noConversationBox.setVisible(true);
        }
        if (messagesList != null) {
            messagesList.setVisible(false);
        }
    }

    /**
     * Sets the recipient to chat with (from external navigation).
     */
    public void setRecipient(User recipient) {
        this.selectedRecipient = recipient;

        // Check if conversation exists, if not add to list
        if (!conversations.contains(recipient)) {
            conversations.add(0, recipient);
            displayConversations();
        }

        selectConversation(recipient);
    }

    private void loadConversations() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<User> convList = messageService.getConversations(currentUser.getId());

                Platform.runLater(() -> {
                    conversations.setAll(convList);
                    displayConversations();
                    showLoading(false);

                    // If we have a pre-selected recipient, load their messages
                    if (selectedRecipient != null) {
                        loadMessages(selectedRecipient);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading conversations", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load conversations.");
                });
            }
        }).start();
    }

    private void displayConversations() {
        conversationsList.getChildren().clear();

        if (conversations.isEmpty()) {
            Label empty = new Label("No conversations yet");
            empty.getStyleClass().add("empty-state-label");
            conversationsList.getChildren().add(empty);
        } else {
            for (User user : conversations) {
                conversationsList.getChildren().add(createConversationItem(user));
            }
        }
    }

    private HBox createConversationItem(User user) {
        HBox item = new HBox(12);
        item.getStyleClass().add("conversation-item");

        if (selectedRecipient != null && selectedRecipient.getId() == user.getId()) {
            item.getStyleClass().add("selected");
        }

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitWidth(45);
        avatar.setFitHeight(45);
        avatar.getStyleClass().add("conversation-avatar");

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                avatar.setImage(new Image(user.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load avatar", e);
            }
        }

        // Online indicator
        StackPane avatarContainer = new StackPane();
        avatarContainer.getChildren().add(avatar);

        Label onlineIndicator = new Label("●");
        onlineIndicator.getStyleClass().add("online-indicator-small");
        StackPane.setAlignment(onlineIndicator, Pos.BOTTOM_RIGHT);
        avatarContainer.getChildren().add(onlineIndicator);

        // Info
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.getStyleClass().add("conversation-name");

        Label lastMessageLabel = new Label("Click to start chatting");
        lastMessageLabel.getStyleClass().add("last-message");

        info.getChildren().addAll(nameLabel, lastMessageLabel);

        // Time
        Label timeLabel = new Label("");
        timeLabel.getStyleClass().add("conversation-time");

        item.getChildren().addAll(avatarContainer, info, timeLabel);

        item.setOnMouseClicked(e -> selectConversation(user));

        return item;
    }

    private void selectConversation(User user) {
        selectedRecipient = user;

        // Update UI
        if (noConversationBox != null) noConversationBox.setVisible(false);
        if (messagesList != null) messagesList.setVisible(true);

        // Update header
        if (recipientNameLabel != null) {
            recipientNameLabel.setText(user.getFirstName() + " " + user.getLastName());
        }
        if (recipientStatusLabel != null) {
            recipientStatusLabel.setText("Online");
        }
        if (recipientAvatar != null && user.getProfilePicture() != null) {
            try {
                recipientAvatar.setImage(new Image(user.getProfilePicture(), true));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load avatar", e);
            }
        }

        // Refresh conversation list to show selection
        displayConversations();

        // Load messages
        loadMessages(user);
    }

    private void loadMessages(User recipient) {
        new Thread(() -> {
            try {
                List<Message> msgList = messageService.getMessages(currentUser.getId(), recipient.getId());

                Platform.runLater(() -> {
                    messages.setAll(msgList);
                    displayMessages();

                    // Mark messages as read
                    try {
                        messageService.markAsRead(currentUser.getId(), recipient.getId());
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error marking messages as read", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading messages", e);
            }
        }).start();
    }

    private void displayMessages() {
        messagesList.getChildren().clear();

        LocalDateTime lastDate = null;

        for (Message message : messages) {
            // Date separator
            LocalDateTime msgDate = message.getCreatedAt().truncatedTo(ChronoUnit.DAYS);
            if (lastDate == null || !lastDate.equals(msgDate)) {
                Label dateSeparator = new Label(formatDateSeparator(message.getCreatedAt()));
                dateSeparator.getStyleClass().add("date-separator");
                HBox dateBox = new HBox(dateSeparator);
                dateBox.setAlignment(Pos.CENTER);
                messagesList.getChildren().add(dateBox);
                lastDate = msgDate;
            }

            messagesList.getChildren().add(createMessageBubble(message));
        }

        // Scroll to bottom
        Platform.runLater(() -> {
            if (messagesScrollPane != null) {
                messagesScrollPane.setVvalue(1.0);
            }
        });
    }

    private HBox createMessageBubble(Message message) {
        HBox container = new HBox();
        container.getStyleClass().add("message-container");

        boolean isSent = message.getSenderId() == currentUser.getId();

        VBox bubble = new VBox(4);
        bubble.getStyleClass().add("message-bubble");
        bubble.getStyleClass().add(isSent ? "sent" : "received");
        bubble.setMaxWidth(400);

        Label contentLabel = new Label(message.getContent());
        contentLabel.getStyleClass().add("message-content");
        contentLabel.setWrapText(true);

        Label timeLabel = new Label(message.getCreatedAt().format(TIME_FORMAT));
        timeLabel.getStyleClass().add("message-time");

        // Read status for sent messages
        if (isSent) {
            HBox statusBox = new HBox(4);
            statusBox.setAlignment(Pos.CENTER_RIGHT);

            Label checkLabel = new Label(message.isRead() ? "✓✓" : "✓");
            checkLabel.getStyleClass().add(message.isRead() ? "read-status" : "sent-status");

            statusBox.getChildren().addAll(timeLabel, checkLabel);
            bubble.getChildren().addAll(contentLabel, statusBox);
        } else {
            bubble.getChildren().addAll(contentLabel, timeLabel);
        }

        if (isSent) {
            container.setAlignment(Pos.CENTER_RIGHT);
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
        }

        container.getChildren().add(bubble);

        return container;
    }

    private String formatDateSeparator(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime messageDate = dateTime.truncatedTo(ChronoUnit.DAYS);

        if (messageDate.equals(today)) {
            return "Today";
        } else if (messageDate.equals(yesterday)) {
            return "Yesterday";
        } else {
            return dateTime.format(DATE_FORMAT);
        }
    }

    private void filterConversations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            displayConversations();
            return;
        }

        String search = searchText.toLowerCase();
        conversationsList.getChildren().clear();

        for (User user : conversations) {
            String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();

            if (fullName.contains(search)) {
                conversationsList.getChildren().add(createConversationItem(user));
            }
        }
    }

    private void startMessageRefresh() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (selectedRecipient != null) {
                    Platform.runLater(() -> loadMessages(selectedRecipient));
                }
            }
        }, 5000, 5000); // Refresh every 5 seconds
    }

    @FXML
    private void handleSendMessage() {
        if (selectedRecipient == null || messageInput == null) return;

        String content = messageInput.getText().trim();
        if (content.isEmpty()) return;

        messageInput.clear();

        new Thread(() -> {
            try {
                Message message = new Message();
                message.setSenderId(currentUser.getId());
                message.setRecipientId(selectedRecipient.getId());
                message.setContent(content);
                message.setCreatedAt(LocalDateTime.now());

                messageService.sendMessage(currentUser.getId(), selectedRecipient.getId(), content);

                Platform.runLater(() -> {
                    messages.add(message);
                    displayMessages();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending message", e);
                Platform.runLater(() -> showError("Failed to send message."));
            }
        }).start();
    }

    @FXML
    private void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(chatContainer.getScene().getWindow());

        if (file != null) {
            // TODO: Upload and send file
            showInfo("File attachment coming soon!");
        }
    }

    @FXML
    private void handleEmoji() {
        // TODO: Show emoji picker
        showInfo("Emoji picker coming soon!");
    }

    @FXML
    private void handleNewConversation() {
        // TODO: Show user search dialog
        showInfo("New conversation dialog coming soon!");
    }

    @FXML
    private void handleViewProfile() {
        if (selectedRecipient != null) {
            // TODO: Navigate to user profile
            showInfo("Profile view coming soon!");
        }
    }

    @FXML
    private void handleBack() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/social/FriendsList.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) chatContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/social.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
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

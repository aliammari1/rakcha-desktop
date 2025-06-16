package com.esprit.utils.ui;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import animatefx.animation.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Utility class for modern UI styling and animations. Provides consistent
 * styling across the application.
 */
public class UIUtils {

    // Color Palette
    public static final Color PRIMARY_COLOR = Color.web("#3498DB");
    public static final Color SECONDARY_COLOR = Color.web("#2ECC71");
    public static final Color ACCENT_COLOR = Color.web("#E74C3C");
    public static final Color BACKGROUND_COLOR = Color.web("#F8F9FA");
    public static final Color CARD_COLOR = Color.web("#FFFFFF");
    public static final Color TEXT_COLOR = Color.web("#2C3E50");
    public static final Color MUTED_COLOR = Color.web("#95A5A6");

    // Button Styles
    public static final String BUTTON_PRIMARY = "btn-primary";
    public static final String BUTTON_SECONDARY = "btn-secondary";
    public static final String BUTTON_SUCCESS = "btn-success";
    public static final String BUTTON_DANGER = "btn-danger";
    public static final String BUTTON_WARNING = "btn-warning";
    public static final String BUTTON_INFO = "btn-info";

    // Card Styles
    public static final String CARD = "card";
    public static final String CARD_ELEVATED = "card-elevated";

    /**
     * Create a modern styled button with icon
     */
    public static Button createButton(String text, FontAwesomeSolid icon, String styleClass) {
        Button button = new Button(text);

        if (icon != null) {
            FontIcon iconNode = new FontIcon(icon);
            iconNode.setIconSize(16);
            button.setGraphic(iconNode);
        }

        button.getStyleClass().addAll("btn", styleClass);
        addHoverEffect(button);
        return button;
    }

    /**
     * Create a primary button
     */
    public static Button createPrimaryButton(String text, FontAwesomeSolid icon) {
        return createButton(text, icon, BUTTON_PRIMARY);
    }

    /**
     * Create a secondary button
     */
    public static Button createSecondaryButton(String text, FontAwesomeSolid icon) {
        return createButton(text, icon, BUTTON_SECONDARY);
    }

    /**
     * Create a success button
     */
    public static Button createSuccessButton(String text, FontAwesomeSolid icon) {
        return createButton(text, icon, BUTTON_SUCCESS);
    }

    /**
     * Create a danger button
     */
    public static Button createDangerButton(String text, FontAwesomeSolid icon) {
        return createButton(text, icon, BUTTON_DANGER);
    }

    /**
     * Create a modern card container
     */
    public static VBox createCard() {
        VBox card = new VBox();
        card.getStyleClass().add(CARD);
        card.setPadding(new Insets(20));
        card.setSpacing(15);

        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        shadow.setRadius(8);
        card.setEffect(shadow);

        return card;
    }

    /**
     * Create an elevated card container
     */
    public static VBox createElevatedCard() {
        VBox card = createCard();
        card.getStyleClass().add(CARD_ELEVATED);

        // Enhanced shadow for elevated cards
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setRadius(12);
        card.setEffect(shadow);

        return card;
    }

    /**
     * Create a modern text field with floating label effect
     */
    public static VBox createFloatingTextField(String labelText) {
        VBox container = new VBox();
        container.setSpacing(5);

        Label label = new Label(labelText);
        label.getStyleClass().add("floating-label");

        TextField textField = new TextField();
        textField.getStyleClass().add("modern-textfield");

        container.getChildren().addAll(label, textField);
        return container;
    }

    /**
     * Create a circular image view
     */
    public static ImageView createCircularImageView(String imagePath, double radius) {
        ImageView imageView = new ImageView();

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Image image = new Image(imagePath);
                imageView.setImage(image);
            } catch (Exception e) {
                // Use placeholder if image loading fails
                imageView.setImage(createPlaceholderImage(radius * 2));
            }
        } else {
            imageView.setImage(createPlaceholderImage(radius * 2));
        }

        imageView.setFitWidth(radius * 2);
        imageView.setFitHeight(radius * 2);
        imageView.setPreserveRatio(true);

        // Create circular clip
        Circle clip = new Circle(radius, radius, radius);
        imageView.setClip(clip);

        return imageView;
    }

    /**
     * Create a placeholder image for avatars
     */
    public static Image createPlaceholderImage(double size) {
        // Create a simple gradient placeholder
        return new Image(UIUtils.class.getResourceAsStream("/images/placeholder-avatar.png"));
    }

    /**
     * Add hover effect to a node
     */
    public static void addHoverEffect(Node node) {
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.05);
            node.setScaleY(1.05);
            new BounceIn(node).setSpeed(2.0).play();
        });

        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    /**
     * Add click animation to a node
     */
    public static void addClickAnimation(Node node) {
        node.setOnMousePressed(e -> {
            new Pulse(node).setSpeed(3.0).play();
        });
    }

    /**
     * Animate node entrance
     */
    public static void animateIn(Node node, AnimationType type) {
        Platform.runLater(() -> {
            switch (type) {
            case FADE_IN -> new FadeIn(node).play();
            case SLIDE_IN_LEFT -> new SlideInLeft(node).play();
            case SLIDE_IN_RIGHT -> new SlideInRight(node).play();
            case SLIDE_IN_UP -> new SlideInUp(node).play();
            case SLIDE_IN_DOWN -> new SlideInDown(node).play();
            case BOUNCE_IN -> new BounceIn(node).play();
            case ZOOM_IN -> new ZoomIn(node).play();
            default -> new FadeIn(node).play();
            }
        });
    }

    /**
     * Animate node exit
     */
    public static void animateOut(Node node, AnimationType type, Runnable onFinished) {
        Platform.runLater(() -> {
            AnimationFX animation = switch (type) {
            case FADE_OUT -> new FadeOut(node);
            case SLIDE_OUT_LEFT -> new SlideOutLeft(node);
            case SLIDE_OUT_RIGHT -> new SlideOutRight(node);
            case SLIDE_OUT_UP -> new SlideOutUp(node);
            case SLIDE_OUT_DOWN -> new SlideOutDown(node);
            case BOUNCE_OUT -> new BounceOut(node);
            case ZOOM_OUT -> new ZoomOut(node);
            default -> new FadeOut(node);
            };

            animation.setOnFinished(e -> {
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            animation.play();
        });
    }

    /**
     * Create a loading indicator
     */
    public static ProgressIndicator createLoadingIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.getStyleClass().add("modern-progress");
        indicator.setProgress(-1); // Indeterminate progress
        return indicator;
    }

    /**
     * Create a modern alert dialog
     */
    public static Alert createModernAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Add custom styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("modern-dialog");

        return alert;
    }

    /**
     * Create a modern confirmation dialog
     */
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = createModernAlert(Alert.AlertType.CONFIRMATION, title, message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Show a success notification
     */
    public static void showSuccessNotification(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    /**
     * Show an error notification
     */
    public static void showErrorNotification(String message) {
        showNotification(message, NotificationType.ERROR);
    }

    /**
     * Show an info notification
     */
    public static void showInfoNotification(String message) {
        showNotification(message, NotificationType.INFO);
    }

    /**
     * Show a warning notification
     */
    public static void showWarningNotification(String message) {
        showNotification(message, NotificationType.WARNING);
    }

    /**
     * Generic notification method
     */
    private static void showNotification(String message, NotificationType type) {
        Platform.runLater(() -> {
            // Create notification popup
            Label notificationLabel = new Label(message);
            VBox notification = new VBox(notificationLabel);
            notification.getStyleClass().addAll("notification", "notification-" + type.name().toLowerCase());
            notification.setPadding(new Insets(15));
            notification.setAlignment(Pos.CENTER);

            // Add to scene and animate
            // Note: Implementation depends on having a root container to add notifications
            // to
            animateIn(notification, AnimationType.SLIDE_IN_DOWN);

            // Auto-hide after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                animateOut(notification, AnimationType.FADE_OUT, () -> {
                    // Remove from parent
                    if (notification.getParent() instanceof Pane parent) {
                        parent.getChildren().remove(notification);
                    }
                });
            }));
            timeline.play();
        });
    }

    /**
     * Apply modern dark theme
     */
    public static void applyDarkTheme(Scene scene) {
        scene.getStylesheets().add(UIUtils.class.getResource("/styles/dark-theme.css").toExternalForm());
    }

    /**
     * Apply modern light theme
     */
    public static void applyLightTheme(Scene scene) {
        scene.getStylesheets().add(UIUtils.class.getResource("/styles/light-theme.css").toExternalForm());
    }

    /**
     * Create a responsive grid pane
     */
    public static GridPane createResponsiveGrid(int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Set column constraints for responsiveness
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / columns);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }

        return grid;
    }

    /**
     * Animation types enumeration
     */
    public enum AnimationType {
        FADE_IN, FADE_OUT, SLIDE_IN_LEFT, SLIDE_IN_RIGHT, SLIDE_IN_UP, SLIDE_IN_DOWN, SLIDE_OUT_LEFT, SLIDE_OUT_RIGHT, SLIDE_OUT_UP, SLIDE_OUT_DOWN, BOUNCE_IN, BOUNCE_OUT, ZOOM_IN, ZOOM_OUT
    }

    /**
     * Notification types enumeration
     */
    public enum NotificationType {
        SUCCESS, ERROR, WARNING, INFO
    }
}

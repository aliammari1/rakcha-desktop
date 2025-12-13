package com.esprit.utils;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

import java.util.Stack;

/**
 * Modern slide-over navigation utility for detail screens.
 * Provides macOS-style slide-in panels with smooth animations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 */
@Log4j2
public class SlideOverNavigator {

    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double OVERLAY_OPACITY = 0.5;
    private static final double PANEL_WIDTH_RATIO = 0.65; // 65% of parent width

    private static final Stack<SlideOverState> navigationStack = new Stack<>();
    private static Region currentOverlay;

    private SlideOverNavigator() {
        // Utility class
    }

    /**
     * Slides in a panel from the right side of the parent container.
     *
     * @param panel  the panel content to display
     * @param parent the parent StackPane to add the panel to
     */
    public static void slideIn(Node panel, StackPane parent) {
        slideIn(panel, parent, null);
    }

    /**
     * Slides in a panel from the right side with a callback on close.
     *
     * @param panel   the panel content to display
     * @param parent  the parent StackPane to add the panel to
     * @param onClose callback to run when the panel is closed
     */
    public static void slideIn(Node panel, StackPane parent, Runnable onClose) {
        if (panel == null || parent == null) {
            log.warn("Cannot slide in null panel or parent");
            return;
        }

        // Create dimmed overlay
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, " + OVERLAY_OPACITY + ");");
        overlay.setOpacity(0);
        overlay.setOnMouseClicked(e -> slideOut(onClose));
        currentOverlay = overlay;

        // Configure panel
        double panelWidth = parent.getWidth() * PANEL_WIDTH_RATIO;
        if (panel instanceof Region) {
            ((Region) panel).setPrefWidth(panelWidth);
            ((Region) panel).setMaxWidth(panelWidth);
        }

        // Position panel off-screen to the right
        panel.setTranslateX(parent.getWidth());

        // Style the panel with glassmorphism
        panel.getStyleClass().add("slide-panel");

        // Add to parent
        parent.getChildren().addAll(currentOverlay, panel);
        StackPane.setAlignment(panel, javafx.geometry.Pos.CENTER_RIGHT);

        // Push state to navigation stack
        navigationStack.push(new SlideOverState(panel, parent, onClose));

        // Animate in
        FadeTransition fadeIn = new FadeTransition(ANIMATION_DURATION, currentOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(ANIMATION_DURATION, panel);
        slideIn.setFromX(parent.getWidth());
        slideIn.setToX(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fadeIn, slideIn);
        parallel.play();

        // Setup keyboard shortcut
        panel.setFocusTraversable(true);
        panel.requestFocus();
        panel.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                slideOut(onClose);
                e.consume();
            }
        });

        log.debug("Slide-over panel opened. Navigation stack size: {}", navigationStack.size());
    }

    /**
     * Slides out the current panel with animation.
     */
    public static void slideOut() {
        slideOut(null);
    }

    /**
     * Slides out the current panel with animation and runs callback.
     *
     * @param onComplete callback to run after slide-out completes
     */
    public static void slideOut(Runnable onComplete) {
        if (navigationStack.isEmpty()) {
            log.debug("No panels to slide out");
            return;
        }

        SlideOverState state = navigationStack.pop();
        Node panel = state.panel;
        StackPane parent = state.parent;

        // Animate out
        FadeTransition fadeOut = new FadeTransition(ANIMATION_DURATION, currentOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(ANIMATION_DURATION, panel);
        slideOut.setToX(parent.getWidth());
        slideOut.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition parallel = new ParallelTransition(fadeOut, slideOut);
        parallel.setOnFinished(e -> {
            parent.getChildren().removeAll(currentOverlay, panel);
            currentOverlay = null;

            if (state.onClose != null) {
                state.onClose.run();
            }
            if (onComplete != null) {
                onComplete.run();
            }

            log.debug("Slide-over panel closed. Navigation stack size: {}", navigationStack.size());
        });
        parallel.play();
    }

    /**
     * Checks if any slide-over panels are open.
     *
     * @return true if panels are open
     */
    public static boolean hasOpenPanels() {
        return !navigationStack.isEmpty();
    }

    /**
     * Gets the current navigation stack depth.
     *
     * @return number of open panels
     */
    public static int getStackDepth() {
        return navigationStack.size();
    }

    /**
     * Closes all open panels.
     */
    public static void closeAll() {
        while (!navigationStack.isEmpty()) {
            slideOut();
        }
    }

    /**
     * Internal state class for tracking slide-over panels.
     */
    private static class SlideOverState {
        final Node panel;
        final StackPane parent;
        final Runnable onClose;

        SlideOverState(Node panel, StackPane parent, Runnable onClose) {
            this.panel = panel;
            this.parent = parent;
            this.onClose = onClose;
        }
    }
}

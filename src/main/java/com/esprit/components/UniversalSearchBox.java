package com.esprit.components;

import com.esprit.services.search.SearchService;
import com.esprit.services.search.SearchService.EntityType;
import com.esprit.services.search.SearchService.SearchResult;
import com.esprit.services.search.SearchService.UserRole;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * A high-performance, beautiful search component with autocomplete,
 * keyboard navigation, and categorized results.
 * <p>
 * Features:
 * - Real-time search with debouncing
 * - Keyboard navigation (up/down arrows, enter, escape)
 * - Categorized results with icons
 * - Image thumbnails
 * - Loading states
 * - Recent searches
 * - Trending suggestions
 *
 * @author RAKCHA Team
 * @version 1.0.0
 */
public class UniversalSearchBox extends HBox {

    private static final Logger LOGGER = Logger.getLogger(UniversalSearchBox.class.getName());
    private static final long DEBOUNCE_DELAY = 150; // ms
    // Styling constants
    private static final String SEARCH_BOX_STYLE = """
        -fx-background-color: linear-gradient(to right, rgba(30,30,30,0.95), rgba(40,40,40,0.95));
        -fx-background-radius: 25;
        -fx-border-color: rgba(255,255,255,0.1);
        -fx-border-radius: 25;
        -fx-border-width: 1;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
        """;
    private static final String SEARCH_FIELD_STYLE = """
        -fx-background-color: transparent;
        -fx-text-fill: white;
        -fx-prompt-text-fill: rgba(255,255,255,0.5);
        -fx-font-size: 14px;
        -fx-padding: 8 15 8 5;
        """;
    private static final String RESULTS_CONTAINER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, rgba(25,25,35,0.98), rgba(20,20,30,0.98));
        -fx-background-radius: 15;
        -fx-border-color: rgba(255,255,255,0.1);
        -fx-border-radius: 15;
        -fx-border-width: 1;
        -fx-padding: 10;
        """;
    // UI Components
    private final TextField searchField;
    private final Button clearButton;
    private final Button searchButton;
    private final Popup resultsPopup;
    private final VBox resultsContainer;
    private final ProgressIndicator loadingIndicator;
    // Search engine
    private final SearchService searchEngine;
    private UserRole currentRole = UserRole.CLIENT;
    // Debounce timer
    private Timer debounceTimer;
    // State
    private int selectedIndex = -1;
    private List<SearchResult> currentResults;
    private Consumer<SearchResult> onResultSelected;
    private Consumer<String> onSearchSubmit;

    public UniversalSearchBox() {
        this(400);
    }

    public UniversalSearchBox(double prefWidth) {
        super();

        this.searchEngine = SearchService.getInstance();

        // Setup main container
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(0);
        setPrefWidth(prefWidth);
        setMaxWidth(prefWidth);
        setStyle(SEARCH_BOX_STYLE);
        setPadding(new Insets(5, 15, 5, 15));

        // Search icon
        FontIcon searchIcon = new FontIcon("mdi2m-magnify:20");
        searchIcon.setIconColor(Color.web("#888"));

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search films, series, products...");
        searchField.setStyle(SEARCH_FIELD_STYLE);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(18, 18);
        loadingIndicator.setVisible(false);
        loadingIndicator.setStyle("-fx-progress-color: #E50914;");

        // Clear button
        clearButton = new Button();
        clearButton.setGraphic(new FontIcon("mdi2c-close:16"));
        clearButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        clearButton.setVisible(false);
        clearButton.setOnAction(e -> clearSearch());

        // Search button (voice/submit)
        searchButton = new Button();
        FontIcon micIcon = new FontIcon("mdi2m-microphone:18");
        micIcon.setIconColor(Color.web("#888"));
        searchButton.setGraphic(micIcon);
        searchButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Add components
        getChildren().addAll(searchIcon, searchField, loadingIndicator, clearButton, searchButton);

        // Setup results popup
        resultsPopup = new Popup();
        resultsPopup.setAutoHide(true);

        resultsContainer = new VBox(5);
        resultsContainer.setStyle(RESULTS_CONTAINER_STYLE);
        resultsContainer.setMinWidth(prefWidth);
        resultsContainer.setMaxWidth(prefWidth + 100);
        resultsContainer.setMaxHeight(450);

        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(20);
        shadow.setOffsetY(5);
        resultsContainer.setEffect(shadow);

        ScrollPane scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(450);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        resultsPopup.getContent().add(scrollPane);

        // Setup event handlers
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // Text change listener with debounce
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                clearButton.setVisible(true);
                debounceSearch(newVal);
            } else {
                clearButton.setVisible(false);
                hideResults();
            }
        });

        // Focus handling
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && searchField.getText() != null && !searchField.getText().isEmpty()) {
                showResults();
            }
        });

        // Keyboard navigation
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                navigateResults(1);
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                navigateResults(-1);
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                selectCurrentResult();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                hideResults();
                event.consume();
            }
        });

        // Focus effect
        searchField.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (focused) {
                setStyle(SEARCH_BOX_STYLE + "-fx-border-color: rgba(229,9,20,0.5);");
            } else {
                setStyle(SEARCH_BOX_STYLE);
            }
        });
    }

    private void debounceSearch(String query) {
        if (debounceTimer != null) {
            debounceTimer.cancel();
        }

        debounceTimer = new Timer();
        debounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> performSearch(query));
            }
        }, DEBOUNCE_DELAY);
    }

    private void performSearch(String query) {
        if (query == null || query.trim().length() < 2) {
            showSuggestions();
            return;
        }

        loadingIndicator.setVisible(true);

        CompletableFuture.supplyAsync(() ->
            searchEngine.search(query, currentRole, 10)
        ).thenAccept(results -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                currentResults = results;
                displayResults(results, query);
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                showError("Search failed. Please try again.");
            });
            return null;
        });
    }

    private void displayResults(List<SearchResult> results, String query) {
        resultsContainer.getChildren().clear();
        selectedIndex = -1;

        if (results.isEmpty()) {
            showNoResults(query);
            return;
        }

        // Group results by type
        var groupedResults = results.stream()
            .collect(java.util.stream.Collectors.groupingBy(SearchResult::getType));

        boolean first = true;
        for (EntityType type : EntityType.values()) {
            var typeResults = groupedResults.get(type);
            if (typeResults != null && !typeResults.isEmpty()) {
                if (!first) {
                    resultsContainer.getChildren().add(createSeparator());
                }
                first = false;

                // Category header
                resultsContainer.getChildren().add(createCategoryHeader(type));

                // Results
                for (SearchResult result : typeResults) {
                    resultsContainer.getChildren().add(createResultItem(result));
                }
            }
        }

        showResults();
    }

    private HBox createCategoryHeader(EntityType type) {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(5, 10, 2, 10));

        FontIcon icon = new FontIcon(type.icon + ":14");
        icon.setIconColor(Color.web(type.color));

        Label label = new Label(type.name.substring(0, 1).toUpperCase() + type.name.substring(1) + "s");
        label.setStyle("-fx-text-fill: " + type.color + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        header.getChildren().addAll(icon, label);
        return header;
    }

    private HBox createResultItem(SearchResult result) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setStyle("-fx-background-radius: 8; -fx-cursor: hand;");
        item.setUserData(result);

        // Thumbnail or icon
        StackPane thumbnail = new StackPane();
        thumbnail.setPrefSize(45, 45);
        thumbnail.setMinSize(45, 45);

        if (result.getImageUrl() != null && !result.getImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(45);
                imageView.setFitHeight(45);
                imageView.setPreserveRatio(true);

                // Load image async
                CompletableFuture.runAsync(() -> {
                    try {
                        Image image = new Image(result.getImageUrl(), 45, 45, true, true, true);
                        Platform.runLater(() -> imageView.setImage(image));
                    } catch (Exception e) {
                        // Use icon fallback
                    }
                });

                // Clip to rounded rectangle
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(45, 45);
                clip.setArcWidth(8);
                clip.setArcHeight(8);
                imageView.setClip(clip);

                thumbnail.getChildren().add(imageView);
            } catch (Exception e) {
                addIconFallback(thumbnail, result);
            }
        } else {
            addIconFallback(thumbnail, result);
        }

        // Text content
        VBox textContent = new VBox(2);
        HBox.setHgrow(textContent, Priority.ALWAYS);

        Label titleLabel = new Label(result.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        titleLabel.setMaxWidth(250);
        titleLabel.setEllipsisString("...");

        Label subtitleLabel = new Label(result.getSubtitle());
        subtitleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");
        subtitleLabel.setMaxWidth(250);

        textContent.getChildren().addAll(titleLabel, subtitleLabel);

        // Type badge
        Label typeBadge = new Label(result.getType().name.toUpperCase());
        typeBadge.setStyle(
            "-fx-background-color: " + result.getColor() + "33;" +
                "-fx-text-fill: " + result.getColor() + ";" +
                "-fx-font-size: 9px;" +
                "-fx-padding: 2 6;" +
                "-fx-background-radius: 10;"
        );

        item.getChildren().addAll(thumbnail, textContent, typeBadge);

        // Hover effects
        item.setOnMouseEntered(e -> {
            item.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-cursor: hand;");
            selectItem(item);
        });

        item.setOnMouseExited(e -> {
            item.setStyle("-fx-background-radius: 8; -fx-cursor: hand;");
        });

        item.setOnMouseClicked(e -> {
            if (onResultSelected != null) {
                onResultSelected.accept(result);
            }
            hideResults();
        });

        return item;
    }

    private void addIconFallback(StackPane container, SearchResult result) {
        Circle bg = new Circle(22);
        bg.setFill(Color.web(result.getColor() + "33"));

        FontIcon icon = new FontIcon(result.getIcon() + ":20");
        icon.setIconColor(Color.web(result.getColor()));

        container.getChildren().addAll(bg, icon);
    }

    private Region createSeparator() {
        Region separator = new Region();
        separator.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
        separator.setPrefHeight(1);
        separator.setMaxHeight(1);
        VBox.setMargin(separator, new Insets(5, 10, 5, 10));
        return separator;
    }

    private void showNoResults(String query) {
        resultsContainer.getChildren().clear();

        VBox noResults = new VBox(10);
        noResults.setAlignment(Pos.CENTER);
        noResults.setPadding(new Insets(30));

        FontIcon icon = new FontIcon("mdi2m-magnify-close:48");
        icon.setIconColor(Color.web("#666"));

        Label message = new Label("No results for \"" + query + "\"");
        message.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 14px;");

        Label hint = new Label("Try different keywords or check spelling");
        hint.setStyle("-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 12px;");

        noResults.getChildren().addAll(icon, message, hint);
        resultsContainer.getChildren().add(noResults);

        showResults();
    }

    private void showSuggestions() {
        resultsContainer.getChildren().clear();

        // Get suggestions
        List<String> suggestions = searchEngine.getSuggestions(
            searchField.getText(), currentRole, 8
        );

        if (suggestions.isEmpty()) {
            hideResults();
            return;
        }

        // Header
        Label header = new Label(searchField.getText().isEmpty() ? "🔥 Trending" : "Suggestions");
        header.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-padding: 5 10;");
        resultsContainer.getChildren().add(header);

        for (String suggestion : suggestions) {
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.setStyle("-fx-cursor: hand;");

            FontIcon icon = new FontIcon("mdi2h-history:16");
            icon.setIconColor(Color.web("#666"));

            Label label = new Label(suggestion);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

            item.getChildren().addAll(icon, label);

            item.setOnMouseEntered(e ->
                item.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-cursor: hand;")
            );
            item.setOnMouseExited(e ->
                item.setStyle("-fx-cursor: hand;")
            );
            item.setOnMouseClicked(e -> {
                searchField.setText(suggestion);
                performSearch(suggestion);
            });

            resultsContainer.getChildren().add(item);
        }

        showResults();
    }

    private void showError(String message) {
        resultsContainer.getChildren().clear();

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-padding: 20;");
        resultsContainer.getChildren().add(errorLabel);

        showResults();
    }

    private void showResults() {
        if (!resultsPopup.isShowing() && getScene() != null && getScene().getWindow() != null) {
            Window window = getScene().getWindow();
            var bounds = localToScreen(getBoundsInLocal());
            resultsPopup.show(window, bounds.getMinX(), bounds.getMaxY() + 5);
        }
    }

    private void hideResults() {
        resultsPopup.hide();
    }

    private void navigateResults(int direction) {
        var items = resultsContainer.getChildren().stream()
            .filter(n -> n instanceof HBox && n.getUserData() instanceof SearchResult)
            .toList();

        if (items.isEmpty()) return;

        // Clear previous selection
        items.forEach(n -> n.setStyle("-fx-background-radius: 8; -fx-cursor: hand;"));

        selectedIndex += direction;
        if (selectedIndex < 0) selectedIndex = items.size() - 1;
        if (selectedIndex >= items.size()) selectedIndex = 0;

        var selected = items.get(selectedIndex);
        selected.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-cursor: hand;");
        selected.requestFocus();
    }

    private void selectItem(HBox item) {
        var items = resultsContainer.getChildren().stream()
            .filter(n -> n instanceof HBox && n.getUserData() instanceof SearchResult)
            .toList();

        selectedIndex = items.indexOf(item);
    }

    private void selectCurrentResult() {
        if (currentResults != null && selectedIndex >= 0 && selectedIndex < currentResults.size()) {
            SearchResult result = currentResults.get(selectedIndex);
            if (onResultSelected != null) {
                onResultSelected.accept(result);
            }
            hideResults();
        } else if (onSearchSubmit != null) {
            onSearchSubmit.accept(searchField.getText());
            hideResults();
        }
    }

    private void clearSearch() {
        searchField.clear();
        hideResults();
        searchField.requestFocus();
    }

    // Public API

    public void setUserRole(UserRole role) {
        this.currentRole = role;
    }

    public void setOnResultSelected(Consumer<SearchResult> handler) {
        this.onResultSelected = handler;
    }

    public void setOnSearchSubmit(Consumer<String> handler) {
        this.onSearchSubmit = handler;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public void setSearchText(String text) {
        searchField.setText(text);
    }

    public void focus() {
        searchField.requestFocus();
    }

    public void setPlaceholder(String text) {
        searchField.setPromptText(text);
    }
}

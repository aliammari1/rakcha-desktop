package com.esprit.utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Generic TableView utility class that provides common functionality for tables
 * including validation, tooltips, filtering, and editing capabilities.
 *
 * @param <T> The type of objects displayed in the table
 */
public class GenericTableView<T> {
    private static final Logger LOGGER = Logger.getLogger(GenericTableView.class.getName());

    private final TableView<T> tableView;
    private final ObservableList<T> items;
    private FilteredList<T> filteredItems;
    private SortedList<T> sortedItems;
    private final Map<TableColumn<T, ?>, ColumnConfig<T, ?>> columnConfigs = new HashMap<>();

    public GenericTableView(TableView<T> tableView) {
        this.tableView = tableView;
        this.items = FXCollections.observableArrayList();
        setupFilteringAndSorting();
        tableView.setEditable(true);
    }


    /**
     * Configuration class for table columns
     */
    public static class ColumnConfig<T, S> {
        private Function<T, S> valueExtractor;
        private BiConsumer<T, S> valueSetter;
        private Function<S, String> validator;
        private StringConverter<S> converter;
        private boolean editable = true;

        public ColumnConfig<T, S> withValueExtractor(Function<T, S> extractor) {
            this.valueExtractor = extractor;
            return this;
        }


        public ColumnConfig<T, S> withValueSetter(BiConsumer<T, S> setter) {
            this.valueSetter = setter;
            return this;
        }


        public ColumnConfig<T, S> withValidator(Function<S, String> validator) {
            this.validator = validator;
            return this;
        }


        public ColumnConfig<T, S> withConverter(StringConverter<S> converter) {
            this.converter = converter;
            return this;
        }


        public ColumnConfig<T, S> withEditable(boolean editable) {
            this.editable = editable;
            return this;
        }

    }


    /**
     * Configure a text column with validation
     */
    public <S> void configureColumn(TableColumn<T, S> column, ColumnConfig<T, S> config) {
        columnConfigs.put(column, config);

        // Set cell value factory
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, S>, ObservableValue<S>>() {
            @Override
            public ObservableValue<S> call(TableColumn.CellDataFeatures<T, S> param) {
                return new SimpleObjectProperty<>(config.valueExtractor.apply(param.getValue()));
            }

        }
);

        // Set cell factory with validation
        if (config.editable && config.converter != null) {
            column.setCellFactory(createValidatingCellFactory(config));
        }


        // Set on edit commit
        if (config.valueSetter != null) {
            column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<T, S>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<T, S> event) {
                    T item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    config.valueSetter.accept(item, event.getNewValue());
                    tableView.refresh();
                }

            }
);
        }


        column.setEditable(config.editable);
    }


    /**
     * Create a cell factory with validation support
     */
    private <S> Callback<TableColumn<T, S>, TableCell<T, S>> createValidatingCellFactory(ColumnConfig<T, S> config) {
        return column -> new TextFieldTableCell<T, S>(config.converter) {
            private Validator validator;
            private Tooltip errorTooltip;

            @Override
            public void startEdit() {
                super.startEdit();
                TextField textField = (TextField) this.getGraphic();

                if (textField != null && config.validator != null) {
                    if (validator == null) {
                        validator = new Validator();
                        errorTooltip = new Tooltip();
                        errorTooltip.setStyle("-fx-background-color: #f00; -fx-text-fill: white;");
                    }


                    validator.createCheck()
                            .dependsOn("value", textField.textProperty())
                            .withMethod(c -> {
                                String input = c.get("value");
                                String error = config.validator.apply((S) input);
                                if (error != null) {
                                    c.error(error);
                                }

                            }
)
                            .decorates(textField)
                            .immediate();

                    textField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                            updateValidationTooltip(textField);
                        }

                    }
);
                }

            }


            private void updateValidationTooltip(TextField textField) {
                if (validator != null && validator.containsErrors()) {
                    String errorMessage = validator.createStringBinding().getValue();
                    errorTooltip.setText(errorMessage);
                    textField.setTooltip(errorTooltip);

                    Window window = getScene().getWindow();
                    Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                    errorTooltip.show(window, bounds.getMinX(), bounds.getMinY() - 30);
                }
 else {
                    errorTooltip.hide();
                    textField.setTooltip(null);
                }

            }

        }
;
    }


    /**
     * Setup filtering and sorting
     */
    private void setupFilteringAndSorting() {
        filteredItems = new FilteredList<>(items, p -> true);
        sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedItems);
    }


    /**
     * Set filter predicate
     */
    public void setFilterPredicate(Predicate<T> predicate) {
        filteredItems.setPredicate(predicate);
    }


    /**
     * Add search functionality
     */
    public void setupSearchField(TextField searchField, Function<T, String> searchableFieldsExtractor) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }


                String lowerCaseFilter = newValue.toLowerCase();
                String searchableText = searchableFieldsExtractor.apply(item).toLowerCase();
                return searchableText.contains(lowerCaseFilter);
            }
);
        }
);
    }


    /**
     * Add items to the table
     */
    public void addItem(T item) {
        items.add(item);
    }


    /**
     * Add multiple items
     */
    public void addItems(ObservableList<T> newItems) {
        items.clear();
        items.addAll(newItems);
    }


    /**
     * Remove item
     */
    public void removeItem(T item) {
        items.remove(item);
    }


    /**
     * Clear all items
     */
    public void clearItems() {
        items.clear();
    }


    /**
     * Get filtered items
     */
    public ObservableList<T> getFilteredItems() {
        return filteredItems;
    }


    /**
     * Refresh the table
     */
    public void refresh() {
        tableView.refresh();
    }


    /**
     * Common validators
     */
    public static class Validators {
        public static Function<String, String> notEmpty() {
            return input -> {
                if (input == null || input.trim().isEmpty()) {
                    return "Field cannot be empty";
                }

                return null;
            }
;
        }


        public static Function<String, String> startsWithUppercase() {
            return input -> {
                String emptyError = notEmpty().apply(input);
                if (emptyError != null)
                    return emptyError;

                if (!Character.isUpperCase(input.charAt(0))) {
                    return "Must start with uppercase letter";
                }

                return null;
            }
;
        }


        public static Function<String, String> validYear(int minYear, int maxYear) {
            return input -> {
                String emptyError = notEmpty().apply(input);
                if (emptyError != null)
                    return emptyError;

                try {
                    int year = Integer.parseInt(input);
                    if (year < minYear || year > maxYear) {
                        return String.format("Year must be between %d and %d", minYear, maxYear);
                    }

                } catch (NumberFormatException e) {
                    return "Please enter a valid year";
                }

                return null;
            }
;
        }


        public static Function<String, String> timeFormat() {
            return input -> {
                String emptyError = notEmpty().apply(input);
                if (emptyError != null)
                    return emptyError;

                String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
                if (!input.matches(timeRegex)) {
                    return "Invalid time format. Use HH:MM:SS";
                }

                return null;
            }
;
        }

    }

}

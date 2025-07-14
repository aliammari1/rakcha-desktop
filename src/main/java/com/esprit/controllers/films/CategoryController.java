package com.esprit.controllers.films;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Category;
import com.esprit.services.films.CategoryService;
import com.esprit.utils.PageRequest;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import net.synedra.validatorfx.Validator;

/**
 * Controller class for managing film categories in the RAKCHA application.
 * 
 * <p>
 * This controller handles user interactions with the category management
 * interface,
 * including listing, creating, updating, and deleting film categories. It
 * provides
 * a robust validation system for user input with real-time feedback through
 * tooltips.
 * </p>
 * 
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Category CRUD operations (Create, Read, Update, Delete)</li>
 * <li>Real-time validation with immediate feedback</li>
 * <li>Inline table editing with validation</li>
 * <li>Category search and filtering</li>
 * </ul>
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CategoryController {
    private static final Logger LOGGER = Logger.getLogger(CategoryController.class.getName());

    @FXML
    private TextArea descriptionCategory_textArea;
    @FXML
    private TextArea nomCategory_textArea;
    @FXML
    private TableView<Category> filmCategory_tableView;
    @FXML
    private TableColumn<Category, String> nomCategory_tableColumn;
    @FXML
    private TableColumn<Category, String> descrptionCategory_tableColumn;
    @FXML
    private TableColumn<Category, Integer> idCategory_tableColumn;
    @FXML
    private TableColumn<Category, Button> delete_tableColumn;
    @FXML
    private AnchorPane categoryCrudInterface;
    @FXML
    private ComboBox<String> filterCriteriaComboBox;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button AjouterCategory_Button;
    @FXML
    private TextField recherche_textField;

    /**
     * Initializes the controller after FXML loading is complete.
     * 
     * <p>
     * This method sets up the TableView with necessary columns, configures
     * cell factories and value factories, and initializes the filtering criteria.
     * It is automatically called by JavaFX after the FXML file is loaded.
     * </p>
     */
    @FXML
    void initialize() {
        this.delete_tableColumn = new TableColumn<>("delete");
        this.filmCategory_tableView.getColumns().add(this.delete_tableColumn);
        this.filmCategory_tableView.setEditable(true);
        this.setupCellFactory();
        this.setupCellValueFactory();
        this.setupCellOnEditCommit();
        this.readCategoryTable();
        this.filterCriteriaComboBox.setItems(FXCollections.observableArrayList("Name", "Description"));
    }

    /**
     * Filters and displays categories based on a search keyword.
     * 
     * <p>
     * This method searches through all categories in the database and filters them
     * based on the provided keyword. If the keyword is null or empty, all
     * categories
     * are displayed. Otherwise, only categories whose name or description contains
     * the keyword (case-insensitive) are shown.
     * </p>
     *
     * @param keyword the search term to filter categories by name or description
     */
    private void search(final String keyword) {
        final CategoryService categoryService = new CategoryService();
        final ObservableList<Category> filteredList = FXCollections.observableArrayList();
        PageRequest pageRequest = new PageRequest(0, 10);
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read(pageRequest).getContent());
        } else {
            for (final Category category : categoryService.read(pageRequest).getContent()) {
                if (category.getName().toLowerCase().contains(keyword.toLowerCase())
                        || category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        this.filmCategory_tableView.setItems(filteredList);
    }

    /**
     * Creates a new category in the database.
     * 
     * <p>
     * This method creates a Category object with the name and description
     * provided in the respective text areas, persists it to the database
     * via CategoryService, and displays a confirmation alert to the user.
     * </p>
     *
     * @param event the action event triggered by clicking the insert button
     * @see Category
     * @see CategoryService#create(Category)
     */
    @FXML
    void insertCategory(final ActionEvent event) {
        final CategoryService categoryService = new CategoryService();
        final Category category = new Category(this.nomCategory_textArea.getText(),
                this.descriptionCategory_textArea.getText());
        categoryService.create(category);
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("l'insertion est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        this.readCategoryTable();
    }

    /**
     * Retrieves categories from the database and populates the table view.
     * 
     * <p>
     * This method fetches all categories using the CategoryService,
     * converts them to an ObservableList, and sets this list as the
     * items source for the category table view. Any exceptions during
     * this process are logged.
     * </p>
     * 
     * @see CategoryService#read()
     * @see FXCollections#observableArrayList(java.util.Collection)
     */
    void readCategoryTable() {
        try {
            final CategoryService categoryService = new CategoryService();
            PageRequest pageRequest = new PageRequest(0, 10);
            final ObservableList<Category> obC = FXCollections
                    .observableArrayList(categoryService.read(pageRequest).getContent());
            this.filmCategory_tableView.setItems(obC);
        } catch (final Exception e) {
            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Toggles the visibility of the category CRUD interface.
     * 
     * <p>
     * This method checks if the source of the event is the AjouterCategory_Button
     * and if so, makes the categoryCrudInterface visible.
     * </p>
     *
     * @param event the action event that triggered this method
     */
    public void switchForm(final ActionEvent event) {
        if (event.getSource() == this.AjouterCategory_Button) {
            this.categoryCrudInterface.setVisible(true);
        }
    }

    /**
     * Deletes a category with the given ID from the database.
     * 
     * <p>
     * This method retrieves the category with the specified ID using
     * CategoryService,
     * deletes it from the database, displays a confirmation alert, and refreshes
     * the category table to reflect the change.
     * </p>
     *
     * @param id the unique identifier of the category to delete
     * @see CategoryService#delete(Category)
     * @see CategoryService#getCategory(Long)
     */
    void deleteCategory(final Long id) {
        try {
            final CategoryService categoryService = new CategoryService();
            categoryService.delete(categoryService.getCategory(id));
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("la suppression est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            this.readCategoryTable();
        } catch (final Exception e) {
            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Updates a category in the database.
     * 
     * <p>
     * This method uses the CategoryService to update the provided category in the
     * database, displays a confirmation alert, and refreshes the category table
     * to reflect the changes.
     * </p>
     *
     * @param category the category object to update with new values
     * @see CategoryService#update(Category)
     */
    void updateCategory(final Category category) {
        final CategoryService categoryService = new CategoryService();
        categoryService.update(category);
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("le mis à jour est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        this.readCategoryTable();
    }

    /**
     * Sets cell factories for three table columns, `idCategory_tableColumn`,
     * `nomCategory_tableColumn`, and `descrptionCategory_tableColumn`. These
     * factories create custom TableCells that display a text field with an error
     * message when the user types invalid input.
     */
    private void setupCellFactory() {
        this.idCategory_tableColumn.setVisible(false);
        final Callback<TableColumn<Category, String>, TableCell<Category, String>> stringCellFactory = new Callback<>() {
            /**
             * Generates a `TextFieldTableCell` that provides text validation. When the user
             * starts editing the cell, the validator checks for input errors and displays
             * them as a tooltip.
             *
             * @param param
             *              TableColumn object that provides the editing functionality for
             *              the
             *              cell.
             *
             *              - `param`: A `TableColumn<Category, String>` object,
             *              representing
             *              the column to be edited.
             *
             * @returns a `TableCell` object that displays an editable text field with
             *          validation rules.
             *
             *          - `TableCell<Category, String>`: This is the type of the output,
             *          indicating that it is a cell in a table with a category parameter
             *          and a string value. - `TextField` and `Validator`: These are objects
             *          used within the `call` function to create a text field with a
             *          validator. The `TextField` object represents the text field itself,
             *          while the `Validator` object defines the validation rules for the
             *          input field. - `localToScreen()`: This method is used to convert the
             *          local coordinates of the text field to screen coordinates, allowing
             *          us to position the tooltip correctly.
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<Category, String> call(final TableColumn<Category, String> param) {
                return new TextFieldTableCell<>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Creates a validator that checks if the input is not empty, starts with an
                     * uppercase letter, and displays an error tooltip if any of these conditions
                     * are true. It also listens for changes to the text property of a TextField and
                     * displays the error tooltip when there are errors.
                     */
                    @Override
                    /**
                     * Performs startEdit operation.
                     *
                     * @return the result of the operation
                     */
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("text", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("text");
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                } else if (!Character.isUpperCase(input.charAt(0))) {
                                    c.error("Please start with an uppercase letter.");
                                }
                            }).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Is called whenever the value of an observable changes. It checks if there are
                                 * any validation errors and displays a tooltip with the error message if
                                 * present.
                                 *
                                 * @param observable
                                 *                   ObservableValue object that is being observed for changes,
                                 *                   and it
                                 *                   provides the old and new values of the observed value in
                                 *                   the
                                 *                   method call.
                                 *
                                 *                   - `observable`: An observable value that can hold a String
                                 *                   value.
                                 *                   - `oldValue`: The previous value of the observable. -
                                 *                   `newValue`:
                                 *                   The current value of the observable.
                                 *
                                 * @param oldValue
                                 *                   previous value of the observable property before the change
                                 *                   occurred, which is used to check if the new value is valid
                                 *                   or not.
                                 *
                                 * @param newValue
                                 *                   updated value of the observable field, which is used to
                                 *                   determine
                                 *                   if any validation errors exist and to update the tooltip
                                 *                   text
                                 *                   accordingly.
                                 */
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    CategoryController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }
        };
        this.nomCategory_tableColumn.setCellFactory(stringCellFactory);
        this.descrptionCategory_tableColumn.setCellFactory(stringCellFactory);
    }

    /**
     * Sets cell values for four table columns in a category list by creating
     * property value factories that return the id, description, nom, and delete
     * buttons for each category respectively.
     */
    private void setupCellValueFactory() {
        this.idCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
        this.descrptionCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Generates an observable value of a string property based on the value of a
                     * `Category` object and returns it.
                     *
                     * @param param
                     *              cell value of a table column, and provides access to its
                     *              corresponding description property.
                     *
                     * @returns a `SimpleStringProperty` containing the description of the input
                     *          value.
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(final TableColumn.CellDataFeatures<Category, String> param) {
                        return new SimpleStringProperty(param.getValue().getDescription());
                    }
                });
        this.nomCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Generates a `SimpleStringProperty` instance from a `Category` object's
                     * `getName()` method result and returns it.
                     *
                     * @param filmcategoryStringCellDataFeatures
                     *                                           cell data features of a table
                     *                                           column, specifically the string
                     *                                           value of the category field.
                     *
                     *                                           - `Value`: The current value of the
                     *                                           cell data feature, which is a
                     *                                           string representing the nominal
                     *                                           value of a category.
                     *
                     * @returns a `SimpleStringProperty` containing the nominated value of the `
                     *          filmcategoryStringCellDataFeatures` parameter.
                     *
                     *          - The output is an instance of `SimpleStringProperty`, which
                     *          represents a simple string value. - The value of the string property
                     *          is obtained by calling the `getName()` method on the input parameter
                     *          `filmcategoryStringCellDataFeatures.getValue()`.
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Category, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getName());
                    }
                });
        this.delete_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, Button>, ObservableValue<Button>>() {
                    /**
                     * Creates a `Button` element with an `onAction` event handler that calls the
                     * `deleteCategory` function when clicked, passing the category ID as an
                     * argument. The function returns a `SimpleObjectProperty` of the created
                     * button.
                     *
                     * @param param
                     *              current cell value of the table column, which is of type
                     *              `Category`, and provides access to its `Id` attribute.
                     *
                     *              - `param.getValue()`: returns the value of the category being
                     *              processed, which is of type `Category`. - `param.getId()`:
                     *              returns
                     *              the ID of the category being processed.
                     *
                     * @returns a `SimpleObjectProperty` of a `Button` object with an action to
                     *          delete a category.
                     *
                     *          - The output is an instance of `SimpleObjectProperty`, which
                     *          represents a single object that can be used to display or manipulate
                     *          the object in a UI component. - The output is initialized with a new
                     *          instance of `Button`, which has a text property of `"delete"` and an
                     *          `onAction` property that is set to a lambda function that handles
                     *          the button's action when clicked.
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Button> call(final TableColumn.CellDataFeatures<Category, Button> param) {
                        final Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Deletes a category based on its ID.
                             *
                             * @param event
                             *              deletion of a category, which is triggered by the user's action.
                             */
                            @Override
                            /**
                             * Performs handle operation.
                             *
                             * @return the result of the operation
                             */
                            public void handle(final ActionEvent event) {
                                deleteCategory(param.getValue().getId());
                            }
                        });
                        return new SimpleObjectProperty<Button>(button);
                    }
                });
    }

    /**
     * Sets cell edit events for two columns of a table to update the corresponding
     * fields of a `Category` object when committed.
     */
    private void setupCellOnEditCommit() {
        this.nomCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            /**
             * Updates the value of a cell in a table column when the user edits it, and
             * also updates the corresponding category object.
             *
             * @param event
             *              `TableColumn.CellEditEvent` that triggered the function's
             *              execution, providing the edited cell value and its position in
             *              the
             *              table.
             *
             *              - `event.getTableView()` returns the table view object
             *              associated
             *              with the event. - `event.getTablePosition().getRow()` returns
             *              the
             *              row index of the cell being edited. - `event.getNewValue()`
             *              returns the new value to be set in the cell.
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
                    CategoryController.this
                            .updateCategory(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.descrptionCategory_tableColumn
                .setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
                    /**
                     * Is called when a cell in a table is edited, and it updates the description of
                     * the corresponding category item in the table view, and also updates the
                     * category object itself.
                     *
                     * @param event
                     *              CellEditEvent object that contains information about the edited
                     *              cell, including the new value and the row index of the edited
                     *              cell.
                     *
                     *              - `TableColumn.CellEditEvent<Category, String> event`: This
                     *              represents an event that occurs when a cell in a table is being
                     *              edited. The event provides information about the edited cell and
                     *              its position in the table.
                     */
                    @Override
                    /**
                     * Performs handle operation.
                     *
                     * @return the result of the operation
                     */
                    public void handle(final TableColumn.CellEditEvent<Category, String> event) {
                        try {
                            event.getTableView().getItems().get(event.getTablePosition().getRow())
                                    .setDescription(event.getNewValue());
                            updateCategory(
                                    event.getTableView().getItems().get(event.getTablePosition().getRow()));
                        } catch (final Exception e) {
                            CategoryController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                });
    }
}

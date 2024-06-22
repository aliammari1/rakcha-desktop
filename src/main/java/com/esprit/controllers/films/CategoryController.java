package com.esprit.controllers.films;
import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.films.Category;
import com.esprit.models.users.Client;
import com.esprit.services.films.CategoryService;
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
 * Is responsible for handling user interactions with the category table. It provides
 * a setupCellFactory method to set up cell factories for each column in the table,
 * including id, nom, and description. The setupCellValueFactory method sets the cell
 * value factory for each column, and the setupCellOnEditCommit method sets the on
 * edit commit event handler for each column. These methods work together to handle
 * user input and updates to the category table.
 */
public class CategoryController {
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
     * Adds a new column to a table, makes the table editable, sets up cell factories,
     * value factories, and on-edit commit listeners. It also reads data from a category
     * table and provides initial values for a filter criteria combobox.
     */
    @FXML
    void initialize() {
        delete_tableColumn = new TableColumn<>("delete");
        filmCategory_tableView.getColumns().add(delete_tableColumn);
        filmCategory_tableView.setEditable(true);
        setupCellFactory();
        setupCellValueFactory();
        setupCellOnEditCommit();
        readCategoryTable();
        filterCriteriaComboBox.setItems(FXCollections.observableArrayList("Name", "Description"));
    }
    /**
     * Filters and displays a list of categories based on a search query provided as an
     * argument.
     * 
     * @param keyword search query used to filter and display only relevant categories
     * in the `filmCategory_tableView`.
     */
    private void search(String keyword) {
        CategoryService categoryService = new CategoryService();
        ObservableList<Category> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read());
        } else {
            for (Category category : categoryService.read()) {
                if (category.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                        category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        filmCategory_tableView.setItems(filteredList);
    }
    /**
     * Allows the user to create a new category by filling in a form with the category
     * name and description, then calls the `create()` method of a `CategoryService` class
     * to insert the category into the database, displays an alert message confirming the
     * operation, and then refreshes the table displaying the categories.
     * 
     * @param event user-generated action that triggered the function execution, allowing
     * the code to respond accordingly.
     * 
     * Event type: `ActionEvent`
     * Target object: `nomCategory_textArea` and `descriptionCategory_textArea`
     * Context: Callback function for button press
     */
    @FXML
    void insertCategory(ActionEvent event) {
        CategoryService categoryService = new CategoryService();
        Category category = new Category(nomCategory_textArea.getText(), descriptionCategory_textArea.getText());
        categoryService.create(category);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("l'insertion est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        readCategoryTable();
    }
    /**
     * Retrieves a list of categories from a service and populates a table with them.
     */
    void readCategoryTable() {
        try {
            CategoryService categoryService = new CategoryService();
            ObservableList<Category> obC = FXCollections.observableArrayList(categoryService.read());
            filmCategory_tableView.setItems(obC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets the visibility of an interface based on the source of the event.
     * 
     * @param event ActionEvent object that triggered the method, providing information
     * about the source of the event and its state.
     */
    public void switchForm(ActionEvent event) {
        if (event.getSource() == AjouterCategory_Button) {
            categoryCrudInterface.setVisible(true);
        }
    }
    /**
     * Deletes a category with the given ID using `CategoryService`. If successful, it
     * displays an alert message and updates the category table.
     * 
     * @param id identity of the category to be deleted.
     */
    void deleteCategory(int id) {
        try {
            CategoryService categoryService = new CategoryService();
            categoryService.delete(categoryService.getCategory(id));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("la suppression est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            readCategoryTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Updates a specified category using the `CategoryService`, displays an alert to
     * confirm the update, and then calls the `readCategoryTable()` function to refresh
     * the category table.
     * 
     * @param category category object to be updated.
     * 
     * 	- `Category category`: Represents a single category to be updated. Its main
     * properties include its name and any relevant subcategories.
     */
    void updateCategory(Category category) {
        CategoryService categoryService = new CategoryService();
        categoryService.update(category);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("le mis à jour est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        readCategoryTable();
    }
    /**
     * Sets cell factories for three table columns, `idCategory_tableColumn`,
     * `nomCategory_tableColumn`, and `descrptionCategory_tableColumn`. These factories
     * create custom TableCells that display a text field with an error message when the
     * user types invalid input.
     */
    private void setupCellFactory() {
        idCategory_tableColumn.setVisible(false);
        Callback<TableColumn<Category, String>, TableCell<Category, String>> stringCellFactory = new Callback<TableColumn<Category, String>, TableCell<Category, String>>() {
            /**
             * Generates a `TextFieldTableCell` that provides text validation. When the user
             * starts editing the cell, the validator checks for input errors and displays them
             * as a tooltip.
             * 
             * @param param TableColumn object that provides the editing functionality for the cell.
             * 
             * 	- `param`: A `TableColumn<Category, String>` object, representing the column to
             * be edited.
             * 
             * @returns a `TableCell` object that displays an editable text field with validation
             * rules.
             * 
             * 	- `TableCell<Category, String>`: This is the type of the output, indicating that
             * it is a cell in a table with a category parameter and a string value.
             * 	- `TextField` and `Validator`: These are objects used within the `call` function
             * to create a text field with a validator. The `TextField` object represents the
             * text field itself, while the `Validator` object defines the validation rules for
             * the input field.
             * 	- `localToScreen()`: This method is used to convert the local coordinates of the
             * text field to screen coordinates, allowing us to position the tooltip correctly.
             */
            @Override
            public TableCell<Category, String> call(TableColumn<Category, String> param) {
                return new TextFieldTableCell<Category, String>(new DefaultStringConverter()) {
                    private Validator validator;
                    /**
                     * Creates a validator that checks if the input is not empty, starts with an uppercase
                     * letter, and displays an error tooltip if any of these conditions are true. It also
                     * listens for changes to the text property of a TextField and displays the error
                     * tooltip when there are errors.
                     */
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("text", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("text");
                                        if (input == null || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!Character.isUpperCase(input.charAt(0))) {
                                            c.error("Please start with an uppercase letter.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Is called whenever the value of an observable changes. It checks if there are any
                                 * validation errors and displays a tooltip with the error message if present.
                                 * 
                                 * @param observable ObservableValue object that is being observed for changes, and
                                 * it provides the old and new values of the observed value in the method call.
                                 * 
                                 * 	- `observable`: An observable value that can hold a String value.
                                 * 	- `oldValue`: The previous value of the observable.
                                 * 	- `newValue`: The current value of the observable.
                                 * 
                                 * @param oldValue previous value of the observable property before the change occurred,
                                 * which is used to check if the new value is valid or not.
                                 * 
                                 * @param newValue updated value of the observable field, which is used to determine
                                 * if any validation errors exist and to update the tooltip text accordingly.
                                 */
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                                    System.out.println(validator.containsErrors());
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (textField.getTooltip() != null)
                                            textField.getTooltip().hide();
                                    }
                                }
                            });
                        }
                    }
                };
            }
        };
        nomCategory_tableColumn.setCellFactory(stringCellFactory);
        descrptionCategory_tableColumn.setCellFactory(stringCellFactory);
    }
    /**
     * Sets cell values for four table columns in a category list by creating property
     * value factories that return the id, description, nom, and delete buttons for each
     * category respectively.
     */
    private void setupCellValueFactory() {
        idCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
        descrptionCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Generates an observable value of a string property based on the value of a `Category`
                     * object and returns it.
                     * 
                     * @param param cell value of a table column, and provides access to its corresponding
                     * description property.
                     * 
                     * @returns a `SimpleStringProperty` containing the description of the input value.
                     */
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                        return new SimpleStringProperty(param.getValue().getDescription());
                    }
                });
        nomCategory_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
                    /**
                     * Generates a `SimpleStringProperty` instance from a `Category` object's `getNom()`
                     * method result and returns it.
                     * 
                     * @param filmcategoryStringCellDataFeatures cell data features of a table column,
                     * specifically the string value of the category field.
                     * 
                     * 	- `Value`: The current value of the cell data feature, which is a string representing
                     * the nominal value of a category.
                     * 
                     * @returns a `SimpleStringProperty` containing the nominated value of the `
                     * filmcategoryStringCellDataFeatures` parameter.
                     * 
                     * 	- The output is an instance of `SimpleStringProperty`, which represents a simple
                     * string value.
                     * 	- The value of the string property is obtained by calling the `getNom()` method
                     * on the input parameter `filmcategoryStringCellDataFeatures.getValue()`.
                     */
                    @Override
                    public ObservableValue<String> call(
                            TableColumn.CellDataFeatures<Category, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getNom());
                    }
                });
        delete_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Category, Button>, ObservableValue<Button>>() {
                    /**
                     * Creates a `Button` element with an `onAction` event handler that calls the
                     * `deleteCategory` function when clicked, passing the category ID as an argument.
                     * The function returns a `SimpleObjectProperty` of the created button.
                     * 
                     * @param param current cell value of the table column, which is of type `Category`,
                     * and provides access to its `Id` attribute.
                     * 
                     * 	- `param.getValue()`: returns the value of the category being processed, which
                     * is of type `Category`.
                     * 	- `param.getId()`: returns the ID of the category being processed.
                     * 
                     * @returns a `SimpleObjectProperty` of a `Button` object with an action to delete a
                     * category.
                     * 
                     * 	- The output is an instance of `SimpleObjectProperty`, which represents a single
                     * object that can be used to display or manipulate the object in a UI component.
                     * 	- The output is initialized with a new instance of `Button`, which has a text
                     * property of `"delete"` and an `onAction` property that is set to a lambda function
                     * that handles the button's action when clicked.
                     */
                    @Override
                    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Category, Button> param) {
                        Button button = new Button("delete");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Deletes a category based on its ID.
                             * 
                             * @param event deletion of a category, which is triggered by the user's action.
                             */
                            @Override
                            public void handle(ActionEvent event) {
                                deleteCategory(param.getValue().getId());
                            }
                        });
                        return new SimpleObjectProperty<Button>(button);
                    }
                });
    }
    /**
     * Sets cell edit events for two columns of a table to update the corresponding fields
     * of a `Category` object when committed.
     */
    private void setupCellOnEditCommit() {
        nomCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            /**
             * Updates the value of a cell in a table column when the user edits it, and also
             * updates the corresponding category object.
             * 
             * @param event `TableColumn.CellEditEvent` that triggered the function's execution,
             * providing the edited cell value and its position in the table.
             * 
             * 	- `event.getTableView()` returns the table view object associated with the event.
             * 	- `event.getTablePosition().getRow()` returns the row index of the cell being edited.
             * 	- `event.getNewValue()` returns the new value to be set in the cell.
             */
            @Override
            public void handle(TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setNom(event.getNewValue());
                    updateCategory(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        descrptionCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            /**
             * Is called when a cell in a table is edited, and it updates the description of the
             * corresponding category item in the table view, and also updates the category object
             * itself.
             * 
             * @param event CellEditEvent object that contains information about the edited cell,
             * including the new value and the row index of the edited cell.
             * 
             * 	- `TableColumn.CellEditEvent<Category, String> event`: This represents an event
             * that occurs when a cell in a table is being edited. The event provides information
             * about the edited cell and its position in the table.
             */
            @Override
            public void handle(TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDescription(event.getNewValue());
                    updateCategory(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

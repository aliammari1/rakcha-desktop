package com.esprit.controllers.series;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Is responsible for handling user input and displaying information related to
 * categories in a movie streaming platform. The controller includes methods for
 * modifiying category details, adding new categories, and displaying statistics
 * related to the categories. Additionally, it includes methods for navigating
 * between different views of the application, such as the main menu, series
 * view, episode view, and product view.
 */
public class CategorieController {
    private static final Logger LOGGER = Logger.getLogger(CategorieController.class.getName());

    @FXML
    private Label checkdescreption;
    @FXML
    private Label checkname;
    @FXML
    private TextField nomF;
    @FXML
    private TextField descreptionF;
    @FXML
    private TableView<Category> tableView;

    /**
     * Updates the table view's contents by clearing its items and columns, then
     * adding new columns for an "Edit" and "Delete" button, and finally adding the
     * recovered categories from the database to the table view.
     */
    private void ref() {
        this.tableView.getItems().clear();
        this.tableView.getColumns().clear();
        this.nomF.setText("");
        this.descreptionF.setText("");
        // affichege tableau
        final IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        // TableColumn<Categorie, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idcategorie"));
        final TableColumn<Category, String> nomCol = new TableColumn<>("Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        final TableColumn<Category, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        final TableColumn<Category, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Delete");

            {
                this.button.setOnAction(event -> {
                    final Category category = this.getTableView().getItems().get(this.getIndex());
                    try {
                        categorieserv.delete(category);
                        CategorieController.this.tableView.getItems().remove(category);
                        CategorieController.this.showAlert("Succes", "Deleted successfully !");
                        CategorieController.this.ref();
                        CategorieController.this.tableView.refresh();
                    } catch (final Exception e) {
                        CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        CategorieController.this.showAlert("Error", e.getMessage());
                    }
                });
            }

            /**
             * Updates an item's graphic based on whether it is empty or not. If the item is
             * empty, the function sets the graphic to null; otherwise, it sets the graphic
             * to a button.
             *
             * @param item
             *            Void object that is being updated by the function, and its value
             *            determines whether the graphic associated with the item should be
             *            set to null or the button provided as an argument to the function.
             *
             *            - `item`: A Void object representing an item whose graphic is to
             *            be updated.
             *
             * @param empty
             *            whether the item is empty or not, which determines whether the
             *            graphic element is set to `null` or `button`.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
        final TableColumn<Category, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount;

            {
                this.button.setOnAction(event -> {
                    this.clickCount++;
                    if (2 == clickCount) {
                        final Category category = this.getTableView().getItems().get(this.getIndex());
                        CategorieController.this.modifierCategorie(category);
                        this.clickCount = 0;
                    }
                });
            }

            /**
             * Updates the graphics of an item based on whether it is empty or not. If the
             * item is empty, the function sets the graphic to null. Otherwise, it sets the
             * graphic to a button.
             *
             * @param item
             *            Void object being updated, and its value is passed to the super
             *            method `updateItem()` for further processing.
             *
             *            - `item`: A Void object representing an item to be updated. -
             *            `empty`: A boolean indicating whether the item is empty or not.
             *
             * @param empty
             *            whether the item is empty or not, and determines whether the
             *            graphic of the item should be updated to null or the button
             *            object.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
        // tableView.getColumns().addAll(idCol,nomCol, descriptionCol,modifierCol,
        // supprimerCol);
        this.tableView.getColumns().addAll(nomCol, descriptionCol, modifierCol, supprimerCol);
        try {
            this.tableView.getItems().addAll(categorieserv.read());
        } catch (final Exception e) {
            CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Creates an Alert object with an informational message and shows it to the
     * user via the `showAndWait()` method.
     *
     * @param title
     *            title of an alert window that will be displayed when the
     *            `showAlert` method is called.
     * @param message
     *            text to be displayed in the alert box.
     */
    private void showAlert(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Allows for the modification of a category's name and description through a
     * dialogue box interface. The function calls the `iServiceCategorie.modifier()`
     * method to update the category in the database after the user has entered the
     * new details and confirmed the modifications.
     *
     * @param categorie
     *            category object that is to be modified through the dialog box, and
     *            its properties (name and description) are editable and can be
     *            modified by the user through the dialog UI.
     *            <p>
     *            - `nom`: The name of the category. - `description`: A brief
     *            description of the category.
     */
    private void modifierCategorie(final Category categorie) {
        final IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");
        final TextField nomFild = new TextField(categorie.getName());
        final TextField desqFild = new TextField(String.valueOf(categorie.getDescription()));
        dialog.getDialogPane()
                .setContent(new VBox(8, new Label("Name:"), nomFild, new Label("Description:"), desqFild));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nomFild.getText(), desqFild.getText());
            }
            return null;
        });
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            categorie.setName(nomFild.getText());
            categorie.setDescription(desqFild.getText());
            try {
                iServiceCategorie.update(categorie);
                this.showAlert("successfully", "Modified successfully!");
                this.ref();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * References an object called `ref`.
     */
    @FXML
    private void initialize() {
        this.ref();
    }

    /**
     * Checks if the user has entered a non-empty string. If so, it returns `true`.
     * Otherwise, it displays an error message and returns `false`.
     *
     * @returns a boolean value indicating whether a valid name has been entered.
     *          <p>
     *          - The function returns a boolean value indicating whether the user
     *          has entered a valid name or not. - If the input field is not empty,
     *          the function returns `true`. - If the input field is empty, the
     *          function returns `false` and sets the `checkname` text to "Please
     *          enter a valid Name".
     */
    boolean checkname() {
        if ("" != nomF.getText()) {
            return true;
        } else {
            this.checkname.setText("Please enter a valid Name");
            return false;
        }
    }

    /**
     * Checks if the user has entered a non-empty string in the
     * `descreptionF.getText()` field, and returns `true` if so, or `false`
     * otherwise, along with a message to enter a valid description.
     *
     * @returns a boolean value indicating whether a valid description has been
     *          entered.
     *          <p>
     *          - The function returns a boolean value indicating whether the user
     *          has entered a valid description or not. - If the description is not
     *          empty, the function returns `true`. - If the description is empty,
     *          the function sets the `checkdescreption` text to "Please enter a
     *          valid Description" and returns `false`.
     */
    boolean checkdescreption() {
        if ("" != descreptionF.getText()) {
            return true;
        } else {
            this.checkdescreption.setText("Please enter a valid Description");
            return false;
        }
    }

    /**
     * Allows users to add a new category by validating input fields, saving the
     * category to a service implementation class, and displaying an alert message
     * upon successful save.
     *
     * @param event
     *            user's action of clicking the "Add Category" button, which
     *            triggers the execution of the code within the function.
     *            <p>
     *            - Type: `ActionEvent` - Details: Contains information about the
     *            action that triggered the function, such as the source of the
     *            event and the identifier of the event.
     */
    @FXML
    void ajouteroeuvre(final ActionEvent event) {
        final IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        final Category categorie = new Category();
        this.checkname();
        this.checkdescreption();
        if (this.checkname() && this.checkdescreption()) {
            try {
                categorie.setName(this.nomF.getText());
                categorie.setDescription(this.descreptionF.getText());
                categorieserv.create(categorie);
                this.showAlert("Succes", "The category has been saved successfully");
                this.checkname.setText("");
                this.checkdescreption.setText("");
                this.ref();
            } catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the category : " + e.getMessage());
            }
        }
    }

    /// Gestion du menu
    /**
     * Loads a FXML file, creates a scene and sets it as the scene of a stage, and
     * then shows the stage.
     *
     * @param event
     *            event that triggered the method, specifically the opening of the
     *            `Categorie-view.fxml` file.
     *            <p>
     *            - `Event`: This is the class that represents an event in Java,
     *            containing information about the source and type of the event. -
     *            `Object`: The `event` parameter is an instance of the `Object`
     *            class, which provides no additional information beyond the fact
     *            that it is an event object.
     */
    @FXML
    void Ocategories(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Categorie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads and displays a FXML view named "Serie-view".
     *
     * @param event
     *            event object that triggered the function, providing information
     *            about the source of the event and other details.
     *            <p>
     *            Event: An ActionEvent object representing an action triggered by
     *            the user.
     *            <p>
     *            Properties:
     *            <p>
     *            - `getSource()`: Returns the source of the event (i.e., the button
     *            or menu item that was clicked).
     */
    @FXML
    void Oseriess(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Serie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file, creates a scene, and sets it as the scene of a Stage,
     * displaying the content on the stage.
     *
     * @param event
     *            source of the action that triggered the method, providing a
     *            reference to the object from which the event originated.
     *            <p>
     *            Event: An instance of the `ActionEvent` class that contains
     *            information about the action performed by the user. Properties:
     *            <p>
     *            - `getSource()`: Returns the object that triggered the event. In
     *            this case, it is a `Node` representing the `Episode-view.fxml`
     *            stage.
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Episode-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads a FXML view and replaces the current scene with it when an action event
     * is triggered.
     *
     * @param actionEvent
     *            event that triggered the function, which is used to determine what
     *            action to take based on the event type.
     *            <p>
     *            - If `actionEvent` is not null, it represents an action event
     *            triggered by the user.
     */
    @FXML
    /**
     * Performs showStatistics operation.
     *
     * @return the result of the operation
     */
    public void showStatistics(final ActionEvent actionEvent) {
        if (null != actionEvent) {
            // Logique pour basculer vers la vue des statistiques
            // Vous pouvez utiliser un FXMLLoader pour charger la vue des statistiques
            try {
                final FXMLLoader loader = new FXMLLoader(
                        this.getClass().getResource("/ui/series/StatistiquesView.fxml"));
                final Parent root = loader.load();
                final StatistiqueController statistiqueController = loader.getController();
                // Initialisez le contrôleur statistique si nécessaire
                statistiqueController.initialize();
                // Créez une nouvelle scène
                final Scene scene = new Scene(root);
                // Obtenez la scène actuelle à partir de l'événement
                final Stage stage = new Stage();
                // Remplacez la scène actuelle par la nouvelle scène
                stage.setScene(scene);
                stage.show();
            } catch (final IOException e) {
                CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // Gérer le cas où actionEvent est nul
            CategorieController.LOGGER.info("actionEvent is null");
        }
    }

    /**
     * Likely displays a list or movies or performs some other movie-related actions
     * when an event is triggered.
     *
     * @param actionEvent
     *            event that triggered the execution of the `showMovies` function.
     */
    public void showmovies(final ActionEvent actionEvent) {
    }

    /**
     * Displays a list of products.
     *
     * @param actionEvent
     *            event that triggered the function call.
     */
    public void showproducts(final ActionEvent actionEvent) {
    }

    /**
     * Does not have any discernible behavior or functionality as it is empty and
     * lacks any statements or actions to perform.
     *
     * @param actionEvent
     *            event that triggered the function call.
     */
    public void showcinema(final ActionEvent actionEvent) {
    }

    /**
     * Is invoked when an event occurs and has no discernible functionality as it
     * only contains a blank implementation.
     *
     * @param actionEvent
     *            event that triggered the function's execution.
     */
    public void showevent(final ActionEvent actionEvent) {
    }

    /**
     * Is triggered by an `ActionEvent`. It does not provide any information about
     * the code author or licensing.
     *
     * @param actionEvent
     *            event that triggered the `showseries()` function to be called,
     *            providing the necessary context for its execution.
     */
    public void showseries(final ActionEvent actionEvent) {
    }
}

package com.esprit.controllers.series;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.utils.PageRequest;

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
     * Refreshes the category table and input fields, and repopulates the table with current categories.
     *
     * Reinitializes the TableView by clearing its items and columns, clearing the name and description input
     * fields, installing columns for name, description, and per-row Edit/Delete actions, and loading the
     * first page of categories from the service. The Edit and Delete columns provide row buttons that
     * trigger an edit flow or remove the row respectively; failures are logged and reported via alerts.
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
                        tableView.getItems().remove(category);
                        showAlert("Succes", "Deleted successfully !");
                        ref();
                        tableView.refresh();
                    }
 catch (final Exception e) {
                        CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        showAlert("Error", e.getMessage());
                    }

                }
);
            }


            /**
             * Set the cell's graphic to the configured button when the cell is not empty; clear the graphic when it is empty.
             *
             * @param item  unused placeholder (type Void) required by the cell API.
             * @param empty true if the cell should be treated as empty and display no graphic, false to display the button.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        final TableColumn<Category, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount;

            {
                this.button.setOnAction(event -> {
                    this.clickCount++;
                    if (2 == clickCount) {
                        final Category category = this.getTableView().getItems().get(this.getIndex());
                        modifierCategorie(category);
                        this.clickCount = 0;
                    }

                }
);
            }


            /**
             * Set the cell's graphic to the configured button when the cell is not empty; clear the graphic when it is empty.
             *
             * @param item  unused placeholder (type Void) required by the cell API.
             * @param empty true if the cell should be treated as empty and display no graphic, false to display the button.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        // tableView.getColumns().addAll(idCol,nomCol, descriptionCol,modifierCol,
        // supprimerCol);
        this.tableView.getColumns().addAll(nomCol, descriptionCol, modifierCol, supprimerCol);
        try {
            PageRequest pageRequest = new PageRequest(0,10);
            this.tableView.getItems().addAll(categorieserv.read(pageRequest).getContent());
        }
 catch (final Exception e) {
            CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Display an informational alert with the given window title and content message.
     *
     * @param title   window title shown on the alert dialog
     * @param message content text shown inside the alert
     */
    private void showAlert(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Opens a dialog to edit the given category's name and description and persists the changes.
     *
     * @param categorie the category to be edited; its name and description are updated with the user's input
     * @throws RuntimeException if persisting the updated category fails
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
        }
);
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            categorie.setName(nomFild.getText());
            categorie.setDescription(desqFild.getText());
            try {
                iServiceCategorie.update(categorie);
                this.showAlert("successfully", "Modified successfully!");
                this.ref();
            }
 catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
);
    }


    /**
     * Initialize the controller and populate the category table by refreshing view data.
     *
     * This method is invoked by the FXML loader after the controller is constructed.
     */
    @FXML
    private void initialize() {
        this.ref();
    }


    /**
     * Validate that the name input field contains at least one character.
     *
     * @return `true` if the name field contains at least one character, `false` otherwise.
     */
    boolean checkname() {
        if ("" != nomF.getText()) {
            return true;
        }
 else {
            this.checkname.setText("Please enter a valid Name");
            return false;
        }

    }


    /**
     * Validate that the description input is not empty.
     *
     * @return `true` if the description text is not empty, `false` otherwise; when the
     *         description is empty the `checkdescreption` label is set to
     *         "Please enter a valid Description".
     */
    boolean checkdescreption() {
        if ("" != descreptionF.getText()) {
            return true;
        }
 else {
            this.checkdescreption.setText("Please enter a valid Description");
            return false;
        }

    }


    /**
     * Add a new category from the input fields after validating them and persist it via the category service.
     *
     * Validates the name and description fields; if both are valid, creates a Category, saves it through the service,
     * shows a success alert, clears validation messages, and refreshes the view. If saving fails, shows an error alert
     * with the failure message.
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
            }
 catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the category : " + e.getMessage());
            }

        }

    }


    /// Gestion du menu
    /**
     * Navigate to and display the Categorie-view.fxml scene.
     *
     * @param event the ActionEvent that triggered the navigation; used to obtain the current window
     * @throws IOException if the FXML resource cannot be loaded
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
     * Open the "Serie-view.fxml" UI and set it as the scene of the current window.
     *
     * @throws IOException if the FXML resource cannot be loaded
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
     * Switches the current window to the Episode view defined by /ui/series/Episode-view.fxml.
     *
     * Loads the FXML, creates a Scene from it, and sets that Scene on the Stage obtained
     * from the event's source node.
     *
     * @param event the ActionEvent whose source Node is used to obtain the current Stage
     * @throws IOException if the FXML resource cannot be loaded
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
     * Opens the statistics view in a new window by loading and displaying
     * the "StatistiquesView.fxml" scene.
     *
     * If `actionEvent` is non-null, the method loads the FXML, obtains and
     * initializes its controller, creates a new Stage, sets the scene and shows it.
     * If `actionEvent` is null, the method logs the condition and performs no action.
     *
     * @param actionEvent the triggering ActionEvent; may be null to indicate no action
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
            }
 catch (final IOException e) {
                CategorieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }
 else {
            // Gérer le cas où actionEvent est nul
            CategorieController.LOGGER.info("actionEvent is null");
        }

    }


    /**
     * Placeholder event handler for opening or displaying the movies view.
     *
     * Currently has no implementation.
     *
     * @param actionEvent the event that triggered this handler
     */
    public void showmovies(final ActionEvent actionEvent) {
    }


    /**
     * Displays a list of products.
     *
     * @param actionEvent
     *                    event that triggered the function call.
     */
    public void showproducts(final ActionEvent actionEvent) {
    }


    /**
     * Placeholder event handler for cinema actions.
     *
     * Currently performs no action.
     */
    public void showcinema(final ActionEvent actionEvent) {
    }


    /**
     * Placeholder handler for the "event" UI action; intended to open or update the Event view but currently has no implementation.
     *
     * @param actionEvent the ActionEvent that triggered this handler
     */
    public void showevent(final ActionEvent actionEvent) {
    }


    /**
     * Handler invoked when the user activates the "series" UI control.
     *
     * @param actionEvent the event that triggered this handler; may be null
     */
    public void showseries(final ActionEvent actionEvent) {
    }

}

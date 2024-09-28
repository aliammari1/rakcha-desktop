package com.esprit.controllers.produits;

import com.esprit.models.produits.Commande;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CommandeService;
import com.esprit.services.users.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is responsible for handling user interactions related to commande data
 * display and
 * manipulation. The controller initiates the display of the commande table,
 * enables
 * cell selection, and implements a search function using an observable list.
 * Additionally, it provides a delete column and handles deletion events.
 * Finally,
 * it offers an option to open a new stage with an analysis interface for
 * further
 * examination of the command data.
 */
public class ListCommandeController {
    private static final Logger LOGGER = Logger.getLogger(ListCommandeController.class.getName());

    @FXML
    private TableColumn<Commande, String> idStatu;
    @FXML
    private TableColumn<Commande, String> idadresse;
    @FXML
    private TableColumn<Commande, Date> iddate;
    @FXML
    private TableColumn<Commande, String> idnom;
    @FXML
    private TableColumn<Commande, Integer> idnumero;
    @FXML
    private TableColumn<Commande, String> idprenom;
    @FXML
    private TableView<Commande> commandeTableView;
    @FXML
    private TextField SearchBar;
    @FXML
    private TableColumn<Commande, Void> deleteColumn;

    /**
     * Sets up events for the `SearchBar` text property, triggering the `search`
     * method
     * when the text changes. It also calls `afficheCommande()` and initializes a
     * delete
     * column.
     */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
        });
        this.afficheCommande();
        this.initDeleteColumn();
    }

    /**
     * Displays the details of a command in a table, including the client's first
     * and
     * last name, address, phone number, and order date, using a
     * `PropertyValueFactory`
     * to generate cell values based on the command object. It also sets up an
     * observable
     * list to store the commands and enables cell selection for easy navigation.
     */
    void afficheCommande() {
        this.idnom.setCellValueFactory(cellData -> {
            final Commande commande = cellData.getValue();
            final UserService userService = new UserService();
            final Client client = (Client) userService.getUserById(commande.getIdClient().getId());
            return new SimpleStringProperty(client.getFirstName());
        });
        this.idprenom.setCellValueFactory(cellData -> {
            final Commande commande = cellData.getValue();
            final UserService userService = new UserService();
            final Client client = (Client) userService.getUserById(commande.getIdClient().getId());
            return new SimpleStringProperty(client.getLastName());
        });
        this.idadresse.setCellValueFactory(new PropertyValueFactory<Commande, String>("adresse"));
        this.idnumero.setCellValueFactory(new PropertyValueFactory<Commande, Integer>("num_telephone"));
        this.iddate.setCellValueFactory(new PropertyValueFactory<Commande, Date>("dateCommande"));
        this.idStatu.setCellValueFactory(new PropertyValueFactory<Commande, String>("statu"));
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Commande> list = FXCollections.observableArrayList();
        final CommandeService ps = new CommandeService();
        list.addAll(ps.read());
        this.commandeTableView.setItems(list);
        // Activer la sélection de cellules
        this.commandeTableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Receives a keyword and filters the `Commande` objects based on their
     * addresses,
     * client names, or statuses containing the keyword. It then adds the filtered
     * objects
     * to an observable list and sets it as the items of a table view.
     *
     * @param keyword search query used to filter the list of Commands displayed in
     *                the
     *                `commandeTableView`.
     */
    @FXML
    private void search(final String keyword) {
        final CommandeService commandeservice = new CommandeService();
        final ObservableList<Commande> filteredList = FXCollections.observableArrayList();
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(commandeservice.read());
        } else {
            for (final Commande commande : commandeservice.read()) {
                if (commande.getAdresse().toLowerCase().contains(keyword.toLowerCase())
                        || commande.getIdClient().getLastName().toLowerCase().contains(keyword.toLowerCase())
                        || commande.getIdClient().getFirstName().toLowerCase().contains(keyword.toLowerCase())
                        || commande.getStatu().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(commande);
                }
            }
        }
        this.commandeTableView.setItems(filteredList);
    }

    /**
     * Sets up a custom cell factory for a delete button in a table view, which when
     * clicked deletes the item in the table and updates the view.
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Commande, Void>, TableCell<Commande, Void>> cellFactory = new Callback<>() {
            /**
             * Creates a new `TableCell` instance that displays a delete button for each
             * item in
             * the table. When the button is pressed, the corresponding item is deleted from
             * the
             * table and the table view is updated.
             *
             * @param param TableColumn object that invokes the function, providing the
             *              necessary
             *              context for the function to operate on the appropriate data.
             *
             *              - `param`: A `TableColumn<Commande, Void>` object representing
             *              the table column
             *              that the cell belongs to.
             *
             * @returns a `TableCell` object that displays a delete button for each item in
             *          the
             *          table.
             *
             *          - The output is a `TableCell` object of type `<Commande, Void>`.
             *          - The cell contains a button with a class of "delete-button".
             *          - The button has an `onAction` method that deletes the corresponding
             *          Commande
             *          object from the database when clicked.
             *          - The method also updates the TableView by removing the deleted
             *          Commande object
             *          and refreshing the view.
             */
            @Override
            public TableCell<Commande, Void> call(TableColumn<Commande, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final Commande commande = this.getTableView().getItems().get(this.getIndex());
                            final CommandeService ps = new CommandeService();
                            ps.delete(commande);
                            // Mise à jour de la TableView après la suppression de la base de données
                            ListCommandeController.this.commandeTableView.getItems().remove(commande);
                            ListCommandeController.this.commandeTableView.refresh();
                        });
                    }

                    /**
                     * Updates an item's graphic based on its emptiness, setting `null` if empty and
                     * `btnDelete` otherwise.
                     *
                     * @param item  Void object being updated, which is passed to the superclass's
                     *              `updateItem()` method and then processed further in the current
                     *              method based on
                     *              the value of the `empty` parameter.
                     *
                     *              - `item`: The item being updated, which can be either null or an
                     *              instance of `Void`.
                     *              - `empty`: A boolean indicating whether the item is empty or
                     *              not. If true, the
                     *              graphic is set to null; otherwise, it is set to `btnDelete`.
                     *
                     * @param empty whether the item being updated is empty or not, and determines
                     *              whether
                     *              the graphic of the update button should be set to `null` or
                     *              `btnDelete`.
                     */
                    @Override
                    protected void updateItem(final Void item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic(null);
                        } else {
                            this.setGraphic(this.btnDelete);
                        }
                    }
                };
            }
        };
        this.deleteColumn.setCellFactory(cellFactory);
    }

    /**
     * Loads a new FXML interface, creates a new scene and stage, and attaches the
     * scene
     * to the stage. When the stage is closed, the original stage is shown.
     *
     * @param event ActionEvent object that triggered the function execution and
     *              provides
     *              access to the source node of the event, which is the button in
     *              this case.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the event
     *              triggered by the user's
     *              action on the UI element.
     *              <p>
     *              The `event` object provides information about the source of the
     *              event, the type
     *              of event, and other details that can be used to handle the event
     *              appropriately.
     */
    @FXML
    void statCommande(final ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/AnalyseCommande.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("statisqtisue");
            stage.setOnHidden(e -> currentStage.show()); // Afficher l'ancienne fenêtre lorsque la nouvelle est fermée
            stage.show();
        } catch (final IOException e) {
            ListCommandeController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }
}

package com.esprit.controllers.produits;
import com.esprit.models.produits.Commande;
import com.esprit.models.produits.Commande;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CommandeService;
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
/**
 * Is responsible for handling user interactions related to commande data display and
 * manipulation. The controller initiates the display of the commande table, enables
 * cell selection, and implements a search function using an observable list.
 * Additionally, it provides a delete column and handles deletion events. Finally,
 * it offers an option to open a new stage with an analysis interface for further
 * examination of the command data.
 */
public class ListCommandeController {
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
     * Sets up events for the `SearchBar` text property, triggering the `search` method
     * when the text changes. It also calls `afficheCommande()` and initializes a delete
     * column.
     */
    @FXML
    void initialize() {
        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });
        afficheCommande();
        initDeleteColumn();
    }
    /**
     * Displays the details of a command in a table, including the client's first and
     * last name, address, phone number, and order date, using a `PropertyValueFactory`
     * to generate cell values based on the command object. It also sets up an observable
     * list to store the commands and enables cell selection for easy navigation.
     */
    void afficheCommande() {
        idnom.setCellValueFactory(cellData -> {
            Commande commande = cellData.getValue();
            UserService userService = new UserService();
            Client client = (Client) userService.getUserById(commande.getIdClient().getId());
            return new SimpleStringProperty(client.getFirstName());
        });
        idprenom.setCellValueFactory(cellData -> {
            Commande commande = cellData.getValue();
            UserService userService = new UserService();
            Client client = (Client) userService.getUserById(commande.getIdClient().getId());
            return new SimpleStringProperty(client.getLastName());
        });
        idadresse.setCellValueFactory(new PropertyValueFactory<Commande, String>("adresse"));
        idnumero.setCellValueFactory(new PropertyValueFactory<Commande, Integer>("num_telephone"));
        iddate.setCellValueFactory(new PropertyValueFactory<Commande, Date>("dateCommande"));
        idStatu.setCellValueFactory(new PropertyValueFactory<Commande, String>("statu"));
        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Commande> list = FXCollections.observableArrayList();
        CommandeService ps = new CommandeService();
        list.addAll(ps.read());
        commandeTableView.setItems(list);
        // Activer la sélection de cellules
        commandeTableView.getSelectionModel().setCellSelectionEnabled(true);
    }
    /**
     * Receives a keyword and filters the `Commande` objects based on their addresses,
     * client names, or statuses containing the keyword. It then adds the filtered objects
     * to an observable list and sets it as the items of a table view.
     * 
     * @param keyword search query used to filter the list of Commands displayed in the
     * `commandeTableView`.
     */
    @FXML
    private void search(String keyword) {
        CommandeService commandeservice = new CommandeService();
        ObservableList<Commande> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(commandeservice.read());
        } else {
            for (Commande commande : commandeservice.read()) {
                if (commande.getAdresse().toLowerCase().contains(keyword.toLowerCase()) ||
                        commande.getIdClient().getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                        commande.getIdClient().getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                        commande.getStatu().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(commande);
                }
            }
        }
        commandeTableView.setItems(filteredList);
    }
    /**
     * Sets up a custom cell factory for a delete button in a table view, which when
     * clicked deletes the item in the table and updates the view.
     */
    private void initDeleteColumn() {
        Callback<TableColumn<Commande, Void>, TableCell<Commande, Void>> cellFactory = new Callback<>() {
            /**
             * Creates a new `TableCell` instance that displays a delete button for each item in
             * the table. When the button is pressed, the corresponding item is deleted from the
             * table and the table view is updated.
             * 
             * @param param TableColumn object that invokes the function, providing the necessary
             * context for the function to operate on the appropriate data.
             * 
             * 	- `param`: A `TableColumn<Commande, Void>` object representing the table column
             * that the cell belongs to.
             * 
             * @returns a `TableCell` object that displays a delete button for each item in the
             * table.
             * 
             * 	- The output is a `TableCell` object of type `<Commande, Void>`.
             * 	- The cell contains a button with a class of "delete-button".
             * 	- The button has an `onAction` method that deletes the corresponding Commande
             * object from the database when clicked.
             * 	- The method also updates the TableView by removing the deleted Commande object
             * and refreshing the view.
             */
            @Override
            public TableCell<Commande, Void> call(final TableColumn<Commande, Void> param) {
                final TableCell<Commande, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");
                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            CommandeService ps = new CommandeService();
                            ps.delete(commande);
                            // Mise à jour de la TableView après la suppression de la base de données
                            commandeTableView.getItems().remove(commande);
                            commandeTableView.refresh();
                        });
                    }
                    /**
                     * Updates an item's graphic based on its emptiness, setting `null` if empty and
                     * `btnDelete` otherwise.
                     * 
                     * @param item Void object being updated, which is passed to the superclass's
                     * `updateItem()` method and then processed further in the current method based on
                     * the value of the `empty` parameter.
                     * 
                     * 	- `item`: The item being updated, which can be either null or an instance of `Void`.
                     * 	- `empty`: A boolean indicating whether the item is empty or not. If true, the
                     * graphic is set to null; otherwise, it is set to `btnDelete`.
                     * 
                     * @param empty whether the item being updated is empty or not, and determines whether
                     * the graphic of the update button should be set to `null` or `btnDelete`.
                     */
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
                return cell;
            }
        };
        deleteColumn.setCellFactory(cellFactory);
    }
    /**
     * Loads a new FXML interface, creates a new scene and stage, and attaches the scene
     * to the stage. When the stage is closed, the original stage is shown.
     * 
     * @param event ActionEvent object that triggered the function execution and provides
     * access to the source node of the event, which is the button in this case.
     * 
     * 	- `event`: An `ActionEvent` object representing the event triggered by the user's
     * action on the UI element.
     * 
     * The `event` object provides information about the source of the event, the type
     * of event, and other details that can be used to handle the event appropriately.
     */
    @FXML
    void statCommande(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AnalyseCommande.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("statisqtisue");
            stage.setOnHidden(e -> currentStage.show()); // Afficher l'ancienne fenêtre lorsque la nouvelle est fermée
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
}

package com.esprit.controllers.products;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Order;
import com.esprit.models.users.Client;
import com.esprit.services.products.OrderService;
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

/**
 * Controller class for managing and displaying orders in the RAKCHA
 * application.
 * This controller handles the display of orders in a TableView, provides search
 * functionality, and allows for order deletion and statistical analysis.
 * 
 * <p>
 * The controller initializes the table view with order data from the database,
 * configures cell factories for displaying client information, and sets up
 * event
 * handlers for user interactions with the UI.
 * </p>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ListOrderController {
    private static final Logger LOGGER = Logger.getLogger(ListOrderController.class.getName());

    @FXML
    private TableColumn<Order, String> idStatu;
    @FXML
    private TableColumn<Order, String> idadresse;
    @FXML
    private TableColumn<Order, Date> iddate;
    @FXML
    private TableColumn<Order, String> idnom;
    @FXML
    private TableColumn<Order, Integer> idnumero;
    @FXML
    private TableColumn<Order, String> idprenom;
    @FXML
    private TableView<Order> orderTableView;
    @FXML
    private TextField SearchBar;
    @FXML
    private TableColumn<Order, Void> deleteColumn;

    /**
     * Initializes the controller by setting up event listeners and populating the
     * table view.
     * 
     * <p>
     * This method configures the search bar to trigger filtering when text changes,
     * initializes the order display, and sets up the delete column functionality.
     * </p>
     */
    @FXML
    void initialize() {
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.search(newValue);
        });
        this.afficheOrder();
        this.initDeleteColumn();
    }

    /**
     * Displays orders in the table view with appropriate column mappings.
     * 
     * <p>
     * This method configures the table columns to display client information
     * (first name, last name), order address, phone number, date, and status.
     * It populates the table with data from the OrderService and enables cell
     * selection.
     * </p>
     */
    void afficheOrder() {
        this.idnom.setCellValueFactory(cellData -> {
            final Order order = cellData.getValue();
            final UserService userService = new UserService();
            final Client client = (Client) userService.getUserById(order.getClient().getId());
            return new SimpleStringProperty(client.getFirstName());
        });
        this.idprenom.setCellValueFactory(cellData -> {
            final Order order = cellData.getValue();
            final UserService userService = new UserService();
            final Client client = (Client) userService.getUserById(order.getClient().getId());
            return new SimpleStringProperty(client.getLastName());
        });
        this.idadresse.setCellValueFactory(new PropertyValueFactory<Order, String>("adresse"));
        this.idnumero.setCellValueFactory(new PropertyValueFactory<Order, Integer>("num_telephone"));
        this.iddate.setCellValueFactory(new PropertyValueFactory<Order, Date>("dateOrder"));
        this.idStatu.setCellValueFactory(new PropertyValueFactory<Order, String>("statu"));
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Order> list = FXCollections.observableArrayList();
        final OrderService ps = new OrderService();
        list.addAll(ps.read());
        this.orderTableView.setItems(list);
        // Activer la sélection de cellules
        this.orderTableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Filters the orders displayed in the table view based on a search keyword.
     * 
     * <p>
     * This method searches for orders that match the keyword in client name,
     * address, or status fields. If the keyword is empty, all orders are displayed.
     * </p>
     *
     * @param keyword The search term to filter orders by
     */
    @FXML
    private void search(final String keyword) {
        final OrderService orderservice = new OrderService();
        final ObservableList<Order> filteredList = FXCollections.observableArrayList();
        if (null == keyword || keyword.trim().isEmpty()) {
            filteredList.addAll(orderservice.read());
        } else {
            for (final Order order : orderservice.read()) {
                if (order.getAddress().toLowerCase().contains(keyword.toLowerCase())
                        || order.getClient().getLastName().toLowerCase().contains(keyword.toLowerCase())
                        || order.getClient().getFirstName().toLowerCase().contains(keyword.toLowerCase())
                        || order.getStatus().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(order);
                }
            }
        }
        this.orderTableView.setItems(filteredList);
    }

    /**
     * Initializes the delete column with a button that allows users to delete
     * orders.
     * 
     * <p>
     * This method creates a custom cell factory for the delete column, adding a
     * delete
     * button to each row that, when clicked, removes the corresponding order from
     * both
     * the database and the table view.
     * </p>
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<>() {
            /**
             * Creates a new TableCell instance with a delete button.
             *
             * @param param The TableColumn to which the cell belongs
             * @return A TableCell containing a delete button
             */
            @Override
            public TableCell<Order, Void> call(TableColumn<Order, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final Order order = this.getTableView().getItems().get(this.getIndex());
                            final OrderService ps = new OrderService();
                            ps.delete(order);
                            // Mise à jour de la TableView après la suppression de la base de données
                            ListOrderController.this.orderTableView.getItems().remove(order);
                            ListOrderController.this.orderTableView.refresh();
                        });
                    }

                    /**
                     * Updates the cell to display the delete button when the cell is not empty.
                     *
                     * @param item  The item to update
                     * @param empty Whether the cell is empty
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
     * Opens the order statistics view in a new window.
     * 
     * <p>
     * This method loads the AnalyseOrder.fxml file, creates a new scene and stage,
     * and displays it. The original stage is shown again when the statistics window
     * is closed.
     * </p>
     *
     * @param event The action event that triggered this method
     */
    @FXML
    void statOrder(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/AnalyseOrder.fxml"));
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
            ListOrderController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception d'entrée/sortie
        }
    }
}

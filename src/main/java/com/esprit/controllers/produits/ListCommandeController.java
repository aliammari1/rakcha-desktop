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
    private TableColumn<Commande,Void> deleteColumn;







    @FXML
    void initialize() {
        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });



        afficheCommande();
        initDeleteColumn();

    }

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


        @FXML
        private void search(String keyword) {
            CommandeService commandeservice = new CommandeService();
            ObservableList<Commande> filteredList = FXCollections.observableArrayList();
            if (keyword == null || keyword.trim().isEmpty()) {
                filteredList.addAll(commandeservice.read());
            } else {
                for (Commande commande : commandeservice.read()) {
                    if (
                            commande.getAdresse().toLowerCase().contains(keyword.toLowerCase())||
                            commande.getIdClient().getLastName().toLowerCase().contains(keyword.toLowerCase())||
                            commande.getIdClient().getFirstName().toLowerCase().contains(keyword.toLowerCase())||
                            commande.getStatu().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredList.add(commande);
                    }
                }
            }
            commandeTableView.setItems(filteredList);
        }


    private void initDeleteColumn() {
        Callback<TableColumn<Commande, Void>, TableCell<Commande, Void>> cellFactory = new Callback<>() {
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










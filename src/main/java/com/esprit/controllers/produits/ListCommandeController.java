package com.esprit.controllers.produits;


import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CommandeService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;


import java.net.URL;

import java.util.ResourceBundle;

public class ListCommandeController implements Initializable {

    @FXML
    private FlowPane commandeitem;

    private CommandeService commandeService = new CommandeService();
    private CommandeItemService commandeItemService = new CommandeItemService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        loadAcceptedPanier();



    }



    private void loadAcceptedPanier() {





        }




    }










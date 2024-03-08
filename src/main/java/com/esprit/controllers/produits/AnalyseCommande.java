package com.esprit.controllers.produits;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.esprit.models.produits.Commande;
import com.esprit.services.produits.CommandeService;

public class AnalyseCommande implements Initializable {

    @FXML
    private LineChart<String, Number> TauxCommande;

    @FXML
    private CategoryAxis xAxis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Remplacez ces données factices par les données réelles de votre base de données
        CommandeService commandeService = new CommandeService();
        List<Commande> commandes = commandeService.read(); // Assurez-vous que cette méthode existe dans votre service

        // Utiliser une map pour stocker le nombre de commandes par date
        Map<String, Integer> ordersByDate = new HashMap<>();

        // Compter le nombre de commandes par date
        for (Commande commande : commandes) {
            Date dateCommande = commande.getDateCommande();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(dateCommande);

            ordersByDate.put(formattedDate, ordersByDate.getOrDefault(formattedDate, 0) + 1);
        }

        // Créer une série de données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de commandes par date");

        // Ajouter les données de commande à la série
        for (Map.Entry<String, Integer> entry : ordersByDate.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Configurer l'axe X avec les dates
        ObservableList<String> dates = FXCollections.observableArrayList(ordersByDate.keySet());
        xAxis.setCategories(dates);

        // Afficher le nombre de commandes dans l'axe Y
        TauxCommande.setTitle("Nombre de commandes par date");

        // Ajouter la série au graphique
        TauxCommande.getData().add(series);
    }
}

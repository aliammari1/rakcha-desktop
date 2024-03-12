package com.esprit.controllers.produits;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import com.esprit.models.produits.Commande;
import com.esprit.models.produits.CommandeItem;
import com.esprit.models.produits.Categorie_Produit;
import com.esprit.services.produits.CommandeService;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CategorieService;

public class AnalyseCommande implements Initializable {

    @FXML
    private LineChart<String, Number> TauxCommande;

    @FXML
    private StackedBarChart<String, Number> TauxCategorie;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private CategoryAxis xCommandeAxis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Appeler la logique d'initialisation
        updateGraphs();
    }


    private void updateGraphs() {
        // Mettre à jour les graphiques ici
        TauxCommande.getData().clear();
        TauxCategorie.getData().clear();

        CommandeService commandeService = new CommandeService();
        List<Commande> commandes = commandeService.read();

        CommandeItemService commandeItemService = new CommandeItemService();
        List<CommandeItem> commandeItems = commandeItemService.read();

        CategorieService categorieService = new CategorieService();
        List<Categorie_Produit> categories = categorieService.read();


        Map<String, Integer> enCoursByDate = new HashMap<>();
        Map<String, Integer> payeeByDate = new HashMap<>();
        Map<String, Map<String, Integer>> categoriesAchatsByDate = new HashMap<>();
        Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();

        // Remplir les maps avec toutes les dates possibles
        for (Commande commande : commandes) {
            Date dateCommande = commande.getDateCommande();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(dateCommande);

            enCoursByDate.put(formattedDate, 0);
            payeeByDate.put(formattedDate, 0);

            categoriesAchatsByDate.put(formattedDate, new HashMap<>());
            for (Categorie_Produit categorie : categories) {
                categoriesAchatsByDate.get(formattedDate).put(categorie.getNom_categorie(), 0);
            }
        }

        // Compter le nombre de commandes par date et par statut
        for (Commande commande : commandes) {
            Date dateCommande = commande.getDateCommande();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(dateCommande);

            if ("en cours".equalsIgnoreCase(commande.getStatu())) {
                enCoursByDate.put(formattedDate, enCoursByDate.get(formattedDate) + 1);
            } else if ("payee".equalsIgnoreCase(commande.getStatu())) {
                payeeByDate.put(formattedDate, payeeByDate.get(formattedDate) + 1);

                // Analyse des catégories achetées
                Map<String, Integer> categoriesAchats = categoriesAchatsByDate.get(formattedDate);
                for (CommandeItem commandeItem : commandeItems) {
                    if (commandeItem.getCommande().getIdCommande() == commande.getIdCommande()) {
                        String categorie = commandeItem.getProduit().getCategorie().getNom_categorie();
                        categoriesAchats.put(categorie, categoriesAchats.get(categorie) + 1);
                    }
                }

            }
        }

        ObservableList<String> dates = FXCollections.observableArrayList(enCoursByDate.keySet());

        // Configurer l'axe X avec les dates
        xAxis.setCategories(dates);

        XYChart.Series<String, Number> enCoursSeries = new XYChart.Series<>();
        enCoursSeries.setName("Commandes en cours");

        XYChart.Series<String, Number> payeeSeries = new XYChart.Series<>();
        payeeSeries.setName("Commandes payées");

        for (String date : dates) {
            enCoursSeries.getData().add(new XYChart.Data<>(date, enCoursByDate.get(date)));
            payeeSeries.getData().add(new XYChart.Data<>(date, payeeByDate.get(date)));
        }

        TauxCommande.setTitle("Nombre de commandes par date");

        // Ajouter les séries au graphique
        TauxCommande.getData().addAll(enCoursSeries, payeeSeries);

        // Configurer la StackedBarChart pour les catégories
        for (Categorie_Produit categorie : categories) {
            // Créer la série avec le nom de la catégorie
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(categorie.getNom_categorie());
            seriesMap.put(categorie.getNom_categorie(), serie);
        }

        // Parcourir les dates et ajouter les données aux séries
        for (Map.Entry<String, Map<String, Integer>> entry : categoriesAchatsByDate.entrySet()) {
            String date = entry.getKey();

            for (Map.Entry<String, Integer> categorieEntry : entry.getValue().entrySet()) {
                String categorie = categorieEntry.getKey();
                int nombreAchats = categorieEntry.getValue();

                seriesMap.get(categorie).getData().add(new XYChart.Data<>(date, nombreAchats));
            }
        }

        // Afficher les séries sur le graphique
        ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList(seriesMap.values());
        TauxCategorie.getData().addAll(seriesList);

        // Configuration des axes
        xAxis.setCategories(FXCollections.observableArrayList(categoriesAchatsByDate.keySet()));
        TauxCategorie.getXAxis().setLabel("Date de commande");
        TauxCategorie.getYAxis().setLabel("Nombre d'achats");
        TauxCategorie.setTitle("Nombre de catégories achetées par date");
    }
}

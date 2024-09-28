package com.esprit.controllers.produits;

import com.esprit.models.produits.Categorie_Produit;
import com.esprit.models.produits.Commande;
import com.esprit.models.produits.CommandeItem;
import com.esprit.services.produits.CategorieService;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Is used to analyze the number of orders placed by date and categorize them based
 * on the products purchased. It updates two graphical representations: TauxCommande
 * (number of orders per date) and TauxCategorie (number of categories purchased per
 * date). The class retrieves data from various sources, including a list of all
 * orders, a list of all products, and a list of all categories. It then processes
 * the data to create the graphs and displays them on the screen.
 */
public class AnalyseCommande implements Initializable {
    @FXML
    private LineChart<String, Number> TauxCommande;
    @FXML
    private StackedBarChart<String, Number> TauxCategorie;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private CategoryAxis xCommandeAxis;

    /**
     * Is responsible for calling the initialization logic, which in turn updates the graphs.
     *
     * @param location  URL of the initial graph layout, which is passed to the `updateGraphs()`
     *                  method for initialization.
     * @param resources resource bundle that provides localized strings and values for
     *                  the application.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Appeler la logique d'initialisation
        this.updateGraphs();
    }

    /**
     * Updates a bar graph and a stacked bar chart with the number of orders, payments,
     * and categories purchased by date. It retrieves data from a database and calculates
     * the number of orders and payments for each category by date.
     */
    private void updateGraphs() {
        // Mettre à jour les graphiques ici
        this.TauxCommande.getData().clear();
        this.TauxCategorie.getData().clear();
        final CommandeService commandeService = new CommandeService();
        final List<Commande> commandes = commandeService.read();
        final CommandeItemService commandeItemService = new CommandeItemService();
        final List<CommandeItem> commandeItems = commandeItemService.read();
        final CategorieService categorieService = new CategorieService();
        final List<Categorie_Produit> categories = categorieService.read();
        final Map<String, Integer> enCoursByDate = new HashMap<>();
        final Map<String, Integer> payeeByDate = new HashMap<>();
        final Map<String, Map<String, Integer>> categoriesAchatsByDate = new HashMap<>();
        final Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        // Remplir les maps avec toutes les dates possibles
        for (final Commande commande : commandes) {
            final Date dateCommande = commande.getDateCommande();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String formattedDate = dateFormat.format(dateCommande);
            enCoursByDate.put(formattedDate, 0);
            payeeByDate.put(formattedDate, 0);
            categoriesAchatsByDate.put(formattedDate, new HashMap<>());
            for (final Categorie_Produit categorie : categories) {
                categoriesAchatsByDate.get(formattedDate).put(categorie.getNom_categorie(), 0);
            }
        }
        // Compter le nombre de commandes par date et par statut
        for (final Commande commande : commandes) {
            final Date dateCommande = commande.getDateCommande();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String formattedDate = dateFormat.format(dateCommande);
            if ("en cours".equalsIgnoreCase(commande.getStatu())) {
                enCoursByDate.put(formattedDate, enCoursByDate.get(formattedDate) + 1);
            } else if ("payee".equalsIgnoreCase(commande.getStatu())) {
                payeeByDate.put(formattedDate, payeeByDate.get(formattedDate) + 1);
                // Analyse des catégories achetées
                final Map<String, Integer> categoriesAchats = categoriesAchatsByDate.get(formattedDate);
                for (final CommandeItem commandeItem : commandeItems) {
                    if (commandeItem.getCommande().getIdCommande() == commande.getIdCommande()) {
                        final String categorie = commandeItem.getProduit().getCategorie().getNom_categorie();
                        categoriesAchats.put(categorie, categoriesAchats.get(categorie) + 1);
                    }
                }
            }
        }
        final ObservableList<String> dates = FXCollections.observableArrayList(enCoursByDate.keySet());
        // Configurer l'axe X avec les dates
        this.xAxis.setCategories(dates);
        final XYChart.Series<String, Number> enCoursSeries = new XYChart.Series<>();
        enCoursSeries.setName("Commandes en cours");
        final XYChart.Series<String, Number> payeeSeries = new XYChart.Series<>();
        payeeSeries.setName("Commandes payees");
        for (final String date : dates) {
            enCoursSeries.getData().add(new XYChart.Data<>(date, enCoursByDate.get(date)));
            payeeSeries.getData().add(new XYChart.Data<>(date, payeeByDate.get(date)));
        }
        this.TauxCommande.setTitle("Nombre de commandes par date");
        // Ajouter les séries au graphique
        this.TauxCommande.getData().addAll(enCoursSeries, payeeSeries);
        // Configurer la StackedBarChart pour les catégories
        for (final Categorie_Produit categorie : categories) {
            // Créer la série avec le nom de la catégorie
            final XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(categorie.getNom_categorie());
            seriesMap.put(categorie.getNom_categorie(), serie);
        }
        // Parcourir les dates et ajouter les données aux séries
        for (final Map.Entry<String, Map<String, Integer>> entry : categoriesAchatsByDate.entrySet()) {
            final String date = entry.getKey();
            for (final Map.Entry<String, Integer> categorieEntry : entry.getValue().entrySet()) {
                final String categorie = categorieEntry.getKey();
                final int nombreAchats = categorieEntry.getValue();
                seriesMap.get(categorie).getData().add(new XYChart.Data<>(date, nombreAchats));
            }
        }
        // Afficher les séries sur le graphique
        final ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList(seriesMap.values());
        this.TauxCategorie.getData().addAll(seriesList);
        // Configuration des axes
        this.xAxis.setCategories(FXCollections.observableArrayList(categoriesAchatsByDate.keySet()));
        this.TauxCategorie.getXAxis().setLabel("Date de commande");
        this.TauxCategorie.getYAxis().setLabel("Nombre d'achats");
        this.TauxCategorie.setTitle("Nombre de catégories achetées par date");
    }
}

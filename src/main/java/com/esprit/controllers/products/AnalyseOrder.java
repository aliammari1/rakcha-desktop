package com.esprit.controllers.products;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.ProductCategory;
import com.esprit.services.products.CategoryService;
import com.esprit.services.products.OrderItemService;
import com.esprit.services.products.OrderService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

/**
 * Is used to analyze the number of orders placed by date and categorize them
 * based on the products purchased. It updates two graphical representations:
 * TauxOrder (number of orders per date) and TauxCategorie (number of categories
 * purchased per date). The class retrieves data from various sources, including
 * a list of all orders, a list of all products, and a list of all categories.
 * It then processes the data to create the graphs and displays them on the
 * screen.
 */
public class AnalyseOrder implements Initializable {
    @FXML
    private LineChart<String, Number> TauxOrder;
    @FXML
    private StackedBarChart<String, Number> TauxCategorie;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private CategoryAxis xOrderAxis;

    /**
     * Is responsible for calling the initialization logic, which in turn updates
     * the graphs.
     *
     * @param location
     *            URL of the initial graph layout, which is passed to the
     *            `updateGraphs()` method for initialization.
     * @param resources
     *            resource bundle that provides localized strings and values for the
     *            application.
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        // Appeler la logique d'initialisation
        this.updateGraphs();
    }

    /**
     * Updates a bar graph and a stacked bar chart with the number of orders,
     * payments, and categories purchased by date. It retrieves data from a database
     * and calculates the number of orders and payments for each category by date.
     */
    private void updateGraphs() {
        // Mettre à jour les graphiques ici
        this.TauxOrder.getData().clear();
        this.TauxCategorie.getData().clear();
        final OrderService orderService = new OrderService();
        final List<Order> orders = orderService.read();
        final OrderItemService orderItemService = new OrderItemService();
        final List<OrderItem> orderItems = orderItemService.read();
        final CategoryService categoryService = new CategoryService();
        final List<ProductCategory> categories = categoryService.read();
        final Map<String, Integer> enCoursByDate = new HashMap<>();
        final Map<String, Integer> payeeByDate = new HashMap<>();
        final Map<String, Map<String, Integer>> categoriesAchatsByDate = new HashMap<>();
        final Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        // Remplir les maps avec toutes les dates possibles
        for (final Order order : orders) {
            final Date dateOrder = order.getOrderDate();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String formattedDate = dateFormat.format(dateOrder);
            enCoursByDate.put(formattedDate, 0);
            payeeByDate.put(formattedDate, 0);
            categoriesAchatsByDate.put(formattedDate, new HashMap<>());
            for (final ProductCategory categorie : categories) {
                categoriesAchatsByDate.get(formattedDate).put(categorie.getCategoryName(), 0);
            }
        }
        // Compter le nombre de orders par date et par statut
        for (final Order order : orders) {
            final Date dateOrder = order.getOrderDate();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String formattedDate = dateFormat.format(dateOrder);
            if ("en cours".equalsIgnoreCase(order.getStatus())) {
                enCoursByDate.put(formattedDate, enCoursByDate.get(formattedDate) + 1);
            } else if ("payee".equalsIgnoreCase(order.getStatus())) {
                payeeByDate.put(formattedDate, payeeByDate.get(formattedDate) + 1);
                // Analyse des catégories achetées
                final Map<String, Integer> categoriesAchats = categoriesAchatsByDate.get(formattedDate);
                for (final OrderItem orderItem : orderItems) {
                    if (orderItem.getOrder().getId() == order.getId()) {
                        final String categorie = orderItem.getProduct().getCategories().get(0).getCategoryName();
                        categoriesAchats.put(categorie, categoriesAchats.get(categorie) + 1);
                    }
                }
            }
        }
        final ObservableList<String> dates = FXCollections.observableArrayList(enCoursByDate.keySet());
        // Configurer l'axe X avec les dates
        this.xAxis.setCategories(dates);
        final XYChart.Series<String, Number> enCoursSeries = new XYChart.Series<>();
        enCoursSeries.setName("Orders en cours");
        final XYChart.Series<String, Number> payeeSeries = new XYChart.Series<>();
        payeeSeries.setName("Orders payees");
        for (final String date : dates) {
            enCoursSeries.getData().add(new XYChart.Data<>(date, enCoursByDate.get(date)));
            payeeSeries.getData().add(new XYChart.Data<>(date, payeeByDate.get(date)));
        }
        this.TauxOrder.setTitle("Nombre de orders par date");
        // Ajouter les séries au graphique
        this.TauxOrder.getData().addAll(enCoursSeries, payeeSeries);
        // Configurer la StackedBarChart pour les catégories
        for (final ProductCategory categorie : categories) {
            // Créer la série avec le nom de la catégorie
            final XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(categorie.getCategoryName());
            seriesMap.put(categorie.getCategoryName(), serie);
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
        final ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections
                .observableArrayList(seriesMap.values());
        this.TauxCategorie.getData().addAll(seriesList);
        // Configuration des axes
        this.xAxis.setCategories(FXCollections.observableArrayList(categoriesAchatsByDate.keySet()));
        this.TauxCategorie.getXAxis().setLabel("Date de order");
        this.TauxCategorie.getYAxis().setLabel("Nombre d'achats");
        this.TauxCategorie.setTitle("Nombre de catégories achetées par date");
    }
}

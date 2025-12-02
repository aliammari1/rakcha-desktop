package com.esprit.controllers.products;

import com.esprit.models.common.Category;
import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.services.common.CategoryService;
import com.esprit.services.products.OrderItemService;
import com.esprit.services.products.OrderService;
import com.esprit.utils.PageRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller class for analyzing orders and visualizing order data in the
 * RAKCHA application.
 * This controller provides graphical representations of order statistics
 * including:
 * - Number of orders by date (completed vs. in progress)
 * - Products purchased by category over time
 *
 * <p>
 * The controller uses LineChart and StackedBarChart components to display the
 * data
 * and retrieves information from OrderService, OrderItemService, and
 * CategoryService.
 * </p>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
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
     * Populate the controller's charts with current order and category statistics.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
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
     * Refreshes the order and category charts using current data from services.
     *
     * <p>Clears both charts and repopulates them with date-grouped counts for orders
     * ("en cours" and "payee") and per-category purchase counts for paid orders.</p>
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
        final List<Category> categories = categoryService.read(new PageRequest(0, 10)).getContent();
        final Map<String, Integer> enCoursByDate = new HashMap<>();
        final Map<String, Integer> payeeByDate = new HashMap<>();
        final Map<String, Map<String, Integer>> categoriesAchatsByDate = new HashMap<>();
        final Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        // Remplir les maps avec toutes les dates possibles
        for (final Order order : orders) {
            final java.time.LocalDateTime dateOrder = order.getOrderDate();
            final String formattedDate = dateOrder.toLocalDate().toString();
            enCoursByDate.put(formattedDate, 0);
            payeeByDate.put(formattedDate, 0);
            categoriesAchatsByDate.put(formattedDate, new HashMap<>());
            for (final Category categorie : categories) {
                categoriesAchatsByDate.get(formattedDate).put(categorie.getName(), 0);
            }

        }

        // Compter le nombre de orders par date et par statut
        for (final Order order : orders) {
            final java.time.LocalDateTime dateOrder = order.getOrderDate();
            final String formattedDate = dateOrder.toLocalDate().toString();
            if ("en cours".equalsIgnoreCase(order.getStatus())) {
                enCoursByDate.put(formattedDate, enCoursByDate.get(formattedDate) + 1);
            } else if ("payee".equalsIgnoreCase(order.getStatus())) {
                payeeByDate.put(formattedDate, payeeByDate.get(formattedDate) + 1);
                // Analyse des catégories achetées
                final Map<String, Integer> categoriesAchats = categoriesAchatsByDate.get(formattedDate);
                for (final OrderItem orderItem : orderItems) {
                    if (orderItem.getOrder().getId() == order.getId()) {
                        final String categorie = orderItem.getProduct().getCategories().get(0).getName();
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
        for (final Category categorie : categories) {
            // Créer la série avec le nom de la catégorie
            final XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(categorie.getName());
            seriesMap.put(categorie.getName(), serie);
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

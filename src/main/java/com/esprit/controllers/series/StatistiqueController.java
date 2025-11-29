package com.esprit.controllers.series;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * Manages user interaction with the FXML file "/ui//ui/StatistiqueView.fxml".
 * It initializes and populates a BarChart, PieChart, or BorderPane depending on
 * the selected category from the ComboBox. The controller also handles events
 * such as closing the application and updating the PieChart data. Additionally,
 * it provides methods for showing the BarChart and PieChart graphs.
 */
public class StatistiqueController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(StatistiqueController.class.getName());
    @FXML
    private ComboBox<String> statisticsComboBox;
    private Map<Category, Long> statistics = new IServiceCategorieImpl().getCategoriesStatistics(); // Ajout de données
    @FXML
    private BorderPane borderPane;

    /**
     * @param statistics
     * @param limit
     * @return BarChart<String, Number>
     */
    // Méthode pour créer un BarChart avec une limite de catégories

    /**
         * Create a bar chart displaying category counts and show at most the specified number of categories.
         *
         * The returned chart contains one bar per category (category name on the x axis and count on the y axis).
         * Each bar has a tooltip showing "category: count".
         *
         * @param statistics map from Category to its count; if null the chart will contain no bars
         * @param limit      maximum number of categories to include in the chart
         * @return           a BarChart<String, Number> whose series contains up to `limit` category count entries
         */
    private BarChart<String, Number> createBarChart(final Map<Category, Long> statistics, final int limit) {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categorys");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Classification of categorys");
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        final XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        // Vérifier si les statistiques ne sont pas nulles
        if (null != statistics) {
            int count = 0;
            for (final Map.Entry<Category, Long> entry : statistics.entrySet()) {
                final Category category = entry.getKey();
                final String categoryName = String.valueOf(category.getName()); // Extraire le nom de la catégorie
                final Long numberOfCategorys = entry.getValue();
                // Ajouter des données uniquement si le compteur est dans la limite spécifiée
                if (count < limit) {
                    // Ajouter des données directement au graphique en barres
                    dataSeries.getData().add(new XYChart.Data<>(categoryName, numberOfCategorys));
                    // Ajouter une infobulle aux données
                    final XYChart.Data<String, Number> data = dataSeries.getData().get(dataSeries.getData().size() - 1);
                    final Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
                    Tooltip.install(data.getNode(), tooltip);
                    count++;
                }
 else {
                    break; // Interrompre la boucle si la limite est atteinte
                }

            }

        }

        barChart.getData().add(dataSeries);
        return barChart;
    }


    /**
     * @param statistics
     * @param limit
     */
    // Méthode pour afficher le BarChart avec une limite de catégories

    /**
     * Displays a bar chart of category counts and places it in the controller's center.
     *
     * Builds a BarChart from the provided map, installs a tooltip on each bar showing "category: count",
     * and limits the number of bars to {@code limit}.
     *
     * @param statistics map of Category to count used to populate the chart; entries beyond {@code limit} are ignored
     * @param limit maximum number of categories (bars) to display
     */
    private void showBarChart(final Map<Category, Long> statistics, final int limit) {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categorys");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Classification of categorys");
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        final XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        int count = 0;
        for (final Map.Entry<Category, Long> entry : statistics.entrySet()) {
            final Category category = entry.getKey();
            final String categoryName = String.valueOf(category.getName()); // Extraire le nom de la catégorie
            final Long numberOfCategorys = entry.getValue();
            // Ajouter des données uniquement si le compteur est dans la limite spécifiée
            if (count < limit) {
                // Ajouter des données directement au graphique en barres
                dataSeries.getData().add(new XYChart.Data<>(categoryName, numberOfCategorys));
                // Ajouter une infobulle aux données
                final XYChart.Data<String, Number> data = dataSeries.getData().get(dataSeries.getData().size() - 1);
                final Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
                Tooltip.install(data.getNode(), tooltip);
                count++;
            }
 else {
                break; // Interrompre la boucle si la limite est atteinte
            }

        }

        barChart.getData().add(dataSeries);
        this.borderPane.setCenter(barChart);
    }


    /**
     * Configure the controller UI: populate the statistics ComboBox, load category statistics, and show a bar chart (up to 20 categories) in the center pane.
     *
     * Loads category statistics from IServiceCategorieImpl and copies them into the controller's statistics map if present; creates and places a bar chart limited to 20 categories.
     *
     * @param url            location used to resolve relative paths for the root object, may be null
     * @param resourceBundle resources used to localize the root object, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        // Initialiser le ComboBox ici
        final ObservableList<String> statisticsOptions = FXCollections.observableArrayList("Statistiques of category");
        this.statisticsComboBox.setItems(statisticsOptions);
        this.statisticsComboBox.setValue("Statistiques of category");
        final IServiceCategorieImpl serviceCategory = new IServiceCategorieImpl();
        // S'assurer que les statistiques ne sont pas nulles avant de les utiliser
        final Map<Category, Long> statistiques = serviceCategory.getCategoriesStatistics();
        if (null != statistiques) {
            this.statistics = new HashMap<>(statistiques.size());
            for (final Map.Entry<Category, Long> entry : statistiques.entrySet()) {
                this.statistics.put(entry.getKey(), entry.getValue());
            }

        }

        // Initialiser le graphique en barres avec une limite de 20 catégories
        final BarChart<String, Number> barChart = this.createBarChart(statistiques, 20);
        this.borderPane.setCenter(barChart);
    }


    /**
     * Display category statistics as a bar chart when the combo box selection equals "Statistiques of category".
     *
     * Retrieves current category statistics and sets a bar chart (limited to 20 entries) into the center of the border pane.
     */
    @FXML
    private void handleShowStatistics() {
        if ("Statistiques of category".equals(this.statisticsComboBox.getValue())) {
            // Afficher les statistiques de catégorie
            final Map<Category, Long> statistiques = new IServiceCategorieImpl().getCategoriesStatistics();
            this.borderPane.setCenter(this.createBarChart(statistiques, 20));
        }

    }


    /**
     * Show the controller's bar chart view using the current statistics limited to 20 categories.
     */
    @FXML
    private void handleShowBarChart() {
        this.showBarChart(this.statistics, 20);
    }


    // ... (Les autres méthodes restent inchangées)

    /**
     * Load category statistics, log the retrieved data, and display them as a pie chart.
     *
     * @param event the UI action event that triggered this handler
     */
    @FXML
    private void handleShowPieChart(final ActionEvent event) {
        // Récupérer les statistiques des catégories
        final Map<Category, Long> statistiques = new IServiceCategorieImpl().getCategoriesStatistics();
        // Ajouter un log pour vérifier les statistiques
        StatistiqueController.LOGGER.info("Statistiques : " + statistiques);
        // Créer et afficher le graphique en secteurs
        this.showPieChart(statistiques);
    }


    // Méthode pour afficher le graphique en secteurs

    /**
     * Display a PieChart in the controller's center using the provided category counts.
     *
     * @param statistics map associating each Category with its count; each entry becomes a pie slice labeled by the category's name and sized by its count
     */
    private void showPieChart(final Map<Category, Long> statistics) {
        final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (final Map.Entry<Category, Long> entry : statistics.entrySet()) {
            final Category category = entry.getKey();
            final String categoryName = String.valueOf(category.getName()); // Extraire le nom de la catégorie
            final Long numberOfCategorys = entry.getValue();
            pieChartData.add(new PieChart.Data(categoryName, numberOfCategorys));
        }

        final PieChart pieChart = new PieChart(pieChartData);
        this.createToolTips(pieChart);
        pieChart.setTitle("Statistiques of  categorys");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setStartAngle(180);
        pieChartData.forEach(
                data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty(), " ")));
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem miSwitchToBarChart = new MenuItem("Switch to Bar Chart");
        contextMenu.getItems().add(miSwitchToBarChart);
        pieChart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (MouseButton.SECONDARY == event.getButton()) {
                contextMenu.show(pieChart, event.getScreenX(), event.getScreenY());
            }

        }
);
        miSwitchToBarChart.setOnAction(event -> this.showBarChart(statistics));
        this.borderPane.setCenter(pieChart);
    }


    /**
     * Displays the provided category-to-count mapping as a bar chart placed in the controller's center BorderPane.
     *
     * @param statistics mapping from Category to its count used to build and render the bar chart; may be null or empty
     */
    private void showBarChart(final Map<Category, Long> statistics) {
    }


    /**
     * Attaches a tooltip to each slice of the given PieChart that displays the slice's current numeric value and updates the tooltip text when the value changes.
     *
     * @param pc the PieChart whose slices will receive tooltips showing each slice's current value
     */
    private void createToolTips(final PieChart pc) {
        for (final PieChart.Data data : pc.getData()) {
            final String msg = Double.toString(data.getPieValue());
            final Tooltip tp = new Tooltip(msg);
            tp.setShowDelay(Duration.seconds(0));
            Tooltip.install(data.getNode(), tp);
            data.pieValueProperty().addListener((observable, oldValue, newValue) -> {
                tp.setText(newValue.toString());
            }
);
        }

    }


    /**
     * Exit the application immediately.
     *
     * @param actionEvent the event that triggered this handler (ignored)
     */
    public void handleClose(final ActionEvent actionEvent) {
        System.exit(0);
    }


    /**
     * Increases the third slice's value in the center PieChart by 10% and refreshes its tooltips.
     *
     * If the center node of the border pane is not a PieChart or has fewer than three slices,
     * the method performs no action.
     */
    public void handleUpdatePieData(final ActionEvent actionEvent) {
        final Node node = this.borderPane.getCenter();
        if (node instanceof final PieChart pc) {
            final double value = pc.getData().get(2).getPieValue();
            pc.getData().get(2).setPieValue(value * 1.10);
            this.createToolTips(pc);
        }

    }


    /**
     * No-op initialization method retained for lifecycle compatibility.
     *
     * <p>This method intentionally performs no action.</p>
     */
    public void initialize() {
    }

}
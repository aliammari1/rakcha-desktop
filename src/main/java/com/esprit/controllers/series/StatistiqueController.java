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
     * Creates a bar chart based on category-related data, limiting the number of
     * categorys displayed to a specified value. It generates XY chart data series
     * and installs tooltips for each data point.
     *
     * @param statistics
     *                   map of categorys and their corresponding number of
     *                   occurrences,
     *                   which is used to generate the data series for the bar
     *                   chart.
     *                   <p>
     *                   - `statistics`: a map containing category-wise statistical
     *                   data,
     *                   where each key is a category and the value is the number of
     *                   occurrences of that category.
     * @param limit
     *                   maximum number of categorys to be plotted on the graph, and
     *                   it is
     *                   used to control the amount of data added to the chart
     *                   through the
     *                   `XYChart.Data` array.
     * @returns a Bar Chart representing the number of categorys in a given set of
     *          statistics, with each category represented by a bar.
     *          <p>
     *          - `BarChart<String, Number> barChart`: A chart object that
     *          represents a bar chart with two axes - a category axis and a
     *          numerical axis. - `XYChart.Series<String, Number> dataSeries`: A
     *          series of data points represented as bars on the chart, where each
     *          point is associated with a category name and a corresponding number
     *          value. - `int count`: The number of data points added to the chart,
     *          which represents the number of categorys that have been classified.
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
     * Creates a bar chart from a map of category-related data, limiting the number
     * of categorys displayed based on an input parameter. It adds data points to
     * the chart and displays tooltips for each point.
     *
     * @param statistics
     *                   Map containing statistics of categorys, where each key is a
     *                   category and value is the number of occurrences of that
     *                   category.
     *                   <p>
     *                   - `Map<Category, Long> statistics`: A map containing
     *                   categorys as
     *                   keys and their corresponding number of occurrences as
     *                   values.
     * @param limit
     *                   maximum number of categorys to be displayed on the bar
     *                   chart, and
     *                   it is used to control the number of data points added to
     *                   the graph
     *                   during the loop.
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
     * Initializes a ComboBox with category options, sets the selected option to
     * "Statistiques of category", and retrieves and maps category statistics from
     * an service implementation to display in a bar chart with a limit of 20
     * categorys.
     *
     * @param url
     *                       URL of a resource that provides the statistics data to
     *                       be
     *                       displayed in the graphical user interface.
     *                       <p>
     *                       - Type: URL representing a web page with category
     *                       statistics data.
     * @param resourceBundle
     *                       application's resource bundle, which contains key-value
     *                       pairs of
     *                       localized messages and resources that can be used to
     *                       display text
     *                       and other data to the user.
     *                       <p>
     *                       - `resourceBundle`: A ResourceBundle object containing
     *                       key-value
     *                       pairs representing category names and their
     *                       corresponding
     *                       statistics.
     *                       <p>
     *                       Note: The `resourceBundle` is not explicitly mentioned
     *                       in the code
     *                       snippet provided, but it is implied based on the
     *                       context of the
     *                       function.
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
     * Handles the display of category statistics when the user selects
     * "Statistiques de catégorie" from the combo box. It retrieves the category
     * statistics from an implementation of an interface `IServiceCategoryImpl`,
     * maps them to a bar chart, and displays the chart in the center of the border
     * pane.
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
     * Displays a bar chart for statistics passed as an argument and with a
     * specified limit of 20.
     */
    @FXML
    private void handleShowBarChart() {
        this.showBarChart(this.statistics, 20);
    }


    // ... (Les autres méthodes restent inchangées)

    /**
     * 1) retrieves category statistics from an implementation class, 2) prints the
     * statistics to the console, and 3) creates and displays a pie chart
     * representing the data using the `showPieChart()` method.
     *
     * @param event
     *              occurrence of a button press event that triggers the execution
     *              of
     *              the function.
     *              <p>
     *              - Event type: `ActionEvent` - Target object: Undefined (as
     *              `event`
     *              is not referenced directly in the function body)
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
     * Generates a pie chart based on map data, adds tooltips and menu items to the
     * chart, and displays it in the center of a BorderPane.
     *
     * @param statistics
     *                   map of categorys and their corresponding counts, which is
     *                   used to
     *                   populate the data list for the pie chart.
     *                   <p>
     *                   - `Map<Category, Long> statistics`: A map that associates a
     *                   category with its count. The categorys are identified by
     *                   `Category`, and the count is represented by a long value.
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
     * Takes a map of category-specific count data and displays it as a bar chart.
     *
     * @param statistics
     *                   map of category-specific long values that will be displayed
     *                   as
     *                   bars on the bar chart.
     */
    private void showBarChart(final Map<Category, Long> statistics) {
    }


    /**
     * Generates and installs tooltips for each data point in a pie chart, updating
     * the text within the tooltip whenever the pie value changes.
     *
     * @param pc
     *           PieChart object that the function operates on, providing the data
     *           and nodes to create tooltips for.
     *           <p>
     *           - `PieChart`: Represents a pie chart component - `getData()`:
     *           Returns a list of data points for the pie chart -
     *           `pieValueProperty()`: A property that holds the value of each data
     *           point as a double value.
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
     * Terminates the application by calling `System.exit(0)`.
     *
     * @param actionEvent
     *                    event that triggered the `handleClose()` function to
     *                    execute, and
     *                    it is passed as an argument to the function for further
     *                    processing
     *                    or handling.
     */
    public void handleClose(final ActionEvent actionEvent) {
        System.exit(0);
    }


    /**
     * Updates the value of a pie chart by multiplying its current value by 1.10 and
     * setting it back to the chart. Additionally, it creates tooltips for the
     * chart.
     *
     * @param actionEvent
     *                    event that triggered the `handleUpdatePieData()` method to
     *                    be
     *                    executed.
     *                    <p>
     *                    - `actionEvent` represents an event related to a user
     *                    interaction
     *                    with the application. - It is an instance of the
     *                    `ActionEvent`
     *                    class in Java.
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
     * Initializes an unspecified object or system.
     */
    public void initialize() {
    }

}


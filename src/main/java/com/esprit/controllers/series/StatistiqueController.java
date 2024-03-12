package com.esprit.controllers.series;

import com.esprit.models.series.Categorie;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class StatistiqueController implements Initializable {
    @FXML
    private ComboBox<String> statisticsComboBox;
    private Map<Categorie, Long> statistics = new IServiceCategorieImpl().getCategoriesStatistics(); // Ajout de données par défaut

    @FXML
    private BorderPane borderPane;

    // Méthode pour créer un BarChart avec une limite de catégories
    private BarChart<String, Number> createBarChart(Map<Categorie, Long> statistics, int limit) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categories");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Classification of categories");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        // Vérifier si les statistiques ne sont pas nulles
        if (statistics != null) {
            int count = 0;
            for (Map.Entry<Categorie, Long> entry : statistics.entrySet()) {
                Categorie categorie = entry.getKey();
                String categoryName = String.valueOf(categorie.getNom()); // Extraire le nom de la catégorie
                Long numberOfCategories = entry.getValue();

                // Ajouter des données uniquement si le compteur est dans la limite spécifiée
                if (count < limit) {
                    // Ajouter des données directement au graphique en barres
                    dataSeries.getData().add(new XYChart.Data<>(categoryName, numberOfCategories));

                    // Ajouter une infobulle aux données
                    XYChart.Data<String, Number> data = dataSeries.getData().get(dataSeries.getData().size() - 1);
                    Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
                    Tooltip.install(data.getNode(), tooltip);

                    count++;
                } else {
                    break; // Interrompre la boucle si la limite est atteinte
                }
            }
        }

        barChart.getData().add(dataSeries);

        return barChart;
    }

    // Méthode pour afficher le BarChart avec une limite de catégories
    private void showBarChart(Map<Categorie, Long> statistics, int limit) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categories");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Classification of categories");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        int count = 0;
        for (Map.Entry<Categorie, Long> entry : statistics.entrySet()) {
            Categorie categorie = entry.getKey();
            String categoryName = String.valueOf(categorie.getNom()); // Extraire le nom de la catégorie
            Long numberOfCategories = entry.getValue();

            // Ajouter des données uniquement si le compteur est dans la limite spécifiée
            if (count < limit) {
                // Ajouter des données directement au graphique en barres
                dataSeries.getData().add(new XYChart.Data<>(categoryName, numberOfCategories));

                // Ajouter une infobulle aux données
                XYChart.Data<String, Number> data = dataSeries.getData().get(dataSeries.getData().size() - 1);
                Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
                Tooltip.install(data.getNode(), tooltip);

                count++;
            } else {
                break; // Interrompre la boucle si la limite est atteinte
            }
        }

        barChart.getData().add(dataSeries);

        borderPane.setCenter(barChart);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser le ComboBox ici
        ObservableList<String> statisticsOptions = FXCollections.observableArrayList("Statistiques of categorie");
        statisticsComboBox.setItems(statisticsOptions);
        statisticsComboBox.setValue("Statistiques of categorie");

        IServiceCategorieImpl serviceCategorie = new IServiceCategorieImpl();

        // S'assurer que les statistiques ne sont pas nulles avant de les utiliser
        Map<Categorie, Long> statistiques = serviceCategorie.getCategoriesStatistics();
        if (statistiques != null) {
            statistics = new HashMap<>(statistiques.size());
            for (Map.Entry<Categorie, Long> entry : statistiques.entrySet()) {
                statistics.put(entry.getKey(), entry.getValue());
            }
        }

        // Initialiser le graphique en barres avec une limite de 20 catégories
        BarChart<String, Number> barChart = createBarChart(statistiques, 20);
        borderPane.setCenter(barChart);
    }

    @FXML
    private void handleShowStatistics() {
        if ("Statistiques of categorie".equals(statisticsComboBox.getValue())) {
            // Afficher les statistiques de catégorie
            Map<Categorie, Long> statistiques = new IServiceCategorieImpl().getCategoriesStatistics();
            borderPane.setCenter(createBarChart(statistiques, 20));
        }
    }

    @FXML
    private void handleShowBarChart() {
        showBarChart(statistics, 20);
    }

    // ... (Les autres méthodes restent inchangées)

    @FXML
    private void handleShowPieChart(ActionEvent event) {
        // Récupérer les statistiques des catégories
        Map<Categorie, Long> statistiques = new IServiceCategorieImpl().getCategoriesStatistics();

        // Ajouter un log pour vérifier les statistiques
        System.out.println("Statistiques : " + statistiques);

        // Créer et afficher le graphique en secteurs
        showPieChart(statistiques);
    }

    // Méthode pour afficher le graphique en secteurs
    private void showPieChart(Map<Categorie, Long> statistics) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<Categorie, Long> entry : statistics.entrySet()) {
            Categorie categorie = entry.getKey();
            String categoryName = String.valueOf(categorie.getNom()); // Extraire le nom de la catégorie
            Long numberOfCategories = entry.getValue();
            pieChartData.add(new PieChart.Data(categoryName, numberOfCategories));
        }

        PieChart pieChart = new PieChart(pieChartData);
        createToolTips(pieChart);

        pieChart.setTitle("Statistiques of  categories");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setStartAngle(180);

        pieChartData.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty(), " ")
                ));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem miSwitchToBarChart = new MenuItem("Switch to Bar Chart");
        contextMenu.getItems().add(miSwitchToBarChart);

        pieChart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(pieChart, event.getScreenX(), event.getScreenY());
            }
        });

        miSwitchToBarChart.setOnAction(event -> showBarChart(statistics));

        borderPane.setCenter(pieChart);
    }

    private void showBarChart(Map<Categorie, Long> statistics) {
    }

    private void createToolTips(PieChart pc) {
        for (PieChart.Data data : pc.getData()) {
            String msg = Double.toString(data.getPieValue());
            Tooltip tp = new Tooltip(msg);
            tp.setShowDelay(Duration.seconds(0));

            Tooltip.install(data.getNode(), tp);

            data.pieValueProperty().addListener((observable, oldValue, newValue) ->
            {
                tp.setText(newValue.toString());
            });
        }
    }

    public void handleClose(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleUpdatePieData(ActionEvent actionEvent) {
        Node node = borderPane.getCenter();

        if (node instanceof PieChart pc) {
            double value = pc.getData().get(2).getPieValue();
            pc.getData().get(2).setPieValue(value * 1.10);
            createToolTips(pc);
        }
    }

    public void initialize() {
    }
}
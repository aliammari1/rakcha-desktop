package com.esprit.controllers.cinemas;

import com.esprit.models.cinemas.CommentaireCinema;
import com.esprit.services.cinemas.CommentaireCinemaService;
import com.esprit.services.cinemas.CinemaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CinemaStatisticsController {

    @FXML
    private AnchorPane StatisticsAnchor;

    private CommentaireCinemaService commentaireCinemaService = new CommentaireCinemaService();
    private CinemaService cinemaService = new CinemaService();

    @FXML
    void showStatistics(ActionEvent event) {
        StatisticsAnchor.getChildren().clear();

        // Récupérer les commentaires de la base de données
        List<CommentaireCinema> comments = commentaireCinemaService.read();

        // Générer les statistiques sur l'analyse de sentiment
        Map<String, Map<String, Integer>> cinemaSentimentStatistics = generateSentimentStatistics(comments);

        // Créer un VBox pour contenir les PieCharts
        VBox chartContainer = new VBox();
        chartContainer.setSpacing(20);

        // Afficher les statistiques dans des PieCharts
        for (Map.Entry<String, Map<String, Integer>> entry : cinemaSentimentStatistics.entrySet()) {
            PieChart pieChart = createPieChart(entry.getKey(), entry.getValue());
            chartContainer.getChildren().add(pieChart);
        }

        // Ajouter le VBox à l'AnchorPane
        StatisticsAnchor.getChildren().add(chartContainer);
    }

    // Méthode pour générer les statistiques sur l'analyse de sentiment pour chaque cinéma
    private Map<String, Map<String, Integer>> generateSentimentStatistics(List<CommentaireCinema> comments) {
        Map<String, Map<String, Integer>> cinemaSentimentStatistics = new HashMap<>();

        // Parcourir les commentaires et compter le nombre de sentiments pour chaque cinéma
        for (CommentaireCinema comment : comments) {
            String cinemaName = cinemaService.getCinema(comment.getCinema().getId_cinema()).getNom();
            String sentiment = comment.getSentiment();

            // Vérifier si le cinéma est déjà dans la map
            if (cinemaSentimentStatistics.containsKey(cinemaName)) {
                Map<String, Integer> sentimentStatistics = cinemaSentimentStatistics.get(cinemaName);
                sentimentStatistics.put(sentiment, sentimentStatistics.getOrDefault(sentiment, 0) + 1);
            } else {
                Map<String, Integer> sentimentStatistics = new HashMap<>();
                sentimentStatistics.put(sentiment, 1);
                cinemaSentimentStatistics.put(cinemaName, sentimentStatistics);
            }
        }

        return cinemaSentimentStatistics;
    }

    // Méthode pour créer un PieChart pour un cinéma donné
    private PieChart createPieChart(String cinemaName, Map<String, Integer> sentimentStatistics) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle(cinemaName + " Sentiment Statistics");

        // Ajouter les données au PieChart
        for (Map.Entry<String, Integer> entry : sentimentStatistics.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        return pieChart;
    }
}

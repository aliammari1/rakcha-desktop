package com.esprit.controllers.cinemas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esprit.models.cinemas.CinemaComment;
import com.esprit.services.cinemas.CinemaCommentService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.utils.PageRequest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller responsible for generating and displaying sentiment statistics for
 * various
 * cinemas based on user comments.
 * 
 * <p>
 * This controller retrieves movie commentary data from a service, generates
 * sentiment
 * statistics for each cinema using the comments, and creates PieCharts to
 * visualize the
 * statistics. The controller also displays the generated PieCharts in an
 * AnchorPane.
 * </p>
 * 
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 */
public class CinemaStatisticsController {
    private final CinemaCommentService cinemaCommentService = new CinemaCommentService();
    private final CinemaService cinemaService = new CinemaService();
    @FXML
    private AnchorPane statisticsAnchor;

    /**
     * Generate and display per-cinema sentiment pie charts in the statistics anchor pane.
     *
     * Clears existing content in the statistics anchor, aggregates sentiment counts from
     * stored cinema comments, creates a PieChart for each cinema showing its sentiment
     * distribution, and adds the charts in a vertical layout to the anchor pane.
     *
     * @param event the ActionEvent that triggered the statistics display
     */
    @FXML
    void showStatistics(final ActionEvent event) {
        this.statisticsAnchor.getChildren().clear();
        // Récupérer les commentaires de la base de données
        final List<CinemaComment> comments = this.cinemaCommentService.read(PageRequest.defaultPage()).getContent();
        // Générer les statistiques sur l'analyse de sentiment
        final Map<String, Map<String, Integer>> cinemaSentimentStatistics = this.generateSentimentStatistics(comments);
        // Créer un VBox pour contenir les PieCharts
        final VBox chartContainer = new VBox();
        chartContainer.setSpacing(20);
        // Afficher les statistiques dans des PieCharts
        for (final Map.Entry<String, Map<String, Integer>> entry : cinemaSentimentStatistics.entrySet()) {
            final PieChart pieChart = this.createPieChart(entry.getKey(), entry.getValue());
            chartContainer.getChildren().add(pieChart);
        }

        // Ajouter le VBox à l'AnchorPane
        this.statisticsAnchor.getChildren().add(chartContainer);
    }


    /**
     * Builds a map of sentiment counts per cinema.
     *
     * @param comments list of CinemaComment; each comment increments the count for its cinema's sentiment
     * @return a map that maps each cinema name to a map from sentiment label to the number of comments with that sentiment
     */
    private Map<String, Map<String, Integer>> generateSentimentStatistics(final List<CinemaComment> comments) {
        final Map<String, Map<String, Integer>> cinemaSentimentStatistics = new HashMap<>();
        // Parcourir les commentaires et compter le nombre de sentiments pour chaque
        // cinéma
        for (final CinemaComment comment : comments) {
            final String cinemaName = this.cinemaService.getCinemaById(comment.getCinema().getId()).getName();
            final String sentiment = comment.getSentiment();
            // Vérifier si le cinéma est déjà dans la map
            if (cinemaSentimentStatistics.containsKey(cinemaName)) {
                final Map<String, Integer> sentimentStatistics = cinemaSentimentStatistics.get(cinemaName);
                sentimentStatistics.put(sentiment, sentimentStatistics.getOrDefault(sentiment, 0) + 1);
            }
 else {
                final Map<String, Integer> sentimentStatistics = new HashMap<>();
                sentimentStatistics.put(sentiment, 1);
                cinemaSentimentStatistics.put(cinemaName, sentimentStatistics);
            }

        }

        return cinemaSentimentStatistics;
    }


    /**
     * Create a PieChart showing the sentiment distribution for a cinema.
     *
     * @param cinemaName          the cinema's display name used in the chart title
     * @param sentimentStatistics map of sentiment label to count used to build slices
     * @return a PieChart titled "<cinemaName> Sentiment Statistics" containing one slice per sentiment with the provided counts
     * @since 1.0
     */
    private PieChart createPieChart(final String cinemaName, final Map<String, Integer> sentimentStatistics) {
        final PieChart pieChart = new PieChart();
        pieChart.setTitle(cinemaName + " Sentiment Statistics");
        // Ajouter les données au PieChart
        for (final Map.Entry<String, Integer> entry : sentimentStatistics.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        return pieChart;
    }

}
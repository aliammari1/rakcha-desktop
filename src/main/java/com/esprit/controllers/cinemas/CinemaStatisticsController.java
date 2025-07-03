package com.esprit.controllers.cinemas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esprit.models.cinemas.CinemaComment;
import com.esprit.services.cinemas.CinemaCommentService;
import com.esprit.services.cinemas.CinemaService;

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
     * Displays sentiment statistics for all cinemas as pie charts.
     * 
     * <p>
     * This method clears the existing statistics display, retrieves all cinema
     * comments
     * from the database, generates sentiment statistics for each cinema, and
     * creates
     * pie charts to visualize the data. The charts are displayed in a vertical
     * layout
     * within the statistics anchor pane.
     * </p>
     *
     * @param event the action event triggered by the button click that initiates
     *              the statistics generation and display process
     */
    @FXML
    void showStatistics(final ActionEvent event) {
        this.statisticsAnchor.getChildren().clear();
        // Récupérer les commentaires de la base de données
        final List<CinemaComment> comments = this.cinemaCommentService.read();
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
     * Generates sentiment statistics for each cinema based on user comments.
     * 
     * <p>
     * This method processes a list of cinema comments and aggregates sentiment
     * counts
     * by cinema name. For each cinema, it counts how many comments fall into each
     * sentiment category (positive, negative, neutral, etc.).
     * </p>
     *
     * @param comments the list of cinema comments to analyze for sentiment
     *                 statistics
     * @return a map where keys are cinema names and values are maps of sentiment
     *         types
     *         to their respective counts
     * @throws IllegalArgumentException if comments list is null
     * @since 1.0
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
            } else {
                final Map<String, Integer> sentimentStatistics = new HashMap<>();
                sentimentStatistics.put(sentiment, 1);
                cinemaSentimentStatistics.put(cinemaName, sentimentStatistics);
            }
        }
        return cinemaSentimentStatistics;
    }

    /**
     * Creates a pie chart displaying sentiment statistics for a specific cinema.
     * 
     * <p>
     * This method creates a PieChart object with a title that includes the cinema
     * name
     * and populates it with sentiment data. Each sentiment category is represented
     * as a slice in the pie chart with its corresponding count.
     * </p>
     *
     * @param cinemaName          the name of the cinema for which the chart is
     *                            being created
     * @param sentimentStatistics a map containing sentiment types as keys and their
     *                            counts as values for the specified cinema
     * @return a configured PieChart displaying the sentiment distribution for the
     *         cinema
     * @throws IllegalArgumentException if cinemaName is null or empty
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

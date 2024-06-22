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
/**
 * Is responsible for generating and displaying sentiment statistics for various
 * cinemas based on user comments. It retrieves movie commentary data from a service,
 * generates sentiment statistics for each cinema using the comments, and creates
 * PieCharts to visualize the statistics. The controller also displays the generated
 * PieCharts in an AnchorPane.
 */
public class CinemaStatisticsController {
    @FXML
    private AnchorPane StatisticsAnchor;
    private CommentaireCinemaService commentaireCinemaService = new CommentaireCinemaService();
    private CinemaService cinemaService = new CinemaService();
    /**
     * Clears an existing children container, retrieves comments from a database, generates
     * sentiment statistics for each cinema, and displays the statistics as PieCharts
     * within a new container added to the parent AnchorPane.
     * 
     * @param event ActionEvent triggered by the button click that initiates the code
     * execution and calls the `showStatistics()` method.
     * 
     * 	- `event`: an instance of `ActionEvent`, representing a user-generated event
     */
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
    /** 
     * @param comments
     * @return Map<String, Map<String, Integer>>
     */
    // Méthode pour générer les statistiques sur l'analyse de sentiment pour chaque cinéma
    /**
     * Generates a map of sentiment statistics for each cinema based on a list of comments,
     * where each comment is associated with a cinema and a sentiment label. The function
     * counts the number of comments with each sentiment and stores it in the map for
     * that cinema.
     * 
     * @param comments list of comments that will be processed to generate sentiment
     * statistics for each cinema.
     * 
     * 	- It is a list of `CommentaireCinema` objects, representing user comments on
     * cinema movies.
     * 
     * @returns a map of cinema names to sentiment statistics, where each cinema's sentiment
     * is counted and stored as an integer value.
     * 
     * 	- The output is a map of cinema names to maps of sentiment names to integer counts
     * of the number of comments expressing that sentiment for each cinema.
     * 	- Each cinema name in the outer map corresponds to a map of sentiment names in
     * the inner map, indicating the number of comments expressing that sentiment for
     * that cinema.
     * 	- The maps are keyed by sentiment name and contain integer values representing
     * the count of comments expressing that sentiment for each cinema.
     */
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
    /**
     * Creates a PieChart object and sets its title based on a cinema name and sentiment
     * statistics map. It then adds the chart data based on the map entries.
     * 
     * @param cinemaName name of the cinema for which sentiment statistics are being
     * generated and displayed in the PieChart.
     * 
     * @param sentimentStatistics sentiment statistics of a cinema, which is used to
     * generate a pie chart displaying the distribution of sentiment scores for that cinema.
     * 
     * 	- `Map<String, Integer>`: This map contains sentiment statistics for different
     * cinema names, where each key represents a cinema name and the value represents the
     * number of reviews with a positive sentiment towards that cinema.
     * 
     * @returns a pie chart representing the sentiment statistics of a given cinema.
     * 
     * 1/ Title: The title of the PieChart is set to the cinema name followed by " Sentiment
     * Statistics".
     * 2/ Data: The function adds the sentiment statistics data to the PieChart's data
     * list. Each entry in the data list consists of a category (key) and the corresponding
     * frequency (value).
     */
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

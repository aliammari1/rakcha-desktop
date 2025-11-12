package com.esprit.controllers.cinemas;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import java.util.HashMap;

/**
 * Lightweight controller for analyzing text sentiment using VADER algorithm.
 * 
 * <p>
 * This class uses VADER (Valence Aware Dictionary and sEntiment Reasoner) which
 * is
 * a lexicon and rule-based sentiment analysis tool that is specifically attuned
 * to
 * sentiments expressed in social media, but works well on other domains too.
 * </p>
 * 
 * <p>
 * VADER uses a combination of qualitative and quantitative measures and doesn't
 * require
 * manual word list definitions. It's much lighter than Stanford CoreNLP.
 * </p>
 * 
 * @author Esprit Team
 * @version 2.0
 * @since 2.0
 */
public class SentimentAnalysisController {

    private final SentimentAnalyzer sentimentAnalyzer;

    /**
     * Creates a SentimentAnalysisController and initializes the internal VADER SentimentAnalyzer.
     */
    public SentimentAnalysisController() {
        this.sentimentAnalyzer = new SentimentAnalyzer();
    }


    /**
     * Analyzes the sentiment of the given text using VADER algorithm.
     * 
     * <p>
     * VADER returns sentiment scores including compound, positive, neutral, and
     * negative.
     * The compound score is normalized between -1 (most extreme negative) and +1
     * (most extreme positive).
     * </p>
     *
     * @param text the input text to be analyzed for sentiment
     * @return a string containing sentiment classification: "Positive", "Negative",
     *         or "Neutral"
     * @throws IllegalArgumentException if text is null
     * @since 2.0
     */
    public String analyzeSentiment(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }


        try {
            // Set the input string and analyze
            sentimentAnalyzer.setInputString(text);
            sentimentAnalyzer.setInputStringProperties();
            sentimentAnalyzer.analyze();

            // Get the polarity scores
            final HashMap<String, Float> sentimentScores = sentimentAnalyzer.getPolarity();
            final double compound = sentimentScores.get("compound");

            if (compound >= 0.05) {
                return "Positive";
            }
 else if (compound <= -0.05) {
                return "Negative";
            }
 else {
                return "Neutral";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing sentiment", e);
        }

    }


    /**
     * Produce a formatted sentiment label and polarity scores for the given text.
     *
     * @param text the text to analyze; must not be null
     * @return a string in the form "<Sentiment> (Compound: <compound>, Pos: <positive>, Neg: <negative>, Neu: <neutral>)"
     * @throws IllegalArgumentException if {@code text} is null
     * @throws RuntimeException if an error occurs during analysis
     */
    public String analyzeSentimentDetailed(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }


        try {
            // Set the input string and analyze
            sentimentAnalyzer.setInputString(text);
            sentimentAnalyzer.setInputStringProperties();
            sentimentAnalyzer.analyze();

            // Get the polarity scores
            final HashMap<String, Float> sentimentScores = sentimentAnalyzer.getPolarity();
            final double compound = sentimentScores.get("compound");
            final double positive = sentimentScores.get("positive");
            final double negative = sentimentScores.get("negative");
            final double neutral = sentimentScores.get("neutral");

            String sentiment;
            if (compound >= 0.05) {
                sentiment = "Positive";
            }
 else if (compound <= -0.05) {
                sentiment = "Negative";
            }
 else {
                sentiment = "Neutral";
            }


            return String.format("%s (Compound: %.3f, Pos: %.3f, Neg: %.3f, Neu: %.3f)",
                    sentiment, compound, positive, negative, neutral);
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing detailed sentiment", e);
        }

    }


    /**
     * Retrieve the compound sentiment score for the input text.
     *
     * @param text the input text to analyze; must not be null
     * @return the compound score between -1.0 (negative) and 1.0 (positive)
     * @throws IllegalArgumentException if {@code text} is null
     * @throws RuntimeException if an error occurs during analysis
     */
    public double getSentimentScore(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }


        try {
            // Set the input string and analyze
            sentimentAnalyzer.setInputString(text);
            sentimentAnalyzer.setInputStringProperties();
            sentimentAnalyzer.analyze();

            // Get the polarity scores and return compound score
            final HashMap<String, Float> sentimentScores = sentimentAnalyzer.getPolarity();
            return sentimentScores.get("compound");
        } catch (Exception e) {
            throw new RuntimeException("Error getting sentiment score", e);
        }

    }


    /**
     * Return a map of sentiment polarity scores for the provided text.
     *
     * @param text the input text to analyze
     * @return a map with keys "compound", "positive", "negative", and "neutral" mapped to their corresponding scores
     * @throws IllegalArgumentException if {@code text} is null
     * @throws RuntimeException if an error occurs while performing sentiment analysis
     */
    public HashMap<String, Float> getAllSentimentScores(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }


        try {
            // Set the input string and analyze
            sentimentAnalyzer.setInputString(text);
            sentimentAnalyzer.setInputStringProperties();
            sentimentAnalyzer.analyze();

            return sentimentAnalyzer.getPolarity();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all sentiment scores", e);
        }

    }

}
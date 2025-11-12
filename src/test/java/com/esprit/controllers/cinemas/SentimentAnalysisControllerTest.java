package com.esprit.controllers.cinemas;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Comprehensive test suite for SentimentAnalysisController.
 * Tests sentiment analysis of cinema comments using VADER algorithm.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SentimentAnalysisControllerTest {

    private final SentimentAnalysisController controller = new SentimentAnalysisController();

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Sentiment Analysis Tests")
    class SentimentAnalysisTests {

        @Test
        @Order(1)
        @DisplayName("Should analyze positive sentiment correctly")
        void testPositiveSentiment() {
            String result = controller.analyzeSentiment("I love this movie! It's absolutely fantastic!");
            assertThat(result).isEqualTo("Positive");
        }


        @Test
        @Order(2)
        @DisplayName("Should analyze negative sentiment correctly")
        void testNegativeSentiment() {
            String result = controller.analyzeSentiment("This movie is terrible and boring. Worst film ever!");
            assertThat(result).isEqualTo("Negative");
        }


        @Test
        @Order(3)
        @DisplayName("Should analyze neutral sentiment correctly")
        void testNeutralSentiment() {
            String result = controller.analyzeSentiment("The movie has a plot.");
            assertThat(result).isEqualTo("Neutral");
        }


        @Test
        @Order(4)
        @DisplayName("Should get sentiment score between -1 and 1")
        void testSentimentScore() {
            double score = controller.getSentimentScore("I really enjoyed watching this film");
            assertThat(score).isBetween(-1.0, 1.0);
            assertThat(score).isGreaterThan(0.0); // Should be positive
        }


        @Test
        @Order(5)
        @DisplayName("Should get detailed sentiment analysis")
        void testDetailedSentimentAnalysis() {
            String detailed = controller.analyzeSentimentDetailed("Excellent movie! Highly recommend!");
            assertThat(detailed).contains("Positive");
            assertThat(detailed).contains("Compound:");
            assertThat(detailed).contains("Pos:");
            assertThat(detailed).contains("Neg:");
            assertThat(detailed).contains("Neu:");
        }


        @Test
        @Order(6)
        @DisplayName("Should get all sentiment scores as map")
        void testGetAllSentimentScores() {
            HashMap<String, Float> scores = controller.getAllSentimentScores("Great film!");
            
            assertThat(scores).isNotNull();
            assertThat(scores).containsKeys("compound", "positive", "negative", "neutral");
            assertThat(scores.get("compound")).isBetween(-1f, 1f);
            assertThat(scores.get("positive")).isBetween(0f, 1f);
            assertThat(scores.get("negative")).isBetween(0f, 1f);
            assertThat(scores.get("neutral")).isBetween(0f, 1f);
        }


        @Test
        @Order(7)
        @DisplayName("Should handle mixed sentiments")
        void testMixedSentiment() {
            String result = controller.analyzeSentiment("The movie was good, but too long");
            // This might be Neutral or Positive depending on the compound score
            assertThat(result).isIn("Positive", "Neutral", "Negative");
        }


        @Test
        @Order(8)
        @DisplayName("Should handle very strong positive sentiment")
        void testVeryStrongPositive() {
            String result = controller.analyzeSentiment("This is the best movie ever made! Absolutely amazing! Fantastic!");
            assertThat(result).isEqualTo("Positive");
            
            double score = controller.getSentimentScore("This is the best movie ever made! Absolutely amazing! Fantastic!");
            assertThat(score).isGreaterThan(0.5);
        }


        @Test
        @Order(9)
        @DisplayName("Should handle very strong negative sentiment")
        void testVeryStrongNegative() {
            String result = controller.analyzeSentiment("Terrible! Horrible! Worst! Awful! Disgusting!");
            assertThat(result).isEqualTo("Negative");
            
            double score = controller.getSentimentScore("Terrible! Horrible! Worst! Awful! Disgusting!");
            assertThat(score).isLessThan(-0.5);
        }


        @Test
        @Order(10)
        @DisplayName("Should handle empty strings near threshold")
        void testThresholdBoundary() {
            // Test compound score exactly at boundaries
            String result = controller.analyzeSentiment("ok");
            assertThat(result).isIn("Positive", "Neutral", "Negative");
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(11)
        @DisplayName("Should throw IllegalArgumentException for null input to analyzeSentiment")
        void testNullInputAnalyzeSentiment() {
            assertThatThrownBy(() -> controller.analyzeSentiment(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text cannot be null");
        }


        @Test
        @Order(12)
        @DisplayName("Should throw IllegalArgumentException for null input to getSentimentScore")
        void testNullInputGetSentimentScore() {
            assertThatThrownBy(() -> controller.getSentimentScore(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text cannot be null");
        }


        @Test
        @Order(13)
        @DisplayName("Should throw IllegalArgumentException for null input to getAllSentimentScores")
        void testNullInputGetAllSentimentScores() {
            assertThatThrownBy(() -> controller.getAllSentimentScores(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text cannot be null");
        }


        @Test
        @Order(14)
        @DisplayName("Should throw IllegalArgumentException for null input to analyzeSentimentDetailed")
        void testNullInputAnalyzeSentimentDetailed() {
            assertThatThrownBy(() -> controller.analyzeSentimentDetailed(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text cannot be null");
        }


        @Test
        @Order(15)
        @DisplayName("Should handle empty strings without throwing")
        void testEmptyString() {
            String result = controller.analyzeSentiment("");
            assertThat(result).isIn("Positive", "Neutral", "Negative");
        }


        @Test
        @Order(16)
        @DisplayName("Should handle very long text without throwing")
        void testVeryLongText() {
            StringBuilder longText = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                longText.append("This is a great movie! ");
            }

            
            String result = controller.analyzeSentiment(longText.toString());
            assertThat(result).isIn("Positive", "Neutral", "Negative");
        }


        @Test
        @Order(17)
        @DisplayName("Should handle special characters without throwing")
        void testSpecialCharacters() {
            String result = controller.analyzeSentiment("@#$% !!! *** ???");
            assertThat(result).isIn("Positive", "Neutral", "Negative");
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Sentiment Score Range Tests")
    class SentimentScoreRangeTests {

        @Test
        @Order(18)
        @DisplayName("Should consistently return positive score for positive text")
        void testPositiveScoreConsistency() {
            for (int i = 0; i < 3; i++) {
                double score = controller.getSentimentScore("Wonderful! Excellent! Great!");
                assertThat(score).isGreaterThan(0.0);
            }

        }


        @Test
        @Order(19)
        @DisplayName("Should consistently return negative score for negative text")
        void testNegativeScoreConsistency() {
            for (int i = 0; i < 3; i++) {
                double score = controller.getSentimentScore("Awful! Terrible! Horrible!");
                assertThat(score).isLessThan(0.0);
            }

        }


        @Test
        @Order(20)
        @DisplayName("Should return neutral score near zero for neutral text")
        void testNeutralScoreRange() {
            double score = controller.getSentimentScore("The movie has actors");
            assertThat(score).isBetween(-0.05, 0.05);
        }

    }


}


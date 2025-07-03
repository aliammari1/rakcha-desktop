package com.esprit.controllers.cinemas;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * Controller for analyzing text sentiment using Stanford CoreNLP pipeline.
 * 
 * <p>
 * This class provides functionality to analyze text sentiment by utilizing the
 * Stanford CoreNLP library. It configures a natural language processing
 * pipeline
 * with tokenization, sentence splitting, part-of-speech tagging, lemmatization,
 * parsing, and sentiment analysis capabilities.
 * </p>
 * 
 * <p>
 * The sentiment analysis returns classifications such as "Very Positive",
 * "Positive",
 * "Neutral", "Negative", or "Very Negative" for each sentence in the input
 * text.
 * </p>
 * 
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 * @see edu.stanford.nlp.pipeline.StanfordCoreNLP
 * @see edu.stanford.nlp.sentiment.SentimentCoreAnnotations
 */
public class SentimentAnalysisController {
    /**
     * Analyzes the sentiment of the given text using Stanford CoreNLP pipeline.
     * 
     * <p>
     * This method configures a Stanford CoreNLP pipeline with tokenization,
     * sentence splitting,
     * part-of-speech tagging, lemmatization, parsing, and sentiment analysis. It
     * processes
     * the input text and returns sentiment classifications for each sentence.
     * </p>
     * 
     * <p>
     * The sentiment analysis categorizes text into one of five sentiment classes:
     * "Very Positive", "Positive", "Neutral", "Negative", or "Very Negative".
     * </p>
     *
     * @param text the input text to be analyzed for sentiment
     * @return a string containing sentiment classifications, one per line for each
     *         sentence
     * @throws IllegalArgumentException if text is null
     * @since 1.0
     */
    public String analyzeSentiment(final String text) {
        // Configure pipeline properties
        final Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        // Initialize StanfordCoreNLP pipeline
        final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Create an annotation with the text
        final Annotation annotation = new Annotation(text);
        // Analyze the text
        pipeline.annotate(annotation);
        // Extract sentiment annotations
        final StringBuilder sentimentResult = new StringBuilder();
        for (final CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            final String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentResult.append(sentiment).append("\n");
        }
        // Retourner le résultat du sentiment
        return sentimentResult.toString();
    }
}

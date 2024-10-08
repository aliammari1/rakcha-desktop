package com.esprit.controllers.cinemas;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

/**
 * Is designed to analyze text sentiment using Stanford CoreNLP pipeline. It takes a
 * text input and returns a sentiment result as a string. The class configures the
 * pipeline properties, initializes the StanfordCoreNLP pipeline, creates an annotation
 * with the input text, and analyzes the text to extract sentiment annotations.
 */
public class SentimentAnalysisController {
    /**
     * Analyzes a given text using Stanford CoreNLP pipeline to extract sentiment
     * annotations, such as positive, negative or neutral. It returns the extracted
     * sentiment in a string format.
     *
     * @param text text to be analyzed for sentiment using the Stanford CoreNLP pipeline.
     * @returns a string containing the sentiment annotations of the given text.
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

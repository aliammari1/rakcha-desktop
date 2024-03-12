package com.esprit.controllers.cinemas;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.Properties;

public class SentimentAnalysisController {



    public String analyzeSentiment(String text) {
        // Configure pipeline properties
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");

        // Initialize StanfordCoreNLP pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Create an annotation with the text
        Annotation annotation = new Annotation(text);

        // Analyze the text
        pipeline.annotate(annotation);

        // Extract sentiment annotations
        StringBuilder sentimentResult = new StringBuilder();
        for (edu.stanford.nlp.util.CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentResult.append(sentiment).append("\n");
        }

        // Retourner le résultat du sentiment
        return sentimentResult.toString();
    }

}


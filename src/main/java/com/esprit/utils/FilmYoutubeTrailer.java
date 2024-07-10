package com.esprit.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.services.produits.AvisService;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.client.json.gson.GsonFactory;

public class FilmYoutubeTrailer {
    private final String API_KEY = System.getenv("YOUTUBE_API_KEY");
    private static final Logger LOGGER = Logger.getLogger(FilmYoutubeTrailer.class.getName());

    /**
     * @param filmNom
     * @return String
     */
    public String watchTrailer(String filmNom) {
        LOGGER.info("watch the trailer");
        try {
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    request -> {
                    })
                    .setApplicationName("Rakcha")
                    .build();
            LOGGER.info("the trailer is not watched");
            YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
            search.setKey(API_KEY);
            search.setQ(filmNom);
            search.setType(Collections.singletonList("video"));
            search.setFields("items(id/videoId)");
            search.setMaxResults(1L);
            SearchResult searchResult = search.execute().getItems().get(0);
            String videoId = searchResult.getId().getVideoId();
            return "https://www.youtube.com/embed/" + videoId;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return "https://www.youtube.com";
    }
}

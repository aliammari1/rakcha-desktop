package com.esprit.utils;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmYoutubeTrailer {
    private static final Logger LOGGER = Logger.getLogger(FilmYoutubeTrailer.class.getName());
    private final String API_KEY = System.getenv("YOUTUBE_API_KEY");

    /**
     * @param filmNom
     * @return String
     */
    public String watchTrailer(final String filmNom) {
        FilmYoutubeTrailer.LOGGER.info("watch the trailer");
        try {
            final YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    request -> {
                    })
                    .setApplicationName("Rakcha")
                    .build();
            FilmYoutubeTrailer.LOGGER.info("the trailer is not watched");
            final YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
            search.setKey(this.API_KEY);
            search.setQ(filmNom);
            search.setType(Collections.singletonList("video"));
            search.setFields("items(id/videoId)");
            search.setMaxResults(1L);
            final SearchResult searchResult = search.execute().getItems().get(0);
            final String videoId = searchResult.getId().getVideoId();
            return "https://www.youtube.com/embed/" + videoId;
        } catch (final Exception e) {
            FilmYoutubeTrailer.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return "https://www.youtube.com";
    }
}

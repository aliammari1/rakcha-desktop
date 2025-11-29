package com.esprit.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FilmYoutubeTrailer {
    private static final Logger LOGGER = Logger.getLogger(FilmYoutubeTrailer.class.getName());
    private static final String APPLICATION_NAME = "Rakcha";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/embed/";
    private static final String FALLBACK_URL = "https://www.youtube.com";
    private static final long MAX_RESULTS = 1L;

    private final String apiKey;
    private final YouTube youtube;

    /**
     * Constructs a new FilmYoutubeTrailer instance.
     * Initializes YouTube API service.
     */
    public FilmYoutubeTrailer() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("YOUTUBE_API_KEY");
        if (this.apiKey == null) {
            throw new IllegalStateException("YouTube API key not found in .env file");
        }


        this.youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {
        }
).setApplicationName(APPLICATION_NAME).build();
    }


    /**
     * Search for and return the trailer URL for a given film
     *
     * @param filmName
     *                 The name of the film to search for
     * @return URL of the trailer video, or fallback URL if not found
     */
    public String watchTrailer(final String filmName) {
        if (filmName == null || filmName.trim().isEmpty()) {
            LOGGER.warning("Invalid film name provided");
            return FALLBACK_URL;
        }


        try {
            LOGGER.info("Searching for trailer: " + filmName);

            final YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
            search.setKey(this.apiKey);
            search.setQ(filmName + " official trailer");
            search.setType(Collections.singletonList("video"));
            search.setFields("items(id/videoId)");
            search.setMaxResults(MAX_RESULTS);

            final SearchResult searchResult = search.execute().getItems().get(0);
            final String videoId = searchResult.getId().getVideoId();

            if (videoId != null && !videoId.isEmpty()) {
                return YOUTUBE_BASE_URL + videoId;
            }


            LOGGER.warning("No trailer found for: " + filmName);
            return FALLBACK_URL;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error searching for trailer: " + filmName, e);
            return FALLBACK_URL;
        }

    }

}


package com.esprit.utils;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private String apiKey;
    private YouTube youtube;
    private boolean apiAvailable = false;

    /**
     * Constructs a new FilmYoutubeTrailer instance.
     * Initializes YouTube API service if API key is available.
     */
    public FilmYoutubeTrailer() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            this.apiKey = dotenv.get("YOUTUBE_API_KEY");

            if (this.apiKey != null && !this.apiKey.isEmpty() && !this.apiKey.equals("YOUR_API_KEY_HERE")) {
                this.youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {
                }).setApplicationName(APPLICATION_NAME).build();
                this.apiAvailable = true;
                LOGGER.info("YouTube API initialized successfully");
            } else {
                LOGGER.warning("YouTube API key not found or invalid in .env file - using web scraping fallback");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize YouTube API - using web scraping fallback", e);
        }
    }

    /**
     * Search for and return the trailer URL for a given film.
     * First tries YouTube API if available, then falls back to web scraping.
     *
     * @param filmName The name of the film to search for
     * @return URL of the trailer video, or fallback URL if not found
     */
    public String watchTrailer(final String filmName) {
        if (filmName == null || filmName.trim().isEmpty()) {
            LOGGER.warning("Invalid film name provided");
            return FALLBACK_URL;
        }

        // Try YouTube API first if available
        if (apiAvailable) {
            String result = searchWithYouTubeApi(filmName);
            if (result != null && !result.equals(FALLBACK_URL)) {
                return result;
            }
        }

        // Fall back to web scraping
        LOGGER.info("Using IMDB scraper to find trailer for: " + filmName);
        String scrapedUrl = IMDBScraper.getTrailerUrl(filmName);
        if (scrapedUrl != null && !scrapedUrl.isEmpty()) {
            return scrapedUrl;
        }

        LOGGER.warning("No trailer found for: " + filmName);
        return FALLBACK_URL;
    }

    /**
     * Search for trailer using YouTube Data API
     */
    private String searchWithYouTubeApi(final String filmName) {
        try {
            LOGGER.info("Searching YouTube API for trailer: " + filmName);

            final YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));
            search.setKey(this.apiKey);
            search.setQ(filmName + " official trailer");
            search.setType(Collections.singletonList("video"));
            search.setFields("items(id/videoId)");
            search.setMaxResults(MAX_RESULTS);

            var items = search.execute().getItems();
            if (items != null && !items.isEmpty()) {
                final SearchResult searchResult = items.get(0);
                final String videoId = searchResult.getId().getVideoId();

                if (videoId != null && !videoId.isEmpty()) {
                    LOGGER.info("Found trailer via YouTube API: " + videoId);
                    return YOUTUBE_BASE_URL + videoId;
                }
            }

            LOGGER.warning("No trailer found via YouTube API for: " + filmName);
            return null;

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error searching YouTube API for trailer: " + filmName, e);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "YouTube API error - falling back to scraping: " + e.getMessage());
            this.apiAvailable = false; // Disable API for future calls in this session
            return null;
        }
    }
}

package com.esprit.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for scraping IMDB data including movie URLs and trailer information.
 * Uses Jsoup for HTML parsing and web scraping.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class IMDBScraper {

    private static final Logger LOGGER = Logger.getLogger(IMDBScraper.class.getName());

    private static final String IMDB_SEARCH_URL = "https://www.imdb.com/find/?q=";
    private static final String IMDB_BASE_URL = "https://www.imdb.com";
    private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";
    private static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query=";

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT = 10000; // 10 seconds timeout

    /**
     * Gets the IMDB URL for a movie by searching for its name
     *
     * @param movieName The name of the movie to search for
     * @return The IMDB URL of the movie, or a search URL as fallback
     */
    public static String getIMDBUrl(String movieName) {
        if (movieName == null || movieName.trim().isEmpty()) {
            LOGGER.warning("Invalid movie name provided");
            return IMDB_BASE_URL;
        }

        try {
            String encodedQuery = URLEncoder.encode(movieName, StandardCharsets.UTF_8);
            String searchUrl = IMDB_SEARCH_URL + encodedQuery + "&s=tt&ttype=ft";

            LOGGER.info("Searching IMDB for: " + movieName);

            Document doc = Jsoup.connect(searchUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();

            // Look for search results - IMDB uses different selectors
            Elements results = doc.select("a.ipc-metadata-list-summary-item__t");

            if (results.isEmpty()) {
                // Try alternative selector
                results = doc.select(".find-title-result a");
            }

            if (results.isEmpty()) {
                // Try another alternative
                results = doc.select("[class*='find'] a[href*='/title/tt']");
            }

            if (!results.isEmpty()) {
                Element firstResult = results.first();
                String href = firstResult.attr("href");
                if (href.startsWith("/")) {
                    href = IMDB_BASE_URL + href;
                }
                // Clean up the URL - remove query params
                if (href.contains("?")) {
                    href = href.substring(0, href.indexOf("?"));
                }
                LOGGER.info("Found IMDB URL: " + href);
                return href;
            }

            LOGGER.warning("No IMDB results found for: " + movieName);
            return IMDB_BASE_URL + "/find/?q=" + encodedQuery;

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error scraping IMDB for: " + movieName, e);
            return IMDB_BASE_URL + "/find/?q=" + URLEncoder.encode(movieName, StandardCharsets.UTF_8);
        }
    }

    /**
     * Gets the trailer URL for a movie by scraping YouTube search results
     *
     * @param movieName The name of the movie
     * @return The YouTube embed URL for the trailer, or null if not found
     */
    public static String getTrailerUrl(String movieName) {
        if (movieName == null || movieName.trim().isEmpty()) {
            LOGGER.warning("Invalid movie name provided for trailer search");
            return null;
        }

        try {
            String searchQuery = movieName + " official trailer";
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String searchUrl = YOUTUBE_SEARCH_URL + encodedQuery;

            LOGGER.info("Searching YouTube for trailer: " + movieName);

            Document doc = Jsoup.connect(searchUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();

            // Extract video ID from the page source
            String html = doc.html();
            String videoId = extractVideoId(html);

            if (videoId != null) {
                String trailerUrl = YOUTUBE_EMBED_URL + videoId + "?autoplay=1&rel=0";
                LOGGER.info("Found trailer: " + trailerUrl);
                return trailerUrl;
            }

            // Fallback: Try to find any video link
            Elements videoLinks = doc.select("a[href*='watch?v=']");
            if (!videoLinks.isEmpty()) {
                String href = videoLinks.first().attr("href");
                videoId = extractVideoIdFromUrl(href);
                if (videoId != null) {
                    String trailerUrl = YOUTUBE_EMBED_URL + videoId + "?autoplay=1&rel=0";
                    LOGGER.info("Found trailer (fallback): " + trailerUrl);
                    return trailerUrl;
                }
            }

            LOGGER.warning("No trailer found for: " + movieName);
            return null;

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error searching for trailer: " + movieName, e);
            return null;
        }
    }

    /**
     * Extracts YouTube video ID from YouTube page HTML
     */
    private static String extractVideoId(String html) {
        // Look for video ID patterns in the HTML
        String[] patterns = {
            "\"videoId\":\"",
            "/watch?v=",
            "\"video_id\":\"",
            "embed/"
        };

        for (String pattern : patterns) {
            int index = html.indexOf(pattern);
            if (index != -1) {
                int startIndex = index + pattern.length();
                int endIndex = startIndex;

                // Find the end of the video ID (11 characters or until special char)
                while (endIndex < html.length() && endIndex - startIndex < 11) {
                    char c = html.charAt(endIndex);
                    if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                        endIndex++;
                    } else {
                        break;
                    }
                }

                if (endIndex - startIndex >= 10) {
                    String videoId = html.substring(startIndex, endIndex);
                    // Validate video ID format
                    if (videoId.matches("[a-zA-Z0-9_-]{10,12}")) {
                        return videoId;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts video ID from a YouTube URL
     */
    private static String extractVideoIdFromUrl(String url) {
        if (url == null) return null;

        // Pattern: watch?v=VIDEO_ID
        int vIndex = url.indexOf("v=");
        if (vIndex != -1) {
            int startIndex = vIndex + 2;
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            String videoId = url.substring(startIndex, Math.min(endIndex, startIndex + 11));
            if (videoId.matches("[a-zA-Z0-9_-]{10,12}")) {
                return videoId;
            }
        }

        // Pattern: /embed/VIDEO_ID
        int embedIndex = url.indexOf("/embed/");
        if (embedIndex != -1) {
            int startIndex = embedIndex + 7;
            int endIndex = url.indexOf("?", startIndex);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            String videoId = url.substring(startIndex, Math.min(endIndex, startIndex + 11));
            if (videoId.matches("[a-zA-Z0-9_-]{10,12}")) {
                return videoId;
            }
        }

        return null;
    }

    /**
     * Gets movie details from IMDB page
     *
     * @param imdbUrl The IMDB URL of the movie
     * @return MovieDetails object with scraped information, or null on error
     */
    public static MovieDetails getMovieDetails(String imdbUrl) {
        if (imdbUrl == null || !imdbUrl.contains("imdb.com")) {
            return null;
        }

        try {
            Document doc = Jsoup.connect(imdbUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .get();

            MovieDetails details = new MovieDetails();

            // Get title
            Element titleElement = doc.selectFirst("h1[data-testid='hero__pageTitle']");
            if (titleElement != null) {
                details.title = titleElement.text();
            }

            // Get rating
            Element ratingElement = doc.selectFirst("[data-testid='hero-rating-bar__aggregate-rating__score'] span");
            if (ratingElement != null) {
                try {
                    details.rating = Double.parseDouble(ratingElement.text());
                } catch (NumberFormatException e) {
                    details.rating = 0.0;
                }
            }

            // Get year
            Elements yearElements = doc.select("[data-testid='hero__pageTitle'] ~ ul li a");
            for (Element el : yearElements) {
                String text = el.text();
                if (text.matches("\\d{4}")) {
                    try {
                        details.year = Integer.parseInt(text);
                        break;
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }

            // Get description
            Element plotElement = doc.selectFirst("[data-testid='plot'] span[data-testid='plot-l']");
            if (plotElement == null) {
                plotElement = doc.selectFirst("[data-testid='plot'] span");
            }
            if (plotElement != null) {
                details.description = plotElement.text();
            }

            return details;

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error getting movie details from: " + imdbUrl, e);
            return null;
        }
    }

    /**
     * Simple class to hold movie details
     */
    public static class MovieDetails {

        public String title;
        public Double rating;
        public Integer year;
        public String description;
        public String posterUrl;
    }
}

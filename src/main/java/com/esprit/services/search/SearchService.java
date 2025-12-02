package com.esprit.services.search;

import com.esprit.utils.DataSource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ultra-fast universal search engine using in-memory caching.
 * Searches across all entities based on user role.
 */
public class SearchService {

    private static final Logger LOGGER = Logger.getLogger(SearchService.class.getName());
    private static volatile SearchService instance;

    private final Connection connection;
    private final Cache<String, List<SearchResult>> searchCache;
    private final Cache<String, List<String>> suggestionCache;
    private final ExecutorService executor;

    private SearchService() {
        this.connection = DataSource.getInstance().getConnection();
        this.searchCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
        this.suggestionCache = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public static SearchService getInstance() {
        if (instance == null) {
            synchronized (SearchService.class) {
                if (instance == null) {
                    instance = new SearchService();
                }
            }
        }
        return instance;
    }

    public List<SearchResult> search(String query, UserRole role, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedQuery = query.trim().toLowerCase();
        String cacheKey = normalizedQuery + "_" + role.name() + "_" + maxResults;

        List<SearchResult> cached = searchCache.getIfPresent(cacheKey);
        if (cached != null) return cached;

        List<SearchResult> results = new ArrayList<>();
        String likeQuery = "%" + normalizedQuery + "%";

        // Search films
        results.addAll(searchTable("films", "id", "name", "description", "image",
            "release_year", EntityType.FILM, likeQuery, maxResults));

        // Search series
        results.addAll(searchTable("series", "id", "name", "summary", "image",
            "director", EntityType.SERIES, likeQuery, maxResults));

        // Search products
        results.addAll(searchTable("products", "id", "name", "description", "image",
            "price", EntityType.PRODUCT, likeQuery, maxResults));

        // Search actors
        results.addAll(searchTable("actors", "id", "name", "biography", "image",
            null, EntityType.ACTOR, likeQuery, maxResults));

        // Search cinemas
        results.addAll(searchTable("cinemas", "id", "name", "address", "logo",
            "status", EntityType.CINEMA, likeQuery, maxResults));

        // Sort by score and limit
        results.sort(SearchResult::compareTo);
        if (results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }

        searchCache.put(cacheKey, results);
        return results;
    }

    private List<SearchResult> searchTable(String table, String idCol, String nameCol,
                                           String descCol, String imageCol, String extraCol, EntityType type,
                                           String likeQuery, int limit) {

        List<SearchResult> results = new ArrayList<>();
        String sql = String.format(
            "SELECT %s, %s, %s, %s %s FROM %s WHERE LOWER(%s) LIKE ? OR LOWER(%s) LIKE ? LIMIT ?",
            idCol, nameCol, descCol != null ? descCol : "''",
            imageCol != null ? imageCol : "''",
            extraCol != null ? ", " + extraCol : "",
            table, nameCol, descCol != null ? descCol : nameCol
        );

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, likeQuery);
            ps.setString(2, likeQuery);
            ps.setInt(3, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString(2);
                float score = calculateScore(likeQuery.replace("%", ""), name);

                String subtitle = "";
                if (extraCol != null) {
                    Object extra = rs.getObject(5);
                    subtitle = extra != null ? extra.toString() : "";
                }

                results.add(new SearchResult(
                    type,
                    rs.getLong(1),
                    name,
                    subtitle,
                    rs.getString(3),
                    rs.getString(4),
                    score
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Search error in " + table, e);
        }

        return results;
    }

    private float calculateScore(String query, String target) {
        if (target == null) return 0;
        String lowerTarget = target.toLowerCase();
        if (lowerTarget.equals(query)) return 1.0f;
        if (lowerTarget.startsWith(query)) return 0.9f;
        if (lowerTarget.contains(query)) return 0.7f;
        return 0.3f;
    }

    public List<String> getSuggestions(String prefix, UserRole role, int max) {
        if (prefix == null || prefix.trim().length() < 2) {
            return getTrending(max);
        }

        String cacheKey = prefix.toLowerCase() + "_" + role.name();
        List<String> cached = suggestionCache.getIfPresent(cacheKey);
        if (cached != null) return cached;

        Set<String> suggestions = new LinkedHashSet<>();
        String likeQuery = prefix.toLowerCase() + "%";

        String[] tables = {"films", "series", "products", "actors"};
        for (String table : tables) {
            String sql = "SELECT name FROM " + table + " WHERE LOWER(name) LIKE ? LIMIT ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, likeQuery);
                ps.setInt(2, max);
                ResultSet rs = ps.executeQuery();
                while (rs.next() && suggestions.size() < max) {
                    suggestions.add(rs.getString(1));
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Suggestion error", e);
            }
        }

        List<String> result = new ArrayList<>(suggestions);
        suggestionCache.put(cacheKey, result);
        return result;
    }

    private List<String> getTrending(int max) {
        List<String> trending = new ArrayList<>();
        String sql = "SELECT name FROM films ORDER BY release_year DESC LIMIT ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, max);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                trending.add(rs.getString(1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Trending error", e);
        }
        return trending;
    }

    public void clearCache() {
        searchCache.invalidateAll();
        suggestionCache.invalidateAll();
    }

    public enum EntityType {
        FILM("film", "mdi2m-movie", "#E50914"),
        SERIES("series", "mdi2t-television-classic", "#00D9FF"),
        PRODUCT("product", "mdi2s-shopping", "#FFD700"),
        ACTOR("actor", "mdi2a-account-star", "#FF6B6B"),
        CINEMA("cinema", "mdi2t-theater", "#9B59B6"),
        CATEGORY("category", "mdi2t-tag", "#2ECC71");

        public final String name;
        public final String icon;
        public final String color;

        EntityType(String name, String icon, String color) {
            this.name = name;
            this.icon = icon;
            this.color = color;
        }
    }

    public enum UserRole {
        CLIENT, ADMIN, CINEMA_MANAGER
    }

    public static class SearchResult implements Comparable<SearchResult> {

        private final EntityType type;
        private final Long id;
        private final String title;
        private final String subtitle;
        private final String description;
        private final String imageUrl;
        private final float score;

        public SearchResult(EntityType type, Long id, String title, String subtitle,
                            String description, String imageUrl, float score) {
            this.type = type;
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.description = description;
            this.imageUrl = imageUrl;
            this.score = score;
        }

        public EntityType getType() {
            return type;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public float getScore() {
            return score;
        }

        public String getIcon() {
            return type.icon;
        }

        public String getColor() {
            return type.color;
        }

        @Override
        public int compareTo(SearchResult other) {
            return Float.compare(other.score, this.score);
        }
    }
}

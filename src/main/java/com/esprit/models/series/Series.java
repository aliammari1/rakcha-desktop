package com.esprit.models.series;

import com.esprit.models.common.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Series management entity class for the RAKCHA application. Represents series
 * data with Hibernate/JPA annotations for database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class Series {

    private Long id;

    private String name;

    private String summary;

    private String director;

    private String country;

    private String imageUrl;

    private int releaseYear;

    private List<Category> categories;

    private List<Episode> episodes;

    private List<Favorite> favorites;

    private int clickLikes;

    private int clickDislikes;

    private int clickFavorites;

    /**
     * Create a Series instance without an id.
     *
     * @param name        the series title
     * @param summary     a short description of the series
     * @param director    the director's name
     * @param country     the country of origin
     * @param imageUrl    the image URL or path for the series
     * @param releaseYear the release year
     */
    public Series(final String name, final String summary, final String director, final String country,
                  final String imageUrl, final int releaseYear) {
        this.name = name;
        this.summary = summary;
        this.director = director;
        this.country = country;
        this.imageUrl = imageUrl;
        this.releaseYear = releaseYear;
        this.clickLikes = 0;
        this.clickDislikes = 0;
        this.clickFavorites = 0;
    }

    /**
     * Legacy constructor with like/dislike counters.
     *
     * @deprecated Use simplified constructor instead
     */
    @Deprecated(forRemoval = true)
    public Series(final String name, final String summary, final String director, final String country,
                  final String imageUrl, final int releaseYear, final int liked, final int numberOfLikes, final int disliked,
                  final int numberOfDislikes) {
        this.name = name;
        this.summary = summary;
        this.director = director;
        this.country = country;
        this.imageUrl = imageUrl;
        this.releaseYear = releaseYear;
        this.clickLikes = 0;
        this.clickDislikes = 0;
        this.clickFavorites = 0;
    }

    /**
     * Get the series image URL (convenience method).
     * @return the image URL
     */
    public String getImage() {
        return this.imageUrl;
    }

    /**
     * Set the series image URL (convenience method).
     * @param imageUrl the image URL to set
     */
    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Get the series name/title (alias for compatibility).
     * @return the series name
     */
    public String getNom() {
        return this.name;
    }

    /**
     * Set the series name/title (alias for compatibility).
     * @param nom the name to set
     */
    public void setNom(String nom) {
        this.name = nom;
    }

    /**
     * Get the release year (alias for compatibility).
     * @return the release year
     */
    public int getAnnee() {
        return this.releaseYear;
    }

    /**
     * Set the release year (alias for compatibility).
     * @param annee the year to set
     */
    public void setAnnee(int annee) {
        this.releaseYear = annee;
    }

    /**
     * Get the series rating/note (calculated from episodes or reviews).
     * @return the rating as a double
     */
    public double getNote() {
        // Return a default rating for now
        return 8.0;
    }

    /**
     * Get the series description (alias for compatibility).
     * @return the series description
     */
    public String getDescription() {
        return this.summary;
    }

    /**
     * Set the series description (alias for compatibility).
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.summary = description;
    }

    /**
     * Get the series genres/categories (alias for compatibility).
     * @return list of categories/genres
     */
    public List<Category> getGenres() {
        return this.categories;
    }

    /**
     * Get the series ID (alias for compatibility).
     * @return the series ID
     */
    public Long getIdserie() {
        return this.id;
    }

}



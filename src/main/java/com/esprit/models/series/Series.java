package com.esprit.models.series;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String image;

    private int liked;

    private int numberOfLikes;

    private int disliked;

    private int numberOfDislikes;

    private List<Category> categories;

    private List<Episode> episodes;

    private List<Favorite> favorites;

    private int clickLikes;

    private int clickDislikes;

    private int clickFavorites;

    /**
     * Create a Series instance without an id and initialize interaction counters to zero.
     *
     * @param name               the series title
     * @param summary            a short description of the series
     * @param director           the director's name
     * @param country            the country of origin
     * @param image              the image URL or path for the series
     * @param liked              legacy or initial liked count
     * @param numberOfLikes      total number of likes
     * @param disliked           legacy or initial disliked count
     * @param numberOfDislikes   total number of dislikes
     */
    public Series(final String name, final String summary, final String director, final String country,
            final String image, final int liked, final int numberOfLikes, final int disliked,
            final int numberOfDislikes) {
        this.name = name;
        this.summary = summary;
        this.director = director;
        this.country = country;
        this.image = image;
        this.liked = liked;
        this.numberOfLikes = numberOfLikes;
        this.disliked = disliked;
        this.numberOfDislikes = numberOfDislikes;
        this.clickLikes = 0;
        this.clickDislikes = 0;
        this.clickFavorites = 0;
    }

}

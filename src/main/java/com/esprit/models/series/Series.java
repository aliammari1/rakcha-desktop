package com.esprit.models.series;

import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "serie")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "summary")
    private String summary;

    @Column(name = "director")
    private String director;

    @Column(name = "country")
    private String country;

    @Column(name = "image")
    private String image;

    @Column(name = "liked")
    private int liked;

    @Column(name = "number_of_likes")
    private int numberOfLikes;

    @Column(name = "disliked")
    private int disliked;

    @Column(name = "number_of_dislikes")
    private int numberOfDislikes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "series_categories", joinColumns = @JoinColumn(name = "series_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episode> episodes;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @Transient
    private int clickLikes;

    @Transient
    private int clickDislikes;

    @Transient
    private int clickFavorites;

    /**
     * Constructor without id for creating new series instances.
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

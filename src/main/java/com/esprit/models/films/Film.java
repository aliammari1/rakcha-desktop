package com.esprit.models.films;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.esprit.models.cinemas.MovieSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a film.
 */
@Entity
@Table(name = "films")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "duration")
    private Time duration;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year")
    private int releaseYear;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_category", joinColumns = @JoinColumn(name = "film_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "actor_film", joinColumns = @JoinColumn(name = "film_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @Builder.Default
    private List<Actor> actors = new ArrayList<>();

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<FilmComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<FilmRating> ratings = new ArrayList<>();

    /**
     * Constructor without id for creating new film instances.
     */
    public Film(final String name, final String image, final Time duration, final String description,
            final int releaseYear) {
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.description = description;
        this.releaseYear = releaseYear;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    /**
     * Constructor with id for existing film instances.
     */
    public Film(final Long id, final String name, final String image, final Time duration, final String description,
            final int releaseYear) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.description = description;
        this.releaseYear = releaseYear;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    /**
     * Copy constructor.
     */
    public Film(Film f) {
        this.id = f.id;
        this.name = f.name;
        this.image = f.image;
        this.duration = f.duration;
        this.description = f.description;
        this.releaseYear = f.releaseYear;
        this.categories = new ArrayList<>(f.categories != null ? f.categories : new ArrayList<>());
        this.actors = new ArrayList<>(f.actors != null ? f.actors : new ArrayList<>());
        this.movieSessions = new ArrayList<>(f.movieSessions != null ? f.movieSessions : new ArrayList<>());
        this.comments = new ArrayList<>(f.comments != null ? f.comments : new ArrayList<>());
        this.ratings = new ArrayList<>(f.ratings != null ? f.ratings : new ArrayList<>());
    }

    /**
     * Performs Film operation.
     *
     * @return the result of the operation
     */
    public Film(final Long id) {
        this.id = id;
    }
}

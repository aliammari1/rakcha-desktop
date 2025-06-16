package com.esprit.models.films;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * The Actor class represents an actor in a film.
 */
@Entity
@Table(name = "actors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "number_of_appearances")
    private int numberOfAppearances;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Film> films = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "actor_category", joinColumns = @JoinColumn(name = "actor_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    /**
     * Constructor without id for creating new actor instances.
     */
    public Actor(final String name, final String image, final String biography) {
        this.name = name;
        this.image = image;
        this.biography = biography;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    /**
     * Constructor with id for existing actor instances.
     */
    public Actor(final Long id, final String name, final String image, final String biography) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.biography = biography;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    /**
     * Constructor with only id.
     */
    public Actor(final Long id) {
        this.id = id;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }
}

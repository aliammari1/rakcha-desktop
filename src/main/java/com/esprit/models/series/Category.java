package com.esprit.models.series;

import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Series> seriesList;

    /**
     * Constructor without id for creating new category instances.
     *
     * @param name
     *            the name of the category
     * @param description
     *            the description of the category
     */
    public Category(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}

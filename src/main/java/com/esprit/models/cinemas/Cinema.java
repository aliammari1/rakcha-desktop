package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.esprit.models.users.CinemaManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a cinema.
 */
@Entity
@Table(name = "cinema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private CinemaManager manager;

    @OneToMany(mappedBy = "cinema", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CinemaHall> cinemaHalls = new ArrayList<>();

    @OneToMany(mappedBy = "cinema", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CinemaRating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "cinema", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CinemaComment> comments = new ArrayList<>();

    /**
     * Constructor without id for creating new cinema instances.
     */
    public Cinema(final String name, final String address, final CinemaManager manager, final String logoPath,
            final String status) {
        this.name = name;
        this.address = address;
        this.manager = manager;
        this.logoPath = logoPath;
        this.status = status;
        this.cinemaHalls = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Get all movie sessions for this cinema across all halls.
     */
    public List<MovieSession> getMovieSessions() {
        List<MovieSession> allSessions = new ArrayList<>();
        if (cinemaHalls != null) {
            for (CinemaHall hall : cinemaHalls) {
                if (hall.getMovieSessions() != null) {
                    allSessions.addAll(hall.getMovieSessions());
                }
            }
        }
        return allSessions;
    }
}

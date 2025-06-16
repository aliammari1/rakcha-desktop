package com.esprit.models.users;

import java.sql.Date;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract User entity representing the base user model Fixed issues:
 * phoneNumber changed from int to String for proper phone handling Added proper
 * Hibernate annotations and Lombok annotations
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @EqualsAndHashCode.Include
    private String email;

    @Column(name = "photo_de_profil", length = 255)
    private String photoDeProfil;

    /**
     * Constructor without id for creating new user instances.
     */
    protected User(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.photoDeProfil = photoDeProfil;
    }
}

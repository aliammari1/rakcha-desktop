package com.esprit.models.users;

import java.sql.Date;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract User entity representing the base user model Fixed issues:
 * phoneNumber changed from int to String for proper phone handling Added proper
 * Hibernate annotations and Lombok annotations
 */

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

    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String password;

    private String role;

    private String address;

    private Date birthDate;

    @EqualsAndHashCode.Include
    private String email;

    private String photoDeProfil;

    /**
     * Create a new User instance without an id.
     *
     * @param firstName     the user's first name
     * @param lastName      the user's last name
     * @param phoneNumber   the user's phone number
     * @param password      the user's password
     * @param role          the user's role in the system
     * @param address       the user's address
     * @param birthDate     the user's birth date
     * @param email         the user's email address
     * @param photoDeProfil the path to the user's profile photo
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

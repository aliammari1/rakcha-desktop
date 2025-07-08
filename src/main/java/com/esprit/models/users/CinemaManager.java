package com.esprit.models.users;

import java.sql.Date;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Cinema Manager entity extending User with Hibernate annotations
 */

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CinemaManager extends User {

    /**
     * Constructor without id for creating new cinema manager instances.
     * 
     * @param firstName     the cinema manager's first name
     * @param lastName      the cinema manager's last name
     * @param phoneNumber   the cinema manager's phone number
     * @param password      the cinema manager's password
     * @param role          the cinema manager's role in the system
     * @param address       the cinema manager's address
     * @param birthDate     the cinema manager's birth date
     * @param email         the cinema manager's email address
     * @param photoDeProfil the path to the cinema manager's profile photo
     */
    public CinemaManager(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photoDeProfil);
    }
}

package com.esprit.models.users;

import java.sql.Date;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Admin entity extending User with Hibernate annotations
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
public class Admin extends User {

    /**
     * Constructor without id for creating new admin instances.
     * 
     * @param firstName     the admin's first name
     * @param lastName      the admin's last name
     * @param phoneNumber   the admin's phone number
     * @param password      the admin's password
     * @param role          the admin's role in the system
     * @param address       the admin's address
     * @param birthDate     the admin's birth date
     * @param email         the admin's email address
     * @param photoDeProfil the path to the admin's profile photo
     */
    public Admin(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photoDeProfil);
    }
}

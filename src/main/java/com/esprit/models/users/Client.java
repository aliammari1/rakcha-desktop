package com.esprit.models.users;

import java.sql.Date;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Client entity extending User with Hibernate annotations
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
public class Client extends User {

    /**
     * Create a Client instance without an identifier for new clients.
     *
     * @param firstName     the client's first name
     * @param lastName      the client's last name
     * @param phoneNumber   the client's phone number
     * @param password      the client's account password
     * @param role          the client's role
     * @param address       the client's address
     * @param birthDate     the client's birth date
     * @param email         the client's email address
     * @param photoDeProfil the client's profile photo path
     */
    public Client(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photoDeProfil);
    }

}

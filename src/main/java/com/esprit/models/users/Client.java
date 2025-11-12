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
     * Constructor without id for creating new client instances.
     * 
     * @param firstName     the first name of the client
     * @param lastName      the last name of the client
     * @param phoneNumber   the phone number of the client
     * @param password      the password for the client account
     * @param role          the role of the client
     * @param address       the address of the client
     * @param birthDate     the birth date of the client
     * @param email         the email address of the client
     * @param photoDeProfil the profile photo path of the client
     */
    public Client(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photoDeProfil);
    }

}


package com.esprit.models.users;

import java.sql.Date;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Admin entity extending User with Hibernate annotations
 */
@Entity
@DiscriminatorValue("ADMIN")
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
     */
    public Admin(final String firstName, final String lastName, final String phoneNumber, final String password,
            final String role, final String address, final Date birthDate, final String email,
            final String photoDeProfil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photoDeProfil);
    }
}

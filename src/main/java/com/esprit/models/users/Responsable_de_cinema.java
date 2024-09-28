package com.esprit.models.users;

import java.sql.Date;

public class Responsable_de_cinema extends User {
    public Responsable_de_cinema(final String firstName, final String lastName, final int phoneNumber, final String password, final String role, final String address, final Date birthDate, final String email, final String photo_de_profil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    public Responsable_de_cinema(final int id, final String firstName, final String lastName, final int phoneNumber, final String password, final String role, final String address, final Date birthDate, final String email, final String photo_de_profil) {
        super(id, firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    /**
     * @return String
     */
    @Override
    public String toString() {
        return "Responsable_de_cinema{" + super.toString() + "}";
    }
}

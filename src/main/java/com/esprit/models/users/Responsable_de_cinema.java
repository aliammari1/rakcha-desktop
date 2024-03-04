package com.esprit.models.users;

import java.sql.Date;

public class Responsable_de_cinema extends User {
    public Responsable_de_cinema(String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, String photo_de_profil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    public Responsable_de_cinema(int id, String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, String photo_de_profil) {
        super(id, firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    @Override
    public String toString() {
        return "Responsable_de_cinema{" + super.toString() + "}";
    }
}

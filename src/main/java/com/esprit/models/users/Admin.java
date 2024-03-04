package com.esprit.models.users;

import java.sql.Date;

public class Admin extends User {
    public Admin(String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, String photo_de_profil) {
        super(firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    public Admin(int id, String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, String photo_de_profil) {
        super(id, firstName, lastName, phoneNumber, password, role, address, birthDate, email, photo_de_profil);
    }

    @Override
    public String toString() {
        return "Admin{" + super.toString() + "}";
    }
}

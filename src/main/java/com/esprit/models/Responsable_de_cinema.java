package com.esprit.models;

import java.sql.Blob;
import java.sql.Date;

public class Responsable_de_cinema extends User {

    public Responsable_de_cinema(String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, Blob photo_de_profil) {
        super(nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil);
    }

    public Responsable_de_cinema(String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, String photo_de_profil_filepath) {
        super(nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil_filepath);
    }

    public Responsable_de_cinema(int id, String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, Blob photo_de_profil) {
        super(id, nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil);
    }
}

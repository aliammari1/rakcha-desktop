package com.esprit.models;

import java.io.File;
import java.sql.Blob;
import java.sql.Date;

public class Client extends User {
    public Client(String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, Blob photo_de_profil) {
        super(nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil);
    }

    public Client(String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, String photo_de_profil_filepath) {
        super(nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil_filepath);
    }

    public Client(int id, String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, File file) {
        super(id, nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, file);
    }


    public Client(int id, String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email, Blob photo_de_profil) {
        super(id, nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email, photo_de_profil);
    }

    public Client(int id, String nom, String prenom, int num_telephone, String password, String role, String adresse, Date date_de_naissance, String email) {
        super(id, nom, prenom, num_telephone, password, role, adresse, date_de_naissance, email);
    }
}

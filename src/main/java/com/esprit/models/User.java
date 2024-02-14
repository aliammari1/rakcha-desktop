package com.esprit.models;

import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;

public abstract class User {

    private Blob photo_de_profil;
    private int id;
    private String nom;
    private String prenom;
    private int num_telephone;
    private String password;
    private String role;
    private String adresse;
    private Date date_de_naissance;
    private String email;

    public User(String nom, String prenom, int num_telephone, String password, String role, String adresse,
            Date date_de_naissance, String email, Blob photo_de_profil) {
        this.nom = nom;
        this.prenom = prenom;
        this.num_telephone = num_telephone;
        this.password = password;
        this.role = role;
        this.adresse = adresse;
        this.date_de_naissance = date_de_naissance;
        this.email = email;
        this.photo_de_profil = photo_de_profil;
    }

    public User(String nom, String prenom, int num_telephone, String password, String role, String adresse,
            Date date_de_naissance, String email, String photo_de_profil_filepath) {
        this.nom = nom;
        this.prenom = prenom;
        this.num_telephone = num_telephone;
        this.password = password;
        this.role = role;
        this.adresse = adresse;
        this.date_de_naissance = date_de_naissance;
        this.email = email;

        File file = new File(photo_de_profil_filepath);
        try (InputStream in = new FileInputStream(file)) {
            this.photo_de_profil = DataSource.getInstance().getConnection().createBlob();
            this.photo_de_profil.setBinaryStream(1).write(in.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public User(int id, String nom, String prenom, int num_telephone, String password, String role, String adresse,
            Date date_de_naissance, String email, Blob photo_de_profil) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.num_telephone = num_telephone;
        this.password = password;
        this.role = role;
        this.adresse = adresse;
        this.date_de_naissance = date_de_naissance;
        this.email = email;
        this.photo_de_profil = photo_de_profil;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNum_telephone() {
        return num_telephone;
    }

    public void setNum_telephone(int num_telephone) {
        this.num_telephone = num_telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Date getDate_de_naissance() {
        return date_de_naissance;
    }

    public void setDate_de_naissance(Date date_de_naissance) {
        this.date_de_naissance = date_de_naissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blob getPhoto_de_profil() {
        return photo_de_profil;
    }

    public void setPhoto_de_profil(String photo_de_profil) {
        File file = new File(photo_de_profil);
        try (InputStream in = new FileInputStream(file)) {
            this.photo_de_profil.setBytes(file.length(), in.readAllBytes());
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", num_telephone=" + num_telephone +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", adresse='" + adresse + '\'' +
                ", date_de_naissance=" + date_de_naissance +
                ", email='" + email + '\'' +
                ", photo_de_profil=" + photo_de_profil +
                '}';
    }
}

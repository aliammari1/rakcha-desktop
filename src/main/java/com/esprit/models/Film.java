package com.esprit.models;

import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Time;

public class Film {
    private final String nom;
    private String categoryNom;
    private int id;
    private Blob image;
    private Time duree;
    private String description;
    private int annederalisation;
    private int idacteur;
    private int idcinema;

    // Existing constructor


    public Film(int id, String nom, Blob image, Time duree, String description, int annederalisation, int idacteur, int idcinema) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
        this.idacteur = idacteur;
        this.idcinema = idcinema;

    }

    public Film(String nom, Blob image, Time duree, String description, int annederalisation, int idacteur, int idcinema) {
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
        this.idacteur = idacteur;
        this.idcinema = idcinema;
    }

    public Film(String nom, String image_path, Time duree, String description, int annederalisation, int idacteur, int idcinema) {
        this.nom = nom;
        File file = new File(image_path);
        try (InputStream in = new FileInputStream(file)) {
            this.image = DataSource.getInstance().getConnection().createBlob();
            this.image.setBinaryStream(1).write(in.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
        this.idacteur = idacteur;
        this.idcinema = idcinema;
    }

    public Film(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.duree = null;
        this.description = null;
        this.annederalisation = 0;
        this.idacteur = 0;
        this.idcinema = 0;
    }


    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Time getDuree() {
        return duree;
    }

    public void setDuree(Time duree) {
        this.duree = duree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAnnederalisation() {
        return annederalisation;
    }

    public void setAnnederalisation(int annederalisation) {
        this.annederalisation = annederalisation;
    }

    public int getIdacteur() {
        return idacteur;
    }

    public void setIdacteur(int idacteur) {
        this.idacteur = idacteur;
    }

    public int getIdcinema() {
        return idcinema;
    }

    public void setIdcinema(int idcinema) {
        this.idcinema = idcinema;
    }

    @Override
    public String toString() {
        return "Film{" +
                "nom='" + nom + '\'' +
                ", id=" + id +
                ", image=" + image +
                ", duree=" + duree +
                ", description='" + description + '\'' +
                ", annederalisation=" + annederalisation +
                ", idacteur=" + idacteur +
                ", idcinema=" + idcinema +
                '}';
    }


}

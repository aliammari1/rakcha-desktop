package com.esprit.models;

import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;

public class Actor {
    private int id;
    private String nom;
    private Blob image;
    private int idfilm;

    public Actor(String nom, Blob image, int idfilm) {
        this.nom = nom;
        this.image = image;
        this.idfilm = idfilm;
    }

    public Actor(int id, String nom, String image_path, int idfilm) {
        this.id = id;
        File file = new File(image_path);
        try (InputStream in = new FileInputStream(file)) {
            this.image = DataSource.getInstance().getConnection().createBlob();
            this.image.setBinaryStream(1).write(in.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.image = image;
        this.idfilm = idfilm;
    }

    public Actor(int id, String nom, Blob image, int idfilm) {
        this.id = id;
        this.image = image;
        this.image = image;
        this.idfilm = idfilm;
    }

    public Actor(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.idfilm = 0;
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

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getIdfilm() {
        return idfilm;
    }

    public void setIdfilm(int idfilm) {
        this.idfilm = idfilm;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", image=" + image +
                ", idfilm=" + idfilm +
                '}';
    }
}

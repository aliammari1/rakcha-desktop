package com.esprit.models;

import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;

public class Actor {
    private final String biographie;
    private int id;
    private String nom;
    private Blob image;


    public Actor(String nom, Blob image, String biographie) {
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }

    public Actor(int id, String nom, Blob image, String biographie) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }

    public Actor(String nom, String image_path, String biographie) {
        this.nom = nom;
        File file = new File(image_path);
        try (InputStream in = new FileInputStream(file)) {
            this.image = DataSource.getInstance().getConnection().createBlob();
            this.image.setBinaryStream(1).write(in.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.biographie = biographie;
    }


    public Actor(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.biographie = null;

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

    public String getBiographie() {
        return biographie;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", image=" + image +
                ", biographie='" + biographie + '\'' +
                '}';
    }
}

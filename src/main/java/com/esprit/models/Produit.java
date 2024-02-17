package com.esprit.models;

import com.esprit.services.CategorieService;
import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;

public class Produit {



    private int id_produit;

    private String nom;
    private String prix;
    private Blob image;
    private String description;

    private Categorie categorie;

    private int quantiteP;



    public Produit(int id_produit, String nom, String prix, Blob image, String description, Categorie categorie, int quantiteP) {
        this.id_produit = id_produit;
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorie = categorie;
        this.quantiteP = quantiteP;

    }


    public Produit(String nom, String prix,Blob image, String description, Categorie categorie, int quantiteP) {
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorie = categorie;
        this.quantiteP = quantiteP;
    }

    public Produit(String nom, String prix,String image_path, String description, Categorie categorie, int quantiteP) {
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.categorie = categorie;
        this.quantiteP = quantiteP;

        File file = new File(image_path);
        try (InputStream in = new FileInputStream(file)) {
            this.image = DataSource.getInstance().getConnection().createBlob();
            this.image.setBinaryStream(1).write(in.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int getId_produit() {
        return id_produit;
    }



    public String getNom() {
        return nom;
    }

    public String getPrix() {
        return prix;
    }

    public Blob getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }


    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }



    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantiteP() {
        return quantiteP;
    }

    public void setQuantiteP(int quantiteP) {
        this.quantiteP = quantiteP;
    }


    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }


    @Override
    public String toString() {
        return "Produit{" +
                "id_produit=" + id_produit +
                ", nom='" + nom + '\'' +
                ", prix='" + prix + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", categorie=" + categorie.getNom_categorie() +
                ", quantiteP=" + quantiteP +
                '}';
    }
}

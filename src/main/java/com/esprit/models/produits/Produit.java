package com.esprit.models.produits;

import java.sql.Blob;

public class Produit {



    private int id_produit;

    private String nom;
    private int prix;
    private Blob image;
    private String description;

    private Categorie_Produit categorieProduit;

    private int quantiteP;



    public Produit(int id_produit, String nom, int prix, Blob image, String description, Categorie_Produit categorieProduit, int quantiteP) {
        this.id_produit = id_produit;
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;

    }


    public Produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public Produit(String nom, int prix, Blob image, String description, Categorie_Produit categorieProduit, int quantiteP) {
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;
    }

    public Produit() {

    }


    public int getId_produit() {
        return id_produit;
    }



    public String getNom() {
        return nom;
    }

    public int getPrix() {
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

    public void setPrix(int prix) {
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


    public Categorie_Produit getCategorie() {
        return categorieProduit;
    }


    public void setCategorie(Categorie_Produit categorieProduit) {
        this.categorieProduit = categorieProduit;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id_produit=" + id_produit +
                ", nom='" + nom + '\'' +
                ", prix='" + prix + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", categorie=" + categorieProduit.getNom_categorie() +
                ", quantiteP=" + quantiteP +
                '}';
    }


    public Categorie_Produit getId_categorieProduit() {
        return categorieProduit;
    }

    public String getNom_categorie() {
        return categorieProduit.getNom_categorie();
    }
}

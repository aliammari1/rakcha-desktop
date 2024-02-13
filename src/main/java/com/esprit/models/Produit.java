package com.esprit.models;

public class Produit {


    private int id_produit;

    private String nom;
    private String prix;
    private String image;
    private String description;


    public Produit(int id_produit,  String nom, String prix, String image, String description) {
        this.id_produit = id_produit;

        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;

    }

    public Produit(String nom, String prix, String image, String description) {
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;

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

    public String getImage() {
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

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id_produit=" + id_produit +
                ", nom='" + nom + '\'' +
                ", prix='" + prix + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

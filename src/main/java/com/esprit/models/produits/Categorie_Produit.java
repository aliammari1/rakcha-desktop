package com.esprit.models.produits;

public class Categorie_Produit {

    private int id_categorie;
    private String nom_categorie;
    private String description;

    public Categorie_Produit(){ }

    public Categorie_Produit(int id_categorie, String nom_categorie, String description) {
        this.id_categorie = id_categorie;
        this.nom_categorie = nom_categorie;
        this.description = description;
    }

    public Categorie_Produit(String nom_categorie, String description) {
        this.nom_categorie = nom_categorie;
        this.description = description;
    }




    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    public String getNom_categorie() {
        return nom_categorie;
    }

    public void setNom_categorie(String nom_categorie) {
        this.nom_categorie = nom_categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id_categorieProduit=" + id_categorie +
                ", nom='" + nom_categorie + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}

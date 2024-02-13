package com.esprit.models;

public class Categorie {

    private int id_categorieProduit;
    private String nom;
    private String description;

    public Categorie(int id_categorieProduit, String nom, String description) {
        this.id_categorieProduit = id_categorieProduit;
        this.nom = nom;
        this.description = description;
    }

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public int getId_categorieProduit() {
        return id_categorieProduit;
    }

    public void setId_categorieProduit(int id_categorieProduit) {
        this.id_categorieProduit = id_categorieProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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
                "id_categorieProduit=" + id_categorieProduit +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

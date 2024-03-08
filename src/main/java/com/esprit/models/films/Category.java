package com.esprit.models.films;

public class Category {
    private String nom;
    private int id;
    private String description;


    public Category(String nom, String description) {
        this.nom = nom;

        this.description = description;
    }

    public Category(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;

    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "category{" +
                "nom='" + nom + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}

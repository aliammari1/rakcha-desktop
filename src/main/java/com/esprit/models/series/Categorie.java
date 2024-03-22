package com.esprit.models.series;

public class Categorie {
    private int idcategorie;
    private String nom;
    private String description;

    public Categorie() {
    }

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }



    public int getIdcategorie() {
        return idcategorie;
    }

    public void setIdcategorie(int idcategorie) {
        this.idcategorie = idcategorie;
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
}

package com.esprit.models.series;

public class Categorie {
    private int idcategorie;
    private String nom;
    private String description;

    public Categorie() {
    }

    public Categorie(final String nom, final String description) {
        this.nom = nom;
        this.description = description;
    }

    /**
     * @return int
     */
    public int getIdcategorie() {
        return this.idcategorie;
    }

    /**
     * @param idcategorie
     */
    public void setIdcategorie(final int idcategorie) {
        this.idcategorie = idcategorie;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

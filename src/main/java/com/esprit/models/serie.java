package com.esprit.models;

public class serie {

    public static int idserie;
    public String nom;
    public String resume;
    public String directeur;

    public String pays;



    public static int getIdserie() {
        return idserie;
    }

    public void setIdserie(int idserie) {
        this.idserie = idserie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getDirecteur() {
        return directeur;
    }

    public void setDirecteur(String directeur) {
        this.directeur = directeur;
    }



    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public serie(String nom, String resume, String directeur, String pays) {
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;

        this.pays = pays;
    }

    public serie(int idserie, String nom, String resume,  String directeur, String pays) {
        this.idserie = idserie;
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;
        this.pays = pays;
    }

    @Override
    public String toString() {
        return "serie{" +
                "idserie=" + idserie +
                ", nom='" + nom + '\'' +
                ", resume='" + resume + '\'' +
                ", directeur='" + directeur + '\'' +
                ", pays='" + pays + '\'' +
                '}';
    }

}

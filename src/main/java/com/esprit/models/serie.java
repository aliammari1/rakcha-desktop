package com.esprit.models;
import java.sql.Blob;

public class serie {

    public static int idserie;



    public String nom;
    public String resume;
    public String directeur;
    private Blob image;

    public serie(int idserie, String serie1, String resumeserie, String directeurdelaserie, String pays, String image1) {
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

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


    public serie(int idserie,String nom, String resume, String directeur, Blob image, String pays) {
        this.idserie = idserie;
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;
        this.image = image;
        this.pays = pays;
    }

    public serie(String nom, String resume, String directeur, Blob image, String pays) {
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;
        this.image = image;
        this.pays = pays;
    }
    @Override
    public String toString() {
        return "serie{" +
                "nom='" + nom + '\'' +
                ", resume='" + resume + '\'' +
                ", directeur='" + directeur + '\'' +
                ", image=" + image +
                ", pays='" + pays + '\'' +
                '}';
    }


}

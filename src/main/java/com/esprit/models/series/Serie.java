package com.esprit.models.series;


public class Serie {
    public  int idserie;
    public String nom;
    public String resume;
    public String directeur;
    public String pays;
    private String image;
    private int idcategorie;

    public Serie() {
    }

    public Serie(String s, String s1, String s2, String s3, String image) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public int getIdcategorie() {
        return idcategorie;
    }

    public void setIdcategorie(int idcategorie) {
        this.idcategorie = idcategorie;
    }

    public int getIdserie() {
        return idserie;
    }

    public void setIdserie(int idserie) {
        this.idserie = idserie;
    }

    @Override
    public String toString() {
        return "Serie{" +
                "idserie=" + idserie +
                ", nom='" + nom + '\'' +
                ", resume='" + resume + '\'' +
                ", directeur='" + directeur + '\'' +
                ", pays='" + pays + '\'' +
                ", image='" + image + '\'' +
                ", idcategorie=" + idcategorie +
                '}';
    }
}

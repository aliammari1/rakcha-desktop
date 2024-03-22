package com.esprit.models.series;


public class Serie {
    public int idserie;
    public String nom;
    public String resume;
    public String directeur;
    public String pays;
    private String image;

    private int liked;

    private int nbLikes;

    private int disliked;

    private int nbDislikes;
    private int idcategorie;

    private int clickLikes;
    private int clickDislikes;

    private int clickFavoris;

    public Serie() {
    }

    public Serie(String s, String s1, String s2, String s3, String image) {
    }

    public Serie(int idserie, String nom, String resume, String directeur, String pays, String image, int liked, int nbLikes, int disliked, int nbDislikes, int idcategorie) {
        this.idserie = idserie;
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;
        this.pays = pays;
        this.image = image;
        this.liked = liked;
        this.nbLikes = nbLikes;
        this.disliked = disliked;
        this.nbDislikes = nbDislikes;
        this.idcategorie = idcategorie;
        this.clickLikes = 0;
        this.clickDislikes = 0;
        this.clickFavoris = 0;
    }

    public Serie(String nom, String resume, String directeur, String pays, String image, int liked, int nbLikes, int disliked, int nbDislikes, int idcategorie) {
        this.nom = nom;
        this.resume = resume;
        this.directeur = directeur;
        this.pays = pays;
        this.image = image;
        this.liked = liked;
        this.nbLikes = nbLikes;
        this.disliked = disliked;
        this.nbDislikes = nbDislikes;
        this.idcategorie = idcategorie;
        this.clickLikes = 0;
        this.clickDislikes = 0;
        this.clickFavoris = 0;
    }

    public int getIdserie() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getNbLikes() {
        return nbLikes;
    }

    public void setNbLikes(int nbLikes) {
        this.nbLikes = nbLikes;
    }

    public int getDisliked() {
        return disliked;
    }

    public void setDisliked(int disliked) {
        this.disliked = disliked;
    }

    public int getNbDislikes() {
        return nbDislikes;
    }

    public void setNbDislikes(int nbDislikes) {
        this.nbDislikes = nbDislikes;
    }

    public int getIdcategorie() {
        return idcategorie;
    }

    public void setIdcategorie(int idcategorie) {
        this.idcategorie = idcategorie;
    }

    public int getClickLikes() {
        return clickLikes;
    }

    public void setClickLikes(int clickLikes) {
        this.clickLikes = clickLikes;
    }

    public int getClickDislikes() {
        return clickDislikes;
    }

    public void setClickDislikes(int clickDislikes) {
        this.clickDislikes = clickDislikes;
    }

    public int getClickFavoris() {
        return clickFavoris;
    }

    public void setClickFavoris(int clickFavoris) {
        this.clickFavoris = clickFavoris;
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

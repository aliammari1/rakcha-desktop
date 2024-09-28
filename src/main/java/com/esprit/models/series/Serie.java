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

    public Serie(final String s, final String s1, final String s2, final String s3, final String image) {
    }

    public Serie(final int idserie, final String nom, final String resume, final String directeur, final String pays, final String image, final int liked, final int nbLikes, final int disliked, final int nbDislikes, final int idcategorie) {
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
        clickLikes = 0;
        clickDislikes = 0;
        clickFavoris = 0;
    }

    public Serie(final String nom, final String resume, final String directeur, final String pays, final String image, final int liked, final int nbLikes, final int disliked, final int nbDislikes, final int idcategorie) {
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
        clickLikes = 0;
        clickDislikes = 0;
        clickFavoris = 0;
    }

    /**
     * @return int
     */
    public int getIdserie() {
        return this.idserie;
    }

    /**
     * @param idserie
     */
    public void setIdserie(final int idserie) {
        this.idserie = idserie;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }

    public String getResume() {
        return this.resume;
    }

    public void setResume(final String resume) {
        this.resume = resume;
    }

    public String getDirecteur() {
        return this.directeur;
    }

    public void setDirecteur(final String directeur) {
        this.directeur = directeur;
    }

    public String getPays() {
        return this.pays;
    }

    public void setPays(final String pays) {
        this.pays = pays;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public int getLiked() {
        return this.liked;
    }

    public void setLiked(final int liked) {
        this.liked = liked;
    }

    public int getNbLikes() {
        return this.nbLikes;
    }

    public void setNbLikes(final int nbLikes) {
        this.nbLikes = nbLikes;
    }

    public int getDisliked() {
        return this.disliked;
    }

    public void setDisliked(final int disliked) {
        this.disliked = disliked;
    }

    public int getNbDislikes() {
        return this.nbDislikes;
    }

    public void setNbDislikes(final int nbDislikes) {
        this.nbDislikes = nbDislikes;
    }

    public int getIdcategorie() {
        return this.idcategorie;
    }

    public void setIdcategorie(final int idcategorie) {
        this.idcategorie = idcategorie;
    }

    public int getClickLikes() {
        return this.clickLikes;
    }

    public void setClickLikes(final int clickLikes) {
        this.clickLikes = clickLikes;
    }

    public int getClickDislikes() {
        return this.clickDislikes;
    }

    public void setClickDislikes(final int clickDislikes) {
        this.clickDislikes = clickDislikes;
    }

    public int getClickFavoris() {
        return this.clickFavoris;
    }

    public void setClickFavoris(final int clickFavoris) {
        this.clickFavoris = clickFavoris;
    }

    @Override
    public String toString() {
        return "Serie{"
                + "idserie=" + this.idserie
                + ", nom='" + this.nom + '\''
                + ", resume='" + this.resume + '\''
                + ", directeur='" + this.directeur + '\''
                + ", pays='" + this.pays + '\''
                + ", image='" + this.image + '\''
                + ", idcategorie=" + this.idcategorie
                + '}';
    }
}

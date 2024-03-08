package com.esprit.models.series;


public class Episode {
    public int idepisode;
    public String titre;
    public  int  numeroepisode;
    public int saison;
    private String image;
    private String video;
    private int idserie;
    ///
    private Serie serie;
///
    public Episode() {
        this.serie = new Serie();
    }

    public Episode(String titre, int numeroepisode, int saison, String image,String video, int idserie) {
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
        this.image = image;
        this.video=video;
        this.idserie = idserie;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getIdepisode() {
        return idepisode;
    }

    public void setIdepisode(int idepisode) {
        this.idepisode = idepisode;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getNumeroepisode() {
        return numeroepisode;
    }

    public void setNumeroepisode(int numeroepisode) {
        this.numeroepisode = numeroepisode;
    }

    public int getSaison() {
        return saison;
    }

    public void setSaison(int saison) {
        this.saison = saison;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIdserie() {
        return idserie;
    }

    public void setIdserie(int idserie) {
        this.idserie = idserie;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "idepisode=" + idepisode +
                ", titre='" + titre + '\'' +
                ", numeroepisode=" + numeroepisode +
                ", saison=" + saison +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", idserie=" + idserie +
                '}';
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    // Méthode pour obtenir le nom de la série associée à l'épisode
    public String getNomSerie() {
        return serie != null ? serie.getNom() : null;
    }




}

package com.esprit.models.series;

public class Episode {
    public int idepisode;
    public String titre;
    public int numeroepisode;
    public int saison;
    private String image;
    private String video;
    private int idserie;
    ///
    private Serie serie;

    ///
    public Episode() {
        serie = new Serie();
    }

    public Episode(final String titre, final int numeroepisode, final int saison, final String image, final String video, final int idserie) {
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
        this.image = image;
        this.video = video;
        this.idserie = idserie;
    }

    /**
     * @return String
     */
    public String getVideo() {
        return this.video;
    }

    /**
     * @param video
     */
    public void setVideo(final String video) {
        this.video = video;
    }

    public int getIdepisode() {
        return this.idepisode;
    }

    public void setIdepisode(final int idepisode) {
        this.idepisode = idepisode;
    }

    public String getTitre() {
        return this.titre;
    }

    public void setTitre(final String titre) {
        this.titre = titre;
    }

    public int getNumeroepisode() {
        return this.numeroepisode;
    }

    public void setNumeroepisode(final int numeroepisode) {
        this.numeroepisode = numeroepisode;
    }

    public int getSaison() {
        return this.saison;
    }

    public void setSaison(final int saison) {
        this.saison = saison;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public int getIdserie() {
        return this.idserie;
    }

    public void setIdserie(final int idserie) {
        this.idserie = idserie;
    }

    @Override
    public String toString() {
        return "Episode{"
                + "idepisode=" + this.idepisode
                + ", titre='" + this.titre + '\''
                + ", numeroepisode=" + this.numeroepisode
                + ", saison=" + this.saison
                + ", image='" + this.image + '\''
                + ", video='" + this.video + '\''
                + ", idserie=" + this.idserie
                + '}';
    }

    public Serie getSerie() {
        return this.serie;
    }

    public void setSerie(final Serie serie) {
        this.serie = serie;
    }

    // Méthode pour obtenir le nom de la série associée à l'épisode
    public String getNomSerie() {
        return null != serie ? this.serie.getNom() : null;
    }
}

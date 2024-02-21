package com.esprit.models;

import java.sql.Blob;

public class episode {
           public int idepisode;
           public String titre;
           public  int  numeroepisode;
           public int saison;
           private Blob image;
           private serie serie;


    public serie getSerie() {
        return serie;
    }

    public void setSerie(serie serie) {
        this.serie = serie;
    }

    public episode(int i, String premierEpisode, int saison) {
    }
    public episode(String titre, int numeroepisode, int saison, Blob image,serie serie) {
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
        this.image = image;
        this.serie=serie;   }

    public episode(int idepisode, String titre, int numeroepisode, int saison, Blob image,serie serie) {
        this.idepisode = idepisode;
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
        this.image = image;
        this.serie=serie;
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
    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "episode{" +
                "idepisode=" + idepisode +
                ", titre='" + titre + '\'' +
                ", numeroepisode=" + numeroepisode +
                ", saison=" + saison +
                ", image=" + image +
                ", serie=" + serie +
                '}';
    }


    public int getIdserie() {
    }

}

package com.esprit.models;

public class episode {
           public int idepisode;
           public String titre;
           public  int  numeroepisode;
           public int saison;

    public episode(int i, String premierEpisode, int saison) {
    }

    public episode(String titre, int numeroepisode, int saison) {
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
    }

    public episode(int idepisode, String titre, int numeroepisode, int saison) {
        this.idepisode = idepisode;
        this.titre = titre;
        this.numeroepisode = numeroepisode;
        this.saison = saison;
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

    @Override
    public String toString() {
        return "episode{" +
                "idepisode=" + idepisode +
                ", titre='" + titre + '\'' +
                ", numeroepisode=" + numeroepisode +
                ", saison=" + saison +
                '}';
    }

}

package com.esprit.models.cinemas;

import com.esprit.models.films.Filmcinema;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

public class Seance {
    private Filmcinema filmcinema;
    private int id_seance;
    private Salle salle;
    private Time HD;
    private Time HF;
    private Date date;
    private double prix;

    public Seance(int id_seance, Salle salle, Time HD, Time HF, Date date, double prix, Filmcinema filmcinema) {
        this.id_seance = id_seance;
        this.filmcinema = filmcinema;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.prix = prix;
    }

    public Seance(Salle salle, Time HD, Time HF, Date date, double prix, Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.prix = prix;
    }

    public Seance(int idSeance, Salle salle, Time HD, Time HF, LocalDate date, int prix, Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
        this.id_seance = id_seance;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = Date.valueOf(date);
        this.prix = prix;
    }

    public Filmcinema getFilmcinema() {
        return filmcinema;
    }

    public void setFilmcinema(Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
    }

    public int getId_seance() {
        return id_seance;
    }

    public void setId_seance(int id_seance) {
        this.id_seance = id_seance;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Time getHD() {
        return HD;
    }

    public void setHD(Time HD) {
        this.HD = HD;
    }

    public Time getHF() {
        return HF;
    }

    public void setHF(Time HF) {
        this.HF = HF;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "id_seance=" + id_seance +
                ", salle=" + salle +
                ", HD=" + HD +
                ", HF=" + HF +
                ", date=" + date +
                ", prix=" + prix +
                '}';
    }

    public Salle getId_salle() {
        return salle;
    }

    public String getNom_salle() {
        return salle.getNom_salle();
    }
}

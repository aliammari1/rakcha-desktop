package com.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Seance {

    private int id_seance;
    private Film film;
    private Salle salle;
    private Time HD;
    private Time HF;
    private Date date;
    private Cinema cinema ;
    private double prix;

    public Seance(int id_seance, Film film, Salle salle, Time HD, Time HF, Date date, Cinema cinema, double prix) {
        this.id_seance = id_seance;
        this.film = film;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.cinema = cinema;
        this.prix = prix;
    }

    public Seance(Film film, Salle salle, Time HD, Time HF, Date date, Cinema cinema, double prix) {
        this.film = film;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.cinema = cinema;
        this.prix = prix;
    }

    public int getId_seance() {
        return id_seance;
    }

    public void setId_seance(int id_seance) {
        this.id_seance = id_seance;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
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

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
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
                ", film=" + film +
                ", salle=" + salle +
                ", HD=" + HD +
                ", HF=" + HF +
                ", date=" + date +
                ", cinema=" + cinema +
                ", prix=" + prix +
                '}';
    }

    public Cinema getId_cinema() {
        return cinema;
    }

    public String getNom_cinema() {
        return cinema.getNom();
    }

    public Film getId_film() {
        return film;
    }

    public String getNom_film() {
        return film.getNom();
    }

    public Salle getId_salle() {
        return salle;
    }

    public String getNom_salle() {
        return salle.getNom_salle();
    }
}

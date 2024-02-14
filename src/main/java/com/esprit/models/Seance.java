package com.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Seance {

    private int id_seance;
    private int id_film;
    private int id_salle;
    private Time HD;
    private Time HF;
    private Date date;

    public Seance(int id_seance, int id_film, int id_salle, Time HD, Time HF, Date date) {
        this.id_seance = id_seance;
        this.id_film = id_film;
        this.id_salle = id_salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
    }

    public Seance(int id_film, int id_salle, Time HD, Time HF, Date date) {
        this.id_film = id_film;
        this.id_salle = id_salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
    }

    public int getId_seance() {
        return id_seance;
    }

    public void setId_seance(int id_seance) {
        this.id_seance = id_seance;
    }

    public int getId_film() {
        return id_film;
    }

    public void setId_film(int id_film) {
        this.id_film = id_film;
    }

    public int getId_salle() {
        return id_salle;
    }

    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
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

    @Override
    public String toString() {
        return "Seance{" +
                "id_seance=" + id_seance +
                ", id_film=" + id_film +
                ", id_salle=" + id_salle +
                ", HD=" + HD +
                ", HF=" + HF +
                ", date=" + date +
                '}';
    }
}

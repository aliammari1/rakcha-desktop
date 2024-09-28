package com.esprit.models.cinemas;

import com.esprit.models.films.Filmcinema;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

/**
 * Represents a movie session in a cinema.
 */
public class Seance {
    private Filmcinema filmcinema;
    private int id_seance;
    private Salle salle;
    private Time HD;
    private Time HF;
    private Date date;
    private double prix;

    /**
     * Constructs a Seance object with the specified parameters.
     *
     * @param id_seance  the ID of the seance
     * @param salle      the salle where the seance takes place
     * @param HD         the start time of the seance
     * @param HF         the end time of the seance
     * @param date       the date of the seance
     * @param prix       the price of the seance
     * @param filmcinema the filmcinema associated with the seance
     */
    public Seance(final int id_seance, final Salle salle, final Time HD, final Time HF, final Date date, final double prix, final Filmcinema filmcinema) {
        this.id_seance = id_seance;
        this.filmcinema = filmcinema;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.prix = prix;
    }

    /**
     * Constructs a Seance object with the specified parameters.
     *
     * @param salle      the salle where the seance takes place
     * @param HD         the start time of the seance
     * @param HF         the end time of the seance
     * @param date       the date of the seance
     * @param prix       the price of the seance
     * @param filmcinema the filmcinema associated with the seance
     */
    public Seance(final Salle salle, final Time HD, final Time HF, final Date date, final double prix, final Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = date;
        this.prix = prix;
    }

    /**
     * Constructs a Seance object with the specified parameters.
     *
     * @param idSeance   the ID of the seance
     * @param salle      the salle where the seance takes place
     * @param HD         the start time of the seance
     * @param HF         the end time of the seance
     * @param date       the date of the seance
     * @param prix       the price of the seance
     * @param filmcinema the filmcinema associated with the seance
     */
    public Seance(final int idSeance, final Salle salle, final Time HD, final Time HF, final LocalDate date, final int prix, final Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
        id_seance = this.id_seance;
        this.salle = salle;
        this.HD = HD;
        this.HF = HF;
        this.date = Date.valueOf(date);
        this.prix = prix;
    }

    /**
     * Returns the filmcinema associated with the seance.
     *
     * @return the filmcinema associated with the seance
     */
    public Filmcinema getFilmcinema() {
        return this.filmcinema;
    }

    /**
     * Sets the filmcinema associated with the seance.
     *
     * @param filmcinema the filmcinema to be set
     */
    public void setFilmcinema(final Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
    }

    // Other getter and setter methods...

    /**
     * Returns a string representation of the Seance object.
     *
     * @return a string representation of the Seance object
     */
    @Override
    public String toString() {
        return "Seance{"
                + "id_seance=" + this.id_seance
                + ", salle=" + this.salle
                + ", HD=" + this.HD
                + ", HF=" + this.HF
                + ", date=" + this.date
                + ", prix=" + this.prix
                + '}';
    }

    /**
     * Returns the salle ID associated with the seance.
     *
     * @return the salle ID associated with the seance
     */
    public Salle getId_salle() {
        return this.salle;
    }

    /**
     * Returns the name of the salle associated with the seance.
     *
     * @return the name of the salle associated with the seance
     */
    public String getNom_salle() {
        return this.salle.getNom_salle();
    }

    public Salle getSalle() {
        return this.salle;
    }

    public void setSalle(final Salle salle) {
        this.salle = salle;
    }

    public Time getHD() {
        return this.HD;
    }

    public void setHD(final Time HD) {
        this.HD = HD;
    }

    public Time getHF() {
        return this.HF;
    }

    public void setHF(final Time HF) {
        this.HF = HF;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public double getPrix() {
        return this.prix;
    }

    public void setPrix(final double prix) {
        this.prix = prix;
    }

    public int getId_seance() {
        return this.id_seance;
    }
}

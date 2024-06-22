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
     * @param id_seance   the ID of the seance
     * @param salle       the salle where the seance takes place
     * @param HD          the start time of the seance
     * @param HF          the end time of the seance
     * @param date        the date of the seance
     * @param prix        the price of the seance
     * @param filmcinema  the filmcinema associated with the seance
     */
    public Seance(int id_seance, Salle salle, Time HD, Time HF, Date date, double prix, Filmcinema filmcinema) {
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
     * @param salle       the salle where the seance takes place
     * @param HD          the start time of the seance
     * @param HF          the end time of the seance
     * @param date        the date of the seance
     * @param prix        the price of the seance
     * @param filmcinema  the filmcinema associated with the seance
     */
    public Seance(Salle salle, Time HD, Time HF, Date date, double prix, Filmcinema filmcinema) {
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
     * @param idSeance    the ID of the seance
     * @param salle       the salle where the seance takes place
     * @param HD          the start time of the seance
     * @param HF          the end time of the seance
     * @param date        the date of the seance
     * @param prix        the price of the seance
     * @param filmcinema  the filmcinema associated with the seance
     */
    public Seance(int idSeance, Salle salle, Time HD, Time HF, LocalDate date, int prix, Filmcinema filmcinema) {
        this.filmcinema = filmcinema;
        this.id_seance = id_seance;
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
        return filmcinema;
    }
    /**
     * Sets the filmcinema associated with the seance.
     *
     * @param filmcinema the filmcinema to be set
     */
    public void setFilmcinema(Filmcinema filmcinema) {
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
        return "Seance{" +
                "id_seance=" + id_seance +
                ", salle=" + salle +
                ", HD=" + HD +
                ", HF=" + HF +
                ", date=" + date +
                ", prix=" + prix +
                '}';
    }
    /**
     * Returns the salle ID associated with the seance.
     *
     * @return the salle ID associated with the seance
     */
    public Salle getId_salle() {
        return salle;
    }
    /**
     * Returns the name of the salle associated with the seance.
     *
     * @return the name of the salle associated with the seance
     */
    public String getNom_salle() {
        return salle.getNom_salle();
    }
    public Salle getSalle() {
        return salle;
    }
    public Time getHD() {
        return HD;
    }
    public Time getHF() {
        return HF;
    }
    public Date getDate() {
        return date;
    }
    public double getPrix() {
        return prix;
    }
    public int getId_seance() {
        return id_seance;
    }
    public void setSalle(Salle salle) {
        this.salle = salle;
    }
    public void setHD(Time HD) {
        this.HD = HD;
    }
    public void setHF(Time HF) {
        this.HF = HF;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }
}

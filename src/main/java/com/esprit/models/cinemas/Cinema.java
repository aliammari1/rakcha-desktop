package com.esprit.models.cinemas;
import com.esprit.models.users.Responsable_de_cinema;
/**
 * Represents a cinema.
 */
public class Cinema {
    private final Responsable_de_cinema responsable;
    private int id_cinema;
    private String nom;
    private String adresse;
    private String logo;
    private String Statut;
    /**
     * Constructs a Cinema object with the specified parameters.
     *
     * @param id_cinema    the ID of the cinema
     * @param nom          the name of the cinema
     * @param adresse      the address of the cinema
     * @param responsable  the responsible person for the cinema
     * @param logo         the logo of the cinema
     * @param statut       the status of the cinema
     */
    public Cinema(int id_cinema, String nom, String adresse, Responsable_de_cinema responsable, String logo, String statut) {
        this.id_cinema = id_cinema;
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        Statut = statut;
    }
    /**
     * Constructs a Cinema object with the specified parameters.
     *
     * @param nom          the name of the cinema
     * @param adresse      the address of the cinema
     * @param responsable  the responsible person for the cinema
     * @param logo         the logo of the cinema
     * @param statut       the status of the cinema
     */
    public Cinema(String nom, String adresse, Responsable_de_cinema responsable, String logo, String statut) {
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        Statut = statut;
    }
    /**
     * Returns the ID of the cinema.
     *
     * @return the ID of the cinema
     */
    public int getId_cinema() {
        return id_cinema;
    }
    /**
     * Sets the ID of the cinema.
     *
     * @param id_cinema the ID of the cinema
     */
    public void setId_cinema(int id_cinema) {
        this.id_cinema = id_cinema;
    }
    /**
     * Returns the name of the cinema.
     *
     * @return the name of the cinema
     */
    public String getNom() {
        return nom;
    }
    /**
     * Sets the name of the cinema.
     *
     * @param nom the name of the cinema
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    /**
     * Returns the address of the cinema.
     *
     * @return the address of the cinema
     */
    public String getAdresse() {
        return adresse;
    }
    /**
     * Sets the address of the cinema.
     *
     * @param adresse the address of the cinema
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    /**
     * Returns the responsible person for the cinema.
     *
     * @return the responsible person for the cinema
     */
    public Responsable_de_cinema getResponsable() {
        return responsable;
    }
    /**
     * Returns the logo of the cinema.
     *
     * @return the logo of the cinema
     */
    public String getLogo() {
        return logo;
    }
    /**
     * Sets the logo of the cinema.
     *
     * @param logo the logo of the cinema
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
    /**
     * Returns the status of the cinema.
     *
     * @return the status of the cinema
     */
    public String getStatut() {
        return Statut;
    }
    /**
     * Sets the status of the cinema.
     *
     * @param statut the status of the cinema
     */
    public void setStatut(String statut) {
        Statut = statut;
    }
    /**
     * Returns a string representation of the Cinema object.
     *
     * @return a string representation of the Cinema object
     */
    @Override
    public String toString() {
        return "Cinema{" +
                "id_cinema=" + id_cinema +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", responsable='" + responsable + '\'' +
                ", logo=" + logo +
                ", Statut='" + Statut + '\'' +
                '}';
    }
}

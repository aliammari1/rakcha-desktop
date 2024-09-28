package com.esprit.models.films;

/**
 * The Actorfilm class represents the association between an actor and a film.
 */
public class Actorfilm {
    private Actor idactor;
    private Film idfilm;

    /**
     * Constructs an Actorfilm object with the specified actor and film.
     *
     * @param idactor the actor associated with the film
     * @param idfilm  the film associated with the actor
     */
    public Actorfilm(final Actor idactor, final Film idfilm) {
        this.idactor = idactor;
        this.idfilm = idfilm;
    }

    /**
     * Returns the actor associated with the film.
     *
     * @return the actor associated with the film
     */
    public Actor getIdactor() {
        return this.idactor;
    }

    /**
     * Sets the actor associated with the film.
     *
     * @param idactor the actor to be set
     */
    public void setIdactor(final Actor idactor) {
        this.idactor = idactor;
    }

    /**
     * Returns the film associated with the actor.
     *
     * @return the film associated with the actor
     */
    public Film getIdfilm() {
        return this.idfilm;
    }

    /**
     * Sets the film associated with the actor.
     *
     * @param idfilm the film to be set
     */
    public void setIdfilm(final Film idfilm) {
        this.idfilm = idfilm;
    }

    /**
     * Returns a string representation of the Actorfilm object.
     *
     * @return a string representation of the Actorfilm object
     */
    @Override
    public String toString() {
        return "Actorfilm{"
                + "idactor=" + this.idactor
                + ", idfilm=" + this.idfilm
                + '}';
    }
}

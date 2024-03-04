package com.esprit.models.films;

public class Actorfilm {
    private Actor idactor;
    private Film idfilm;

    public Actorfilm(Actor idactor, Film idfilm) {
        this.idactor = idactor;
        this.idfilm = idfilm;
    }

    public Actor getIdactor() {
        return idactor;
    }

    public void setIdactor(Actor idactor) {
        this.idactor = idactor;
    }

    public Film getIdfilm() {
        return idfilm;
    }

    public void setIdfilm(Film idfilm) {
        this.idfilm = idfilm;
    }

    @Override
    public String toString() {
        return "Actorfilm{" +
                "idactor=" + idactor +
                ", idfilm=" + idfilm +
                '}';
    }
}

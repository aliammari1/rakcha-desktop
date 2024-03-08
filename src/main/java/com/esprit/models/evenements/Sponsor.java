package com.esprit.models.evenements;

import java.sql.Blob;

public class Sponsor {

    private int id;

    private String nomSociete;

    private Blob logo;

    public Sponsor(int id, String nomSociete, Blob logo) {
        this.id = id;
        this.nomSociete = nomSociete;
        this.logo = logo;
    }

    public Sponsor(String nomSociete, Blob logo) {
        this.nomSociete = nomSociete;
        this.logo = logo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomSociete() {
        return nomSociete;
    }

    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }

    public Blob getLogo() {
        return logo;
    }

    public void setLogo(Blob logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", nomSociete='" + nomSociete + '\'' +
                '}';
    }


}

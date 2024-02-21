package com.esprit.models;
import java.util.Base64;

public class Sponsor {

private int id;

private String nomSociete;

private byte[] logo;

    public Sponsor(int id, String nomSociete, byte[] logo) {
        this.id = id;
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

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
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

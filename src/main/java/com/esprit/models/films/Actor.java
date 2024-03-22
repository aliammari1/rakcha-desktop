package com.esprit.models.films;

public class Actor {
    private String biographie;
    private int id;
    private String nom;
    private String image;
    private int numberOfAppearances;

    public Actor(int id, String nom, String image, String biographie) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }


    public Actor(String nom, String image, String biographie) {
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }

    public Actor(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.biographie = null;

    }

    public Actor(int id, String nom, String img, String s, int numberOfAppearances) {
        this.id = id;
        this.nom = nom;
        this.biographie = s;
        this.image = img;
        this.numberOfAppearances = numberOfAppearances;
    }

    public int getNumberOfAppearances() {
        return numberOfAppearances;
    }

    public void setNumberOfAppearances(int numberOfAppearances) {
        this.numberOfAppearances = numberOfAppearances;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBiographie() {
        return biographie;
    }

    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", image=" + image +
                ", biographie='" + biographie + '\'' +
                '}';
    }
}

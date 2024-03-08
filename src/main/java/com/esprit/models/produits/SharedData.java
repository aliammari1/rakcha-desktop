package com.esprit.models.produits;

public class SharedData {
    private static SharedData instance = null;
    private double totalPrix;

    public SharedData() {
        // Constructeur privé pour empêcher l'instanciation directe
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public double getTotalPrix() {
        return totalPrix;
    }

    public void setTotalPrix(double totalPrix) {
        this.totalPrix = totalPrix;
    }
}


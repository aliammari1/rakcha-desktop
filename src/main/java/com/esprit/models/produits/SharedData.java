package com.esprit.models.produits;
/**
 * The SharedData class represents a shared data object that stores the total price.
 * It follows the Singleton design pattern to ensure that only one instance of the class exists.
 */
public class SharedData {
    private static SharedData instance = null;
    private double totalPrix;
    /**
     * Private constructor to prevent direct instantiation of the class.
     */
    public SharedData() {
    }
    /**
     * Returns the instance of the SharedData class.
     * If the instance does not exist, a new instance is created.
     *
     * @return The instance of the SharedData class.
     */
    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }
    /**
     * Returns the total price stored in the shared data object.
     *
     * @return The total price.
     */
    public double getTotalPrix() {
        return totalPrix;
    }
    /**
     * Sets the total price in the shared data object.
     *
     * @param totalPrix The total price to be set.
     */
    public void setTotalPrix(double totalPrix) {
        this.totalPrix = totalPrix;
    }
}

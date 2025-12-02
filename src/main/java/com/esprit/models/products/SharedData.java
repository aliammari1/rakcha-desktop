package com.esprit.models.products;

import lombok.Getter;
import lombok.Setter;

/**
 * The SharedData class represents a shared data object that stores the total
 * price. It follows the Singleton design pattern to ensure that only one
 * instance of the class exists.
 */
@Getter
@Setter
/**
 * Product management entity class for the RAKCHA application. Manages product
 * data and relationships with database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class SharedData {

    private static SharedData instance;
    private double totalPrice;

    /**
     * Private constructor to prevent direct instantiation of the class.
     */
    private SharedData() {
    }


    /**
     * Get the singleton SharedData instance, creating it if necessary.
     *
     * @return the current SharedData singleton instance
     */
    public static SharedData getInstance() {
        if (null == instance) {
            SharedData.instance = new SharedData();
        }

        return SharedData.instance;
    }

}



package com.esprit.utils;

/**
 * Interface for controllers that accept data during navigation.
 */
public interface IDataProvider {

    /**
     * Sets data on the controller.
     *
     * @param data the data to set
     */
    void setData(Object data);
}

package com.esprit.utils;

/**
 * Callback interface for configuring controllers during navigation.
 */
@FunctionalInterface
public interface ControllerCallback {

    /**
     * Called to configure the loaded controller.
     *
     * @param controller the loaded controller instance
     */
    void configure(Object controller);
}

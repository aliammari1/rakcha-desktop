package com.esprit.services;

import java.util.List;

/**
 * IService class provides functionality for the RAKCHA desktop application.
 * <p>
 * This class is part of the JavaFX-based desktop application designed for
 * comprehensive cinema and product management system.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 * @param <T> the type of entity this service manages
 * @see <a href="https://github.com/your-repo/rakcha-desktop">RAKCHA Desktop</a>
 */
public interface IService<T> {
    /**
     * Creates a new entity in the database.
     * 
     * @param t the entity to create
     */
    void create(T t);

    /**
     * Retrieves all entities of type T from the database.
     * 
     * @return a list of all entities
     */
    List<T> read();

    /**
     * Updates an existing entity in the database.
     * 
     * @param t the entity to update
     */
    void update(T t);

    /**
     * Deletes an entity from the database.
     * 
     * @param t the entity to delete
     */
    void delete(T t);
}

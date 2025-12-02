package com.esprit.services;

import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;

import java.util.List;

/**
 * IService class provides functionality for the RAKCHA desktop application.
 * <p>
 * This class is part of the JavaFX-based desktop application designed for
 * comprehensive cinema and product management system.
 * </p>
 *
 * @param <T> the type of entity this service manages
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @see <a href="https://github.com/your-repo/rakcha-desktop">RAKCHA Desktop</a>
 * @since 1.0.0
 */
public interface IService<T> {

    /**
     * Creates a new entity in the database.
     *
     * @param t the entity to create
     */
    void create(T t);

    /**
     * Retrieves entities with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of entities
     */
    Page<T> read(PageRequest pageRequest);

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

    /**
     * Counts the total number of entities.
     *
     * @return the total count of entities
     */
    int count();

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier of the entity
     * @return the entity if found, otherwise null
     */
    T getById(Long id);

    /**
     * Finds all entities.
     *
     * @return a list of all entities
     */
    List<T> getAll();

    /**
     * Searches for entities based on a query string.
     *
     * @param query the search query
     * @return a list of entities matching the search criteria
     */
    List<T> search(String query);

    /**
     * Checks if an entity exists by its unique identifier.
     *
     * @param id the unique identifier of the entity
     * @return true if the entity exists, otherwise false
     */
    boolean exists(Long id);
}


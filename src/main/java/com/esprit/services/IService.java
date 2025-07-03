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
 * @see <a href="https://github.com/your-repo/rakcha-desktop">RAKCHA Desktop</a>
 */
public interface IService<T> {
    void create(T t);

    List<T> read();

    void update(T t);

    void delete(T t);
}

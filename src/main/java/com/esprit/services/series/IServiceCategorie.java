package com.esprit.services.series;


import com.esprit.models.series.Categorie;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IServiceCategorie<T> {
    void ajouter(T t) throws SQLException;

    void modifier(T t) throws SQLException;

    void supprimer(int id) throws SQLException;

    List<T> recuperer() throws SQLException;

    Map<Categorie, Long> getCategoriesStatistics();
}







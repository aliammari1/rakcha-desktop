package com.esprit.services.series;



import com.esprit.models.series.Categorie;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IServiceCategorie<T> {
   public void ajouter(T t) throws SQLException;

   public void modifier(T t) throws SQLException;

   public void supprimer(int id) throws SQLException;

   public List<T> recuperer() throws SQLException;

   public Map<Categorie, Long> getCategoriesStatistics();
}







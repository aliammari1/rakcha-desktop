package com.example.rakcha1.service.series;



import com.example.rakcha1.modeles.series.Categorie;

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







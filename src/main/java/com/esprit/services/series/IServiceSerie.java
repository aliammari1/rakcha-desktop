package com.esprit.services.series;


import com.esprit.services.series.DTO.SerieDto;

import java.sql.SQLException;
import java.util.List;


public interface IServiceSerie<T> {
   public void ajouter(T t) throws SQLException;
   public void modifier(T t) throws SQLException;
   public void supprimer(int id) throws SQLException;
   public List<SerieDto> recuperer() throws SQLException;
   public List<T> recuperers() throws SQLException;
   public List<T> recuperes(int id) throws  SQLException;

}
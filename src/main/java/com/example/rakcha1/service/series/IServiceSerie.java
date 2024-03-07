package com.example.rakcha1.service.series;


import com.example.rakcha1.service.series.DTO.SerieDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;  // Import Map


public interface IServiceSerie<T> {
   public void ajouter(T t) throws SQLException;
   public void modifier(T t) throws SQLException;
   public void supprimer(int id) throws SQLException;
   public List<SerieDto> recuperer() throws SQLException;
   public List<T> recuperers() throws SQLException;
   public List<T> recuperes(int id) throws  SQLException;

}

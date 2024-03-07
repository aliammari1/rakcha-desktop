package com.esprit.services.series;

import com.esprit.models.series.Episode;
import com.esprit.services.series.DTO.EpisodeDto;

import java.sql.SQLException;
import java.util.List;

public interface IServiceEpisode<T> {
   public void ajouter(T t) throws SQLException;
   public void modifier(T t) throws SQLException;
   public void supprimer(int id) throws SQLException;
   public List<EpisodeDto> recuperer() throws SQLException;
   public List<Episode> recupuerselonSerie(int id) throws SQLException;



}

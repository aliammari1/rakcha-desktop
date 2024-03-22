package com.esprit.services.series;

import com.esprit.models.series.Episode;
import com.esprit.services.series.DTO.EpisodeDto;

import java.sql.SQLException;
import java.util.List;

public interface IServiceEpisode<T> {
    void ajouter(T t) throws SQLException;

    void modifier(T t) throws SQLException;

    void supprimer(int id) throws SQLException;

    List<EpisodeDto> recuperer() throws SQLException;

    List<Episode> recupuerselonSerie(int id) throws SQLException;


}

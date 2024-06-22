package com.esprit.services.series;
import com.esprit.services.series.DTO.SerieDto;
import java.sql.SQLException;
import java.util.List;
public interface IServiceSerie<T> {
    void ajouter(T t) throws SQLException;
    void modifier(T t) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<SerieDto> recuperer() throws SQLException;
    List<T> recuperers() throws SQLException;
    List<T> recuperes(int id) throws SQLException;
}

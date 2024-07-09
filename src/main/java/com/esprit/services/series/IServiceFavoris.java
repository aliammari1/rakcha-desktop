package com.esprit.services.series;
import com.esprit.models.series.Favoris;
import java.sql.SQLException;
import java.util.List;
public interface IServiceFavoris<F> {
    void ajouter(F a);
    void modifier(F a);
    void supprimer(int id) throws SQLException;
    List<Favoris> Show();
}

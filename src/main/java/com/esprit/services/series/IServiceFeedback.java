package com.esprit.services.series;

import com.esprit.models.series.Feedback;

import java.sql.SQLException;
import java.util.List;

public interface IServiceFeedback<F> {
    void ajouter(F a);

    void modifier(F a);

    void supprimer(int id) throws SQLException;

    List<Feedback> Show();
}

package com.esprit.services.episode;

import com.esprit.models.episode;

import java.sql.SQLException;
import java.util.List;

public interface Iepisode<T> {
    public void create(episode episode);
    public void update(episode episode);
    public void delete(episode episode);
    public List<episode> read();
}

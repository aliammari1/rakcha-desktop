package com.esprit.services.serie;

import com.esprit.models.serie;

import java.sql.SQLException;
import java.util.List;

public interface Iserie <T>{
    public void create(serie serie);
    public void update(serie serie);
    public void delete(serie serie);
    public List<serie> read();
}

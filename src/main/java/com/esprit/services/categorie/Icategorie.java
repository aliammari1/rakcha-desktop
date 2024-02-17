package com.esprit.services.categorie;


import com.esprit.models.categorie;

import java.sql.SQLException;
import java.util.List;

public interface Icategorie<T> {
    public void create(categorie categorie);
    public void update(categorie categorie);
    public void delete(categorie categorie);
    public List<categorie> read();





}

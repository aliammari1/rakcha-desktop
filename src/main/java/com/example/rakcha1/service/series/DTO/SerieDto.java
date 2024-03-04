package com.example.rakcha1.service.series.DTO;

import com.example.rakcha1.modeles.series.Serie;

public class SerieDto extends Serie {
    private String nomCategories;

    public SerieDto() {
    }



    public String getNomCategories() {
        return nomCategories;
    }

    public void setNomCategories(String nomCategories1) {
        nomCategories = nomCategories1;
    }
}

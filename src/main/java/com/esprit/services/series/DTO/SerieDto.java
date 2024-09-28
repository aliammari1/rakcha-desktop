package com.esprit.services.series.DTO;

import com.esprit.models.series.Serie;

public class SerieDto extends Serie {
    private String nomCategories;

    public SerieDto() {
    }

    /**
     * @return String
     */
    public String getNomCategories() {
        return this.nomCategories;
    }

    /**
     * @param nomCategories1
     */
    public void setNomCategories(final String nomCategories1) {
        this.nomCategories = nomCategories1;
    }
}

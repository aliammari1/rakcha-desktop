package com.esprit.services.series.DTO;

import com.esprit.models.series.Episode;

public class EpisodeDto extends Episode {
    private String nomSerie;

    public EpisodeDto() {
    }

    /**
     * @return String
     */
    @Override
    public String getNomSerie() {
        return this.nomSerie;
    }

    /**
     * @param nomSerie
     */
    public void setNomSerie(final String nomSerie) {
        this.nomSerie = nomSerie;
    }
}

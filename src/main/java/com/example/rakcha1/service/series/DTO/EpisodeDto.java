package com.example.rakcha1.service.series.DTO;

import com.example.rakcha1.modeles.series.Episode;

public class EpisodeDto extends Episode {
    private String nomSerie;




    public EpisodeDto() {
    }
    public String getNomSerie() {
        return nomSerie;
    }

    public void setNomSerie(String nomSerie) {
        this.nomSerie = nomSerie;
    }
   
}

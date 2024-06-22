package com.esprit.services.series.DTO;
import com.esprit.models.series.Episode;
public class EpisodeDto extends Episode {
    private String nomSerie;
    public EpisodeDto() {
    }
    /** 
     * @return String
     */
    public String getNomSerie() {
        return nomSerie;
    }
    /** 
     * @param nomSerie
     */
    public void setNomSerie(String nomSerie) {
        this.nomSerie = nomSerie;
    }
}

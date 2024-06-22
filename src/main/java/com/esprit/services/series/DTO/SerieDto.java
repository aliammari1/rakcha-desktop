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
        return nomCategories;
    }
    /** 
     * @param nomCategories1
     */
    public void setNomCategories(String nomCategories1) {
        nomCategories = nomCategories1;
    }
}

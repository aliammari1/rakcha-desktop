package com.esprit.models.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Season {

    private Long id;
    private Long seriesId;
    private int seasonNumber;
    private String title;

    /**
     * Get the season description (alias for compatibility).
     * For seasons, the title serves as description.
     *
     * @return the season title/description
     */
    public String getDescription() {
        return this.title;
    }

    /**
     * Set the season description (alias for compatibility).
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.title = description;
    }

}



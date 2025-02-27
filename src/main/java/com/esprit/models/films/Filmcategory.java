package com.esprit.models.films;

/**
 * Represents a film category.
 */
public class Filmcategory {
    private Category categoryId;
    private Film filmId;

    /**
     * Constructs a new Filmcategory object with the specified category and film.
     *
     * @param categoryId The category of the film.
     * @param filmId     The film.
     */
    public Filmcategory(final Category categoryId, final Film filmId) {
        this.categoryId = categoryId;
        this.filmId = filmId;
    }

    /**
     * Gets the category of the film.
     *
     * @return The category of the film.
     */
    public Category getCategoryId() {
        return this.categoryId;
    }

    /**
     * Sets the category of the film.
     *
     * @param categoryId The category of the film.
     */
    public void setCategoryId(final Category categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the film.
     *
     * @return The film.
     */
    public Film getFilmId() {
        return this.filmId;
    }

    /**
     * Sets the film.
     *
     * @param filmId The film.
     */
    public void setFilmId(final Film filmId) {
        this.filmId = filmId;
    }

    /**
     * Returns a string representation of the Filmcategory object.
     *
     * @return A string representation of the Filmcategory object.
     */
    @Override
    public String toString() {
        return "Filmcategory{"
                + "categoryId=" + this.categoryId
                + ", filmId=" + this.filmId
                + '}';
    }
}

package com.esprit.models.films;

public class Filmcategory {
    private Category categoryId;
    private Film filmId;

    public Filmcategory(Category categoryId, Film filmId) {
        this.categoryId = categoryId;
        this.filmId = filmId;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    public Film getFilmId() {
        return filmId;
    }

    public void setFilmId(Film filmId) {
        this.filmId = filmId;
    }

    @Override
    public String toString() {
        return "Filmcategory{" +
                "categoryId=" + categoryId +
                ", filmId=" + filmId +
                '}';
    }
}


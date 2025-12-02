package com.esprit.enums;

public enum CinemaStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REFUSED("Refused");

    private final String status;

    CinemaStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}

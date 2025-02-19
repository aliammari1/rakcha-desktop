package com.esprit.models.cinemas;

public class Seat {
    private int id;
    private int seatNumber;
    private int rowNumber;
    private boolean isOccupied;
    private int salleId;

    public Seat(int id, int seatNumber, int rowNumber, boolean isOccupied, int salleId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.isOccupied = isOccupied;
        this.salleId = salleId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    public int getSalleId() { return salleId; }
    public void setSalleId(int salleId) { this.salleId = salleId; }
}

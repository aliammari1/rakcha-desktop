package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Seat;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatService {
    private Connection connection;

    public SeatService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    public List<Seat> getSeatsBySalleId(int salleId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE salle_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, salleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                seats.add(new Seat(
                    rs.getInt("id"),
                    rs.getInt("seat_number"),
                    rs.getInt("row_number"),
                    rs.getBoolean("is_occupied"),
                    rs.getInt("salle_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public boolean updateSeatStatus(int seatId, boolean isOccupied) {
        String query = "UPDATE seats SET is_occupied = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, isOccupied);
            stmt.setInt(2, seatId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

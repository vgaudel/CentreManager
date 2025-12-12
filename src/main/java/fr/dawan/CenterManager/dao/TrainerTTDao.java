package fr.dawan.CenterManager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TrainerTTDao {

    public void addRemoteDayColumnIfNotExists() throws SQLException {
        String sql = "ALTER TABLE trainer ADD COLUMN remoteDay TEXT";
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            // Ignore l'erreur si la colonne existe déjà
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
    }


    public void addDay(int trainerId, String day) throws SQLException {
        String sql = "INSERT INTO trainerTT(trainer_id, day) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            pstmt.setString(2, day);
            pstmt.executeUpdate();
        }
    }

    public List<String> getDays(int trainerId) throws SQLException {
        List<String> days = new ArrayList<>();
        String sql = "SELECT day FROM trainerTT WHERE trainer_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                days.add(rs.getString("day"));
            }
        }
        return days;
    }

    public void removeDay(int trainerId, String day) throws SQLException {
        String sql = "DELETE FROM trainerTT WHERE trainer_id = ? AND day = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            pstmt.setString(2, day);
            pstmt.executeUpdate();
        }
    }
}

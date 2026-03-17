package fr.dawan.CenterManager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.dawan.CenterManager.model.RemoteDay;

public class TrainerRdDao {

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS trainerRD (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trainer_id INTEGER NOT NULL,
                day TEXT NOT NULL,
                FOREIGN KEY(trainer_id) REFERENCES trainer(id)
            )
        """;
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void addDay(int trainerId, String day) throws SQLException {
        String sql = "INSERT INTO trainerRD(trainer_id, day) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            pstmt.setString(2, day);
            pstmt.executeUpdate();
        }
    }

    public List<String> getDays(int trainerId) throws SQLException {
        List<String> days = new ArrayList<>();
        String sql = "SELECT day FROM trainerRD WHERE trainer_id = ?";
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
        String sql = "DELETE FROM trainerRD WHERE trainer_id = ? AND day = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            pstmt.setString(2, day);
            pstmt.executeUpdate();
        }
    }

    public void removeAllDays(int trainerID) throws SQLException {
        String sql = "DELETE FROM trainerRD WHERE trainer_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerID);
            pstmt.executeUpdate();
        }
    }

    public List<RemoteDay> getRemoteDays(int trainerId) throws SQLException {
        List<RemoteDay> days = new ArrayList<>();
        String sql = "SELECT id, day FROM trainerTT WHERE trainer_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                days.add(new RemoteDay(
                    rs.getInt("id"),
                    trainerId,
                    rs.getString("day")
                ));
            }
        }
        return days;
    }
}

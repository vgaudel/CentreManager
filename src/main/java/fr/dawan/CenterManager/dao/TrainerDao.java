package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.Trainer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDao {

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS trainer (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                firstname TEXT NOT NULL,
                lastname TEXT NOT NULL
            )
        """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insert(Trainer t) throws SQLException {
        String sql = "INSERT INTO trainer(firstname, lastname) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getFirstname());
            pstmt.setString(2, t.getLastname());
            pstmt.executeUpdate();
        }
    }

    public List<Trainer> getAll() throws SQLException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT id, firstname, lastname, remoteDay FROM trainer";
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Trainer t = new Trainer(
                    rs.getInt("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    new ArrayList<>()
                );
                list.add(t);
            }
        }
        return list;
    }
}

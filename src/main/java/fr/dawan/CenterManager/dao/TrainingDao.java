package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.Training;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingDao implements GenericDao<Training> {

    public void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS training (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT
                )
        """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
        }
    }

    @Override
    public List<Training> getAll() throws SQLException {
        List<Training> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM training";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet res = stmt.executeQuery(sql)) {
                while (res.next()) {
                    Training t = new Training(
                        res.getInt("id"),
                        res.getString("name"),
                        res.getString("description")
                    );
                    list.add(t);
                }
             }
        return list;
    }

    @Override
    public Training insert(Training entity) throws SQLException {
        String sql = "INSERT INTO training(name, description) VALUES(?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.executeUpdate();
        }
        return entity;
    }

    @Override
    public void update(Training entity) throws SQLException {
        String sql = "UPDATE training SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getDescription());
                pstmt.setInt(3, entity.getId());
                pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Training entity) throws SQLException {
        String sql = "DELETE FROM training WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Training createFromName(String name) throws SQLException {
        return new Training(name);
    }
}

package fr.dawan.CenterManager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.Fog;

public class FogDao implements GenericDao<Fog> {

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS fog (
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

    // public void insert(Fog f) throws SQLException {
    //     String sql = "INSERT INTO fog(name, description) VALUES(?, ?)";
    //     try (Connection conn = Database.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //         pstmt.setString(1, f.getName());
    //         pstmt.setString(2, f.getDescription());
    //         pstmt.executeUpdate();
    //     }
    // }

    @Override
    public List<Fog> getAll() throws SQLException {
        List<Fog> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM fog";
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(sql)) {
            while (res.next()) {
                Fog f = new Fog(
                    res.getInt("id"),
                    res.getString("name"),
                    res.getString("description")
                );
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public Fog insert(Fog f) throws SQLException {
        String sql = "INSERT INTO fog(name, description) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, f.getName());
            pstmt.setString(2, f.getDescription());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    f.setId(keys.getInt(1));
                }
            }
        }
        return f;
    }

    @Override
    public void update(Fog entity) throws SQLException {
        String sql = "UPDATE fog SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getDescription());
                pstmt.setInt(3, entity.getId());
                pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Fog entity) throws SQLException {
        String sql = "DELETE FROM fog WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Fog createFromName(String name) throws SQLException {
        Fog f = new Fog(name);
        return insert(f);
    }
}

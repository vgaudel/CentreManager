package fr.dawan.CenterManager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.dawan.CenterManager.model.Fog;

public class FogDao implements GenericDao<Fog> {
    private static final Logger logger = Logger.getLogger(FogDao.class.getName());

    public void createTable() throws SQLException {
        String sql = """
                    CREATE TABLE IF NOT EXISTS fog (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT,
                        training_id INTEGER,
                        FOREIGN KEY (training_id) REFERENCES training(id) ON DELETE SET NULL
                    )
                """;
        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public List<Fog> getAll() throws SQLException {
        List<Fog> list = new ArrayList<>();
        String sql = "SELECT id, name, description, training_id FROM fog";
        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(sql)) {
            while (res.next()) {
                Fog f = new Fog(
                        res.getInt("id"),
                        res.getString("name"),
                        res.getString("description"));
                // Récupérer la clé étrangère
                int trainingId = res.getInt("training_id");
                if (!res.wasNull()) {
                    f.setTrainingId(trainingId);
                }
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public Fog insert(Fog entity) throws SQLException {
        String sql = "INSERT INTO fog(name, description, training_id) VALUES(?, ?, ?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());

            // Gérer le training_id (peut être null)
            if (entity.getTrainingId() != null) {
                pstmt.setInt(3, entity.getTrainingId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getInt(1));
                }
            }
        }
        return entity;
    }

    @Override
    public void update(Fog entity) throws SQLException {
        String sql = "UPDATE fog SET name = ?, description = ?, training_id = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());

            // Gérer le training_id (peut être null)
            if (entity.getTrainingId() != null) {
                pstmt.setInt(3, entity.getTrainingId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ SQL Error during update:", e);
            e.printStackTrace();
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
        return new Fog(name);
    }
}

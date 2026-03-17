package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.RemoteDay;
import fr.dawan.CenterManager.model.Trainer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDao implements GenericDao<Trainer> {

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS trainer (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                firstname TEXT NOT NULL,
                lastname TEXT
            )
        """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public List<Trainer> getAll() throws SQLException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT id, firstname, lastname, remoteDay FROM trainer";
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Trainer(
                    rs.getInt("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    new ArrayList<>()
                ));
            }
        }
        TrainerRdDao rdDao = new TrainerRdDao();
        for (Trainer t : list) {
            t.setRemoteDays(rdDao.getRemoteDays(t.getId()));
        }
        return list;
    }

    @Override
    public Trainer insert(Trainer entity) throws SQLException {
        String sql = "INSERT INTO trainer(firstname, lastname) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getFirstName());
            pstmt.setString(2, entity.getLastName());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                entity.setId(keys.getInt(1)); // ← récupère le vrai ID
            }
        }
        TrainerRdDao ttDao = new TrainerRdDao();
        for (RemoteDay rd : entity.getRemoteDays()) {
            ttDao.addDay(entity.getId(), rd.getDay());
        }
        return entity;
    }

    @Override
    public void update(Trainer entity) throws SQLException {
        String sql = "UPDATE trainer SET firstname = ?, lastname = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, entity.getFirstName());
                pstmt.setString(2, entity.getLastName());
                pstmt.setInt(3, entity.getId());
                pstmt.executeUpdate();
        }
        TrainerRdDao ttDao = new TrainerRdDao();
        ttDao.removeAllDays(entity.getId());
        for (RemoteDay rd : entity.getRemoteDays()) {
            ttDao.addDay(entity.getId(), rd.getDay());
        }
    }

    @Override
    public void delete(Trainer entity) throws SQLException {
        String sql = "DELETE FROM trainer WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getId());
            pstmt.executeUpdate();
        }
        TrainerRdDao ttDao = new TrainerRdDao();
        ttDao.removeAllDays(entity.getId());
    }

    @Override
    public Trainer createFromName(String name) throws SQLException {
        return new Trainer(name);
    }
}

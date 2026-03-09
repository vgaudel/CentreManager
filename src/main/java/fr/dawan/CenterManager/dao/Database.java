package fr.dawan.CenterManager.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection conn;
    private static final String APP_DIR = System.getenv("APPDATA") + "/CenterManager";
    private static final String DB_NAME = "center.db";
    private static final String URL = "jdbc:sqlite:" + APP_DIR + "/" + DB_NAME;

    /**
     * Retourne la connexion SQLite. Crée le dossier 'data' si nécessaire.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // créer le dossier si inexistant
            File dbDir = new File(APP_DIR);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            // créer la connexion si nécessaire
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL); // SQLite créera center.db si absent
                conn.setAutoCommit(true);
            }
            return conn;

        } catch (Exception e) {
            throw new SQLException("Erreur lors de la connexion à la base : " + e.getMessage(), e);
        }
    }

    private Database() {
    }

    /**
     * Initialise toutes les tables de la base.
     */
    public static void initDatabase() throws SQLException {
        new FogDao().createTable();
        new TrainerDao().createTable();
        new TrainingDao().createTable();
        new TrainerTTDao().addRemoteDayColumnIfNotExists();
    }
}

package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.Fog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe FogDao
 * Teste les opérations CRUD sur la table fog
 * 
 * ATTENTION: Ces tests nécessitent une base de données SQLite configurée.
 * Pour un environnement de test isolé, utiliser des mocks.
 */
@DisplayName("Tests pour la classe FogDao")
class FogDaoTest {

    private FogDao fogDao;

    @BeforeEach
    void setUp() {
        // Initialisation du DAO
        fogDao = new FogDao();
    }

    @Test
    @DisplayName("Créer un Fog depuis un nom")
    void testCreateFromName() throws SQLException {
        // Arrange
        String name = "Java Avancé";

        // Act
        Fog fog = fogDao.createFromName(name);

        // Assert
        assertNotNull(fog);
        assertEquals(name, fog.getName());
    }

    @Test
    @DisplayName("Tester createFromName avec une chaîne valide")
    void testCreateFromNameValid() throws Exception {
        // Arrange
        String validName = "Test Formation";

        // Act
        Fog fog = fogDao.createFromName(validName);

        // Assert
        assertNotNull(fog);
        assertEquals(validName, fog.getName());
    }

    @Test
    @DisplayName("Tester l'interface GenericDao pour Fog")
    void testGenericDaoInterface() {
        // Assert que FogDao implémente GenericDao
        assertTrue(fogDao instanceof GenericDao);
    }

    // Note: Les tests réels de persistance (insert, update, delete, getAll)
    // nécessitent une base de données. Voir le document TP pour les implémentations.
}

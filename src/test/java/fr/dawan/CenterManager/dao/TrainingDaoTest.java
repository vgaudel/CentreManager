package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe TrainingDao
 * Teste les opérations CRUD sur la table training
 * 
 * ATTENTION: Ces tests nécessitent une base de données SQLite configurée.
 * Pour un environnement de test isolé, utiliser des mocks.
 */
@DisplayName("Tests pour la classe TrainingDao")
class TrainingDaoTest {

    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        // Initialisation du DAO
        trainingDao = new TrainingDao();
    }

    @Test
    @DisplayName("Créer une Training depuis un nom")
    void testCreateFromName() throws SQLException {
        // Arrange
        String name = "Formation Java";

        // Act
        Training training = trainingDao.createFromName(name);

        // Assert
        assertNotNull(training);
        assertEquals(name, training.getName());
    }

    @Test
    @DisplayName("Tester createFromName avec un nom valide")
    void testCreateFromNameValid() throws Exception {
        // Arrange
        String validName = "Test Formation";

        // Act
        Training training = trainingDao.createFromName(validName);

        // Assert
        assertNotNull(training);
        assertEquals(validName, training.getName());
    }

    @Test
    @DisplayName("Créer une Training avec identifiant, nom et description")
    void testCreateTrainingComplet() {
        // Arrange
        int id = 1;
        String name = "Python Basics";
        String description = "Introduction à Python";

        // Act
        Training training = new Training(id, name, description);

        // Assert
        assertEquals(id, training.getId());
        assertEquals(name, training.getName());
        assertEquals(description, training.getDescription());
    }

    @Test
    @DisplayName("Vérifier l'interface GenericDao pour Training")
    void testTrainingImplementsGenericDao() {
        // Assert que TrainingDao implémente GenericDao
        assertTrue(trainingDao instanceof GenericDao);
    }

    @Test
    @DisplayName("Créer une Training sans description")
    void testTrainingWithoutDescription() {
        // Arrange & Act
        Training training = new Training("JavaScript");

        // Assert
        assertEquals("JavaScript", training.getName());
        assertNull(training.getDescription());
    }

    @Test
    @DisplayName("Modifier les informations d'une Training")
    void testUpdateTrainingInfo() {
        // Arrange
        Training training = new Training(1, "Old Name", "Old Description");

        // Act
        training.setName("New Name");
        training.setDescription("New Description");

        // Assert
        assertEquals("New Name", training.getName());
        assertEquals("New Description", training.getDescription());
    }

    // Note: Les tests réels de persistance (insert, update, delete, getAll)
    // nécessitent une base de données. Voir le document TP pour les implémentations.
}

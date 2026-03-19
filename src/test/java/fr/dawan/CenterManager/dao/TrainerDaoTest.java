package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe TrainerDao
 * Teste les opérations CRUD sur la table trainer
 * 
 * ATTENTION: Ces tests nécessitent une base de données SQLite configurée.
 * Pour un environnement de test isolé, utiliser des mocks.
 */
@DisplayName("Tests pour la classe TrainerDao")
class TrainerDaoTest {

    private TrainerDao trainerDao;

    @BeforeEach
    void setUp() {
        // Initialisation du DAO
        trainerDao = new TrainerDao();
    }

    @Test
    @DisplayName("Créer un Trainer depuis un nom")
    void testCreateFromName() throws SQLException {
        // Arrange
        String name = "Jean Dupont";

        // Act
        Trainer trainer = trainerDao.createFromName(name);

        // Assert
        assertNotNull(trainer);
        assertEquals(name, trainer.getFirstName());
    }

    @Test
    @DisplayName("Tester createFromName avec un nom valide")
    void testCreateFromNameValid() throws Exception {
        // Arrange
        String validName = "Test Trainer";

        // Act
        Trainer trainer = trainerDao.createFromName(validName);

        // Assert
        assertNotNull(trainer);
        assertEquals(validName, trainer.getFirstName());
    }

    @Test
    @DisplayName("Créer un Trainer avec prénom et nom")
    void testCreateTrainerComplet() throws SQLException {
        // Arrange
        String firstName = "Pierre";
        String lastName = "Martin";

        // Act
        Trainer trainer = new Trainer(0, firstName, lastName, new ArrayList<>());

        // Assert
        assertEquals(firstName, trainer.getFirstName());
        assertEquals(lastName, trainer.getLastName());
    }

    @Test
    @DisplayName("Vérifier l'interface GenericDao pour Trainer")
    void testTrainerImplementsGenericDao() {
        // Assert que TrainerDao implémente GenericDao
        assertTrue(trainerDao instanceof GenericDao);
    }

    // Note: Les tests réels de persistance (insert, update, delete, getAll)
    // nécessitent une base de données et des mocks pour les journées distantes.
    // Voir le document TP pour les implémentations complètes.

    @Test
    @DisplayName("Créer un Trainer valide avec données minimales")
    void testTrainerWithMinimalData() {
        // Arrange & Act
        Trainer trainer = new Trainer(1, "Alice", null, new ArrayList<>());

        // Assert
        assertEquals(1, trainer.getId());
        assertEquals("Alice", trainer.getFirstName());
        assertNull(trainer.getLastName());
    }
}

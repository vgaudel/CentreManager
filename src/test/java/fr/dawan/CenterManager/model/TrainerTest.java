package fr.dawan.CenterManager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Trainer
 * Représente un formateur avec ses informations et ses jours distants
 */
@DisplayName("Tests pour la classe Trainer")
class TrainerTest {

    private Trainer trainer;
    private List<RemoteDay> remoteDays;

    @BeforeEach
    void setUp() {
        // Initialisation des données
        remoteDays = new ArrayList<>();
        remoteDays.add(new RemoteDay(1, 1, "Lundi"));
        remoteDays.add(new RemoteDay(2, 1, "Mercredi"));

        trainer = new Trainer(1, "Jean", "Dupont", remoteDays);
    }

    @Test
    @DisplayName("Créer un Trainer avec nom simple")
    void testConstructorSimple() {
        // Arrange & Act
        Trainer newTrainer = new Trainer("Marie");

        // Assert
        assertEquals("Marie", newTrainer.getFirstName());
        assertNull(newTrainer.getLastName());
    }

    @Test
    @DisplayName("Créer un Trainer complet")
    void testConstructorComplet() {
        // Act & Assert
        assertEquals(1, trainer.getId());
        assertEquals("Jean", trainer.getFirstName());
        assertEquals("Dupont", trainer.getLastName());
        assertEquals(2, trainer.getRemoteDays().size());
    }

    @Test
    @DisplayName("Modifier le prénom du formateur")
    void testSetFirstName() {
        // Arrange
        String newFirstName = "Pierre";

        // Act
        trainer.setFirstName(newFirstName);

        // Assert
        assertEquals(newFirstName, trainer.getFirstName());
    }

    @Test
    @DisplayName("Modifier le nom du formateur")
    void testSetLastName() {
        // Arrange
        String newLastName = "Martin";

        // Act
        trainer.setLastName(newLastName);

        // Assert
        assertEquals(newLastName, trainer.getLastName());
    }

    @Test
    @DisplayName("Modifier l'identifiant du formateur")
    void testSetId() {
        // Arrange
        int newId = 42;

        // Act
        trainer.setId(newId);

        // Assert
        assertEquals(newId, trainer.getId());
    }

    @Test
    @DisplayName("Ajouter et consulter les jours distants")
    void testSetRemoteDays() {
        // Arrange
        List<RemoteDay> newRemoteDays = new ArrayList<>();
        newRemoteDays.add(new RemoteDay(3, 1, "Vendredi"));

        // Act
        trainer.setRemoteDays(newRemoteDays);

        // Assert
        assertEquals(1, trainer.getRemoteDays().size());
        assertEquals("Vendredi", trainer.getRemoteDays().get(0).getDay());
    }

    @Test
    @DisplayName("Récupérer le nom d'affichage complet")
    void testGetDisplayName() {
        // Act
        String displayName = trainer.getDisplayName();

        // Assert
        assertEquals("Jean Dupont", displayName);
    }

    @Test
    @DisplayName("Récupérer les champs d'affichage")
    void testGetDisplayFields() {
        // Act
        Map<String, Object> fields = trainer.getDisplayFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Nom"));
        assertTrue(fields.containsKey("Prénom"));
        assertTrue(fields.containsKey("Jours de télétravail"));
        assertEquals("Dupont", fields.get("Nom"));
        assertEquals("Jean", fields.get("Prénom"));
    }

    @Test
    @DisplayName("Récupérer les champs éditables")
    void testGetEditableFields() {
        // Act
        Map<String, EditableField> fields = trainer.getEditableFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Prénom"));
        assertTrue(fields.containsKey("Nom"));
        assertTrue(fields.containsKey("Jours de télétravail"));
    }

    @Test
    @DisplayName("Représentation texte d'un formateur")
    void testToString() {
        // Act
        String result = trainer.toString();

        // Assert
        assertTrue(result.contains("Jean"));
        assertTrue(result.contains("Dupont"));
        assertTrue(result.contains("TT:"));
    }

    @Test
    @DisplayName("Vérifier qu'un formateur sans jours distants est valide")
    void testTrainerSansJoursDistants() {
        // Arrange & Act
        Trainer newTrainer = new Trainer(5, "Alice", "Bernard", new ArrayList<>());

        // Assert
        assertEquals(0, newTrainer.getRemoteDays().size());
        assertEquals("Alice Bernard", newTrainer.getDisplayName());
    }

    @Test
    @DisplayName("Mettre à jour les informations du formateur")
    void testSetDisplayName() {
        // Arrange
        Map<String, String> fields = Map.of(
                "Prénom", "Marc",
                "Nom", "Lefevre"
        );

        // Act
        trainer.setDisplayName(fields);

        // Assert
        assertEquals("Marc", trainer.getFirstName());
        assertEquals("Lefevre", trainer.getLastName());
    }
}

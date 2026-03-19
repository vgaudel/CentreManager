package fr.dawan.CenterManager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Fog
 * Representative Object of Training (FOG)
 */
@DisplayName("Tests pour la classe Fog")
class FogTest {

    private Fog fog;

    @BeforeEach
    void setUp() {
        // Initialisation d'un objet Fog avant chaque test
        fog = new Fog("Java Avancé", "Formation complète en Java");
        fog.setId(1);
        fog.setTrainingId(10);
    }

    @Test
    @DisplayName("Créer un Fog avec constructeur simple")
    void testConstructorSimple() {
        // Arrange & Act
        Fog newFog = new Fog("JavaScript");

        // Assert
        assertEquals("JavaScript", newFog.getName());
        assertNull(newFog.getDescription());
    }

    @Test
    @DisplayName("Créer un Fog avec nom et description")
    void testConstructorNomDescription() {
        // Arrange & Act
        Fog newFog = new Fog("Python", "Programmation Python");

        // Assert
        assertEquals("Python", newFog.getName());
        assertEquals("Programmation Python", newFog.getDescription());
    }

    @Test
    @DisplayName("Créer un Fog avec identifiant, nom et description")
    void testConstructorComplet() {
        // Arrange & Act
        Fog newFog = new Fog(5, "C#", "Programmation C#");

        // Assert
        assertEquals(5, newFog.getId());
        assertEquals("C#", newFog.getName());
        assertEquals("Programmation C#", newFog.getDescription());
    }

    @Test
    @DisplayName("Modifier le nom d'un Fog")
    void testSetName() {
        // Arrange
        String newName = "Java Débutant";

        // Act
        fog.setName(newName);

        // Assert
        assertEquals(newName, fog.getName());
    }

    @Test
    @DisplayName("Modifier la description d'un Fog")
    void testSetDescription() {
        // Arrange
        String newDescription = "Formation pour débutants";

        // Act
        fog.setDescription(newDescription);

        // Assert
        assertEquals(newDescription, fog.getDescription());
    }

    @Test
    @DisplayName("Définir et récupérer l'identifiant de formation")
    void testTrainingId() {
        // Arrange
        Integer trainingId = 25;

        // Act
        fog.setTrainingId(trainingId);

        // Assert
        assertEquals(trainingId, fog.getTrainingId());
    }

    @Test
    @DisplayName("Gérer un ID de formation nulle")
    void testTrainingIdNull() {
        // Arrange & Act
        fog.setTrainingId(null);

        // Assert
        assertNull(fog.getTrainingId());
    }

    @Test
    @DisplayName("Récupérer le nom d'affichage")
    void testGetDisplayName() {
        // Act
        String displayName = fog.getDisplayName();

        // Assert
        assertEquals("Java Avancé", displayName);
        assertEquals(fog.getName(), displayName);
    }

    @Test
    @DisplayName("Récupérer les champs d'affichage")
    void testGetDisplayFields() {
        // Act
        Map<String, Object> fields = fog.getDisplayFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Nom"));
        assertTrue(fields.containsKey("Déscritpion")); // Note: typo du code original
        assertEquals("Java Avancé", fields.get("Nom"));
        assertEquals("Formation complète en Java", fields.get("Déscritpion"));
    }

    @Test
    @DisplayName("Récupérer les champs éditables")
    void testGetEditableFields() {
        // Act
        Map<String, EditableField> fields = fog.getEditableFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Titre")); // TRAINING_TITLE
        assertTrue(fields.containsKey("Déscritpion")); // DESCRIPTION
    }

    @Test
    @DisplayName("Vérifier que tous les getters retournent les bonnes valeurs")
    void testAllGetters() {
        // Assert
        assertEquals(1, fog.getId());
        assertEquals("Java Avancé", fog.getName());
        assertEquals("Formation complète en Java", fog.getDescription());
        assertEquals(10, fog.getTrainingId());
    }
}

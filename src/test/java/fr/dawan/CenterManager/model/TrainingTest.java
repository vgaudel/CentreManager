package fr.dawan.CenterManager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Training
 * Représente une formation avec ses FOGs requis
 */
@DisplayName("Tests pour la classe Training")
class TrainingTest {

    private Training training;
    private List<Fog> requiredFogs;

    @BeforeEach
    void setUp() {
        // Initialisation des données
        requiredFogs = new ArrayList<>();
        requiredFogs.add(new Fog(1, "Java Base", "Introduction à Java"));
        requiredFogs.add(new Fog(2, "OOP", "Programmation orientée objet"));

        training = new Training(1, "Formation Java", "Formation complète en Java");
        training.setRequiredFogs(requiredFogs);
    }

    @Test
    @DisplayName("Créer une Training avec nom simple")
    void testConstructorSimple() {
        // Arrange & Act
        Training newTraining = new Training("Python Basics");

        // Assert
        assertEquals("Python Basics", newTraining.getName());
        assertNull(newTraining.getDescription());
    }

    @Test
    @DisplayName("Créer une Training avec identifiant, nom et description")
    void testConstructorComplet() {
        // Act & Assert
        assertEquals(1, training.getId());
        assertEquals("Formation Java", training.getName());
        assertEquals("Formation complète en Java", training.getDescription());
    }

    @Test
    @DisplayName("Modifier le nom de la formation")
    void testSetName() {
        // Arrange
        String newName = "Formation JavaScript";

        // Act
        training.setName(newName);

        // Assert
        assertEquals(newName, training.getName());
    }

    @Test
    @DisplayName("Modifier la description de la formation")
    void testSetDescription() {
        // Arrange
        String newDescription = "Formation avancée en JavaScript";

        // Act
        training.setDescription(newDescription);

        // Assert
        assertEquals(newDescription, training.getDescription());
    }

    @Test
    @DisplayName("Modifier l'identifiant de la formation")
    void testSetId() {
        // Arrange
        int newId = 100;

        // Act
        training.setId(newId);

        // Assert
        assertEquals(newId, training.getId());
    }

    @Test
    @DisplayName("Définir et récupérer les FOGs requis")
    void testSetRequiredFogs() {
        // Arrange
        List<Fog> newFogs = new ArrayList<>();
        newFogs.add(new Fog(3, "Databases", "SQL et NoSQL"));

        // Act
        training.setRequiredFogs(newFogs);

        // Assert
        assertEquals(1, training.getRequiredFogs().size());
        assertEquals("Databases", training.getRequiredFogs().get(0).getName());
    }

    @Test
    @DisplayName("Récupérer le nom d'affichage de la formation")
    void testGetDisplayName() {
        // Act
        String displayName = training.getDisplayName();

        // Assert
        assertEquals("Formation Java", displayName);
    }

    @Test
    @DisplayName("Récupérer les champs d'affichage")
    void testGetDisplayFields() {
        // Act
        Map<String, Object> fields = training.getDisplayFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Nom"));
        assertTrue(fields.containsKey("Déscritpion")); // Note: typo du code original
        assertEquals("Formation Java", fields.get("Nom"));
        assertEquals("Formation complète en Java", fields.get("Déscritpion"));
    }

    @Test
    @DisplayName("Récupérer les champs éditables")
    void testGetEditableFields() {
        // Act
        Map<String, EditableField> fields = training.getEditableFields();

        // Assert
        assertNotNull(fields);
        assertTrue(fields.containsKey("Titre")); // TRAINING_TITLE
        assertTrue(fields.containsKey("Déscritpion")); // DESCRIPTION
    }

    @Test
    @DisplayName("Formation avec FOGs requis")
    void testTrainingAvecFogs() {
        // Act & Assert
        assertEquals(2, training.getRequiredFogs().size());
        assertTrue(training.getRequiredFogs().stream()
                .anyMatch(f -> f.getName().equals("Java Base")));
        assertTrue(training.getRequiredFogs().stream()
                .anyMatch(f -> f.getName().equals("OOP")));
    }

    @Test
    @DisplayName("Formation sans FOGs requis")
    void testTrainingSansFogs() {
        // Arrange & Act
        Training newTraining = new Training(2, "Formation Vide", "Pour tester");
        newTraining.setRequiredFogs(new ArrayList<>());

        // Assert
        assertEquals(0, newTraining.getRequiredFogs().size());
    }

    @Test
    @DisplayName("Mettre à jour le nom d'affichage")
    void testSetDisplayName() {
        // Arrange
        Map<String, String> fields = Map.of(
                "Name", "Nouvelle Formation"
        );

        // Act
        training.setDisplayName(fields);

        // Assert
        assertEquals("Nouvelle Formation", training.getName());
    }

    @Test
    @DisplayName("Mettre à jour les informations via updateFromFields")
    void testUpdateFromFields() {
        // Arrange
        Map<String, Object> fields = Map.of(
                "Titre", "Formation Avancée",
                "Déscritpion", "Une formation très avancée" // Note: typo du code original
        );

        // Act
        training.updateFromFields(fields);

        // Assert
        assertEquals("Formation Avancée", training.getName());
        assertEquals("Une formation très avancée", training.getDescription());
    }

    @Test
    @DisplayName("Vérifier que tous les getters retournent les bonnes valeurs")
    void testAllGetters() {
        // Assert
        assertEquals(1, training.getId());
        assertEquals("Formation Java", training.getName());
        assertEquals("Formation complète en Java", training.getDescription());
        assertEquals(2, training.getRequiredFogs().size());
    }
}

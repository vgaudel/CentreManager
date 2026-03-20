package fr.dawan.CenterManager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe RemoteDay
 * Représente un jour distant (télétravail) d'un formateur
 */
@DisplayName("Tests pour la classe RemoteDay")
class RemoteDayTest {

    private RemoteDay remoteDay;

    @BeforeEach
    void setUp() {
        remoteDay = new RemoteDay(1, 5, "Lund"); // Ajout d'une erreur ici
    }

    @Test
    @DisplayName("Créer un RemoteDay avec constructeur par défaut")
    void testConstructorDefault() {
        // Arrange & Act
        RemoteDay newDay = new RemoteDay();

        // Assert
        assertEquals(0, newDay.getId());
        assertEquals(0, newDay.getTrainerId());
        assertNull(newDay.getDay());
    }

    @Test
    @DisplayName("Créer un RemoteDay avec tous les paramètres")
    void testConstructorComplet() {
        // Act & Assert
        assertEquals(1, remoteDay.getId());
        assertEquals(5, remoteDay.getTrainerId());
        assertEquals("Lundi", remoteDay.getDay());
    }

    @Test
    @DisplayName("Modifier l'identifiant du jour distant")
    void testSetId() {
        // Arrange
        int newId = 10;

        // Act
        remoteDay.setId(newId);

        // Assert
        assertEquals(newId, remoteDay.getId());
    }

    @Test
    @DisplayName("Modifier l'identifiant du formateur")
    void testSetTrainerId() {
        // Arrange
        int newTrainerId = 8;

        // Act
        remoteDay.setTrainerId(newTrainerId);

        // Assert
        assertEquals(newTrainerId, remoteDay.getTrainerId());
    }

    @Test
    @DisplayName("Modifier le jour distant")
    void testSetDay() {
        // Arrange
        String newDay = "Mercredi";

        // Act
        remoteDay.setDay(newDay);

        // Assert
        assertEquals(newDay, remoteDay.getDay());
    }

    @Test
    @DisplayName("Représentation texte du jour distant")
    void testToString() {
        // Act
        String result = remoteDay.toString();

        // Assert
        assertEquals("Lundi", result);
    }

    @Test
    @DisplayName("Tester les différents jours de la semaine")
    void testDifferentsDays() {
        // Arrange
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

        // Act & Assert
        for (String day : daysOfWeek) {
            RemoteDay testDay = new RemoteDay(1, 1, day);
            assertEquals(day, testDay.getDay());
            assertEquals(day, testDay.toString());
        }
    }

    @Test
    @DisplayName("Vérifier que tous les getters retournent les bonnes valeurs")
    void testAllGetters() {
        // Assert
        assertEquals(1, remoteDay.getId());
        assertEquals(5, remoteDay.getTrainerId());
        assertEquals("Lundi", remoteDay.getDay());
    }
}

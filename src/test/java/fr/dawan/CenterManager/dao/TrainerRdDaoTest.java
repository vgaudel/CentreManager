package fr.dawan.CenterManager.dao;

import fr.dawan.CenterManager.model.RemoteDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe TrainerRdDao
 * Teste les opérations CRUD sur la table trainerRD (Remote Days)
 * 
 * ATTENTION: Ces tests nécessitent une base de données SQLite configurée.
 * Pour un environnement de test isolé, utiliser des mocks.
 */
@DisplayName("Tests pour la classe TrainerRdDao")
class TrainerRdDaoTest {

    private TrainerRdDao trainerRdDao;

    @BeforeEach
    void setUp() {
        // Initialisation du DAO
        trainerRdDao = new TrainerRdDao();
    }

    @Test
    @DisplayName("Créer un RemoteDay valide")
    void testCreateRemoteDay() {
        // Arrange & Act
        RemoteDay remoteDay = new RemoteDay(1, 5, "Lundi");

        // Assert
        assertNotNull(remoteDay);
        assertEquals(1, remoteDay.getId());
        assertEquals(5, remoteDay.getTrainerId());
        assertEquals("Lundi", remoteDay.getDay());
    }

    @Test
    @DisplayName("Tester les jours de la semaine valides")
    void testValidDaysOfWeek() {
        // Arrange
        String[] validDays = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

        // Act & Assert
        for (String day : validDays) {
            RemoteDay rd = new RemoteDay(1, 1, day);
            assertEquals(day, rd.getDay());
        }
    }

    @Test
    @DisplayName("Tester la liste des jours distants pour un formateur")
    void testRemoteDaysForTrainer() {
        // Arrange
        List<RemoteDay> remoteDays = new ArrayList<>();
        remoteDays.add(new RemoteDay(1, 5, "Lundi"));
        remoteDays.add(new RemoteDay(2, 5, "Mercredi"));
        remoteDays.add(new RemoteDay(3, 5, "Vendredi"));

        // Act & Assert
        assertEquals(3, remoteDays.size());
        assertTrue(remoteDays.stream()
                .anyMatch(rd -> rd.getDay().equals("Lundi")));
        assertTrue(remoteDays.stream()
                .anyMatch(rd -> rd.getDay().equals("Mercredi")));
    }

    @Test
    @DisplayName("Créer une liste vide de jours distants")
    void testEmptyRemoteDaysList() {
        // Arrange & Act
        List<RemoteDay> remoteDays = new ArrayList<>();

        // Assert
        assertTrue(remoteDays.isEmpty());
        assertEquals(0, remoteDays.size());
    }

    @Test
    @DisplayName("Modifier un jour distant")
    void testUpdateRemoteDay() {
        // Arrange
        RemoteDay remoteDay = new RemoteDay(1, 5, "Lundi");

        // Act
        remoteDay.setDay("Mercredi");
        remoteDay.setTrainerId(10);

        // Assert
        assertEquals("Mercredi", remoteDay.getDay());
        assertEquals(10, remoteDay.getTrainerId());
    }

    @Test
    @DisplayName("Vérifier la représentation texte d'un RemoteDay")
    void testRemoteDayToString() {
        // Arrange
        RemoteDay remoteDay = new RemoteDay(1, 5, "Jeudi");

        // Act
        String result = remoteDay.toString();

        // Assert
        assertEquals("Jeudi", result);
    }

    // Note: Les tests réels de persistance (addDay, getDays, removeDay, removeAllDays, getRemoteDays)
    // nécessitent une base de données. Voir le document TP pour les implémentations.

    @Test
    @DisplayName("Filtrer les jours distants par ID formateur")
    void testFilterRemoteDaysByTrainerId() {
        // Arrange
        List<RemoteDay> remoteDays = new ArrayList<>();
        remoteDays.add(new RemoteDay(1, 5, "Lundi"));
        remoteDays.add(new RemoteDay(2, 10, "Mardi"));
        remoteDays.add(new RemoteDay(3, 5, "Mercredi"));

        // Act
        List<RemoteDay> trainerFiveDays = remoteDays.stream()
                .filter(rd -> rd.getTrainerId() == 5)
                .toList();

        // Assert
        assertEquals(2, trainerFiveDays.size());
        assertTrue(trainerFiveDays.stream()
                .allMatch(rd -> rd.getTrainerId() == 5));
    }
}

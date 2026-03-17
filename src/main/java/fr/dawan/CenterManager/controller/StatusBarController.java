package fr.dawan.CenterManager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StatusBarController {

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private Label operationStatusLabel;

    // Méthode pour mettre à jour la connexion BDD
    public void setConnectionStatus(boolean connected) {
        connectionStatusLabel.setText(connected ? "Connecté" : "Déconnecté");
    }

    // // Méthode pour mettre à jour le status CRUD
    public void setOperationStatus(String message) {
        operationStatusLabel.setText(message);
    }
}
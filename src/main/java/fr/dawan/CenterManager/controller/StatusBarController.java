package fr.dawan.CenterManager.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class StatusBarController {

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private Label operationStatusLabel;

    // Méthode pour mettre à jour la connexion BDD
    // public void setConnectionStatus(boolean connected) {
    //     connectionStatusLabel.setText(connected ? "Connecté" : "Déconnecté");
    // }

    // // Méthode pour mettre à jour le status CRUD
    // public void setOperationStatus(String message) {
    //     operationStatusLabel.setText(message);
    // }
}
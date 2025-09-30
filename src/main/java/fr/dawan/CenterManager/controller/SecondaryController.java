package fr.dawan.CenterManager.controller;

import java.io.IOException;

import fr.dawan.CenterManager.app.CenterManagerApplication;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        CenterManagerApplication.setRoot("/view/primary");
    }
}
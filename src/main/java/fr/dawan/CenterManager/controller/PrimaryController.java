package fr.dawan.CenterManager.controller;

import java.io.IOException;

import fr.dawan.CenterManager.app.CenterManagerApplication;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        CenterManagerApplication.setRoot("/view/secondary");
    }
}

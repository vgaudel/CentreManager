package fr.dawan.CenterManager.controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private AnchorPane statusPane; // Pour y placer status bar et detail pane proprement

    @FXML
    private AnchorPane detailPane;

    @FXML
    private AnchorPane treePane;

    @FXML
    private AnchorPane planContent;

    private TreeController treeController;
    private MapViewController mapController;
    private StatusBarController statusBarController;
    private DetailPaneController detailPaneController;


    @FXML
    public void initialize() throws SQLException, IOException {
        // charger le TreeController
        treeController = loadFXML("/view/tree.fxml", treePane);
        mapController = loadFXML("/view/map.fxml", planContent);
        detailPaneController = loadFXML("/view/detail-pane.fxml", detailPane);
        statusBarController = loadFXML("/view/status-bar.fxml", statusPane);

        listenTree();
        // Exemple d’état initial
        // updateStatus("Application initialisée", true);
        // updateDbStatus(false);
    }

    private <T> T loadFXML(String path, AnchorPane container) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            T controller;
            container.getChildren().add(loader.load());
            controller = loader.getController();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void listenTree() {
        treeController.setOnItemSelected(item -> {
            if (item == null) {
                detailPaneController.hide();
                return;
            } else if (item.isCategory()) {
                return;
            }

            detailPaneController.showDetailsFrom(item.getData());
        });
    }

    // private <T> T loadFXML(String path, BorderPane container, String position) {
    //     try {
    //         FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
    //         Node node = loader.load();
    //         T controller = loader.getController();

    //         switch (position.toLowerCase()) {
    //             case "top" -> container.setTop(node);
    //             case "bottom" -> container.setBottom(node);
    //             case "left" -> container.setLeft(node);
    //             case "right" -> container.setRight(node);
    //             case "center" -> container.setCenter(node);
    //             default -> container.setCenter(node);
    //         }

    //         return controller;
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }




    // === Méthodes de communication globale ===

    // public void showDetailPane(boolean visible) {
    //     if (detailPaneController != null) {
    //         detailPaneController.getRoot().setVisible(visible);
    //     }
    // }

    // public void updateStatus(String message, boolean success) {
    //     if (statusBarController != null) {
    //         statusBarController.setStatusMessage(message);
    //         statusBarController.setSuccessStyle(success);
    //     }
    // }

    // public void updateDbStatus(boolean connected) {
    //     if (statusBarController != null) {
    //         statusBarController.setDbStatus(connected);
    //     }
    // }
}

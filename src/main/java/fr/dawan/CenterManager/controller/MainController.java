package fr.dawan.CenterManager.controller;

import java.io.IOException;
import java.sql.SQLException;

import fr.dawan.CenterManager.action.ExportAction;
import fr.dawan.CenterManager.handler.MenuActionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox root;

    @FXML
    private AnchorPane statusPane; // Pour y placer status bar et detail pane proprement

    @FXML
    private AnchorPane detailPane;

    @FXML
    private AnchorPane treePane;

    @FXML
    private AnchorPane planContent;

    private TreeController treeController;
    private MapViewController mapViewController;
    private MenuActionHandler menuActionHandler;
    private StatusBarController statusBarController;
    private DetailPaneController detailPaneController;


    @FXML
    public void initialize() throws SQLException, IOException {
        // charger le TreeController
        treeController = loadFXML("/view/tree.fxml", treePane);
        mapViewController = loadFXML("/view/map.fxml", planContent);
        detailPaneController = loadFXML("/view/detail-pane.fxml", detailPane);
        statusBarController = loadFXML("/view/status-bar.fxml", statusPane);

        menuActionHandler = new MenuActionHandler();
        menuActionHandler.registerAction("export", new ExportAction(mapViewController));

        detailPane.prefWidthProperty().bind(root.widthProperty().multiply(0.08));
        treePane.setMinWidth(220);
        listenTree();
        // Exemple d’état initial
        // updateStatus("Application initialisée", true);
        // updateDbStatus(false);
    }

    @FXML
    private void handleExport(ActionEvent event) {
        menuActionHandler.handleMenuAction("export");
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
}

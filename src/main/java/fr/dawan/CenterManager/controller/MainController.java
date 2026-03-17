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

    private MenuActionHandler menuActionHandler;

    @FXML
    public void initialize() throws SQLException, IOException {

        TreeController treeController = loadFXML("/view/tree.fxml", treePane);
        MapViewController mapViewController = loadFXML("/view/map.fxml", planContent);
        DetailPaneController detailPaneController = loadFXML("/view/detail-pane.fxml", detailPane);
        StatusBarController statusBarController = loadFXML("/view/status-bar.fxml", statusPane);


        menuActionHandler = new MenuActionHandler();

        menuActionHandler.registerAction("export", new ExportAction(mapViewController));
        // statusBarController.setOperationStatus("Bienvenue dans CenterManager");

        detailPane.prefWidthProperty().bind(root.widthProperty().multiply(0.08));
        detailPane.setMinWidth(250);
        treePane.setMinWidth(220);
        // statusBarController.setConnectionStatus(true);
        // updateStatus("Application initialisée", true);
        // updateDbStatus(false);
        listenTree(treeController, detailPaneController);
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

    private void listenTree(TreeController tree, DetailPaneController detail) {
        tree.setOnItemSelected(item -> {
            if (item == null) {
                return;
            } else if (item.isCategory()) {
                detail.hide();
                return;
            }

            detail.showDetailsFrom(item.getData());
        });
    }
}

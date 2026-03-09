package fr.dawan.CenterManager.controller;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import fr.dawan.CenterManager.ui.DragSVGHandler;
import fr.dawan.CenterManager.ui.MapPlacementManager;
import fr.dawan.CenterManager.ui.ZoomHandler;
import fr.dawan.CenterManager.analysis.PlanAnalyzer;
import fr.dawan.CenterManager.model.Zone;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

public class MapViewController {

    @FXML
    private AnchorPane planContent;

    @FXML
    private Pane planPane;

    @FXML
    private Pane planOverlay;

    @FXML
    private Button addPlanButton;

    private MapPlacementManager placementManager;
    private final PlanAnalyzer planAnalyzer = new PlanAnalyzer();
    public static final DataFormat DISPLAYABLE_FORMAT = new DataFormat("application/x-displayable");
    private static final Logger logger = Logger.getLogger(MapViewController.class.getName());

    @FXML
    public void initialize() {
        ZoomHandler zoomHandler = new ZoomHandler(planPane);
        DragSVGHandler dragSvgHandler = new DragSVGHandler(planPane);

        placementManager = null;

        planPane.setFocusTraversable(true);
        planOverlay.setMouseTransparent(true);
        planPane.setOnMouseClicked(e -> planPane.requestFocus());
        zoomHandler.initialize();
        dragSvgHandler.initialize();

        addPlanButton.setOnAction(e -> {
            try {
                loadSvgPlan(zoomHandler);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Erreur lors du chargement du plan : ", ex);

                // Affiche une alerte
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors du chargement du plan");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        planPane.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        planPane.setOnDragDropped(event -> {
            String name = event.getDragboard().getString();
            double x = event.getX();
            double y = event.getY();
            Zone targetZone = null;
            Bounds zoneBounds = null;
            double requiredWidth = 0;
            double requiredHeight = 0;

            for (Zone zone : placementManager.getZones()) {
                if (zone.contains(x, y)) {
                    targetZone = zone;
                    break;
                }
            }

            if (targetZone != null && name != null) {
                placementManager.placeInZone(name, targetZone);
                zoneBounds = targetZone.getNode().getBoundsInLocal();
                requiredWidth = zoneBounds.getMaxX();
                requiredHeight = zoneBounds.getMaxY();
                planPane.setPrefWidth(Math.max(planPane.getPrefWidth(), requiredWidth));
                planPane.setPrefHeight(Math.max(planPane.getPrefHeight(), requiredHeight));
            }

            event.setDropCompleted(targetZone != null);
            event.consume();
        });
    }

    private void loadSvgPlan(ZoomHandler zoomHandler) throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner un plan SVG");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SVG", "*.svg")
        );

        File file = chooser.showOpenDialog(addPlanButton.getScene().getWindow());
        if (file == null)
            return;

        // Nettoyage
        planPane.getChildren().clear();

        // Analyse du SVG
        List<Zone> zones = planAnalyzer.analyzeSvg(file).getZones();

        // Affichage
        for (Zone zone : zones) {
            Shape node = zone.getNode();
            if (node != null) {
                planPane.getChildren().add(node);
            }
        }

        // Centrage
        Platform.runLater(() -> zoomHandler.centerAndScale());

        // Placement manager
        placementManager = new MapPlacementManager(zones, planPane);
        addPlanButton.setVisible(false);
    }

    public StackPane getStackPane() {
        return (StackPane) planPane.getParent();
    }
}
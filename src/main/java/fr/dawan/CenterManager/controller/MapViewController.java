package fr.dawan.CenterManager.controller;

import java.io.File;
import java.util.List;

import fr.dawan.CenterManager.ui.DragHandler;
import fr.dawan.CenterManager.ui.MapPlacementManager;
import fr.dawan.CenterManager.ui.ZoomHandler;
import fr.dawan.CenterManager.analysis.PlanAnalyzer;
import fr.dawan.CenterManager.model.Zone;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

public class MapViewController {
    @FXML
    private Pane planPane;

    @FXML
    private Pane planOverlay;

    @FXML
    private Button addPlanButton;

    private MapPlacementManager placementManager;
    private final PlanAnalyzer planAnalyzer = new PlanAnalyzer();

    @FXML
    public void initialize() {
        ZoomHandler zoomHandler = new ZoomHandler(planPane);
        DragHandler dragHandler = new DragHandler(planPane);

        planPane.setFocusTraversable(true);
        planOverlay.setMouseTransparent(true);
        planPane.setOnMouseClicked(e -> planPane.requestFocus());
        // planPane.setOnScroll(e -> System.out.println("SCROLL DETECT"));
        zoomHandler.initialize();
        dragHandler.initialize();

        addPlanButton.setOnAction(e -> {
            try {
                loadSvgPlan();
                planPane.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1);");
                planPane.setStyle("-fx-background-color: rgba(0,255,0,0.15);");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        planPane.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        planPane.setOnDragDropped(event -> {
            String data = event.getDragboard().getString();
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

            if (targetZone != null) {
                placementManager.placeInZone(data, targetZone);
                zoneBounds = targetZone.getNode().getBoundsInLocal();
                requiredHeight = zoneBounds.getMaxY();
                requiredWidth = zoneBounds.getCenterX();
                planPane.setPrefWidth(Math.max(planPane.getPrefWidth(), requiredWidth));
                planPane.setPrefHeight(Math.max(planPane.getPrefHeight(), requiredHeight));
            }

            event.setDropCompleted(targetZone != null);
            event.consume();
        });
    }

    private void loadSvgPlan() throws Exception {
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
        planOverlay.getChildren().clear();

        // Analyse du SVG
        List<Zone> zones = planAnalyzer.analyzeSvg(file).getZones();

        // Nettoyage avant la boucle
        planPane.getChildren().clear();
        planOverlay.getChildren().clear();

        // Affichage
        for (Zone zone : zones) {
            Shape node = zone.getNode();
            if (node != null) {
                planPane.getChildren().add(node);
            }
        }

        planOverlay.toFront();

        Platform.runLater(() -> {
            Bounds bounds = planPane.getBoundsInLocal();

            double paneWidth = planPane.getParent().getLayoutBounds().getWidth();
            double paneHeight = planPane.getParent().getLayoutBounds().getHeight();

            planPane.setTranslateX((paneWidth - bounds.getWidth()) / 2 - bounds.getMinX());
            planPane.setTranslateY((paneHeight - bounds.getHeight()) / 2 - bounds.getMinY());
        });

        // Overlay au-dessus
        planOverlay.toFront();

        // Placement manager basé sur des zones, PAS des pixels
        placementManager = new MapPlacementManager(zones, planOverlay);

        addPlanButton.setVisible(false);
    }

}
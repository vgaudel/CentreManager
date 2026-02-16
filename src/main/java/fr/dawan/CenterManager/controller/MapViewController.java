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
import javafx.scene.shape.SVGPath;
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
        planPane.setOnScroll(e -> System.out.println("SCROLL DETECT"));
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

            for (Zone zone : placementManager.getZones()) {
                if (zone.contains(x, y)) {
                    targetZone = zone;
                    break;
                }
            }

            if (targetZone != null) {
                placementManager.placeInZone(data, targetZone);
            }

            event.setDropCompleted(targetZone != null);
            event.consume();
        });
    }

    private void loadSvgPlan() throws Exception {
        FileChooser chooser = new FileChooser();
        System.out.println(">>> Bouton cliqué");
        chooser.setTitle("Sélectionner un plan SVG");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SVG", "*.svg")
        );

        File file = chooser.showOpenDialog(addPlanButton.getScene().getWindow());
        if (file == null)
            return;

        System.out.println(">>> Fichier SVG sélectionné : " + file.getAbsolutePath());

        // Nettoyage
        planPane.getChildren().clear();
        planOverlay.getChildren().clear();

        // Analyse du SVG
        List<Zone> zones = planAnalyzer.analyzeSvg(file).getZones();

        // Nettoyage avant la boucle
        planPane.getChildren().clear();
        planOverlay.getChildren().clear();

        // Affichage
        int i = 0;
        for (Zone zone : zones) {
            SVGPath path = zone.getShape();

            // Couleur de remplissage aléatoire (ou à partir du SVG si tu veux)
            path.setFill(new Color(Math.random(), Math.random(), Math.random(), 0.5));

            // Bordure
            path.setStroke(Color.BLACK);
            path.setStrokeWidth(1);
            System.out.println(i);
            i++;
            // Ajouter au planPane
            planPane.getChildren().add(path);
        }

        planOverlay.toFront();

        Platform.runLater(() -> {
            Bounds bounds = planPane.getBoundsInLocal();
            System.out.println(">>> Bounds SVG : " + bounds);

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
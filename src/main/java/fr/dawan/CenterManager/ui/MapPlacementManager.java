package fr.dawan.CenterManager.ui;

import java.util.*;

import fr.dawan.CenterManager.dao.PlacementHistoryLogger;
import fr.dawan.CenterManager.model.Zone;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MapPlacementManager {

    private final List<Zone> zones;
    private final Pane overlay;

    private final PlacementHistoryLogger historyLogger = new PlacementHistoryLogger();

    private final Map<Zone, List<Node>> zoneNodes = new HashMap<>();
    private final Map<Node, Zone> nodeZone = new HashMap<>();

    private static final double PADDING = 5;
    private static final double ITEM_HEIGHT = 22;

    public MapPlacementManager(List<Zone> zones, Pane overlay) {
        this.zones = zones;
        this.overlay = overlay;

        // initialisation
        for (Zone z : zones) {
            zoneNodes.put(z, new ArrayList<>());
        }
    }

    // =========================
    // PUBLIC API
    // =========================

    public List<Zone> getZones() {
        return zones;
    }

    /**
     * Drop depuis TreeView
     */
    /**
     * Place un item (label) dans une zone donnée
     */
    public void placeInZone(String data, Zone targetZone) {
        if (targetZone == null || !targetZone.isPlaceable() || targetZone.getGroupId() == "Millieu") {
            System.out.println("[DROP] Zone invalide : " + targetZone);
            return;
        }

        System.out.println("[DROP] Placement : " + data + " dans " + targetZone.getGroupId());

        // Récupération des bounds de la zone
        Bounds sceneBounds = targetZone.getNode().localToScene(targetZone.getNode().getBoundsInLocal());
        Bounds overlayBounds = overlay.sceneToLocal(sceneBounds);

        // Taille de la zone
        double zoneWidth = overlayBounds.getWidth();
        double zoneHeight = overlayBounds.getHeight();

        // Labels = 10% de la zone (ou min 40px si trop petit)
        double labelWidth = Math.max(zoneWidth * 0.25, 40);
        double labelHeight = Math.max(zoneHeight * 0.15, 30);

        // Position de départ (coin haut-gauche de la zone + padding)
        double x = overlayBounds.getMinX() + 5;
        double y = overlayBounds.getMinY() + 5;

        // Compter les labels existants dans cette zone
        int count = 0;
        for (Node node : overlay.getChildren()) {
            if (node instanceof Label lbl && targetZone.getGroupId().equals(lbl.getUserData())) {
                count++;
            }
        }

        // Empiler en dessous
        y += count * (labelHeight + 5);

        // Création du label
        Label label = new Label(data);
        label.setPrefSize(labelWidth, labelHeight);
        label.setStyle("""
            -fx-background-color: #e0e0e0;
            -fx-border-color: #333;
            -fx-padding: 10;
            -fx-font-size: 20px;
            -fx-alignment: CENTER;
            -fx-text-alignment: CENTER;
            -fx-wrap-text: true;
        """);

        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setUserData(targetZone.getGroupId());

        overlay.getChildren().add(label);
        historyLogger.logPlacement(data, targetZone.getGroupId());
        System.out.println("[DROP] Label ajouté à " + x + ", " + y);
    }

    /**
     * Drop par coordonnées (si tu en as besoin)
     */
    public void place(Node node, double x, double y) {
        Zone zone = findZoneAt(x, y);
        if (zone == null || !zone.isPlaceable()) return;

        addNodeToZone(node, zone);
        enableDrag(node);
    }

    // =========================
    // CORE LOGIC
    // =========================

    private void addNodeToZone(Node node, Zone zone) {
        // enlever de l’ancienne zone si nécessaire
        Zone oldZone = nodeZone.get(node);
        if (oldZone != null) {
            zoneNodes.get(oldZone).remove(node);
        }

        zoneNodes.get(zone).add(node);
        nodeZone.put(node, zone);

        if (!overlay.getChildren().contains(node)) {
            overlay.getChildren().add(node);
        }

        layoutZone(zone);
    }

    private void layoutZone(Zone zone) {
        List<Node> nodes = zoneNodes.get(zone);
        Bounds bounds = zone.getNode().getBoundsInParent();

        double xStart = bounds.getMinX() + PADDING;
        double yStart = bounds.getMinY() + PADDING;

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setLayoutX(xStart);
            node.setLayoutY(yStart + i * (ITEM_HEIGHT + PADDING));
        }
    }

    private Zone findZoneAt(double x, double y) {
        for (Zone z : zones) {
            if (z.contains(x, y)) return z;
        }
        return null;
    }

    // =========================
    // VISUAL REPRESENTATION
    // =========================

    private Node createVisualNode(String labelText) {
        Label label = new Label(labelText);

        label.setStyle("""
            -fx-background-color: white;
            -fx-border-color: black;
            -fx-padding: 3 6 3 6;
            -fx-font-size: 20px;
        """);

        return label;
    }

    // =========================
    // DRAG INTERNE (salle → salle)
    // =========================

    private void enableDrag(Node node) {

        node.setOnMousePressed(e -> node.toFront());

        node.setOnMouseDragged(e -> {
            node.setLayoutX(e.getSceneX() - overlay.getLayoutX());
            node.setLayoutY(e.getSceneY() - overlay.getLayoutY());
        });

        node.setOnMouseReleased(e -> {
            Zone newZone = findZoneAt(node.getLayoutX(), node.getLayoutY());
            if (newZone != null && newZone.isPlaceable()) {
                addNodeToZone(node, newZone);
            } else {
                // revenir à sa zone d’origine
                Zone original = nodeZone.get(node);
                layoutZone(original);
            }
        });
    }
}

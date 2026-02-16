package fr.dawan.CenterManager.ui;

import java.util.*;
import fr.dawan.CenterManager.model.Zone;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MapPlacementManager {

    private final List<Zone> zones;
    private final Pane overlay;

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
    public void placeInZone(String data, Zone targetZone) {
        if (targetZone == null || !targetZone.isPlaceable()) return;

        Node node = createVisualNode(data);
        addNodeToZone(node, targetZone);
        enableDrag(node);
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
        Bounds bounds = zone.getShape().getBoundsInParent();

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
            -fx-font-size: 11px;
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

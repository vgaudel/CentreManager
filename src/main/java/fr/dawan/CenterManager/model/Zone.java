package fr.dawan.CenterManager.model;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

public class Zone {

    private final String id;
    private final Shape node;
    // private final SVGPath shape;
    // private final Polygon polygon;
    // private final Rectangle rectangle;
    private final String IDPREFIX = "zone-";
    private List<Point2D> contour;
    private double area;
    private String groupId;
    private boolean placeable;

    // Constructeur principal utilisé dans PlanAnalyzer
    public Zone(int index, Shape node, List<Point2D> contour, double area, boolean placeable) {
        this.id = IDPREFIX + index;
        this.node = node;
        this.contour = contour;
        this.area = area;
        this.placeable = placeable;
    }

    // Getters / Setters
    public String getId() {
        return id;
    }

    public Shape getNode() {
        return node;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public void setPlaceable(boolean placeable) {
        this.placeable = placeable;
    }

    public List<Point2D> getContour() {
        return contour;
    }

    public void setContour(List<Point2D> contour) {
        this.contour = contour;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    // Vérifie si un point est dans la zone (utilise directement le SVGPath)
    public boolean contains(double x, double y) {
        return node != null && node.contains(x, y);
    }
}

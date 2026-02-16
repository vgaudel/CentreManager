package fr.dawan.CenterManager.model;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.SVGPath;

public class Zone {

    private final String id;
    private final SVGPath shape;
    private boolean placeable;
    private List<Point2D> contour;
    private double area;

    // Constructeur principal utilisé dans PlanAnalyzer
    public Zone(int index, SVGPath shape, List<Point2D> contour, double area, boolean placeable) {
        this.id = "zone-" + index;       // transforme l'index en String
        this.shape = shape;
        this.contour = contour;
        this.area = area;
        this.placeable = placeable;
    }

    // Getters / Setters
    public String getId() {
        return id;
    }

    public SVGPath getShape() {
        return shape;
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

    // Vérifie si un point est dans la zone (utilise directement le SVGPath)
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }
}

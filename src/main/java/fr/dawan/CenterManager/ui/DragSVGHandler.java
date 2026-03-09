package fr.dawan.CenterManager.ui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public class DragSVGHandler {
    private final Pane planPane;

    private double lastMouseX;
    private double lastMouseY;

    public DragSVGHandler(Pane planPane) {
        this.planPane = planPane;
    }

    public void initialize() {
        isDrag();
    }

    private void isDrag() {
        // gestion du drag
        planPane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        planPane.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double deltaX = event.getSceneX() - lastMouseX;
                double deltaY = event.getSceneY() - lastMouseY;

                // déplace l’image
                planPane.setTranslateX(planPane.getTranslateX() + deltaX);
                planPane.setTranslateY(planPane.getTranslateY() + deltaY);

                // met à jour les positions de référence
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        planPane.setOnMouseReleased(event -> {
            if (event.isPrimaryButtonDown()) {
                // A SUPPRIMER
            }
        });
    }

    public void makeDraggable(SVGPath svg) {
        svg.setOnMousePressed(e -> {
            lastMouseX = e.getSceneX() - svg.getTranslateX();
            lastMouseY = e.getSceneY() - svg.getTranslateY();
        });

        svg.setOnMouseDragged(e -> {
            svg.setTranslateX(e.getSceneX() - lastMouseX);
            svg.setTranslateY(e.getSceneY() - lastMouseY);
        });
    }

}

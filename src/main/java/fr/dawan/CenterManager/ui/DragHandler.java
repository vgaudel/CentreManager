package fr.dawan.CenterManager.ui;

import javafx.scene.image.ImageView;

public class DragHandler {
    private ImageView planImage;

    private double lastMouseX;
    private double lastMouseY;

    public DragHandler(ImageView plImageView) {
        this.planImage = plImageView;
    }

    public void initialize() {
        isDrag();
        System.out.println("Is Drag");
    }

    private void isDrag() {
        // gestion du drag
        planImage.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        planImage.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double deltaX = event.getSceneX() - lastMouseX;
                double deltaY = event.getSceneY() - lastMouseY;

                // déplace l’image
                planImage.setTranslateX(planImage.getTranslateX() + deltaX);
                planImage.setTranslateY(planImage.getTranslateY() + deltaY);

                // met à jour les positions de référence
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        planImage.setOnMouseReleased(event -> {
            if (event.isPrimaryButtonDown()) {
                // rien de spécial à faire, mais tu peux reset si tu veux
            }
        });
    }
}

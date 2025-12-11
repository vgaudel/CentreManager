package fr.dawan.CenterManager.ui;

import javafx.scene.image.ImageView;

public class ZoomHandler {
    private ImageView planImage;

    public ZoomHandler(ImageView planImage) {
        this.planImage = planImage;
    }

    public void initialize() {
        isZoom();
        System.out.println("Is Zoom");
    }

    private void isZoom() {
        planImage.setOnScroll(event -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            } else if (event.getDeltaY() > 0) {
                zoomFactor = 1 * zoomFactor;
            }
            planImage.setScaleX(planImage.getScaleX() * zoomFactor);
            planImage.setScaleY(planImage.getScaleY() * zoomFactor);
        });
    }
}

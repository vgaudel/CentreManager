package fr.dawan.CenterManager.ui;

import javafx.scene.layout.Pane;

public class ZoomHandler {
    private Pane planPane;

    public ZoomHandler(Pane planPane) {
        this.planPane = planPane;
    }

    public void initialize() {
        isZoom();
    }

    private void isZoom() {
        planPane.setOnScroll(event -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            } else if (event.getDeltaY() > 0) {
                zoomFactor = 1 * zoomFactor;
            }
            planPane.setScaleX(planPane.getScaleX() * zoomFactor);
            planPane.setScaleY(planPane.getScaleY() * zoomFactor);
        });
    }
}

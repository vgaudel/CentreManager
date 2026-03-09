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

    public void centerAndScale() {
        double parentWidth = planPane.getParent().getLayoutBounds().getWidth();
        double parentHeight = planPane.getParent().getLayoutBounds().getHeight();
        double contentWidth = planPane.getBoundsInLocal().getWidth();
        double contentHeight = planPane.getBoundsInLocal().getHeight();

        double scaleX = parentWidth / contentWidth;
        double scaleY = parentHeight / contentHeight;
        double scale = Math.min(scaleX, scaleY);

        planPane.setScaleX(scale);
        planPane.setScaleY(scale);

        double offsetX = (parentWidth - contentWidth * scale) / 2;
        double offsetY = (parentHeight - contentHeight * scale) / 2;

        planPane.setTranslateX(Math.max(offsetX, 0));
        planPane.setTranslateY(Math.max(offsetY, 0));
    }
}

package fr.dawan.CenterManager.analysis;

import javafx.scene.paint.Color;

public class ColorMatcher {

    public boolean isSimilar(Color a, Color b, double tolerance) {
        double dr = a.getRed() - b.getRed();
        double dg = a.getGreen() - b.getGreen();
        double db = a.getBlue() - b.getBlue();

        return Math.sqrt(dr * dr + dg * dg + db * db) < tolerance;
    }
}

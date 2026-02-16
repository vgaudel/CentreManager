package fr.dawan.CenterManager.analysis;

import fr.dawan.CenterManager.model.PlanAnalysisResult;
import fr.dawan.CenterManager.model.Zone;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanAnalyzer {

    private static class SvgCursor {
        double x;
        double y;
    }

    private static final Pattern TOKEN_PATTERN =
        Pattern.compile("[MLHVZmlhvz]|-?\\d*\\.?\\d+");


    /**
     * Analyse un plan SVG et retourne les zones exploitables
     */
    public PlanAnalysisResult analyzeSvg(File svgFile) throws Exception {

        System.out.println(">>> analyzeSvg() appelé");
        System.out.println(">>> Fichier reçu : " + svgFile.getName());
        List<Zone> zones = new ArrayList<>();

        List<SVGPath> paths = loadPathsFromSvg(svgFile);
        System.out.println(">>> Nombre de paths trouvés : " + paths.size());

        int index = 1;
        for (SVGPath path : paths) {
            System.out.println("---- PATH ----");
            System.out.println(path.getContent());

            List<Point2D> contour = convertSvgPathToPoints(path);
            System.out.println("Points détectés : " + contour.size());

            // Ignore les paths invalides
            if (contour.size() < 3) continue;

            double area = calculatePolygonArea(contour);
            System.out.println("Surface calculée : " + area);

            // Filtre basique : ignore les micro-zones (placards, traits, etc.)
            if (area < 500) continue;

            Zone zone = new Zone(
                    index++,
                    path,
                    contour,
                    area,
                    true // placeable par défaut, ajustable ensuite
            );

            zones.add(zone);
        }


        if (zones.isEmpty()) {
            return new PlanAnalysisResult(false, List.of());
        }

        // Trier par surface décroissante
        zones.sort(Comparator.comparingDouble(Zone::getArea).reversed());

        // Suppression de la plus grande zone (souvent contour / extérieur)
        zones.remove(0);

        return new PlanAnalysisResult(true, zones);
    }

    /**
     * Extrait tous les SVGPath depuis un fichier SVG
     */
    private List<SVGPath> loadPathsFromSvg(File file) throws Exception {

        List<SVGPath> paths = new ArrayList<>();
        String svgContent = Files.readString(file.toPath());

        Pattern pattern = Pattern.compile("<path[^>]*d=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(svgContent);

        while (matcher.find()) {
            String d = matcher.group(1);
            SVGPath path = new SVGPath();
            SVGPath svgPath = new SVGPath();

            path.setContent(d);
            svgPath.setContent(d);

            // DEBUG VISUEL (temporaire)
            svgPath.setFill(null); // ou Color.TRANSPARENT
            svgPath.setStroke(javafx.scene.paint.Color.RED);
            svgPath.setStrokeWidth(2);

            svgPath.setStyle("-fx-fill: white;");
            path.setStyle("-fx-fill: red;");

            paths.add(path);
            paths.add(svgPath);
        }

        return paths;
    }

    /**
     * Convertit un SVGPath simple (M / L / Z) en liste de points
     * Hypothèse : plan propre (pas de courbes)
     */
    private List<Point2D> convertSvgPathToPoints(SVGPath path) {
        List<Point2D> points = new ArrayList<>();
        SvgCursor cursor = new SvgCursor();

        Matcher matcher = TOKEN_PATTERN.matcher(path.getContent());

        String command = null;

        while (matcher.find()) {
            String token = matcher.group();

            if (token.matches("[MLHVZmlhvz]")) {
                command = token;
                continue;
            }

            double value = Double.parseDouble(token);

            switch (command) {
                case "M", "L" -> {
                    double x = value;
                    matcher.find();
                    double y = Double.parseDouble(matcher.group());
                    cursor.x = x;
                    cursor.y = y;
                    points.add(new Point2D(cursor.x, cursor.y));
                }
                case "H" -> {
                    cursor.x = value;
                    points.add(new Point2D(cursor.x, cursor.y));
                }
                case "V" -> {
                    cursor.y = value;
                    points.add(new Point2D(cursor.x, cursor.y));
                }
            }
        }
        return points;
    }



    /**
     * Calcul de surface par formule du polygone (shoelace)
     */
    private double calculatePolygonArea(List<Point2D> points) {

        double area = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % n);
            area += (p1.getX() * p2.getY()) - (p2.getX() * p1.getY());
        }

        return Math.abs(area) / 2.0;
    }
}

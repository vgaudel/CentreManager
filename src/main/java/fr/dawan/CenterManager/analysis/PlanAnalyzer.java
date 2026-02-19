package fr.dawan.CenterManager.analysis;

import fr.dawan.CenterManager.model.PlanAnalysisResult;
import fr.dawan.CenterManager.model.Zone;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanAnalyzer {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[MLHVZmlhvz]|-?\\d*\\.?\\d+");

    // Parse les styles pour récupérer les couleurs des classes
    private Map<String, Color> parseCssStyles(String svgContent) {
        Map<String, Color> classColorMap = new HashMap<>();
        Pattern stylePattern = Pattern.compile("\\.(cls-\\d+)\\s*\\{[^}]*fill:\\s*#([0-9a-fA-F]{6})");
        Matcher matcher = stylePattern.matcher(svgContent);
        while (matcher.find()) {
            String cls = matcher.group(1);
            String hex = matcher.group(2);
            Color color = Color.web("#" + hex);
            System.out.println(">>> Couleur " + color + " ajoute");
            classColorMap.put(cls, color);
        }
        return classColorMap;
    }

    public PlanAnalysisResult analyzeSvg(File svgFile) throws Exception {
        List<Zone> zones = new ArrayList<>();
        String svgContent = Files.readString(svgFile.toPath());

        // Récupérer couleurs CSS
        Map<String, Color> classColorMap = parseCssStyles(svgContent);

        // Récupérer le path principal (contour)
        SVGPath contour = null;
        Pattern pathPattern = Pattern.compile("<path[^>]*d=\"([^\"]+)\"[^>]*>");
        Matcher pathMatcher = pathPattern.matcher(svgContent);
        if (pathMatcher.find()) {
            contour = new SVGPath();
            contour.setContent(pathMatcher.group(1));
        }

        // Récupérer tous les <g> avec polygon ou rect
        Pattern groupPattern = Pattern.compile("<g[^>]*id=\"([^\"]+)\"[^>]*>(.*?)</g>", Pattern.DOTALL);
        Matcher groupMatcher = groupPattern.matcher(svgContent);
        int index = 1;

        while (groupMatcher.find()) {
            String groupId = groupMatcher.group(1);
            String groupContent = groupMatcher.group(2);

            // Cherche polygons
            Pattern polyPattern = Pattern.compile("<polygon[^>]*>");
            Matcher polyMatcher = polyPattern.matcher(groupContent);
            while (polyMatcher.find()) {
                System.out.println(">>> Polygons trouve");
                String tag = polyMatcher.group();
                String pointsStr = extractAttribute(tag, "points");
                String cls = extractAttribute(tag, "class");

                Polygon polygon = new Polygon();
                String[] tokens = pointsStr.trim().split("\\s+");
                for (int i = 0; i < tokens.length; i += 2) {
                    double x = Double.parseDouble(tokens[i]);
                    double y = Double.parseDouble(tokens[i + 1]);
                    polygon.getPoints().addAll(x, y);
                }

                Color fill = classColorMap.getOrDefault(cls, Color.color(Math.random(), Math.random(), Math.random(), 0.5));
                polygon.setFill(fill);
                polygon.setStroke(Color.BLACK);
                polygon.setStrokeWidth(1);

                List<Point2D> contourPoints = new ArrayList<>();
                for (int i = 0; i < polygon.getPoints().size(); i += 2) {
                    contourPoints.add(new Point2D(polygon.getPoints().get(i), polygon.getPoints().get(i + 1)));
                }

                Zone zone = new Zone(index++, polygon, contourPoints, calculatePolygonArea(contourPoints), true);
                zone.setGroupId(groupId);

                System.out.println("Zone: " + groupId + " Couleur = " + fill);

                zones.add(zone);
            }

            // Cherche rectangles
            Pattern rectPattern = Pattern.compile("<rect[^>]*>");
            Matcher rectMatcher = rectPattern.matcher(groupContent);
            while (rectMatcher.find()) {
                System.out.println(">>> Rectangles trouve");
                String tag = rectMatcher.group();
                String cls = extractAttribute(tag, "class");
                double x = Double.parseDouble(extractAttribute(tag, "x"));
                double y = Double.parseDouble(extractAttribute(tag, "y"));
                double w = Double.parseDouble(extractAttribute(tag, "width"));
                double h = Double.parseDouble(extractAttribute(tag, "height"));

                Rectangle rectangle = new Rectangle(x, y, w, h);
                Color fill = classColorMap.getOrDefault(cls, Color.color(Math.random(), Math.random(), Math.random(), 0.5));
                rectangle.setFill(fill);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);

                List<Point2D> contourPoints = List.of(
                        new Point2D(x, y),
                        new Point2D(x + w, y),
                        new Point2D(x + w, y + h),
                        new Point2D(x, y + h)
                );

                Zone zone = new Zone(index++, rectangle, contourPoints, calculatePolygonArea(contourPoints), true);
                zone.setGroupId(groupId);

                System.out.println(">>> Zone: " + groupId + " Couleur = " + fill);

                zones.add(zone);
            }
        }

        // Si on veut filtrer la plus grande zone contour, on peut garder le path principal
        if (contour != null) {
            System.out.println(">>> Contour trouve");
            List<Point2D> contourPoints = convertSvgPathToPoints(contour);
            Zone contourZone = new Zone(0, contour, contourPoints, calculatePolygonArea(contourPoints), false);
            contourZone.setGroupId("Contour");
            zones.add(contourZone); // Optionnel : tu peux le mettre à part
            System.out.println(">>> Contour ajoute");
        }

        return new PlanAnalysisResult(!zones.isEmpty(), zones);
    }

    private List<Point2D> convertSvgPathToPoints(SVGPath path) {
        List<Point2D> points = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(path.getContent());
        String command = null;
        double cx = 0;
        double cy = 0;

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
                    cx = x; cy = y;
                    points.add(new Point2D(cx, cy));
                }
                case "H" -> { cx = value; points.add(new Point2D(cx, cy)); }
                case "V" -> { cy = value; points.add(new Point2D(cx, cy)); }
                default -> { break; }
            }
        }
        return points;
    }

    private double calculatePolygonArea(List<Point2D> points) {
        double area = 0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % n);
            area += (p1.getX() * p2.getY()) - (p2.getX() * p1.getY());
        }
        return Math.abs(area) / 2.0;
    }

    private String extractAttribute(String tag, String attributeName) {
        Pattern pattern = Pattern.compile(attributeName + "\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tag);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

package fr.dawan.CenterManager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * Analyse un plan (Image) comme une grille dynamique par salle.
 * - Détecte d'abord les différentes salles (par couleur).
 * - Pour chaque salle, détecte automatiquement la taille de case locale.
 * - Chaque salle a sa propre grille avec sa propre taille de case.
 */
public class MapGridService {

    public record Cell(int col, int row, Color color, int cellSize) {
    }

    public record Room(Color color, int minX, int minY, int maxX, int maxY, int cellSize, List<Cell> cells) {
    }

    private Image image;
    private Map<Color, Room> roomsByColor = new HashMap<>();

    public MapGridService(Image image, int cellSize) {
        this.image = image;
        buildGridWithFixedSize(cellSize);
    }

    /**
     * Constructeur qui détecte automatiquement la taille des cases par salle.
     */
    public MapGridService(Image image) {
        this.image = image;
        buildDynamicGrid();
    }

    /**
     * Construit une grille avec une taille de case fixe (ancienne méthode).
     */
    private void buildGridWithFixedSize(int cellSize) {
        if (image == null)
            return;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        if (reader == null)
            return;

        Map<Color, List<Cell>> tempRooms = new HashMap<>();

        int cols = width / cellSize;
        int rows = height / cellSize;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * cellSize + cellSize / 2;
                int y = row * cellSize + cellSize / 2;
                if (x >= width || y >= height)
                    continue;

                Color c = reader.getColor(x, y);

                if (isWall(c))
                    continue;

                Cell cell = new Cell(col, row, c, cellSize);
                tempRooms.computeIfAbsent(c, k -> new ArrayList<>()).add(cell);
            }
        }

        // Convertir en Room avec taille fixe
        for (Map.Entry<Color, List<Cell>> entry : tempRooms.entrySet()) {
            List<Cell> cells = entry.getValue();
            if (cells.isEmpty())
                continue;

            int minX = cells.stream().mapToInt(c -> c.col() * cellSize).min().orElse(0);
            int minY = cells.stream().mapToInt(c -> c.row() * cellSize).min().orElse(0);
            int maxX = cells.stream().mapToInt(c -> (c.col() + 1) * cellSize).max().orElse(width);
            int maxY = cells.stream().mapToInt(c -> (c.row() + 1) * cellSize).max().orElse(height);

            roomsByColor.put(entry.getKey(), new Room(entry.getKey(), minX, minY, maxX, maxY, cellSize, cells));
        }
    }

    /**
     * Construit une grille dynamique : détecte les salles puis la taille de case
     * pour chaque salle.
     */
    private void buildDynamicGrid() {
        if (image == null)
            return;

        PixelReader reader = image.getPixelReader();
        if (reader == null)
            return;

        // 1. Détecter toutes les salles (zones de même couleur non-noire)
        Map<Color, List<Point2D>> roomPixels = detectRooms(reader);

        System.out.println("[DEBUG] Nombre de salles détectées: " + roomPixels.size());

        // 2. Pour chaque salle, détecter sa taille de case et construire sa grille
        for (Map.Entry<Color, List<Point2D>> entry : roomPixels.entrySet()) {
            Color color = entry.getKey();
            List<Point2D> pixels = entry.getValue();

            if (pixels.isEmpty())
                continue;

            // Trouver les limites de la salle
            int minX = (int) pixels.stream().mapToDouble(Point2D::getX).min().orElse(0);
            int minY = (int) pixels.stream().mapToDouble(Point2D::getY).min().orElse(0);
            int maxX = (int) pixels.stream().mapToDouble(Point2D::getX).max().orElse(0);
            int maxY = (int) pixels.stream().mapToDouble(Point2D::getY).max().orElse(0);

            // Détecter la taille de case pour cette salle spécifique
            int roomCellSize = detectCellSizeForRoom(reader, minX, minY, maxX, maxY);
            System.out.println("[DEBUG] Salle " + color + " : taille de case détectée = " + roomCellSize + " pixels");

            // Construire la grille pour cette salle
            List<Cell> cells = buildRoomGrid(reader, color, minX, minY, maxX, maxY, roomCellSize);

            roomsByColor.put(color, new Room(color, minX, minY, maxX, maxY, roomCellSize, cells));
        }
    }

    /**
     * Détecte toutes les salles en scannant l'image pixel par pixel.
     */
    private Map<Color, List<Point2D>> detectRooms(PixelReader reader) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        Map<Color, List<Point2D>> rooms = new HashMap<>();

        // Échantillonnage pour accélérer (on ne scanne pas tous les pixels)
        int step = 2; // on prend un pixel sur 2

        for (int y = 0; y < height; y += step) {
            for (int x = 0; x < width; x += step) {
                Color c = reader.getColor(x, y);
                if (!isWall(c)) {
                    // Normaliser la couleur pour regrouper les pixels similaires
                    Color normalized = normalizeColor(c);
                    rooms.computeIfAbsent(normalized, k -> new ArrayList<>()).add(new Point2D(x, y));
                }
            }
        }

            // 2. Filtre les salles trop petites
            rooms.entrySet().removeIf(entry -> entry.getValue().size() < 1000);

        return rooms;
    }

    /**
     * Normalise une couleur pour regrouper les pixels similaires (tolérance de 5%).
     */
    private Color normalizeColor(Color c) {
        double tolerance = 0.05;
        double r = Math.round(c.getRed() / tolerance) * tolerance;
        double g = Math.round(c.getGreen() / tolerance) * tolerance;
        double b = Math.round(c.getBlue() / tolerance) * tolerance;
        return new Color(Math.min(1.0, r), Math.min(1.0, g), Math.min(1.0, b), 1.0);
    }

    /**
     * Détecte la taille de case pour une salle spécifique en analysant ses murs
     * locaux.
     */
    private int detectCellSizeForRoom(PixelReader reader, int minX, int minY, int maxX, int maxY) {
        int roomWidth = maxX - minX;
        int roomHeight = maxY - minY;

        List<Integer> verticalWalls = new ArrayList<>();
        List<Integer> horizontalWalls = new ArrayList<>();

        // Scan des murs verticaux dans la zone de la salle
        for (int x = minX; x < maxX; x++) {
            int blackPixels = 0;
            for (int y = minY; y < maxY; y++) {
                Color c = reader.getColor(x, y);
                if (isWallColor(c)) {
                    blackPixels++;
                }
            }
            if (blackPixels > roomHeight * 0.2) {
                verticalWalls.add(x - minX); // relatif à la salle
            }
        }

        // Scan des murs horizontaux dans la zone de la salle
        for (int y = minY; y < maxY; y++) {
            int blackPixels = 0;
            for (int x = minX; x < maxX; x++) {
                Color c = reader.getColor(x, y);
                if (isWallColor(c)) {
                    blackPixels++;
                }
            }
            if (blackPixels > roomWidth * 0.2) {
                horizontalWalls.add(y - minY); // relatif à la salle
            }
        }

        // Calculer les espacements
        List<Integer> spacings = new ArrayList<>();
        for (int i = 1; i < verticalWalls.size(); i++) {
            int spacing = verticalWalls.get(i) - verticalWalls.get(i - 1);
            if (spacing > 5 && spacing < 200) {
                spacings.add(spacing);
            }
        }
        for (int i = 1; i < horizontalWalls.size(); i++) {
            int spacing = horizontalWalls.get(i) - horizontalWalls.get(i - 1);
            if (spacing > 5 && spacing < 200) {
                spacings.add(spacing);
            }
        }

        if (spacings.isEmpty()) {
            // Valeur par défaut basée sur la taille de la salle
            int defaultSize = Math.min(roomWidth, roomHeight) / 10;
            return Math.max(10, Math.min(50, defaultSize)); // entre 10 et 50 pixels
        }

        // Trouver l'espacement le plus fréquent
        Map<Integer, Integer> frequency = new TreeMap<>();
        for (int spacing : spacings) {
            int rounded = ((spacing + 5) / 10) * 10;
            frequency.put(rounded, frequency.getOrDefault(rounded, 0) + 1);
        }

        int mostFrequent = 20;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        return mostFrequent;
    }

    /**
     * Construit la grille pour une salle spécifique.
     */
    private List<Cell> buildRoomGrid(PixelReader reader, Color roomColor, int minX, int minY, int maxX, int maxY,
            int cellSize) {
        List<Cell> cells = new ArrayList<>();
        int roomWidth = maxX - minX;
        int roomHeight = maxY - minY;

        int cols = roomWidth / cellSize;
        int rows = roomHeight / cellSize;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = minX + col * cellSize + cellSize / 2;
                int y = minY + row * cellSize + cellSize / 2;
                if (x >= maxX || y >= maxY)
                    continue;

                Color c = reader.getColor(x, y);
                if (isWall(c))
                    continue;

                // Vérifier que c'est bien la couleur de la salle (tolérance)
                if (colorsMatch(c, roomColor)) {
                    cells.add(new Cell(col, row, roomColor, cellSize));
                }
            }
        }

        return cells;
    }

    /**
     * Vérifie si deux couleurs correspondent (avec tolérance).
     */
    private boolean colorsMatch(Color c1, Color c2) {
        double tolerance = 0.1;
        return Math.abs(c1.getRed() - c2.getRed()) < tolerance &&
                Math.abs(c1.getGreen() - c2.getGreen()) < tolerance &&
                Math.abs(c1.getBlue() - c2.getBlue()) < tolerance;
    }

    private boolean isWall(Color c) {
        return isWallColor(c);
    }

    /**
     * Renvoie la cellule correspondant à des coordonnées pixel de l'image.
     */
    public Cell getCellAtPixel(double x, double y) {
        PixelReader reader = image.getPixelReader();
        if (reader == null)
            return null;

        int px = (int) x;
        int py = (int) y;

        // Trouver dans quelle salle on est en regardant la couleur du pixel
        Color pixelColor = reader.getColor(px, py);
        if (isWall(pixelColor))
            return null;

        Color normalized = normalizeColor(pixelColor);
        Room room = roomsByColor.get(normalized);

        if (room == null)
            return null;

        // Convertir les coordonnées globales en coordonnées relatives à la salle
        int relX = px - room.minX();
        int relY = py - room.minY();

        int col = relX / room.cellSize();
        int row = relY / room.cellSize();

        // Vérifier que la cellule existe dans cette salle
        for (Cell cell : room.cells()) {
            if (cell.col() == col && cell.row() == row) {
                return cell;
            }
        }

        return null;
    }

    /**
     * Renvoie toutes les cellules d'une salle (même couleur).
     */
    public List<Cell> getCellsForColor(Color color) {
        Room room = roomsByColor.get(normalizeColor(color));
        return room != null ? room.cells() : List.of();
    }

    /**
     * Convertit une Cell en coordonnées pixel (centre de la cellule) dans l'image
     * globale.
     */
    public Point2D cellToPixel(Cell cell) {
        Room room = roomsByColor.get(normalizeColor(cell.color()));
        if (room == null)
            return null;

        double x = room.minX() + cell.col() * cell.cellSize() + cell.cellSize() / 2.0;
        double y = room.minY() + cell.row() * cell.cellSize() + cell.cellSize() / 2.0;
        return new Point2D(x, y);
    }

    public Image getImage() {
        return image;
    }

    /**
     * Détecte automatiquement la taille des cases en analysant les murs noirs.
     * Cherche les lignes noires verticales et horizontales et mesure les
     * espacements.
     */
    public static int detectCellSize(Image image) {
        if (image == null)
            return 20; // valeur par défaut

        PixelReader reader = image.getPixelReader();
        if (reader == null)
            return 20;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // On cherche les lignes noires (murs) verticales et horizontales
        List<Integer> verticalWalls = new ArrayList<>();
        List<Integer> horizontalWalls = new ArrayList<>();

        // Scan vertical : cherche les colonnes qui sont majoritairement noires
        for (int x = 0; x < width; x++) {
            int blackPixels = 0;
            for (int y = 0; y < height; y++) {
                Color c = reader.getColor(x, y);
                if (isWallColor(c)) {
                    blackPixels++;
                }
            }
            // Si plus de 30% de la colonne est noire, c'est probablement un mur
            if (blackPixels > height * 0.3) {
                verticalWalls.add(x);
            }
        }

        // Scan horizontal : cherche les lignes qui sont majoritairement noires
        for (int y = 0; y < height; y++) {
            int blackPixels = 0;
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                if (isWallColor(c)) {
                    blackPixels++;
                }
            }
            // Si plus de 30% de la ligne est noire, c'est probablement un mur
            if (blackPixels > width * 0.3) {
                horizontalWalls.add(y);
            }
        }

        // Calcule les espacements entre les murs
        List<Integer> spacings = new ArrayList<>();

        // Espacements verticaux
        for (int i = 1; i < verticalWalls.size(); i++) {
            int spacing = verticalWalls.get(i) - verticalWalls.get(i - 1);
            if (spacing > 5 && spacing < 200) { // filtre les valeurs aberrantes
                spacings.add(spacing);
            }
        }

        // Espacements horizontaux
        for (int i = 1; i < horizontalWalls.size(); i++) {
            int spacing = horizontalWalls.get(i) - horizontalWalls.get(i - 1);
            if (spacing > 5 && spacing < 200) {
                spacings.add(spacing);
            }
        }

        if (spacings.isEmpty()) {
            System.out.println("[DEBUG] Aucun espacement détecté, utilisation de la valeur par défaut: 20");
            return 20;
        }

        // Trouve l'espacement le plus fréquent (mode)
        Map<Integer, Integer> frequency = new TreeMap<>();
        for (int spacing : spacings) {
            // Arrondir à la dizaine la plus proche pour regrouper les valeurs similaires
            int rounded = ((spacing + 5) / 10) * 10;
            frequency.put(rounded, frequency.getOrDefault(rounded, 0) + 1);
        }

        int mostFrequent = 20;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        System.out.println("[DEBUG] Espacements détectés: " + spacings);
        System.out.println("[DEBUG] Espacement le plus fréquent: " + mostFrequent + " (apparu " + maxCount + " fois)");

        return mostFrequent;
    }

    private static boolean isWallColor(Color c) {
        // noir ou quasi noir
        return c.getRed() < 0.1 && c.getGreen() < 0.1 && c.getBlue() < 0.1;
    }
}
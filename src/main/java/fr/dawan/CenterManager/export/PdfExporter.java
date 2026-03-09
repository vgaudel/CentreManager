package fr.dawan.CenterManager.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class PdfExporter {

    public static void exportToPdf(StackPane stackPane) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter en PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        int week = now.get(weekFields.weekOfYear());
        int year = now.getYear();
        fileChooser.setInitialFileName("Plan_Strasbourg_S" + String.format("%02d", week) + "_" + year + ".pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try {
            createPdf(file, stackPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createPdf(File file, StackPane stackPane) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        document.addPage(page);

        float pageWidth  = page.getMediaBox().getWidth();   // ~842
        float pageHeight = page.getMediaBox().getHeight();  // ~595

        PDPageContentStream cs = new PDPageContentStream(document, page);

        // ── Fond blanc total ───────────────────────────────────────────
        cs.setNonStrokingColor(java.awt.Color.WHITE);
        cs.addRect(0, 0, pageWidth, pageHeight);
        cs.fill();

        // ── Bandeau titre ──────────────────────────────────────────────
        float headerH = 50;
        cs.setNonStrokingColor(new java.awt.Color(30, 30, 80));
        cs.addRect(0, pageHeight - headerH, pageWidth, headerH);
        cs.fill();

        cs.setNonStrokingColor(java.awt.Color.WHITE);
        cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
        cs.beginText();
        cs.newLineAtOffset(30, pageHeight - 33);
        cs.showText("Plan Strasbourg - S" + getCurrentWeek() + " - " + getCurrentYear());
        cs.endText();

        // ── Ligne de séparation rouge (comme l'original) ───────────────
        cs.setStrokingColor(new java.awt.Color(200, 30, 30));
        cs.setLineWidth(3);
        cs.moveTo(0, pageHeight - headerH);
        cs.lineTo(pageWidth, pageHeight - headerH);
        cs.stroke();

        // ── Légende avec carrés colorés ────────────────────────────────
        float legendY = pageHeight - headerH - 30;
        addColoredLegend(cs, legendY);

        // ── Snapshot du StackPane (haute résolution) ───────────────────
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(javafx.scene.transform.Transform.scale(2, 2)); // 2x pour la qualité
        WritableImage snapshot = stackPane.snapshot(params, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        File tempFile = File.createTempFile("plan_snapshot", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);

        PDImageXObject image = PDImageXObject.createFromFile(tempFile.getAbsolutePath(), document);

        // Centrer le plan sur la page
        float imgW = (pageWidth - 50) * 0.65f;
        float imgH = imgW * (float) (snapshot.getHeight() / snapshot.getWidth());
        float imgX = (pageWidth - imgW) / 2;
        float imgY = pageHeight - headerH - 60 - imgH;

        // Ombre légère
        cs.setNonStrokingColor(new java.awt.Color(180, 180, 180));
        cs.addRect(imgX + 4, imgY - 4, imgW, imgH);
        cs.fill();

        cs.drawImage(image, imgX, imgY, imgW, imgH);

        // Bordure autour du plan
        // cs.setStrokingColor(new java.awt.Color(50, 50, 50));
        // cs.setLineWidth(1.5f);
        // cs.addRect(imgX, imgY, imgW, imgH);
        // cs.stroke();

        // ── Footer ─────────────────────────────────────────────────────
        float footerY = imgY - 50;
        cs.setNonStrokingColor(new java.awt.Color(60, 60, 60));
        cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
        cs.beginText();
        cs.newLineAtOffset(30, footerY);
        cs.showText("Jours de télétravail : ");
        cs.endText();

        footerY -= 14;
        cs.setFont(PDType1Font.HELVETICA, 9);
        // Ajouter les trainers dynamiquement ici
        cs.beginText();
        cs.newLineAtOffset(35, footerY);
        cs.showText("- Claude Helios : Lundi, Mardi");
        cs.endText();

        // Ligne de pied de page
        cs.setStrokingColor(new java.awt.Color(200, 200, 200));
        cs.setLineWidth(0.5f);
        cs.moveTo(30, 20);
        cs.lineTo(pageWidth - 30, 20);
        cs.stroke();

        // cs.setFont(PDType1Font.HELVETICA, 7);
        // cs.setNonStrokingColor(new java.awt.Color(150, 150, 150));
        // cs.beginText();
        // cs.newLineAtOffset(30, 10);
        // cs.showText("Dawan — Centre de Strasbourg");
        // cs.endText();

        cs.close();
        document.save(file);
        document.close();
        tempFile.deleteOnExit();
    }

    private static void addColoredLegend(PDPageContentStream cs, float y) throws IOException {
        float squareSize = 12;
        float textOffset = 16;

        // ■ En formation (rouge)
        cs.setNonStrokingColor(new java.awt.Color(220, 50, 50)); // -> Recuperer la color en dynamique
        cs.addRect(30, y, squareSize, squareSize);
        cs.fill();
        cs.setNonStrokingColor(java.awt.Color.BLACK);
        cs.setFont(PDType1Font.HELVETICA, 10);
        cs.beginText();
        cs.newLineAtOffset(30 + textOffset, y + 2);
        cs.showText("En formation");
        cs.endText();

        // ■ Hors Formation (bleu)
        cs.setNonStrokingColor(new java.awt.Color(50, 80, 200)); // -> Recuperer la color en dynamique
        cs.addRect(200, y, squareSize, squareSize);
        cs.fill();
        cs.setNonStrokingColor(java.awt.Color.BLACK);
        cs.beginText();
        cs.newLineAtOffset(200 + textOffset, y + 2);
        cs.showText("En Formation");
        cs.endText();

        // ■ Besoin FOG (vert)
        cs.setNonStrokingColor(new java.awt.Color(100, 200, 100)); // -> Recuperer la color en dynamique
        cs.addRect(380, y, squareSize, squareSize);
        cs.fill();
        cs.setNonStrokingColor(java.awt.Color.BLACK);
        cs.beginText();
        cs.newLineAtOffset(380 + textOffset, y + 2);
        cs.showText("Besoin FOG");
        cs.endText();
    }

    private static int getCurrentWeek() {
        return LocalDate.now().get(WeekFields.of(Locale.FRANCE).weekOfYear()) + 1;
    }

    private static int getCurrentYear() {
        return LocalDate.now().getYear();
    }
}
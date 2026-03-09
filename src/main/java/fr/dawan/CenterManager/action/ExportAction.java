package fr.dawan.CenterManager.action;

import fr.dawan.CenterManager.controller.MapViewController;
import fr.dawan.CenterManager.export.PdfExporter;
import javafx.scene.layout.StackPane;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ExportAction extends BaseMenuAction {

    public ExportAction(MapViewController mapViewController) {
        super(mapViewController);
    }

    private static final Logger logger = Logger.getLogger(ExportAction.class.getName());

    @Override
    public void execute() {
        if (mapViewController == null) {
            logger.log(Level.SEVERE, "MapViewController is null");
            return;
        }
        StackPane stackPane = mapViewController.getStackPane();
        if (stackPane == null) {
            logger.log(Level.SEVERE, "StackPane is null");
            return;
        }
        logger.log(Level.INFO, "Exporting PDF...");
        PdfExporter.exportToPdf(stackPane);
    }
}
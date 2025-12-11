package fr.dawan.CenterManager.controller;

import java.io.File;

import fr.dawan.CenterManager.ui.DragHandler;
import fr.dawan.CenterManager.ui.ZoomHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class MapViewController {
    @FXML ImageView planImage;

    @FXML Button addPlanButton;

    private ZoomHandler zoomHandler;
    private DragHandler dragHandler;

    @FXML
    public void initialize() {
        System.out.println("Plan Controller : Initialize");
        addPlanButton.setOnAction(e -> addImage());

        zoomHandler = new ZoomHandler(planImage);
        dragHandler = new DragHandler(planImage);
        zoomHandler.initialize();
        dragHandler.initialize();
    }

    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selectionner un Plan");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", ".jpeg")
        );

        File file = fileChooser.showOpenDialog(addPlanButton.getScene().getWindow());
        if (file != null) {
            planImage.setImage(new Image(file.toURI().toString()));

            // Ajuste à la taille du conteneur
            planImage.fitWidthProperty().bind(planImage.getParent().layoutBoundsProperty().map(b -> b.getWidth()));
            planImage.fitHeightProperty().bind(planImage.getParent().layoutBoundsProperty().map(b -> b.getHeight()));

            //Cache le bouton une fois l'image chargée
            addPlanButton.setVisible(false);
        }
    }
}

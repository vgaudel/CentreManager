package fr.dawan.CenterManager.controller;

import java.util.Map;

import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.TreeItemData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class DetailPaneController {

    @FXML private Label titleLabel;
    @FXML private GridPane fieldGrid;

    @FXML
    public void initialize() {
        hide(); // panel caché au départ
    }

    // Affiche le panel et met à jour les infos
    public void showDetailsFrom(Displayable d) {
        fieldGrid.getChildren().clear();

        titleLabel.setText(d.getDisplayName());

        Map<String, String> fields = d.getDisplayFields();

        int row = 0;
        for (var entry : fields.entrySet()) {
            Label key = new Label(entry.getKey() + " :");
            key.setStyle("-fx-font-weight: bold;");

            Label value = new Label(entry.getValue());

            GridPane.setRowIndex(key, row);
            GridPane.setColumnIndex(key, 0);

            GridPane.setRowIndex(value, row);
            GridPane.setColumnIndex(value, 1);

            fieldGrid.getChildren().addAll(key, value);

            row++;
        }
    }

    // Masque le panel
    public void hide() {
        titleLabel.setText("Aucun élément sélectionné");
        fieldGrid.getChildren().clear();
        fieldGrid.getRowConstraints().clear();
        fieldGrid.getColumnConstraints().clear();
    }
}

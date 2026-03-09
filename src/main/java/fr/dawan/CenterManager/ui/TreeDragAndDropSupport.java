package fr.dawan.CenterManager.ui;

import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.TreeItemData;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Active le drag & drop générique sur un TreeView<TreeItemData<?>>.
 */
public class TreeDragAndDropSupport {

    // DataFormat unique pour transférer un Displayable
    public static DataFormat displayableFormat;

    public static DataFormat getDisplayableFormat() {
        if (displayableFormat == null) {
            DataFormat existing = DataFormat.lookupMimeType("application/x-displayable");
            displayableFormat = (existing != null) ? existing : new DataFormat("application/x-displayable");
        }
        return displayableFormat;
    }

    public static void enableDrag(TreeView<TreeItemData<?>> treeView) {
        treeView.setCellFactory(tv -> {
            TreeCell<TreeItemData<?>> cell = new TreeCell<>() {
                @Override
                protected void updateItem(TreeItemData<?> item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getLabel());
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.isEmpty()) return;

                TreeItemData<?> data = cell.getItem();
                if (data == null || data.isCategory()) return;

                Object raw = data.getData();
                if (!(raw instanceof Displayable entity)) return;

                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.put(getDisplayableFormat(), entity);
                db.setContent(content);

                System.out.println("[DEBUG] Drag démarré avec : " + entity.getClass().getSimpleName() + " - " + entity.getDisplayName());
                event.consume();
            });

            return cell;
        });
    }
}
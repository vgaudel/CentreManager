package fr.dawan.CenterManager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import fr.dawan.CenterManager.dao.GenericDao;
import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.TreeItemData;
import fr.dawan.CenterManager.ui.TreeDragAndDropSupport;
import fr.dawan.CenterManager.model.EditableField;
import fr.dawan.CenterManager.model.RemoteDay;
import fr.dawan.CenterManager.util.DisplayFields;
import fr.dawan.CenterManager.util.FieldType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class TreeContextMenuHandler<T extends Displayable> {
    private static final Logger logger = Logger.getLogger(TreeContextMenuHandler.class.getName());

    private final TreeView<TreeItemData<T>> menuTree;

    public TreeContextMenuHandler(TreeView<TreeItemData<T>> treeView) {
        this.menuTree = treeView;
    }

    public void init() {
        menuTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeItemData<T> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof Displayable d) {
                    setText(d.getDisplayName()); // affiche juste le nom
                } else {
                    setText(item.getLabel()); // fallback pour les catégories
                }
            }
        });
    }

    public void checkItemClicked() {
        menuTree.setCellFactory(tv -> {
            TreeCell<TreeItemData<T>> cell = new TreeCell<>() {
                @Override
                protected void updateItem(TreeItemData<T> item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getLabel());
                }
            };

            // Clic droit : menu contextuel
            cell.setOnContextMenuRequested(event -> {
                if (!cell.isEmpty() && cell.getTreeItem() != null) {
                    TreeItem<TreeItemData<T>> treeItem = cell.getTreeItem();
                    ContextMenu menu = buildContextMenu(treeItem);
                    menu.show(cell, event.getScreenX(), event.getScreenY());
                }
            });

            // Détection du clic gauche
            cell.setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown() && !cell.isEmpty()) {
                    TreeItemData<T> data = cell.getItem();
                    if (data != null && !data.isCategory()) {
                        T entity = data.getData();
                        if (entity instanceof Displayable) {
                            Displayable displayable = (Displayable) entity;
                            System.out.println("[DEBUG] Clic gauche détecté sur: " + displayable.getDisplayName() +
                                    " (Position: " + event.getX() + ", " + event.getY() + ")");
                        }
                    }
                }
            });

            // Clic gauche + drag : drag & drop
            cell.setOnDragDetected(event -> {
                System.out.println("[DEBUG] Drag détecté!");
                if (cell.isEmpty()) {
                    logger.log(Level.WARNING, "Cell est vide, abandon");
                    return;
                }

                TreeItemData<T> data = cell.getItem();
                if (data == null || data.isCategory()) {
                    logger.log(Level.WARNING, "Data est null ou est une catégorie, abandon");
                    return;
                }

                T entity = data.getData();
                if (!(entity instanceof Displayable)) {
                    logger.log(Level.WARNING, "Entity n'est pas Displayable, abandon");
                    return;
                }
                Displayable displayable = (Displayable) entity;
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(displayable.getDisplayName());
                db.setContent(content);

                // Image visuelle qui suit la souris pendant le drag
                Label dragLabel = new Label(displayable.getDisplayName());
                dragLabel.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.9); " +
                                "-fx-border-color: black; " +
                                "-fx-padding: 3 6 3 6; " +
                                "-fx-font-size: 11px;");

                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                Image dragView = dragLabel.snapshot(params, null);
                db.setDragView(dragView, dragView.getWidth() / 2, dragView.getHeight() / 2);

                event.consume();
            });
            return cell;
        });
    }

    private ContextMenu buildContextMenu(TreeItem<TreeItemData<T>> treeItem) {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(createAddEditDeleteActions(treeItem));

        return menu;
    }

    private List<MenuItem> createAddEditDeleteActions(TreeItem<TreeItemData<T>> treeItem) {
        List<MenuItem> menuItems = new ArrayList<>();

        // ---------- ADD ----------
        if (treeItem.getValue().isCategory()) {
            MenuItem add = new MenuItem("Ajouter");
            add.setOnAction(e -> {
                TreeItemData<T> parentData = treeItem.getValue();
                if (!parentData.isCategory())
                    return;

                @SuppressWarnings("unchecked")
                GenericDao<T> dao = (GenericDao<T>) parentData.getDao();
                if (dao == null)
                    return;

                // 1. Créer une entité "vide" (ou avec un nom temporaire)
                T entity;
                try {
                    entity = dao.createFromName("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                // 2. Récupérer les champs éditables
                Map<String, EditableField> editableFields = entity.getEditableFields();

                // 3. Créer la fenêtre dynamique (même principe que pour EDIT)
                Dialog<Map<String, Object>> dialog = new Dialog<>();
                dialog.setTitle("Ajouter");
                dialog.setHeaderText("Créer un nouvel élément");

                ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                Map<String, Node> editors = new HashMap<>();
                int row = 0;

                for (var entry : editableFields.entrySet()) {
                    String fieldName = entry.getKey();
                    EditableField field = entry.getValue();

                    Label label = new Label(fieldName + " :");
                    Node editor = fieldFactory.get(field.type()).apply(field);

                    grid.add(label, 0, row);
                    grid.add(editor, 1, row);
                    editors.put(fieldName, editor);
                    row++;
                }

                dialog.getDialogPane().setContent(grid);

                // 4. Récupérer les valeurs saisies
                dialog.setResultConverter(button -> {
                    if (button == saveButtonType) {
                        Map<String, Object> values = new HashMap<>();
                        for (var entry : editableFields.entrySet()) {
                            String fieldName = entry.getKey();
                            EditableField field = entry.getValue();
                            Node editor = editors.get(fieldName);

                            Object extractedValue = extractorFactory.get(field.type()).apply(editor);

                            values.put(fieldName, extractedValue);
                        }
                        return values;
                    }
                    return null;
                });

                Optional<Map<String, Object>> result = dialog.showAndWait();
                if (result.isEmpty())
                    return;

                Map<String, Object> newValues = result.get();

                // 5. Mettre à jour l'entité avec les champs saisis
                entity.updateFromFields(newValues);

                // 6. Insérer en base
                try {
                    entity = dao.insert(entity);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                // 7. Ajouter au TreeView
                TreeItem<TreeItemData<T>> newItem = new TreeItem<>(new TreeItemData<>(entity.getDisplayName(), entity));
                newItem.getValue().setDaoFromParent(parentData.getDao());
                treeItem.getChildren().add(newItem);
                // menuTree.refresh();
                treeItem.setExpanded(true);
            });
            menuItems.add(add);
        } else {
            // ---------- EDIT ----------
            MenuItem edit = new MenuItem("Modifier");
            edit.setOnAction(e -> {
                TreeItemData<T> data = treeItem.getValue();
                if (data.isCategory())
                    return;

                T entity = data.getData();

                // 1. Champs éditables
                Map<String, EditableField> editableFields = entity.getEditableFields();

                // 2. Création de la fenêtre dynamique
                Dialog<Map<String, Object>> dialog = new Dialog<>();
                dialog.setTitle("Modifier");
                dialog.setHeaderText("Modifier les informations");

                ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                // Stocke chaque node pour extraction
                Map<String, Node> editors = new HashMap<>();

                int row = 0;
                for (var entry : editableFields.entrySet()) {
                    String fieldName = entry.getKey();
                    EditableField field = entry.getValue();

                    Label label = new Label(fieldName + " :");

                    // Factory => création du bon composant
                    Node editor = fieldFactory.get(field.type()).apply(field);

                    grid.add(label, 0, row);
                    grid.add(editor, 1, row);

                    editors.put(fieldName, editor);
                    row++;
                }

                dialog.getDialogPane().setContent(grid);

                // 3. Résultat dynamique avec extractors
                dialog.setResultConverter(button -> {
                    if (button == saveButtonType) {
                        Map<String, Object> newValues = new HashMap<>();

                        for (var entry : editableFields.entrySet()) {
                            String fieldName = entry.getKey();
                            EditableField field = entry.getValue();
                            Node editor = editors.get(fieldName);

                            // Extraction dynamique basé sur FieldType
                            Object extractedValue = extractorFactory.get(field.type()).apply(editor);

                            newValues.put(fieldName, extractedValue);
                        }
                        return newValues;
                    }
                    return null;
                });

                Optional<Map<String, Object>> result = dialog.showAndWait();
                if (result.isEmpty())
                    return;

                Map<String, Object> updatedFields = result.get();

                // 4. Mise à jour du model
                entity.updateFromFields(updatedFields);

                // 5. Mise à jour BD
                GenericDao<T> dao = treeItem.getParent().getValue().getDao();
                if (dao != null) {
                    try {
                        dao.update(entity);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // 6. Mise à jour du TreeView
                treeItem.setValue(new TreeItemData<>(entity.getDisplayName(), entity));
            });

            menuItems.add(edit);

            // ---------- DELETE ----------
            MenuItem delete = new MenuItem("Supprimer");
            delete.setOnAction(e -> {
                TreeItemData<T> data = treeItem.getValue();
                if (data == null || data.getData() == null)
                    return;

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText("Supprimer l’élément");
                alert.setContentText(
                        "Voulez-vous vraiment supprimer :\n\n" +
                                data.getData().getDisplayName() + " ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK)
                    return;

                GenericDao<T> dao = data.isCategory() ? data.getDao() : treeItem.getParent().getValue().getDao();

                if (dao != null && data.getData() != null) {
                    treeItem.getParent().getChildren().remove(treeItem);
                    try {
                        dao.delete(data.getData());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.err.println("Error: DAO is null on delete");
                }
            });

            menuItems.add(delete);
        }

        return menuItems;
    }

    private final Map<FieldType, Function<EditableField, Node>> fieldFactory = Map.of(
            FieldType.TEXT, field -> new TextField(
                    field.value() == null ? "" : field.value().toString()),

            FieldType.TEXTAREA, field -> {
                String value = field.value() == null ? "" : field.value().toString();
                TextArea ta = new TextArea(value);
                ta.setPrefRowCount(4);
                return ta;
            },

            FieldType.CHECKBOX_LIST, field -> {
                @SuppressWarnings("unchecked")
                List<RemoteDay> selected = field.value() == null ? List.of() : (List<RemoteDay>) field.value();

                Set<String> selectedDays = selected.stream()
                        .map(RemoteDay::getDay)
                        .collect(Collectors.toSet());

                VBox box = new VBox(4);

                for (String day : DisplayFields.REMOTE_DAYS) {
                    CheckBox cb = new CheckBox(day);

                    if (selectedDays.contains(day)) {
                        cb.setSelected(true);
                    }

                    box.getChildren().add(cb);
                }
                return box;
            }

    );

    private final Map<FieldType, Function<Node, Object>> extractorFactory = Map.of(
        FieldType.TEXT, node -> ((TextField) node).getText(),

        FieldType.TEXTAREA, node -> ((TextArea) node).getText(),

        FieldType.CHECKBOX_LIST, node -> {
            List<String> result = new ArrayList<>();
            VBox box = (VBox) node;
            for (Node c : box.getChildren()) {
                CheckBox cb = (CheckBox) c;
                if (cb.isSelected())
                    result.add(cb.getText());
            }
            return result;
        }
    );
}

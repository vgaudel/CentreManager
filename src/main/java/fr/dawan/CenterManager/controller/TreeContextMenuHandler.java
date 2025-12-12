package fr.dawan.CenterManager.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.dawan.CenterManager.dao.GenericDao;
import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.TreeItemData;
import fr.dawan.CenterManager.util.EditableField;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;

public class TreeContextMenuHandler<T extends Displayable> {

    private final TreeView<TreeItemData<T>> menuTree;
    // private final Map<Class<?>, GenericDao<?>> daoRegisty = new HashMap<>();

    public TreeContextMenuHandler(TreeView<TreeItemData<T>> treeView) {
        this.menuTree = treeView;
    }

    public void init() {
        menuTree.setCellFactory(tv -> new TreeCell<>() {
                @Override
                protected void updateItem(TreeItemData<T> item, boolean emplty) {
                    super.updateItem(item, emplty);
                    setText(emplty || item == null ? null : item.getLabel());
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

            cell.setOnContextMenuRequested(event -> {
                if (!cell.isEmpty()) {
                    TreeItem<TreeItemData<T>> treeItem = cell.getTreeItem();
                    ContextMenu menu = buildContextMenu(treeItem);
                    menu.show(cell, event.getScreenX(), event.getScreenY());
                }
            });


            return cell;
        });
    }

    private ContextMenu buildContextMenu(TreeItem<TreeItemData<T>> treeItem) {
        ContextMenu menu = new ContextMenu();
        // TreeItemData<T> data = treeItem.getValue();

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
                if (!parentData.isCategory()) return;

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Ajouter");
                dialog.setHeaderText("Nom du nouvel élément");
                dialog.showAndWait().ifPresent(name -> {
                    try {
                        @SuppressWarnings("unchecked")
                        GenericDao<T> dao = (GenericDao<T>) parentData.getDao();
                        T newEntity = dao.createFromName(name);

                        TreeItem<TreeItemData<T>> newItem =
                                new TreeItem<>(new TreeItemData<>(newEntity.getDisplayName(), newEntity));
                        newItem.getValue().setDaoFromParent(parentData.getDao());
                        treeItem.getChildren().add(newItem);
                        menuTree.refresh();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            });
            menuItems.add(add);
        } else {
            // ---------- EDIT ----------
            MenuItem edit = new MenuItem("Modifier");
            edit.setOnAction(e -> {
                TreeItemData<T> data = treeItem.getValue();
                if (data.isCategory()) return null;

                T entity = data.getData();

                // 1. On récupère les champs éditables de l'entité
                Map<String, EditableField> editableFields = entity.getEditableFields();

                // 2. Création d'une fenêtre dynamique
                Dialog<Map<String, Object>> dialog = new Dialog<>();
                dialog.setTitle("Modifier");
                dialog.setHeaderText("Modifier les informations");

                ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                // Stockage des composants pour récupérer les valeurs
                Map<String, Control> editors = new HashMap<>();

                int row = 0;

                for (Map.Entry<String, EditableField> entry : editableFields.entrySet()) {
                    String fieldName = entry.getKey();
                    EditableField fieldValue = entry.getValue();

                    Label label = new Label(fieldName + " :");

                    Control editor;

                    // 3. On crée un champ adapté selon le type
                    if (fieldValue.getType() == "") {
                        TextField tf = new TextField(fieldName);
                        editor = tf;
                    } else if (fieldValue.getType()) {
                        // Liste => TextArea (simple et générique)
                        TextArea ta = new TextArea(String.join("\n", (List<String>) fieldValue));
                        ta.setPrefRowCount(4);
                        editor = ta;
                    } else if (fieldValue instanceof List) {
                        // Liste => TextArea (simple et générique)
                        TextArea ta = new TextArea(String.join("\n", (List<String>) fieldValue));
                        ta.setPrefRowCount(4);
                        editor = ta;
                    }

                    grid.add(label, 0, row);
                    grid.add(editor, 1, row);

                    editors.put(fieldName, editor);
                    row++;
                }

                dialog.getDialogPane().setContent(grid);

                // 4. On transforme la boîte de dialogue en résultat
                dialog.setResultConverter(button -> {
                    if (button == saveButtonType) {
                        Map<String, Object> newValues = new HashMap<>();

                        for (Map.Entry<String, Control> entry : editors.entrySet()) {
                            String field = entry.getKey();
                            Control editor = entry.getValue();

                            if (editor instanceof TextField) {
                                newValues.put(field, ((TextField) editor).getText());

                            } else if (editor instanceof DatePicker) {
                                newValues.put(field, ((DatePicker) editor).getValue());

                            } else if (editor instanceof TextArea) {
                                List<String> list = Arrays
                                    .stream(((TextArea) editor).getText().split("\n"))
                                    .filter(s -> !s.isBlank())
                                    .toList();
                                newValues.put(field, list);
                            }
                        }

                        return newValues;
                    }
                    return null;
                });

                Optional<Map<String, Object>> result = dialog.showAndWait();
                if (result.isEmpty()) return;

                Map<String, Object> updatedFields = result.get();

                // 5. Mise à jour de l'entité
                try {
                    entity.updateFromFields(updatedFields);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                // 6. Mise à jour de la DB
                GenericDao<T> dao = treeItem.getParent().getValue().getDao();
                if (dao != null) {
                    try {
                        dao.update(entity);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // 7. Mise à jour dans le TreeView (nouveau displayName)
                treeItem.setValue(new TreeItemData<>(entity.getDisplayName(), entity));
            });


            menuItems.add(edit);

            // ---------- DELETE ----------
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(e -> {
                TreeItemData<T> data = treeItem.getValue();
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


    private GenericDao<?> getDaoFromParent(TreeItem<TreeItemData<?>> item) {
        TreeItem<TreeItemData<?>> parent = item.getParent();
        if (parent != null) {
            TreeItemData<?> parentData = parent.getValue();
            return parentData.getDao();
        }
        return null;
    }
}

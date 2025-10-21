package fr.dawan.CenterManager.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dawan.CenterManager.dao.GenericDao;
import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.TreeItemData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeContextMenuHandler<T extends Displayable> {

    private final TreeView<TreeItemData<T>> menuTree;
    private final Map<Class<?>, GenericDao<?>> daoRegisty = new HashMap<>();

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
        TreeItemData<T> data = treeItem.getValue();

        menu.getItems().addAll(createAddEditDeleteActions(treeItem));

        // if (data.isCategory()) {
        //     menu.getItems().add(createAddAction(treeItem));
        // } else {
        //     menu.getItems().addAll(createEditDeleteActions(treeItem));
        // }

        return menu;
    }

    // private MenuItem createAddAction(TreeItem<TreeItemData<T>> treeItem) {
    //     MenuItem add = new MenuItem("Ajouter");
    //     add.setOnAction(e -> {
    //         TextInputDialog dialog = new TextInputDialog();
    //         dialog.setTitle("Ajouter");
    //         dialog.setHeaderText("Nom du nouvel élément");
    //         dialog.showAndWait().ifPresent(name -> {
    //             TreeItemData<T> parentData = treeItem.getValue();
    //             if (!parentData.isCategory()) return;
    //             try {
    //                 // On avertit juste ici le compilateur qu'on sait ce qu'on fait :
    //                 @SuppressWarnings("unchecked")
    //                 GenericDao<T> dao = (GenericDao<T>) parentData.getDao();
    //                 T newEntity = dao.createFromName(name);
    //                 TreeItem<TreeItemData<T>> newItem =
    //                         new TreeItem<>(new TreeItemData<>(newEntity.getDisplayName(), newEntity));
    //                 newItem.getValue().setDaoFromParent(parentData.getDao());
    //                 treeItem.getChildren().add(newItem);
    //                 menuTree.refresh();
    //             } catch (Exception ex) {
    //                 ex.printStackTrace();
    //             }
    //         });
    //     });
    //     return add;
    // }

    // private List<MenuItem> createEditDeleteActions(TreeItem<TreeItemData<T>> treeItem) {
    //     MenuItem edit = new MenuItem("Modifier");
    //     edit.setOnAction(e -> {
    //         TextInputDialog dialog = new TextInputDialog();
    //         TreeItemData<T> data = treeItem.getValue();
    //         GenericDao<T> dao = treeItem.getParent().getValue().getDao();

    //         dialog.setTitle("Modifier");
    //         dialog.setHeaderText("Modifier le nom");
    //         dialog.showAndWait().ifPresent(newName -> {
    //             T entity = data.getData();
    //             // data.getLabelSetter().accept(entity, newName);

    //             data.setLabel(newName);
    //             treeItem.setValue(new TreeItemData<>(newName, data.getData()));


    //             if (dao != null) {
    //                 try {
    //                     dao.update(data.getData());
    //                     System.out.println("Update faite pour " + newName);
    //                 } catch (Exception e1) {
    //                     e1.printStackTrace();
    //                 }
    //             } else {
    //                 System.err.println("Error update");
    //             }
    //         });
    //     });

    //     MenuItem delete = new MenuItem("Supprimer");
    //     delete.setOnAction(e -> {
    //         TreeItemData<T> data = treeItem.getValue();
    //         GenericDao<T> dao = data.isCategory() ? data.getDao() : treeItem.getParent().getValue().getDao();
    //         if (dao != null && data.getData() != null) {
    //             treeItem.getParent().getChildren().remove(treeItem);
    //             try {
    //                 dao.delete(data.getData());
    //             } catch (SQLException e1) {
    //                 e1.printStackTrace();
    //             }
    //         } else {
    //             System.err.println("element supprimé");
    //         }
    //         System.out.println("Delete fin");
    //     });

    //     return List.of(edit, delete);
    // }

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
                if (data.isCategory()) return;

                TextInputDialog dialog = new TextInputDialog(data.getLabel());
                dialog.setTitle("Modifier");
                dialog.setHeaderText("Modifier le nom");
                dialog.showAndWait().ifPresent(newName -> {
                    data.setLabel(newName);

                    T entity = data.getData();
                    entity.setDisplayName(newName);

                    treeItem.setValue(new TreeItemData<>(newName, data.getData()));
                    treeItem.getValue().setDaoFromParent(treeItem.getParent().getValue().getDao());

                    GenericDao<T> dao = treeItem.getParent().getValue().getDao();
                    if (dao != null) {
                        try {
                            System.out.println(data);
                            dao.update(entity);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        System.err.println("Error: DAO is null on edit");
                    }
                });
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

package fr.dawan.CenterManager.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController {
    private String[] formateursData = {"bob", "sam", "eric"};
    private String[] formationsData = {"SQLLite", "Java", "C"};
    private String[] fogData = {"Unix", "Windows", "Linux"};

    @FXML
    private TreeView<String> menuTree;

    private TreeItem<String> createCategory(String name, String[] data) {
        TreeItem<String> category = new TreeItem<>(name);
        System.out.println("Ajout Tree Children");
        for (String s : data) {
            category.getChildren().add(new TreeItem<>(s));
        }
        // dernier enfant "Ajouter..."
        TreeItem<String> addItem = new TreeItem<>("Ajouter…");
        category.getChildren().add(addItem);

        // Optionnel : ajouter un listener sur le clic de "Ajouter…"
        addItem.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
            System.out.println("Ajouter un nouvel élément dans " + name);
            e.consume(); // éviter que ça se comporte comme un vrai élément
        });
        return category;
    }


    @FXML
    public void initialize() {
        TreeItem<String> root = new TreeItem<>(); // invisible root
        menuTree.setRoot(root);

        // Catégorie Formateurs
        TreeItem<String> formateurs = createCategory("Formateurs", formateursData);

        // Catégorie Formations
        TreeItem<String> formations = createCategory("Formations", formationsData);

        // Catégorie Fogs
        TreeItem<String> fogs = createCategory("Fogs", fogData);

        // Ajouter au root
        root.getChildren().addAll(List.of(formateurs, formations, fogs));

        checkItemClicked();
    }

    private void checkItemClicked() {
        menuTree.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnContextMenuRequested(event -> {
                if (!cell.isEmpty()) {
                    ContextMenu menu = new ContextMenu();

                    TreeItem<String> item = cell.getTreeItem();

                    // Si c'est un parent (a des enfants)
                    if (!item.getChildren().isEmpty()) {
                        MenuItem add = new MenuItem("Ajouter");
                        add.setOnAction(e -> {
                            TreeItem<String> newItem = new TreeItem<>("Nouvel élément");
                            item.getChildren().add(newItem);
                        });
                        menu.getItems().add(add);
                    } else {
                        // Sinon, c'est un enfant
                        MenuItem modify = new MenuItem("Modifier");
                        MenuItem delete = new MenuItem("Supprimer");

                        modify.setOnAction(e -> item.setValue(item.getValue() + " Modifié"));
                        delete.setOnAction(e -> item.getParent().getChildren().remove(item));

                        menu.getItems().addAll(modify, delete);
                    }

                    menu.show(cell, event.getScreenX(), event.getScreenY());
                }
            });

            return cell;
        });

    }
}

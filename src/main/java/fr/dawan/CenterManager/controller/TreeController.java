package fr.dawan.CenterManager.controller;

import java.sql.SQLException;
import java.util.List;

import fr.dawan.CenterManager.dao.FogDao;
import fr.dawan.CenterManager.dao.GenericDao;
import fr.dawan.CenterManager.dao.TrainerDao;
import fr.dawan.CenterManager.dao.TrainingDao;
import fr.dawan.CenterManager.model.Displayable;
import fr.dawan.CenterManager.model.Fog;
import fr.dawan.CenterManager.model.Trainer;
import fr.dawan.CenterManager.model.Training;
import fr.dawan.CenterManager.model.TreeItemData;
import fr.dawan.CenterManager.util.DaoRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController {
    private final FogDao fogDao = new FogDao();
    private final TrainerDao trainerDao = new TrainerDao();
    private final TrainingDao trainingDao = new TrainingDao();

    @FXML
    private TreeView<TreeItemData<?>> menuTree;

    @FXML
    public void initialize() throws SQLException {
        TreeContextMenuHandler handler = new TreeContextMenuHandler(menuTree);
        // final DaoRegistry daoRegistry = new DaoRegistry();

        handler.init();

        DaoRegistry.registerDao(Fog.class, fogDao);
        DaoRegistry.registerDao(Trainer.class, trainerDao);
        DaoRegistry.registerDao(Training.class, trainingDao);

        TreeItem<TreeItemData<?>> root = new TreeItem<>(new TreeItemData<>("root", true));
        menuTree.setRoot(root);

        root.getChildren().addAll(List.of(
            createCategory("Formateurs", trainerDao),
            createCategory("Formations", trainingDao),
            createCategory("Fogs", fogDao)
        ));

        handler.checkItemClicked();
    }

    private <T extends Displayable> TreeItem<TreeItemData<?>> createCategory(String name, GenericDao<T> dao) throws SQLException {
        TreeItem<TreeItemData<?>> categoryItem = new TreeItem<>(new TreeItemData<>(name, dao));

        List<T> items = dao.getAll();
        for (T data : items) {
            categoryItem.getChildren().add(new TreeItem<>(new TreeItemData<>(data.getDisplayName(), data)));
        }

        return categoryItem;
    }


    // private void checkItemClicked() {
    //     menuTree.setCellFactory(tv -> {
    //         TreeCell<TreeItemData<?>> cell = new TreeCell<>() {
    //             @Override
    //             protected void updateItem(TreeItemData<?> item, boolean empty) {
    //                 super.updateItem(item, empty);
    //                 setText(empty ? null : item.getLabel());
    //             }
    //         };

    //         cell.setOnContextMenuRequested(event -> {
    //             if (!cell.isEmpty()) {
    //                 ContextMenu menu = new ContextMenu();

    //                 TreeItem<TreeItemData<?>> item = cell.getTreeItem();
    //                 TreeItemData<?> data = item.getValue();
    //                 // Si c'est un parent (a des enfants)
    //                 if (data.isCategory()) {
    //                     MenuItem add = new MenuItem("Ajouter");
    //                     add.setOnAction(e -> {
    //                         TreeItem<TreeItemData<?>> newItem = new TreeItem<>(new TreeItemData<>("Nouvel élément", false, null));
    //                         item.getChildren().add(newItem);
    //                     });
    //                     menu.getItems().add(add);
    //                 } else {
    //                     // Sinon, c'est un enfant
    //                     MenuItem modify = new MenuItem("Modifier");
    //                     MenuItem delete = new MenuItem("Supprimer");

    //                     modify.setOnAction(e -> {
    //                         data.setLabel(data.getLabel() + " Modifié");
    //                         item.setValue(data);
    //                     });

    //                     delete.setOnAction(e -> item.getParent().getChildren().remove(item));

    //                     menu.getItems().addAll(modify, delete);
    //                 }

    //                 menu.show(cell, event.getScreenX(), event.getScreenY());
    //             }
    //         });

    //         return cell;
    //     });

    // }
}

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
            TreeItem<TreeItemData<?>> childItem = new TreeItem<>(new TreeItemData<Displayable>(data.getDisplayName(), data));
            childItem.getValue().setDaoFromParent(dao);
            categoryItem.getChildren().add(childItem);
        }

        return categoryItem;
    }
}

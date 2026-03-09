package fr.dawan.CenterManager.controller;

import fr.dawan.CenterManager.handler.MenuActionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MenuController {
    private static final Logger logger = Logger.getLogger(MenuController.class.getName());
    private MenuActionHandler menuActionHandler;

    public void setMenuActionHandler(MenuActionHandler menuActionHandler) {
        this.menuActionHandler = menuActionHandler;
    }

    @FXML
    private void handleNew(ActionEvent event) {
        logger.log(Level.INFO, "New");
        // TODO
    }

    @FXML
    private void handleOpen(ActionEvent event) {
        logger.log(Level.INFO, "Open");
        // TODO
    }

    @FXML
    private void handleExport(ActionEvent event) {
        logger.log(Level.INFO, "Export");
        menuActionHandler.handleMenuAction("export");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        logger.log(Level.INFO, "Save");
        // TODO
    }

    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }
}
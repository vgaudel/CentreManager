package fr.dawan.CenterManager.handler;

import java.util.HashMap;
import java.util.Map;

import fr.dawan.CenterManager.action.MenuAction;

public class MenuActionHandler {

    private Map<String, MenuAction> actions = new HashMap<>();

    public <T extends MenuAction> void registerAction(String name, MenuAction action) {
        actions.put(name, action);
    }

    public void handleMenuAction(String actionName) {
        MenuAction action = actions.get(actionName);
        if (action != null) {
            action.execute();
        } else {
            System.err.println("Action not found: " + actionName);
        }
    }
}

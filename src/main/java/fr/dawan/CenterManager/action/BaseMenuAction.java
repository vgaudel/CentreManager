package fr.dawan.CenterManager.action;

import fr.dawan.CenterManager.controller.MapViewController;

public abstract class BaseMenuAction implements MenuAction {
    protected MapViewController mapViewController;

    protected BaseMenuAction(MapViewController mapViewController) {
        this.mapViewController = mapViewController;
    }

    @Override
    public abstract void execute();
}
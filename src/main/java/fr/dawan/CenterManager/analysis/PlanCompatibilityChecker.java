package fr.dawan.CenterManager.analysis;

import java.util.List;

import fr.dawan.CenterManager.model.Zone;

public class PlanCompatibilityChecker {

    public boolean isCompatible(List<Zone> zones) {
        if (zones.isEmpty() || zones.size() > 50)
            return false;
        return true;
    }
}

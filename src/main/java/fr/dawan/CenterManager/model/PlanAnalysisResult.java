package fr.dawan.CenterManager.model;

import java.util.List;

public class PlanAnalysisResult {
    private final boolean compatible;
    private final List<Zone> zones;

    public PlanAnalysisResult(boolean compatible, List<Zone> zones) {
        this.compatible = compatible;
        this.zones = zones;
    }

    public List<Zone> getZones() {
        return zones;
    }
}

package fr.dawan.CenterManager.model;

import java.util.Map;

public interface Displayable extends Editable{
    String getDisplayName();
    void setDisplayName(Map<String, String> fields);
    Map<String, String> getDisplayFields();
    Map<String, Object> getEditableFields();
    // Map<String, Object>
}

package fr.dawan.CenterManager.model;

import java.util.Map;


public interface Displayable extends Editable{
    String getDisplayName();
    void setDisplayName(Map<String, String> fields);
    Map<String, Object> getDisplayFields();
    Map<String, EditableField> getEditableFields();
    // Map<String, Object>
}

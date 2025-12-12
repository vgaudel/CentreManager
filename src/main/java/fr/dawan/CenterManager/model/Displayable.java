package fr.dawan.CenterManager.model;

import java.util.Map;

import fr.dawan.CenterManager.util.EditableField;

public interface Displayable extends Editable{
    String getDisplayName();
    void setDisplayName(Map<String, String> fields);
    Map<String, String> getDisplayFields();
    Map<String, EditableField> getEditableFields();
    // Map<String, Object>
}

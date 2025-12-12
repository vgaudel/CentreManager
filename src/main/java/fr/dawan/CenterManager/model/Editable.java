package fr.dawan.CenterManager.model;

import java.util.Map;

import fr.dawan.CenterManager.util.EditableField;

public interface Editable {
    Map<String, EditableField> getEditableFields();
    void updateFromFields(Map<String, Object> fields);
}

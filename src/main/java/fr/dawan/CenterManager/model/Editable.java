package fr.dawan.CenterManager.model;

import java.util.Map;

public interface Editable {
    Map<String, EditableField> getEditableFields();
    void updateFromFields(Map<String, Object> fields);
}

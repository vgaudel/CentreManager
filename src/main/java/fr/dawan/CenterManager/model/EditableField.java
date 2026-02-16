package fr.dawan.CenterManager.model;

import java.util.List;

import fr.dawan.CenterManager.util.FieldType;

public record EditableField(FieldType type, Object value) {
    public Object getValue() { return value; }
    public FieldType getType() { return type; }
    public static EditableField text(String value) {
        return new EditableField(FieldType.TEXT, value);
    }

    public static EditableField textArea(String value) {
        return new EditableField(FieldType.TEXTAREA, value);
    }

    public static EditableField checkBoxList(List<?> value) {
        return new EditableField(FieldType.CHECKBOX_LIST, value);
    }

}

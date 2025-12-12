package fr.dawan.CenterManager.model;

import fr.dawan.CenterManager.util.FieldType;

public record EditableField(FieldType type, Object value) {
    public Object getValue() { return value; }
    public FieldType getType() { return type; }
}

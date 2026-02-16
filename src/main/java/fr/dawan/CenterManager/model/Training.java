package fr.dawan.CenterManager.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.dawan.CenterManager.util.DisplayFields;

public class Training implements Displayable {
    private int id;
    private String name;
    private String description;
    private final Map<String, Consumer<Object>> fieldSetters = Map.of(
        DisplayFields.TRAINING_TITLE, v -> setName(v.toString()),
        DisplayFields.DESCIRPTION, v -> setDescription(v.toString())
    );

    public Training(String name) {
        this.name = name;
    }

    public Training(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public void setDisplayName(Map<String, String> fields) {
        setName(fields.get("Name"));
    }

    @Override
    public Map<String, Object> getDisplayFields() {
        Map<String, Object> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.DISPLAY_NAME, getName());
        fields.put(DisplayFields.DESCIRPTION, getDescription());
        return (fields);
    }

    @Override
    public Map getEditableFields() {
        Map<String, EditableField> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.TRAINING_TITLE, EditableField.text(getName()));
        fields.put(DisplayFields.DESCIRPTION, EditableField.textArea(getDescription()));

        return fields;
    }

    @Override
    public void updateFromFields(Map<String, Object> fields) {
        fields.forEach((key, value) -> {
            Consumer<Object> setter = fieldSetters.get(key);

            if (setter != null)
                setter.accept(value);
        });
    }
}

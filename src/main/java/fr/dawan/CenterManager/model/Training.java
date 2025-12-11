package fr.dawan.CenterManager.model;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.dawan.CenterManager.util.DisplayFields;

public class Training implements Displayable {
    private int id;
    private String name;
    private String description;

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
    public Map<String, String> getDisplayFields() {
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.DISPLAY_NAME, getName());
        fields.put(DisplayFields.DESCIRPTION, getDescription());
        return (fields);
    }

    @Override
    public Map<String, Object> getEditableFields() {
        Map<String, Object> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.TRAINING_TITLE, getName());
        fields.put(DisplayFields.DESCIRPTION, getDescription());
        return fields;
    }

    @Override
    public void updateFromFields(Map<String, Object> fields) {
        if (fields.containsKey(DisplayFields.TRAINING_TITLE))
            setName(fields.get(DisplayFields.TRAINING_TITLE).toString());
        if (fields.containsKey(DisplayFields.DESCIRPTION))
            setDescription(fields.get(DisplayFields.DESCIRPTION).toString());
    }
}

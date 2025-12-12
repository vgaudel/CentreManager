package fr.dawan.CenterManager.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.dawan.CenterManager.util.DisplayFields;
import fr.dawan.CenterManager.util.FieldType;

public class Trainer implements Displayable {
    private int id;
    private String firstName; // Prenom
    private String lastName;  // Nom
    private List<RemoteDay> remoteDays; // 1 ou 2 jours de TT

    public Trainer(String name) {
        this.firstName = name;
    }

    public Trainer(int id, String firstName, String lastName, List<RemoteDay> remoteDays) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.remoteDays = remoteDays;
    }

    // --- Getters/Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<RemoteDay> getRemoteDays() { return remoteDays; }
    public void setRemoteDays(List<RemoteDay> remoteDays) { this.remoteDays = remoteDays; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (TT: " + remoteDays + ")";
    }

    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public void setDisplayName(Map<String, String> fields) {
        setFirstName(fields.get(DisplayFields.FIRST_NAME));
        setLastName(fields.get(DisplayFields.LAST_NAME));
    }

    @Override
    public Map<String, String> getDisplayFields() {
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.LAST_NAME, getLastName());
        fields.put(DisplayFields.FIRST_NAME, getFirstName());
        fields.put(DisplayFields.REMOTE_DAYS, getRemoteDays().toString());
        return (fields);
    }

    @Override
    public Map getEditableFields() {
        Map<String, EditableField> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.FIRST_NAME, new EditableField(FieldType.TEXT, getFirstName()));
        fields.put(DisplayFields.LAST_NAME, new EditableField(FieldType.TEXT, getLastName()));
        fields.put(DisplayFields.REMOTE_DAYS, new EditableField(FieldType.MULTI_CHOICE, getRemoteDays()));

        return fields;
    }



    @Override
    public void updateFromFields(Map<String, Object> fields) {
        if (fields.containsKey(DisplayFields.FIRST_NAME))
            setFirstName(fields.get(DisplayFields.FIRST_NAME).toString());
        if (fields.containsKey(DisplayFields.LAST_NAME))
            setLastName(fields.get(DisplayFields.LAST_NAME).toString());
        if (fields.containsKey(DisplayFields.REMOTE_DAYS)) {
            Object value = fields.get(DisplayFields.REMOTE_DAYS);
            if (value instanceof List<?> list) {
                List<RemoteDay> remoteDaysTemp = list.stream()
                                                 .filter(RemoteDay.class::isInstance)
                                                 .map(RemoteDay.class::cast)
                                                 .toList();
                setRemoteDays(remoteDaysTemp);
            }
        }
    }
}

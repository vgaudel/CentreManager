package fr.dawan.CenterManager.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.dawan.CenterManager.util.DisplayFields;

public class Trainer implements Displayable {
    private int id;
    private String firstName; // Prenom
    private String lastName;  // Nom
    private List<RemoteDay> remoteDays = new ArrayList<>(); // 1 ou 2 jours de TT

    private final Map<String, Consumer<Object>> fieldSetters = new LinkedHashMap<>();
    {
        fieldSetters.put(DisplayFields.FIRST_NAME, v -> setFirstName(v.toString()));
        fieldSetters.put(DisplayFields.LAST_NAME,  v -> setLastName(v.toString()));
        fieldSetters.put(DisplayFields.REMOTE_DAY, v -> {
            if (v instanceof List<?> list) {

                // Si la valeur vient du CHECKBOX_LIST, ce sera une liste de String
                List<RemoteDay> remoteDaysTemp = list.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(day -> new RemoteDay(0, this.id, day))
                        .toList();

                setRemoteDays(remoteDaysTemp);
            }
        });
    }


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
    public Map<String, Object> getDisplayFields() {
        Map<String, Object> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.LAST_NAME, getLastName());
        fields.put(DisplayFields.FIRST_NAME, getFirstName());
        fields.put(DisplayFields.REMOTE_DAY, getRemoteDays());
        return (fields);
    }

    @Override
    public Map getEditableFields() {
        Map<String, EditableField> fields = new LinkedHashMap<>();

        fields.put(DisplayFields.FIRST_NAME, EditableField.text(getFirstName()));
        fields.put(DisplayFields.LAST_NAME, EditableField.text(getLastName()));
        fields.put(DisplayFields.REMOTE_DAY, EditableField.checkBoxList(getRemoteDays()));

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

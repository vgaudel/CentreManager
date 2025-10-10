package fr.dawan.CenterManager.model;

public class Fog implements Displayable {
    private int id;
    private String name;
    private String description;

    public Fog(String name) {
        this.name = name;
    }

    public Fog(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Fog(int id, String name, String description) {
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
}

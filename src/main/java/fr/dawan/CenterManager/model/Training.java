package fr.dawan.CenterManager.model;

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
    public void setDisplayName(String name) {
        this.name = name;
    }
}

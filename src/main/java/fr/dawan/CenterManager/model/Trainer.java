package fr.dawan.CenterManager.model;

import java.util.List;

public class Trainer implements Displayable {
    private int id;
    private String firstName;
    private String lastName;
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
        return getFirstName();
    }
}

package fr.dawan.CenterManager.model;

import java.util.List;

public class Trainer {
    private int id;
    private String firstname;
    private String lastname;
    private List<RemoteDay> remoteDays; // 1 ou 2 jours de TT

    public Trainer() {}

    public Trainer(int id, String firstname, String lastname, List<RemoteDay> remoteDays) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.remoteDays = remoteDays;
    }

    // --- Getters/Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public List<RemoteDay> getRemoteDays() { return remoteDays; }
    public void setRemoteDays(List<RemoteDay> remoteDays) { this.remoteDays = remoteDays; }

    @Override
    public String toString() {
        return firstname + " " + lastname + " (TT: " + remoteDays + ")";
    }
}

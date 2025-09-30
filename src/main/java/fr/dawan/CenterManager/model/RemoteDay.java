package fr.dawan.CenterManager.model;

public class RemoteDay {
    private int id;
    private int trainerId;
    private String day; // "Lundi", "Mercredi", etc.

    public RemoteDay() {}

    public RemoteDay(int id, int trainerId, String day) {
        this.id = id;
        this.trainerId = trainerId;
        this.day = day;
    }

    // --- Getters / Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    @Override
    public String toString() {
        return day;
    }
}

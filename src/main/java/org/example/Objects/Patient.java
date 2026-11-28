package org.example.Objects;

public class Patient extends User {
    private final int id;
    private String illness;
    private String symptoms;

    public Patient(int id, String illness, String symptoms, String name, String email, String password, long phone) {
        super(name, email, password, phone);
        this.id = id;
        this.illness = illness;
        this.symptoms = symptoms;
    }

    @Override
    public int getId() { return id; }

    @Override
    public Role getRole() { return Role.PATIENT; }

    public String getIllness() { return illness; }
    public void setIllness(String illness) { this.illness = illness; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
}

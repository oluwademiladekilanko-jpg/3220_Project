package org.example.Objects;

public class Receptionist extends User {
    private int receptionistID;

    public Receptionist(int receptionistID, String name, String email, String password, long phone) {
        super(name, email, password, phone);
        this.receptionistID = receptionistID;
    }

    @Override
    public int getId() { return receptionistID; }

    @Override
    public Role getRole() { return Role.RECEPTIONIST; }

    public int getReceptionistID() { return receptionistID; }
    public void setReceptionistID(int receptionistID) { this.receptionistID = receptionistID; }
}

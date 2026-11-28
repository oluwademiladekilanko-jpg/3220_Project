package org.example.Objects;

public class Receptionist extends User {
    private final int id;

    public Receptionist(int id, String name, String email, String password, long phone) {
        super(name, email, password, phone);
        this.id = id;
    }

    @Override
    public int getId() { return id; }

    @Override
    public Role getRole() { return Role.RECEPTIONIST; }
}

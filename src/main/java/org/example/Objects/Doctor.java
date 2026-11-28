package org.example.Objects;

public class Doctor extends User {
    private int id;
    private String field;
    private long emergencyPhone;

    public Doctor(String name, String email, String password, String field, long phone, long emergencyPhone, int id) {
        super(name, email, password, phone);
        this.field = field;
        this.emergencyPhone = emergencyPhone;
        this.id = id;
    }

    @Override
    public int getId() { return id; }

    @Override
    public Role getRole() { return Role.DOCTOR; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public long getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(long emergencyPhone) { this.emergencyPhone = emergencyPhone; }
}

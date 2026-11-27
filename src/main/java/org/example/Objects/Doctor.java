package org.example.Objects;

public class Doctor extends User {
    private int docID;
    private String field;
    private long emrgphone;

    public Doctor(String name, String email, String password, String field, long phone, long emrgphone, int docID) {
        super(name, email, password, phone);
        this.field = field;
        this.emrgphone = emrgphone;
        this.docID = docID;
    }

    @Override
    public int getId() { return docID; }

    @Override
    public Role getRole() { return Role.DOCTOR; }

    public int getDocID() { return docID; }
    public void setDocID(int docID) { this.docID = docID; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public long getEmrgphone() { return emrgphone; }
    public void setEmrgphone(long emrgphone) { this.emrgphone = emrgphone; }
}

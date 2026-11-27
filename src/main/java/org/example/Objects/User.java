package org.example.Objects;

public abstract class User {
    private String name;
    private String email;
    private String password; // holds the password HASH, never plain text
    private long phone;

    protected User(String name, String email, String password, long phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public abstract int getId();

    public abstract Role getRole();

    public long getPhone() { return phone; }
    public void setPhone(long phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

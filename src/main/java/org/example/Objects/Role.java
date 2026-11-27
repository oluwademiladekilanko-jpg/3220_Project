package org.example.Objects;

/** The three kinds of actor in the clinic. Replaces ad-hoc string checks on email suffixes and table names. */
public enum Role {
    PATIENT("patient"),
    DOCTOR("doctor"),
    RECEPTIONIST("receptionist");

    private final String tableName;

    Role(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    /** @doctor.ca -> DOCTOR, @clinic.ca -> RECEPTIONIST, anything else -> PATIENT. */
    public static Role fromEmail(String email) {
        String e = email == null ? "" : email.trim().toLowerCase();
        if (e.endsWith("@doctor.ca")) return DOCTOR;
        if (e.endsWith("@clinic.ca")) return RECEPTIONIST;
        return PATIENT;
    }

    /** Returns null when the name matches no role (e.g. logged out or "invalid"). */
    public static Role fromTableName(String name) {
        for (Role r : values()) {
            if (r.tableName.equals(name)) return r;
        }
        return null;
    }
}

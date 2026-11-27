package org.example.backend.service;

import org.example.Objects.Doctor;
import org.example.Objects.Patient;
import org.example.Objects.Receptionist;
import org.example.Objects.Role;
import org.example.Objects.User;
import org.example.backend.persistence.UserRepository;
import org.example.backend.security.PasswordHasher;

/** Validates registrations and builds the right User subtype. Result strings match what the UI already displays. */
public class SignupService {
    private interface UserBuilder {
        User build(String name, String email, String passwordHash, long phone);
    }

    private final UserRepository users;
    private final PasswordHasher hasher;

    public SignupService(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    public String signupPatient(String name, String email, String password, String phone, String illness, String symptoms) {
        if (email != null && Role.fromEmail(email) != Role.PATIENT) return "invalid patient email";
        if (blank(illness)) return "all fields required";
        final String ill = illness.trim();
        final String sym = symptoms == null ? null : symptoms.trim();
        return register(name, email, password, phone,
                (n, e, h, p) -> new Patient(0, ill, sym, n, e, h, p));
    }

    public String signupDoctor(String name, String email, String password, String phone, String field, String emrgphone) {
        if (blank(field) || blank(emrgphone)) return "all fields required";
        if (email == null || Role.fromEmail(email) != Role.DOCTOR) return "invalid doctor email";
        Long emergency = parsePhone(emrgphone);
        if (emergency == null) return "invalid phone";
        final String f = field.trim();
        final long em = emergency;
        return register(name, email, password, phone,
                (n, e, h, p) -> new Doctor(n, e, h, f, p, em, 0));
    }

    public String signupReceptionist(String name, String email, String password, String phone) {
        if (email == null || Role.fromEmail(email) != Role.RECEPTIONIST) return "invalid receptionist email";
        return register(name, email, password, phone,
                (n, e, h, p) -> new Receptionist(0, n, e, h, p));
    }

    private String register(String name, String email, String password, String phone, UserBuilder builder) {
        if (blank(name) || blank(email) || blank(password) || blank(phone)) return "all fields required";
        String cleanEmail = email.trim().toLowerCase();
        if (!cleanEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "invalid email";
        Long parsedPhone = parsePhone(phone);
        if (parsedPhone == null) return "invalid phone";

        User user = builder.build(name.trim(), cleanEmail, hasher.hash(password), parsedPhone);
        switch (users.save(user)) {
            case SAVED: return "success";
            case DUPLICATE_EMAIL: return "email already registered";
            default: return "signup failed";
        }
    }

    private static Long parsePhone(String raw) {
        try {
            return Long.parseLong(raw.trim().replaceAll("[\\s-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

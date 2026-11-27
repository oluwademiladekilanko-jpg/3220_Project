package org.example.backend.service;

import org.example.Objects.Role;
import org.example.Objects.User;
import org.example.backend.persistence.UserRepository;
import org.example.backend.security.PasswordHasher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

public class AuthService {
    private final UserRepository users;
    private final PasswordHasher hasher;

    public AuthService(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    /** Empty means invalid credentials. Legacy plain-text passwords are accepted once and upgraded to a hash. */
    public Optional<SessionContext> login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        String trimmed = email.trim().toLowerCase();
        Role role = Role.fromEmail(trimmed);

        Optional<User> found = users.findByEmail(role, trimmed);
        if (!found.isPresent()) return Optional.empty();

        User user = found.get();
        String stored = user.getPassword();
        boolean ok;
        if (hasher.isHashed(stored)) {
            ok = hasher.verify(password, stored);
        } else {
            ok = stored != null && MessageDigest.isEqual(
                    stored.getBytes(StandardCharsets.UTF_8), password.getBytes(StandardCharsets.UTF_8));
            if (ok) {
                users.updatePassword(role, user.getId(), hasher.hash(password));
            }
        }
        return ok ? Optional.of(new SessionContext(role, user.getId())) : Optional.<SessionContext>empty();
    }
}

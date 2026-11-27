package org.example.backend.service;

import org.example.Objects.Role;

/** Immutable description of who is acting. Replaces reading public static fields scattered across the app. */
public final class SessionContext {
    private final Role role;
    private final int userId;

    public SessionContext(Role role, int userId) {
        this.role = role;
        this.userId = userId;
    }

    public Role getRole() { return role; }
    public int getUserId() { return userId; }
    public boolean hasRole(Role other) { return role != null && role == other; }
}

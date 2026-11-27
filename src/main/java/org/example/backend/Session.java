package org.example.backend;

import org.example.Objects.Role;
import org.example.backend.service.SessionContext;

/**
 * Transitional holder kept so existing UI code (Session.id / Session.role) still compiles.
 * Services never read these fields: they receive an immutable SessionContext built by current().
 */
public class Session {
    public static int id;
    public static String role;

    public static SessionContext current() {
        return new SessionContext(Role.fromTableName(role), id);
    }
}

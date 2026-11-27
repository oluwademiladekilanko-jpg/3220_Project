package org.example.backend;

import org.example.Objects.Role;
import org.example.backend.service.AppContext;
import org.example.backend.service.SessionContext;

import java.util.Optional;

/** Transitional facade: returns the role's table name ("patient", "doctor", "receptionist") or "invalid". */
public class login {
    public static String login(String email, String password) {
        Optional<SessionContext> result = AppContext.get().auth().login(email, password);
        if (!result.isPresent()) {
            return "invalid";
        }
        Role role = result.get().getRole();
        Session.id = result.get().getUserId();
        Session.role = role.getTableName();
        return role.getTableName();
    }
}

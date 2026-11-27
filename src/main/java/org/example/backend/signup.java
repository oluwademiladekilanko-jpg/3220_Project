package org.example.backend;

import org.example.backend.service.AppContext;

/** Transitional facade: same signatures as before, now delegating to SignupService. */
public class signup {
    public static String signupPatient(String name, String email, String password, String phone, String illness, String symptoms) {
        return AppContext.get().signup().signupPatient(name, email, password, phone, illness, symptoms);
    }

    public static String signupDoctor(String name, String email, String password, String phone, String field, String emrgphone) {
        return AppContext.get().signup().signupDoctor(name, email, password, phone, field, emrgphone);
    }

    public static String signupReceptionist(String name, String email, String password, String phone) {
        return AppContext.get().signup().signupReceptionist(name, email, password, phone);
    }
}

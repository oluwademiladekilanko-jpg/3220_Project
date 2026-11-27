package org.example.backend.service;

import org.example.Objects.Role;
import org.example.backend.persistence.AppointmentRepository;
import org.example.backend.persistence.AvailabilityRepository;
import org.example.backend.persistence.ConnectionProvider;
import org.example.backend.persistence.SQLiteAppointmentRepository;
import org.example.backend.persistence.SQLiteAvailabilityRepository;
import org.example.backend.persistence.SQLiteConnectionProvider;
import org.example.backend.persistence.SQLiteUserRepository;
import org.example.backend.persistence.SchemaInitializer;
import org.example.backend.persistence.UserRepository;
import org.example.backend.security.PasswordHasher;
import org.example.backend.security.Pbkdf2PasswordHasher;

import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;

/** Composition root: the ONE place where concrete classes are chosen and wired together (dependency injection by hand). */
public final class AppContext {
    public static final String DEFAULT_URL = "jdbc:sqlite:./identifier.sqlite";

    private static AppContext instance;

    private final ConnectionProvider connections;
    private final SchemaInitializer schema;
    private final UserRepository users;
    private final AuthService auth;
    private final SignupService signup;
    private final AppointmentService appointments;

    private AppContext(String url) {
        connections = new SQLiteConnectionProvider(url);
        schema = new SchemaInitializer(connections);
        users = new SQLiteUserRepository(connections);
        AppointmentRepository appointmentRepo = new SQLiteAppointmentRepository(connections);
        AvailabilityRepository availabilityRepo = new SQLiteAvailabilityRepository(connections);
        PasswordHasher hasher = new Pbkdf2PasswordHasher();

        Map<Role, CancellationPolicy> policies = new EnumMap<>(Role.class);
        policies.put(Role.PATIENT, new PatientCancellationPolicy());
        policies.put(Role.RECEPTIONIST, new ReceptionistCancellationPolicy());

        auth = new AuthService(users, hasher);
        signup = new SignupService(users, hasher);
        appointments = new AppointmentService(appointmentRepo, availabilityRepo, users, policies, Clock.systemDefaultZone());
    }

    public static synchronized AppContext get() {
        if (instance == null) {
            instance = new AppContext(DEFAULT_URL);
        }
        return instance;
    }

    public ConnectionProvider connections() { return connections; }
    public SchemaInitializer schema() { return schema; }
    public UserRepository users() { return users; }
    public AuthService auth() { return auth; }
    public SignupService signup() { return signup; }
    public AppointmentService appointments() { return appointments; }
}

package org.example.backend.persistence;

import org.example.Objects.Doctor;
import org.example.Objects.Patient;
import org.example.Objects.Role;
import org.example.Objects.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    enum SaveResult { SAVED, DUPLICATE_EMAIL, FAILED }

    Optional<User> findByEmail(Role role, String email);

    SaveResult save(User user);

    void updatePassword(Role role, int userId, String newHash);

    List<Patient> findAllPatients();

    /** A null or empty field returns every doctor. */
    List<Doctor> findDoctors(String field);

    List<String> findDoctorFields();
}

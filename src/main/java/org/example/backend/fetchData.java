package org.example.backend;

import org.example.Objects.Patient;
import org.example.backend.service.AppContext;

import java.util.List;

/** Transitional facade. */
public class fetchData {
    public static List<Patient> GetPatientData() {
        return AppContext.get().users().findAllPatients();
    }
}

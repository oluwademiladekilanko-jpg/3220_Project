package org.example.backend.service;

import org.example.Objects.Appointment;

import java.time.LocalDateTime;

/** A patient may cancel only their own appointments, and only while they are still in the future. */
public class PatientCancellationPolicy implements CancellationPolicy {
    @Override
    public boolean permits(Appointment appointment, SessionContext actor, LocalDateTime now) {
        return appointment.getPatient().getId() == actor.getUserId()
                && appointment.getDate().isAfter(now);
    }
}

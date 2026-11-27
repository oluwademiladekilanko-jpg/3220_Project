package org.example.backend.service;

import org.example.Objects.Appointment;

import java.time.LocalDateTime;

/** Receptionists may cancel any appointment. */
public class ReceptionistCancellationPolicy implements CancellationPolicy {
    @Override
    public boolean permits(Appointment appointment, SessionContext actor, LocalDateTime now) {
        return true;
    }
}

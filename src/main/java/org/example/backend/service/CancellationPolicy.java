package org.example.backend.service;

import org.example.Objects.Appointment;

import java.time.LocalDateTime;

/** Strategy: who may cancel which appointment, and when. New rules = new implementation, no edits to the service. */
public interface CancellationPolicy {
    boolean permits(Appointment appointment, SessionContext actor, LocalDateTime now);
}

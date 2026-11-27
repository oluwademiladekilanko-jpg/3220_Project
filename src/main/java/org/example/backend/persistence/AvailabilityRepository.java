package org.example.backend.persistence;

import org.example.Objects.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository {
    /** Slots after the given moment that are offered and not yet booked. */
    List<TimeSlot> findOpenSlots(int doctorId, LocalDateTime after);

    /** Every offered slot, each flagged as booked or free. */
    List<TimeSlot> findAllSlots(int doctorId);

    void addSlot(int doctorId, LocalDateTime slot);

    void removeSlot(int doctorId, LocalDateTime slot);
}

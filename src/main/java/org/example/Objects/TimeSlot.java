package org.example.Objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Value object for one bookable slot of one doctor. Replaces raw "yyyy-MM-dd HH:mm" strings. */
public class TimeSlot {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int doctorId;
    private final LocalDateTime start;
    private final boolean booked;

    public TimeSlot(int doctorId, LocalDateTime start, boolean booked) {
        this.doctorId = doctorId;
        this.start = start;
        this.booked = booked;
    }

    public int getDoctorId() { return doctorId; }
    public LocalDateTime getStart() { return start; }
    public boolean isBooked() { return booked; }

    public boolean isAfter(LocalDateTime moment) { return start.isAfter(moment); }

    public String format() { return start.format(FORMAT); }

    public static LocalDateTime parse(String text) { return LocalDateTime.parse(text.trim(), FORMAT); }
}

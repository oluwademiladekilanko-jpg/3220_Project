package org.example.Objects;

import java.time.LocalDateTime;

/** A booked appointment. Immutable: changing an appointment means cancelling and re-booking. */
public class Appointment {
    private final int id;
    private final Patient patient;
    private final Doctor doc;
    private final LocalDateTime date;
    private final String location;

    public Appointment(int id, Patient patient, Doctor doc, LocalDateTime date, String location) {
        this.id = id;
        this.patient = patient;
        this.doc = doc;
        this.date = date;
        this.location = location;
    }

    public int getId() { return id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoc() { return doc; }
    public LocalDateTime getDate() { return date; }
    public String getLocation() { return location; }
}

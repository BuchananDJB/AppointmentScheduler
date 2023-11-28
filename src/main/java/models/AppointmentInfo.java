package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AppointmentInfo {

    private final int doctorId;
    private final int personId;
    private Date appointmentTime;
    private final boolean isNewPatientAppointment;

    public AppointmentInfo(int doctorId, int personId, String appointmentTime, boolean isNewPatientAppointment) {
        this.doctorId = doctorId;
        this.personId = personId;
        this.isNewPatientAppointment = isNewPatientAppointment;
        try {
            this.appointmentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZZZ").parse(appointmentTime);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    public int getDoctorId() { return doctorId; }

    public Doctor getDoctor() {
        return Doctor.getDoctorById(doctorId);
    }

    public int getPersonId() {
        return personId;
    }

    public Date getAppointmentTimeAsDate() {
        return appointmentTime;
    }

    public LocalDate getAppointmentTimeAsLocalDate() {
        return appointmentTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public boolean isNewPatientAppointment() {
        return isNewPatientAppointment;
    }
}

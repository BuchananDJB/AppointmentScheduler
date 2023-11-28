package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentInfoRequest {

    private final int doctorId;
    private final int personId;
    private final String appointmentTime;
    private final boolean isNewPatientAppointment;
    private final int requestId;

    public AppointmentInfoRequest(int doctorId, int personId, String appointmentTime, boolean isNewPatientAppointment, int requestId) {
        this.doctorId = doctorId;
        this.personId = personId;
        this.appointmentTime = appointmentTime;
        this.isNewPatientAppointment = isNewPatientAppointment;
        this.requestId = requestId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public Doctor getDoctor() {
        return Doctor.getDoctorById(doctorId);
    }

    public int getPersonId() {
        return personId;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public Date getAppointmentTimeAsDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZZZ").parse(appointmentTime);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean isNewPatientAppointment() {
        return isNewPatientAppointment;
    }

    public int getRequestId() {
        return requestId;
    }
}

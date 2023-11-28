package service;

import models.AppointmentInfo;
import models.AppointmentInfoRequest;
import models.AppointmentRequest;

import java.util.List;

public interface ApiService {
    /**
     * Hit this endpoint to reset the test system before each 'run' of your program.
     */
    void start();

    /**
     * This is an optional endpoint that will allow you to mark a test run as 'done'.
     * @return the current schedule as you have requested it, for your debugging pleasure.
     */
    List<AppointmentInfo> stop();

    /**
     * @return the next appointment request to be serviced.
     */
    AppointmentRequest appointmentRequest();

    /**
     * @return the initial monthly schedule.
     */
    List<AppointmentInfo> getSchedule();

    /**
     * Marks an appointment slot as taken.
     */
    void postSchedule(AppointmentInfoRequest appointmentInfoRequest);
}

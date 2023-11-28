package scheduler;

import models.AppointmentInfo;

import java.util.List;

/**
 * It occurred to me that it's not unlikely that the method of
 * scheduling appointments is something that could be changed in the future,
 * so to facilitate that possibility, I am using this interface.
 */
public interface AppointmentScheduler {
    List<AppointmentInfo> scheduleAppointments();
}

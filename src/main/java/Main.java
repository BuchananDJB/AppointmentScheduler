import models.AppointmentInfo;
import scheduler.AppointmentScheduler;
import scheduler.DefaultAppointmentScheduler;
import service.ApiService;
import service.SchedulingApiService;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        AppointmentScheduler appointmentScheduler = getAppointmentScheduler();
        List<AppointmentInfo> finalSchedule = appointmentScheduler.scheduleAppointments();
    }

    private static AppointmentScheduler getAppointmentScheduler() {
        // In the hypothetical scenario where the way appointments are scheduled is chosen
        // via some setting selected by the user, I would check that setting and then
        // return the correct scheduler accordingly.
        ApiService apiService = getApiService();
        return new DefaultAppointmentScheduler(apiService);
    }

    private static ApiService getApiService() {
        // Again, in the hypothetical scenario where the ApiService used is chosen by a
        // user-selected setting, or if the need to connect to a test API or some similar
        // scenario arises, this is where I would return the appropriate ApiService.
        return new SchedulingApiService();
    }
}

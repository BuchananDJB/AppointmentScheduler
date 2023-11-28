package scheduler;

import models.AppointmentInfo;
import models.AppointmentInfoRequest;
import models.AppointmentRequest;
import models.Doctor;
import service.ApiService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DefaultAppointmentScheduler implements AppointmentScheduler {

    private final ApiService apiService;
    private Map<Doctor, List<AppointmentInfo>> appointmentsByDoctor;

    public DefaultAppointmentScheduler(ApiService apiService) {
        this.apiService = apiService;
        this.appointmentsByDoctor = new HashMap<>();
    }

    @Override
    public List<AppointmentInfo> scheduleAppointments() {
        apiService.start();

        List<AppointmentInfo> initialAppointments = apiService.getSchedule();
        appointmentsByDoctor = filterAppointmentsByDoctor(initialAppointments);

        AppointmentRequest appointmentRequest = apiService.appointmentRequest();
        while (appointmentRequest != null) {
            scheduleAppointment(appointmentRequest);
            appointmentRequest = apiService.appointmentRequest();
        }

        return apiService.stop();
    }

    // I chose to use a map here because, even though this assignment may have a specific, finite number of doctors,
    // in a real situation that may not be the case, so I would need to account for all available doctors
    private Map<Doctor, List<AppointmentInfo>> filterAppointmentsByDoctor(List<AppointmentInfo> initialAppointments) {
        Map<Doctor, List<AppointmentInfo>> appointmentsByDoctor = new HashMap<>();
        if (initialAppointments == null) {
            return appointmentsByDoctor;
        }

        for (Doctor doctor : Doctor.values()) {
            List<AppointmentInfo> appointments = initialAppointments.stream()
                    .filter(appointment -> appointment.getDoctor().equals(doctor))
                    .toList();
            appointmentsByDoctor.putIfAbsent(doctor, appointments);
        }

        return appointmentsByDoctor;
    }

    private void scheduleAppointment(AppointmentRequest appointmentRequest) {
        // Prioritize preferred doctors
        for (int doctorId : appointmentRequest.getPreferredDocs()) {
            if (attemptToScheduleAppointment(appointmentRequest, doctorId))
                return;
        }

        // If unable to schedule with preferred doctors
        for (Doctor doctor : Doctor.values()) {
            // Skip doctors we already attempted to schedule with
            if (preferredDocsContains(appointmentRequest.getPreferredDocs(), doctor.getDoctorId()))
                continue;

            if (attemptToScheduleAppointment(appointmentRequest, doctor.getDoctorId()))
                return;
        }
    }

    private boolean attemptToScheduleAppointment(AppointmentRequest appointmentRequest, int doctorId) {
        try {
            Doctor doctor = Doctor.getDoctorById(doctorId);
            boolean successfullyFoundTimeSlot = findAvailableTimeSlot(appointmentRequest, doctor);
            if (successfullyFoundTimeSlot)
                return true;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    private boolean preferredDocsContains(int[] preferredDocs, int doctorId) {
        for (int id : preferredDocs) {
            if (id == doctorId)
                return true;
        }
        return false;
    }

    private boolean findAvailableTimeSlot(AppointmentRequest appointmentRequest, Doctor doctor) {
        List<AppointmentInfo> appointmentsForPatient = filterAppointmentsByPatient(appointmentRequest);
        Set<LocalDate> patientAvailabilities = findPatientAvailabilities(appointmentsForPatient);
        List<AppointmentInfo> appointmentsForDoctor = appointmentsByDoctor.getOrDefault(doctor, Collections.emptyList());

        for (LocalDate localDate : patientAvailabilities) {
            List<Date> doctorAvailabilitiesForDate = findDoctorAvailabilitiesForDate(appointmentsForDoctor, localDate);
            if (doctorAvailabilitiesForDate.isEmpty())
                continue;

            if (appointmentRequest.isNew()) {
                Date appointmentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Calendar calendar = Calendar.getInstance();
                for (int hour = 15; hour <= 16; ++hour) {
                    calendar.setTime(appointmentDate);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    Date tempDate = calendar.getTime();

                    if (doctorAvailabilitiesForDate.contains(tempDate)) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZZZ");
                        String dateString = dateFormat.format(tempDate);
                        AppointmentInfoRequest appointmentInfoRequest =
                                new AppointmentInfoRequest(
                                        doctor.getDoctorId(),
                                        appointmentRequest.getPersonId(),
                                        dateString,
                                        appointmentRequest.isNew(),
                                        appointmentRequest.getRequestId());
                        apiService.postSchedule(appointmentInfoRequest);
                        return true;
                    }
                }
            }

            Date appointmentDate = doctorAvailabilitiesForDate.get(0);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZZZ");
            String dateString = dateFormat.format(appointmentDate);
            AppointmentInfoRequest appointmentInfoRequest =
                    new AppointmentInfoRequest(
                            doctor.getDoctorId(),
                            appointmentRequest.getPersonId(),
                            dateString,
                            appointmentRequest.isNew(),
                            appointmentRequest.getRequestId());
            apiService.postSchedule(appointmentInfoRequest);
            return true;
        }

        return false;
    }

    private List<AppointmentInfo> filterAppointmentsByPatient(AppointmentRequest appointmentRequest) {
        return appointmentsByDoctor.values().stream()
                .flatMap(Collection::stream)
                .filter(appointment -> appointment.getPersonId() == appointmentRequest.getPersonId())
                .toList();
    }

    private Set<LocalDate> findPatientAvailabilities(List<AppointmentInfo> patientAppointments) {
        Set<LocalDate> patientAvailabilities = new HashSet<>();
        Set<LocalDate> patientUnavailabilities = findPatientUnavailabilities(patientAppointments);

        LocalDate earliestDate = LocalDate.of(2021, 11, 1);
        LocalDate latestDate = LocalDate.of(2022, 1, 1);
        LocalDate dateIterator = earliestDate;
        while (dateIterator.isBefore(latestDate)) {
            if (dateIterator.getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
                dateIterator.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                dateIterator = dateIterator.plusDays(1);
                continue;
            }

            if (!patientUnavailabilities.contains(dateIterator))
                patientAvailabilities.add(dateIterator);

            dateIterator = dateIterator.plusDays(1);
        }

        return patientAvailabilities;
    }

    private Set<LocalDate> findPatientUnavailabilities(List<AppointmentInfo> patientAppointments) {
        Set<LocalDate> patientUnavailabilities = new HashSet<>();
        for (AppointmentInfo appointmentInfo : patientAppointments) {
            for (int days = -7; days <= 7; ++days) {
                LocalDate unavailableDate = appointmentInfo.getAppointmentTimeAsLocalDate().plusDays(days);
                patientUnavailabilities.add(unavailableDate);
            }
        }
        return patientUnavailabilities;
    }

    private List<Date> findDoctorAvailabilitiesForDate(List<AppointmentInfo> doctorAppointments, LocalDate localDate) {
        List<AppointmentInfo> doctorAppointmentsForDate = doctorAppointments.stream()
                .filter(appointment -> appointment.getAppointmentTimeAsLocalDate().equals(localDate))
                .toList();
        List<Date> doctorAvailabilities = new ArrayList<>();

        Date appointmentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();

        for (int hour = 8; hour <= 16; ++hour) {
            calendar.setTime(appointmentDate);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            Date tempDate = calendar.getTime();

            if (!doctorHasAppointmentAtTime(doctorAppointmentsForDate, tempDate))
                doctorAvailabilities.add(tempDate);
        }

        return doctorAvailabilities;
    }


    private boolean doctorHasAppointmentAtTime(List<AppointmentInfo> doctorAppointments, Date appointmentTime) {
        for (AppointmentInfo appointmentInfo : doctorAppointments) {
            if (appointmentInfo.getAppointmentTimeAsDate().equals(appointmentTime))
                return true;
        }
        return false;
    }

}

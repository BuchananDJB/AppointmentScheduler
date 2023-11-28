package models;

public enum Doctor {
    DR_ZERO(0),
    DR_ONE(1),
    DR_TWO(2),
    DR_THREE(3);

    private final int doctorId;

    Doctor(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public static Doctor getDoctorById(int doctorId) {
        for (Doctor doctor : Doctor.values()) {
            if (doctor.doctorId == doctorId)
                return doctor;
        }
        throw new IllegalArgumentException("No doctor found with that ID.");
    }
}

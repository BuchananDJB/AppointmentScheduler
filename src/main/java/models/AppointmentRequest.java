package models;

public class AppointmentRequest {

    private final int requestId;
    private final int personId;
    private final String[] preferredDays;
    private final int[] preferredDocs;
    private final boolean isNew;

    public AppointmentRequest(int requestId, int personId, String[] preferredDays, int[] preferredDocs, boolean isNew) {
        this.requestId = requestId;
        this.personId = personId;
        this.preferredDays = preferredDays;
        this.preferredDocs = preferredDocs;
        this.isNew = isNew;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getPersonId() {
        return personId;
    }

    public String[] getPreferredDays() {
        return preferredDays;
    }

    public int[] getPreferredDocs() {
        return preferredDocs;
    }

    public boolean isNew() {
        return isNew;
    }
}

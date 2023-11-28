package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.AppointmentInfo;
import models.AppointmentInfoRequest;
import models.AppointmentRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class SchedulingApiService implements ApiService {

    // It would be more secure to not hard-code this and instead, depending on how the program is hosted,
    // store it in something like a kubernetes secret or some similar method of securely storing and accessing this token
    // For the purposes of this assignment, I will simply keep it here.
    private static final UUID AUTH_TOKEN = UUID.fromString("7a6a06e0-e6bc-40f5-a612-892d85659109");
    private final String schedulingURL = "http://scheduling-interview-2021-265534043.us-west-2.elb.amazonaws.com/api/Scheduling/";

    @Override
    public void start() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(schedulingURL + "Start?token=" + AUTH_TOKEN))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AppointmentInfo> stop() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(schedulingURL + "Stop?token=" + AUTH_TOKEN))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            return new Gson().fromJson(response.body(), new TypeToken<List<AppointmentInfo>>(){}.getType());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AppointmentRequest appointmentRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(schedulingURL + "AppointmentRequest?token=" + AUTH_TOKEN))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            return new Gson().fromJson(response.body(), AppointmentRequest.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AppointmentInfo> getSchedule() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(schedulingURL + "Schedule?token=" + AUTH_TOKEN))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            return new Gson().fromJson(response.body(), new TypeToken<List<AppointmentInfo>>(){}.getType());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void postSchedule(AppointmentInfoRequest appointmentInfoRequest) {
        String appointmentInfoRequestString = new Gson().toJson(appointmentInfoRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(schedulingURL + "Schedule?token=" + AUTH_TOKEN))
                .header("accept", "*/*")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(appointmentInfoRequestString))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

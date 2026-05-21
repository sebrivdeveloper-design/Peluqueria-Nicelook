package co.edu.univalle.NiceLook.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "NiceLook";

    public List<Event> obtenerEventos(String accessToken)
            throws Exception {

        GoogleCredential credential =
                new GoogleCredential()
                        .setAccessToken(accessToken);

        Calendar service =
                new Calendar.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential
                )
                .setApplicationName(APPLICATION_NAME)
                .build();

        Events events = service.events()
                .list("primary")
                .execute();

        return events.getItems();
    }
}
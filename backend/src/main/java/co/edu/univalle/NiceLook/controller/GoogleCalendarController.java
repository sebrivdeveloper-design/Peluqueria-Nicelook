package co.edu.univalle.NiceLook.controller;

import com.google.api.services.calendar.model.Event;

import co.edu.univalle.NiceLook.service.GoogleCalendarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google/calendar")
@CrossOrigin("*")
public class GoogleCalendarController {

    @Autowired
    private GoogleCalendarService service;

    @GetMapping("/events")
    public List<Event> obtenerEventos(
            @RequestHeader("Authorization") String token
    ) throws Exception {

        token = token.replace("Bearer ", "");

        return service.obtenerEventos(token);
    }
}
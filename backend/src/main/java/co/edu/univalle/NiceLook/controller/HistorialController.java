package co.edu.univalle.NiceLook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import co.edu.univalle.NiceLook.DTO.HistorialCitaDTO;
import co.edu.univalle.NiceLook.service.HistorialService;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin("*")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @GetMapping("/{idCliente}")
    public List<HistorialCitaDTO> obtenerHistorial(
            @PathVariable Integer idCliente
    ) {

        return historialService.obtenerHistorial(idCliente);
    }
}
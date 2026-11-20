package co.edu.sena.finanzas.api.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.finanzas.api.dto.ResumenResponse;
import co.edu.sena.finanzas.api.servicio.ResumenServicio;

/** Servicio de solo lectura con el resumen general de las finanzas. */
@RestController
@RequestMapping("/api/resumen")
public class ResumenControlador {

    private final ResumenServicio servicio;

    public ResumenControlador(ResumenServicio servicio) {
        this.servicio = servicio;
    }

    /** GET /api/resumen: totales generales y por categoria y cuenta. Responde 200. */
    @GetMapping
    public ResumenResponse resumen() {
        return servicio.obtener();
    }
}

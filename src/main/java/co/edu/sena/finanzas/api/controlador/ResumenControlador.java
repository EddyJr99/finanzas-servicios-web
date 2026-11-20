package co.edu.sena.finanzas.api.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.finanzas.api.dto.ResumenResponse;
import co.edu.sena.finanzas.api.servicio.ResumenServicio;

/** Servicio de solo lectura con el resumen de las finanzas (lo usa el dashboard). */
@RestController
@RequestMapping("/api/resumen")
public class ResumenControlador {

    private final ResumenServicio servicio;

    public ResumenControlador(ResumenServicio servicio) {
        this.servicio = servicio;
    }

    /**
     * GET /api/resumen: ingresos, gastos, balance y totales por categoria y cuenta.
     * Con ?idUsuario=N el resumen es solo de ese usuario. Responde 200.
     */
    @GetMapping
    public ResumenResponse resumen(@RequestParam(required = false) Integer idUsuario) {
        return servicio.obtener(idUsuario);
    }
}

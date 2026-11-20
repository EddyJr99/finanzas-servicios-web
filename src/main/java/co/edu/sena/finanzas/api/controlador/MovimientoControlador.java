package co.edu.sena.finanzas.api.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.finanzas.api.dto.MovimientoPatchRequest;
import co.edu.sena.finanzas.api.dto.MovimientoRequest;
import co.edu.sena.finanzas.api.dto.MovimientoResponse;
import co.edu.sena.finanzas.api.servicio.MovimientoServicio;
import jakarta.validation.Valid;

/**
 * Controlador REST de los movimientos: recibe las solicitudes HTTP en /api/movimientos,
 * valida los datos y delega el trabajo en el servicio.
 *
 * Verbos HTTP usados:
 * GET (consultar), POST (crear), PUT (reemplazar), PATCH (modificar parcialmente)
 * y DELETE (eliminar).
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoControlador {

    private final MovimientoServicio servicio;

    public MovimientoControlador(MovimientoServicio servicio) {
        this.servicio = servicio;
    }

    /**
     * GET /api/movimientos: lista los movimientos. Con ?idUsuario=N devuelve solo los de
     * ese usuario (lo usa el dashboard). Responde 200.
     */
    @GetMapping
    public List<MovimientoResponse> listar(@RequestParam(required = false) Integer idUsuario) {
        return servicio.listar(idUsuario);
    }

    /** GET /api/movimientos/{id}: consulta un movimiento. Responde 200, o 404 si no existe. */
    @GetMapping("/{id}")
    public MovimientoResponse obtener(@PathVariable int id) {
        return servicio.obtener(id);
    }

    /**
     * POST /api/movimientos: crea un movimiento. Responde 201 con la cabecera Location
     * apuntando al recurso nuevo, o 400 si los datos no son validos.
     */
    @PostMapping
    public ResponseEntity<MovimientoResponse> crear(@Valid @RequestBody MovimientoRequest datos) {
        MovimientoResponse creado = servicio.crear(datos);
        URI ubicacion = URI.create("/api/movimientos/" + creado.idMovimiento());
        return ResponseEntity.created(ubicacion).body(creado);
    }

    /** PUT /api/movimientos/{id}: reemplaza todo el movimiento. Responde 200, 400 o 404. */
    @PutMapping("/{id}")
    public MovimientoResponse reemplazar(@PathVariable int id,
            @Valid @RequestBody MovimientoRequest datos) {
        return servicio.reemplazar(id, datos);
    }

    /** PATCH /api/movimientos/{id}: cambia solo los campos enviados. Responde 200, 400 o 404. */
    @PatchMapping("/{id}")
    public MovimientoResponse modificarParcialmente(@PathVariable int id,
            @Valid @RequestBody MovimientoPatchRequest cambios) {
        return servicio.modificarParcialmente(id, cambios);
    }

    /** DELETE /api/movimientos/{id}: elimina el movimiento. Responde 204 sin cuerpo, o 404. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

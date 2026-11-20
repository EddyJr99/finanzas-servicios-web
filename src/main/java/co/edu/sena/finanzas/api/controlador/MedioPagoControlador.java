package co.edu.sena.finanzas.api.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.finanzas.api.dto.MedioPagoRequest;
import co.edu.sena.finanzas.api.dto.MedioPagoResponse;
import co.edu.sena.finanzas.api.servicio.MedioPagoServicio;
import jakarta.validation.Valid;

/**
 * Controlador REST de los medios de pago: recibe las solicitudes HTTP en /api/medios-pago,
 * valida los datos y delega el trabajo en el servicio.
 *
 * GET (listar y consultar), POST (crear), PUT (reemplazar) y DELETE (eliminar).
 */
@RestController
@RequestMapping("/api/medios-pago")
public class MedioPagoControlador {

    private final MedioPagoServicio servicio;

    public MedioPagoControlador(MedioPagoServicio servicio) {
        this.servicio = servicio;
    }

    /** GET /api/medios-pago: lista todos. Responde 200. */
    @GetMapping
    public List<MedioPagoResponse> listar() {
        return servicio.listar();
    }

    /** GET /api/medios-pago/{id}: consulta uno. Responde 200, o 404 si no existe. */
    @GetMapping("/{id}")
    public MedioPagoResponse obtener(@PathVariable int id) {
        return servicio.obtener(id);
    }

    /** POST /api/medios-pago: crea uno. Responde 201 con la cabecera Location, 400 (datos) o 409 (conflicto). */
    @PostMapping
    public ResponseEntity<MedioPagoResponse> crear(@Valid @RequestBody MedioPagoRequest datos) {
        MedioPagoResponse creado = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/medios-pago/" + creado.idMedioPago())).body(creado);
    }

    /** PUT /api/medios-pago/{id}: reemplaza todo el registro. Responde 200, 400, 404 o 409. */
    @PutMapping("/{id}")
    public MedioPagoResponse reemplazar(@PathVariable int id, @Valid @RequestBody MedioPagoRequest datos) {
        return servicio.reemplazar(id, datos);
    }

    /** DELETE /api/medios-pago/{id}: elimina. Responde 204 sin cuerpo, 404 o 409 si esta en uso. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

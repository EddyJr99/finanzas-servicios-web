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

import co.edu.sena.finanzas.api.dto.CuentaRequest;
import co.edu.sena.finanzas.api.dto.CuentaResponse;
import co.edu.sena.finanzas.api.servicio.CuentaServicio;
import jakarta.validation.Valid;

/**
 * Controlador REST de las cuentas: recibe las solicitudes HTTP en /api/cuentas,
 * valida los datos y delega el trabajo en el servicio.
 *
 * GET (listar y consultar), POST (crear), PUT (reemplazar) y DELETE (eliminar).
 */
@RestController
@RequestMapping("/api/cuentas")
public class CuentaControlador {

    private final CuentaServicio servicio;

    public CuentaControlador(CuentaServicio servicio) {
        this.servicio = servicio;
    }

    /** GET /api/cuentas: lista todos. Responde 200. */
    @GetMapping
    public List<CuentaResponse> listar() {
        return servicio.listar();
    }

    /** GET /api/cuentas/{id}: consulta uno. Responde 200, o 404 si no existe. */
    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable int id) {
        return servicio.obtener(id);
    }

    /** POST /api/cuentas: crea uno. Responde 201 con la cabecera Location, 400 (datos) o 409 (conflicto). */
    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest datos) {
        CuentaResponse creado = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/cuentas/" + creado.idCuenta())).body(creado);
    }

    /** PUT /api/cuentas/{id}: reemplaza todo el registro. Responde 200, 400, 404 o 409. */
    @PutMapping("/{id}")
    public CuentaResponse reemplazar(@PathVariable int id, @Valid @RequestBody CuentaRequest datos) {
        return servicio.reemplazar(id, datos);
    }

    /** DELETE /api/cuentas/{id}: elimina. Responde 204 sin cuerpo, 404 o 409 si esta en uso. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

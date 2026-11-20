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

import co.edu.sena.finanzas.api.dto.UsuarioRequest;
import co.edu.sena.finanzas.api.dto.UsuarioResponse;
import co.edu.sena.finanzas.api.servicio.UsuarioServicio;
import jakarta.validation.Valid;

/**
 * Controlador REST de los usuarios: recibe las solicitudes HTTP en /api/usuarios,
 * valida los datos y delega el trabajo en el servicio.
 *
 * GET (listar y consultar), POST (crear), PUT (reemplazar) y DELETE (eliminar).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio servicio;

    public UsuarioControlador(UsuarioServicio servicio) {
        this.servicio = servicio;
    }

    /** GET /api/usuarios: lista todos. Responde 200. */
    @GetMapping
    public List<UsuarioResponse> listar() {
        return servicio.listar();
    }

    /** GET /api/usuarios/{id}: consulta uno. Responde 200, o 404 si no existe. */
    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable int id) {
        return servicio.obtener(id);
    }

    /** POST /api/usuarios: crea uno. Responde 201 con la cabecera Location, 400 (datos) o 409 (conflicto). */
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest datos) {
        UsuarioResponse creado = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.idUsuario())).body(creado);
    }

    /** PUT /api/usuarios/{id}: reemplaza todo el registro. Responde 200, 400, 404 o 409. */
    @PutMapping("/{id}")
    public UsuarioResponse reemplazar(@PathVariable int id, @Valid @RequestBody UsuarioRequest datos) {
        return servicio.reemplazar(id, datos);
    }

    /** DELETE /api/usuarios/{id}: elimina. Responde 204 sin cuerpo, 404 o 409 si esta en uso. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

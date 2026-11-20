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

import co.edu.sena.finanzas.api.dto.CategoriaRequest;
import co.edu.sena.finanzas.api.dto.CategoriaResponse;
import co.edu.sena.finanzas.api.servicio.CategoriaServicio;
import jakarta.validation.Valid;

/**
 * Controlador REST de las categorias: recibe las solicitudes HTTP en /api/categorias,
 * valida los datos y delega el trabajo en el servicio.
 *
 * GET (listar y consultar), POST (crear), PUT (reemplazar) y DELETE (eliminar).
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaControlador {

    private final CategoriaServicio servicio;

    public CategoriaControlador(CategoriaServicio servicio) {
        this.servicio = servicio;
    }

    /** GET /api/categorias: lista todos. Responde 200. */
    @GetMapping
    public List<CategoriaResponse> listar() {
        return servicio.listar();
    }

    /** GET /api/categorias/{id}: consulta uno. Responde 200, o 404 si no existe. */
    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable int id) {
        return servicio.obtener(id);
    }

    /** POST /api/categorias: crea uno. Responde 201 con la cabecera Location, 400 (datos) o 409 (conflicto). */
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest datos) {
        CategoriaResponse creado = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/categorias/" + creado.idCategoria())).body(creado);
    }

    /** PUT /api/categorias/{id}: reemplaza todo el registro. Responde 200, 400, 404 o 409. */
    @PutMapping("/{id}")
    public CategoriaResponse reemplazar(@PathVariable int id, @Valid @RequestBody CategoriaRequest datos) {
        return servicio.reemplazar(id, datos);
    }

    /** DELETE /api/categorias/{id}: elimina. Responde 204 sin cuerpo, 404 o 409 si esta en uso. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

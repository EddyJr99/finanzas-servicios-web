package co.edu.sena.finanzas.api.dto;

import java.time.LocalDate;

/** Usuario tal como lo devuelve la API. */
public record UsuarioResponse(Integer idUsuario, String nombre, String correo,
        LocalDate fechaRegistro) {
}

package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos del formulario de inicio de sesion.
 *
 * @param correo correo del usuario
 * @param clave  contrasena del usuario
 */
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        String correo,

        @NotBlank(message = "La contrasena es obligatoria")
        String clave) {
}

package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que envia el cliente para crear o reemplazar un usuario.
 *
 * @param nombre nombre completo (maximo 100 caracteres)
 * @param correo correo electronico valido y unico
 */
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite maximo 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 100, message = "El correo admite maximo 100 caracteres")
        String correo) {
}

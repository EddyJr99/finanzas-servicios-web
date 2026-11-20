package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de registro (crear cuenta en el sistema).
 *
 * @param nombre nombre completo de la persona
 * @param correo correo con el que va a iniciar sesion
 * @param clave  contrasena; se guarda cifrada, nunca en texto plano
 */
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite maximo 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 100, message = "El correo admite maximo 100 caracteres")
        String correo,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String clave) {
}

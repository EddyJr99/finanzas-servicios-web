package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o reemplazar una cuenta.
 *
 * @param nombreCuenta nombre de la cuenta (por ejemplo, "Ahorros BBVA")
 * @param tipoCuenta   tipo de cuenta (Ahorros, Corriente, Efectivo...)
 * @param banco        entidad donde esta la cuenta
 * @param idUsuario    id del usuario dueño de la cuenta (debe existir)
 */
public record CuentaRequest(
        @NotBlank(message = "El nombre de la cuenta es obligatorio")
        @Size(max = 100, message = "El nombre de la cuenta admite maximo 100 caracteres")
        String nombreCuenta,

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        @Size(max = 50, message = "El tipo de cuenta admite maximo 50 caracteres")
        String tipoCuenta,

        @NotBlank(message = "El banco es obligatorio")
        @Size(max = 100, message = "El banco admite maximo 100 caracteres")
        String banco,

        @NotNull(message = "El id del usuario es obligatorio")
        @Positive(message = "El id del usuario debe ser mayor que 0")
        Integer idUsuario) {
}

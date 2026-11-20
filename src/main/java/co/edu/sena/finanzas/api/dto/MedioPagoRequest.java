package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o reemplazar un medio de pago.
 *
 * @param nombreMedioPago   por ejemplo "Tarjeta Debito"
 * @param entidadFinanciera entidad que lo emite
 */
public record MedioPagoRequest(
        @NotBlank(message = "El nombre del medio de pago es obligatorio")
        @Size(max = 100, message = "El nombre del medio de pago admite maximo 100 caracteres")
        String nombreMedioPago,

        @NotBlank(message = "La entidad financiera es obligatoria")
        @Size(max = 100, message = "La entidad financiera admite maximo 100 caracteres")
        String entidadFinanciera) {
}

package co.edu.sena.finanzas.api.dto;

/** Medio de pago tal como lo devuelve la API. */
public record MedioPagoResponse(Integer idMedioPago, String nombreMedioPago,
        String entidadFinanciera) {
}

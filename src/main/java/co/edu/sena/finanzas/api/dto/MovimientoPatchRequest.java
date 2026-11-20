package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo JSON para modificar parcialmente un movimiento (PATCH).
 *
 * Todos los campos son opcionales: solo se actualizan los que vienen en la
 * solicitud (distintos de null); los demas conservan su valor actual.
 */
public record MovimientoPatchRequest(
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        @Size(min = 1, max = 150, message = "La descripcion debe tener entre 1 y 150 caracteres")
        String descripcion,

        @DecimalMin(value = "0.01", message = "El monto total debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2,
                message = "El monto total admite maximo 10 enteros y 2 decimales")
        BigDecimal montoTotal,

        @Positive(message = "El id de la cuenta debe ser mayor que 0")
        Integer idCuenta,

        @Positive(message = "El id del medio de pago debe ser mayor que 0")
        Integer idMedioPago,

        @Size(min = 1, message = "Si envia categorias debe indicar al menos una")
        @Valid
        List<CategoriaAsignadaRequest> categorias) {
}

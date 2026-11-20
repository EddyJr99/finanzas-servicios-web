package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Parte de un movimiento asignada a una categoria (tabla detalle_categoria).
 *
 * @param idCategoria   id de una categoria existente
 * @param montoAsignado monto del movimiento que corresponde a esa categoria
 */
public record CategoriaAsignadaRequest(
        @NotNull(message = "El id de la categoria es obligatorio")
        @Positive(message = "El id de la categoria debe ser mayor que 0")
        Integer idCategoria,

        @NotNull(message = "El monto asignado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto asignado debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2,
                message = "El monto asignado admite maximo 10 enteros y 2 decimales")
        BigDecimal montoAsignado) {
}

package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo JSON para crear (POST) o reemplazar (PUT) un movimiento.
 * Todos los campos son obligatorios.
 *
 * @param fecha         fecha del movimiento (AAAA-MM-DD), no puede ser futura
 * @param descripcion   descripcion de hasta 150 caracteres
 * @param montoTotal    monto total, mayor que 0 y con maximo 2 decimales
 * @param idCuenta      id de una cuenta existente
 * @param idMedioPago   id de un medio de pago existente
 * @param categorias    categorias entre las que se reparte el monto (minimo una)
 */
public record MovimientoRequest(
        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 150, message = "La descripcion admite maximo 150 caracteres")
        String descripcion,

        @NotNull(message = "El monto total es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto total debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2,
                message = "El monto total admite maximo 10 enteros y 2 decimales")
        BigDecimal montoTotal,

        @NotNull(message = "El id de la cuenta es obligatorio")
        @Positive(message = "El id de la cuenta debe ser mayor que 0")
        Integer idCuenta,

        @NotNull(message = "El id del medio de pago es obligatorio")
        @Positive(message = "El id del medio de pago debe ser mayor que 0")
        Integer idMedioPago,

        @NotEmpty(message = "Debe indicar al menos una categoria")
        @Valid
        List<CategoriaAsignadaRequest> categorias) {
}

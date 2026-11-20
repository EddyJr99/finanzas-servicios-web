package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;

/** Categoria de un movimiento, con el monto que le corresponde. */
public record CategoriaAsignadaResponse(
        Integer idCategoria,
        String nombreCategoria,
        BigDecimal montoAsignado) {
}

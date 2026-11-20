package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;

/** Categoria de un movimiento, con su tipo (INGRESO o GASTO) y el monto que le corresponde. */
public record CategoriaAsignadaResponse(
        Integer idCategoria,
        String nombreCategoria,
        String tipo,
        BigDecimal montoAsignado) {
}

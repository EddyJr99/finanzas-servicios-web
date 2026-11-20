package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Movimiento tal como la API lo devuelve en formato JSON. */
public record MovimientoResponse(
        Integer idMovimiento,
        LocalDate fecha,
        String descripcion,
        BigDecimal montoTotal,
        Integer idCuenta,
        String nombreCuenta,
        Integer idMedioPago,
        String nombreMedioPago,
        List<CategoriaAsignadaResponse> categorias) {
}

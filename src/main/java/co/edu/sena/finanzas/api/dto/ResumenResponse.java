package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumen general de las finanzas: cuantos movimientos hay, cuanto suman y como
 * se distribuyen por categoria y por cuenta.
 */
public record ResumenResponse(int totalMovimientos, BigDecimal montoTotal,
        List<TotalPorCategoria> porCategoria, List<TotalPorCuenta> porCuenta) {

    /** Suma de los montos asignados a una categoria. */
    public record TotalPorCategoria(Integer idCategoria, String nombreCategoria,
            BigDecimal total) {
    }

    /** Cantidad de movimientos y suma de montos de una cuenta. */
    public record TotalPorCuenta(Integer idCuenta, String nombreCuenta,
            int cantidadMovimientos, BigDecimal total) {
    }
}

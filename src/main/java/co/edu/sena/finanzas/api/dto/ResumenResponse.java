package co.edu.sena.finanzas.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumen de las finanzas: cuantos movimientos hay, cuanto suman los ingresos y los
 * gastos, el balance (ingresos - gastos) y como se distribuyen por categoria y cuenta.
 */
public record ResumenResponse(int totalMovimientos, BigDecimal totalIngresos,
        BigDecimal totalGastos, BigDecimal balance,
        List<TotalPorCategoria> porCategoria, List<TotalPorCuenta> porCuenta) {

    /** Suma de los montos asignados a una categoria. */
    public record TotalPorCategoria(Integer idCategoria, String nombreCategoria, String tipo,
            BigDecimal total) {
    }

    /** Cantidad de movimientos y suma de montos de una cuenta. */
    public record TotalPorCuenta(Integer idCuenta, String nombreCuenta,
            int cantidadMovimientos, BigDecimal total) {
    }
}

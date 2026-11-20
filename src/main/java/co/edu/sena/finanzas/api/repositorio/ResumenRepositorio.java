package co.edu.sena.finanzas.api.repositorio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.ResumenResponse.TotalPorCategoria;
import co.edu.sena.finanzas.api.dto.ResumenResponse.TotalPorCuenta;

/** Consultas de agregacion (sumas y conteos) para el resumen de las finanzas. */
@Repository
public class ResumenRepositorio extends RepositorioBase {

    public ResumenRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public int contarMovimientos() {
        return contar("SELECT COUNT(*) FROM movimientos");
    }

    public BigDecimal sumarMovimientos() {
        BigDecimal suma = jdbc.queryForObject(
                "SELECT COALESCE(SUM(monto_total), 0) FROM movimientos", BigDecimal.class);
        return suma == null ? BigDecimal.ZERO : suma;
    }

    public List<TotalPorCategoria> totalesPorCategoria() {
        return jdbc.query(
                "SELECT ca.id_categoria, ca.nombre_categoria, SUM(dc.monto_asignado) AS total "
                        + "FROM detalle_categoria dc "
                        + "JOIN categorias ca ON ca.id_categoria = dc.id_categoria "
                        + "GROUP BY ca.id_categoria, ca.nombre_categoria "
                        + "ORDER BY total DESC",
                (rs, fila) -> new TotalPorCategoria(rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"), rs.getBigDecimal("total")));
    }

    public List<TotalPorCuenta> totalesPorCuenta() {
        return jdbc.query(
                "SELECT c.id_cuenta, c.nombre_cuenta, COUNT(m.id_movimiento) AS cantidad, "
                        + "COALESCE(SUM(m.monto_total), 0) AS total "
                        + "FROM cuentas c LEFT JOIN movimientos m ON m.id_cuenta = c.id_cuenta "
                        + "GROUP BY c.id_cuenta, c.nombre_cuenta ORDER BY c.id_cuenta",
                (rs, fila) -> new TotalPorCuenta(rs.getInt("id_cuenta"),
                        rs.getString("nombre_cuenta"), rs.getInt("cantidad"),
                        rs.getBigDecimal("total")));
    }
}

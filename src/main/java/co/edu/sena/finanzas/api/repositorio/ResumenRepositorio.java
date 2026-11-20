package co.edu.sena.finanzas.api.repositorio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.ResumenResponse.TotalPorCategoria;
import co.edu.sena.finanzas.api.dto.ResumenResponse.TotalPorCuenta;

/**
 * Consultas de agregacion (sumas y conteos) para el resumen de las finanzas.
 * Todas aceptan un idUsuario opcional: si viene, solo se cuentan las cuentas de ese usuario.
 */
@Repository
public class ResumenRepositorio extends RepositorioBase {

    public ResumenRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    /** Condicion SQL para filtrar por usuario (o ninguna) y sus parametros. */
    private static String filtro(Integer idUsuario, String alias) {
        return idUsuario == null ? "" : " AND " + alias + ".id_usuario = ? ";
    }

    private static Object[] parametros(Integer idUsuario) {
        return idUsuario == null ? new Object[0] : new Object[] {idUsuario};
    }

    public int contarMovimientos(Integer idUsuario) {
        return contar("SELECT COUNT(*) FROM movimientos m JOIN cuentas c ON c.id_cuenta = m.id_cuenta "
                + "WHERE 1 = 1" + filtro(idUsuario, "c"), parametros(idUsuario));
    }

    /** Suma lo asignado a las categorias del tipo indicado (INGRESO o GASTO). */
    public BigDecimal sumarPorTipo(String tipo, Integer idUsuario) {
        Object[] base = parametros(idUsuario);
        Object[] todos = new Object[base.length + 1];
        todos[0] = tipo;
        System.arraycopy(base, 0, todos, 1, base.length);
        BigDecimal suma = jdbc.queryForObject(
                "SELECT COALESCE(SUM(dc.monto_asignado), 0) FROM detalle_categoria dc "
                        + "JOIN categorias ca ON ca.id_categoria = dc.id_categoria "
                        + "JOIN movimientos m ON m.id_movimiento = dc.id_movimiento "
                        + "JOIN cuentas c ON c.id_cuenta = m.id_cuenta "
                        + "WHERE ca.tipo = ?" + filtro(idUsuario, "c"),
                BigDecimal.class, todos);
        return suma == null ? BigDecimal.ZERO : suma;
    }

    public List<TotalPorCategoria> totalesPorCategoria(Integer idUsuario) {
        return jdbc.query(
                "SELECT ca.id_categoria, ca.nombre_categoria, ca.tipo, SUM(dc.monto_asignado) AS total "
                        + "FROM detalle_categoria dc "
                        + "JOIN categorias ca ON ca.id_categoria = dc.id_categoria "
                        + "JOIN movimientos m ON m.id_movimiento = dc.id_movimiento "
                        + "JOIN cuentas c ON c.id_cuenta = m.id_cuenta "
                        + "WHERE 1 = 1" + filtro(idUsuario, "c")
                        + " GROUP BY ca.id_categoria, ca.nombre_categoria, ca.tipo "
                        + "ORDER BY total DESC",
                (rs, fila) -> new TotalPorCategoria(rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"), rs.getString("tipo"),
                        rs.getBigDecimal("total")),
                parametros(idUsuario));
    }

    public List<TotalPorCuenta> totalesPorCuenta(Integer idUsuario) {
        return jdbc.query(
                "SELECT c.id_cuenta, c.nombre_cuenta, COUNT(m.id_movimiento) AS cantidad, "
                        + "COALESCE(SUM(m.monto_total), 0) AS total "
                        + "FROM cuentas c LEFT JOIN movimientos m ON m.id_cuenta = c.id_cuenta "
                        + "WHERE 1 = 1" + filtro(idUsuario, "c")
                        + " GROUP BY c.id_cuenta, c.nombre_cuenta ORDER BY c.id_cuenta",
                (rs, fila) -> new TotalPorCuenta(rs.getInt("id_cuenta"),
                        rs.getString("nombre_cuenta"), rs.getInt("cantidad"),
                        rs.getBigDecimal("total")),
                parametros(idUsuario));
    }
}

package co.edu.sena.finanzas.api.repositorio;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.CategoriaAsignadaRequest;
import co.edu.sena.finanzas.api.dto.CategoriaAsignadaResponse;
import co.edu.sena.finanzas.api.dto.MovimientoResponse;

/**
 * Acceso a datos de los movimientos. Usa JdbcTemplate de Spring, que abre y cierra
 * las conexiones y convierte las excepciones de JDBC, y sentencias SQL con parametros
 * (?) para evitar la inyeccion de SQL.
 *
 * Cada movimiento esta en la tabla movimientos y el reparto de su monto entre
 * categorias en la tabla detalle_categoria.
 */
@Repository
public class MovimientoRepositorio {

    private static final String SQL_MOVIMIENTOS =
            "SELECT m.id_movimiento, m.fecha, m.descripcion, m.monto_total, "
                    + "m.id_cuenta, c.nombre_cuenta, m.id_medio_pago, mp.nombre_medio_pago "
                    + "FROM movimientos m "
                    + "LEFT JOIN cuentas c ON c.id_cuenta = m.id_cuenta "
                    + "LEFT JOIN medios_pago mp ON mp.id_medio_pago = m.id_medio_pago ";
    private static final String SQL_DETALLES =
            "SELECT dc.id_movimiento, dc.id_categoria, ca.nombre_categoria, dc.monto_asignado "
                    + "FROM detalle_categoria dc "
                    + "LEFT JOIN categorias ca ON ca.id_categoria = dc.id_categoria ";
    private static final String SQL_INSERTAR_MOVIMIENTO =
            "INSERT INTO movimientos (fecha, descripcion, monto_total, id_cuenta, id_medio_pago) "
                    + "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR_MOVIMIENTO =
            "UPDATE movimientos SET fecha = ?, descripcion = ?, monto_total = ?, "
                    + "id_cuenta = ?, id_medio_pago = ? WHERE id_movimiento = ?";
    private static final String SQL_INSERTAR_DETALLE =
            "INSERT INTO detalle_categoria (id_movimiento, id_categoria, monto_asignado) "
                    + "VALUES (?, ?, ?)";
    private static final String SQL_ELIMINAR_DETALLES =
            "DELETE FROM detalle_categoria WHERE id_movimiento = ?";
    private static final String SQL_ELIMINAR_MOVIMIENTO =
            "DELETE FROM movimientos WHERE id_movimiento = ?";

    private final JdbcTemplate jdbc;

    public MovimientoRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Fila de la tabla movimientos (con los nombres de cuenta y medio de pago) sin sus categorias. */
    private record Fila(Integer idMovimiento, LocalDate fecha, String descripcion,
            BigDecimal montoTotal, Integer idCuenta, String nombreCuenta,
            Integer idMedioPago, String nombreMedioPago) {
    }

    /** Devuelve todos los movimientos, los mas recientes primero. */
    public List<MovimientoResponse> listar() {
        List<Fila> filas = jdbc.query(
                SQL_MOVIMIENTOS + "ORDER BY m.fecha DESC, m.id_movimiento DESC",
                (rs, numeroFila) -> new Fila(
                        rs.getInt("id_movimiento"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("monto_total"),
                        rs.getInt("id_cuenta"),
                        rs.getString("nombre_cuenta"),
                        rs.getInt("id_medio_pago"),
                        rs.getString("nombre_medio_pago")));

        // Se consultan todos los detalles de una vez y se agrupan por movimiento.
        Map<Integer, List<CategoriaAsignadaResponse>> detalles = new HashMap<>();
        jdbc.query(SQL_DETALLES + "ORDER BY dc.id_categoria", rs -> {
            detalles.computeIfAbsent(rs.getInt("id_movimiento"), id -> new ArrayList<>())
                    .add(new CategoriaAsignadaResponse(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre_categoria"),
                            rs.getBigDecimal("monto_asignado")));
        });

        List<MovimientoResponse> movimientos = new ArrayList<>();
        for (Fila fila : filas) {
            movimientos.add(armar(fila,
                    detalles.getOrDefault(fila.idMovimiento(), List.of())));
        }
        return movimientos;
    }

    /** Busca un movimiento por id; vacio si no existe. */
    public Optional<MovimientoResponse> buscarPorId(int idMovimiento) {
        List<Fila> filas = jdbc.query(
                SQL_MOVIMIENTOS + "WHERE m.id_movimiento = ?",
                (rs, numeroFila) -> new Fila(
                        rs.getInt("id_movimiento"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("monto_total"),
                        rs.getInt("id_cuenta"),
                        rs.getString("nombre_cuenta"),
                        rs.getInt("id_medio_pago"),
                        rs.getString("nombre_medio_pago")),
                idMovimiento);
        if (filas.isEmpty()) {
            return Optional.empty();
        }
        List<CategoriaAsignadaResponse> categorias = jdbc.query(
                SQL_DETALLES + "WHERE dc.id_movimiento = ? ORDER BY dc.id_categoria",
                (rs, numeroFila) -> new CategoriaAsignadaResponse(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"),
                        rs.getBigDecimal("monto_asignado")),
                idMovimiento);
        return Optional.of(armar(filas.get(0), categorias));
    }

    /** Inserta un movimiento y devuelve el id que genero la base de datos. */
    public int insertar(LocalDate fecha, String descripcion, BigDecimal montoTotal,
            int idCuenta, int idMedioPago) {
        KeyHolder claves = new GeneratedKeyHolder();
        jdbc.update(conexion -> {
            PreparedStatement sentencia = conexion.prepareStatement(
                    SQL_INSERTAR_MOVIMIENTO, Statement.RETURN_GENERATED_KEYS);
            sentencia.setDate(1, Date.valueOf(fecha));
            sentencia.setString(2, descripcion);
            sentencia.setBigDecimal(3, montoTotal);
            sentencia.setInt(4, idCuenta);
            sentencia.setInt(5, idMedioPago);
            return sentencia;
        }, claves);
        Number id = claves.getKey();
        if (id == null) {
            throw new IllegalStateException("La base de datos no devolvio el id generado");
        }
        return id.intValue();
    }

    /** Actualiza los datos principales de un movimiento. */
    public void actualizar(int idMovimiento, LocalDate fecha, String descripcion,
            BigDecimal montoTotal, int idCuenta, int idMedioPago) {
        jdbc.update(SQL_ACTUALIZAR_MOVIMIENTO, Date.valueOf(fecha), descripcion, montoTotal,
                idCuenta, idMedioPago, idMovimiento);
    }

    /** Guarda el reparto del movimiento entre categorias. */
    public void insertarDetalles(int idMovimiento, List<CategoriaAsignadaRequest> categorias) {
        for (CategoriaAsignadaRequest categoria : categorias) {
            jdbc.update(SQL_INSERTAR_DETALLE, idMovimiento, categoria.idCategoria(),
                    categoria.montoAsignado());
        }
    }

    /** Borra el reparto de categorias de un movimiento. */
    public void eliminarDetalles(int idMovimiento) {
        jdbc.update(SQL_ELIMINAR_DETALLES, idMovimiento);
    }

    /** Borra un movimiento (sus detalles se deben borrar antes). */
    public void eliminar(int idMovimiento) {
        jdbc.update(SQL_ELIMINAR_MOVIMIENTO, idMovimiento);
    }

    private MovimientoResponse armar(Fila fila, List<CategoriaAsignadaResponse> categorias) {
        return new MovimientoResponse(fila.idMovimiento(), fila.fecha(), fila.descripcion(),
                fila.montoTotal(), fila.idCuenta(), fila.nombreCuenta(), fila.idMedioPago(),
                fila.nombreMedioPago(), categorias);
    }
}

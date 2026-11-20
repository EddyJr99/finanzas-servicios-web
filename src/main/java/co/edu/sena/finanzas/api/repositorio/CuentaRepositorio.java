package co.edu.sena.finanzas.api.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.CuentaResponse;

/** Acceso a la tabla cuentas (con el nombre del usuario dueño). */
@Repository
public class CuentaRepositorio extends RepositorioBase {

    private static final String SQL_BASE =
            "SELECT c.id_cuenta, c.nombre_cuenta, c.tipo_cuenta, c.banco, c.id_usuario, u.nombre "
                    + "FROM cuentas c LEFT JOIN usuarios u ON u.id_usuario = c.id_usuario ";

    private static final RowMapper<CuentaResponse> MAPEADOR = (rs, fila) -> new CuentaResponse(
            rs.getInt("id_cuenta"), rs.getString("nombre_cuenta"), rs.getString("tipo_cuenta"),
            rs.getString("banco"), rs.getInt("id_usuario"), rs.getString("nombre"));

    public CuentaRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    /** Lista las cuentas (todas, o solo las del usuario indicado). */
    public List<CuentaResponse> listar(Integer idUsuario) {
        if (idUsuario == null) {
            return jdbc.query(SQL_BASE + "ORDER BY c.id_cuenta", MAPEADOR);
        }
        return jdbc.query(SQL_BASE + "WHERE c.id_usuario = ? ORDER BY c.id_cuenta", MAPEADOR, idUsuario);
    }

    public Optional<CuentaResponse> buscarPorId(int id) {
        return jdbc.query(SQL_BASE + "WHERE c.id_cuenta = ?", MAPEADOR, id).stream().findFirst();
    }

    public int insertar(String nombreCuenta, String tipoCuenta, String banco, int idUsuario) {
        return insertarYObtenerId("INSERT INTO cuentas (nombre_cuenta, tipo_cuenta, banco, id_usuario) "
                + "VALUES (?, ?, ?, ?)", nombreCuenta, tipoCuenta, banco, idUsuario);
    }

    public void actualizar(int id, String nombreCuenta, String tipoCuenta, String banco,
            int idUsuario) {
        jdbc.update("UPDATE cuentas SET nombre_cuenta = ?, tipo_cuenta = ?, banco = ?, "
                + "id_usuario = ? WHERE id_cuenta = ?",
                nombreCuenta, tipoCuenta, banco, idUsuario, id);
    }

    public int contarMovimientos(int id) {
        return contar("SELECT COUNT(*) FROM movimientos WHERE id_cuenta = ?", id);
    }

    public void eliminar(int id) {
        jdbc.update("DELETE FROM cuentas WHERE id_cuenta = ?", id);
    }
}

package co.edu.sena.finanzas.api.repositorio;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.UsuarioResponse;

/** Acceso a la tabla usuarios. */
@Repository
public class UsuarioRepositorio extends RepositorioBase {

    private static final String SQL_BASE =
            "SELECT id_usuario, nombre, correo, fecha_registro FROM usuarios ";

    private static final RowMapper<UsuarioResponse> MAPEADOR = (rs, fila) -> new UsuarioResponse(
            rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("correo"),
            rs.getDate("fecha_registro").toLocalDate());

    public UsuarioRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<UsuarioResponse> listar() {
        return jdbc.query(SQL_BASE + "ORDER BY id_usuario", MAPEADOR);
    }

    public Optional<UsuarioResponse> buscarPorId(int id) {
        return jdbc.query(SQL_BASE + "WHERE id_usuario = ?", MAPEADOR, id).stream().findFirst();
    }

    /** Indica si ya hay otro usuario con ese correo (se ignora el usuario con id excluido). */
    public boolean existeCorreo(String correo, int idExcluido) {
        return contar("SELECT COUNT(*) FROM usuarios WHERE LOWER(correo) = LOWER(?) "
                + "AND id_usuario <> ?", correo, idExcluido) > 0;
    }

    public int insertar(String nombre, String correo) {
        return insertarYObtenerId("INSERT INTO usuarios (nombre, correo, fecha_registro) VALUES (?, ?, ?)",
                nombre, correo, Date.valueOf(LocalDate.now()));
    }

    public void actualizar(int id, String nombre, String correo) {
        jdbc.update("UPDATE usuarios SET nombre = ?, correo = ? WHERE id_usuario = ?",
                nombre, correo, id);
    }

    public int contarCuentas(int id) {
        return contar("SELECT COUNT(*) FROM cuentas WHERE id_usuario = ?", id);
    }

    public void eliminar(int id) {
        jdbc.update("DELETE FROM usuarios WHERE id_usuario = ?", id);
    }
}

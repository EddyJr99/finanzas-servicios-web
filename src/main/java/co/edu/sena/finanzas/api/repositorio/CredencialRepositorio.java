package co.edu.sena.finanzas.api.repositorio;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla credenciales, donde se guarda la contrasena cifrada de cada usuario.
 * Va en una tabla aparte para no mezclar datos personales con datos de acceso.
 */
@Repository
public class CredencialRepositorio extends RepositorioBase {

    public CredencialRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    /** Busca el id de un usuario por su correo (sin distinguir mayusculas). */
    public Optional<Integer> buscarIdPorCorreo(String correo) {
        return jdbc.query("SELECT id_usuario FROM usuarios WHERE LOWER(correo) = LOWER(?)",
                (rs, fila) -> rs.getInt("id_usuario"), correo).stream().findFirst();
    }

    /** Devuelve la contrasena cifrada del usuario, si tiene una registrada. */
    public Optional<String> buscarClave(int idUsuario) {
        return jdbc.query("SELECT clave_hash FROM credenciales WHERE id_usuario = ?",
                (rs, fila) -> rs.getString("clave_hash"), idUsuario).stream().findFirst();
    }

    public void guardar(int idUsuario, String claveHash) {
        jdbc.update("INSERT INTO credenciales (id_usuario, clave_hash) VALUES (?, ?)",
                idUsuario, claveHash);
    }

    public void eliminar(int idUsuario) {
        jdbc.update("DELETE FROM credenciales WHERE id_usuario = ?", idUsuario);
    }
}

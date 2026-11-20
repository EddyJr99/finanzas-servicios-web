package co.edu.sena.finanzas.api.repositorio;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Clase base de los repositorios: guarda el JdbcTemplate y ofrece operaciones comunes
 * (insertar devolviendo el id generado, contar registros) para no repetir codigo.
 * Todas las consultas usan parametros (?) para evitar la inyeccion de SQL.
 */
public abstract class RepositorioBase {

    protected final JdbcTemplate jdbc;

    protected RepositorioBase(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Ejecuta un INSERT y devuelve el id autogenerado por MySQL. */
    protected int insertarYObtenerId(String sql, Object... parametros) {
        KeyHolder claves = new GeneratedKeyHolder();
        jdbc.update(conexion -> {
            PreparedStatement sentencia =
                    conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setObject(i + 1, parametros[i]);
            }
            return sentencia;
        }, claves);
        Number id = claves.getKey();
        if (id == null) {
            throw new IllegalStateException("La base de datos no devolvio el id generado");
        }
        return id.intValue();
    }

    /** Ejecuta un SELECT COUNT(*) con parametros y devuelve el resultado. */
    protected int contar(String sql, Object... parametros) {
        Integer total = jdbc.queryForObject(sql, Integer.class, parametros);
        return total == null ? 0 : total;
    }
}

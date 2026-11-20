package co.edu.sena.finanzas.api.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.CategoriaResponse;

/** Acceso a la tabla categorias. */
@Repository
public class CategoriaRepositorio extends RepositorioBase {

    private static final String SQL_BASE = "SELECT id_categoria, nombre_categoria FROM categorias ";

    private static final RowMapper<CategoriaResponse> MAPEADOR = (rs, fila) ->
            new CategoriaResponse(rs.getInt("id_categoria"), rs.getString("nombre_categoria"));

    public CategoriaRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<CategoriaResponse> listar() {
        return jdbc.query(SQL_BASE + "ORDER BY id_categoria", MAPEADOR);
    }

    public Optional<CategoriaResponse> buscarPorId(int id) {
        return jdbc.query(SQL_BASE + "WHERE id_categoria = ?", MAPEADOR, id).stream().findFirst();
    }

    /** Indica si ya existe otra categoria con ese nombre (se ignora la de id excluido). */
    public boolean existeNombre(String nombre, int idExcluido) {
        return contar("SELECT COUNT(*) FROM categorias WHERE LOWER(nombre_categoria) = LOWER(?) "
                + "AND id_categoria <> ?", nombre, idExcluido) > 0;
    }

    public int insertar(String nombreCategoria) {
        return insertarYObtenerId("INSERT INTO categorias (nombre_categoria) VALUES (?)", nombreCategoria);
    }

    public void actualizar(int id, String nombreCategoria) {
        jdbc.update("UPDATE categorias SET nombre_categoria = ? WHERE id_categoria = ?",
                nombreCategoria, id);
    }

    public int contarUsos(int id) {
        return contar("SELECT COUNT(*) FROM detalle_categoria WHERE id_categoria = ?", id);
    }

    public void eliminar(int id) {
        jdbc.update("DELETE FROM categorias WHERE id_categoria = ?", id);
    }
}

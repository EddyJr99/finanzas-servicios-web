package co.edu.sena.finanzas.api.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import co.edu.sena.finanzas.api.dto.MedioPagoResponse;

/** Acceso a la tabla medios_pago. */
@Repository
public class MedioPagoRepositorio extends RepositorioBase {

    private static final String SQL_BASE =
            "SELECT id_medio_pago, nombre_medio_pago, entidad_financiera FROM medios_pago ";

    private static final RowMapper<MedioPagoResponse> MAPEADOR = (rs, fila) ->
            new MedioPagoResponse(rs.getInt("id_medio_pago"), rs.getString("nombre_medio_pago"),
                    rs.getString("entidad_financiera"));

    public MedioPagoRepositorio(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<MedioPagoResponse> listar() {
        return jdbc.query(SQL_BASE + "ORDER BY id_medio_pago", MAPEADOR);
    }

    public Optional<MedioPagoResponse> buscarPorId(int id) {
        return jdbc.query(SQL_BASE + "WHERE id_medio_pago = ?", MAPEADOR, id).stream()
                .findFirst();
    }

    public int insertar(String nombreMedioPago, String entidadFinanciera) {
        return insertarYObtenerId("INSERT INTO medios_pago (nombre_medio_pago, entidad_financiera) "
                + "VALUES (?, ?)", nombreMedioPago, entidadFinanciera);
    }

    public void actualizar(int id, String nombreMedioPago, String entidadFinanciera) {
        jdbc.update("UPDATE medios_pago SET nombre_medio_pago = ?, entidad_financiera = ? "
                + "WHERE id_medio_pago = ?", nombreMedioPago, entidadFinanciera, id);
    }

    public int contarMovimientos(int id) {
        return contar("SELECT COUNT(*) FROM movimientos WHERE id_medio_pago = ?", id);
    }

    public void eliminar(int id) {
        jdbc.update("DELETE FROM medios_pago WHERE id_medio_pago = ?", id);
    }
}

package co.edu.sena.finanzas.api.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.MedioPagoRequest;
import co.edu.sena.finanzas.api.dto.MedioPagoResponse;
import co.edu.sena.finanzas.api.excepcion.ConflictoException;
import co.edu.sena.finanzas.api.excepcion.RecursoNoEncontradoException;
import co.edu.sena.finanzas.api.repositorio.MedioPagoRepositorio;

/** Reglas de negocio de los medios de pago: no se borran los que tienen movimientos. */
@Service
public class MedioPagoServicio {

    private final MedioPagoRepositorio repositorio;

    public MedioPagoServicio(MedioPagoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<MedioPagoResponse> listar() {
        return repositorio.listar();
    }

    @Transactional(readOnly = true)
    public MedioPagoResponse obtener(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe un medio de pago con id " + id));
    }

    @Transactional
    public MedioPagoResponse crear(MedioPagoRequest datos) {
        int id = repositorio.insertar(datos.nombreMedioPago().trim(),
                datos.entidadFinanciera().trim());
        return obtener(id);
    }

    @Transactional
    public MedioPagoResponse reemplazar(int id, MedioPagoRequest datos) {
        obtener(id);
        repositorio.actualizar(id, datos.nombreMedioPago().trim(),
                datos.entidadFinanciera().trim());
        return obtener(id);
    }

    @Transactional
    public void eliminar(int id) {
        obtener(id);
        int movimientos = repositorio.contarMovimientos(id);
        if (movimientos > 0) {
            throw new ConflictoException("No se puede eliminar el medio de pago " + id
                    + " porque tiene " + movimientos + " movimiento(s) registrado(s)");
        }
        repositorio.eliminar(id);
    }
}

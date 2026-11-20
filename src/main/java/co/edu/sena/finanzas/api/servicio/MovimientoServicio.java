package co.edu.sena.finanzas.api.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.CategoriaAsignadaRequest;
import co.edu.sena.finanzas.api.dto.CategoriaAsignadaResponse;
import co.edu.sena.finanzas.api.dto.MovimientoPatchRequest;
import co.edu.sena.finanzas.api.dto.MovimientoRequest;
import co.edu.sena.finanzas.api.dto.MovimientoResponse;
import co.edu.sena.finanzas.api.excepcion.RecursoNoEncontradoException;
import co.edu.sena.finanzas.api.excepcion.ReglaNegocioException;
import co.edu.sena.finanzas.api.repositorio.MovimientoRepositorio;

/**
 * Logica de negocio de los movimientos.
 *
 * Las operaciones que modifican datos son transaccionales ({@code @Transactional}):
 * como cada movimiento toca dos tablas (movimientos y detalle_categoria), o se guardan
 * los dos cambios o no se guarda ninguno.
 */
@Service
public class MovimientoServicio {

    private final MovimientoRepositorio repositorio;

    public MovimientoServicio(MovimientoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Lista los movimientos (todos, o solo los del usuario indicado). */
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar(Integer idUsuario) {
        return repositorio.listar(idUsuario);
    }

    /** Devuelve un movimiento o lanza 404 si no existe. */
    @Transactional(readOnly = true)
    public MovimientoResponse obtener(int idMovimiento) {
        return repositorio.buscarPorId(idMovimiento).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "No existe un movimiento con id " + idMovimiento));
    }

    /** POST: crea un movimiento con su reparto de categorias. */
    @Transactional
    public MovimientoResponse crear(MovimientoRequest datos) {
        validarReparto(datos.montoTotal(), datos.categorias());
        int idNuevo = repositorio.insertar(datos.fecha(), datos.descripcion(),
                datos.montoTotal(), datos.idCuenta(), datos.idMedioPago());
        repositorio.insertarDetalles(idNuevo, datos.categorias());
        return obtener(idNuevo);
    }

    /** PUT: reemplaza por completo un movimiento existente. */
    @Transactional
    public MovimientoResponse reemplazar(int idMovimiento, MovimientoRequest datos) {
        obtener(idMovimiento);
        validarReparto(datos.montoTotal(), datos.categorias());
        repositorio.actualizar(idMovimiento, datos.fecha(), datos.descripcion(),
                datos.montoTotal(), datos.idCuenta(), datos.idMedioPago());
        repositorio.eliminarDetalles(idMovimiento);
        repositorio.insertarDetalles(idMovimiento, datos.categorias());
        return obtener(idMovimiento);
    }

    /** PATCH: modifica solo los campos que vienen en la solicitud. */
    @Transactional
    public MovimientoResponse modificarParcialmente(int idMovimiento,
            MovimientoPatchRequest cambios) {
        MovimientoResponse actual = obtener(idMovimiento);

        LocalDate fecha = cambios.fecha() != null ? cambios.fecha() : actual.fecha();
        String descripcion = cambios.descripcion() != null
                ? cambios.descripcion() : actual.descripcion();
        BigDecimal montoTotal = cambios.montoTotal() != null
                ? cambios.montoTotal() : actual.montoTotal();
        int idCuenta = cambios.idCuenta() != null ? cambios.idCuenta() : actual.idCuenta();
        int idMedioPago = cambios.idMedioPago() != null
                ? cambios.idMedioPago() : actual.idMedioPago();

        // Si no se envian categorias, se conserva el reparto actual (y se revisa contra el nuevo monto).
        List<CategoriaAsignadaRequest> categorias = cambios.categorias() != null
                ? cambios.categorias() : repartoActual(actual);
        if (!categorias.isEmpty()) {
            validarReparto(montoTotal, categorias);
        }

        repositorio.actualizar(idMovimiento, fecha, descripcion, montoTotal, idCuenta,
                idMedioPago);
        if (cambios.categorias() != null) {
            repositorio.eliminarDetalles(idMovimiento);
            repositorio.insertarDetalles(idMovimiento, cambios.categorias());
        }
        return obtener(idMovimiento);
    }

    /** DELETE: elimina un movimiento y todo su reparto de categorias. */
    @Transactional
    public void eliminar(int idMovimiento) {
        obtener(idMovimiento);
        repositorio.eliminarDetalles(idMovimiento);
        repositorio.eliminar(idMovimiento);
    }

    private List<CategoriaAsignadaRequest> repartoActual(MovimientoResponse actual) {
        return actual.categorias().stream()
                .map((CategoriaAsignadaResponse categoria) -> new CategoriaAsignadaRequest(
                        categoria.idCategoria(), categoria.montoAsignado()))
                .toList();
    }

    /**
     * Regla de negocio: no se puede repetir una categoria y la suma de los montos
     * asignados no puede superar el monto total del movimiento.
     */
    private void validarReparto(BigDecimal montoTotal, List<CategoriaAsignadaRequest> categorias) {
        Set<Integer> vistas = new HashSet<>();
        BigDecimal suma = BigDecimal.ZERO;
        for (CategoriaAsignadaRequest categoria : categorias) {
            if (!vistas.add(categoria.idCategoria())) {
                throw new ReglaNegocioException(
                        "La categoria " + categoria.idCategoria() + " esta repetida");
            }
            suma = suma.add(categoria.montoAsignado());
        }
        if (suma.compareTo(montoTotal) > 0) {
            throw new ReglaNegocioException("La suma de los montos asignados a las categorias ("
                    + suma + ") no puede superar el monto total (" + montoTotal + ")");
        }
    }
}

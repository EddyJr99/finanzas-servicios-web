package co.edu.sena.finanzas.api.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.CuentaRequest;
import co.edu.sena.finanzas.api.dto.CuentaResponse;
import co.edu.sena.finanzas.api.excepcion.ConflictoException;
import co.edu.sena.finanzas.api.excepcion.RecursoNoEncontradoException;
import co.edu.sena.finanzas.api.excepcion.ReglaNegocioException;
import co.edu.sena.finanzas.api.repositorio.CuentaRepositorio;
import co.edu.sena.finanzas.api.repositorio.UsuarioRepositorio;

/** Reglas de negocio de las cuentas: el usuario dueño debe existir y no se borran cuentas con movimientos. */
@Service
public class CuentaServicio {

    private final CuentaRepositorio repositorio;
    private final UsuarioRepositorio usuarios;

    public CuentaServicio(CuentaRepositorio repositorio, UsuarioRepositorio usuarios) {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listar(Integer idUsuario) {
        return repositorio.listar(idUsuario);
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtener(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe una cuenta con id " + id));
    }

    @Transactional
    public CuentaResponse crear(CuentaRequest datos) {
        validarUsuario(datos.idUsuario());
        int id = repositorio.insertar(datos.nombreCuenta().trim(), datos.tipoCuenta().trim(),
                datos.banco().trim(), datos.idUsuario());
        return obtener(id);
    }

    @Transactional
    public CuentaResponse reemplazar(int id, CuentaRequest datos) {
        obtener(id);
        validarUsuario(datos.idUsuario());
        repositorio.actualizar(id, datos.nombreCuenta().trim(), datos.tipoCuenta().trim(),
                datos.banco().trim(), datos.idUsuario());
        return obtener(id);
    }

    @Transactional
    public void eliminar(int id) {
        obtener(id);
        int movimientos = repositorio.contarMovimientos(id);
        if (movimientos > 0) {
            throw new ConflictoException("No se puede eliminar la cuenta " + id + " porque tiene "
                    + movimientos + " movimiento(s) registrado(s)");
        }
        repositorio.eliminar(id);
    }

    private void validarUsuario(int idUsuario) {
        if (usuarios.buscarPorId(idUsuario).isEmpty()) {
            throw new ReglaNegocioException("El usuario " + idUsuario + " no existe");
        }
    }
}

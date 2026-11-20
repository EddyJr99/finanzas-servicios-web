package co.edu.sena.finanzas.api.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.UsuarioRequest;
import co.edu.sena.finanzas.api.dto.UsuarioResponse;
import co.edu.sena.finanzas.api.excepcion.ConflictoException;
import co.edu.sena.finanzas.api.excepcion.RecursoNoEncontradoException;
import co.edu.sena.finanzas.api.repositorio.UsuarioRepositorio;

/** Reglas de negocio de los usuarios: correo unico y no borrar usuarios con cuentas. */
@Service
public class UsuarioServicio {

    private final UsuarioRepositorio repositorio;

    public UsuarioServicio(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repositorio.listar();
    }

    /** Devuelve el usuario o lanza 404 si no existe. */
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe un usuario con id " + id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest datos) {
        validarCorreo(datos.correo(), 0);
        int id = repositorio.insertar(datos.nombre().trim(), datos.correo().trim());
        return obtener(id);
    }

    @Transactional
    public UsuarioResponse reemplazar(int id, UsuarioRequest datos) {
        obtener(id);
        validarCorreo(datos.correo(), id);
        repositorio.actualizar(id, datos.nombre().trim(), datos.correo().trim());
        return obtener(id);
    }

    /** Elimina el usuario; responde 409 si todavia tiene cuentas. */
    @Transactional
    public void eliminar(int id) {
        obtener(id);
        int cuentas = repositorio.contarCuentas(id);
        if (cuentas > 0) {
            throw new ConflictoException("No se puede eliminar el usuario " + id
                    + " porque tiene " + cuentas + " cuenta(s) asociada(s)");
        }
        repositorio.eliminar(id);
    }

    private void validarCorreo(String correo, int idExcluido) {
        if (repositorio.existeCorreo(correo.trim(), idExcluido)) {
            throw new ConflictoException("Ya existe un usuario con el correo " + correo.trim());
        }
    }
}

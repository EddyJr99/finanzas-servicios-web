package co.edu.sena.finanzas.api.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.CategoriaRequest;
import co.edu.sena.finanzas.api.dto.CategoriaResponse;
import co.edu.sena.finanzas.api.excepcion.ConflictoException;
import co.edu.sena.finanzas.api.excepcion.RecursoNoEncontradoException;
import co.edu.sena.finanzas.api.repositorio.CategoriaRepositorio;

/** Reglas de negocio de las categorias: nombre unico y no borrar las que ya se usan. */
@Service
public class CategoriaServicio {

    private final CategoriaRepositorio repositorio;

    public CategoriaServicio(CategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repositorio.listar();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe una categoria con id " + id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest datos) {
        validarNombre(datos.nombreCategoria(), 0);
        return obtener(repositorio.insertar(datos.nombreCategoria().trim()));
    }

    @Transactional
    public CategoriaResponse reemplazar(int id, CategoriaRequest datos) {
        obtener(id);
        validarNombre(datos.nombreCategoria(), id);
        repositorio.actualizar(id, datos.nombreCategoria().trim());
        return obtener(id);
    }

    @Transactional
    public void eliminar(int id) {
        obtener(id);
        int usos = repositorio.contarUsos(id);
        if (usos > 0) {
            throw new ConflictoException("No se puede eliminar la categoria " + id
                    + " porque esta asignada a " + usos + " movimiento(s)");
        }
        repositorio.eliminar(id);
    }

    private void validarNombre(String nombre, int idExcluido) {
        if (repositorio.existeNombre(nombre.trim(), idExcluido)) {
            throw new ConflictoException("Ya existe una categoria llamada " + nombre.trim());
        }
    }
}

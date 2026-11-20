package co.edu.sena.finanzas.api.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.LoginRequest;
import co.edu.sena.finanzas.api.dto.RegistroRequest;
import co.edu.sena.finanzas.api.dto.SesionResponse;
import co.edu.sena.finanzas.api.dto.UsuarioResponse;
import co.edu.sena.finanzas.api.excepcion.ConflictoException;
import co.edu.sena.finanzas.api.excepcion.NoAutorizadoException;
import co.edu.sena.finanzas.api.repositorio.CredencialRepositorio;
import co.edu.sena.finanzas.api.repositorio.UsuarioRepositorio;

/**
 * Registro e inicio de sesion.
 *
 * Si el correo ya existe como usuario pero nunca se le asigno contrasena (por ejemplo,
 * los usuarios creados antes de tener login), el registro le "activa" la cuenta.
 */
@Service
public class AuthServicio {

    /** Mismo mensaje para correo o clave incorrectos: asi no se revela cual de los dos fallo. */
    private static final String ERROR_AUTENTICACION =
            "Error en la autenticacion: correo o contrasena incorrectos";

    private final UsuarioRepositorio usuarios;
    private final CredencialRepositorio credenciales;

    public AuthServicio(UsuarioRepositorio usuarios, CredencialRepositorio credenciales) {
        this.usuarios = usuarios;
        this.credenciales = credenciales;
    }

    @Transactional
    public SesionResponse registrar(RegistroRequest datos) {
        String correo = datos.correo().trim();
        Integer idExistente = credenciales.buscarIdPorCorreo(correo).orElse(null);
        int idUsuario;
        if (idExistente == null) {
            idUsuario = usuarios.insertar(datos.nombre().trim(), correo);
        } else if (credenciales.buscarClave(idExistente).isPresent()) {
            throw new ConflictoException("Ya existe una cuenta con el correo " + correo);
        } else {
            idUsuario = idExistente;
        }
        credenciales.guardar(idUsuario, ClaveSegura.cifrar(datos.clave()));
        UsuarioResponse usuario = usuarios.buscarPorId(idUsuario).orElseThrow();
        return new SesionResponse(usuario.idUsuario(), usuario.nombre(), usuario.correo(),
                "Cuenta creada correctamente. Ya puede iniciar sesion");
    }

    @Transactional(readOnly = true)
    public SesionResponse iniciarSesion(LoginRequest datos) {
        int idUsuario = credenciales.buscarIdPorCorreo(datos.correo().trim())
                .orElseThrow(() -> new NoAutorizadoException(ERROR_AUTENTICACION));
        String guardada = credenciales.buscarClave(idUsuario)
                .orElseThrow(() -> new NoAutorizadoException(ERROR_AUTENTICACION));
        if (!ClaveSegura.coincide(datos.clave(), guardada)) {
            throw new NoAutorizadoException(ERROR_AUTENTICACION);
        }
        UsuarioResponse usuario = usuarios.buscarPorId(idUsuario).orElseThrow();
        return new SesionResponse(usuario.idUsuario(), usuario.nombre(), usuario.correo(),
                "Autenticacion satisfactoria. Bienvenido, " + usuario.nombre());
    }
}

package co.edu.sena.finanzas.api.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.finanzas.api.dto.LoginRequest;
import co.edu.sena.finanzas.api.dto.RegistroRequest;
import co.edu.sena.finanzas.api.dto.SesionResponse;
import co.edu.sena.finanzas.api.servicio.AuthServicio;
import jakarta.validation.Valid;

/**
 * Servicios de acceso: registro de una cuenta e inicio de sesion.
 * Los usa la pagina de login y registro del front-end.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final AuthServicio servicio;

    public AuthControlador(AuthServicio servicio) {
        this.servicio = servicio;
    }

    /** POST /api/auth/registro: crea la cuenta. Responde 201, 400 (datos) o 409 (correo ya registrado). */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionResponse registrar(@Valid @RequestBody RegistroRequest datos) {
        return servicio.registrar(datos);
    }

    /** POST /api/auth/login: valida correo y contrasena. Responde 200 o 401 (error en la autenticacion). */
    @PostMapping("/login")
    public SesionResponse iniciarSesion(@Valid @RequestBody LoginRequest datos) {
        return servicio.iniciarSesion(datos);
    }
}

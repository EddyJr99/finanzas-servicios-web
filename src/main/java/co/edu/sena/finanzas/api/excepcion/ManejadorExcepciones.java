package co.edu.sena.finanzas.api.excepcion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import co.edu.sena.finanzas.api.dto.ErrorRespuesta;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Convierte las excepciones de la API en respuestas JSON con el codigo de estado
 * HTTP adecuado, para que el cliente siempre reciba el mismo formato de error.
 */
@RestControllerAdvice
public class ManejadorExcepciones {

    /** 404: el recurso pedido no existe. */
    @ExceptionHandler({RecursoNoEncontradoException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorRespuesta> manejarNoEncontrado(Exception excepcion,
            HttpServletRequest solicitud) {
        String mensaje = excepcion instanceof RecursoNoEncontradoException
                ? excepcion.getMessage()
                : "La ruta solicitada no existe";
        return construir(HttpStatus.NOT_FOUND, mensaje, List.of(), solicitud);
    }

    /** 400: algun campo no cumple las validaciones (@NotNull, @Size, ...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> manejarValidacion(MethodArgumentNotValidException excepcion,
            HttpServletRequest solicitud) {
        List<String> detalles = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Los datos enviados no son validos", detalles,
                solicitud);
    }

    /** 400: el cuerpo no es un JSON valido o trae campos o formatos desconocidos. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> manejarJsonInvalido(HttpMessageNotReadableException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es un JSON valido o tiene campos desconocidos "
                        + "(las fechas deben ser AAAA-MM-DD)",
                List.of(), solicitud);
    }

    /** 400: el id de la ruta no es un numero. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> manejarTipoIncorrecto(
            MethodArgumentTypeMismatchException excepcion, HttpServletRequest solicitud) {
        return construir(HttpStatus.BAD_REQUEST,
                "El parametro '" + excepcion.getName() + "' tiene un valor no valido",
                List.of(), solicitud);
    }

    /** 400: se incumple una regla del negocio (por ejemplo, el reparto de montos). */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> manejarReglaNegocio(ReglaNegocioException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.BAD_REQUEST, excepcion.getMessage(), List.of(), solicitud);
    }

    /** 401: correo o contrasena incorrectos al iniciar sesion. */
    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<ErrorRespuesta> manejarNoAutorizado(NoAutorizadoException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.UNAUTHORIZED, excepcion.getMessage(), List.of(), solicitud);
    }

    /** 409: la operacion choca con los datos existentes (duplicado o registro en uso). */
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorRespuesta> manejarConflicto(ConflictoException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.CONFLICT, excepcion.getMessage(), List.of(), solicitud);
    }

    /** 400: la base rechazo el dato, por ejemplo una cuenta, medio de pago o categoria inexistente. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> manejarIntegridad(DataIntegrityViolationException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.BAD_REQUEST,
                "La base de datos rechazo el dato: alguna referencia indicada no existe "
                        + "o el registro esta en uso",
                List.of(), solicitud);
    }

    /** 405: el verbo HTTP no esta permitido en esa ruta. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> manejarMetodoNoPermitido(
            HttpRequestMethodNotSupportedException excepcion, HttpServletRequest solicitud) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED,
                "El metodo " + excepcion.getMethod() + " no esta permitido en esta ruta",
                List.of(), solicitud);
    }

    /** 500: no se pudo hablar con la base de datos (MySQL apagado, clave incorrecta...). */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorRespuesta> manejarBaseDeDatos(DataAccessException excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error de base de datos. Revise que MySQL este iniciado y que los datos de "
                        + "conexion de application.properties sean correctos",
                List.of(), solicitud);
    }

    /** 500: cualquier otro error inesperado. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarInesperado(Exception excepcion,
            HttpServletRequest solicitud) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado",
                List.of(), solicitud);
    }

    private ResponseEntity<ErrorRespuesta> construir(HttpStatus estado, String mensaje,
            List<String> detalles, HttpServletRequest solicitud) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(estado.value(), estado.getReasonPhrase(),
                mensaje, detalles, solicitud.getRequestURI(), LocalDateTime.now());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}

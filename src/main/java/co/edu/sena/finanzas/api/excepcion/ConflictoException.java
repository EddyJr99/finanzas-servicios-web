package co.edu.sena.finanzas.api.excepcion;

/**
 * Se lanza cuando la operacion choca con el estado actual de los datos
 * (por ejemplo, borrar un usuario que tiene cuentas, o repetir un correo).
 * La API la responde con el codigo HTTP 409 (Conflict).
 */
public class ConflictoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}

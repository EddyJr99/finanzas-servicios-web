package co.edu.sena.finanzas.api.excepcion;

/** Se lanza cuando el correo o la contrasena no son correctos. La API responde 401. */
public class NoAutorizadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}

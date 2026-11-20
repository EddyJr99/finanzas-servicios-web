package co.edu.sena.finanzas.api.excepcion;

/** Se lanza cuando se pide un recurso (por ejemplo, un movimiento) que no existe. La API responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

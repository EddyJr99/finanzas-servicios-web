package co.edu.sena.finanzas.api.excepcion;

/** Se lanza cuando los datos son validos en formato pero incumplen una regla del negocio. La API responde 400. */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

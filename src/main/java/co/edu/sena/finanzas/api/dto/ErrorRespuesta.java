package co.edu.sena.finanzas.api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cuerpo JSON que devuelve la API cuando una solicitud falla.
 *
 * @param estado   codigo de estado HTTP (400, 404, 500...)
 * @param error    nombre corto del estado ("Bad Request", "Not Found"...)
 * @param mensaje  explicacion general
 * @param detalles lista de problemas concretos (por ejemplo, un mensaje por campo invalido)
 * @param ruta     ruta de la solicitud que fallo
 * @param fecha    momento del error
 */
public record ErrorRespuesta(
        int estado,
        String error,
        String mensaje,
        List<String> detalles,
        String ruta,
        LocalDateTime fecha) {
}

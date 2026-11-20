package co.edu.sena.finanzas.api.dto;

/**
 * Respuesta del registro y del inicio de sesion: los datos basicos del usuario
 * (nunca la contrasena) y un mensaje para mostrar en pantalla.
 */
public record SesionResponse(Integer idUsuario, String nombre, String correo, String mensaje) {
}

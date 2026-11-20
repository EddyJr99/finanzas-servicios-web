package co.edu.sena.finanzas.api.dto;

/** Cuenta tal como la devuelve la API, con el nombre de su dueño. */
public record CuentaResponse(Integer idCuenta, String nombreCuenta, String tipoCuenta,
        String banco, Integer idUsuario, String nombreUsuario) {
}

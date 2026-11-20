package co.edu.sena.finanzas.api.dto;

/** Categoria tal como la devuelve la API. El tipo indica si es de INGRESO o de GASTO. */
public record CategoriaResponse(Integer idCategoria, String nombreCategoria, String tipo) {
}

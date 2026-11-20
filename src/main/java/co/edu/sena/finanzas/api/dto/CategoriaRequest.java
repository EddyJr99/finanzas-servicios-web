package co.edu.sena.finanzas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o reemplazar una categoria.
 *
 * @param nombreCategoria por ejemplo "Mercado" o "Transporte"
 */
public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoria es obligatorio")
        @Size(max = 100, message = "El nombre de la categoria admite maximo 100 caracteres")
        String nombreCategoria) {
}

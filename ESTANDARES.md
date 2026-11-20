# Estándares de codificación aplicados

| Aspecto | Criterio usado |
|---|---|
| Paquetes | minúsculas, jerarquía `co.edu.sena.finanzas.api.{controlador,servicio,repositorio,dto,excepcion}` |
| Clases | `PascalCase`, nombre = responsabilidad (`MovimientoControlador`, `MovimientoServicio`, `MovimientoRepositorio`) |
| Métodos y variables | `camelCase`, verbos en métodos (`crear`, `reemplazar`, `eliminar`), nombres descriptivos |
| Constantes | `MAYUSCULAS_CON_GUION_BAJO` (`SQL_INSERTAR_MOVIMIENTO`) |
| Capas | Controlador (HTTP) → Servicio (reglas y transacciones) → Repositorio (SQL). Cada capa solo conoce a la siguiente |
| Inyección de dependencias | Por constructor, campos `final` |
| SQL | Siempre con parámetros `?` (`JdbcTemplate`), nunca concatenando texto: previene inyección SQL |
| Validación | Anotaciones Bean Validation en los DTO (`@NotNull`, `@NotBlank`, `@DecimalMin`, `@PastOrPresent`, `@Valid`) + reglas de negocio en el servicio |
| Errores | Un único `@RestControllerAdvice` devuelve siempre el mismo JSON de error con el código HTTP correcto |
| DTO | `record` inmutables; la entidad de BD nunca se expone directamente |
| Transacciones | `@Transactional` en el servicio: movimiento y reparto se guardan juntos o no se guarda nada |
| Comentarios | Javadoc en cada clase y método público explicando qué hace y qué código HTTP responde |
| Versionamiento | Git con commits descriptivos, repositorio en GitHub, `.gitignore` para `target/` |
| API | Recursos en plural y minúsculas (`/api/movimientos`), verbos HTTP según su semántica, códigos 200/201/204/400/404/405/500 |

# Documentación de los servicios web – SGFP

Servicios REST del Sistema de Gestión de Finanzas Personales. Formato JSON, URL base `http://localhost:8080`
(el puerto se cambia con `ejecutar_api.bat 9090`).

## Convenciones

- Los nombres de los campos van en `camelCase`; las fechas, en formato `AAAA-MM-DD`.
- Los montos son números con máximo 2 decimales.
- Crear (POST) y reemplazar (PUT) exigen todos los campos. PATCH (solo movimientos) acepta cualquier subconjunto.
- `POST` responde `201 Created` y la cabecera `Location` con la ruta del nuevo recurso.
- `DELETE` responde `204 No Content` (sin cuerpo).
- Todos los errores tienen el mismo formato:

```json
{
  "estado": 409,
  "error": "Conflict",
  "mensaje": "No se puede eliminar el usuario 1 porque tiene 2 cuenta(s) asociada(s)",
  "detalles": [],
  "ruta": "/api/usuarios/1",
  "fecha": "2026-11-08T19:40:00.123"
}
```

En los errores de validación (400), `detalles` lista cada campo con su problema, por ejemplo
`"correo: El correo no tiene un formato valido"`.

## Códigos de respuesta

| Código | Significado | Cuándo |
|---|---|---|
| 200 | OK | Consulta, reemplazo o modificación correctos |
| 201 | Created | Recurso creado |
| 204 | No Content | Recurso eliminado |
| 400 | Bad Request | Datos inválidos, JSON mal formado, id no numérico, referencia inexistente |
| 404 | Not Found | El recurso o la ruta no existen |
| 405 | Method Not Allowed | Verbo no permitido en esa ruta |
| 409 | Conflict | Correo o nombre duplicado, o intento de borrar un registro que está en uso |
| 500 | Internal Server Error | Error de base de datos o error inesperado |

---

## 0. Acceso – `/api/auth`

Registro e inicio de sesión. Las contraseñas se guardan cifradas con PBKDF2 (tabla `credenciales`), nunca en texto plano.

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| POST | `/api/auth/registro` | Crea la cuenta (nombre, correo, clave) | 201, 400, 409 |
| POST | `/api/auth/login` | Inicia sesión con correo y clave | 200, 400, 401 |

```json
{ "nombre": "Laura Gómez", "correo": "laura.gomez@example.com", "clave": "MiClave2026" }
```

**Respuesta del login (200)**
```json
{ "idUsuario": 4, "nombre": "Laura Gómez", "correo": "laura.gomez@example.com", "mensaje": "Autenticacion satisfactoria. Bienvenido, Laura Gómez" }
```
Si el correo o la clave no son correctos responde **401** con `"mensaje": "Error en la autenticacion: correo o contrasena incorrectos"`.
Si el correo ya tiene cuenta, el registro responde **409**.

## Filtro por usuario

`GET /api/movimientos`, `GET /api/cuentas` y `GET /api/resumen` aceptan `?idUsuario=N` para devolver solo los datos de ese usuario (lo usa el dashboard).
Las categorías tienen el campo `tipo` (`INGRESO` o `GASTO`) y cada movimiento devuelve su `tipo` según sus categorías.
El resumen incluye `totalIngresos`, `totalGastos` y `balance`.

## 1. Usuarios – `/api/usuarios`

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios | 200 |
| GET | `/api/usuarios/{id}` | Consulta un usuario | 200, 404 |
| POST | `/api/usuarios` | Crea un usuario | 201, 400, 409 |
| PUT | `/api/usuarios/{id}` | Reemplaza un usuario | 200, 400, 404, 409 |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario | 204, 404, 409 |

**Cuerpo (POST y PUT)**

| Campo | Tipo | Obligatorio | Regla |
|---|---|---|---|
| `nombre` | texto | sí | no vacío, máx. 100 |
| `correo` | texto | sí | correo válido, máx. 100, **único** |

```json
{ "nombre": "Laura Gómez", "correo": "laura.gomez@example.com" }
```

**Respuesta**
```json
{ "idUsuario": 4, "nombre": "Laura Gómez", "correo": "laura.gomez@example.com", "fechaRegistro": "2026-11-08" }
```
`fechaRegistro` la asigna el servidor al crear. **409** si el correo ya existe o si se borra un usuario con cuentas.

## 2. Cuentas – `/api/cuentas`

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/cuentas` | Lista las cuentas | 200 |
| GET | `/api/cuentas/{id}` | Consulta una cuenta | 200, 404 |
| POST | `/api/cuentas` | Crea una cuenta | 201, 400 |
| PUT | `/api/cuentas/{id}` | Reemplaza una cuenta | 200, 400, 404 |
| DELETE | `/api/cuentas/{id}` | Elimina una cuenta | 204, 404, 409 |

**Cuerpo (POST y PUT)**

| Campo | Tipo | Obligatorio | Regla |
|---|---|---|---|
| `nombreCuenta` | texto | sí | no vacío, máx. 100 |
| `tipoCuenta` | texto | sí | no vacío, máx. 50 |
| `banco` | texto | sí | no vacío, máx. 100 |
| `idUsuario` | entero | sí | mayor que 0 y de un usuario **existente** (si no, 400) |

```json
{ "nombreCuenta": "Ahorros BBVA", "tipoCuenta": "Ahorros", "banco": "BBVA", "idUsuario": 1 }
```

**Respuesta**
```json
{ "idCuenta": 1, "nombreCuenta": "Ahorros BBVA", "tipoCuenta": "Ahorros", "banco": "BBVA", "idUsuario": 1, "nombreUsuario": "Eddy Altamar" }
```
**409** al borrar una cuenta que tiene movimientos.

## 3. Medios de pago – `/api/medios-pago`

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/medios-pago` | Lista los medios de pago | 200 |
| GET | `/api/medios-pago/{id}` | Consulta un medio de pago | 200, 404 |
| POST | `/api/medios-pago` | Crea un medio de pago | 201, 400 |
| PUT | `/api/medios-pago/{id}` | Reemplaza un medio de pago | 200, 400, 404 |
| DELETE | `/api/medios-pago/{id}` | Elimina un medio de pago | 204, 404, 409 |

**Cuerpo (POST y PUT)**

| Campo | Tipo | Obligatorio | Regla |
|---|---|---|---|
| `nombreMedioPago` | texto | sí | no vacío, máx. 100 |
| `entidadFinanciera` | texto | sí | no vacío, máx. 100 |

```json
{ "nombreMedioPago": "Tarjeta Débito", "entidadFinanciera": "BBVA" }
```
**Respuesta:** `{ "idMedioPago": 1, "nombreMedioPago": "Tarjeta Débito", "entidadFinanciera": "BBVA" }`.
**409** al borrar un medio de pago que tiene movimientos.

## 4. Categorías – `/api/categorias`

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/categorias` | Lista las categorías | 200 |
| GET | `/api/categorias/{id}` | Consulta una categoría | 200, 404 |
| POST | `/api/categorias` | Crea una categoría | 201, 400, 409 |
| PUT | `/api/categorias/{id}` | Reemplaza una categoría | 200, 400, 404, 409 |
| DELETE | `/api/categorias/{id}` | Elimina una categoría | 204, 404, 409 |

**Cuerpo (POST y PUT):** `nombreCategoria` (texto, obligatorio, máx. 100, **único** sin distinguir mayúsculas).

```json
{ "nombreCategoria": "Mascotas" }
```
**Respuesta:** `{ "idCategoria": 7, "nombreCategoria": "Mascotas" }`.
**409** si el nombre ya existe o si se borra una categoría asignada a movimientos.

## 5. Movimientos – `/api/movimientos`

Un movimiento tiene un monto total que puede repartirse entre varias categorías.

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/movimientos` | Lista los movimientos (más recientes primero) | 200 |
| GET | `/api/movimientos/{id}` | Consulta un movimiento | 200, 404 |
| POST | `/api/movimientos` | Crea un movimiento con su reparto | 201, 400 |
| PUT | `/api/movimientos/{id}` | Reemplaza todo el movimiento y su reparto | 200, 400, 404 |
| PATCH | `/api/movimientos/{id}` | Modifica solo los campos enviados | 200, 400, 404 |
| DELETE | `/api/movimientos/{id}` | Elimina el movimiento y su reparto | 204, 404 |

**Cuerpo (POST y PUT)**

| Campo | Tipo | Obligatorio | Regla |
|---|---|---|---|
| `fecha` | fecha | sí | `AAAA-MM-DD`, no futura |
| `descripcion` | texto | sí | no vacío, máx. 150 |
| `montoTotal` | número | sí | mayor que 0, máx. 2 decimales |
| `idCuenta` | entero | sí | cuenta existente |
| `idMedioPago` | entero | sí | medio de pago existente |
| `categorias` | lista | sí | al menos una; cada una con `idCategoria` y `montoAsignado` (> 0) |

Reglas de negocio: no se repite una categoría y la suma de `montoAsignado` no puede superar `montoTotal`.

```json
{
  "fecha": "2026-05-10",
  "descripcion": "Compra de mercado",
  "montoTotal": 150000,
  "idCuenta": 1,
  "idMedioPago": 1,
  "categorias": [
    { "idCategoria": 1, "montoAsignado": 100000 },
    { "idCategoria": 2, "montoAsignado": 50000 }
  ]
}
```

**PATCH:** cualquier campo del cuerpo anterior es opcional, por ejemplo `{ "descripcion": "Nuevo texto" }`.
Si se envía `categorias`, reemplaza todo el reparto.

**Respuesta**
```json
{
  "idMovimiento": 9, "fecha": "2026-05-10", "descripcion": "Compra de mercado", "montoTotal": 150000,
  "idCuenta": 1, "nombreCuenta": "Ahorros BBVA", "idMedioPago": 1, "nombreMedioPago": "Tarjeta Débito",
  "categorias": [
    { "idCategoria": 1, "nombreCategoria": "Mercado", "montoAsignado": 100000 },
    { "idCategoria": 2, "nombreCategoria": "Hogar", "montoAsignado": 50000 }
  ]
}
```

## 6. Resumen – `/api/resumen`

| Verbo | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/resumen` | Totales generales y por categoría y cuenta | 200 |

```json
{
  "totalMovimientos": 3,
  "montoTotal": 290000,
  "porCategoria": [ { "idCategoria": 1, "nombreCategoria": "Mercado", "total": 250000 } ],
  "porCuenta": [ { "idCuenta": 1, "nombreCuenta": "Ahorros BBVA", "cantidadMovimientos": 3, "total": 290000 } ]
}
```

---

## Cómo probar

Con la API en ejecución, abrir `http://localhost:8080/`: la página lista los servicios y tiene un probador con
ejemplos para cada uno. También sirve `peticiones.http` (extensión *REST Client* de VS Code).
Para PUT, PATCH y DELETE hay que cambiar `CAMBIE_POR_SU_ID` por el id de un registro creado para la prueba.

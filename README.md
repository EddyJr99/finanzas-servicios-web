# Servicios web del proyecto – SGFP

**Evidencia:** GA7-220501096-AA5-EV03 – Diseño y desarrollo de servicios web – proyecto
**Aprendiz:** Eddy Alexander Altamar Angulo · SENA ADSO · Ficha 3336221
**Tecnología:** Java 17 · Spring Boot 3.3.5 · Spring JDBC · MySQL 8 · Bean Validation

Servicios REST del Sistema de Gestión de Finanzas Personales (SGFP): usuarios, cuentas, medios de pago,
categorías, movimientos y resumen. Cualquier cliente (web, móvil o de escritorio) puede consumirlos por
HTTP/JSON. Un movimiento puede repartirse entre varias categorías (tabla `detalle_categoria`).

## Requisitos
- Java 17 (`java -version`)
- MySQL en ejecución (Laragon) con la base `sgfp`. Si no existe, ejecutar `sql/sgfp_completo.sql`.
- Internet la primera vez (Maven descarga las dependencias). **No hay que instalar Maven**: viene en `tools/`.

## Ejecución
- Windows: doble clic en `ejecutar_api.bat` (o `ejecutar_api.bat 9090` para otro puerto).
- Linux/Mac: `./ejecutar_api.sh`

Cuando aparezca `Started FinanzasApiApplication`, abrir **http://localhost:8080/**: sale el login del SGFP.
Desde ahí se crea la cuenta (Regístrate), se inicia sesión y se entra al dashboard. La documentación y el probador de la API están en **http://localhost:8080/api.html**.

Conexión por defecto: `root` sin contraseña en `localhost:3306/sgfp`. Para cambiarla se usan las
variables de entorno `SGFP_DB_URL`, `SGFP_DB_USER`, `SGFP_DB_PASSWORD` (o editar `application.properties`).

## Servicios
Documentación completa de cada servicio (rutas, campos, validaciones, ejemplos y códigos): **`DOCUMENTACION_API.md`**.

| Recurso | Ruta base | Operaciones |
|---|---|---|
| Acceso | `/api/auth` | POST registro, POST login |
| Usuarios | `/api/usuarios` | GET, GET por id, POST, PUT, DELETE |
| Cuentas | `/api/cuentas` | GET, GET por id, POST, PUT, DELETE |
| Medios de pago | `/api/medios-pago` | GET, GET por id, POST, PUT, DELETE |
| Categorías | `/api/categorias` | GET, GET por id, POST, PUT, DELETE |
| Movimientos | `/api/movimientos` | GET, GET por id, POST, PUT, PATCH, DELETE |
| Resumen | `/api/resumen` | GET |

Códigos usados: 200, 201, 204, 400, 404, 405, 409 y 500. Los errores siempre vienen en el mismo JSON.

## Estructura
```
src/main/java/co/edu/sena/finanzas/api/
  controlador/  un controlador por recurso                   (HTTP)
  servicio/     un servicio por recurso                       (reglas y transacciones)
  repositorio/  un repositorio por tabla + RepositorioBase     (SQL con JdbcTemplate)
  dto/          records de entrada y salida
  excepcion/    excepciones y ManejadorExcepciones (errores JSON)
src/main/resources/  application.properties, static/index.html (documentación + probador)
DOCUMENTACION_API.md · peticiones.http · ESTANDARES.md · sql/ · tools/ (Maven incluido)
```

## Aplicación web (front-end)
El front-end de la AA4 (login, registro y dashboard) quedó dentro de `src/main/resources/static` y ahora consume la API:
- `index.html` y `registro.html`: inicio de sesión y creación de cuenta (`/api/auth`).
- `dashboard.html`: ingresos, gastos, balance, historial y gasto por categoría del usuario; crear y eliminar movimientos.
- `cuentas.html`: cuentas, categorías (ingreso o gasto) y medios de pago.
- `api.html`: documentación y probador de los servicios.

## Despliegue con Docker en Render
El archivo `Dockerfile` (raíz del proyecto) compila la API con Maven y la ejecuta con Java 17.

- Local: `docker build -t sgfp-api .` y `docker run -p 8080:8080 -e SGFP_DB_URL=... sgfp-api`
- Render: *New → Web Service* → conectar el repositorio de GitHub → *Language: Docker* → crear.
  El Dockerfile activa el perfil `demo`: una base H2 en memoria con datos de ejemplo
  (usuario `demo@sgfp.com`, clave `Demo2026*`), porque Render no ofrece MySQL.
  Para usar una base MySQL en la nube: `SPRING_PROFILES_ACTIVE=default` y las variables
  `SGFP_DB_URL`, `SGFP_DB_USER` y `SGFP_DB_PASSWORD`. Render envía el puerto en `PORT` y la API lo usa.

## Pruebas
Desde el navegador en `http://localhost:8080/` (sección "Probar la API") o con `peticiones.http`
(extensión *REST Client* de VS Code).

## Solución de problemas
- *Communications link failure*: MySQL (Laragon) no está iniciado.
- *Unknown database 'sgfp'*: ejecutar `sql/sgfp_completo.sql`.
- *Port 8080 already in use*: `ejecutar_api.bat 9090`.
- *JAVA_HOME / java no reconocido*: instalar JDK 17 y reabrir la terminal.

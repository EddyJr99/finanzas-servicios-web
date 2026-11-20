-- Base de datos de demostracion (H2 en memoria, modo MySQL).
-- Se usa solo en Render (perfil "demo") para que la aplicacion publicada tenga datos.
-- En el equipo local se usa MySQL con sql/sgfp_completo.sql.

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario     INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    correo         VARCHAR(100) NOT NULL,
    fecha_registro DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS credenciales (
    id_usuario INT          NOT NULL PRIMARY KEY,
    clave_hash VARCHAR(255) NOT NULL,
    CONSTRAINT fk_credenciales_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);

CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta     INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_cuenta VARCHAR(100) NOT NULL,
    tipo_cuenta   VARCHAR(50)  NOT NULL,
    banco         VARCHAR(100) NOT NULL,
    id_usuario    INT          NOT NULL,
    CONSTRAINT fk_cuentas_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);

CREATE TABLE IF NOT EXISTS medios_pago (
    id_medio_pago      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_medio_pago  VARCHAR(100) NOT NULL,
    entidad_financiera VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria     INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL,
    tipo             VARCHAR(10)  NOT NULL DEFAULT 'GASTO'
);

CREATE TABLE IF NOT EXISTS movimientos (
    id_movimiento INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fecha         DATE          NOT NULL,
    descripcion   VARCHAR(150)  NOT NULL,
    monto_total   DECIMAL(12,2) NOT NULL,
    id_cuenta     INT           NOT NULL,
    id_medio_pago INT           NOT NULL,
    CONSTRAINT fk_movimientos_cuentas FOREIGN KEY (id_cuenta) REFERENCES cuentas (id_cuenta),
    CONSTRAINT fk_movimientos_medios_pago FOREIGN KEY (id_medio_pago) REFERENCES medios_pago (id_medio_pago)
);

CREATE TABLE IF NOT EXISTS detalle_categoria (
    id_movimiento  INT           NOT NULL,
    id_categoria   INT           NOT NULL,
    monto_asignado DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id_movimiento, id_categoria),
    CONSTRAINT fk_detalle_movimientos FOREIGN KEY (id_movimiento) REFERENCES movimientos (id_movimiento),
    CONSTRAINT fk_detalle_categorias FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
);

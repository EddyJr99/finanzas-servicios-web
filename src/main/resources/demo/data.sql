-- Datos de demostracion. Usuario: demo@sgfp.com  Contrasena: Demo2026*
INSERT INTO usuarios (nombre, correo, fecha_registro) VALUES ('Usuario Demo', 'demo@sgfp.com', '2026-11-01');
INSERT INTO credenciales (id_usuario, clave_hash) VALUES (1, 'pbkdf2$120000$GlCVIwJx95PwWoI5ZuYKdg==$4xmlJSF6dJwGKKXVfN+a5nj5djv4IitCh1vBOtwCvS4=');

INSERT INTO cuentas (nombre_cuenta, tipo_cuenta, banco, id_usuario) VALUES
    ('Ahorros BBVA', 'Ahorros', 'BBVA', 1),
    ('Nomina Bancolombia', 'Nomina', 'Bancolombia', 1);

INSERT INTO medios_pago (nombre_medio_pago, entidad_financiera) VALUES
    ('Tarjeta Debito', 'BBVA'),
    ('Efectivo', 'N/A'),
    ('Transferencia', 'Bancolombia');

INSERT INTO categorias (nombre_categoria, tipo) VALUES
    ('Salario', 'INGRESO'),
    ('Mercado', 'GASTO'),
    ('Transporte', 'GASTO'),
    ('Hogar', 'GASTO'),
    ('Salud', 'GASTO'),
    ('Entretenimiento', 'GASTO');

INSERT INTO movimientos (fecha, descripcion, monto_total, id_cuenta, id_medio_pago) VALUES
    ('2026-11-01', 'Pago de nomina', 2400000, 2, 3),
    ('2026-11-03', 'Recarga tarjeta de bus', 80000, 2, 2),
    ('2026-11-05', 'Compra en supermercado', 150000, 1, 1),
    ('2026-11-08', 'Consulta medica particular', 60000, 1, 1),
    ('2026-11-12', 'Cine con amigos', 45000, 1, 1),
    ('2026-11-15', 'Servicios publicos', 210000, 2, 3);

INSERT INTO detalle_categoria (id_movimiento, id_categoria, monto_asignado) VALUES
    (1, 1, 2400000),
    (2, 3, 80000),
    (3, 2, 100000),
    (3, 4, 50000),
    (4, 5, 60000),
    (5, 6, 45000),
    (6, 4, 210000);

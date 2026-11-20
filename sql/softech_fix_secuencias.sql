-- Re-sincroniza los contadores (SERIAL) con el MAX(id) real de cada tabla.
-- Ejecutar si al guardar sale "Ya existe un registro con esos datos" o error 500
-- (pasa cuando los datos se cargaron con ids explícitos). Es seguro repetirlo.
SELECT setval(pg_get_serial_sequence('usuario','id_usuario'),   COALESCE((SELECT MAX(id_usuario) FROM usuario),1));
SELECT setval(pg_get_serial_sequence('clientes','id_cliente'),  COALESCE((SELECT MAX(id_cliente) FROM clientes),1));
SELECT setval(pg_get_serial_sequence('tecnicos','id_tecnico'),  COALESCE((SELECT MAX(id_tecnico) FROM tecnicos),1));
SELECT setval(pg_get_serial_sequence('equipo','id_equipo'),     COALESCE((SELECT MAX(id_equipo) FROM equipo),1));
SELECT setval(pg_get_serial_sequence('servicios','id_servicio'),COALESCE((SELECT MAX(id_servicio) FROM servicios),1));
SELECT setval(pg_get_serial_sequence('facturacion','id_factura'),COALESCE((SELECT MAX(id_factura) FROM facturacion),1));
SELECT setval(pg_get_serial_sequence('detalles_facturacion','id_detalles_facturacion'),COALESCE((SELECT MAX(id_detalles_facturacion) FROM detalles_facturacion),1));
SELECT setval(pg_get_serial_sequence('audit_logs','id_audit'),  COALESCE((SELECT MAX(id_audit) FROM audit_logs),1));

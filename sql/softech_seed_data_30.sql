-- =====================================================================
-- SOFTECH - DATOS DE PRUEBA (30 registros por tabla) - PostgreSQL
-- Ejecutar DESPUÉS de crear las tablas (softech_database_postgres.sql)
-- Alineado al Diagrama Relacional SOFTEC corregido
-- =====================================================================

-- Limpiar datos previos (opcional, mantiene la estructura de tablas)
TRUNCATE TABLE detalles_facturacion, facturacion, servicio_tecnico, servicios, equipo, tecnicos, clientes, usuario RESTART IDENTITY CASCADE;

-- ---------------------------------------------------------------------
-- USUARIO (30)
-- ---------------------------------------------------------------------
INSERT INTO usuario (identificacion, nombre_usuario, telefono, email, contrasena, tipo_usuario) VALUES
('U001', 'Juan Pérez', '3105667265', 'juan.perez1@softech.com', '$2y$10$g6tudQYTUBEgz9T3g3wSeOma0c0lLegwdu9ijUPK4ywEmefnbmwTm', 'Cliente'),
('U002', 'María Gómez', '3101109031', 'maria.gomez2@softech.com', '$2y$10$Is/4G.o3aAfpvFVaM3xP7.PHAfARuIc6w9rTXWN.eWBNAdbiVhsj.', 'Cliente'),
('U003', 'Carlos Rodríguez', '3103678638', 'carlos.rodriguez3@softech.com', '$2y$10$Qx2xYGzmJER2JHKpOhatP.LN7TGreC27Dhl5DA6IBAQ7YICV.u2CG', 'Tecnico'),
('U004', 'Ana Martínez', '3108090293', 'ana.martinez4@softech.com', '$2y$10$lkuXgzL8xJ9y50p1jMqXKumB2axbX.iQOrk2ySillJreYV8LC4Ff2', 'Cliente'),
('U005', 'Luis López', '3106708456', 'luis.lopez5@softech.com', '$2y$10$HvnO5cHVZjFZTVlYIjYnW.Owz8mI28QQJaMZAJ7S0JcyqMnylcoES', 'Cliente'),
('U006', 'Laura García', '3105661907', 'laura.garcia6@softech.com', '$2y$10$hpMY4P8ixHIhOnNn31iijO0pxOIUykWNUAdUuRfRLhDMpuqHtuinC', 'Tecnico'),
('U007', 'Andrés Hernández', '3103608513', 'andres.hernandez7@softech.com', '$2y$10$wci78i17DVAyArKNXog16uodIrMQS.gPbRflLXpw.qNHenUDrwOYW', 'Tecnico'),
('U008', 'Camila Torres', '3104612365', 'camila.torres8@softech.com', '$2y$10$ty.TotkP1roAzChR0HTk8OoOWjoSvyeY5UNWaq.rhm8jSWqPlKIkm', 'Cliente'),
('U009', 'Diego Ramírez', '3106647119', 'diego.ramirez9@softech.com', '$2y$10$OMY/LsfI5VrVaZIEngc1mual.s.ukmqotuVA270OxudDnq/XJNJVi', 'Tecnico'),
('U010', 'Valentina Vargas', '3102714803', 'valentina.vargas10@softech.com', '$2y$10$eDN8HItOWgXQoN9vsRLnIOglfzRNx5UXBRq64zLrw2iTISdciMgOK', 'Cliente'),
('U011', 'Felipe Castro', '3102556017', 'felipe.castro11@softech.com', '$2y$10$jMULygrzZKPncx67Ogtfo.uacw9kR98hqQTnHl2yVc2ySm5MkFsj2', 'Tecnico'),
('U012', 'Daniela Rojas', '3107374122', 'daniela.rojas12@softech.com', '$2y$10$AcZHRGXOANbvu5Hu0aj1Zejzbxy7uqjLpsB1XCmrBNVQFLt9zeNKC', 'Cliente'),
('U013', 'Santiago Morales', '3102622631', 'santiago.morales13@softech.com', '$2y$10$I3xCeEFHBhrfXquXq/8rV.7Czolq6rgp4Vrb339Ys6ngBm.7VMPWK', 'Cliente'),
('U014', 'Isabella Ortiz', '3107022674', 'isabella.ortiz14@softech.com', '$2y$10$DFxvQRidRJQKLrATg7UHE.duS1I.uEIbO1Svayzx36egnB0YzCuPK', 'Cliente'),
('U015', 'Miguel Suárez', '3106770619', 'miguel.suarez15@softech.com', '$2y$10$gnRFQbEt8rYQnrbpZj9p2.hItgrIzhY7cCOSL/SatEEBe6yTMAJSq', 'Cliente'),
('U016', 'Sofía Jiménez', '3105437923', 'sofia.jimenez16@softech.com', '$2y$10$gwaBiAlWLQ0kAavxnMFLres8ZgB3ZmmDiSGKJOkJljm/jD0tfnEd.', 'Cliente'),
('U017', 'Jorge Mejía', '3101728977', 'jorge.mejia17@softech.com', '$2y$10$69U0Tv4UEDY0LkkdI9m88.0X8YZNGC/BTN/tSU8ZmLD.uEa9gtaPq', 'Recepcionista'),
('U018', 'Paula Cárdenas', '3108707870', 'paula.cardenas18@softech.com', '$2y$10$JDNFhk0BfaRx3f2SFrWqeeL3H97zm/qxMf6Xu6kMy0nFE4gdWIFmK', 'Cliente'),
('U019', 'Alejandro Rincón', '3109996414', 'alejandro.rincon19@softech.com', '$2y$10$n.77W5Yvasd03vyk9XA/E.uY7P7Wjvgge1P6CGoAYDoyGQ.ellde2', 'Cliente'),
('U020', 'Natalia Delgado', '3103094235', 'natalia.delgado20@softech.com', '$2y$10$1LjUKUtWX2YJOUGWd2.reu.My3iD498yLiX8vliJCLzPEd.OjYFXm', 'Tecnico'),
('U021', 'Ricardo Guerrero', '3107350753', 'ricardo.guerrero21@softech.com', '$2y$10$fI9oMYdFYIIyOkyObxVLzuNF3.nChBitplZk9zmk1iui6n7UU0Xpm', 'Cliente'),
('U022', 'Carolina Salazar', '3102322047', 'carolina.salazar22@softech.com', '$2y$10$m0emWlIzDT72hTj5EOOJl.t7TVxrPzNTeY9hlUyYewI5YDuo/4R7W', 'Cliente'),
('U023', 'Mateo Peña', '3105918715', 'mateo.pena23@softech.com', '$2y$10$W3Wn4T121dBraX94KnT1reiI6xR/c68u2OvzGr6/dB5u64.4kka4u', 'Tecnico'),
('U024', 'Gabriela Cardona', '3107067228', 'gabriela.cardona24@softech.com', '$2y$10$gZbdFI1r8vGm0LMR4T/mTe10c6N.6u5ZZ5xbtxfHtrbaZwcbiubde', 'Cliente'),
('U025', 'David Muñoz', '3104226067', 'david.munoz25@softech.com', '$2y$10$NdQ1VARROnDsXWrSfKA4VOErb.MENMM35vzpTGYO1Td5dlPR8DWli', 'Tecnico'),
('U026', 'Melissa Reyes', '3102166941', 'melissa.reyes26@softech.com', '$2y$10$EbxZHjsMatLA8vJQ/lyMvuDWpfpqOruSEPR7MFfj8hnDs2HT86w/O', 'Tecnico'),
('U027', 'Fernando Aguirre', '3101768805', 'fernando.aguirre27@softech.com', '$2y$10$iAHnfd/74vkcWMJVhQh3dOlnqpFZuTfWBMqS.N3HkLgWgs5VUaLia', 'Cliente'),
('U028', 'Adriana Serrano', '3104823498', 'adriana.serrano28@softech.com', '$2y$10$2.iZKSsdF.DwGod79RVMJeBfbFY.B1/NWH1vx.FJBWAQ1WFPkKD7G', 'Administrador'),
('U029', 'Sebastián Cifuentes', '3105855124', 'sebastian.cifuentes29@softech.com', '$2y$10$8rEQxQaSg1uUOzYvq9OlquNq36EgbIt9JQedGJNSR3uR1ovX6Gxxi', 'Tecnico'),
('U030', 'Lorena Bermúdez', '3102338687', 'lorena.bermudez30@softech.com', '$2y$10$qnIyXN/u.GLQlkjIRhrh0.lAs5ANh1nJFpaz7zVgKDyXQeb6PU90e', 'Cliente');

-- ---------------------------------------------------------------------
-- CLIENTES (uno por cada usuario de tipo 'Cliente')
-- ---------------------------------------------------------------------
INSERT INTO clientes (id_usuario, nombre_cliente) VALUES
(1, 'Juan Pérez'),
(2, 'María Gómez'),
(4, 'Ana Martínez'),
(5, 'Luis López'),
(8, 'Camila Torres'),
(10, 'Valentina Vargas'),
(12, 'Daniela Rojas'),
(13, 'Santiago Morales'),
(14, 'Isabella Ortiz'),
(15, 'Miguel Suárez'),
(16, 'Sofía Jiménez'),
(18, 'Paula Cárdenas'),
(19, 'Alejandro Rincón'),
(21, 'Ricardo Guerrero'),
(22, 'Carolina Salazar'),
(24, 'Gabriela Cardona'),
(27, 'Fernando Aguirre'),
(30, 'Lorena Bermúdez');

-- ---------------------------------------------------------------------
-- TECNICOS (uno por cada usuario de tipo 'Tecnico')
-- ---------------------------------------------------------------------
INSERT INTO tecnicos (id_usuario, especialidad, turno, estado_disponibilidad) VALUES
(3, 'Hardware', 'Mañana', 'Disponible'),
(6, 'Servidores', 'Noche', 'Ocupado'),
(7, 'Hardware', 'Tarde', 'Disponible'),
(9, 'Reparacion_Moviles', 'Tarde', 'Ocupado'),
(11, 'Reparacion_Moviles', 'Noche', 'Disponible'),
(20, 'Reparacion_Moviles', 'Mañana', 'Disponible'),
(23, 'Hardware', 'Mañana', 'Disponible'),
(25, 'Redes', 'Noche', 'Ocupado'),
(26, 'Hardware', 'Noche', 'Disponible'),
(29, 'Software', 'Mañana', 'Ocupado');

-- ---------------------------------------------------------------------
-- EQUIPO (30)
-- ---------------------------------------------------------------------
INSERT INTO equipo (id_cliente, tipo_equipo, marca, modelo, numero_serie, estado_equipo) VALUES
(11, 'Servidor', 'Asus', 'Modelo-167', 'SN-AS-0001', 'En_Diagnostico'),
(11, 'PC Escritorio', 'Epson', 'Modelo-505', 'SN-EP-0002', 'Sin_Reparacion'),
(15, 'PC Escritorio', 'Asus', 'Modelo-242', 'SN-AS-0003', 'En_Diagnostico'),
(18, 'Monitor', 'Asus', 'Modelo-864', 'SN-AS-0004', 'Entregado'),
(14, 'Monitor', 'Samsung', 'Modelo-470', 'SN-SA-0005', 'En_Diagnostico'),
(5, 'Monitor', 'Epson', 'Modelo-193', 'SN-EP-0006', 'Recibido'),
(4, 'PC Escritorio', 'Lenovo', 'Modelo-911', 'SN-LE-0007', 'Sin_Reparacion'),
(14, 'Monitor', 'Dell', 'Modelo-494', 'SN-DE-0008', 'Reparado'),
(15, 'Monitor', 'Asus', 'Modelo-666', 'SN-AS-0009', 'Recibido'),
(4, 'Tablet', 'Asus', 'Modelo-887', 'SN-AS-0010', 'Sin_Reparacion'),
(11, 'Portátil', 'Asus', 'Modelo-545', 'SN-AS-0011', 'En_Diagnostico'),
(15, 'Portátil', 'Asus', 'Modelo-612', 'SN-AS-0012', 'En_Diagnostico'),
(17, 'Portátil', 'Asus', 'Modelo-961', 'SN-AS-0013', 'Sin_Reparacion'),
(17, 'Monitor', 'Apple', 'Modelo-256', 'SN-AP-0014', 'En_Reparacion'),
(6, 'Monitor', 'HP', 'Modelo-713', 'SN-HP-0015', 'En_Reparacion'),
(16, 'Portátil', 'Dell', 'Modelo-471', 'SN-DE-0016', 'En_Reparacion'),
(8, 'Portátil', 'Apple', 'Modelo-999', 'SN-AP-0017', 'Entregado'),
(3, 'Portátil', 'Epson', 'Modelo-935', 'SN-EP-0018', 'Recibido'),
(18, 'PC Escritorio', 'Lenovo', 'Modelo-775', 'SN-LE-0019', 'Reparado'),
(18, 'PC Escritorio', 'Asus', 'Modelo-640', 'SN-AS-0020', 'Entregado'),
(14, 'PC Escritorio', 'Apple', 'Modelo-830', 'SN-AP-0021', 'En_Reparacion'),
(13, 'Tablet', 'Acer', 'Modelo-548', 'SN-AC-0022', 'Entregado'),
(15, 'Portátil', 'Apple', 'Modelo-330', 'SN-AP-0023', 'Recibido'),
(11, 'Portátil', 'Apple', 'Modelo-702', 'SN-AP-0024', 'En_Diagnostico'),
(1, 'Portátil', 'HP', 'Modelo-334', 'SN-HP-0025', 'Recibido'),
(2, 'Impresora', 'Dell', 'Modelo-626', 'SN-DE-0026', 'En_Diagnostico'),
(9, 'Tablet', 'Epson', 'Modelo-319', 'SN-EP-0027', 'Entregado'),
(5, 'Tablet', 'Epson', 'Modelo-348', 'SN-EP-0028', 'Reparado'),
(14, 'PC Escritorio', 'Dell', 'Modelo-199', 'SN-DE-0029', 'Sin_Reparacion'),
(14, 'Impresora', 'Samsung', 'Modelo-520', 'SN-SA-0030', 'Reparado');

-- ---------------------------------------------------------------------
-- SERVICIOS (30)
-- ---------------------------------------------------------------------
INSERT INTO servicios (id_equipo, id_usuario, fecha_ingreso_equipo, fecha_entrega_equipo, problema_reportado, diagnostico_realizado, estado_servicio, descripcion_problema, prioridad, tipo_servicio) VALUES
(1, 28, '2026-06-07', NULL, 'Lentitud general', NULL, 'Pendiente', 'Cliente reporta: lentitud general.', 'Urgente', 'Diagnostico'),
(2, 26, '2026-06-14', NULL, 'Sobrecalentamiento', 'Diagnóstico: sobrecalentamiento - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: sobrecalentamiento.', 'Media', 'Garantia'),
(3, 15, '2026-06-18', NULL, 'Batería no carga', 'Diagnóstico: batería no carga - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: batería no carga.', 'Alta', 'Instalacion'),
(4, 8, '2026-06-10', '2026-06-12', 'No imprime correctamente', 'Diagnóstico: no imprime correctamente - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: no imprime correctamente.', 'Baja', 'Garantia'),
(5, 27, '2026-06-02', NULL, 'Lentitud general', 'Diagnóstico: lentitud general - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: lentitud general.', 'Media', 'Instalacion'),
(6, 16, '2026-08-01', '2026-08-02', 'Sobrecalentamiento', 'Diagnóstico: sobrecalentamiento - revisado por técnico asignado.', 'Entregado', 'Cliente reporta: sobrecalentamiento.', 'Media', 'Instalacion'),
(7, 1, '2026-07-20', '2026-07-25', 'Pantalla azul', 'Diagnóstico: pantalla azul - revisado por técnico asignado.', 'Entregado', 'Cliente reporta: pantalla azul.', 'Urgente', 'Garantia'),
(8, 22, '2026-08-02', NULL, 'No conecta a internet', 'Diagnóstico: no conecta a internet - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: no conecta a internet.', 'Alta', 'Mantenimiento'),
(9, 2, '2026-08-14', NULL, 'Errores de sistema operativo', NULL, 'Pendiente', 'Cliente reporta: errores de sistema operativo.', 'Alta', 'Reparacion'),
(10, 2, '2026-08-14', '2026-08-23', 'No imprime correctamente', 'Diagnóstico: no imprime correctamente - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: no imprime correctamente.', 'Media', 'Reparacion'),
(11, 17, '2026-06-11', NULL, 'No conecta a internet', NULL, 'Pendiente', 'Cliente reporta: no conecta a internet.', 'Baja', 'Mantenimiento'),
(12, 13, '2026-06-16', NULL, 'Virus detectado', 'Diagnóstico: virus detectado - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: virus detectado.', 'Baja', 'Garantia'),
(13, 3, '2026-07-24', '2026-08-02', 'Virus detectado', 'Diagnóstico: virus detectado - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: virus detectado.', 'Alta', 'Diagnostico'),
(14, 7, '2026-07-11', '2026-07-18', 'Sobrecalentamiento', 'Diagnóstico: sobrecalentamiento - revisado por técnico asignado.', 'Finalizado', 'Cliente reporta: sobrecalentamiento.', 'Media', 'Diagnostico'),
(15, 15, '2026-07-11', NULL, 'Lentitud general', NULL, 'Pendiente', 'Cliente reporta: lentitud general.', 'Urgente', 'Garantia'),
(16, 19, '2026-06-13', '2026-06-17', 'Lentitud general', 'Diagnóstico: lentitud general - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: lentitud general.', 'Alta', 'Mantenimiento'),
(17, 30, '2026-07-15', NULL, 'Lentitud general', 'Diagnóstico: lentitud general - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: lentitud general.', 'Alta', 'Diagnostico'),
(18, 6, '2026-07-27', '2026-08-06', 'Errores de sistema operativo', 'Diagnóstico: errores de sistema operativo - revisado por técnico asignado.', 'Finalizado', 'Cliente reporta: errores de sistema operativo.', 'Baja', 'Garantia'),
(19, 10, '2026-06-14', '2026-06-16', 'No conecta a internet', 'Diagnóstico: no conecta a internet - revisado por técnico asignado.', 'Finalizado', 'Cliente reporta: no conecta a internet.', 'Baja', 'Garantia'),
(20, 5, '2026-07-05', '2026-07-09', 'Pantalla azul', 'Diagnóstico: pantalla azul - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: pantalla azul.', 'Alta', 'Mantenimiento'),
(21, 22, '2026-07-04', '2026-07-09', 'Errores de sistema operativo', 'Diagnóstico: errores de sistema operativo - revisado por técnico asignado.', 'Entregado', 'Cliente reporta: errores de sistema operativo.', 'Baja', 'Reparacion'),
(22, 21, '2026-07-25', NULL, 'Pantalla azul', NULL, 'Pendiente', 'Cliente reporta: pantalla azul.', 'Baja', 'Diagnostico'),
(23, 25, '2026-06-17', NULL, 'Pantalla azul', 'Diagnóstico: pantalla azul - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: pantalla azul.', 'Urgente', 'Garantia'),
(24, 23, '2026-07-25', NULL, 'Errores de sistema operativo', NULL, 'Pendiente', 'Cliente reporta: errores de sistema operativo.', 'Baja', 'Reparacion'),
(25, 29, '2026-06-20', NULL, 'Errores de sistema operativo', NULL, 'Pendiente', 'Cliente reporta: errores de sistema operativo.', 'Alta', 'Garantia'),
(26, 18, '2026-06-19', NULL, 'Batería no carga', 'Diagnóstico: batería no carga - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: batería no carga.', 'Baja', 'Diagnostico'),
(27, 12, '2026-06-06', NULL, 'Ruido extraño', 'Diagnóstico: ruido extraño - revisado por técnico asignado.', 'En_Proceso', 'Cliente reporta: ruido extraño.', 'Media', 'Reparacion'),
(28, 12, '2026-08-11', '2026-08-14', 'Batería no carga', 'Diagnóstico: batería no carga - revisado por técnico asignado.', 'Cancelado', 'Cliente reporta: batería no carga.', 'Media', 'Mantenimiento'),
(29, 26, '2026-06-23', NULL, 'Batería no carga', NULL, 'Pendiente', 'Cliente reporta: batería no carga.', 'Media', 'Diagnostico'),
(30, 26, '2026-07-23', '2026-07-26', 'Sobrecalentamiento', 'Diagnóstico: sobrecalentamiento - revisado por técnico asignado.', 'Finalizado', 'Cliente reporta: sobrecalentamiento.', 'Baja', 'Instalacion');

-- ---------------------------------------------------------------------
-- SERVICIO_TECNICO (30)
-- ---------------------------------------------------------------------
INSERT INTO servicio_tecnico (id_servicio, id_tecnico, fecha_asignacion) VALUES
(1, 1, '2026-07-31'),
(2, 4, '2026-06-26'),
(3, 8, '2026-07-15'),
(4, 5, '2026-06-30'),
(5, 4, '2026-06-04'),
(6, 4, '2026-07-22'),
(7, 6, '2026-07-06'),
(8, 2, '2026-07-06'),
(9, 6, '2026-08-05'),
(10, 7, '2026-08-08'),
(11, 6, '2026-06-04'),
(12, 2, '2026-07-04'),
(13, 3, '2026-08-14'),
(14, 5, '2026-06-05'),
(15, 2, '2026-07-26'),
(16, 6, '2026-07-11'),
(17, 7, '2026-08-05'),
(18, 2, '2026-07-20'),
(19, 10, '2026-06-25'),
(20, 5, '2026-06-06'),
(21, 7, '2026-06-01'),
(22, 9, '2026-08-08'),
(23, 4, '2026-07-17'),
(24, 7, '2026-06-09'),
(25, 6, '2026-07-11'),
(26, 2, '2026-07-09'),
(27, 9, '2026-07-10'),
(28, 7, '2026-07-12'),
(29, 7, '2026-07-08'),
(30, 9, '2026-06-17');

-- ---------------------------------------------------------------------
-- FACTURACION (30)
-- ---------------------------------------------------------------------
INSERT INTO facturacion (id_servicio, id_cliente, numero_factura, fecha_factura, fecha_pago, tipo_comprobante, total_pagar, metodo_pago, estado_pago) VALUES
(1, 7, 'FAC-0001', '2026-07-24', NULL, 'Nota_Credito', 755272.75, 'Tarjeta_Debito', 'Parcial'),
(2, 13, 'FAC-0002', '2026-08-10', NULL, 'Factura', 277906.21, 'Tarjeta_Debito', 'Anulado'),
(3, 11, 'FAC-0003', '2026-07-30', NULL, 'Recibo', 381619.85, 'Tarjeta_Debito', 'Anulado'),
(4, 6, 'FAC-0004', '2026-06-11', NULL, 'Recibo', 436589.01, 'PSE', 'Parcial'),
(5, 3, 'FAC-0005', '2026-07-01', '2026-07-02', 'Nota_Credito', 282814.6, 'Tarjeta_Debito', 'Pagado'),
(6, 2, 'FAC-0006', '2026-07-02', NULL, 'Recibo', 508455.48, 'Efectivo', 'Anulado'),
(7, 14, 'FAC-0007', '2026-08-13', NULL, 'Factura', 588759.44, 'Transferencia', 'Anulado'),
(8, 13, 'FAC-0008', '2026-07-02', NULL, 'Factura', 542043.51, 'Efectivo', 'Pendiente'),
(9, 14, 'FAC-0009', '2026-06-29', NULL, 'Factura', 653082.39, 'PSE', 'Anulado'),
(10, 2, 'FAC-0010', '2026-08-11', NULL, 'Factura', 738102.51, 'Efectivo', 'Anulado'),
(11, 5, 'FAC-0011', '2026-07-30', NULL, 'Nota_Credito', 448348.91, 'PSE', 'Parcial'),
(12, 15, 'FAC-0012', '2026-08-04', NULL, 'Recibo', 672926.88, 'PSE', 'Anulado'),
(13, 6, 'FAC-0013', '2026-07-31', NULL, 'Recibo', 244393.66, 'Tarjeta_Debito', 'Parcial'),
(14, 17, 'FAC-0014', '2026-08-02', NULL, 'Nota_Credito', 229434.4, 'Transferencia', 'Pendiente'),
(15, 10, 'FAC-0015', '2026-07-01', NULL, 'Recibo', 301885.65, 'PSE', 'Pendiente'),
(16, 5, 'FAC-0016', '2026-06-20', '2026-06-22', 'Factura', 337276.47, 'Tarjeta_Debito', 'Pagado'),
(17, 14, 'FAC-0017', '2026-07-23', NULL, 'Recibo', 456958.36, 'Transferencia', 'Pendiente'),
(18, 7, 'FAC-0018', '2026-07-24', NULL, 'Recibo', 728628.86, 'PSE', 'Pendiente'),
(19, 13, 'FAC-0019', '2026-08-01', NULL, 'Factura', 757056.24, 'Tarjeta_Credito', 'Anulado'),
(20, 14, 'FAC-0020', '2026-08-08', '2026-08-16', 'Nota_Credito', 600975.55, 'PSE', 'Pagado'),
(21, 8, 'FAC-0021', '2026-07-05', NULL, 'Recibo', 414231.5, 'Transferencia', 'Parcial'),
(22, 13, 'FAC-0022', '2026-06-22', NULL, 'Recibo', 739656.22, 'PSE', 'Pendiente'),
(23, 13, 'FAC-0023', '2026-08-15', NULL, 'Nota_Credito', 547249.82, 'Efectivo', 'Anulado'),
(24, 5, 'FAC-0024', '2026-07-30', NULL, 'Factura', 87713.48, 'Transferencia', 'Parcial'),
(25, 7, 'FAC-0025', '2026-07-29', NULL, 'Recibo', 303125.41, 'Transferencia', 'Parcial'),
(26, 14, 'FAC-0026', '2026-07-03', NULL, 'Factura', 402730.13, 'PSE', 'Pendiente'),
(27, 12, 'FAC-0027', '2026-06-29', NULL, 'Nota_Credito', 101463.78, 'Efectivo', 'Pendiente'),
(28, 8, 'FAC-0028', '2026-06-26', '2026-07-04', 'Factura', 516001.74, 'Tarjeta_Debito', 'Pagado'),
(29, 4, 'FAC-0029', '2026-08-12', NULL, 'Factura', 398775.49, 'Tarjeta_Credito', 'Parcial'),
(30, 6, 'FAC-0030', '2026-06-15', NULL, 'Factura', 773540.58, 'Efectivo', 'Pendiente');

-- ---------------------------------------------------------------------
-- DETALLES_FACTURACION (30) - NUEVA TABLA
-- ---------------------------------------------------------------------
INSERT INTO detalles_facturacion (id_facturacion, descripcion, cantidad, precio, total) VALUES
(1, 'Limpieza interna', 3, 141922.51, 425767.53),
(2, 'Cambio de batería', 2, 189464.71, 378929.42),
(3, 'Repuesto - disco SSD', 1, 126577.62, 126577.62),
(4, 'Cambio de teclado', 1, 38342.23, 38342.23),
(5, 'Limpieza interna', 3, 128074.09, 384222.27),
(6, 'Mano de obra', 3, 160874.36, 482623.08),
(7, 'Cambio de pantalla', 3, 97108.04, 291324.12),
(8, 'Cambio de pantalla', 1, 111072.94, 111072.94),
(9, 'Cambio de pantalla', 1, 172923.61, 172923.61),
(10, 'Instalación de software', 1, 98034.21, 98034.21),
(11, 'Cambio de pantalla', 3, 180483.5, 541450.5),
(12, 'Instalación de software', 3, 47538.41, 142615.23),
(13, 'Repuesto - memoria RAM', 3, 113914.41, 341743.23),
(14, 'Cambio de teclado', 2, 130864.22, 261728.44),
(15, 'Formateo e instalación de SO', 2, 103677.28, 207354.56),
(16, 'Soporte remoto', 3, 68311.2, 204933.6),
(17, 'Repuesto - disco SSD', 1, 70207.08, 70207.08),
(18, 'Instalación de software', 1, 155085.73, 155085.73),
(19, 'Revisión de red', 3, 140273.92, 420821.76),
(20, 'Cambio de pantalla', 1, 108975.17, 108975.17),
(21, 'Cambio de pantalla', 1, 107758.99, 107758.99),
(22, 'Cambio de pantalla', 2, 81260.99, 162521.98),
(23, 'Revisión de red', 3, 178419.32, 535257.96),
(24, 'Formateo e instalación de SO', 1, 112992.91, 112992.91),
(25, 'Repuesto - disco SSD', 1, 63444.09, 63444.09),
(26, 'Cambio de batería', 2, 119931.72, 239863.44),
(27, 'Repuesto - disco SSD', 3, 105696.76, 317090.28),
(28, 'Soporte remoto', 2, 100671.42, 201342.84),
(29, 'Diagnóstico técnico', 1, 72962.4, 72962.4),
(30, 'Cambio de batería', 3, 63794.93, 191384.79);

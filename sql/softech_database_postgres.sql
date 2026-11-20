-- =====================================================================
-- BASE DE DATOS: SOFTECH
-- Sistema de gestión de servicio técnico (usuarios, clientes, equipos,
-- técnicos, servicios y facturación)
-- Motor: PostgreSQL 14+
-- Versión corregida y alineada al Diagrama Relacional SOFTEC
-- =====================================================================
-- NOTA: si ya creaste y estás conectado a la base "softech_db" en
-- pgAdmin (como en tu captura), NO ejecutes las líneas de CREATE
-- DATABASE; simplemente corre desde "DROP TABLE..." en adelante.
-- Si aún no la has creado, ejecuta primero estas 2 líneas conectado
-- a la base "postgres", y luego reconéctate a "softech_db" para
-- correr el resto del script.
-- =====================================================================

-- DROP DATABASE IF EXISTS softech_db;
-- CREATE DATABASE softech_db WITH ENCODING 'UTF8';

-- Una vez conectado a softech_db, limpiar tablas previas (por si reejecutas el script)
DROP TABLE IF EXISTS detalles_facturacion CASCADE;
DROP TABLE IF EXISTS facturacion CASCADE;
DROP TABLE IF EXISTS servicio_tecnico CASCADE;
DROP TABLE IF EXISTS servicios CASCADE;
DROP TABLE IF EXISTS tecnicos CASCADE;
DROP TABLE IF EXISTS equipo CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- ---------------------------------------------------------------------
-- TABLA: USUARIO
-- Cuenta de acceso al sistema. Un usuario puede "ser" un cliente y/o
-- un técnico (relación 1:1 opcional con CLIENTES y TECNICOS), o ser
-- un usuario administrativo/recepción que solo registra servicios.
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario      SERIAL PRIMARY KEY,
    identificacion  VARCHAR(20)  NOT NULL,
    nombre_usuario  VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20),
    email           VARCHAR(150) NOT NULL,
    contrasena      VARCHAR(255) NOT NULL,
    tipo_usuario    VARCHAR(30)  NOT NULL DEFAULT 'Cliente',
    CONSTRAINT uq_usuario_identificacion UNIQUE (identificacion),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_tipo
        CHECK (tipo_usuario IN ('Administrador','Recepcionista','Tecnico','Cliente'))
);

-- ---------------------------------------------------------------------
-- TABLA: CLIENTES
-- Relación: USUARIO (1) --ingresa como--> (0..1) CLIENTES
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
    id_cliente      SERIAL PRIMARY KEY,
    id_usuario      INT NOT NULL,
    nombre_cliente  VARCHAR(150) NOT NULL,
    CONSTRAINT uq_clientes_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_clientes_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- TABLA: TECNICOS
-- Relación: USUARIO (1) --puede ser--> (0..1) TECNICOS
-- ---------------------------------------------------------------------
CREATE TABLE tecnicos (
    id_tecnico            SERIAL PRIMARY KEY,
    id_usuario            INT NOT NULL,
    especialidad          VARCHAR(100),
    turno                 VARCHAR(30),
    estado_disponibilidad VARCHAR(30) NOT NULL DEFAULT 'Disponible',
    CONSTRAINT uq_tecnicos_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_tecnicos_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_tecnicos_turno
        CHECK (turno IS NULL OR turno IN ('Mañana','Tarde','Noche')),
    CONSTRAINT chk_tecnicos_disponibilidad
        CHECK (estado_disponibilidad IN ('Disponible','Ocupado','Inactivo'))
);

-- ---------------------------------------------------------------------
-- TABLA: EQUIPO
-- Relación: CLIENTES (1) --posee--> (0..N) EQUIPO
-- ---------------------------------------------------------------------
CREATE TABLE equipo (
    id_equipo       SERIAL PRIMARY KEY,
    id_cliente      INT NOT NULL,
    tipo_equipo     VARCHAR(50)  NOT NULL,
    marca           VARCHAR(50),
    modelo          VARCHAR(50),
    numero_serie    VARCHAR(100),
    estado_equipo   VARCHAR(30) NOT NULL DEFAULT 'Recibido',
    CONSTRAINT fk_equipo_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_equipo_estado
        CHECK (estado_equipo IN ('Recibido','En_Diagnostico','En_Reparacion','Reparado','Entregado','Sin_Reparacion'))
);

-- ---------------------------------------------------------------------
-- TABLA: SERVICIOS
-- Relaciones:
--   EQUIPO   (1) --recibe--->   (0..N) SERVICIOS
--   USUARIO  (1) --registra---> (0..N) SERVICIOS
-- ---------------------------------------------------------------------
CREATE TABLE servicios (
    id_servicio            SERIAL PRIMARY KEY,
    id_equipo              INT NOT NULL,
    id_usuario             INT NOT NULL,
    fecha_ingreso_equipo   DATE NOT NULL,
    fecha_entrega_equipo   DATE,
    problema_reportado     TEXT,
    diagnostico_realizado  TEXT,
    estado_servicio        VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
    descripcion_problema   TEXT,
    prioridad              VARCHAR(20) NOT NULL DEFAULT 'Media',
    tipo_servicio          VARCHAR(50),
    CONSTRAINT fk_servicios_equipo
        FOREIGN KEY (id_equipo) REFERENCES equipo(id_equipo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_servicios_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_servicios_estado
        CHECK (estado_servicio IN ('Pendiente','En_Proceso','Finalizado','Entregado','Cancelado')),
    CONSTRAINT chk_servicios_prioridad
        CHECK (prioridad IN ('Baja','Media','Alta','Urgente')),
    CONSTRAINT chk_servicios_fechas
        CHECK (fecha_entrega_equipo IS NULL OR fecha_entrega_equipo >= fecha_ingreso_equipo)
);

-- ---------------------------------------------------------------------
-- TABLA: SERVICIO_TECNICO (tabla puente N:M)
-- Relaciones:
--   SERVICIOS (1) --asigna--> (0..N) SERVICIO_TECNICO
--   TECNICOS  (1) --atiende--> (0..N) SERVICIO_TECNICO
-- ---------------------------------------------------------------------
CREATE TABLE servicio_tecnico (
    id_servicio       INT NOT NULL,
    id_tecnico        INT NOT NULL,
    fecha_asignacion  DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (id_servicio, id_tecnico),
    CONSTRAINT fk_st_servicio
        FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_st_tecnico
        FOREIGN KEY (id_tecnico) REFERENCES tecnicos(id_tecnico)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- TABLA: FACTURACION
-- Relaciones:
--   SERVICIOS (1) --genera--> (0..N) FACTURACION
--   CLIENTES  (1) --paga--->  (0..N) FACTURACION
-- ---------------------------------------------------------------------
CREATE TABLE facturacion (
    id_factura        SERIAL PRIMARY KEY,
    id_servicio       INT NOT NULL,
    id_cliente        INT NOT NULL,
    numero_factura    VARCHAR(50) NOT NULL,
    fecha_factura     DATE NOT NULL,
    fecha_pago        DATE,
    tipo_comprobante  VARCHAR(30) NOT NULL DEFAULT 'Factura',
    total_pagar       DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    metodo_pago       VARCHAR(30),
    estado_pago       VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT uq_facturacion_numero UNIQUE (numero_factura),
    CONSTRAINT fk_facturacion_servicio
        FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_facturacion_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_facturacion_estado
        CHECK (estado_pago IN ('Pendiente','Pagado','Parcial','Anulado')),
    CONSTRAINT chk_facturacion_total
        CHECK (total_pagar >= 0)
);

-- ---------------------------------------------------------------------
-- TABLA: DETALLES_FACTURACION
-- Relación: FACTURACION (1) --compone--> (0..N) DETALLES_FACTURACION
-- (NUEVA - agregada según el diagrama corregido)
-- ---------------------------------------------------------------------
CREATE TABLE detalles_facturacion (
    id_detalles_facturacion  SERIAL PRIMARY KEY,
    id_facturacion           INT NOT NULL,
    descripcion              VARCHAR(255) NOT NULL,
    cantidad                 INT NOT NULL DEFAULT 1,
    precio                   DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total                    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_detalles_facturacion
        FOREIGN KEY (id_facturacion) REFERENCES facturacion(id_factura)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_detalles_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalles_precio CHECK (precio >= 0),
    CONSTRAINT chk_detalles_total CHECK (total >= 0)
);

-- =====================================================================
-- ÍNDICES ADICIONALES (para acelerar búsquedas y joins frecuentes)
-- =====================================================================
CREATE INDEX idx_clientes_usuario          ON clientes(id_usuario);
CREATE INDEX idx_tecnicos_usuario          ON tecnicos(id_usuario);
CREATE INDEX idx_equipo_cliente            ON equipo(id_cliente);
CREATE INDEX idx_servicios_equipo          ON servicios(id_equipo);
CREATE INDEX idx_servicios_usuario         ON servicios(id_usuario);
CREATE INDEX idx_servicios_estado          ON servicios(estado_servicio);
CREATE INDEX idx_facturacion_servicio      ON facturacion(id_servicio);
CREATE INDEX idx_facturacion_cliente       ON facturacion(id_cliente);
CREATE INDEX idx_facturacion_estado        ON facturacion(estado_pago);
CREATE INDEX idx_detalles_facturacion_fac  ON detalles_facturacion(id_facturacion);

-- =====================================================================
-- Fin de la estructura. Los datos iniciales se cargan desde
-- softech_seed_data_30.sql.
-- =====================================================================

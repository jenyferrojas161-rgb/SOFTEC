CREATE TABLE IF NOT EXISTS audit_logs (
    id_audit BIGSERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(120) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_http VARCHAR(10),
    ruta VARCHAR(255),
    CONSTRAINT fk_audit_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_audit_usuario_fecha ON audit_logs(id_usuario, fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_audit_accion_fecha ON audit_logs(accion, fecha_hora DESC);

-- Notificaciones guardadas (el cliente las ve al entrar aunque no estuviera conectado)
CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion BIGSERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    mensaje VARCHAR(300) NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_notif_usuario ON notificaciones(id_usuario, leida, fecha_hora DESC);

-- Configuración del sistema (clave/valor). La edita solo un administrador desde configuracion.html
CREATE TABLE IF NOT EXISTS configuracion (
    clave VARCHAR(40) PRIMARY KEY,
    valor TEXT,
    actualizado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_por INT
);
INSERT INTO configuracion (clave, valor) VALUES ('nombre', 'SOFTEC') ON CONFLICT (clave) DO NOTHING;
INSERT INTO configuracion (clave, valor) VALUES ('correo', 'soporte@softec.com') ON CONFLICT (clave) DO NOTHING;
INSERT INTO configuracion (clave, valor) VALUES ('dias', '5') ON CONFLICT (clave) DO NOTHING;
INSERT INTO configuracion (clave, valor) VALUES ('notificaciones', 'Activadas') ON CONFLICT (clave) DO NOTHING;
INSERT INTO configuracion (clave, valor) VALUES ('modo', 'Claro') ON CONFLICT (clave) DO NOTHING;
INSERT INTO configuracion (clave, valor) VALUES ('descripcion', 'Sistema de gestión de servicio técnico y mantenimiento de equipos.') ON CONFLICT (clave) DO NOTHING;

-- Historial (trazabilidad) de cada servicio: quién lo movió, de qué estado a cuál y con qué nota
CREATE TABLE IF NOT EXISTS servicio_historial (
    id_historial BIGSERIAL PRIMARY KEY,
    id_servicio INT NOT NULL,
    id_usuario INT,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30),
    nota TEXT,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hist_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_hist_servicio ON servicio_historial(id_servicio, fecha_hora);

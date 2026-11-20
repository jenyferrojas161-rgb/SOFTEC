-- =====================================================================
-- SOFTEC - Evolución de seguridad y auditoría
-- Ejecutar una sola vez sobre softech_db.
-- =====================================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id_audit BIGSERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(120) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_http VARCHAR(10),
    ruta VARCHAR(255),
    CONSTRAINT fk_audit_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_audit_usuario_fecha
    ON audit_logs(id_usuario, fecha_hora DESC);

CREATE INDEX IF NOT EXISTS idx_audit_accion_fecha
    ON audit_logs(accion, fecha_hora DESC);

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

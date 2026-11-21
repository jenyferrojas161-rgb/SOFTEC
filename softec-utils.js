/* ============================================================
   SOFTEC — Utilidades compartidas (clientes.html, tecnicos.html)
   ------------------------------------------------------------
   Requiere cargarse DESPUÉS de api-config.js.
   ============================================================ */
const SoftecUI = (() => {

    /* Escapa texto antes de meterlo en HTML (evita que un nombre con
       "<" o comillas rompa la tabla o inyecte código). */
    const esc = (v) =>
        String(v ?? "").replace(/[&<>"']/g, (ch) => (
            { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[ch]
        ));

    /* Para buscar sin importar mayúsculas ni tildes: "Pérez" == "perez" */
    const normalizar = (v) =>
        String(v ?? "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();

    /* "María del Carmen Gómez" -> "MG" */
    const iniciales = (nombre) => {
        const p = String(nombre ?? "").trim().split(/\s+/).filter(Boolean);
        if (!p.length) return "?";
        return (p[0][0] + (p.length > 1 ? p[p.length - 1][0] : "")).toUpperCase();
    };

    /* "Reparacion_Moviles" -> "Reparacion Moviles" (solo para mostrar) */
    const bonito = (v) => {
        const s = String(v ?? "").replace(/_/g, " ").trim();
        return s ? s.charAt(0).toUpperCase() + s.slice(1) : "";
    };

    const dinero = (n) =>
        new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 }).format(n || 0);

    /* Texto de celda: el valor escapado, o un guion si viene vacío */
    const celda = (v) => (v ? esc(v) : '<span class="text-muted">—</span>');

    /* Color estable del avatar según el id (0..4) */
    const avatarClase = (id) => "dir-av-" + ((Number(id) || 0) % 5);

    /* Contraseña temporal para cuentas creadas desde el panel:
       8 letras + 2 números (cumple la regla de registro_usuario.html). */
    const claveTemporal = () => {
        const letras = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
        const numeros = "23456789";
        const azar = (set, n) => {
            const buf = new Uint32Array(n);
            crypto.getRandomValues(buf);
            return Array.from(buf, (x) => set[x % set.length]).join("");
        };
        return azar(letras, 8) + azar(numeros, 2);
    };

    /* El backend devuelve el error como JSON de Spring: nos quedamos con el texto útil */
    const mensajeError = (err) => {
        const raw = (err && err.message) || String(err);
        try {
            const o = JSON.parse(raw);
            return o.message || o.error || raw;
        } catch (_) {
            return raw;
        }
    };

    /* Borra un usuario "huérfano" sin molestar si no se puede
       (por ejemplo, si todavía lo referencia otra tabla). */
    const borrarUsuarioSilencioso = async (idUsuario) => {
        if (!idUsuario) return;
        try { await api.del(`${ENDPOINTS.usuarios}/${idUsuario}`); } catch (_) { /* se deja */ }
    };

    /* Duplicados: la BD exige documento y correo únicos. Se avisa antes de llamar al backend. */
    const duplicado = (usuarios, { documento, email, ignorarId }) => {
        const mail = String(email || "").toLowerCase();
        for (const u of usuarios) {
            if (u.id === ignorarId) continue;
            if (u.identificacion === documento) return "Ya existe una persona registrada con ese documento.";
            if (mail && String(u.email || "").toLowerCase() === mail) return "Ya existe una persona registrada con ese correo.";
        }
        return null;
    };

    const emailValido = (e) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e);

    /* Traduce cualquier valor de estado (equipo, servicio o factura) a un
       "badge" visual consistente en toda la aplicación. Acepta valores con
       guion bajo (En_Reparacion) o con espacio (En proceso). */
    const badgeEstado = (estado) => {
        const clave = normalizar(estado).replace(/\s+/g, "-");
        const mapa = {
            "pendiente": "pendiente",
            "recibido": "recibido",
            "en-proceso": "proceso",
            "en-diagnostico": "diagnostico",
            "en-reparacion": "reparacion",
            "reparado": "reparado",
            "entregado": "entregado",
            "finalizado": "finalizado",
            "pagada": "pagada",
            "pagado": "pagada",
            "parcial": "pendiente",
            "anulado": "cancelada",
            "sin-reparacion": "sin-reparacion",
            "cancelada": "cancelada",
            "baja": "reparado",
            "media": "pendiente",
            "alta": "reparacion",
        };
        const sufijo = mapa[clave] || "info";
        return `<span class="softec-badge softec-badge--${sufijo}">${esc(bonito(estado) || "—")}</span>`;
    };

    /* ------------------------------------------------------------
       ASIGNACIÓN REAL DE SERVICIOS A UN TÉCNICO
       --------------------------------------------------------------
       IMPORTANTE: `servicios.idUsuario` es quién REGISTRÓ la solicitud
       (recepción/administrador), NO el técnico asignado a atenderla.
       La asignación real vive en la tabla puente servicio_tecnico
       (id_servicio, id_tecnico), expuesta en /api/servicio-tecnico.
       Por eso, para saber qué servicios (y qué equipos) le
       corresponden a un Técnico que inició sesión, hay que:
         1) buscar su fila en /api/tecnicos por idUsuario (el id de
            techicos es distinto del id de usuario)
         2) buscar en /api/servicio-tecnico las filas con ese idTecnico
         3) esos idServicio son los que realmente tiene asignados

       idsServiciosDeTecnico(idUsuarioTecnico, tecnicos, asignaciones)
       devuelve un Set<number> con los id de servicio asignados. Si el
       técnico todavía no tiene una fila en /api/tecnicos, devuelve un
       Set vacío (sin asignaciones), en vez de fallar. */
    const idsServiciosDeTecnico = (idUsuarioTecnico, tecnicos, asignaciones) => {
        const miTecnico = (tecnicos || []).find(t => t.idUsuario == idUsuarioTecnico);
        if (!miTecnico) return new Set();
        return new Set(
            (asignaciones || [])
                .filter(a => a.idTecnico == miTecnico.id)
                .map(a => a.idServicio)
        );
    };

    return { esc, normalizar, iniciales, bonito, dinero, celda, avatarClase, claveTemporal, mensajeError, borrarUsuarioSilencioso, duplicado, emailValido, badgeEstado, idsServiciosDeTecnico };
})();

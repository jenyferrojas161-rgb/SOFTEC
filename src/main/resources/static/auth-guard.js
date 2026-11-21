/* ============================================================
   SOFTEC — Protección de vistas + navegación por rol
   La autorización real también se valida en Spring Security.
   ============================================================ */

const SOFTEC_NAV_PERMISOS = {
    "dashboard.html": ["Administrador", "Tecnico", "Recepcionista"],
    "servicios.html": ["Administrador", "Tecnico", "Recepcionista"],
    "clientes.html": ["Administrador"],
    "tecnicos.html": ["Administrador"],
    "facturacion.html": ["Administrador"],
    "reportes.html": ["Administrador"],
    "configuracion.html": ["Administrador"],
};

const SOFTEC_HOME_POR_ROL = {
    Cliente: "panel-cliente.html",
    Tecnico: "dashboard.html",
    Recepcionista: "dashboard.html",
};

function softecPaginaInicio(rol) {
    return SOFTEC_HOME_POR_ROL[rol] || "dashboard.html";
}

function softecRequireAuth() {
    if (SoftecSesion.claims()) return true;
    SoftecSesion.limpiar();
    const pagina = location.pathname.split("/").pop();
    if (pagina) sessionStorage.setItem("softec_redirect", pagina);
    location.replace("login.html");
    return false;
}

function softecRequireRole(rolesPermitidos) {
    if (!softecRequireAuth()) return;
    const rol = softecRol();
    if (!rolesPermitidos.includes(rol)) {
        alert(`🔒 Acceso denegado\n\nTu rol (${rol || "desconocido"}) no tiene autorización para esta sección.`);
        location.replace(softecPaginaInicio(rol));
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const rol = softecRol();
    if (!rol) return;

    document.querySelectorAll(".softec-links .nav-item").forEach((li) => {
        const a = li.querySelector("a[href]");
        if (!a) return;
        const permitido = SOFTEC_NAV_PERMISOS[a.getAttribute("href")];
        if (permitido && !permitido.includes(rol)) li.remove();
    });

    if (rol === "Cliente") {
        const links = document.querySelector(".softec-links");
        if (links && !links.querySelector('a[href="panel-cliente.html"]')) {
            links.insertAdjacentHTML("afterbegin", '<li class="nav-item"><a class="nav-link" href="panel-cliente.html">Mi panel</a></li>');
        }
    }
});

/* ============================================================
   Campanita de notificaciones (solo Cliente).
   Muestra las notificaciones guardadas en el servidor, aunque el cliente
   no estuviera conectado cuando el técnico cambió el estado del servicio.
   ============================================================ */
function softecIniciarCampana() {
    if (softecRol() !== "Cliente") return null;
    if (typeof api === "undefined" || typeof ENDPOINTS === "undefined") return null;
    const destino = document.getElementById("softecUserLabel");
    if (!destino || document.getElementById("softecCampana")) return null;

    const estilo = document.createElement("style");
    estilo.textContent = `
        .softec-campana { position: relative; }
        .softec-campana__btn { position: relative; width: 40px; height: 40px; border-radius: 999px; cursor: pointer;
            background: transparent; border: 1px solid rgba(255,255,255,.35); color: #fff; font-size: 1.05rem; line-height: 1; }
        .softec-campana__btn:hover { background: rgba(255,255,255,.12); }
        .softec-campana__badge { position: absolute; top: -5px; right: -5px; min-width: 18px; height: 18px; padding: 0 5px;
            border-radius: 999px; background: #E63946; color: #fff; font-size: .68rem; font-weight: 800;
            display: flex; align-items: center; justify-content: center; }
        .softec-campana__panel { position: absolute; right: 0; top: calc(100% + 10px); width: min(340px, 92vw); z-index: 3000;
            background: #fff; color: #1e293b; border-radius: 14px; box-shadow: 0 16px 40px rgba(10,25,47,.28); overflow: hidden; }
        .softec-campana__head { display: flex; justify-content: space-between; align-items: center; gap: 8px;
            padding: 12px 14px; background: #0A192F; color: #fff; }
        .softec-campana__head strong { font-size: .92rem; }
        .softec-campana__leer { background: transparent; border: 0; color: #cbd5e1; font-size: .75rem; cursor: pointer; text-decoration: underline; }
        .softec-campana__lista { max-height: 340px; overflow-y: auto; }
        .softec-campana__item { display: flex; gap: 10px; padding: 11px 14px; border-bottom: 1px solid #eef2f7; font-size: .85rem; }
        .softec-campana__item--nueva { background: #EEF4FF; font-weight: 600; }
        .softec-campana__fecha { display: block; margin-top: 2px; font-size: .72rem; color: #94a3b8; font-weight: 400; }
        .softec-campana__vacio { padding: 22px 14px; text-align: center; color: #94a3b8; font-size: .85rem; }
    `;
    document.head.appendChild(estilo);

    const wrap = document.createElement("div");
    wrap.id = "softecCampana";
    wrap.className = "softec-campana";
    wrap.innerHTML = `
        <button type="button" class="softec-campana__btn" aria-label="Notificaciones" aria-haspopup="true">🔔
            <span class="softec-campana__badge" hidden>0</span>
        </button>
        <div class="softec-campana__panel" hidden>
            <div class="softec-campana__head">
                <strong>Notificaciones</strong>
                <button type="button" class="softec-campana__leer">Marcar como leídas</button>
            </div>
            <div class="softec-campana__lista"></div>
        </div>`;
    destino.parentNode.insertBefore(wrap, destino);

    const boton = wrap.querySelector(".softec-campana__btn");
    const badge = wrap.querySelector(".softec-campana__badge");
    const panel = wrap.querySelector(".softec-campana__panel");
    const lista = wrap.querySelector(".softec-campana__lista");

    function pintar(items) {
        const nuevas = items.filter((n) => !n.leida).length;
        badge.hidden = nuevas === 0;
        badge.textContent = nuevas > 9 ? "9+" : String(nuevas);
        lista.replaceChildren();
        if (!items.length) {
            const vacio = document.createElement("div");
            vacio.className = "softec-campana__vacio";
            vacio.textContent = "No tienes notificaciones.";
            lista.appendChild(vacio);
            return;
        }
        items.forEach((n) => {
            const fila = document.createElement("div");
            fila.className = "softec-campana__item" + (n.leida ? "" : " softec-campana__item--nueva");
            const icono = document.createElement("span");
            icono.textContent = n.tipo === "SERVICIO_FINALIZADO" ? "✅" : "🔧";
            const cuerpo = document.createElement("div");
            const texto = document.createElement("span");
            texto.textContent = String(n.mensaje ?? "");
            const fecha = document.createElement("span");
            fecha.className = "softec-campana__fecha";
            const f = n.fechaHora ? new Date(n.fechaHora) : null;
            fecha.textContent = f && !isNaN(f) ? f.toLocaleString("es-CO") : "";
            cuerpo.append(texto, fecha);
            fila.append(icono, cuerpo);
            lista.appendChild(fila);
        });
    }

    async function cargar() {
        try {
            const items = await api.get(`${ENDPOINTS.notificaciones}/mias`);
            pintar(Array.isArray(items) ? items : []);
        } catch (_) { /* si falla, la campana queda como estaba */ }
    }

    boton.addEventListener("click", (e) => { e.stopPropagation(); panel.hidden = !panel.hidden; });
    panel.addEventListener("click", (e) => e.stopPropagation());
    document.addEventListener("click", () => { panel.hidden = true; });
    wrap.querySelector(".softec-campana__leer").addEventListener("click", async () => {
        try { await api.put(`${ENDPOINTS.notificaciones}/mias/leidas`, {}); } catch (_) {}
        cargar();
    });
    document.addEventListener("softec:notificacion", cargar);

    cargar();
    return { recargar: cargar };
}

document.addEventListener("DOMContentLoaded", () => {
    if (!SoftecSesion.claims() || typeof EventSource === "undefined" || typeof ENDPOINTS === "undefined") return;
    softecIniciarCampana();
    const token = SoftecSesion.token();
    const es = new EventSource(`${ENDPOINTS.notificaciones}/stream?token=${encodeURIComponent(token)}`);
    es.addEventListener("notificacion", (ev) => {
        try {
            const n = JSON.parse(ev.data);
            if (window.SoftecNotify) SoftecNotify.mostrar(n.mensaje, n.tipo === "SERVICIO_FINALIZADO" ? "exito" : "info", 8000);
            document.dispatchEvent(new CustomEvent("softec:notificacion", { detail: n }));
        } catch (_) {}
    });
    window.addEventListener("beforeunload", () => es.close(), { once: true });
});

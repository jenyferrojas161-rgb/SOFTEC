/* ============================================================
   SOFTEC — Sesión JWT + menú por rol + cierre por inactividad
   Inactividad máxima: 15 minutos.
   ============================================================ */

const SOFTEC_ROL_ETIQUETA = {
    ADMIN: "Administrador",
    TECNICO: "Tecnico",
    CLIENTE: "Cliente",
    RECEPCIONISTA: "Recepcionista",
};

const SOFTEC_INACTIVIDAD_MS = 15 * 60 * 1000;
let softecTimerInactividad = null;
let softecUltimaActividad = 0;

const SoftecSesion = {
    guardar({ token, nombre, rol, idUsuario }) {
        sessionStorage.setItem("softec_token", token);
        sessionStorage.setItem("softec_perfil", JSON.stringify({ nombre, rol, idUsuario }));
        softecMarcarActividad();
    },
    token() { return sessionStorage.getItem("softec_token"); },
    perfil() {
        try { return JSON.parse(sessionStorage.getItem("softec_perfil")) || {}; }
        catch (_) { return {}; }
    },
    claims() {
        const token = this.token();
        if (!token) return null;
        try {
            const part = token.split(".")[1];
            if (!part) return null;
            const base64 = part.replace(/-/g, "+").replace(/_/g, "/");
            const padded = base64 + "=".repeat((4 - base64.length % 4) % 4);
            const bytes = Uint8Array.from(atob(padded), c => c.charCodeAt(0));
            const c = JSON.parse(new TextDecoder("utf-8").decode(bytes));
            if (!c.exp || c.exp * 1000 <= Date.now()) return null;
            return c;
        } catch (_) { return null; }
    },
    limpiar() {
        sessionStorage.removeItem("softec_token");
        sessionStorage.removeItem("softec_perfil");
        sessionStorage.removeItem("softec_redirect");
        softecDetenerControlInactividad();
    },
};

function softecUsuarioActual() {
    const c = SoftecSesion.claims();
    if (!c) return null;
    const rol = SOFTEC_ROL_ETIQUETA[c.rol] || c.rol || "";
    const perfil = SoftecSesion.perfil();
    return {
        id: Number(c.sub),
        nombreUsuario: c.nombre || perfil.nombre || "Usuario",
        tipoUsuario: rol,
        rol,
    };
}

function softecRol() {
    return softecUsuarioActual()?.rol || "";
}

async function softecLogin(correo, password) {
    let data;
    try {
        data = await api.post(`${API_BASE}/auth/login`, { email: correo, contrasena: password });
    } catch (err) {
        throw err;
    }
    if (!data?.token) throw new Error("El servidor no entregó una sesión válida.");
    SoftecSesion.guardar(data);
    return data;
}

function softecCerrarSesion() {
    SoftecSesion.limpiar();
    window.location.replace("login.html");
}

function softecMarcarActividad() {
    if (!SoftecSesion.token()) return;
    softecUltimaActividad = Date.now();
    clearTimeout(softecTimerInactividad);
    softecTimerInactividad = setTimeout(() => {
        const inactivo = Date.now() - softecUltimaActividad;
        if (inactivo >= SOFTEC_INACTIVIDAD_MS) {
            SoftecSesion.limpiar();
            sessionStorage.setItem("softec_aviso", "Tu sesión se cerró automáticamente después de 15 minutos sin actividad.");
            window.location.replace("login.html");
        } else {
            softecMarcarActividad();
        }
    }, SOFTEC_INACTIVIDAD_MS + 100);
}

function softecIniciarControlInactividad() {
    if (!SoftecSesion.claims()) return;
    softecMarcarActividad();
    const eventos = ["click", "keydown", "mousemove", "scroll", "touchstart", "pointerdown"];
    let ultimaMarca = 0;
    eventos.forEach((evento) => document.addEventListener(evento, () => {
        const ahora = Date.now();
        if (ahora - ultimaMarca < 1500) return;
        ultimaMarca = ahora;
        softecMarcarActividad();
    }, { passive: true }));
}

function softecDetenerControlInactividad() {
    clearTimeout(softecTimerInactividad);
    softecTimerInactividad = null;
}

const SOFTEC_PROPOSITO_ROL = {
    Administrador: {
        icono: "🛠️", etiqueta: "Administrador",
        proposito: "Gestionas clientes, técnicos, órdenes de servicio, facturación y reportes.",
        accesos: [["Servicios", "servicios.html"], ["Clientes", "clientes.html"], ["Técnicos", "tecnicos.html"], ["Facturación", "facturacion.html"], ["Reportes", "reportes.html"]],
    },
    Tecnico: {
        icono: "🔧", etiqueta: "Técnico",
        proposito: "Atiendes los equipos asignados y actualizas el estado de cada servicio.",
        accesos: [["Mis servicios", "servicios.html"]],
    },
    Cliente: {
        icono: "📱", etiqueta: "Cliente",
        proposito: "Consultas el avance de tus equipos y recibes avisos cuando cambia su estado.",
        accesos: [],
    },
    Recepcionista: {
        icono: "📋", etiqueta: "Recepcionista",
        proposito: "Gestionas la recepción y seguimiento operativo de los servicios.",
        accesos: [["Servicios", "servicios.html"]],
    },
};

function softecPaginaInicio(rol) {
    return ({ Cliente: "panel-cliente.html", Tecnico: "dashboard.html", Recepcionista: "dashboard.html" })[rol] || "dashboard.html";
}

/* Control de inactividad en cualquier vista que cargue session.js. */
document.addEventListener("DOMContentLoaded", () => {
    softecIniciarControlInactividad();

    const aviso = sessionStorage.getItem("softec_aviso");
    if (aviso && location.pathname.endsWith("login.html")) {
        sessionStorage.removeItem("softec_aviso");
        setTimeout(() => alert(aviso), 100);
    }

    const u = softecUsuarioActual();
    const label = document.getElementById("softecUserLabel");
    if (u && label) label.textContent = `👤 ${u.nombreUsuario}${u.rol ? " · " + u.rol : ""}`;

    document.querySelectorAll('a[href="login.html"].softec-btn-outline').forEach((a) => a.addEventListener("click", softecCerrarSesion));

    const pagina = location.pathname.split("/").pop();
    if (!["dashboard.html", "panel-cliente.html"].includes(pagina)) return;
    const info = SOFTEC_PROPOSITO_ROL[softecRol()];
    const cont = document.querySelector(".softec-main .container-fluid");
    if (!u || !info || !cont) return;
    const esc = (v) => String(v).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
    const chips = info.accesos.map(([t, h]) => `<a class="btn btn-sm softec-btn-outline" href="${h}">${t}</a>`).join("");
    cont.insertAdjacentHTML("afterbegin", `<div class="card p-3 mb-4 shadow-sm" id="softecRolBanner" style="border-left:5px solid var(--softec-red) !important"><div class="d-flex align-items-start gap-3 flex-wrap"><div style="font-size:2rem">${info.icono}</div><div class="flex-grow-1"><h5 class="mb-1" style="color:var(--softec-navy);font-weight:800">¡Hola, ${esc(u.nombreUsuario)}! <span class="softec-badge softec-badge--info">${info.etiqueta}</span></h5><p class="mb-2 text-muted">${info.proposito}</p><div class="d-flex flex-wrap gap-2">${chips}</div></div></div></div>`);
});

/* ============================================================
   SOFTEC â€” NÃºcleo de comunicaciÃ³n con Spring Boot + PostgreSQL
   Backend Ãºnico: ver softec-env.js (por defecto http://localhost:8081/api)
   ============================================================ */

const API_BASE = window.SOFTEC_API_BASE || (location.hostname === "localhost" ? "http://localhost:8081/api" : location.origin + "/api");

const ENDPOINTS = {
    auth: `${API_BASE}/auth`,
    usuarios: `${API_BASE}/usuarios`,
    clientes: `${API_BASE}/clientes`,
    tecnicos: `${API_BASE}/tecnicos`,
    equipos: `${API_BASE}/equipos`,
    servicios: `${API_BASE}/servicios`,
    servicioTecnico: `${API_BASE}/servicio-tecnico`,
    notificaciones: `${API_BASE}/notificaciones`,
    chatbot: `${API_BASE}/chatbot`,
    facturacion: `${API_BASE}/facturacion`,
    detallesFacturacion: `${API_BASE}/detalles-facturacion`,
    configuracion: `${API_BASE}/configuracion`,
    auditoria: `${API_BASE}/auditoria`,
    tecnicoMe: `${API_BASE}/tecnicos/me`,
    publicStats: `${API_BASE}/public/stats`,
    publicContact: `${API_BASE}/public/contact`,
};

const ERRORES_BD = [
    { patron: /uq_usuario_email/i, campo: "email", mensaje: "El correo electrÃ³nico ya se encuentra registrado." },
    { patron: /uq_usuario_identificacion/i, campo: "identificacion", mensaje: "El documento de identidad ya se encuentra registrado." },
    { patron: /uq_facturacion_numero/i, campo: "numeroFactura", mensaje: "Ya existe una factura con ese nÃºmero." },
    { patron: /uq_clientes_usuario/i, mensaje: "Esta persona ya estÃ¡ registrada como cliente." },
    { patron: /uq_tecnicos_usuario/i, mensaje: "Esta persona ya estÃ¡ registrada como tÃ©cnico." },
    { patron: /foreign key|llave for[aÃ¡]nea|clave for[aÃ¡]nea|23503/i, mensaje: "No se puede completar la operaciÃ³n porque el registro estÃ¡ relacionado con otros datos." },
    { patron: /null value|valor nulo|23502/i, mensaje: "Faltan datos obligatorios. Revisa el formulario." },
    { patron: /check constraint|restricci[oÃ³]n de verificaci[oÃ³]n|23514/i, mensaje: "Alguno de los valores enviados no es vÃ¡lido." },
    { patron: /value too long|demasiado largo|22001/i, mensaje: "Alguno de los textos es demasiado largo." },
];

const MENSAJE_POR_ESTADO = {
    400: "Los datos enviados no son vÃ¡lidos. Revisa el formulario.",
    401: "Tu sesiÃ³n no es vÃ¡lida. Inicia sesiÃ³n de nuevo.",
    403: "No tienes permiso para realizar esta acciÃ³n.",
    404: "No se encontrÃ³ el registro solicitado.",
    409: "La operaciÃ³n entra en conflicto con datos que ya existen.",
};

const parecePlanoTecnico = (t) => /could not|ERROR:|SQL|org\.|java\.|Exception|constraint|violates|viola|trace/i.test(String(t || ""));

class ApiError extends Error {
    constructor(mensaje, { status = 0, campo = null, detalle = "" } = {}) {
        super(mensaje);
        this.name = "ApiError";
        this.status = status;
        this.campo = campo;
        this.detalle = detalle;
    }
}

function interpretarError(status, textoCrudo) {
    let cuerpo = null;
    try { cuerpo = JSON.parse(textoCrudo); } catch (_) {}

    if (status < 500 && cuerpo && typeof cuerpo.message === "string" && cuerpo.message.trim() && !cuerpo.trace && !parecePlanoTecnico(cuerpo.message)) {
        return { mensaje: cuerpo.message.trim(), campo: cuerpo.campo || null };
    }

    for (const e of ERRORES_BD) {
        if (e.patron.test(textoCrudo)) return { mensaje: e.mensaje, campo: e.campo || null };
    }

    if (MENSAJE_POR_ESTADO[status]) return { mensaje: MENSAJE_POR_ESTADO[status], campo: null };
    if (status >= 500) return { mensaje: "El servidor tuvo un problema al procesar la solicitud. IntÃ©ntalo de nuevo.", campo: null };
    return { mensaje: `No se pudo completar la solicitud (error ${status}).`, campo: null };
}

async function apiRequest(url, options = {}) {
    try {
        const token = sessionStorage.getItem("softec_token");
        const headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {}),
        };

        const res = await fetch(url, { ...options, headers });

        if (res.status === 401 && !url.includes("/auth/")) {
            sessionStorage.removeItem("softec_token");
            sessionStorage.removeItem("softec_perfil");
            sessionStorage.setItem("softec_aviso", "Tu sesiÃ³n expirÃ³ o no es vÃ¡lida. Inicia sesiÃ³n de nuevo.");
            if (!location.pathname.endsWith("login.html")) location.replace("login.html");
        }

        const texto = await res.text().catch(() => "");

        if (!res.ok) {
            const { mensaje, campo } = interpretarError(res.status, texto);
            (res.status >= 500 ? console.error : console.warn)(`[SOFTEC API ${res.status}] ${options.method || "GET"} ${url}`, texto);
            throw new ApiError(mensaje, { status: res.status, campo, detalle: texto });
        }

        if (!texto.trim()) return null;
        try { return JSON.parse(texto); } catch (_) { return texto; }
    } catch (err) {
        if (err instanceof ApiError) throw err;
        if (err instanceof TypeError) {
            throw new Error("No se pudo conectar con SOFTEC. Verifica que Spring Boot estÃ© ejecutÃ¡ndose en el puerto 8081.");
        }
        throw err;
    }
}

const api = {
    get: (url) => apiRequest(url),
    post: (url, body) => apiRequest(url, { method: "POST", body: JSON.stringify(body) }),
    put: (url, body) => apiRequest(url, { method: "PUT", body: JSON.stringify(body) }),
    del: (url) => apiRequest(url, { method: "DELETE" }),
};


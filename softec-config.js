/* ============================================================
   SOFTEC — Configuración global (viene de PostgreSQL)
   Aplica en TODAS las páginas: nombre del sistema, logo y modo claro/oscuro.
   Expone window.SoftecConfig = { datos, dias, cargar(), aplicar(cfg) }.
   ============================================================ */
(function () {
    const BASE = (typeof API_BASE !== "undefined") ? API_BASE : (window.SOFTEC_API_BASE || "http://localhost:8081/api");

    const LOGO_ORIGINAL = "logo-softec.png";

    function css() {
        if (document.getElementById("softecDarkCss")) return;
        const st = document.createElement("style");
        st.id = "softecDarkCss";
        st.textContent = `
        html.softec-dark body { background:#0b1220 !important; color:#e2e8f0; }
        html.softec-dark .card, html.softec-dark .card-box, html.softec-dark .softec-modal-box,
        html.softec-dark .login-card, html.softec-dark .register-box, html.softec-dark .table,
        html.softec-dark .stat-card { background:#111c33 !important; color:#e2e8f0 !important; border-color:#22304d !important; }
        html.softec-dark .table > :not(caption) > * > * { background-color:transparent !important; color:#e2e8f0 !important; border-color:#22304d !important; }
        html.softec-dark .form-control, html.softec-dark .form-select { background:#0b1424 !important; color:#e2e8f0 !important; border-color:#2b3b5c !important; }
        html.softec-dark .text-muted, html.softec-dark small { color:#94a3b8 !important; }
        html.softec-dark h1, html.softec-dark h2, html.softec-dark h3, html.softec-dark h4, html.softec-dark h5,
        html.softec-dark .login-body label, html.softec-dark .form-label { color:#f1f5f9 !important; }
        html.softec-dark footer.softec-footer { background:#060b16 !important; }
        `;
        document.head.appendChild(st);
    }

    function aplicar(cfg) {
        if (!cfg) return;
        css();
        document.documentElement.classList.toggle("softec-dark", cfg.modo === "Oscuro");

        if (cfg.nombre) {
            document.querySelectorAll(".softec-brand span, .login-header h2, .register-box h1").forEach(el => { el.textContent = cfg.nombre; });
            if (document.title.includes("SOFTEC")) document.title = document.title.replace(/SOFTEC/g, cfg.nombre);
        }
        if (cfg.logo) {
            document.querySelectorAll('img[src="' + LOGO_ORIGINAL + '"], img[data-softec-logo]').forEach(img => {
                img.dataset.softecLogo = "1";
                img.src = cfg.logo;
            });
            const ico = document.querySelector('link[rel="icon"]');
            if (ico) ico.href = cfg.logo;
        } else {
            document.querySelectorAll("img[data-softec-logo]").forEach(img => { img.src = LOGO_ORIGINAL; });
            const ico = document.querySelector('link[rel="icon"]');
            if (ico) ico.href = LOGO_ORIGINAL;
        }
        window.SoftecConfig.datos = cfg;
        window.SoftecConfig.dias = Number(cfg.dias) > 0 ? Number(cfg.dias) : 5;
    }

    function cargar() {
        const cfg = {
            nombre: "SOFTEC",
            logo: LOGO_ORIGINAL,
            modo: "Claro",
            dias: 5
        };

        aplicar(cfg);
        return cfg;
    }
    window.SoftecConfig = {
        datos: {
            nombre: "SOFTEC",
            logo: LOGO_ORIGINAL,
            modo: "Claro",
            dias: 5
        },
        dias: 5,
        cargar,
        aplicar
    };


    // 2) cuando la página ya existe, aplica y refresca desde el servidor
    document.addEventListener("DOMContentLoaded", () => {
        if (window.SoftecConfig.datos) aplicar(window.SoftecConfig.datos);
        cargar();
    });
})();

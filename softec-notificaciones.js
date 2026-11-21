/* ============================================================
   SOFTEC — Notificaciones (toast) compartidas
   ------------------------------------------------------------
   Pequeño helper visual, sin dependencias, para avisos puntuales
   dentro de cualquier página (ej. "Mantenimiento finalizado").
   No reemplaza los mensajes de error de formularios (esos siguen
   usando alert() o los banners propios de cada página); esto es
   para notificaciones informativas de una sola vez.

   Uso:
       SoftecNotify.mostrar("Servicio marcado como Terminado", "exito");
       SoftecNotify.mostrar("No se pudo notificar al cliente", "error");
   ============================================================ */
const SoftecNotify = (() => {
    let contenedor = null;

    function asegurarContenedor() {
        if (contenedor) return contenedor;
        contenedor = document.createElement("div");
        contenedor.id = "softecToastStack";
        contenedor.setAttribute("aria-live", "polite");
        contenedor.setAttribute("aria-atomic", "true");
        contenedor.style.cssText = [
            "position:fixed", "top:20px", "right:20px", "z-index:2000",
            "display:flex", "flex-direction:column", "gap:10px",
            "max-width:min(360px, calc(100vw - 32px))",
        ].join(";");
        document.body.appendChild(contenedor);
        return contenedor;
    }

    const COLORES = {
        exito: { fondo: "#E7F8F0", borde: "#10B981", texto: "#047857", icono: "✅" },
        info: { fondo: "#EAF1FE", borde: "#3B82F6", texto: "#1E3A8A", icono: "ℹ️" },
        error: { fondo: "#FFF1F0", borde: "#E63946", texto: "#C1121F", icono: "⚠️" },
    };

    function mostrar(mensaje, tipo = "info", duracionMs = 5000) {
        const cont = asegurarContenedor();
        const estilo = COLORES[tipo] || COLORES.info;

        const toast = document.createElement("div");
        toast.setAttribute("role", "status");
        toast.style.cssText = [
            `background:${estilo.fondo}`, `color:${estilo.texto}`,
            `border-left:4px solid ${estilo.borde}`,
            "border-radius:12px", "padding:14px 16px", "box-shadow:0 10px 28px rgba(10,25,47,.16)",
            "font-family:'Inter','Segoe UI',sans-serif", "font-size:.9rem", "font-weight:600",
            "display:flex", "align-items:flex-start", "gap:10px",
            "opacity:0", "transform:translateX(12px)", "transition:opacity .25s ease, transform .25s ease",
        ].join(";");
        toast.replaceChildren();
        const icono = document.createElement("span");
        icono.style.cssText = "font-size:1.1rem;line-height:1";
        icono.textContent = estilo.icono;
        const texto = document.createElement("span");
        texto.style.flex = "1";
        texto.textContent = String(mensaje ?? "");
        toast.append(icono, texto);

        cont.appendChild(toast);
        requestAnimationFrame(() => {
            toast.style.opacity = "1";
            toast.style.transform = "translateX(0)";
        });

        const cerrar = () => {
            toast.style.opacity = "0";
            toast.style.transform = "translateX(12px)";
            setTimeout(() => toast.remove(), 250);
        };
        toast.addEventListener("click", cerrar);
        if (duracionMs > 0) setTimeout(cerrar, duracionMs);
    }

    return { mostrar };
})();

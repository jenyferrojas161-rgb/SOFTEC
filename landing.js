/* ============================================================
   SOFTEC — Comportamiento de la landing (index.html)
   ------------------------------------------------------------
   1. Menú móvil y sombra de la barra al hacer scroll
   2. Buscador del héroe  -> resalta los módulos que coinciden
      (conserva la función global buscarAyuda(), el input
      #helpSearch, el contenedor #helpGrid y el ancla
      #centroAyuda que ya existían)
   3. Contadores animados de "SOFTEC en números"
   No toca sesión, formularios ni llamadas al backend.
   ============================================================ */
(function () {
    "use strict";

    const $ = (sel, ctx = document) => ctx.querySelector(sel);
    const $$ = (sel, ctx = document) => Array.from(ctx.querySelectorAll(sel));
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    /* ---------- 1. NAVBAR ---------- */
    const header = $("#sfHeader");
    const toggle = $("#sfToggle");
    const menu = $("#softecNav");

    function setMenu(open) {
        header.classList.toggle("is-open", open);
        toggle.setAttribute("aria-expanded", String(open));
        toggle.setAttribute("aria-label", open ? "Cerrar menú" : "Abrir menú");
    }

    if (header && toggle && menu) {
        toggle.addEventListener("click", () => setMenu(!header.classList.contains("is-open")));

        // Cierra el menú al elegir un enlace, con Escape o al pasar a escritorio
        menu.addEventListener("click", (e) => { if (e.target.closest("a")) setMenu(false); });
        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape" && header.classList.contains("is-open")) {
                setMenu(false);
                toggle.focus();
            }
        });
        window.matchMedia("(min-width: 961px)").addEventListener("change", (e) => { if (e.matches) setMenu(false); });

        const onScroll = () => header.classList.toggle("is-scrolled", window.scrollY > 8);
        onScroll();
        window.addEventListener("scroll", onScroll, { passive: true });
    }

    /* ---------- 2. BUSCADOR ----------
       Ya no depende de tarjetas en el DOM: los destinos viven en este
       arreglo. Al escribir se muestra una lista de sugerencias; al
       enviar (Enter/botón) o elegir una sugerencia, se navega directo
       al módulo correspondiente. */
    const input = $("#helpSearch");
    const form = $("#helpSearchForm");
    const status = $("#helpStatus");
    const suggestBox = $("#helpSuggestions");

    const MODULOS = [
        { nombre: "Gestión de equipos", url: "dashboard.html",
          claves: "equipo equipos computador portatil dispositivo panel general pendiente proceso finalizado estado" },
        { nombre: "Control de clientes", url: "clientes.html",
          claves: "cliente clientes documento telefono correo email direccion datos contacto" },
        { nombre: "Soporte técnico", url: "servicios.html",
          claves: "soporte tecnico servicio servicios reparacion mantenimiento diagnostico instalacion problema prioridad tecnico" },
        { nombre: "Facturación", url: "facturacion.html",
          claves: "factura facturas facturacion pago cobro valor fecha recibo" },
    ];

    // "Reparación" y "reparacion" deben dar el mismo resultado
    const normalizar = (txt) =>
        String(txt).toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();

    function coincidencias(q) {
        if (q === "") return [];
        return MODULOS.filter((m) => normalizar(`${m.claves} ${m.nombre}`).includes(q));
    }

    function ocultarSugerencias() {
        if (!suggestBox) return;
        suggestBox.hidden = true;
        suggestBox.innerHTML = "";
        input.setAttribute("aria-expanded", "false");
    }

    function pintarSugerencias(lista) {
        if (!suggestBox) return;
        if (!lista.length) { ocultarSugerencias(); return; }
        suggestBox.innerHTML = lista.map((m) =>
            `<li role="option"><a href="${m.url}">${m.nombre}</a></li>`
        ).join("");
        suggestBox.hidden = false;
        input.setAttribute("aria-expanded", "true");
    }

    function buscarAyuda() {
        if (!input) return 0;
        const q = normalizar(input.value);
        const coincide = coincidencias(q);
        pintarSugerencias(coincide);

        if (status) {
            if (q === "") {
                status.textContent = "";
            } else if (coincide.length) {
                const n = coincide.length;
                status.textContent = `${n} módulo${n > 1 ? "s" : ""} relacionado${n > 1 ? "s" : ""}: ${coincide.map((m) => m.nombre).join(", ")}.`;
            } else {
                status.textContent = `No encontramos “${input.value.trim()}”. Prueba con equipo, cliente, servicio o factura.`;
            }
        }
        return coincide.length;
    }
    window.buscarAyuda = buscarAyuda; // compatibilidad con el código anterior

    function irAlMejorResultado() {
        const q = normalizar(input.value);
        const coincide = coincidencias(q);
        if (coincide.length) window.location.href = coincide[0].url;
    }

    if (input) {
        input.addEventListener("input", buscarAyuda);
        input.addEventListener("blur", () => setTimeout(ocultarSugerencias, 150));

        // Placeholder más corto en pantallas angostas, para que no se corte
        const largo = input.getAttribute("placeholder");
        const corto = input.dataset.placeholderShort;
        const mq = window.matchMedia("(max-width: 520px)");
        const ajustar = () => input.setAttribute("placeholder", mq.matches && corto ? corto : largo);
        ajustar();
        mq.addEventListener("change", ajustar);
    }
    if (form) {
        form.addEventListener("submit", (e) => {
            e.preventDefault();
            irAlMejorResultado();
        });
    }
    /* ---------- 3. ESTADÍSTICAS REALES ---------- */
    const contadores = $$('[data-stat]');

    function formatear(el, valor) {
        const pre = el.dataset.prefix || "";
        const suf = el.dataset.suffix || "";
        return pre + Math.round(valor).toLocaleString("es-CO") + suf;
    }

    function animar(el, fin) {
        const duracion = 900;
        const inicio = performance.now();
        const paso = (ahora) => {
            const p = Math.min((ahora - inicio) / duracion, 1);
            const suave = 1 - Math.pow(1 - p, 3);
            el.textContent = formatear(el, fin * suave);
            if (p < 1) requestAnimationFrame(paso);
        };
        requestAnimationFrame(paso);
    }

    async function cargarEstadisticas() {
        try {
            const r = await fetch(`${window.SOFTEC_API_BASE || "http://localhost:8081/api"}/public/stats`, { headers: { Accept: "application/json" } });
            if (!r.ok) throw new Error();
            const stats = await r.json();
            contadores.forEach(el => {
                const key = el.dataset.stat;
                const value = Number(stats[key] || 0);
                if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) el.textContent = formatear(el, value);
                else animar(el, value);
            });
        } catch (e) {
            contadores.forEach(el => el.textContent = "—");
            console.warn("No se pudieron cargar las estadísticas públicas de SOFTEC.", e);
        }
    }

    cargarEstadisticas();
})();

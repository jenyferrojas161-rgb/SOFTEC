/* ============================================================
   SOFTEC — Asistente virtual conectado a datos reales
   Consulta pública: /api/servicio-tecnico/buscar
   ============================================================ */
(function () {
    "use strict";

    let BASE_URL = "";

    const ICONOS = {
        chat: '<svg class="sfcw-icon sfcw-icon--chat" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/></svg>',
        close: '<svg class="sfcw-icon sfcw-icon--close" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
        send: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>',
    };

    const CONTACTO_DEFECTO = {
        address: "Bogotá D.C., Colombia",
        phone: "línea de atención configurada por SOFTEC",
        whatsapp: "línea de WhatsApp configurada por SOFTEC",
        hours: "Lunes a viernes, 8:00 a.m. - 6:00 p.m.",
    };

    function calcularBase() {
        const script = document.currentScript || Array.from(document.scripts).find(s => s.src.includes("chatbot-widget.js"));
        return script ? script.src.replace(/chatbot-widget\.js.*$/, "") : "";
    }

    function inyectarEstilos() {
        if (document.querySelector('link[data-sfcw]')) return;
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = BASE_URL + "chatbot-widget.css";
        link.dataset.sfcw = "true";
        document.head.appendChild(link);
    }

    function inyectarMarkup() {
        if (document.getElementById("sfcwRoot")) return document.getElementById("sfcwRoot");
        const root = document.createElement("div");
        root.id = "sfcwRoot";
        root.className = "sfcw";
        root.innerHTML = `
            <button type="button" class="sfcw-toggle" id="sfcwToggle" aria-expanded="false" aria-controls="sfcwWindow" aria-label="Abrir asistente virtual de SOFTEC">
                ${ICONOS.chat}${ICONOS.close}<span class="sfcw-ping" aria-hidden="true"></span>
            </button>
            <section class="sfcw-window" id="sfcwWindow" role="dialog" aria-modal="false" aria-labelledby="sfcwTitle" hidden>
                <header class="sfcw-header">
                    <span class="sfcw-avatar" aria-hidden="true">${ICONOS.chat}</span>
                    <div class="sfcw-heading"><p class="sfcw-title" id="sfcwTitle">Asistente virtual SOFTEC</p><p class="sfcw-status"><span class="sfcw-dot"></span>Atención al cliente</p></div>
                    <button type="button" class="sfcw-close" id="sfcwClose" aria-label="Cerrar asistente">${ICONOS.close}</button>
                </header>
                <div class="sfcw-messages" id="sfcwMessages" role="log" aria-live="polite"></div>
                <div class="sfcw-quick" id="sfcwQuick">
                    <button type="button" data-q="Quiero consultar el estado de mi equipo">Estado de mi equipo</button>
                    <button type="button" data-q="¿Qué servicios y tarifas manejan?">Servicios y tarifas</button>
                    <button type="button" data-q="¿Cuál es la dirección y el horario?">Dirección y horario</button>
                    <button type="button" data-q="Quiero contactar por WhatsApp">WhatsApp</button>
                </div>
                <form class="sfcw-form" id="sfcwForm" autocomplete="off">
                    <label class="sfcw-sr-only" for="sfcwInput">Escribe tu consulta</label>
                    <input id="sfcwInput" type="text" placeholder="Orden, cédula o pregunta…" maxlength="180" autocomplete="off">
                    <button class="sfcw-send" type="submit" aria-label="Enviar">${ICONOS.send}</button>
                </form>
            </section>`;
        document.body.appendChild(root);
        return root;
    }

    function normalizar(texto) {
        return String(texto || "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
    }

    function escapar(texto) {
        const d = document.createElement("div");
        d.textContent = String(texto ?? "");
        return d.innerHTML;
    }

    async function obtenerContacto() {
        try {
            const r = await fetch(`${window.SOFTEC_API_BASE || "http://localhost:8081/api"}/public/contact`, { headers: { Accept: "application/json" } });
            if (!r.ok) throw new Error();
            return { ...CONTACTO_DEFECTO, ...(await r.json()) };
        } catch (_) {
            return CONTACTO_DEFECTO;
        }
    }

    async function consultarEstado(consulta) {
        const limpio = String(consulta || "").trim();
        const q = normalizar(limpio);
        const soloNumeros = limpio.replace(/\D/g, "");
        const esCedula = /cedula|documento/.test(q);
        const esOrden = /orden|servicio/.test(q);
        if (!soloNumeros) throw new Error("Escribe el número de orden o la cédula.");
        if (!esCedula && !esOrden && soloNumeros.length >= 6) {
            throw new Error("Para consultar, escribe por ejemplo: orden 15 o cédula 1234567890.");
        }
        const params = new URLSearchParams(esCedula ? { cedula: soloNumeros } : { orden: soloNumeros });

        const r = await fetch(`${window.SOFTEC_API_BASE || "http://localhost:8081/api"}/servicio-tecnico/buscar?${params.toString()}`, { headers: { Accept: "application/json" } });
        const texto = await r.text();
        let data = {};
        try { data = JSON.parse(texto); } catch (_) {}
        if (!r.ok) throw new Error(data.message || "No encontramos un servicio con esos datos.");
        return data;
    }

    function activar(root) {
        const toggle = root.querySelector("#sfcwToggle");
        const win = root.querySelector("#sfcwWindow");
        const close = root.querySelector("#sfcwClose");
        const messages = root.querySelector("#sfcwMessages");
        const form = root.querySelector("#sfcwForm");
        const input = root.querySelector("#sfcwInput");
        const quick = root.querySelector("#sfcwQuick");
        let abierto = false;
        let saludo = false;

        const add = (text, who = "bot") => {
            const row = document.createElement("div");
            row.className = `sfcw-msg sfcw-msg--${who}`;
            row.innerHTML = `<p>${escapar(text)}</p>`;
            messages.appendChild(row);
            messages.scrollTop = messages.scrollHeight;
        };

        const typing = async (promise) => {
            const row = document.createElement("div");
            row.className = "sfcw-msg sfcw-msg--bot sfcw-msg--typing";
            row.innerHTML = "<p><span></span><span></span><span></span></p>";
            messages.appendChild(row);
            messages.scrollTop = messages.scrollHeight;
            try { return await promise; } finally { row.remove(); }
        };

        const abrir = () => {
            abierto = true; win.hidden = false; root.classList.add("is-open"); toggle.setAttribute("aria-expanded", "true");
            if (!saludo) { saludo = true; add("Hola 👋 Soy el asistente de SOFTEC. Puedo consultar el estado real de un equipo con su número de orden o cédula y orientarte sobre servicios, tarifas y canales de atención."); }
            setTimeout(() => input.focus(), 100);
        };
        const cerrar = () => { abierto = false; root.classList.remove("is-open"); toggle.setAttribute("aria-expanded", "false"); win.hidden = true; };
        toggle.addEventListener("click", () => abierto ? cerrar() : abrir());
        close.addEventListener("click", cerrar);
        document.addEventListener("keydown", e => { if (e.key === "Escape" && abierto) cerrar(); });

        async function responder(texto) {
            const q = normalizar(texto);
            if (!q) return;
            add(texto, "user");

            const pareceSeguimiento = /estado|orden|cedula|equipo|reparacion|reparación|servicio|seguimiento/.test(q) && /\d/.test(q);
            if (pareceSeguimiento) {
                try {
                    const data = await typing(consultarEstado(texto));
                    const estado = String(data.estadoServicio || data.estadoEquipo || "Sin estado").replace(/_/g, " ");
                    const equipo = [data.tipoEquipo, data.marca, data.modelo].filter(Boolean).join(" ");
                    add(`Encontré tu servicio #${data.idServicio}. Equipo: ${equipo || "registrado en SOFTEC"}. Estado del servicio: ${estado}. ${data.numeroSerie ? `Serial: ${data.numeroSerie}.` : ""}`);
                    if (data.diagnosticoRealizado) add(`Diagnóstico registrado: ${data.diagnosticoRealizado}`);
                    return;
                } catch (e) { add(e.message); return; }
            }

            if (/cotiz|precio|tarifa|costo|valor/.test(q)) {
                add("Las tarifas dependen del tipo de equipo, diagnóstico y trabajo requerido. El chatbot no inventa valores: para una cotización exacta se revisa el equipo y se registra el servicio.");
                return;
            }
            if (/direccion|ubicacion|donde|horario|abren|cierran/.test(q)) {
                const c = await typing(obtenerContacto());
                add(`Atendemos en ${c.address}. Horario: ${c.hours}. Teléfono: ${c.phone}.`);
                return;
            }
            if (/whatsapp|contactar|contacto|telefono|llamar/.test(q)) {
                const c = await typing(obtenerContacto());
                add(`Puedes comunicarte por WhatsApp al ${c.whatsapp}. También puedes llamar al ${c.phone}.`);
                return;
            }
            if (/servicios|mantenimiento|diagnostico|reparacion|instalacion/.test(q)) {
                add("SOFTEC gestiona diagnóstico, reparación, mantenimiento e instalación de equipos, además de seguimiento del servicio y facturación.");
                return;
            }
            if (/hola|buenas|buenos/.test(q)) {
                add("¡Hola! 😊 Dime si quieres consultar un equipo, conocer servicios y tarifas o recibir los datos de contacto.");
                return;
            }
            add("Puedo ayudarte con el estado de un equipo, servicios y tarifas, dirección, horarios o WhatsApp. Para consultar un equipo escribe su número de orden o cédula.");
        }

        form.addEventListener("submit", e => { e.preventDefault(); const v = input.value.trim(); if (!v) return; input.value = ""; responder(v); });
        quick.addEventListener("click", e => { const b = e.target.closest("button[data-q]"); if (b) responder(b.dataset.q); });
    }

    function iniciar() { BASE_URL = calcularBase(); inyectarEstilos(); activar(inyectarMarkup()); }
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", iniciar, { once: true }); else iniciar();
})();

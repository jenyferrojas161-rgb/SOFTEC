/* ============================================================
   SOFTEC — Información desplegable (index.html)
   Las secciones informativas quedan plegadas; el menú hamburguesa
   permite mostrar una a la vez. Sin JS, todo se ve normalmente.
   No toca sesión ni backend.
   ============================================================ */
(function () {
    "use strict";

    const wrap = document.getElementById("sfInfoMenu");
    if (!wrap) return;

    const toggle = wrap.querySelector(".sf-infomenu__toggle");
    const list = wrap.querySelector(".sf-infomenu__list");
    const items = Array.from(wrap.querySelectorAll(".sf-infomenu__item"));
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    // Cada botón apunta (data-target) a una sección existente de la página
    const map = items
        .map((btn) => ({ btn, sec: document.querySelector(btn.dataset.target) }))
        .filter((m) => m.sec);

    function setMenu(open) {
        wrap.classList.toggle("is-open", open);
        toggle.setAttribute("aria-expanded", String(open));
        list.hidden = !open;
    }

    function showOnly(sec, scroll) {
        map.forEach((m) => {
            const visible = m.sec === sec;
            m.sec.hidden = !visible;
            m.btn.setAttribute("aria-expanded", String(visible));
        });
        if (sec && scroll) sec.scrollIntoView({ behavior: reduceMotion ? "auto" : "smooth", block: "start" });
    }

    // Estado inicial: todo plegado
    map.forEach((m) => { m.sec.hidden = true; });
    setMenu(false);

    toggle.addEventListener("click", () => setMenu(!wrap.classList.contains("is-open")));

    map.forEach((m) => {
        m.btn.addEventListener("click", () => {
            // Si ya está abierta, se vuelve a plegar
            if (!m.sec.hidden) showOnly(null, false);
            else showOnly(m.sec, true);
        });
    });

    // Enlaces internos (barra superior, preguntas, etc.) que apuntan a una sección plegada
    function abrirDesdeHash(hash, scroll) {
        if (!hash || hash.length < 2) return false;
        let el;
        try { el = document.querySelector(hash); } catch (e) { return false; }
        if (!el) return false;
        const m = map.find((x) => x.sec === el || x.sec.contains(el));
        if (!m) return false;
        setMenu(true);
        showOnly(m.sec, scroll);
        return true;
    }

    document.addEventListener("click", (e) => {
        const a = e.target.closest('a[href^="#"]');
        if (!a || wrap.contains(a)) return;
        if (abrirDesdeHash(a.getAttribute("href"), false)) {
            e.preventDefault();
            const target = document.querySelector(a.getAttribute("href"));
            if (target) target.scrollIntoView({ behavior: reduceMotion ? "auto" : "smooth", block: "start" });
            history.replaceState(null, "", a.getAttribute("href"));
        }
    });

    if (location.hash) abrirDesdeHash(location.hash, true);
    window.addEventListener("hashchange", () => abrirDesdeHash(location.hash, true));
})();

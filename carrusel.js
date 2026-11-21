/* ============================================================
   SOFTEC — Carrusel de imágenes (index.html)
   Cambia solo cada 5 s; se pausa al pasar el mouse, al enfocar,
   con el botón de pausa o si la pestaña no está visible.
   No toca sesión ni backend.
   ============================================================ */
(function () {
    "use strict";

    const root = document.getElementById("sfSlider");
    if (!root) return;

    const slides = Array.from(root.querySelectorAll(".sf-slide"));
    const dots = Array.from(root.querySelectorAll(".sf-slider__dot"));
    const btnPrev = root.querySelector(".sf-slider__nav--prev");
    const btnNext = root.querySelector(".sf-slider__nav--next");
    const btnPause = root.querySelector(".sf-slider__pause");
    const DURACION = 5000;
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    let actual = 0;
    let timer = null;
    let pausaUsuario = reduceMotion; // con movimiento reducido no avanza solo
    let pausaTemporal = false;       // mouse encima, foco o pestaña oculta

    root.style.setProperty("--sf-slide-ms", DURACION + "ms");

    function mostrar(i) {
        actual = (i + slides.length) % slides.length;
        slides.forEach((s, n) => {
            const activa = n === actual;
            s.classList.toggle("is-active", activa);
            s.setAttribute("aria-hidden", String(!activa));
        });
        dots.forEach((d, n) => {
            d.classList.toggle("is-active", n === actual);
            d.setAttribute("aria-current", n === actual ? "true" : "false");
        });
        reiniciar();
    }

    function detener() {
        clearTimeout(timer);
        root.classList.remove("is-running");
    }

    function reiniciar() {
        detener();
        const pausado = pausaUsuario || pausaTemporal;
        root.classList.toggle("is-paused", pausaUsuario);
        root.setAttribute("aria-live", pausado ? "polite" : "off");
        if (pausado) return;
        // reflow para reiniciar la barra de progreso
        void root.offsetWidth;
        root.classList.add("is-running");
        timer = setTimeout(() => mostrar(actual + 1), DURACION);
    }

    btnPrev.addEventListener("click", () => mostrar(actual - 1));
    btnNext.addEventListener("click", () => mostrar(actual + 1));
    dots.forEach((d, n) => d.addEventListener("click", () => mostrar(n)));

    btnPause.addEventListener("click", () => {
        pausaUsuario = !pausaUsuario;
        btnPause.setAttribute("aria-label", pausaUsuario ? "Reanudar carrusel" : "Pausar carrusel");
        reiniciar();
    });

    // Pausas temporales
    const pausar = (v) => () => { pausaTemporal = v; reiniciar(); };
    root.addEventListener("mouseenter", pausar(true));
    root.addEventListener("mouseleave", pausar(false));
    root.addEventListener("focusin", pausar(true));
    root.addEventListener("focusout", pausar(false));
    document.addEventListener("visibilitychange", () => { pausaTemporal = document.hidden; reiniciar(); });

    // Teclado y gesto táctil
    root.addEventListener("keydown", (e) => {
        if (e.key === "ArrowLeft") { mostrar(actual - 1); }
        else if (e.key === "ArrowRight") { mostrar(actual + 1); }
    });
    let x0 = null;
    root.addEventListener("touchstart", (e) => { x0 = e.touches[0].clientX; }, { passive: true });
    root.addEventListener("touchend", (e) => {
        if (x0 === null) return;
        const dx = e.changedTouches[0].clientX - x0;
        if (Math.abs(dx) > 40) mostrar(actual + (dx < 0 ? 1 : -1));
        x0 = null;
    }, { passive: true });

    btnPause.setAttribute("aria-label", pausaUsuario ? "Reanudar carrusel" : "Pausar carrusel");
    mostrar(0);
})();

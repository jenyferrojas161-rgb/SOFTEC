/* SOFTEC — Política de contraseña: 10+ caracteres, mayúscula, minúscula, número y especial. */
const SoftecPassword = (() => {
    function toggle(idCampo, boton) {
        const input = document.getElementById(idCampo);
        if (!input) return;
        const mostrando = input.type === "text";
        input.type = mostrando ? "password" : "text";
        if (boton) boton.textContent = mostrando ? "👁️" : "🙈";
    }

    function evaluar(password) {
        const p = String(password ?? "");
        return {
            length: p.length >= 10,
            letter: /[a-z]/.test(p),
            upper: /[A-Z]/.test(p),
            number: /[0-9]/.test(p),
            special: /[^A-Za-z0-9]/.test(p),
        };
    }

    function actualizarChecklist(idCampo) {
        const input = document.getElementById(idCampo);
        const checks = evaluar(input?.value || "");
        const marcar = (id, ok) => document.getElementById(id)?.classList.toggle("ok", ok);
        marcar("req-length", checks.length);
        marcar("req-letter", checks.letter);
        marcar("req-upper", checks.upper);
        marcar("req-number", checks.number);
        marcar("req-special", checks.special);
        return Object.values(checks).every(Boolean);
    }

    function actualizarCoincidencia(id1, id2, idMensaje = "matchMsg") {
        const p1 = document.getElementById(id1)?.value ?? "";
        const p2 = document.getElementById(id2)?.value ?? "";
        const msg = document.getElementById(idMensaje);
        if (!msg) return p1 !== "" && p1 === p2;
        if (!p2) { msg.textContent = ""; msg.className = "match-msg"; return false; }
        const ok = p1 === p2;
        msg.textContent = ok ? "✔ Las contraseñas coinciden" : "✖ Las contraseñas no coinciden";
        msg.className = `match-msg ${ok ? "ok" : "bad"}`;
        return ok;
    }

    return { toggle, evaluar, actualizarChecklist, actualizarCoincidencia };
})();

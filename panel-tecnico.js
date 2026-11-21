/* ============================================================
   SOFTEC — Panel del técnico (solo se muestra a quien tiene rol Técnico)
   Disponibilidad propia, resumen de carga de trabajo, cola priorizada
   y acceso directo para registrar avances e historial.
   ============================================================ */
(function () {
    document.addEventListener("DOMContentLoaded", async () => {
        if (typeof softecRol !== "function" || softecRol() !== "Tecnico") return;
        const ancla = document.querySelector(".row.text-center.mb-3");
        if (!ancla) return;

        const { esc, bonito, badgeEstado } = SoftecUI;
        const ORDEN = { Urgente: 0, Alta: 1, Media: 2, Baja: 3 };
        const EN_CURSO = ["Pendiente", "En_Proceso"];

        ancla.insertAdjacentHTML("beforebegin", `
            <div class="card p-3 shadow-sm mb-3" id="panelTec">
                <div class="text-muted">Cargando tu panel…</div>
            </div>`);
        const panel = document.getElementById("panelTec");

        try {
            const [yo, servicios, equipos, clientes] = await Promise.all([
                api.get(ENDPOINTS.tecnicoMe).catch(() => null),
                api.get(ENDPOINTS.servicios), api.get(ENDPOINTS.equipos), api.get(ENDPOINTS.clientes).catch(() => []),
            ]);
            if (window.SoftecConfig) await SoftecConfig.cargar();

            const equipo = (id) => equipos.find(e => e.id === id) || {};
            const textoEq = (e) => [e.tipoEquipo, e.marca, e.modelo].filter(Boolean).join(" ") || "Equipo";
            const cliente = (e) => (clientes.find(c => c.id === e.idCliente) || {}).nombreCliente || "—";
            const dias = (s) => SoftecTraza.diasEntre(s.fechaIngresoEquipo, s.fechaEntregaEquipo);

            const enCurso = servicios.filter(s => EN_CURSO.includes(s.estadoServicio));
            const cerrados = servicios.filter(s => ["Finalizado", "Entregado"].includes(s.estadoServicio));
            const vencidos = enCurso.filter(SoftecTraza.vencido);
            const tiempos = cerrados.map(dias).filter(x => x !== null);
            const promedio = tiempos.length ? (tiempos.reduce((a, b) => a + b, 0) / tiempos.length).toFixed(1) + " d" : "—";

            const cola = enCurso.slice().sort((a, b) =>
                (ORDEN[a.prioridad] ?? 9) - (ORDEN[b.prioridad] ?? 9) || (dias(b) || 0) - (dias(a) || 0)).slice(0, 8);

            const tarjeta = (t, v, c = "") => `<div class="col-6 col-md"><div class="border rounded-3 p-2 text-center h-100"><div class="small text-muted">${t}</div><div class="fs-4 fw-bold ${c}">${v}</div></div></div>`;

            panel.innerHTML = `
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                    <div>
                        <h5 class="mb-1" style="color:var(--softec-navy);font-weight:800">🧑‍🔧 Mi trabajo</h5>
                        <div class="small text-muted">${yo ? esc(bonito(yo.especialidad)) + (yo.turno ? " · Turno " + esc(yo.turno) : "") : "Tu cuenta aún no tiene ficha de técnico"}</div>
                    </div>
                    ${yo ? `<div class="d-flex align-items-center gap-2">
                        <label class="small text-muted mb-0" for="tecDisp">Mi disponibilidad</label>
                        <select id="tecDisp" class="form-select form-select-sm" style="width:auto">
                            <option>Disponible</option><option>Ocupado</option><option>Inactivo</option>
                        </select>
                    </div>` : ""}
                </div>
                <div class="row g-2 mb-3">
                    ${tarjeta("Asignados", servicios.length)}${tarjeta("En curso", enCurso.length, "text-warning")}
                    ${tarjeta("Finalizados", cerrados.length, "text-success")}
                    ${tarjeta("Vencidos", vencidos.length, vencidos.length ? "text-danger" : "")}
                    ${tarjeta("Cierre promedio", promedio)}
                </div>
                <h6 class="fw-bold">Mi cola de trabajo <small class="text-muted fw-normal">(ordenada por prioridad y antigüedad)</small></h6>
                ${cola.length ? `<div class="table-responsive"><table class="table table-sm align-middle mb-0">
                    <thead><tr><th>#</th><th>Equipo</th><th>Cliente</th><th>Problema</th><th>Prioridad</th><th>Estado</th><th>Tiempo</th><th></th></tr></thead>
                    <tbody>${cola.map(s => { const e = equipo(s.idEquipo); return `<tr>
                        <td>#${s.id}</td><td>${esc(textoEq(e))}</td><td>${esc(cliente(e))}</td>
                        <td style="white-space:normal;min-width:160px">${esc(s.problemaReportado || "")}</td>
                        <td>${badgeEstado(s.prioridad || "Media")}</td>
                        <td>${badgeEstado(s.estadoServicio)}${SoftecTraza.vencido(s) ? ' <span class="softec-badge softec-badge--cancelada">Vencido</span>' : ""}</td>
                        <td>${dias(s)} d</td>
                        <td style="white-space:nowrap">
                            <a class="btn btn-sm softec-btn-edit" href="servicios.html?avance=${s.id}">🔧 Registrar avance</a>
                            <button class="btn btn-sm softec-btn-edit" onclick="SoftecTraza.servicio(${s.id})">📋</button>
                        </td></tr>`; }).join("")}</tbody></table></div>`
                    : '<p class="text-muted mb-0">No tienes servicios pendientes. 🎉 Cuando la administración te asigne uno, aparecerá aquí.</p>'}
                <div class="mt-2"><a href="servicios.html" class="small">Ver todos mis servicios →</a></div>`;

            if (yo) {
                const sel = document.getElementById("tecDisp");
                sel.value = yo.estadoDisponibilidad || "Disponible";
                sel.addEventListener("change", async () => {
                    try {
                        await api.put(`${ENDPOINTS.tecnicoMe}/disponibilidad`, { estadoDisponibilidad: sel.value });
                        if (typeof SoftecNotify !== "undefined") SoftecNotify.mostrar("Disponibilidad actualizada: " + sel.value, "exito");
                    } catch (err) {
                        alert("No se pudo cambiar la disponibilidad.\n\n" + err.message);
                        sel.value = yo.estadoDisponibilidad;
                    }
                });
            }
        } catch (err) {
            panel.innerHTML = `<div class="text-danger">No se pudo cargar tu panel: ${esc(err.message)}</div>`;
        }
    });
})();

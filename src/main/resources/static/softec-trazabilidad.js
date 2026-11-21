/* ============================================================
   SOFTEC — Trazabilidad (historial de servicios, ficha de cliente y de técnico)
   Requiere api-config.js y softec-utils.js cargados antes.
   Uso: SoftecTraza.servicio(id) · SoftecTraza.cliente({...}) · SoftecTraza.tecnico({...})
   ============================================================ */
const SoftecTraza = (() => {
    const { esc, bonito, badgeEstado, dinero } = SoftecUI;
    const EN_CURSO = ["Pendiente", "En_Proceso"];

    const ACCIONES = {
        CREAR_SERVICIO: "Creó una solicitud de servicio",
        CREAR_SOLICITUD_SERVICIO: "Registró una solicitud (cliente + equipo)",
        MODIFICAR_SERVICIO: "Editó una solicitud de servicio",
        MODIFICAR_ESTADO_MANTENIMIENTO: "Cambió el estado de un servicio",
        REGISTRAR_SEGUIMIENTO: "Registró un avance (estado / diagnóstico / nota)",
        ELIMINAR_SERVICIO: "Eliminó una solicitud de servicio",
        ASIGNAR_TECNICO: "Asignó un técnico a un servicio",
        MODIFICAR_ASIGNACION: "Modificó una asignación",
        ELIMINAR_ASIGNACION: "Quitó una asignación",
        CAMBIAR_DISPONIBILIDAD: "Cambió su disponibilidad",
        CREAR_CLIENTE: "Registró un cliente", MODIFICAR_CLIENTE: "Editó un cliente", ELIMINAR_CLIENTE: "Eliminó un cliente",
        CREAR_TECNICO: "Registró un técnico", MODIFICAR_TECNICO: "Editó un técnico", ELIMINAR_TECNICO: "Eliminó un técnico",
        CREAR_EQUIPO: "Registró un equipo", MODIFICAR_EQUIPO: "Editó un equipo", ELIMINAR_EQUIPO: "Eliminó un equipo",
        MODIFICAR_CONFIGURACION: "Cambió la configuración del sistema",
    };

    const fecha = (v) => {
        if (!v) return "—";
        const d = new Date(v);
        return isNaN(d) ? esc(v) : d.toLocaleString("es-CO", { dateStyle: "medium", timeStyle: "short" });
    };
    const soloFecha = (v) => {
        if (!v) return "—";
        const d = new Date(String(v).length === 10 ? v + "T00:00:00" : v);
        return isNaN(d) ? esc(v) : d.toLocaleDateString("es-CO", { dateStyle: "medium" });
    };
    const diasEntre = (desde, hasta) => {
        if (!desde) return null;
        const a = new Date(String(desde).slice(0, 10) + "T00:00:00");
        const b = hasta ? new Date(String(hasta).slice(0, 10) + "T00:00:00") : new Date();
        return Math.max(0, Math.floor((b - a) / 86400000));
    };
    const limiteDias = () => (window.SoftecConfig && window.SoftecConfig.dias) || 5;
    const vencido = (s) => EN_CURSO.includes(s.estadoServicio) && diasEntre(s.fechaIngresoEquipo) > limiteDias();

    function asegurarModal() {
        if (document.getElementById("trazaModal")) return;
        document.body.insertAdjacentHTML("beforeend", `
            <div class="softec-modal-bg" id="trazaModal" style="z-index:1100">
                <div class="softec-modal-box" style="width:900px;max-height:88vh;overflow:auto">
                    <div class="d-flex justify-content-between align-items-start gap-2">
                        <h5 id="trazaTitulo" class="mb-2"></h5>
                        <button type="button" class="btn-close" aria-label="Cerrar" onclick="SoftecTraza.cerrar()"></button>
                    </div>
                    <div id="trazaCuerpo"></div>
                </div>
            </div>`);
        document.getElementById("trazaModal").addEventListener("click", (e) => { if (e.target.id === "trazaModal") cerrar(); });
    }
    function abrir(titulo, html) {
        asegurarModal();
        document.getElementById("trazaTitulo").innerHTML = titulo;
        document.getElementById("trazaCuerpo").innerHTML = html;
        document.getElementById("trazaModal").style.display = "flex";
    }
    function cerrar() { const m = document.getElementById("trazaModal"); if (m) m.style.display = "none"; }
    const cargando = (t) => abrir(t, '<p class="text-muted">Cargando…</p>');

    const tarjeta = (etiqueta, valor, clase = "") =>
        `<div class="col-6 col-md-3"><div class="border rounded-3 p-2 h-100"><div class="small text-muted">${etiqueta}</div><div class="fs-5 fw-bold ${clase}">${valor}</div></div></div>`;

    /* ---------------- Línea de tiempo de UN servicio ---------------- */
    function lineaDeTiempo(items) {
        if (!items.length) return '<p class="text-muted mb-0">Este servicio todavía no tiene movimientos registrados (los nuevos movimientos aparecerán aquí).</p>';
        return '<ol class="list-unstyled mb-0" style="border-left:3px solid #e2e8f0;margin-left:6px;padding-left:16px">' + items.map(h => {
            const cambio = h.estadoAnterior && h.estadoAnterior !== h.estadoNuevo
                ? `${badgeEstado(h.estadoAnterior)} → ${badgeEstado(h.estadoNuevo)}`
                : badgeEstado(h.estadoNuevo);
            return `<li class="mb-3" style="position:relative">
                <span style="position:absolute;left:-24px;top:4px;width:12px;height:12px;border-radius:50%;background:var(--softec-red)"></span>
                <div class="small text-muted">${fecha(h.fechaHora)} · ${esc(h.usuario || "Sistema")}${h.rol ? " (" + esc(h.rol) + ")" : ""}</div>
                <div>${cambio}</div>
                ${h.nota ? `<div class="mt-1">${esc(h.nota)}</div>` : ""}
            </li>`;
        }).join("") + "</ol>";
    }

    async function servicio(id, resumenHtml = "") {
        cargando(`Historial del servicio #${id}`);
        try {
            const items = await api.get(`${ENDPOINTS.servicios}/${id}/historial`);
            abrir(`📋 Historial del servicio #${id}`, resumenHtml + lineaDeTiempo(items));
        } catch (err) {
            abrir(`Historial del servicio #${id}`, `<p class="text-danger">${esc(err.message)}</p>`);
        }
    }

    /* ---------------- Ficha de un CLIENTE ---------------- */
    async function cliente(c) {
        cargando(`Cliente: ${esc(c.nombre)}`);
        try {
            const [equipos, servicios, facturas, asign, tecnicos, usuarios] = await Promise.all([
                api.get(ENDPOINTS.equipos), api.get(ENDPOINTS.servicios), api.get(ENDPOINTS.facturacion),
                api.get(ENDPOINTS.servicioTecnico).catch(() => []), api.get(ENDPOINTS.tecnicos).catch(() => []),
                api.get(ENDPOINTS.usuarios).catch(() => []),
            ]);
            const nombreTec = new Map(tecnicos.map(t => [t.id, (usuarios.find(u => u.id === t.idUsuario) || {}).nombreUsuario || `Técnico #${t.id}`]));
            const misEquipos = equipos.filter(e => e.idCliente === c.id);
            const idsEq = new Set(misEquipos.map(e => e.id));
            const misServ = servicios.filter(s => idsEq.has(s.idEquipo));
            const misFact = facturas.filter(f => f.idCliente === c.id);
            const enCurso = misServ.filter(s => EN_CURSO.includes(s.estadoServicio)).length;
            const fin = misServ.filter(s => ["Finalizado", "Entregado"].includes(s.estadoServicio)).length;
            const facturado = misFact.filter(f => f.estadoPago !== "Anulado").reduce((a, f) => a + (f.totalPagar || 0), 0);
            const porCobrar = misFact.filter(f => ["Pendiente", "Parcial"].includes(f.estadoPago)).reduce((a, f) => a + (f.totalPagar || 0), 0);

            const filas = misServ.sort((a, b) => b.id - a.id).map(s => {
                const eq = misEquipos.find(e => e.id === s.idEquipo) || {};
                const a = asign.find(x => x.idServicio === s.id);
                const f = misFact.find(x => x.idServicio === s.id);
                const dias = diasEntre(s.fechaIngresoEquipo, s.fechaEntregaEquipo);
                return `<tr>
                    <td>#${s.id}</td>
                    <td>${esc([eq.tipoEquipo, eq.marca, eq.modelo].filter(Boolean).join(" "))}${eq.numeroSerie ? `<div class="small text-muted">S/N ${esc(eq.numeroSerie)}</div>` : ""}</td>
                    <td>${esc(bonito(s.tipoServicio))}<div class="small text-muted">${esc(s.problemaReportado || "")}</div></td>
                    <td>${badgeEstado(s.estadoServicio)}${vencido(s) ? ' <span class="softec-badge softec-badge--cancelada">Vencido</span>' : ""}</td>
                    <td>${soloFecha(s.fechaIngresoEquipo)}<div class="small text-muted">${s.fechaEntregaEquipo ? "Entrega: " + soloFecha(s.fechaEntregaEquipo) : (dias !== null ? dias + " día(s)" : "")}</div></td>
                    <td>${a ? esc(nombreTec.get(a.idTecnico) || `Técnico #${a.idTecnico}`) : '<span class="text-muted">Sin asignar</span>'}</td>
                    <td>${f ? `${esc(f.numeroFactura)}<div class="small">${badgeEstado(f.estadoPago)} ${dinero(f.totalPagar)}</div>` : '<span class="text-muted">—</span>'}</td>
                    <td><button class="btn btn-sm softec-btn-edit" onclick="SoftecTraza.servicio(${s.id})">📋 Historial</button></td>
                </tr>`;
            }).join("");

            abrir(`👤 ${esc(c.nombre)} <small class="text-muted fs-6">· Cliente #${c.id}</small>`, `
                <div class="small text-muted mb-3">
                    ${c.documento ? "Documento: " + esc(c.documento) + " · " : ""}${c.telefono ? "Tel: " + esc(c.telefono) + " · " : ""}${c.email ? esc(c.email) : ""}
                </div>
                <div class="row g-2 mb-3">
                    ${tarjeta("Equipos", misEquipos.length)}${tarjeta("Servicios", misServ.length)}
                    ${tarjeta("En curso", enCurso, "text-warning")}${tarjeta("Finalizados", fin, "text-success")}
                    ${tarjeta("Total facturado", dinero(facturado))}${tarjeta("Por cobrar", dinero(porCobrar), porCobrar > 0 ? "text-danger" : "")}
                </div>
                <h6 class="fw-bold">Equipos y servicios</h6>
                ${misServ.length ? `<div class="table-responsive"><table class="table table-sm align-middle">
                    <thead><tr><th>#</th><th>Equipo</th><th>Servicio</th><th>Estado</th><th>Fechas</th><th>Técnico</th><th>Factura</th><th></th></tr></thead>
                    <tbody>${filas}</tbody></table></div>` : '<p class="text-muted">Este cliente todavía no tiene servicios registrados.</p>'}
                ${misEquipos.filter(e => !misServ.some(s => s.idEquipo === e.id)).length ? `<div class="small text-muted">Equipos sin servicio: ${misEquipos.filter(e => !misServ.some(s => s.idEquipo === e.id)).map(e => esc([e.tipoEquipo, e.marca, e.modelo].filter(Boolean).join(" "))).join(", ")}</div>` : ""}
            `);
        } catch (err) {
            abrir(`Cliente: ${esc(c.nombre)}`, `<p class="text-danger">${esc(err.message)}</p>`);
        }
    }

    /* ---------------- Ficha de un TÉCNICO ---------------- */
    async function tecnico(t) {
        cargando(`Técnico: ${esc(t.nombre)}`);
        try {
            const [servicios, asign, equipos, clientes, actividad] = await Promise.all([
                api.get(ENDPOINTS.servicios), api.get(ENDPOINTS.servicioTecnico), api.get(ENDPOINTS.equipos),
                api.get(ENDPOINTS.clientes), api.get(`${ENDPOINTS.auditoria}?idUsuario=${t.idUsuario}&limite=15`).catch(() => []),
            ]);
            const ids = new Set(asign.filter(a => a.idTecnico === t.id).map(a => a.idServicio));
            const mios = servicios.filter(s => ids.has(s.id)).sort((a, b) => b.id - a.id);
            const enCurso = mios.filter(s => EN_CURSO.includes(s.estadoServicio));
            const cerrados = mios.filter(s => ["Finalizado", "Entregado"].includes(s.estadoServicio));
            const tiempos = cerrados.map(s => diasEntre(s.fechaIngresoEquipo, s.fechaEntregaEquipo)).filter(x => x !== null);
            const promedio = tiempos.length ? (tiempos.reduce((a, b) => a + b, 0) / tiempos.length).toFixed(1) + " días" : "—";
            const nVenc = enCurso.filter(vencido).length;

            const filas = mios.map(s => {
                const eq = equipos.find(e => e.id === s.idEquipo) || {};
                const cli = clientes.find(c => c.id === eq.idCliente) || {};
                return `<tr>
                    <td>#${s.id}</td>
                    <td>${esc([eq.tipoEquipo, eq.marca, eq.modelo].filter(Boolean).join(" "))}</td>
                    <td>${esc(cli.nombreCliente || "—")}</td>
                    <td>${badgeEstado(s.estadoServicio)}${vencido(s) ? ' <span class="softec-badge softec-badge--cancelada">Vencido</span>' : ""}</td>
                    <td>${soloFecha(s.fechaIngresoEquipo)}</td>
                    <td><button class="btn btn-sm softec-btn-edit" onclick="SoftecTraza.servicio(${s.id})">📋 Historial</button></td>
                </tr>`;
            }).join("");

            const act = actividad.length
                ? '<ul class="list-unstyled mb-0">' + actividad.map(a => `<li class="py-1 border-bottom small"><strong>${esc(ACCIONES[a.accion] || bonito(a.accion))}</strong> <span class="text-muted">· ${fecha(a.fechaHora)}</span></li>`).join("") + "</ul>"
                : '<p class="text-muted mb-0">Sin actividad registrada todavía.</p>';

            abrir(`🧑‍🔧 ${esc(t.nombre)} <small class="text-muted fs-6">· Técnico #${t.id}</small>`, `
                <div class="small text-muted mb-3">
                    ${esc(bonito(t.especialidad))}${t.turno ? " · Turno " + esc(t.turno) : ""} · ${esc(t.disponibilidad || "")}
                    ${t.telefono ? " · Tel: " + esc(t.telefono) : ""}${t.email ? " · " + esc(t.email) : ""}
                </div>
                <div class="row g-2 mb-3">
                    ${tarjeta("Asignados", mios.length)}${tarjeta("En curso", enCurso.length, "text-warning")}
                    ${tarjeta("Finalizados", cerrados.length, "text-success")}${tarjeta("Vencidos", nVenc, nVenc ? "text-danger" : "")}
                    ${tarjeta("Tiempo promedio de cierre", promedio)}
                </div>
                <h6 class="fw-bold">Servicios asignados</h6>
                ${mios.length ? `<div class="table-responsive"><table class="table table-sm align-middle">
                    <thead><tr><th>#</th><th>Equipo</th><th>Cliente</th><th>Estado</th><th>Ingreso</th><th></th></tr></thead>
                    <tbody>${filas}</tbody></table></div>` : '<p class="text-muted">Este técnico todavía no tiene servicios asignados.</p>'}
                <h6 class="fw-bold mt-3">Actividad reciente</h6>
                ${act}
            `);
        } catch (err) {
            abrir(`Técnico: ${esc(t.nombre)}`, `<p class="text-danger">${esc(err.message)}</p>`);
        }
    }

    return { servicio, cliente, tecnico, cerrar, fecha, soloFecha, diasEntre, vencido, limiteDias, lineaDeTiempo, ACCIONES };
})();

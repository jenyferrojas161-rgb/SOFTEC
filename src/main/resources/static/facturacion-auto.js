/* ============================================================
   SOFTEC — Facturación automática
   ------------------------------------------------------------
   - La factura se genera sola cuando un servicio llega a
     "Finalizado" (o "Entregado").
   - El número de factura es digital y autogenerado:
         FAC-AAAA-NNNNNN   (ej. FAC-2026-000001)
     Se calcula a partir de la última factura del año y, si otro
     usuario toma el mismo número al mismo tiempo, se reintenta.
   - Una factura generada NO se edita ni se elimina desde la
     interfaz. Lo único que cambia con el tiempo es el registro
     del pago (Pendiente -> Pagado).
   Requiere api-config.js (api, ENDPOINTS) cargado antes.
   ============================================================ */
const SoftecFacturacion = (() => {
    "use strict";

    /* ---------- CONFIGURACIÓN (ajusta estos valores a tu taller) ---------- */

    // Datos del emisor que salen en la factura y en el PDF.
    // REEMPLAZAR: son datos de ejemplo.
    const EMPRESA = {
        razonSocial: "SOFTEC S.A.S.",
        nit: "900.123.456-7",
        direccion: "Calle 45 # 12-30, Bogotá D.C., Colombia",
        email: "soporte@softec.com",
        telefono: "+57 300 000 0000",
    };

    // El servicio no guarda un valor, así que la factura automática usa una
    // tarifa base por tipo de servicio (antes de IVA, en COP).
    // REEMPLAZAR: son valores de ejemplo.
    const TARIFAS_BASE = {
        "mantenimiento": 80000,
        "reparacion": 120000,
        "diagnostico": 50000,
        "instalacion": 90000,
    };
    const TARIFA_POR_DEFECTO = 80000;

    const IVA = 0.19;
    const PREFIJO = "FAC";
    const TIPO_COMPROBANTE = "Factura Electrónica";
    const ESTADOS_FACTURABLES = ["Finalizado", "Entregado"];

    /* ---------- Utilidades ---------- */
    const normalizar = (t) =>
        String(t || "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();

    const esFacturable = (estado) => ESTADOS_FACTURABLES.includes(estado);

    const tarifaBase = (tipoServicio) => TARIFAS_BASE[normalizar(tipoServicio)] ?? TARIFA_POR_DEFECTO;

    const hoy = () => {
        const d = new Date();
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    }; // fecha local (toISOString daría el día siguiente en la noche en Colombia)

    function siguienteNumero(facturas, anio = new Date().getFullYear()) {
        const re = new RegExp(`^${PREFIJO}-${anio}-(\\d+)$`);
        let max = 0;
        (facturas || []).forEach((f) => {
            const m = re.exec(f.numeroFactura || "");
            if (m) max = Math.max(max, Number(m[1]));
        });
        return `${PREFIJO}-${anio}-${String(max + 1).padStart(6, "0")}`;
    }

    // Las generaciones se hacen de una en una para no pedir el mismo número dos veces
    let cola = Promise.resolve();
    function enSerie(fn) {
        const p = cola.then(fn);
        cola = p.catch(() => { });
        return p;
    }

    const textoEquipo = (eq) =>
        eq ? `${eq.tipoEquipo || ""} ${eq.marca || ""} ${eq.modelo || ""}`.replace(/\s+/g, " ").trim() : "";

    // Crea la factura; si el backend exige método de pago, reintenta con uno por defecto.
    // Mientras la factura esté pendiente, la pantalla muestra "Por definir".
    async function postFactura(payload) {
        try {
            return await api.post(ENDPOINTS.facturacion, payload);
        } catch (err) {
            if (err.campo === "numeroFactura" || err.status === 401 || err.status === 403 || payload.metodoPago !== null) throw err;
            return api.post(ENDPOINTS.facturacion, { ...payload, metodoPago: "Efectivo" });
        }
    }

    /* ---------- Generación de la factura de UN servicio ---------- */
    function facturarServicio(servicio, equipos) {
        return enSerie(async () => {
            if (!servicio || !esFacturable(servicio.estadoServicio)) {
                throw new Error("El servicio todavía no está finalizado.");
            }
            const equipo = (equipos || []).find((e) => e.id == servicio.idEquipo);
            if (!equipo || !equipo.idCliente) {
                throw new Error(`No se encontró el cliente del servicio #${servicio.id}.`);
            }

            const subtotal = tarifaBase(servicio.tipoServicio);
            const total = Math.round(subtotal * (1 + IVA) * 100) / 100;
            const descripcion = `${servicio.tipoServicio || "Servicio técnico"}${textoEquipo(equipo) ? " — " + textoEquipo(equipo) : ""}`;

            for (let intento = 0; intento < 5; intento++) {
                const existentes = await api.get(ENDPOINTS.facturacion);
                const previa = existentes.find((f) => f.idServicio == servicio.id);
                if (previa) return { factura: previa, yaExistia: true };

                const payload = {
                    idServicio: Number(servicio.id),
                    idCliente: Number(equipo.idCliente),
                    numeroFactura: siguienteNumero(existentes),
                    fechaFactura: hoy(),
                    totalPagar: total,
                    tipoComprobante: TIPO_COMPROBANTE,
                    metodoPago: null,
                    estadoPago: "Pendiente",
                };

                let creada;
                try {
                    creada = await postFactura(payload);
                } catch (err) {
                    if (err.campo === "numeroFactura") continue; // otro usuario tomó ese número: se recalcula
                    throw err;
                }

                try {
                    await api.post(ENDPOINTS.detallesFacturacion, {
                        idFacturacion: creada.id,
                        descripcion,
                        cantidad: 1,
                        precio: subtotal,
                        total: subtotal,
                    });
                } catch (err) {
                    // No se deja una factura sin su detalle
                    try { await api.del(`${ENDPOINTS.facturacion}/${creada.id}`); } catch (_) { }
                    throw new Error("No se pudo registrar el detalle de la factura; se canceló su generación.");
                }
                return { factura: creada, yaExistia: false };
            }
            throw new Error("No se pudo asignar un número de factura. Inténtalo de nuevo.");
        });
    }

    /* ---------- Genera las facturas de todos los servicios finalizados que no tengan ---------- */
    async function sincronizar(servicios, equipos) {
        const facturas = await api.get(ENDPOINTS.facturacion);
        const conFactura = new Set(facturas.map((f) => String(f.idServicio)));
        const pendientes = (servicios || []).filter((s) => esFacturable(s.estadoServicio) && !conFactura.has(String(s.id)));

        const generadas = [];
        const errores = [];
        for (const s of pendientes) {
            try {
                const r = await facturarServicio(s, equipos);
                if (!r.yaExistia) generadas.push(r.factura);
            } catch (err) {
                errores.push({ servicio: s, mensaje: err.message });
            }
        }
        return { generadas, errores, revisados: pendientes.length };
    }

    return { EMPRESA, IVA, ESTADOS_FACTURABLES, esFacturable, tarifaBase, siguienteNumero, facturarServicio, sincronizar };
})();

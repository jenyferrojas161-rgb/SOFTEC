/* ============================================================
   SOFTEC — Dirección del backend según el entorno
   - En el computador local (localhost / 127.0.0.1 / archivo): http://localhost:8081/api
   - En otro servidor: el mismo host del frontend en el puerto 8081.
   - Para usar otra dirección, define window.SOFTEC_API_BASE en un <script>
     ANTES de cargar este archivo. Ejemplo:
       <script>window.SOFTEC_API_BASE = "https://api.mi-dominio.com/api";</script>
   ============================================================ */
(function () {
    if (window.SOFTEC_API_BASE) return;
    var h = location.hostname;
    var local = h === "" || h === "localhost" || h === "127.0.0.1";
    window.SOFTEC_API_BASE = local
        ? "http://localhost:8081/api"
        : location.origin + "/api";
})();

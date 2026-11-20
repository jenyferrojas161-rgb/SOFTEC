# API de SOFTEC (referencia de endpoints)

Generada a partir de los controladores del backend. Base: `http://localhost:8081`. Todas las respuestas son JSON; los errores tienen la forma `{"message": "..."}`.

- **Autenticación:** enviar `Authorization: Bearer <token>` (el token se obtiene en `POST /api/auth/login`).
- **Acceso:** *Público* no necesita sesión; *Usuario con sesión* acepta cualquier rol autenticado, pero el servicio filtra los datos (el técnico y el cliente solo ven lo suyo); los demás indican los roles permitidos.
- **`GET /api/notificaciones/stream`:** es público en la configuración, pero exige un token JWT válido en el parámetro `?token=` (limitación de `EventSource`, que no permite cabeceras).
- **`GET /api/servicio-tecnico/buscar`:** consulta pública limitada a 30 peticiones por IP cada 15 minutos (responde 429 al pasarse).
- **Auditoría:** si aparece una acción, queda registrada en la tabla `audit_logs`.
- **Códigos comunes:** 400 datos no válidos · 401 sin sesión · 403 sin permiso · 409 conflicto · 429 demasiados intentos · 500 error del servidor.

| Método | Ruta | Acceso | Auditoría | Controlador |
|---|---|---|---|---|
| GET | `/api/auditoria` | ADMIN | — | auditoriaController |
| POST | `/api/auth/login` | Público | — | authController |
| POST | `/api/auth/registro` | Público | — | authController |
| POST | `/api/auth/reset-password` | Público | — | authController |
| GET | `/api/clientes` | ADMIN, CLIENTE, TECNICO | — | clienteController |
| POST | `/api/clientes` | ADMIN | CREAR_CLIENTE | clienteController |
| DELETE | `/api/clientes/{id}` | ADMIN | ELIMINAR_CLIENTE | clienteController |
| PUT | `/api/clientes/{id}` | ADMIN | MODIFICAR_CLIENTE | clienteController |
| PUT | `/api/configuracion` | ADMIN | MODIFICAR_CONFIGURACION | ConfiguracionController |
| GET | `/api/configuracion/publica` | Público | — | ConfiguracionController |
| GET | `/api/detalles-facturacion` | ADMIN | — | detallesFacturacionController |
| POST | `/api/detalles-facturacion` | ADMIN | CREAR_DETALLE_FACTURA | detallesFacturacionController |
| DELETE | `/api/detalles-facturacion/{id}` | ADMIN | ELIMINAR_DETALLE_FACTURA | detallesFacturacionController |
| PUT | `/api/detalles-facturacion/{id}` | ADMIN | MODIFICAR_DETALLE_FACTURA | detallesFacturacionController |
| GET | `/api/equipos` | Usuario con sesión | — | equipoController |
| POST | `/api/equipos` | ADMIN | CREAR_EQUIPO | equipoController |
| DELETE | `/api/equipos/{id}` | ADMIN | ELIMINAR_EQUIPO | equipoController |
| PUT | `/api/equipos/{id}` | ADMIN | MODIFICAR_EQUIPO | equipoController |
| GET | `/api/facturacion` | ADMIN | — | facturacionController |
| POST | `/api/facturacion` | ADMIN | EMITIR_FACTURA | facturacionController |
| GET | `/api/facturacion/mis-facturas` | CLIENTE | — | facturacionController |
| DELETE | `/api/facturacion/{id}` | ADMIN | ELIMINAR_FACTURA | facturacionController |
| PUT | `/api/facturacion/{id}` | ADMIN | MODIFICAR_FACTURA | facturacionController |
| GET | `/api/notificaciones/mias` | Usuario con sesión | — | notificacionesController |
| PUT | `/api/notificaciones/mias/leidas` | Usuario con sesión | — | notificacionesController |
| GET | `/api/notificaciones/stream` | Público | — | notificacionesController |
| GET | `/api/public/contact` | Público | — | publicContactController |
| GET | `/api/public/stats` | Público | — | publicStatsController |
| GET | `/api/servicio-tecnico` | ADMIN, TECNICO | — | servicioTecnicoController |
| POST | `/api/servicio-tecnico` | ADMIN | ASIGNAR_TECNICO | servicioTecnicoController |
| GET | `/api/servicio-tecnico/buscar` | Público | — | servicioTecnicoController |
| DELETE | `/api/servicio-tecnico/{idServicio}/{idTecnico}` | ADMIN | ELIMINAR_ASIGNACION | servicioTecnicoController |
| PUT | `/api/servicio-tecnico/{idServicio}/{idTecnico}` | ADMIN | MODIFICAR_ASIGNACION | servicioTecnicoController |
| GET | `/api/servicios` | Usuario con sesión | — | servicioController |
| POST | `/api/servicios` | ADMIN | CREAR_SERVICIO | servicioController |
| POST | `/api/servicios/solicitud` | ADMIN | CREAR_SOLICITUD_SERVICIO | servicioController |
| DELETE | `/api/servicios/{id}` | ADMIN | ELIMINAR_SERVICIO | servicioController |
| PUT | `/api/servicios/{id}` | ADMIN | MODIFICAR_SERVICIO | servicioController |
| PUT | `/api/servicios/{id}/estado` | ADMIN, TECNICO | MODIFICAR_ESTADO_MANTENIMIENTO | servicioController |
| GET | `/api/servicios/{id}/historial` | Usuario con sesión | — | servicioController |
| PUT | `/api/servicios/{id}/seguimiento` | ADMIN, TECNICO | REGISTRAR_SEGUIMIENTO | servicioController |
| GET | `/api/tecnicos` | ADMIN | — | tecnicoController |
| POST | `/api/tecnicos` | ADMIN | CREAR_TECNICO | tecnicoController |
| GET | `/api/tecnicos/me` | TECNICO | — | tecnicoPanelController |
| PUT | `/api/tecnicos/me/disponibilidad` | TECNICO | CAMBIAR_DISPONIBILIDAD | tecnicoPanelController |
| DELETE | `/api/tecnicos/{id}` | ADMIN | ELIMINAR_TECNICO | tecnicoController |
| PUT | `/api/tecnicos/{id}` | ADMIN | MODIFICAR_TECNICO | tecnicoController |
| GET | `/api/usuarios` | ADMIN | — | usuarioController |
| POST | `/api/usuarios` | ADMIN | CREAR_USUARIO | usuarioController |
| DELETE | `/api/usuarios/{id}` | ADMIN | ELIMINAR_USUARIO | usuarioController |
| PUT | `/api/usuarios/{id}` | ADMIN | MODIFICAR_USUARIO | usuarioController |

> Si cambian los controladores, hay que actualizar esta tabla. Una mejora futura sería generarla automáticamente con SpringDoc/OpenAPI.

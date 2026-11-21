# SOFTEC — Evolución de la plataforma de servicio técnico

## 1. Visión

SOFTEC nació como una propuesta de digitalización para pequeñas y medianas empresas dedicadas al mantenimiento tecnológico. Su evolución se ha orientado a convertir la operación tradicional del taller en un flujo digital trazable, accesible y conectado con información operativa en tiempo real.

La plataforma integra recepción de equipos, clientes, servicios técnicos, asignación de técnicos, seguimiento, facturación, reportes y atención al cliente desde una experiencia web unificada.

## 2. Hitos de evolución

### Hito 1 — Organización del proceso técnico

La primera etapa estructuró el ciclo de atención: recepción, diagnóstico, presupuesto, reparación, seguimiento y entrega. Esta base permitió transformar el flujo operativo del taller en módulos funcionales.

### Hito 2 — Integración de información

La plataforma consolidó usuarios, clientes, equipos, servicios, técnicos y facturación sobre PostgreSQL mediante una API REST desarrollada con Java y Spring Boot. La interfaz web consume los datos directamente del sistema central.

### Hito 3 — Experiencia por roles

SOFTEC evolucionó hacia una navegación diferenciada para Administradores, Técnicos, Recepcionistas y Clientes. El menú, las vistas y las acciones disponibles se adaptan al rol autenticado.

### Hito 4 — Seguridad y trazabilidad

La autenticación utiliza tokens JWT y las contraseñas se almacenan mediante BCrypt. La plataforma aplica una política de contraseña de mínimo 10 caracteres con mayúscula, minúscula, número y carácter especial. Las acciones administrativas y operativas relevantes quedan registradas en `audit_logs` con usuario, acción, fecha y ruta.

### Hito 5 — Operación conectada

Los cambios de estado de los servicios pueden generar notificaciones en tiempo real mediante SSE. El cliente recibe información cuando un mantenimiento cambia de estado o llega a una etapa de finalización.

### Hito 6 — Atención digital

El asistente virtual SOFTEC incorpora consultas reales al backend para localizar servicios por número de orden o cédula. También orienta sobre servicios, tarifas, horarios y canales de contacto sin sustituir la información oficial registrada en la plataforma.

### Hito 7 — Madurez continua

La arquitectura se mantiene preparada para incorporar nuevos módulos, automatizaciones, indicadores, integraciones externas y capacidades móviles sin perder la separación entre presentación, servicios de negocio, seguridad y persistencia.

## 3. Arquitectura

- **Frontend:** HTML5, CSS3, JavaScript ES6, Bootstrap y componentes reutilizables.
- **Backend:** Java 17, Spring Boot, Spring MVC, Spring Data JPA y Spring Security.
- **Persistencia:** PostgreSQL.
- **Autenticación:** JWT firmado.
- **Contraseñas:** BCrypt.
- **Auditoría:** tabla `audit_logs`.
- **Notificaciones:** Server-Sent Events (SSE).
- **Comunicación:** API REST JSON.

## 4. Principios de diseño

1. La interfaz no reemplaza la seguridad del servidor.
2. PostgreSQL es la fuente de verdad de la información operativa.
3. Los roles determinan permisos reales en el backend y experiencia de navegación en el frontend.
4. La facturación tiene controles de inmutabilidad para registros procesados.
5. Las consultas de atención al cliente deben devolver información proveniente de la plataforma.
6. Los botones principales mantienen un área táctil mínima de 48 px.
7. La navegación es responsive para escritorio, tablet y móvil.

## 5. Ejecución

1. Crear o seleccionar la base `softech_db` en PostgreSQL.
2. Ejecutar `sql/softech_database_postgres.sql`.
3. Ejecutar `sql/softech_seed_data_30.sql` para disponer de datos iniciales.
4. Ejecutar el backend Spring Boot en el puerto `8081`.
5. Servir esta carpeta con Live Server en `127.0.0.1:5500` o `localhost:5500`.
6. Abrir `index.html` o `login.html`.

## 6. Contacto configurable

La dirección, teléfono, WhatsApp y horario de SOFTEC se parametrizan en el backend mediante las propiedades `softec.contact.*` o sus variables de entorno equivalentes. De esta forma, la información institucional puede actualizarse sin modificar la lógica del chatbot.

## 7. Credenciales de demostración

Las credenciales de demostración se encuentran documentadas en `sql/softec_demo_credentials.txt`. Deben cambiarse antes de cualquier despliegue real.


---

## Dirección del backend (actualizado)

La dirección del API ya no está escrita en cada archivo. Se define en `softec-env.js`:

- En `localhost`, `127.0.0.1` o abriendo el archivo directamente usa `http://localhost:8081/api`.
- En otro servidor usa el mismo host del frontend en el puerto `8081`.
- Para usar otra dirección, definan `window.SOFTEC_API_BASE` en un `<script>` antes de cargar `softec-env.js`:

```html
<script>window.SOFTEC_API_BASE = "https://api.mi-dominio.com/api";</script>
<script src="softec-env.js"></script>
```

Recuerden agregar la dirección del frontend en `SOFTEC_CORS_ORIGINS` del backend.

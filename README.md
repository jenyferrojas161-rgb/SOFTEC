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

## Configuración por entorno (actualizado)

Las claves ya no están escritas en `application.properties`.

- **Desarrollo local:** no hay que hacer nada. Sin otro perfil se activa `dev` (`application-dev.properties`), que trae la clave de PostgreSQL y un secreto JWT **solo para trabajar en el computador**. Cambien la clave si usan otra en su PostgreSQL.
- **Servidor real:** activar el perfil `prod` (`SPRING_PROFILES_ACTIVE=prod`) y definir las variables de entorno de `.env.example`:

| Variable | Para qué sirve |
|---|---|
| `SOFTEC_DB_PASSWORD` | Contraseña de PostgreSQL (obligatoria en `prod`) |
| `SOFTEC_JWT_SECRET` | Secreto JWT en Base64, mínimo 256 bits. Se genera con `openssl rand -base64 32` (obligatoria en `prod`) |
| `SOFTEC_CORS_ORIGINS` | Direcciones del frontend permitidas, separadas por coma |
| `SOFTEC_DB_URL`, `SOFTEC_DB_USER`, `SOFTEC_PORT` | Opcionales |

Si falta el secreto JWT o la contraseña en `prod`, la aplicación no arranca (a propósito).

## Pruebas automatizadas

Hay pruebas unitarias en `src/test` para la política de contraseñas, el limitador de intentos, los tokens JWT, la regla de facturas inmutables y el manejador de errores.

```bash
./mvnw test
```

## Docker

```bash
cp .env.example .env     # editar los valores
docker compose up --build
```

Levanta PostgreSQL (con el esquema y los datos de demostración cargados en el orden correcto) y el backend en el puerto 8081.

## Copias de seguridad

```bash
SOFTEC_DB_PASSWORD=su_clave ./scripts/backup.sh
```

Crea un `.sql.gz` en `backups/` y borra los de más de 14 días. La forma de restaurar y de programarlo con cron está en el encabezado del script.

## Documentación de la API

Todos los endpoints, con su rol requerido y la acción de auditoría, están en [`docs/API.md`](docs/API.md).

## Validación de datos

Login, registro y recuperación de contraseña validan los datos con Bean Validation (`@Valid`) antes de llegar a la lógica de negocio. Los errores se devuelven como `{"message": "..."}` con código 400. La consulta pública `GET /api/servicio-tecnico/buscar` está limitada a 30 peticiones por IP cada 15 minutos.

## Limitaciones conocidas

- El token del flujo de notificaciones en tiempo real (SSE) viaja en la URL, porque `EventSource` no permite cabeceras. Para quitarlo habría que cambiar el frontend a `fetch` con stream.
- La búsqueda pública acepta número de orden **o** cédula por separado. Exigir las dos juntas sería más seguro, pero cambia cómo funciona el chatbot.
- El contador de intentos fallidos vive en memoria: se reinicia con el servidor y no se comparte entre varias instancias.


## Frontend integrado (Render)

Las paginas HTML y las imagenes institucionales se encuentran en `src/main/resources/static`. Spring Boot las sirve desde el mismo dominio que la API: abre `/` para `index.html` y `/login.html` para el inicio de sesion. Los recursos estaticos son publicos; los endpoints `/api/**` conservan las reglas de autenticacion y autorizacion configuradas en Spring Security.

**Alcance actual:** integrar los archivos permite servir el frontend y el backend desde una sola aplicacion y direccion. No convierte automaticamente todos los formularios y botones HTML en operaciones conectadas a la API. Es necesario revisar y conectar cada formulario/modulo con sus endpoints; las paginas `clientes.html` y `tecnicos.html` enlazadas desde el dashboard tampoco venian en el paquete de frontend recibido.

Para Render, configura `SPRING_PROFILES_ACTIVE=prod`, `SOFTEC_DB_URL`, `SOFTEC_DB_USER`, `SOFTEC_DB_PASSWORD` y `SOFTEC_JWT_SECRET`. Render asigna `PORT` automaticamente y la aplicacion lo utiliza. El perfil `prod` desactiva la ejecucion automatica de `schema.sql`; crea/importa el esquema de PostgreSQL una sola vez antes de iniciar la aplicacion. No ejecutes el script SQL que contiene `DROP TABLE` sobre una base con datos que quieras conservar.

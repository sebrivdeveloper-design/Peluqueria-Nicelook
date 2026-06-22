# CHANGES_V2 — Evolución del sistema NICE LOOK

Evolución incremental sobre el backlog inicial (44 HU, ver `BACKLOG_STATUS.md`).
Stack sin cambios: **Spring Boot 3.5 / Java 21 / JPA / PostgreSQL** + **Vue 3 / Vite**.

Decisiones de alcance acordadas con el equipo:
- **Renombrado Empleado → Estilista:** solo a nivel de **UI y código nuevo**. La tabla
  `empleado`, sus columnas, repositorios, endpoints (`/api/empleados`) y el rol interno
  `EMPLEADO` se conservan para no romper las 44 HU existentes ni los datos. "Estilista" es
  el término visible para el usuario.
- **Persistencia de esquema:** el proyecto usa `spring.jpa.hibernate.ddl-auto=update`, así
  que Hibernate crea/actualiza las tablas y columnas nuevas automáticamente al arrancar.
  `tablas_base.sql` se mantiene como documentación del esquema resultante.

Estado de implementación:

| Cambio | Descripción | Estado |
|--------|-------------|--------|
| **1** | Estilistas + comisión por arrendamiento | ✅ Implementado |
| **2** | Historial de servicios/pagos por estilista | ✅ Implementado |
| **3** | Reporte de saldos de caja por día | ✅ Implementado |
| **4** | Landing pública de reservas con OTP | ✅ Implementado |

---

## CAMBIO 1 — Estilistas con modelo de comisión por arrendamiento ✅

### Modelo de datos (antes → después)

**Antes:** `empleado.salario` (salario fijo) + `servicio.porcentaje_empleado` (comisión por
porcentaje, HU-29/30). La cita no guardaba ningún valor económico; el `pago` guardaba solo
`monto_total`.

**Después:**
- Nueva entidad **`arrendamiento`** (tarifa de uso del salón por servicio realizado).
- La **`cita`** congela 4 valores económicos al cobrarse (históricos e inmutables).
- `empleado.salario` queda **deprecado** (se conserva la columna, no se usa).

```
arrendamiento
  id_arrendamiento     PK
  id_empleado          FK→empleado  (NULL = aplica a todos → valor por defecto)
  id_servicio          FK→servicio  (NULL = tarifa general del estilista)
  valor                DECIMAL(10,2)
  fecha_actualizacion  TIMESTAMP

cita  (columnas nuevas, nullable; solo las citas ya cobradas las tienen)
  valor_servicio            DECIMAL(10,2)
  valor_arrendamiento       DECIMAL(10,2)
  valor_a_pagar_estilista   DECIMAL(10,2)
  valor_para_salon          DECIMAL(10,2)
```

### Regla de resolución de la tarifa (más específico → más general)

Implementada en `ArrendamientoService.resolver(idEmpleado, idServicio)`:
1. Tarifa para esa combinación **estilista + servicio**, si existe.
2. Si no, tarifa **general del estilista** (`id_servicio = NULL`).
3. Si no, **valor por defecto** del salón (`id_empleado = NULL, id_servicio = NULL`).
4. Si nada está configurado → `0` (el salón no retiene nada).

### Cálculo y congelamiento de la comisión

Se calcula **al registrar el pago** (`POST /api/pagos`), que es el momento en que la cita
está *finalizada y pagada*. Los valores quedan congelados en la `cita`:
- `valor_servicio` = precio del servicio.
- `valor_arrendamiento` = tarifa resuelta (regla anterior).
- `valor_a_pagar_estilista` = `valor_servicio − valor_arrendamiento`.
- `valor_para_salon` = `valor_arrendamiento`.

No se recalculan después aunque cambien las tarifas (cumple la inmutabilidad pedida).

### Validación de pago negativo

Si `valor_arrendamiento > valor_servicio`, el pago sería negativo para el estilista. El
backend responde `409 CONFLICT` con `{ requiereConfirmacion: true, mensaje, valorServicio,
valorArrendamiento }` y **no registra el pago**. La UI (PagosView) muestra la advertencia y
solo continúa si el operador confirma la excepción explícitamente
(`confirmarExcepcion = true` en el cuerpo del POST). Regla documentada en el código de
`PagoController.registrarPago`.

### Endpoints nuevos (`/api/arrendamientos`, solo ADMIN)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/arrendamientos` | Lista todas las tarifas (default + específicas) |
| GET | `/api/arrendamientos/default` | Valor por defecto del salón |
| GET | `/api/arrendamientos/resolver?idEmpleado&idServicio` | Previsualiza el valor que se aplicaría |
| PUT | `/api/arrendamientos/default` | Crea/actualiza el valor por defecto |
| POST | `/api/arrendamientos` | Crea/actualiza tarifa por estilista o estilista+servicio (upsert) |
| DELETE | `/api/arrendamientos/{id}` | Elimina una sobrescritura (no el valor por defecto) |

`POST /api/pagos` ahora acepta el campo opcional `confirmarExcepcion`.
`GET /api/pagos/pendientes` ahora incluye `valorArrendamiento` y `valorAPagarEstilista`.

### Archivos

**Backend — nuevos**
- `model/Arrendamiento.java`
- `repository/ArrendamientoRepository.java`
- `service/ArrendamientoService.java`
- `controller/ArrendamientoController.java`

**Backend — modificados**
- `model/Cita.java` — 4 columnas de comisión congelada.
- `model/Empleado.java` — `salario` marcado `@Deprecated`.
- `service/EmpleadoService.java` — no sobrescribe `salario` con null al editar (preserva histórico).
- `controller/PagoController.java` — cálculo/congelamiento de comisión + validación de pago negativo + reparto en `/pendientes`.
- `Segurity/SecurityConfig.java` — `/api/arrendamientos/**` restringido a `ADMIN`.

**Frontend — nuevos**
- `views/ArrendamientoView.vue` — CRUD de tarifas (valor por defecto + específicas).
- `services/arrendamientoService.js`

**Frontend — modificados**
- `router/index.js` — ruta `/admin/arrendamiento`.
- `components/Sidebar.vue` — nav "Arrendamiento"; "Empleados"→"Estilistas"; "Pagos a empleados"→"Pagos a estilistas".
- `components/EmpleadoModal.vue` — textos a "estilista", rol "Estilista", eliminado campo Salario.
- `views/EmpleadosView.vue` — textos a "Estilistas", eliminada columna Salario.
- `views/PagosView.vue` — muestra reparto (arrendamiento / a pagar estilista) y maneja la confirmación de pago negativo.
- `services/pagoService.js` — `registrarPago` admite `confirmarExcepcion`.

### Cómo verificar que nada se rompió

1. **Arranque:** levantar backend (Hibernate crea `arrendamiento` y las columnas de `cita`).
2. **Estilistas (HU-15/16/37):** crear/editar/desactivar desde `/admin/empleados` sigue
   funcionando; ya no aparece Salario. Los estilistas existentes conservan sus citas/pagos.
3. **Arrendamiento:** en `/admin/arrendamiento` definir un valor por defecto, una tarifa por
   estilista y una por estilista+servicio; verificar que `resolver` aplica la más específica.
4. **Cobro (HU-11/27):** finalizar una cita y cobrarla en `/recepcionista/pagos`; verificar
   el reparto mostrado y que la cita guarda los 4 valores congelados.
5. **Pago negativo:** poner un arrendamiento mayor al precio del servicio e intentar cobrar;
   debe pedir confirmación explícita.
6. **Reportes/caja existentes (HU-41/39/10):** siguen operando igual (el `pago` no cambió de forma).

### Limitaciones / pendientes
- El desglose de saldos de caja por día se entrega en el **Cambio 3**.

---

## CAMBIO 2 — Historial de servicios y pagos por estilista ✅

Reemplaza por completo el módulo previo **"Pagos a empleados"** (comisión por porcentaje,
HU-30/43). El reparto ahora se basa en la comisión por arrendamiento del Cambio 1.

### Vistas

- **Admin** (`/admin/historial-estilistas`): selector de estilista + rango de fechas, tabla
  de servicios, tarjetas de resumen y exportación a PDF.
- **Estilista** (`/empleado/historial` — "Mi historial"): solo su propio historial (resuelto
  desde la sesión, no puede ver el de otros), mismo rango de fechas, tabla, resumen y PDF.

Cada fila: fecha, hora, cliente, servicio, valor del servicio, valor de arrendamiento,
valor a pagar al estilista, y estado del pago (**pagado** / **pendiente**).

Resumen del período: total de servicios, total facturado (Σ valor_servicio), total de
arrendamiento (ingreso neto del salón) y total a pagar al estilista (Σ valor_a_pagar).

**Fuente de los valores:** las citas ya cobradas usan los valores **congelados** en la cita
(Cambio 1). Las citas finalizadas aún sin cobrar muestran una **estimación** con las tarifas
vigentes y estado "pendiente". (No se crean ni modifican tablas en este cambio.)

### Endpoints nuevos (`/api/historial-estilistas`)

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| GET | `/api/historial-estilistas/{idEmpleado}?desde&hasta` | ADMIN | Historial de cualquier estilista |
| GET | `/api/historial-estilistas/mio?desde&hasta` | ESTILISTA / ADMIN | Historial del estilista autenticado |

`/mio` resuelve el estilista desde la sesión (correo del JWT), de modo que un estilista no
puede consultar el historial de otro.

### Archivos

**Backend — nuevos**
- `controller/HistorialEstilistaController.java`

**Backend — modificados**
- `repository/EmpleadoRepository.java` — `findByUsuario_Correo`.
- `Segurity/SecurityConfig.java` — `/api/historial-estilistas/mio` (EMPLEADO/ADMIN) y `/{id}` (ADMIN).

**Frontend — nuevos**
- `views/HistorialEstilistasView.vue` (admin)
- `views/MiHistorialView.vue` (estilista)
- `services/historialEstilistaService.js`
- `utils/historialPdf.js` (informe PDF compartido por ambas vistas)

**Frontend — modificados**
- `router/index.js` — rutas `/admin/historial-estilistas` y `/empleado/historial`; **eliminada** la ruta `/admin/pagos-empleados`.
- `components/Sidebar.vue` — nav admin "Historial estilistas" (reemplaza "Pagos a estilistas"); nav estilista "Mi historial".

### Módulo viejo retirado
- Se **quitó de la UI** el módulo "Pagos a empleados" por porcentaje (ruta + ítem de menú).
  `views/PagoEmpleadosView.vue` y el backend `PagoEmpleadoController` (`/api/pagos-empleados`)
  se conservan en el código pero **ya no son accesibles desde la interfaz**; pueden eliminarse
  en una limpieza posterior una vez validado el nuevo flujo.

### Cómo verificar
1. **Admin:** en `/admin/historial-estilistas`, elegir un estilista y un rango con citas
   cobradas; verificar filas (con reparto congelado) y los 4 totales; descargar el PDF.
2. **Estilista:** iniciar sesión como estilista, abrir "Mi historial"; solo aparece su
   propio historial; el rango de fechas filtra correctamente.
3. **Seguridad:** un estilista no puede consultar `/api/historial-estilistas/{otroId}` (403).
4. **Citas pendientes:** una cita finalizada sin cobrar aparece como "pendiente" con
   estimación; tras cobrarla pasa a "pagado" con los valores congelados.

---

## CAMBIO 3 — Reporte de saldos de caja por día ✅

Nuevo apartado **"Saldos de caja"** dentro de la sección de Reportes del Admin
(`/admin/reportes`), como una pestaña junto a "Resumen". No crea tablas nuevas; reutiliza
`pago`, las citas con comisión congelada (Cambio 1) y la base diaria `caja_base` (HU-10).

### Vista mensual
- Selector de **mes y año**.
- Una fila por cada **día con movimiento** (pagos completados ese día):
  fecha, cantidad de servicios, desglose por método (efectivo / tarjeta / transferencia),
  base de caja del día, y **saldo final = base + total recibido**.
- KPI con el saldo final acumulado del mes.

### Detalle del día (modal)
Al hacer clic en un día se abre un modal con:
- Cada servicio cobrado: hora, cliente, estilista, servicio, valor del servicio, valor de
  arrendamiento, valor pagado al estilista, método de pago.
- Totales del día: facturado, arrendamiento (ingreso neto del salón), pagado a estilistas,
  base de caja y saldo final.

La integración con la base diaria sigue la regla existente del sistema
(`saldo_final = base_caja + total_recibido`); si no hay base registrada ese día, se asume 0.

### Endpoints nuevos (`/api/reportes`, ADMIN)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/reportes/saldos-diarios?anio&mes` | Saldos por día del mes |
| GET | `/api/reportes/saldos-dia?fecha` | Detalle de servicios y totales de un día |

### Archivos
**Backend — modificados**
- `controller/ReporteController.java` — métodos `saldosDiarios` y `saldosDia` (+ inyecta `CajaBaseRepository`).

**Frontend — modificados**
- `views/ReportesView.vue` — pestañas "Resumen"/"Saldos de caja", tabla mensual (tarjetas apiladas en móvil) y modal de detalle responsive.
- `services/reporteService.js` — `getSaldosDiarios`, `getSaldosDia`.

### Cómo verificar
1. En `/admin/reportes` → pestaña "Saldos de caja": elegir mes/año con pagos; ver una fila
   por día con el desglose por método y el saldo final.
2. Clic en un día → modal con el detalle de servicios y los totales (incluida la base de caja).
3. En móvil (~360px) la tabla se muestra como tarjetas apiladas y el modal es responsive.
4. La pestaña "Resumen" sigue funcionando igual que antes (HU-41 intacta).

---

## CAMBIO 4 — Landing pública de reservas (sin login) + OTP ✅

Ruta pública **`/reservar`** en la misma app (sin servidor ni dominio aparte), excluida del
guard de autenticación. El cliente agenda sin crear cuenta; su identidad se verifica con un
**OTP por correo** antes de confirmar.

### Decisiones acordadas
- **OTP por correo** (reutiliza `EmailService`/Gmail SMTP existente). Cómo agregar SMS más
  adelante: implementar un `SmsService` (p. ej. Twilio) e inyectarlo en `OtpService`
  ofreciendo el método al cliente; el resto del flujo no cambia.
- **Cliente sin login** ("solo landing pública"): se quitó del flujo el panel de cliente con
  Google. `/cliente` y subrutas **redirigen a `/reservar`**; el login de staff (Admin/
  Recepcionista/Estilista) se conserva intacto.

### Flujo (wizard paso a paso, mobile-first)
1. Servicio (catálogo activo) → 2. Estilista (activos) → 3. Fecha y hora (slots reales por
   duración del servicio, misma disponibilidad que el panel) → 4. Datos de contacto
   (nombre, teléfono, correo) → 5. **OTP** (código de 6 dígitos al correo, con reenviar) →
   6. Confirmación con resumen. Maneja estados de carga, error y vacío
   ("no hay horarios disponibles este día").

### Reglas de negocio respetadas
- La cita se crea con la **lógica única** `ReservaService.crearCita(...)`, la misma que usa
  el panel interno (`POST /api/citas` ahora delega en ella). Respeta validación de
  disponibilidad y división/bloqueo de horarios → sin choques entre landing y panel.
- Cita creada con estado **"pendiente"**, igual que una cita interna.
- Notifica al staff vía el módulo de notificaciones existente (HU-42) y envía el correo de
  confirmación (cliente + estilista).
- Cliente sin duplicar: se reutiliza por correo o teléfono; si no existe, se crea.

### OTP (MVP)
`OtpService` guarda el código **en memoria** (vigencia 10 min, máx. 5 intentos, un solo uso).
Decisión: no persiste en BD porque los OTP son efímeros; un reinicio del servidor solo
invalida los pendientes (seguro). Para producción puede moverse a BD/Redis.

### Endpoints nuevos (`/api/public/**`, sin autenticación)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/public/servicios` | Servicios activos |
| GET | `/api/public/estilistas` | Estilistas activos |
| GET | `/api/public/disponibilidad?idEmpleado&fecha` | Bloques disponibles reales |
| POST | `/api/public/otp` | Envía el código OTP al correo |
| POST | `/api/public/reservas` | Verifica OTP, reutiliza/crea cliente y crea la cita |

### Archivos
**Backend — nuevos**
- `service/ReservaService.java` (lógica única de creación de cita)
- `service/OtpService.java`
- `controller/PublicReservaController.java`

**Backend — modificados**
- `controller/CitaController.java` — `registrarCita` delega en `ReservaService` (sin duplicar lógica).
- `service/EmailService.java` — `enviarOtp(...)`.
- `repository/ClienteRepository.java` — `findByUsuario_Correo`, `findByUsuario_Telefono`.
- `Segurity/SecurityConfig.java` — `/api/public/**` permitAll.

**Frontend — nuevos**
- `views/ReservarLandingView.vue` (landing/wizard)
- `services/publicReservaService.js`

**Frontend — modificados**
- `router/index.js` — ruta pública `/reservar`; `/cliente*` redirige a `/reservar`; guard actualizado; home de CLIENTE → `/reservar`.
- `views/LoginView.vue` — clientes que inicien sesión van a `/reservar`.

### Configuración de correo (OTP y notificaciones)
- El envío usa Gmail SMTP. Gmail **exige una App Password** (16 caracteres) con verificación
  en 2 pasos activada; la contraseña normal de la cuenta es rechazada (errores SMTP 535/534).
- Variables: `MAIL_USERNAME` (cuenta remitente) y `MAIL_PASSWORD` (App Password).
- El remitente (`From`) ahora es **configurable** (`EmailService.fromEmail`): por defecto toma
  `spring.mail.username` (debe coincidir con la cuenta autenticada, o Gmail rechaza el envío).
  Se puede sobreescribir con la propiedad `app.mail.from`.
- Para otro proveedor SMTP, ajustar `spring.mail.host`/`spring.mail.port` en `application.properties`.
- Verificado en runtime: envío de OTP (HTTP 200), verificación de código, y reserva completa
  desde la landing (cita creada + bloque dividido, sin choques).

### Limitaciones conocidas
- `usuario.documento` (NOT NULL/único) se genera sintético (`WEB-<timestamp>`) para reservas
  web; `cliente.genero`/`fecha_nacimiento` (NOT NULL en BD) usan **valores centinela**
  (`'otro'`, `1900-01-01`) que el Admin puede completar luego (HU-35). **Recomendado:** hacer
  `cliente.genero` y `cliente.fecha_nacimiento` NULLABLES con un `ALTER TABLE` para evitar
  centinelas (Hibernate `ddl-auto=update` no relaja NOT NULL por sí solo).
- El OTP es por correo; el SMS queda como mejora futura (ver arriba).
- Vistas de cliente con login previas (`ServicioClienteView`, `MisCitasView`, y el flujo de
  reserva con Google de `ServicioDetalleView`) quedan **fuera del flujo de cliente**;
  `ServicioDetalleView` se conserva porque el Admin aún la usa para ver el detalle de un
  servicio. El endpoint `/auth/google/cliente` queda sin uso desde el frontend.

---

## Cómo levantar y probar todo

```bash
# Backend (requiere variables de entorno: DB_*, MAIL_*, GOOGLE_*)
cd backend && ./gradlew bootRun
# Frontend
cd frontend && npm install && npm run dev
```

- Landing pública: abrir `/reservar` sin sesión y completar el flujo (el OTP llega al correo).
- Paneles de staff: login con Google sigue igual; Admin ve Arrendamiento, Historial
  estilistas y Reportes→Saldos de caja; el estilista ve "Mi historial".

---

## CAMBIO 4.1 — Rediseño UI/UX de la landing pública `/reservar` ✅

Mejora **solo visual/experiencia** (estilo página de reservas profesional tipo WeiBook).
**No cambia ninguna regla de negocio**: mismos `data`/`computed`/`methods` y las mismas
llamadas a `publicReservaService` (catálogo, estilistas, disponibilidad, OTP, reserva).

### Estructura nueva de la landing
1. **Hero** con logo NICE LOOK, título/subtítulo y botón "Reservar cita" (hace scroll al wizard).
2. **Sobre nosotros** (texto editable).
3. **Información práctica** (dirección, horario, teléfono + botón WhatsApp).
4. **Catálogo de servicios** agrupado por categoría en tarjetas (no tabla); cada tarjeta
   tiene "Reservar" que preselecciona el servicio y entra al wizard.
5. **Equipo** (tarjetas de estilistas con avatar, nombre, especialidad).
6. **Wizard** con **indicador de progreso** ("Paso X de 5" + barra) y botones
   "Atrás"/"Continuar" consistentes; en escritorio el paso de fecha muestra fecha y
   horarios **lado a lado**.
7. **Confirmación** con check animado, resumen y qué sigue.
8. **Footer** con contacto, redes (configurables) y eslogan.

Estados manejados: **skeletons** al cargar servicios/estilistas, spinners de "Buscando
horarios", vacíos ("No hay horarios disponibles este día...") y errores de OTP/reserva.
Responsive verificado a 360/768/1024 (grids colapsan a 1 columna, wizard a ancho completo).

### Componentes / archivos
- **Nuevo:** `frontend/src/config/negocioConfig.js` — **único lugar editable** del contenido
  de marketing (nombre, eslogan, hero, descripción "Sobre nosotros", dirección, horario,
  teléfono, WhatsApp, correo, redes sociales, y flag `usarBannerImagen`).
- **Reescrito:** `frontend/src/views/ReservarLandingView.vue` (template + estilos; lógica intacta).
- **Reutilizado:** `publicReservaService.js`, íconos de `lucide-vue-next` (ya dependencia;
  sin librerías nuevas), `assets/logo.png`.

### Dónde editar el contenido (para el dueño)
- Textos y datos de contacto → `frontend/src/config/negocioConfig.js`.
- Banner del hero: por defecto es un degradado de marca (sin imagen, carga instantánea).
  Para una **foto real**, importar la imagen en `ReservarLandingView.vue` y usarla como
  `background-image` del `.hero` (hay un `// TODO` en el CSS y el flag `usarBannerImagen`).
- Redes sociales: rellenar las URLs en `negocioConfig.js`; los íconos vacíos se ocultan solos.

### Nota de alcance
No se añadió la opción visual "sin preferencia" de estilista: el backend asigna siempre un
estilista concreto a la cita (regla de negocio existente), así que cada reserva requiere
elegir uno. Se respeta así "no cambiar reglas de negocio".

### Validación del flujo (sigue funcionando igual)
- Build de producción OK tras el rediseño.
- Reserva real end-to-end ya verificada en runtime **antes y después** del cambio de UI
  (mismos métodos): servicio → estilista → fecha/hora → datos → **OTP por correo (200)** →
  código verificado → **cita creada (201)** → **bloque de disponibilidad dividido** (sin
  choques). El guard de correo de staff también validado (rechaza correos ya registrados).

# CHANGES — Implementación del backlog NICE LOOK

Resumen de los cambios realizados para completar las 44 Historias de Usuario.
Detalle de estado por HU en `BACKLOG_STATUS.md`.

## Transversales
- **Guards de rol** (`frontend/src/router/index.js`): cada grupo de rutas declara `meta.roles`; `beforeEach` decodifica el rol del JWT y redirige a la home del rol si no corresponde. `/cliente/mis-citas` exige sesión.
- **Sidebar** (`components/Sidebar.vue`): menús alineados a rutas reales (Caja, Pagos, Mis citas, Reportes, Pagos a empleados); se eliminaron rutas inexistentes.
- **Seguridad backend** (`Segurity/SecurityConfig.java`): GET de servicios/empleados público; mutaciones y módulos nuevos restringidos por rol.

## Fase 1 — Must
- **HU-10 Caja base diaria**: entidad `CajaBase` + `CajaController` (`POST /api/caja`, `/hoy`, `/cierre`). Vista `CajaView.vue` con reconfirmación y panel de cierre.
- **Estados de cita + endpoints** (`CitaController`): `PUT /{id}/confirmar`, `/finalizar`, `/reprogramar` (rechaza <1h), `GET /cliente/{id}`, `GET /dia`. Helpers `dividirBloque` y `liberarBloqueDeCita` (con fusión de contiguos) extraídos y reutilizados.
- **HU-20/21/22/24 Mis citas** (`MisCitasView.vue`): tabs Próximas/Historial, confirmar, reprogramar (slots por duración) y cancelar.
- **HU-33 Auto-registro cliente** (`AuthController POST /auth/google/cliente`): crea Usuario+Cliente CLIENTE si no existe; `ServicioDetalleView` pide login Google al reservar y completa WhatsApp. Login bloquea cuentas inactivas (403).
- **HU-23 (adaptada)**: resumen de total a pagar presencial en la reserva.
- **HU-26 Agenda diaria** (`AgendaRecepcionistaView`): panel "Citas del día" con date picker.

## Fase 2 — Should
- **HU-11/39 Pagos** (`PagoController`, `PagosView.vue`): cobrar citas finalizadas, pagos del día, anular (conserva para auditoría), cierre de caja suma pagos completados.
- **HU-27 Finalizar cita**: botón en el detalle de cita del barbero; evento gris en calendario.
- **HU-29 Porcentaje por servicio**: campo `porcentajeEmpleado` en `Servicio` + input 0-100 en `ServicioForm`.
- **HU-35 Editar cliente** (`ClienteController PUT`, `ClientesView`): modal de edición con validación de duplicados.
- **HU-9 Todos los barberos**: opción "Todos los estilistas" carga disponibilidad en paralelo.
- **Validaciones**: categorías 20/150, servicios 30/100/precio 8 díg.

## Fase 3 — Could
- **HU-16 Desactivar empleado** (`EmpleadoController PUT /desactivar /activar`): cambia estado laboral y de usuario; oculta de la agenda y bloquea login.
- **HU-40 Bloquear horario**: tipo Disponible/Bloqueado en la agenda del barbero; estado `bloqueado` no se ofrece para reservar.
- **HU-30/43 Pago a empleados** (`PagoEmpleado`, `DetallePagoEmpleado`, `PagoEmpleadoController`, `PagoEmpleadosView`): servicios trabajados × % por periodo + historial.
- **HU-41 Reportes** (`ReporteController`, `ReportesView`): ingresos, servicios top (barras), desempeño por barbero, con presets de fecha.
- **HU-42 Notificaciones** (`Notificacion`, `NotificacionService`, `NotificacionController`): persistentes por rol; generadas en alta/cancelación/reprogramación de cita y activación/desactivación de servicio/categoría; campana en layouts de admin y recepción.

## Fase 4 — Wish
- **HU-45 Desactivar cliente** (`ClienteController PUT /desactivar /activar`, `ClientesView`): toggle + filtros Activos/Inactivos/Todos.
- **HU-28 Cerrar sesión**: `router.replace('/')` + guard impiden volver atrás.

## Validación realizada
- `npm run build` (frontend) sin errores tras cada fase.
- `gradlew compileJava` (backend) sin errores tras cada fase.
- Backend arranca correctamente (`Started NiceLookApplication`); se corrigió la columna `porcentaje_empleado` a nullable para no romper filas existentes con `ddl-auto=update`.
- Smoke test de endpoints: GET públicos (servicios/empleados) → 200; protegidos (caja/notificaciones/reportes) → 403 sin autenticación.

## Limitaciones (decisiones de negocio)
1. WhatsApp simulado con correos (`EmailService` + `RecordatorioScheduler`).
2. Pago en línea adaptado a pago presencial registrado por recepción.
3. Sin imágenes en servicios ni foto de perfil.

# BACKLOG_STATUS — Peluquería NICE LOOK

Estado final de las 44 Historias de Usuario del Product Backlog (`backend/notas/backlog.md`).

**Leyenda:** ✅ Implementada y cumple criterios · ⚠️ Parcial / con adaptación documentada

**Limitaciones documentadas (decisiones acordadas con el equipo):**
1. **WhatsApp**: no hay API de WhatsApp configurada. Las notificaciones al cliente (confirmación de cita y recordatorio 24h antes) se envían por **correo electrónico** vía `EmailService` + `RecordatorioScheduler` (cron diario 9:00 AM).
2. **Pago en línea (HU-23)**: sin pasarela de pago. Adaptada por decisión del negocio: la reserva genera un **resumen del total a pagar presencialmente**; el pago lo registra la recepcionista (HU-11).
3. **Imágenes**: sin infraestructura de subida de archivos. Por decisión de diseño no se manejan imágenes en servicios (HU-5/32) ni foto de perfil (HU-44).

## Estado por HU

| ID | Funcionalidad | Prio | Estado | Archivos clave |
|----|---------------|------|--------|----------------|
| HU-1 | Iniciar sesión (staff) | M | ✅ | LoginView.vue, AuthController.java (bloquea inactivos) |
| HU-2 | Crear categorías | M | ✅ | CategoriaModal.vue (máx 20/150), CategoriaService.java |
| HU-3 | Consultar categoría | C | ✅ | CategoriasView.vue, CategoriaDetalle.vue |
| HU-4 | Desactivar categoría | C | ✅ | CategoriasView.vue, CategoriaController.java (+ notificación) |
| HU-31 | Editar categoría | S | ✅ | CategoriaModal.vue |
| HU-5 | Registrar servicios | S | ✅ | ServicioForm.vue (sin imagen, limitación 3) |
| HU-6 | Consultar servicio | C | ✅ | ServicioView.vue, ServicioDetalleView.vue |
| HU-32 | Editar servicio | S | ✅ | ServicioForm.vue (límites 30/100/8díg), ServicioService.actualizar |
| HU-7 | Desactivar servicio | C | ✅ | ServicioView.vue, ServicioController.java (+ notificación) |
| HU-8 | Registrar cliente | M | ✅ | ClientesView.vue, ClienteController.java |
| HU-9 | Disponibilidad de todos los barberos | S | ✅ | AgendaRecepcionistaView.vue (opción "Todos los estilistas") |
| HU-10 | Registrar base diaria | M | ✅ | CajaView.vue, CajaController.java, CajaBase.java |
| HU-11 | Registrar pago de servicio | S | ✅ | PagosView.vue, PagoController.java |
| HU-12 | Agendar horas de trabajo | M | ✅ | AgendaEmpleadoView.vue, DisponibilidadController.java |
| HU-13 | Editar horas de trabajo | S | ✅ | AgendaEmpleadoView.vue (bloquea si hay citas) |
| HU-14 | Ver turnos agendados | C | ✅ | AgendaEmpleadoView.vue (modal detalle de cita) |
| HU-15 | Registrar empleados | M | ✅ | EmpleadoModal.vue, EmpleadoService.java |
| HU-16 | Desactivar empleado | C | ✅ | EmpleadosView.vue, EmpleadoController.java (bloquea login + oculta de agenda) |
| HU-17 | Listado de servicios (cliente) | S | ✅ | ServicioClienteView.vue (público) |
| HU-18 | Búsqueda de servicios (cliente) | C | ✅ | ServicioClienteView.vue |
| HU-19 | Reservar cita (cliente) | M | ✅ | ServicioDetalleView.vue (login-al-reservar + slots por duración + correo). WhatsApp→email (limitación 1) |
| HU-20 | Cancelar cita (cliente) | S | ✅ | MisCitasView.vue, CitaController DELETE (libera+fusiona bloque). Devolución N/A (pago presencial) |
| HU-21 | Reprogramar cita | M | ✅ | MisCitasView.vue, CitaController PUT /reprogramar (rechaza <1h) |
| HU-22 | Historial de citas (cliente) | C | ✅ | MisCitasView.vue (tab Historial) |
| HU-23 | Pagar cita en línea | M | ⚠️ | Adaptada: resumen de total presencial en ServicioDetalleView (limitación 2) |
| HU-24 | Confirmar asistencia | M | ✅ | MisCitasView.vue (botón Confirmar), CitaController PUT /confirmar. Recordatorio por email |
| HU-26 | Agenda diaria de citas | M | ✅ | AgendaRecepcionistaView.vue (panel "Citas del día"), CitaController GET /dia |
| HU-27 | Marcar cita finalizada | S | ✅ | AgendaEmpleadoView.vue (botón en detalle), CitaController PUT /finalizar |
| HU-28 | Cerrar sesión | W | ✅ | Sidebar.vue (replace + limpia token), guard de rol impide volver |
| HU-29 | Porcentaje por servicio | S | ✅ | ServicioForm.vue (0-100), Servicio.porcentajeEmpleado |
| HU-30 | Pago a empleados (selección servicios) | C | ✅ | PagoEmpleadosView.vue, PagoEmpleadoController.java |
| HU-33 | Autenticarse al reservar | M | ✅ | ServicioDetalleView.vue + AuthController POST /auth/google/cliente (auto-registro) |
| HU-34 | Consultar cliente | S | ✅ | ClientesView.vue (búsqueda nombre/doc/correo) |
| HU-35 | Editar cliente | S | ✅ | ClientesView.vue (modal edición), ClienteController PUT |
| HU-36 | Consultar empleado | C | ✅ | EmpleadosView.vue + búsqueda topbar |
| HU-37 | Editar empleado | S | ✅ | EmpleadoModal.vue, EmpleadoService.editarEmpleado |
| HU-38 | Cita presencial (recepcionista) | S | ✅ | AgendaRecepcionistaView.vue (SearchableSelect cliente/servicio) |
| HU-39 | Historial pagos del día + anular | S | ✅ | PagosView.vue (tabla + anular), PagoController PUT /anular |
| HU-40 | Bloquear horario (barbero) | C | ✅ | AgendaEmpleadoView.vue (tipo Disponible/Bloqueado), estadoBloque "bloqueado" |
| HU-41 | Reportes de ingresos | C | ✅ | ReportesView.vue, ReporteController.java |
| HU-42 | Notificaciones del sistema | C | ✅ | Notificacion.java + NotificacionService + bell en AdminLayout/RecepcionistaLayout (persistente por rol) |
| HU-43 | Historial pagos a empleado | C | ✅ | PagoEmpleadosView.vue (tab Historial), PagoEmpleadoController GET /{id} |
| HU-44 | Editar perfil | W | ✅ | EditarPerfilModal.vue (sin foto, limitación 3) |
| HU-45 | Desactivar cliente | W | ✅ | ClientesView.vue (toggle + filtros), ClienteController PUT /desactivar /activar |

## Transversales corregidos
- **Guards de rol en router**: `meta.roles` por grupo + `beforeEach` que valida el rol del JWT y redirige a la home correspondiente.
- **Rutas fantasma del Sidebar** reemplazadas por vistas reales: Caja, Pagos, Mis citas, Reportes, Pagos a empleados.
- **Seguridad backend**: GET de servicios/empleados público; mutaciones restringidas por rol (`/api/caja`, `/api/pagos`, `/api/pagos-empleados`, `/api/reportes`, `/api/notificaciones`).
- **Bloqueo de cuentas inactivas** en login (empleado/cliente desactivado → 403).

## Resultado
44/44 HU implementadas. HU-23 marcada ⚠️ por adaptación de pago presencial (sin pasarela), según decisión del negocio.

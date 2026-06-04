<template>
  <section class="agenda-view">

    <!-- HEADER -->
    <div class="agenda-header">

      <div>
        <h1>Agenda del Estilista</h1>
        <p>Gestiona tus horarios, citas y disponibilidad</p>
      </div>

      <button
        class="btn-primary"
        @click="abrirModal"
      >
        + Nuevo horario
      </button>

    </div>

    <!-- CALENDARIO -->
    <AgendaCalendar
      :eventos="eventos"
      :selectable="true"
      @select="handleSelect"
      @eventClick="handleEventClick"
    />

    <!-- MODAL -->
    <div
      v-if="mostrarModal"
      class="modal-overlay"
      @click.self="cerrarModal"
    >

      <div class="modal-card">

        <div class="modal-header">

          <h2>
            {{
              form.idDisponibilidad
                ? 'Editar horario'
                : 'Registrar horario'
            }}
          </h2>

          <button
            class="close-btn"
            @click="cerrarModal"
          >
            ✕
          </button>

        </div>

        <!-- FECHA -->
        <div class="form-group">

          <label>Fecha</label>

          <input
            type="date"
            v-model="form.fecha"
          >

        </div>

        <!-- HORAS -->
        <div class="form-row">

          <div class="form-group">

            <label>Hora inicio</label>

            <input
              type="time"
              v-model="form.horaInicio"
            >

          </div>

          <div class="form-group">

            <label>Hora fin</label>

            <input
              type="time"
              v-model="form.horaFin"
            >

          </div>

        </div>

        <!-- TIPO DE BLOQUE -->
        <div class="form-group">
          <label>Tipo de horario</label>
          <div class="tipo-bloque">
            <button
              type="button"
              class="tipo-chip"
              :class="{ activo: form.tipo === 'disponible' }"
              @click="form.tipo = 'disponible'"
            >
              🟢 Disponible
            </button>
            <button
              type="button"
              class="tipo-chip"
              :class="{ activo: form.tipo === 'bloqueado' }"
              @click="form.tipo = 'bloqueado'"
            >
              ⛔ Bloqueado
            </button>
          </div>
          <small class="tipo-hint">
            {{ form.tipo === 'bloqueado'
              ? 'Los clientes y la recepción no podrán agendar en este horario.'
              : 'Este horario quedará disponible para recibir citas.' }}
          </small>
        </div>

        <!-- BOTONES -->
        <div class="form-actions">

          <button
            v-if="form.idDisponibilidad"
            class="btn-eliminar"
            @click="pedirEliminarHorario"
          >
            Eliminar
          </button>

          <button
            class="btn-secondary"
            @click="cerrarModal"
          >
            Cancelar
          </button>

          <button
            class="btn-primary"
            @click="guardarHorario"
          >
            {{
              form.idDisponibilidad
                ? 'Actualizar horario'
                : 'Guardar horario'
            }}
          </button>

        </div>

      </div>

    </div>

    <!-- MODAL DETALLE DE CITA -->
    <div
      v-if="citaDetalle.visible"
      class="modal-overlay"
      @click.self="cerrarDetalle"
    >

      <div class="modal-card detalle-card">

        <div class="modal-header">
          <div class="detalle-badge">
            <span class="detalle-dot"></span>
            <h2>Cita asignada</h2>
          </div>
          <button class="close-btn" @click="cerrarDetalle">✕</button>
        </div>

        <div class="detalle-grid">
          <div class="detalle-row">
            <span class="detalle-icon">👤</span>
            <div>
              <p class="detalle-label">Cliente</p>
              <p class="detalle-value">{{ citaDetalle.cliente }}</p>
            </div>
          </div>
          <div class="detalle-row">
            <span class="detalle-icon">✂️</span>
            <div>
              <p class="detalle-label">Servicio</p>
              <p class="detalle-value">{{ citaDetalle.servicio }}</p>
            </div>
          </div>
          <div class="detalle-row">
            <span class="detalle-icon">📅</span>
            <div>
              <p class="detalle-label">Fecha</p>
              <p class="detalle-value">{{ citaDetalle.fecha }}</p>
            </div>
          </div>
          <div class="detalle-row">
            <span class="detalle-icon">🕐</span>
            <div>
              <p class="detalle-label">Horario</p>
              <p class="detalle-value">{{ citaDetalle.horario }}</p>
            </div>
          </div>
        </div>

        <p v-if="citaDetalle.estadoCita !== 'finalizada'" class="detalle-nota">
          Para cancelar o reprogramar esta cita, comunícate con recepción.
        </p>

        <p v-else class="detalle-nota finalizada-nota">
          Servicio realizado. La cita quedó disponible para registro de pago en recepción.
        </p>

        <div class="form-actions">
          <button class="btn-secondary" @click="cerrarDetalle">Cerrar</button>
          <button
            v-if="citaDetalle.idCita && citaDetalle.estadoCita !== 'finalizada'"
            class="btn-primary"
            :disabled="finalizando"
            @click="confirmFinalizar = true"
          >
            {{ finalizando ? 'Guardando...' : 'Marcar como finalizada' }}
          </button>
        </div>

      </div>

    </div>

    <!-- CONFIRMAR FINALIZAR CITA -->
    <AppConfirmModal
      :visible="confirmFinalizar"
      title="Finalizar cita"
      :message="`¿Marcar como finalizada la cita de ${citaDetalle.cliente} (${citaDetalle.servicio})? Quedará lista para el cobro en recepción.`"
      @confirm="ejecutarFinalizarCita"
      @cancel="confirmFinalizar = false"
    />

    <!-- CONFIRMAR ELIMINAR HORARIO -->
    <AppConfirmModal
      :visible="confirmEliminar"
      title="Eliminar horario"
      :message="`¿Eliminar el horario del ${form.fecha} de ${form.horaInicio} a ${form.horaFin}? Esta acción no se puede deshacer.`"
      @confirm="ejecutarEliminarHorario"
      @cancel="confirmEliminar = false"
    />

    <AppToast
      :visible="toast.visible"
      :type="toast.type"
      :title="toast.title"
      :message="toast.message"
      @close="toast.visible = false"
    />

  </section>
</template>

<script>
import {
  getDisponibilidad,
  crearDisponibilidad,
  editarDisponibilidad,
  eliminarDisponibilidad
} from '@/services/disponibilidadService'

import AgendaCalendar
from '@/components/AgendaCalendar.vue'

import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import citaApi from '@/services/citaService'

export default {

  name: 'AgendaEmpleadoView',

  components: {
    AgendaCalendar,
    AppToast,
    AppConfirmModal
  },

  data() {

    return {

      mostrarModal: false,

      eventos: [],

      form: {

        idDisponibilidad: null,

        fecha: '',

        horaInicio: '',

        horaFin: '',

        tipo: 'disponible'
      },

      citaDetalle: {
        visible: false,
        idCita: null,
        estadoCita: '',
        cliente: '',
        servicio: '',
        fecha: '',
        horario: ''
      },

      confirmEliminar: false,
      confirmFinalizar: false,
      finalizando: false,

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  mounted() {

    this.cargarDisponibilidad()
  },

  methods: {

    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3000)
    },

    formatHora(date) {
      if (!date) return ''
      return new Date(date).toLocaleTimeString('es-CO', {
        hour: '2-digit', minute: '2-digit', hour12: true
      })
    },

    cerrarDetalle() {
      this.citaDetalle = { visible: false, idCita: null, estadoCita: '', cliente: '', servicio: '', fecha: '', horario: '' }
    },

    // FINALIZAR CITA

    async ejecutarFinalizarCita() {

      this.confirmFinalizar = false
      this.finalizando = true

      try {

        await citaApi.finalizarCita(this.citaDetalle.idCita)

        this.cerrarDetalle()

        await this.cargarDisponibilidad()

        this.mostrarToast('success', 'Cita finalizada', 'El servicio quedó listo para el cobro en recepción.')

      } catch (error) {

        console.error(error)

        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo finalizar la cita.'

        this.mostrarToast('error', 'Error', msg)

      } finally {
        this.finalizando = false
      }
    },

    // ELIMINAR HORARIO

    pedirEliminarHorario() {
      this.confirmEliminar = true
    },

    async ejecutarEliminarHorario() {

      this.confirmEliminar = false

      try {

        await eliminarDisponibilidad(this.form.idDisponibilidad)

        this.cerrarModal()

        await this.cargarDisponibilidad()

        this.mostrarToast('success', 'Horario eliminado', 'El horario fue eliminado de tu agenda.')

      } catch (error) {

        console.error('Error eliminando horario:', error)

        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo eliminar el horario.'

        this.mostrarToast('error', 'No se pudo eliminar', msg)
      }
    },

    // TOKEN

    getToken() {

      return localStorage.getItem('token')
    },

    // PAYLOAD

    getPayload() {

      const token = this.getToken()

      if (!token) return null

      return JSON.parse(
        atob(token.split('.')[1])
      )
    },

    // ID EMPLEADO

    getIdEmpleado() {

      const payload = this.getPayload()

      return payload?.idEmpleado
    },

    // MODAL

    abrirModal() {

      this.form = {
        idDisponibilidad: null,
        fecha: '',
        horaInicio: '',
        horaFin: '',
        tipo: 'disponible'
      }

      this.mostrarModal = true
    },

    cerrarModal() {

      this.mostrarModal = false

      this.form = {
        idDisponibilidad: null,
        fecha: '',
        horaInicio: '',
        horaFin: '',
        tipo: 'disponible'
      }
    },

    // SELECT CALENDAR

    handleSelect(info) {

      this.form.fecha =
        info.startStr.split('T')[0]

      this.form.horaInicio =
        info.startStr
          .split('T')[1]
          ?.substring(0, 5)

      this.form.horaFin =
        info.endStr
          .split('T')[1]
          ?.substring(0, 5)

      this.mostrarModal = true
    },

    // CLICK EVENTO

    handleEventClick(info) {

      const evento = info.event


      if (
        evento.extendedProps.estado === 'ocupado'
      ) {

        // Mostrar el detalle de la cita asignada
        this.citaDetalle = {
          visible: true,
          idCita: evento.extendedProps.idCita || null,
          estadoCita: evento.extendedProps.estadoCita || 'pendiente',
          cliente: evento.extendedProps.cliente || 'Sin información',
          servicio: evento.extendedProps.servicio || 'Sin información',
          fecha: new Date(evento.start).toLocaleDateString('es-CO', {
            weekday: 'long', day: 'numeric', month: 'long'
          }),
          horario: `${this.formatHora(evento.start)} – ${this.formatHora(evento.end)}`
        }

        return
      }

      this.form.idDisponibilidad =
        evento.id

      this.form.fecha =
        evento.startStr.split('T')[0]

      this.form.horaInicio =
        evento.startStr
          .split('T')[1]
          ?.substring(0, 5)

      this.form.horaFin =
        evento.endStr
          .split('T')[1]
          ?.substring(0, 5)

      this.form.tipo = evento.extendedProps.estado === 'bloqueado'
        ? 'bloqueado'
        : 'disponible'

      this.mostrarModal = true
    },

    // CARGAR DISPONIBILIDAD

    async cargarDisponibilidad() {

  try {

    const idEmpleado =
      this.getIdEmpleado()

    if (!idEmpleado) return

    const hoy = new Date()

    const anio = hoy.getFullYear()

    const mes =
      String(hoy.getMonth() + 1)
        .padStart(2, '0')

    const response =
      await getDisponibilidad(
        idEmpleado,
        mes,
        anio
      )

    this.eventos =
      response.data.map(bloque => {

        let titulo = 'Disponible'
        let clase = 'evento-disponible'

        if (bloque.estado === 'ocupado') {
          titulo = `${bloque.cliente} - ${bloque.servicio}`
          clase = bloque.estadoCita === 'finalizada' ? 'evento-finalizado' : 'evento-ocupado'
        } else if (bloque.estado === 'bloqueado') {
          titulo = '⛔ Bloqueado'
          clase = 'evento-bloqueado'
        }

        return {
          id: bloque.idDisponibilidad,
          title: titulo,
          start: `${bloque.fecha}T${bloque.horaInicio}`,
          end: `${bloque.fecha}T${bloque.horaFin}`,
          className: clase,
          extendedProps: {
            estado: bloque.estado,
            idCita: bloque.idCita,
            estadoCita: bloque.estadoCita,
            cliente: bloque.cliente,
            servicio: bloque.servicio
          }
        }
      })

  } catch (error) {

    console.error(
      'Error cargando disponibilidad:',
      error
    )
  }
},

    // GUARDAR HORARIO

    async guardarHorario() {

      try {

        // VALIDACIONES

        if (
          !this.form.fecha ||
          !this.form.horaInicio ||
          !this.form.horaFin
        ) {

          this.mostrarToast('warning', 'Campos incompletos', 'Completa la fecha y las horas del horario.')

          return
        }

        // FECHA PASADA

        const hoy =
          new Date()
            .toISOString()
            .split('T')[0]

        if (this.form.fecha < hoy) {

          this.mostrarToast('warning', 'Fecha inválida', 'No puedes registrar horarios en fechas pasadas.')

          return
        }

        // HORAS

        if (
          this.form.horaFin <=
          this.form.horaInicio
        ) {

          this.mostrarToast('warning', 'Horas inválidas', 'La hora fin debe ser mayor a la hora inicio.')

          return
        }

        const idEmpleado =
          this.getIdEmpleado()

        
        const data = {

          idEmpleado,

          fecha:
            this.form.fecha,

          horaInicioBloque:
            this.form.horaInicio,

          horaFinBloque:
            this.form.horaFin,

          estadoBloque:
            this.form.tipo === 'bloqueado' ? 'bloqueado' : 'disponible'
        }

          // EDITAR

          if (this.form.idDisponibilidad) {

            await editarDisponibilidad(
              this.form.idDisponibilidad,
              data
            )

          } else {

            // CREAR

            await crearDisponibilidad(data)
          }

        const esEdicion =
  !!this.form.idDisponibilidad



    await this.cargarDisponibilidad()

    this.cerrarModal()

    this.mostrarToast(
      'success',
      esEdicion ? 'Horario actualizado' : 'Horario registrado',
      esEdicion ? 'Los cambios fueron guardados.' : 'Tu horario quedó registrado.'
    )

      } catch (error) {

        console.error(
          'Error guardando horario:',
          error
        )

        if (error.response) {

          const msg = typeof error.response.data === 'string'
            ? error.response.data
            : 'No se pudo guardar el horario.'
          this.mostrarToast('error', 'Error al guardar', msg)

        } else {

          this.mostrarToast('error', 'Sin conexión', 'No hay conexión con el servidor. Intenta de nuevo.')
        }
      }
    }
  }
}
</script>

<!-- SIN scoped para que los estilos lleguen a los eventos de FullCalendar -->
<style>

.agenda-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* HEADER */

.agenda-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.agenda-header h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #1e2a22;
}

.agenda-header p {
  margin-top: 6px;
  color: #6b7a72;
  font-size: 15px;
}

/* BOTONES */

.btn-primary {
  background:
    linear-gradient(
      135deg,
      #004518 0%,
      #0b5d23 100%
    );
  color: white;
  border: none;
  padding: 14px 22px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow:
    0 10px 20px
    rgba(0, 69, 24, 0.18);
}

.btn-secondary {
  background: #eef3ef;
  color: #33443a;
  border: none;
  padding: 13px 20px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
}

/* MODAL */

.modal-overlay {

  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-card {

  width: 450px;
  background: white;
  border-radius: 24px;
  padding: 28px;
  animation: modalFade 0.25s ease;
}

@keyframes modalFade {

  from {
    opacity: 0;
    transform: translateY(15px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {

  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  margin: 0;
  color: #1e2a22;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

/* FORM */

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #33443a;
}

.form-group input {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid #d9e3db;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #004518;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

/* BOTÓN ELIMINAR */

.btn-eliminar {
  background: #fdecec;
  color: #b42318;
  border: 1px solid #f3c2bd;
  padding: 13px 18px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 700;
  font-size: 14px;
  font-family: inherit;
  margin-right: auto;
  transition: all 0.2s ease;
}

.btn-eliminar:hover {
  background: #fad6d3;
  border-color: #e89f98;
}

/* DETALLE DE CITA */

.detalle-card {
  max-width: 420px;
}

.detalle-badge {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detalle-badge h2 {
  margin: 0;
  font-size: 20px;
  color: #1e2a22;
}

.detalle-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #c0392b;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.18);
  flex-shrink: 0;
}

.detalle-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detalle-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 13px 16px;
  background: #f9fcf8;
  border: 1px solid #e8f0e9;
  border-radius: 14px;
}

.detalle-icon {
  font-size: 18px;
  flex-shrink: 0;
  margin-top: 1px;
}

.detalle-label {
  margin: 0;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.7px;
  color: #7a8f80;
}

.detalle-value {
  margin: 3px 0 0;
  font-size: 14px;
  font-weight: 700;
  color: #173221;
  text-transform: capitalize;
}

.detalle-nota {
  margin: 16px 0 0;
  font-size: 13px;
  color: #8a6a00;
  background: #fdf5e6;
  border: 1px solid #f0dcb4;
  border-radius: 12px;
  padding: 11px 14px;
}

.finalizada-nota {
  color: #1d7a3a;
  background: #e7f4ea;
  border-color: #bfe3c8;
}

.tipo-bloque {
  display: flex;
  gap: 10px;
}

.tipo-chip {
  flex: 1;
  padding: 11px 8px;
  border-radius: 12px;
  border: 1px solid #d7e2da;
  background: #ffffff;
  color: #33443a;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.16s ease;
}

.tipo-chip:hover { background: #f0f7f1; }

.tipo-chip.activo {
  background: #004518;
  border-color: #004518;
  color: #ffffff;
}

.tipo-hint {
  font-size: 12px;
  color: #6b7a72;
  margin-top: 6px;
}

/* RESPONSIVE */

@media (max-width: 768px) {
  .agenda-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .modal-card {
    width: 92%;
  }
  :deep(.fc-header-toolbar) {
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }
  :deep(.fc-toolbar-title) {
    font-size: 18px;
  }
}

/* EVENTOS FULLCALENDAR */

.evento-disponible {
  background-color: #004518 !important;
  border-color: #004518 !important;
  color: white !important;
}

.evento-ocupado {
  background-color: #b91c1c !important;
  border-color: #b91c1c !important;
  color: white !important;
}

</style>
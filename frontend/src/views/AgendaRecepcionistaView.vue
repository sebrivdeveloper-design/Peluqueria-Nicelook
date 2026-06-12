<template>

  <section class="agenda-view">

    <!-- HEADER -->
    <div class="agenda-header">

      <div>
        <h1>Agenda Recepción</h1>
        <p>
          Gestiona citas y disponibilidad
        </p>
      </div>

      <!-- SELECT EMPLEADO -->
      <select v-model="idEmpleado" @change="cargarAgenda" class="select-estilista">
        <option disabled value="">
          Selecciona un estilista
        </option>
        <option
          v-for="emp in empleados"
          :key="emp.idEmpleado"
          :value="emp.idEmpleado"
        >
          {{ emp.usuario?.nombreCompleto }}
        </option>
      </select>

    </div>

    <!-- CALENDARIO -->
    <AgendaCalendar
      :eventos="eventos"
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

          <h2>Agendar cita</h2>

          <button
            class="close-btn"
            @click="cerrarModal"
          >
            ✕
          </button>

        </div>

        <!-- CLIENTE -->
        <div class="form-group">

          <label>Cliente</label>

          <select v-model="form.idCliente">

           <option disabled value="">
            Selecciona un cliente
            </option>
            <option
             v-for="cliente in clientes"
             :key="cliente.idCliente"
                :value="cliente.idCliente"
                >
                {{ cliente.nombreCompleto }}
            </option>

          </select>

        </div>

        <!-- SERVICIO -->
        <div class="form-group">

          <label>Servicio</label>

          <select v-model="form.idServicio">

            <option disabled value="">
                Selecciona un servicio
            </option>
            <option
            v-for="servicio in servicios"
            :key="servicio.idServicio"
            :value="servicio.idServicio"
            >
            {{ servicio.nombreServicio }}
            </option>

          </select>

        </div>

        <!-- BOTONES -->
        <div class="form-actions">

          <button
            class="btn-secondary"
            @click="cerrarModal"
          >
            Cancelar
          </button>

          <button
            class="btn-primary"
            @click="guardarCita"
          >
            Confirmar cita
          </button>

        </div>

      </div>

    </div>

  </section>

</template>

<script>
import api from '@/services/axiosInstance'
import AgendaCalendar from '@/components/AgendaCalendar.vue'

export default {

  name: 'AgendaRecepcionistaView',

  components: {
    AgendaCalendar
  },

  data() {

    return {

      empleados: [],
      clientes: [],
      servicios: [],

      eventos: [],

      idEmpleado: '',

      mostrarModal: false,

      bloqueSeleccionado: null,

      form: {

        idCliente: '',
        idServicio: '',
        idDisponibilidad: ''

      }
    }
  },

  mounted() {

    this.cargarEmpleados()
    this.cargarClientes()
    this.cargarServicios()
    this.cargarAgenda()
  },

  methods: {

    // EMPLEADOS
    async cargarEmpleados() {
      try {
        const response = await api.get('/empleados')
        this.empleados = response.data
      } catch (error) {
        console.error(error)
      }
    },

    // CLIENTES
    async cargarClientes() {
      try {
        const response = await api.get('/clientes')
        this.clientes = response.data
      } catch (error) {
        console.error(error)
      }
    },

    // SERVICIOS
    async cargarServicios() {
      try {
        const response = await api.get('/servicios')
        this.servicios = response.data
      } catch (error) {
        console.error(error)
      }
    },

    // CARGAR AGENDA
    async cargarAgenda() {
      if (!this.idEmpleado) return

      try {
        const hoy = new Date()
        const anio = hoy.getFullYear()
        const mes = String(hoy.getMonth() + 1).padStart(2, '0')

        const response = await api.get(
          `/disponibilidad/${this.idEmpleado}`,
          { params: { mes, anio } }
        )

        const nuevos = response.data.map(bloque => ({

          id: bloque.idDisponibilidad,

          title:
            bloque.estado === 'ocupado'
              ? `${bloque.cliente} - ${bloque.servicio}`
              : 'Disponible',

          start:
            `${bloque.fecha}T${bloque.horaInicio}`,

          end:
            `${bloque.fecha}T${bloque.horaFin}`,

          className:
            bloque.estado === 'ocupado'
              ? 'evento-ocupado'
              : 'evento-disponible',

          extendedProps: {

            estado:
              bloque.estado,

            idDisponibilidad:
              bloque.idDisponibilidad,

            cliente:
              bloque.cliente,

            servicio:
              bloque.servicio
          }
        }))

        this.eventos = nuevos

      } catch (error) {
        console.error(error)
      }
    },

    // CLICK EVENTO
    handleEventClick(info) {

      const evento = info.event

      if (evento.extendedProps.estado === 'ocupado') {
        alert('Horario ocupado')
        return
      }

      this.bloqueSeleccionado = evento
      this.mostrarModal = true
    },

    cerrarModal() {
      this.mostrarModal = false
    },

    // GUARDAR CITA
    async guardarCita() {

      try {

        if (!this.form.idCliente || !this.form.idServicio) {
          alert('Completa todos los campos')
          return
        }

        const inicio = this.bloqueSeleccionado.start
        const fin    = this.bloqueSeleccionado.end

        const fecha      = inicio.toISOString().split('T')[0]
        const horaInicio = inicio.toTimeString().slice(0, 8)
        const horaFin    = fin.toTimeString().slice(0, 8)

        await api.post('/citas', {
          idCliente:        this.form.idCliente,
          idEmpleado:       this.idEmpleado,
          idServicio:       this.form.idServicio,
          idDisponibilidad: this.bloqueSeleccionado.extendedProps.idDisponibilidad,
          fecha,
          horaInicio,
          horaFin,
          observaciones:    ''
        })

        alert('Cita agendada')

        this.form = { idCliente: '', idServicio: '' }
        this.cerrarModal()
        this.bloqueSeleccionado.setProp('title', 'Reservado')
        this.bloqueSeleccionado.setProp('classNames', ['evento-ocupado'])

      } catch (error) {
        console.error(error)
        alert('Error agendando cita')
      }
    }
  }
}
</script>
<style scoped>

.agenda-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* =========================
   HEADER
========================= */

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
  color: #183326;
}

.agenda-header p {
  margin-top: 6px;
  color: #5f6f66;
  font-size: 15px;
  font-weight: 500;
}

/* =========================
   SELECT ESTILISTA
========================= */

.select-estilista {

  padding: 14px 16px;

  border-radius: 14px;

  border: 1px solid #d7e2da;

  background: #ffffff !important;

  color: #1e2a22 !important;

  min-width: 260px;

  font-size: 14px;

  font-weight: 600;

  outline: none;

  transition: all 0.2s ease;

  cursor: pointer;
}

.select-estilista:focus {
  border-color: #004518;
  box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08);
}

.select-estilista option {
  font-weight: 500;
}

.select-estilista,
.form-group select {
  color-scheme: light;
}
/* =========================
   MODAL
========================= */

.modal-overlay {

  position: fixed;

  inset: 0;

  background: rgba(0,0,0,0.45);

  display: flex;

  align-items: center;

  justify-content: center;

  z-index: 999;
}

.modal-card {

  width: 450px;

  background: white;

  border-radius: 26px;

  padding: 30px;

  box-shadow:
    0 20px 50px rgba(0,0,0,0.18);

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

/* =========================
   MODAL HEADER
========================= */

.modal-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 26px;
}

.modal-header h2 {

  margin: 0;

  font-size: 24px;

  font-weight: 700;

  color: #183326;
}

.close-btn {

  background: transparent;

  border: none;

  font-size: 22px;

  color: #6b7a72;

  cursor: pointer;

  transition: 0.2s ease;
}

.close-btn:hover {

  color: #004518;

  transform: scale(1.1);
}

/* =========================
   FORMULARIO
========================= */

.form-group {

  display: flex;

  flex-direction: column;

  gap: 8px;

  margin-bottom: 20px;
}

.form-group label {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-primary, #264434);
}
/* =========================
   SELECTS MODAL
========================= */

.form-group select {

  padding: 14px 16px;

  border-radius: 14px;

  border: 1px solid #d7e2da;

  background: #ffffff !important;

  color: #1e2a22 !important;

  font-size: 14px;

  font-weight: 600;

  outline: none;

  transition: all 0.2s ease;

  cursor: pointer;
}

.form-group select:focus {

  border-color: #004518;

  box-shadow:
    0 0 0 4px rgba(0, 69, 24, 0.08);
}

.form-group select option {
  font-weight: 500;
}

/* =========================
   BOTONES
========================= */

.form-actions {

  display: flex;

  justify-content: flex-end;

  gap: 14px;

  margin-top: 26px;
}
select option {

  background: #ffffff !important;

  color: #1e2a22 !important;
}
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

  font-weight: 700;

  cursor: pointer;

  transition: all 0.22s ease;
}

.btn-primary:hover {

  transform: translateY(-2px);

  box-shadow:
    0 10px 20px rgba(0, 69, 24, 0.16);
}

.btn-secondary {

  background: #eef3ef;

  color: #2f4338;

  border: none;

  padding: 14px 22px;

  border-radius: 14px;

  font-size: 14px;

  font-weight: 700;

  cursor: pointer;

  transition: all 0.2s ease;
}

.btn-secondary:hover {

  background: #dde7df;
}

/* =========================
   FULLCALENDAR
========================= */

:deep(.fc) {
  font-family: 'Inter', sans-serif;
}

:deep(.fc-toolbar-title) {

  font-size: 22px;

  font-weight: 700;

  color: #1e2a22;
}

:deep(.fc-button) {

  background: #004518 !important;

  border: none !important;

  border-radius: 10px !important;

  padding: 8px 14px !important;

  font-weight: 600 !important;
}

:deep(.fc-button:hover) {

  background: #0b5d23 !important;
}

:deep(.fc-timegrid-slot) {

  height: 60px;
}

:deep(.fc-event) {

  border: none;

  padding: 4px;

  border-radius: 10px;

  font-size: 13px;

  font-weight: 600;
}

:deep(.evento-disponible) {

  background: #28a745 !important;
}

:deep(.evento-ocupado) {

  background: #dc3545 !important;
}

:deep(.fc-day-today) {

  background:
    rgba(0, 69, 24, 0.05) !important;
}

/* =========================
   RESPONSIVE
========================= */

@media (max-width: 768px) {

  .agenda-header {

    flex-direction: column;

    align-items: flex-start;
  }

  .select-estilista {

    width: 100%;
  }

  .modal-card {
    width: 92%;
    padding: 24px;
  }
}

</style>

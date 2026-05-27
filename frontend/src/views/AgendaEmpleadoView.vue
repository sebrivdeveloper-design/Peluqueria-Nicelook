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
      @datesSet="handleDatesSet"
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

  </section>
</template>

<script>
import {
  getDisponibilidad,
  crearDisponibilidad,
  editarDisponibilidad
} from '@/services/disponibilidadService'

import AgendaCalendar
from '@/components/AgendaCalendar.vue'

export default {

  name: 'AgendaEmpleadoView',

  components: {
    AgendaCalendar
  },

  data() {

    return {

      mostrarModal: false,

      eventos: [],
      mesActual: new Date().getMonth() + 1,
      anioActual: new Date().getFullYear(),
      form: {

        idDisponibilidad: null,

        fecha: '',

        horaInicio: '',

        horaFin: ''
      }
    }
  },

  mounted() {

    this.cargarDisponibilidad()
  },

  methods: {

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

        horaFin: ''
      }

      this.mostrarModal = true
    },

    cerrarModal() {

      this.mostrarModal = false

      this.form = {

        idDisponibilidad: null,

        fecha: '',

        horaInicio: '',

        horaFin: ''
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

        alert(
          'Este horario tiene citas agendadas'
        )

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


      this.mostrarModal = true
    },

    handleDatesSet(info) {

    const fecha = info.start

    this.mesActual =
    fecha.getMonth() + 1

    this.anioActual =
    fecha.getFullYear()

    this.cargarDisponibilidad()
    },

    // CARGAR DISPONIBILIDAD

    async cargarDisponibilidad() {

  try {

    const idEmpleado =
      this.getIdEmpleado()

    if (!idEmpleado) return

    const mes = this.mesActual
    const anio = this.anioActual

    const response =
      await getDisponibilidad(
        idEmpleado,
        mes,
        anio
      )

    this.eventos =
      response.data.map(bloque => ({

        id:
          bloque.idDisponibilidad,

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

          cliente:
            bloque.cliente,

          servicio:
            bloque.servicio
        }

      }))

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

          alert(
            'Completa todos los campos'
          )

          return
        }

        // FECHA PASADA

        const hoy =
          new Date()
            .toISOString()
            .split('T')[0]

        if (this.form.fecha < hoy) {

          alert(
            'No puedes registrar horarios en fechas pasadas'
          )

          return
        }

        // HORAS

        if (
          this.form.horaFin <=
          this.form.horaInicio
        ) {

          alert(
            'La hora fin debe ser mayor a la hora inicio'
          )

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
            'disponible'
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

    alert(
      esEdicion
        ? 'Horario actualizado correctamente'
        : 'Horario registrado correctamente'
    )

      } catch (error) {

        console.error(
          'Error guardando horario:',
          error
        )

        if (error.response) {

          alert(error.response.data)

        } else {

          alert(
            'Error de conexión con el servidor'
          )
        }
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
}

</style>
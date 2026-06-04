<template>
  <div class="container">

    <h1>Mis citas</h1>

    <div v-if="citas.length === 0" class="empty">
      No tienes citas registradas
    </div>

    <div v-else class="cards">

      <div
        class="card"
        v-for="(cita, index) in citas"
        :key="index"
      >
        <h3>{{ cita.servicio }}</h3>

        <p><strong>Fecha:</strong> {{ cita.fecha }}</p>

        <p><strong>Barbero:</strong> {{ cita.barbero }}</p>

        <p><strong>Hora:</strong> {{ cita.horaInicio }} - {{ cita.horaFin }}</p>

        <p>
          <strong>Estado:</strong>
          <span :class="'badge badge-' + cita.estado.toLowerCase()">
            {{ cita.estado }}
          </span>
        </p>

        <button
          v-if="cita.estado.toLowerCase() === 'pendiente'"
          class="btn-cancelar"
          @click="solicitarCancelacion(cita)"
        >
          Cancelar cita
        </button>

      </div>

    </div>

    <!-- Modal de confirmación -->
    <div v-if="mostrarModal" class="modal-overlay">
      <div class="modal">
        <h3>¿Cancelar cita?</h3>
        <p>¿Estás seguro de que deseas cancelar tu cita de <strong>{{ citaSeleccionada?.servicio }}</strong> el {{ citaSeleccionada?.fecha }}?</p>
        <div class="modal-actions">
          <button class="btn-confirmar" @click="confirmarCancelacion">Sí, cancelar</button>
          <button class="btn-volver" @click="mostrarModal = false">Volver</button>
        </div>
      </div>
    </div>

    <!-- Mensaje de éxito -->
    <div v-if="mensajeExito" class="toast">
      ✅ Cita cancelada exitosamente
    </div>

  </div>
</template>

<script>
import { getHistorial, cancelarCita } from '../services/historialService'

export default {

  data() {
    return {
      citas: [],
      mostrarModal: false,
      citaSeleccionada: null,
      mensajeExito: false
    }
  },

  async mounted() {
    await this.cargarCitas()
  },

  methods: {

    getIdCliente() {
      const token = localStorage.getItem('token')
      if (!token) return null
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload?.idCliente
    },

    async cargarCitas() {
      try {
        const idCliente = this.getIdCliente()
        if (!idCliente) return
        const res = await getHistorial(idCliente)
        this.citas = res.data
      } catch (error) {
        console.error("Error cargando historial:", error)
      }
    },

    solicitarCancelacion(cita) {
      this.citaSeleccionada = cita
      this.mostrarModal = true
    },

    async confirmarCancelacion() {
      try {
        await cancelarCita(this.citaSeleccionada.idCita)
        this.mostrarModal = false
        this.citaSeleccionada = null
        this.mensajeExito = true

        await this.cargarCitas()

        setTimeout(() => { this.mensajeExito = false }, 3000)

      } catch (error) {
        console.error("Error cancelando cita:", error)
        this.mostrarModal = false
      }
    }
  }
}
</script>

<style scoped>
.container {
  padding: 40px;
}

h1 {
  margin-bottom: 30px;
  color: #004518;
}

.cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.card {
  width: 300px;
  background: #dce6d7;
  border-radius: 20px;
  padding: 20px;
}

.card h3 {
  margin-bottom: 15px;
  color: #004518;
}

.empty {
  color: #777;
  font-style: italic;
}

.badge {
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: bold;
  text-transform: capitalize;
}

.badge-pendiente {
  background: #fff3cd;
  color: #856404;
}

.badge-completada {
  background: #d4edda;
  color: #155724;
}

.badge-cancelada {
  background: #f8d7da;
  color: #721c24;
}

.btn-cancelar {
  margin-top: 12px;
  width: 100%;
  padding: 8px;
  background-color: #c0392b;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
}

.btn-cancelar:hover {
  background-color: #a93226;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 30px;
  border-radius: 16px;
  width: 360px;
  text-align: center;
}

.modal h3 {
  margin-bottom: 12px;
  color: #004518;
}

.modal p {
  margin-bottom: 20px;
  color: #333;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-confirmar {
  background: #c0392b;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
}

.btn-volver {
  background: #dce6d7;
  color: #004518;
  border: none;
  padding: 8px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
}

.toast {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: #d4edda;
  color: #155724;
  padding: 14px 24px;
  border-radius: 12px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  z-index: 999;
}
</style>
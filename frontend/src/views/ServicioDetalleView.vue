<template>
  <section class="detalle-page">
    <div class="navigation-header">
      <button @click="$router.back()" class="btn-back">
        <i class="pi pi-arrow-left"></i> Volver a servicios
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      <p>Cargando información del servicio...</p>
    </div>

    <div v-else-if="servicio" class="detalle-container">
      <div class="detalle-grid">

        <div class="info-column">
          <span class="categoria-badge">
            {{ servicio.categoria?.nombreCategoria || 'Servicio' }}
          </span>
          <h1 class="servicio-titulo">{{ servicio.nombreServicio }}</h1>

          <div class="metas-rapidas">
            <div class="meta-item">
              <span class="label">Duración</span>
              <span class="valor">⏱ {{ servicio.duracionMinutos }} min</span>
            </div>
            <div class="meta-item">
              <span class="label">Precio</span>
              <span class="valor precio-destacado">$ {{ formatearPrecio(servicio.precio) }}</span>
            </div>
          </div>

          <div class="descripcion-box">
            <h3>Sobre este servicio</h3>
            <p>{{ servicio.descripcion }}</p>
          </div>
        </div>

        <div class="action-column">
          <div class="reserva-card">
            <div class="reserva-header">
              <i class="pi pi-calendar-plus"></i>
              <span>Agendar Cita</span>
            </div>

            <!-- PASO 1: Seleccionar barbero -->
            <div v-if="paso === 1">
              <p>Selecciona un barbero disponible:</p>
              <div class="lista-barberos">
                <div
                  v-for="emp in empleados"
                  :key="emp.idEmpleado"
                  class="barbero-item"
                  :class="{ seleccionado: empleadoSeleccionado?.idEmpleado === emp.idEmpleado }"
                  @click="seleccionarBarbero(emp)"
                >
                  <i class="pi pi-user"></i>
                  <div>
                    <strong>{{ emp.usuario?.nombreCompleto }}</strong>
                    <small>{{ emp.especialidad || 'Barbero' }}</small>
                  </div>
                </div>
              </div>

              <div v-if="empleadoSeleccionado" class="fecha-picker">
                <label>Selecciona una fecha:</label>
                <input
                  type="date"
                  v-model="fechaSeleccionada"
                  :min="hoy"
                  @change="cargarDisponibilidad"
                />
              </div>

              <button
                v-if="empleadoSeleccionado && fechaSeleccionada"
                class="btn-confirmar-reserva"
                @click="paso = 2"
              >
                Ver horarios disponibles
              </button>
            </div>

            <!-- PASO 2: Seleccionar horario -->
            <div v-if="paso === 2">
              <button class="btn-volver-paso" @click="paso = 1">
                <i class="pi pi-arrow-left"></i> Cambiar barbero
              </button>
              <p>Horarios disponibles para <strong>{{ fechaSeleccionada }}</strong>:</p>

              <div v-if="loadingHorarios" class="loading-small">Cargando horarios...</div>

              <div v-else-if="horarios.length === 0" class="sin-horarios">
                No hay horarios disponibles para esta fecha.
              </div>

              <div v-else class="lista-horarios">
                <div
                  v-for="bloque in horarios"
                  :key="bloque.idDisponibilidad"
                  class="horario-item"
                  :class="{ seleccionado: horarioSeleccionado?.idDisponibilidad === bloque.idDisponibilidad }"
                  @click="horarioSeleccionado = bloque"
                >
                  {{ bloque.horaInicioBloque }} - {{ bloque.horaFinBloque }}
                </div>
              </div>

              <button
                v-if="horarioSeleccionado"
                class="btn-confirmar-reserva"
                @click="paso = 3"
              >
                Continuar
              </button>
            </div>

            <!-- PASO 3: Confirmar reserva -->
            <div v-if="paso === 3">
              <button class="btn-volver-paso" @click="paso = 2">
                <i class="pi pi-arrow-left"></i> Cambiar horario
              </button>

              <div class="resumen-cita">
                <h4>Resumen de tu cita</h4>
                <p>💈 <strong>Barbero:</strong> {{ empleadoSeleccionado?.usuario?.nombreCompleto }}</p>
                <p>📅 <strong>Fecha:</strong> {{ fechaSeleccionada }}</p>
                <p>⏰ <strong>Hora:</strong> {{ horarioSeleccionado?.horaInicioBloque }} - {{ horarioSeleccionado?.horaFinBloque }}</p>
                <p>✂️ <strong>Servicio:</strong> {{ servicio.nombreServicio }}</p>
              </div>

              <div class="form-group">
                <label>Observaciones (opcional):</label>
                <textarea v-model="observaciones" rows="2" placeholder="Ej. Corte con máquina 2..."></textarea>
              </div>

              <div v-if="mensajeExito" class="alerta-exito">{{ mensajeExito }}</div>
              <div v-if="mensajeError" class="alerta-error">{{ mensajeError }}</div>

              <button
                class="btn-confirmar-reserva"
                @click="confirmarReserva"
                :disabled="guardando"
              >
                {{ guardando ? 'Registrando...' : 'Confirmar reserva' }}
              </button>

              <p class="nota-pago">El pago se realiza directamente en el local.</p>
            </div>

          </div>
        </div>
      </div>
    </div>

    <div v-else class="error-state">
      <h3>Servicio no encontrado</h3>
      <p>Lo sentimos, el servicio que buscas no está disponible.</p>
      <button @click="$router.push('/cliente/servicios')" class="btn-primary">
        Ir al catálogo
      </button>
    </div>
  </section>
</template>

<script>
import servicioApi from "@/services/servicioService"
import citaApi from "@/services/citaService"

export default {
  name: 'ServicioDetalleView',
  data() {
    return {
      servicio: null,
      loading: true,
      paso: 1,
      empleados: [],
      empleadoSeleccionado: null,
      fechaSeleccionada: '',
      horarios: [],
      horarioSeleccionado: null,
      loadingHorarios: false,
      observaciones: '',
      guardando: false,
      mensajeExito: '',
      mensajeError: '',
      hoy: new Date().toISOString().split('T')[0]
    }
  },
  methods: {
    formatearPrecio(valor) {
      return new Intl.NumberFormat("es-CO").format(valor)
    },

    async cargarDetalle() {
      this.loading = true
      const id = this.$route.params.id
      try {
        const res = await servicioApi.getById(id)
        this.servicio = res.data
        await this.cargarEmpleados()
      } catch (error) {
        console.error("Error al obtener detalle:", error)
      } finally {
        this.loading = false
      }
    },

    async cargarEmpleados() {
      try {
        const res = await citaApi.getEmpleados()
        this.empleados = res.data
      } catch (error) {
        console.error("Error cargando empleados:", error)
      }
    },

    seleccionarBarbero(emp) {
      this.empleadoSeleccionado = emp
      this.fechaSeleccionada = ''
      this.horarios = []
      this.horarioSeleccionado = null
    },

    async cargarDisponibilidad() {
      if (!this.empleadoSeleccionado || !this.fechaSeleccionada) return
      this.loadingHorarios = true
      this.horarios = []
      this.horarioSeleccionado = null
      try {
        const res = await citaApi.getHorariosDisponibles(
      this.empleadoSeleccionado.idEmpleado,
      this.fechaSeleccionada,
      this.servicio.idServicio
      )
        this.horarios = res.data
      } catch (error) {
        console.error("Error cargando horarios:", error)
      } finally {
        this.loadingHorarios = false
      }
    },

  async confirmarReserva() {

  const token = localStorage.getItem('token')

  // 🚨 VALIDAR LOGIN
  if (!token) {

    this.mensajeError = 'Debes iniciar sesión para reservar una cita'

    setTimeout(() => {
      this.$router.push('/')
    }, 1500)

    return
  }

  this.guardando = true
  this.mensajeError = ''
  this.mensajeExito = ''

  const payload = JSON.parse(atob(token.split('.')[1]))

  try {

    const citaData = {
      idCliente: payload.idCliente,
      idEmpleado: this.empleadoSeleccionado.idEmpleado,
      idServicio: this.servicio.idServicio,
      idDisponibilidad: this.horarioSeleccionado.idDisponibilidad,
      fecha: this.fechaSeleccionada,
      horaInicio: this.horarioSeleccionado.horaInicioBloque,
      horaFin: this.horarioSeleccionado.horaFinBloque,
      observaciones: this.observaciones
    }

    console.log("ENVIANDO:", citaData)

    await citaApi.registrarCita(citaData)

    this.mensajeExito = '✅ ¡Cita registrada!'

    setTimeout(() => {
      this.$router.push('/cliente/servicios')
    }, 3000)

  } catch (error) {

    console.error(error)

    if (error.response?.status === 409) {
      this.mensajeError = error.response.data
    } else {
      this.mensajeError = 'Ocurrió un error al registrar la cita.'
    }

  } finally {
    this.guardando = false
  }
}
  },
  mounted() {
    this.cargarDetalle()
  }
}
</script>

<style scoped>
.detalle-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.navigation-header {
  margin-bottom: 30px;
}

.btn-back {
  background: none;
  border: none;
  color: #145c43;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.detalle-container {
  background: white;
  border-radius: 24px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.06);
  padding: 40px;
  border: 1px solid #d9e4da;
}

.detalle-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 50px;
}

.categoria-badge {
  background: #e6f4ea;
  color: #145c43;
  padding: 6px 16px;
  border-radius: 99px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
}

.servicio-titulo {
  font-size: 42px;
  color: #173221;
  margin: 15px 0 25px 0;
  line-height: 1.1;
}

.metas-rapidas {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.meta-item {
  display: flex;
  flex-direction: column;
}

.meta-item .label {
  font-size: 13px;
  color: #888;
  text-transform: uppercase;
}

.meta-item .valor {
  font-size: 20px;
  font-weight: 600;
  color: #173221;
}

.precio-destacado {
  color: #145c43 !important;
  font-size: 24px !important;
}

.descripcion-box h3 {
  color: #173221;
  margin-bottom: 15px;
}

.descripcion-box p {
  color: #5f6f63;
  line-height: 1.8;
  font-size: 16px;
}

.reserva-card {
  background: #f8fafc;
  border-radius: 20px;
  padding: 30px;
  border: 1px solid #e2e8f0;
  position: sticky;
  top: 100px;
}

.reserva-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  color: #145c43;
  margin-bottom: 15px;
  font-size: 18px;
}

.lista-barberos {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 16px;
}

.barbero-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #d9e4da;
  border-radius: 10px;
  cursor: pointer;
  background: white;
  transition: all 0.2s;
}

.barbero-item:hover {
  border-color: #145c43;
}

.barbero-item.seleccionado {
  border-color: #145c43;
  background: #e6f4ea;
}

.barbero-item div {
  display: flex;
  flex-direction: column;
}

.barbero-item small {
  color: #888;
  font-size: 12px;
}

.fecha-picker {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.fecha-picker label {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e31;
}

.fecha-picker input {
  padding: 10px;
  border: 1px solid #d9e4da;
  border-radius: 8px;
  font-size: 14px;
}

.lista-horarios {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.horario-item {
  padding: 8px 14px;
  border: 1px solid #d9e4da;
  border-radius: 8px;
  cursor: pointer;
  background: white;
  font-size: 14px;
  transition: all 0.2s;
}

.horario-item:hover {
  border-color: #145c43;
}

.horario-item.seleccionado {
  background: #145c43;
  color: white;
  border-color: #145c43;
}

.resumen-cita {
  background: white;
  border: 1px solid #d9e4da;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: left;
}

.resumen-cita h4 {
  color: #145c43;
  margin-bottom: 10px;
}

.resumen-cita p {
  font-size: 14px;
  color: #2c3e31;
  margin: 6px 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e31;
}

.form-group textarea {
  padding: 10px;
  border: 1px solid #d9e4da;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
}

.btn-confirmar-reserva {
  width: 100%;
  padding: 14px;
  background: #145c43;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 10px;
}

.btn-confirmar-reserva:hover {
  background: #1b5e20;
}

.btn-confirmar-reserva:disabled {
  background: #9ab8a4;
  cursor: not-allowed;
}

.btn-volver-paso {
  background: none;
  border: none;
  color: #145c43;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  margin-bottom: 12px;
  padding: 0;
}

.nota-pago {
  margin-top: 12px;
  font-size: 12px;
  color: #888;
  font-style: italic;
  text-align: center;
}

.loading-small {
  text-align: center;
  color: #5f6f63;
  padding: 20px;
}

.sin-horarios {
  text-align: center;
  color: #888;
  padding: 20px;
  font-style: italic;
}

.alerta-exito {
  background: #d4edda;
  color: #155724;
  padding: 10px 14px;
  border-radius: 6px;
  margin-bottom: 12px;
  font-weight: 600;
  font-size: 14px;
}

.alerta-error {
  background: #f8d7da;
  color: #721c24;
  padding: 10px 14px;
  border-radius: 6px;
  margin-bottom: 12px;
  font-weight: 600;
  font-size: 14px;
}

@media (max-width: 900px) {
  .detalle-grid {
    grid-template-columns: 1fr;
  }
  .reserva-card {
    position: static;
  }
  .servicio-titulo {
    font-size: 32px;
  }
}
</style>
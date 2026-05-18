<template>
  <section class="agenda-page">
    
    <div class="agenda-header">
      <button class="btn-registrar" @click="mostrarModalHorario = true">
        + Registrar horario
      </button>
    </div>

    <div class="calendario-container">
      <div class="calendario-nav">
        <button @click="mesAnterior" class="btn-nav">‹</button>
        <h2>{{ nombreMes }} {{ anio }}</h2>
        <button @click="mesSiguiente" class="btn-nav">›</button>
      </div>

      <div class="calendario-grid">
        <div class="dia-header" v-for="dia in diasSemana" :key="dia">{{ dia }}</div>

        <div
          v-for="(dia, index) in diasCalendario"
          :key="index"
          class="dia-celda"
          :class="{ 'otro-mes': !dia.mesActual, 'hoy': dia.esHoy }"
        >
          <span class="dia-numero">{{ dia.numero }}</span>
          <div class="eventos-dia">
            <div
              v-for="bloque in dia.bloques"
              :key="bloque.idDisponibilidad"
              class="evento-bloque"
              :class="bloque.estadoBloque"
            >
              {{ bloque.horaInicioBloque }} - {{ bloque.horaFinBloque }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal registrar horario -->
    <div v-if="mostrarModalHorario" class="modal-overlay" @click.self="mostrarModalHorario = false">
      <div class="modal-content">
        <h2>Registrar Horario</h2>

        <div v-if="mensajeExito" class="alerta-exito">{{ mensajeExito }}</div>
        <div v-if="mensajeError" class="alerta-error">{{ mensajeError }}</div>

        <div class="form-group">
          <label>Fecha:</label>
          <input type="date" v-model="nuevoHorario.fecha" :min="hoy" />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Hora inicio:</label>
            <input type="time" v-model="nuevoHorario.horaInicio" />
          </div>
          <div class="form-group">
            <label>Hora fin:</label>
            <input type="time" v-model="nuevoHorario.horaFin" />
          </div>
        </div>

        <div class="modal-actions">
          <button class="btn-cancelar" @click="mostrarModalHorario = false">Cancelar</button>
          <button class="btn-guardar" @click="guardarHorario">Guardar</button>
        </div>
      </div>
    </div>

  </section>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AgendaView',
  data() {
    const hoy = new Date()
    return {
      hoy: hoy.toISOString().split('T')[0],
      mes: hoy.getMonth(),
      anio: hoy.getFullYear(),
      diasSemana: ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'],
      disponibilidades: [],
      mostrarModalHorario: false,
      mensajeExito: '',
      mensajeError: '',
      nuevoHorario: {
        fecha: '',
        horaInicio: '',
        horaFin: ''
      }
    }
  },
  computed: {
    nombreMes() {
      return new Date(this.anio, this.mes).toLocaleString('es-CO', { month: 'long' }).toUpperCase()
    },
    idEmpleado() {
      const token = localStorage.getItem('token')
      if (!token) return null
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.idEmpleado
    },
    diasCalendario() {
      const primerDia = new Date(this.anio, this.mes, 1).getDay()
      const diasEnMes = new Date(this.anio, this.mes + 1, 0).getDate()
      const diasMesAnterior = new Date(this.anio, this.mes, 0).getDate()
      const hoy = new Date()
      const dias = []

      for (let i = primerDia - 1; i >= 0; i--) {
        dias.push({ numero: diasMesAnterior - i, mesActual: false, esHoy: false, bloques: [] })
      }

      for (let d = 1; d <= diasEnMes; d++) {
        const fecha = `${this.anio}-${String(this.mes + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
        const esHoy = hoy.getDate() === d && hoy.getMonth() === this.mes && hoy.getFullYear() === this.anio
        const bloques = this.disponibilidades.filter(b => b.fecha === fecha)
        dias.push({ numero: d, mesActual: true, esHoy, bloques, fecha })
      }

      const restantes = 42 - dias.length
      for (let i = 1; i <= restantes; i++) {
        dias.push({ numero: i, mesActual: false, esHoy: false, bloques: [] })
      }

      return dias
    }
  },
  methods: {
    mesAnterior() {
      if (this.mes === 0) { this.mes = 11; this.anio-- }
      else this.mes--
      this.cargarDisponibilidad()
    },
    mesSiguiente() {
      if (this.mes === 11) { this.mes = 0; this.anio++ }
      else this.mes++
      this.cargarDisponibilidad()
    },
    getAuthHeader() {
      const token = localStorage.getItem('token')
      return { headers: { Authorization: `Bearer ${token}` } }
    },
    async cargarDisponibilidad() {
      if (!this.idEmpleado) return
      try {
        const res = await axios.get(
          `http://localhost:8080/api/citas/disponibilidad/${this.idEmpleado}`,
          { params: { fecha: `${this.anio}-${String(this.mes + 1).padStart(2, '0')}-01` }, ...this.getAuthHeader() }
        )
        this.disponibilidades = res.data
      } catch (error) {
        console.error('Error cargando disponibilidad:', error)
      }
    },
    async guardarHorario() {
      this.mensajeExito = ''
      this.mensajeError = ''

      if (!this.nuevoHorario.fecha || !this.nuevoHorario.horaInicio || !this.nuevoHorario.horaFin) {
        this.mensajeError = 'Completa todos los campos.'
        return
      }

      try {
        await axios.post('http://localhost:8080/api/disponibilidad', {
          idEmpleado: this.idEmpleado,
          fecha: this.nuevoHorario.fecha,
          horaInicioBloque: this.nuevoHorario.horaInicio,
          horaFinBloque: this.nuevoHorario.horaFin,
          estadoBloque: 'disponible'
        }, this.getAuthHeader())

        this.mensajeExito = '✓ Horario registrado exitosamente.'
        this.nuevoHorario = { fecha: '', horaInicio: '', horaFin: '' }
        await this.cargarDisponibilidad()
        setTimeout(() => {
          this.mostrarModalHorario = false
          this.mensajeExito = ''
        }, 2000)
      } catch (error) {
        this.mensajeError = 'Error al registrar el horario.'
      }
    }
  },
  mounted() {
    this.cargarDisponibilidad()
  }
}
</script>

<style scoped>
.agenda-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.agenda-header {
  display: flex;
  justify-content: flex-start;
}

.btn-registrar {
  background: #004518;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-registrar:hover {
  background: #145c43;
}

.calendario-container {
  background: #eaf2eb;
  border-radius: 16px;
  padding: 24px;
}

.calendario-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.calendario-nav h2 {
  color: #004518;
  font-size: 20px;
  font-weight: 700;
  min-width: 200px;
  text-align: center;
}

.btn-nav {
  background: none;
  border: none;
  font-size: 28px;
  color: #004518;
  cursor: pointer;
  font-weight: bold;
}

.calendario-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.dia-header {
  text-align: center;
  font-weight: 700;
  font-size: 13px;
  color: #4f6b55;
  padding: 8px 0;
  border-bottom: 1px solid #c5d9c7;
}

.dia-celda {
  min-height: 90px;
  background: #f4f9f5;
  border-radius: 8px;
  padding: 6px;
  position: relative;
}

.dia-celda.otro-mes {
  background: transparent;
  opacity: 0.4;
}

.dia-celda.hoy {
  background: #d4edda;
  border: 2px solid #004518;
}

.dia-numero {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e31;
}

.eventos-dia {
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-top: 4px;
}

.evento-bloque {
  font-size: 11px;
  padding: 3px 5px;
  border-radius: 4px;
  font-weight: 500;
}

.evento-bloque.disponible {
  background: #c3e6cb;
  color: #155724;
}

.evento-bloque.ocupado {
  background: #f5c6cb;
  color: #721c24;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 420px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
}

.modal-content h2 {
  color: #004518;
  margin-bottom: 20px;
  font-size: 22px;
  border-bottom: 2px solid #f0f4f1;
  padding-bottom: 10px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e31;
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid #d9e4da;
  border-radius: 6px;
  font-size: 14px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.btn-cancelar {
  background: #f1f5f2;
  border: none;
  color: #4f5d52;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-guardar {
  background: #004518;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.alerta-exito {
  background: #d4edda;
  color: #155724;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 14px;
  font-weight: 600;
}

.alerta-error {
  background: #f8d7da;
  color: #721c24;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 14px;
  font-weight: 600;
}
</style>
<template>
  <section class="miscitas-page">

    <div class="page-header">
      <div>
        <h1>Mis citas</h1>
        <p>Consulta, confirma, reprograma o cancela tus citas.</p>
      </div>
    </div>

    <!-- FILTROS -->
    <div class="filtros">
      <button
        v-for="f in filtros"
        :key="f.valor"
        class="filtro-pill"
        :class="{ activo: filtro === f.valor }"
        @click="filtro = f.valor"
      >
        {{ f.label }}
      </button>
    </div>

    <div v-if="loading" class="estado-vacio">Cargando tus citas...</div>

    <div v-else-if="citasFiltradas.length === 0" class="estado-vacio">
      {{ filtro === 'historial'
        ? 'No existen citas en el historial.'
        : 'No tienes citas próximas. ¡Reserva un servicio!' }}
    </div>

    <!-- LISTA DE CITAS -->
    <div v-else class="citas-grid">
      <div v-for="cita in citasFiltradas" :key="cita.idCita" class="cita-card">

        <div class="cita-top">
          <span class="badge" :class="`badge-${cita.estadoCita}`">
            {{ etiquetaEstado(cita.estadoCita) }}
          </span>
          <span class="cita-precio">{{ formatoMoneda(cita.precio) }}</span>
        </div>

        <h3 class="cita-servicio">{{ cita.servicio }}</h3>

        <div class="cita-datos">
          <p>📅 <span class="capitalize">{{ fechaTexto(cita.fecha) }}</span></p>
          <p>🕐 {{ horaTexto(cita.horaInicio) }} – {{ horaTexto(cita.horaFin) }}</p>
          <p>💈 {{ cita.empleado }}</p>
        </div>

        <!-- ACCIONES (solo próximas activas) -->
        <div v-if="esProxima(cita)" class="cita-acciones">
          <button
            v-if="cita.estadoCita === 'pendiente'"
            class="btn-accion confirmar"
            @click="confirmarAsistencia(cita)"
          >
            Confirmar asistencia
          </button>
          <button class="btn-accion reprogramar" @click="abrirReprogramar(cita)">
            Reprogramar
          </button>
          <button class="btn-accion cancelar" @click="pedirCancelar(cita)">
            Cancelar
          </button>
        </div>

      </div>
    </div>

    <!-- ═══ MODAL REPROGRAMAR ═══ -->
    <div v-if="reprogramar.visible" class="modal-overlay" @click.self="cerrarReprogramar">
      <div class="modal-card">

        <div class="modal-header">
          <h2>Reprogramar cita</h2>
          <button class="close-btn" @click="cerrarReprogramar">✕</button>
        </div>

        <p class="repro-info">
          {{ reprogramar.cita?.servicio }} con {{ reprogramar.cita?.empleado }}
          · duración {{ reprogramar.cita?.duracion }}
        </p>

        <div class="form-group">
          <label>Nueva fecha</label>
          <input
            type="date"
            v-model="reprogramar.fecha"
            :min="hoy"
            @change="cargarHorariosReprogramar"
          />
        </div>

        <div v-if="reprogramar.cargando" class="estado-vacio small">Buscando horarios...</div>

        <div v-else-if="reprogramar.fecha && reprogramar.bloques.length === 0" class="estado-vacio small">
          No hay horarios disponibles para esta fecha.
        </div>

        <div v-else-if="reprogramar.bloques.length" class="form-group">
          <label>Horario disponible</label>
          <div class="bloques-lista">
            <button
              v-for="b in reprogramar.bloques"
              :key="b.idDisponibilidad"
              type="button"
              class="bloque-chip"
              :class="{ activo: reprogramar.bloque?.idDisponibilidad === b.idDisponibilidad }"
              @click="seleccionarBloqueReprogramar(b)"
            >
              {{ horaTexto(b.horaInicioBloque) }} – {{ horaTexto(b.horaFinBloque) }}
            </button>
          </div>
        </div>

        <div v-if="reprogramar.bloque" class="form-group">
          <label>Hora de inicio</label>
          <div class="bloques-lista">
            <button
              v-for="slot in slotsReprogramar"
              :key="slot"
              type="button"
              class="bloque-chip"
              :class="{ activo: reprogramar.horaInicio === slot }"
              @click="reprogramar.horaInicio = slot"
            >
              {{ horaTexto(slot) }}
            </button>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-secundario" @click="cerrarReprogramar">Cancelar</button>
          <button
            class="btn-primario"
            :disabled="!reprogramar.horaInicio || reprogramar.guardando"
            @click="confirmarReprogramacion"
          >
            {{ reprogramar.guardando ? 'Guardando...' : 'Confirmar cambio' }}
          </button>
        </div>

      </div>
    </div>

    <!-- CONFIRMAR CANCELACIÓN -->
    <AppConfirmModal
      :visible="confirmCancelar.visible"
      title="Cancelar cita"
      :message="`¿Cancelar tu cita de ${confirmCancelar.cita?.servicio} el ${fechaTexto(confirmCancelar.cita?.fecha)}? El horario quedará libre.`"
      @confirm="ejecutarCancelacion"
      @cancel="confirmCancelar = { visible: false, cita: null }"
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
import citaApi from '@/services/citaService'
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'

export default {
  name: 'MisCitasView',

  components: { AppToast, AppConfirmModal },

  data() {
    return {
      citas: [],
      loading: true,
      filtro: 'proximas',
      filtros: [
        { valor: 'proximas', label: 'Próximas' },
        { valor: 'historial', label: 'Historial' }
      ],
      hoy: new Date().toISOString().split('T')[0],

      reprogramar: {
        visible: false,
        cita: null,
        fecha: '',
        bloques: [],
        bloque: null,
        horaInicio: '',
        cargando: false,
        guardando: false
      },

      confirmCancelar: { visible: false, cita: null },

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    idCliente() {
      try {
        const token = localStorage.getItem('token')
        return JSON.parse(atob(token.split('.')[1])).idCliente || null
      } catch {
        return null
      }
    },

    citasFiltradas() {
      if (this.filtro === 'historial') {
        return this.citas.filter(c => !this.esProxima(c))
      }
      return this.citas.filter(c => this.esProxima(c))
    },

    slotsReprogramar() {
      const r = this.reprogramar
      if (!r.bloque || !r.cita) return []
      const dur = this.parseDuracionMinutos(r.cita.duracion)
      const slots = []
      let t = this.aMinutos(r.bloque.horaInicioBloque)
      const fin = this.aMinutos(r.bloque.horaFinBloque)
      while (t + dur <= fin) {
        slots.push(this.deMinutos(t))
        t += dur
      }
      return slots
    }
  },

  mounted() {
    this.cargarCitas()
  },

  methods: {

    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3500)
    },

    parseDuracionMinutos(duracion) {
      if (!duracion) return 60
      const texto = String(duracion).toLowerCase().trim()
      const match = texto.match(/\d+/)
      if (!match) return 60
      let valor = parseInt(match[0], 10)
      if (texto.includes('hora') || (texto.includes('h') && !texto.includes('min'))) valor *= 60
      return Math.max(valor, 5)
    },

    aMinutos(hora) {
      const [h, m] = String(hora).split(':').map(Number)
      return h * 60 + m
    },

    deMinutos(min) {
      const h = String(Math.floor(min / 60)).padStart(2, '0')
      const m = String(min % 60).padStart(2, '0')
      return `${h}:${m}:00`
    },

    esProxima(cita) {
      if (!cita) return false
      const estado = (cita.estadoCita || '').toLowerCase()
      if (estado === 'cancelada' || estado === 'finalizada') return false
      return cita.fecha >= this.hoy
    },

    etiquetaEstado(estado) {
      const map = {
        pendiente: 'Programada',
        confirmada: 'Confirmada',
        finalizada: 'Finalizada',
        cancelada: 'Cancelada'
      }
      return map[estado] || estado
    },

    fechaTexto(fecha) {
      if (!fecha) return ''
      return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
        weekday: 'long', day: 'numeric', month: 'long'
      })
    },

    horaTexto(hora) {
      if (!hora) return ''
      const [h, m] = String(hora).split(':').map(Number)
      const d = new Date()
      d.setHours(h, m)
      return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true })
    },

    formatoMoneda(valor) {
      return Number(valor || 0).toLocaleString('es-CO', {
        style: 'currency', currency: 'COP', maximumFractionDigits: 0
      })
    },

    async cargarCitas() {
      this.loading = true
      try {
        if (!this.idCliente) {
          this.citas = []
          return
        }
        const res = await citaApi.getCitasCliente(this.idCliente)
        this.citas = res.data
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudieron cargar tus citas.')
      } finally {
        this.loading = false
      }
    },

    // CONFIRMAR ASISTENCIA
    async confirmarAsistencia(cita) {
      try {
        await citaApi.confirmarCita(cita.idCita)
        this.mostrarToast('success', 'Asistencia confirmada', 'Te esperamos en tu cita.')
        await this.cargarCitas()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo confirmar la asistencia.'
        this.mostrarToast('error', 'Error', msg)
      }
    },

    // CANCELAR
    pedirCancelar(cita) {
      this.confirmCancelar = { visible: true, cita }
    },

    async ejecutarCancelacion() {
      const cita = this.confirmCancelar.cita
      this.confirmCancelar = { visible: false, cita: null }
      try {
        await citaApi.cancelarCita(cita.idCita)
        this.mostrarToast('success', 'Cita cancelada', 'El horario quedó disponible nuevamente.')
        await this.cargarCitas()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo cancelar la cita.'
        this.mostrarToast('error', 'Error al cancelar', msg)
      }
    },

    // REPROGRAMAR
    abrirReprogramar(cita) {
      this.reprogramar = {
        visible: true, cita, fecha: '', bloques: [],
        bloque: null, horaInicio: '', cargando: false, guardando: false
      }
    },

    cerrarReprogramar() {
      this.reprogramar.visible = false
    },

    async cargarHorariosReprogramar() {
      const r = this.reprogramar
      r.bloque = null
      r.horaInicio = ''
      r.bloques = []
      if (!r.fecha) return
      r.cargando = true
      try {
        const res = await citaApi.getDisponibilidad(r.cita.idEmpleado, r.fecha)
        r.bloques = res.data
      } catch (e) {
        console.error(e)
      } finally {
        r.cargando = false
      }
    },

    seleccionarBloqueReprogramar(b) {
      this.reprogramar.bloque = b
      this.reprogramar.horaInicio = ''
    },

    async confirmarReprogramacion() {
      const r = this.reprogramar
      if (!r.horaInicio) return
      r.guardando = true
      try {
        await citaApi.reprogramarCita(r.cita.idCita, {
          idDisponibilidad: r.bloque.idDisponibilidad,
          horaInicio: r.horaInicio
        })
        this.cerrarReprogramar()
        this.mostrarToast('success', 'Cita reprogramada', `Tu cita quedó para el ${this.fechaTexto(r.fecha)} a las ${this.horaTexto(r.horaInicio)}.`)
        await this.cargarCitas()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo reprogramar la cita.'
        this.mostrarToast('error', 'Error al reprogramar', msg)
      } finally {
        r.guardando = false
      }
    }
  }
}
</script>

<style scoped>
.miscitas-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #173221;
}

.page-header p {
  margin: 6px 0 0;
  font-size: 15px;
  color: #5f6f66;
}

/* FILTROS */
.filtros {
  display: flex;
  gap: 8px;
}

.filtro-pill {
  padding: 9px 18px;
  border-radius: 999px;
  border: 1px solid #d7e2da;
  background: #ffffff;
  color: #4f5d52;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.18s ease;
}

.filtro-pill:hover { background: #f0f7f1; }

.filtro-pill.activo {
  background: #014421;
  border-color: #014421;
  color: #ffffff;
}

/* ESTADOS */
.estado-vacio {
  padding: 50px 20px;
  text-align: center;
  color: #8a9b8f;
  font-style: italic;
  background: #ffffff;
  border: 1px dashed #d9e8db;
  border-radius: 18px;
}

.estado-vacio.small {
  padding: 18px;
  border-radius: 12px;
  font-size: 13px;
}

/* CARDS */
.citas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.cita-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cita-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.badge {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  padding: 5px 12px;
  border-radius: 999px;
}

.badge-pendiente  { background: #fdf5e6; color: #8a5a0a; border: 1px solid #f0d895; }
.badge-confirmada { background: #e7f4ea; color: #1d7a3a; border: 1px solid #bfe3c8; }
.badge-finalizada { background: #eef1ef; color: #5f6f66; border: 1px solid #d7e0d9; }
.badge-cancelada  { background: #fdecec; color: #b42318; border: 1px solid #f3c2bd; }

.cita-precio {
  font-weight: 800;
  color: #014421;
  font-size: 15px;
}

.cita-servicio {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #173221;
}

.cita-datos p {
  margin: 4px 0;
  font-size: 13.5px;
  color: #4f5d52;
}

.capitalize { text-transform: capitalize; }

.cita-acciones {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
}

.btn-accion {
  flex: 1;
  min-width: 110px;
  padding: 10px 8px;
  border-radius: 12px;
  font-size: 12.5px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.18s ease;
  border: 1px solid transparent;
}

.btn-accion.confirmar {
  background: #014421;
  color: white;
}
.btn-accion.confirmar:hover { background: #1f6a34; }

.btn-accion.reprogramar {
  background: #f0f7f1;
  color: #014421;
  border-color: #cfe3d3;
}
.btn-accion.reprogramar:hover { background: #e0efe3; }

.btn-accion.cancelar {
  background: #fdecec;
  color: #b42318;
  border-color: #f3c2bd;
}
.btn-accion.cancelar:hover { background: #fad6d3; }

/* MODAL */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(10, 20, 14, 0.5);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 999;
}

.modal-card {
  width: 100%;
  max-width: 460px;
  max-height: 92vh;
  overflow-y: auto;
  background: #f9fcf8;
  border: 1px solid #d7e3d6;
  border-radius: 26px;
  padding: 28px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.16);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.modal-header h2 {
  margin: 0;
  font-size: 21px;
  font-weight: 700;
  color: #173221;
}

.close-btn {
  width: 34px;
  height: 34px;
  background: #ffffff;
  border: 1px solid #d7e3d6;
  border-radius: 11px;
  font-size: 16px;
  color: #6b7a72;
  cursor: pointer;
  transition: all 0.18s ease;
}

.close-btn:hover { background: #f0f7f1; color: #004518; }

.repro-info {
  margin: 0 0 16px;
  font-size: 13.5px;
  color: #4f5d52;
  background: #f0f7f1;
  border: 1px solid #d9e8db;
  border-radius: 12px;
  padding: 10px 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 13px;
  font-weight: 700;
  color: #264434;
}

.form-group input[type="date"] {
  padding: 12px 14px;
  border: 1px solid #d5dfd4;
  border-radius: 14px;
  font-size: 14px;
  font-family: inherit;
  color: #173221;
  background: #ffffff;
  outline: none;
}

.form-group input[type="date"]:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.bloques-lista {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.bloque-chip {
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid #d7e2da;
  background: #ffffff;
  color: #1e2a22;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.16s ease;
}

.bloque-chip:hover { border-color: #004518; background: #f0f7f1; }

.bloque-chip.activo {
  background: #004518;
  border-color: #004518;
  color: #ffffff;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.btn-secundario {
  background: #eef3ef;
  color: #2f4338;
  border: 1px solid #d7e3d6;
  padding: 12px 18px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
}

.btn-secundario:hover { background: #dde7df; }

.btn-primario {
  background: #014421;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
}

.btn-primario:hover:not(:disabled) { background: #1f6a34; }

.btn-primario:disabled { opacity: 0.45; cursor: not-allowed; }

/* RESPONSIVE */
@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .citas-grid { grid-template-columns: 1fr; }
  .modal-card { padding: 22px 16px; }
  .form-actions { flex-direction: column-reverse; }
  .btn-primario, .btn-secundario { width: 100%; }
}
</style>

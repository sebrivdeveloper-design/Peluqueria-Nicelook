<template>

  <section class="agenda-view">

    <!-- HEADER -->
    <div class="agenda-header">

      <div class="header-text">
        <h1>Agenda Recepción</h1>
        <p>Gestiona citas y disponibilidad del equipo</p>
      </div>

      <!-- SELECTOR ESTILISTA -->
      <div class="estilista-selector">
        <label class="estilista-label">Estilista</label>
        <SearchableSelect
          v-model="idEmpleado"
          :options="empleadosOpts"
          label-key="label"
          value-key="value"
          placeholder="Selecciona un estilista"
          search-placeholder="Buscar estilista..."
          @update:modelValue="cargarAgenda"
        />
      </div>

    </div>

    <!-- CALENDARIO -->
    <AgendaCalendar
      :eventos="eventos"
      @eventClick="handleEventClick"
    />

    <!-- =============================================
         MODAL: AGENDAR CITA (bloque disponible)
    ============================================== -->
    <div
      v-if="mostrarModal"
      class="modal-overlay"
      @click.self="cerrarModal"
    >

      <div class="modal-card">

        <div class="modal-header">
          <h2>Agendar cita</h2>
          <button class="close-btn" @click="cerrarModal">✕</button>
        </div>

        <!-- INFO DEL BLOQUE -->
        <div class="bloque-info">
          <span class="bloque-chip">{{ fechaBloqueTexto }}</span>
          <span class="bloque-chip horario">{{ rangoBloqueTexto }}</span>
        </div>

        <!-- CLIENTE -->
        <div class="form-group">
          <label>Cliente</label>
          <SearchableSelect
            v-model="form.idCliente"
            :options="clientesOpts"
            label-key="label"
            value-key="value"
            placeholder="Selecciona un cliente"
            search-placeholder="Buscar por nombre o documento..."
          />
        </div>

        <!-- SERVICIO -->
        <div class="form-group">
          <label>Servicio</label>
          <SearchableSelect
            v-model="form.idServicio"
            :options="serviciosOpts"
            label-key="label"
            value-key="value"
            placeholder="Selecciona un servicio"
            search-placeholder="Buscar servicio..."
            @update:modelValue="form.slot = null"
          />
        </div>

        <!-- HORA DE LA CITA -->
        <div class="form-group" v-if="form.idServicio">
          <label>
            Hora de inicio
            <span class="duracion-hint">· {{ duracionSeleccionada }} min</span>
          </label>

          <div v-if="slotsDisponibles.length" class="slots-grid">
            <button
              v-for="slot in slotsDisponibles"
              :key="slot.getTime()"
              type="button"
              class="slot-chip"
              :class="{ activo: form.slot && form.slot.getTime() === slot.getTime() }"
              @click="form.slot = slot"
            >
              {{ formatHora(slot) }}
            </button>
          </div>

          <p v-else class="slots-vacio">
            La duración del servicio ({{ duracionSeleccionada }} min) no cabe en este bloque.
          </p>
        </div>

        <!-- RESUMEN -->
        <div v-if="form.slot" class="resumen-cita">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="15" height="15"><path d="M9 12l2 2 4-4"/><circle cx="12" cy="12" r="10"/></svg>
          La cita quedará de
          <strong>{{ formatHora(form.slot) }}</strong> a
          <strong>{{ formatHora(finCitaEstimado) }}</strong>.
          El resto del bloque seguirá disponible.
        </div>

        <!-- BOTONES -->
        <div class="form-actions">
          <button class="btn-secondary" @click="cerrarModal">Cancelar</button>
          <button
            class="btn-primary"
            :disabled="!form.idCliente || !form.idServicio || !form.slot"
            @click="guardarCita"
          >
            Confirmar cita
          </button>
        </div>

      </div>
    </div>

    <!-- =============================================
         MODAL: DETALLE DE CITA (bloque ocupado)
    ============================================== -->
    <div
      v-if="citaDetalle.visible"
      class="modal-overlay"
      @click.self="cerrarDetalle"
    >

      <div class="modal-card detalle-card">

        <div class="modal-header">
          <div class="detalle-badge">
            <span class="detalle-dot"></span>
            Cita agendada
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
            <span class="detalle-icon">🕐</span>
            <div>
              <p class="detalle-label">Horario</p>
              <p class="detalle-value">{{ citaDetalle.horaInicio }} – {{ citaDetalle.horaFin }}</p>
            </div>
          </div>
        </div>

        <div class="form-actions detalle-actions">
          <button class="btn-secondary" @click="cerrarDetalle">Cerrar</button>
          <button
            class="btn-cancelar-cita"
            @click="confirmarCancelarCita"
            :disabled="cancelando"
          >
            {{ cancelando ? 'Cancelando...' : 'Cancelar cita' }}
          </button>
        </div>

      </div>
    </div>

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
import api from '@/services/axiosInstance'
import AgendaCalendar from '@/components/AgendaCalendar.vue'
import AppToast from '@/components/AppToast.vue'
import SearchableSelect from '@/components/SearchableSelect.vue'

export default {

  name: 'AgendaRecepcionistaView',

  components: { AgendaCalendar, AppToast, SearchableSelect },

  data() {
    return {
      empleados: [],
      clientes: [],
      servicios: [],
      eventos: [],

      idEmpleado: null,

      mostrarModal: false,
      bloqueSeleccionado: null,

      form: { idCliente: null, idServicio: null, slot: null },

      citaDetalle: {
        visible: false,
        idCita: null,
        cliente: '',
        servicio: '',
        horaInicio: '',
        horaFin: ''
      },

      cancelando: false,

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {

    empleadosOpts() {
      return this.empleados.map(e => ({
        value: e.idEmpleado,
        label: e.usuario?.nombreCompleto || 'Sin nombre'
      }))
    },

    clientesOpts() {
      return this.clientes.map(c => ({
        value: c.idCliente,
        label: c.nombreCompleto + (c.documento ? ` · ${c.documento}` : '')
      }))
    },

    serviciosOpts() {
      return this.servicios
        .filter(s => s.estado === 'activo')
        .map(s => ({
          value: s.idServicio,
          label: `${s.nombreServicio} — ${s.duracion}`
        }))
    },

    servicioSeleccionado() {
      return this.servicios.find(s => s.idServicio === this.form.idServicio) || null
    },

    duracionSeleccionada() {
      return this.parseDuracionMinutos(this.servicioSeleccionado?.duracion)
    },

    slotsDisponibles() {
      if (!this.bloqueSeleccionado || !this.servicioSeleccionado) return []
      const durMs = this.duracionSeleccionada * 60000
      const inicio = new Date(this.bloqueSeleccionado.start)
      const fin = new Date(this.bloqueSeleccionado.end)
      const slots = []
      let t = inicio.getTime()
      while (t + durMs <= fin.getTime()) {
        slots.push(new Date(t))
        t += durMs
      }
      return slots
    },

    finCitaEstimado() {
      if (!this.form.slot) return null
      return new Date(this.form.slot.getTime() + this.duracionSeleccionada * 60000)
    },

    fechaBloqueTexto() {
      if (!this.bloqueSeleccionado) return ''
      return new Date(this.bloqueSeleccionado.start).toLocaleDateString('es-CO', {
        weekday: 'long', day: 'numeric', month: 'long'
      })
    },

    rangoBloqueTexto() {
      if (!this.bloqueSeleccionado) return ''
      return `${this.formatHora(this.bloqueSeleccionado.start)} – ${this.formatHora(this.bloqueSeleccionado.end)}`
    }
  },

  mounted() {
    this.cargarEmpleados()
    this.cargarClientes()
    this.cargarServicios()
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

    formatHora(date) {
      if (!date) return ''
      return new Date(date).toLocaleTimeString('es-CO', {
        hour: '2-digit', minute: '2-digit', hour12: true
      })
    },

    async cargarEmpleados() {
      try {
        const res = await api.get('/empleados')
        this.empleados = res.data
      } catch (e) { console.error(e) }
    },

    async cargarClientes() {
      try {
        const res = await api.get('/clientes')
        this.clientes = res.data
      } catch (e) { console.error(e) }
    },

    async cargarServicios() {
      try {
        const res = await api.get('/servicios')
        this.servicios = res.data
      } catch (e) { console.error(e) }
    },

    async cargarAgenda() {
      if (!this.idEmpleado) return
      try {
        const hoy = new Date()
        const anio = hoy.getFullYear()
        const mes = String(hoy.getMonth() + 1).padStart(2, '0')

        const res = await api.get(
          `/disponibilidad/${this.idEmpleado}`,
          { params: { mes, anio } }
        )

        this.eventos = res.data.map(b => ({
          id: b.idDisponibilidad,
          title: b.estado === 'ocupado' ? `${b.cliente} · ${b.servicio}` : 'Disponible',
          start: `${b.fecha}T${b.horaInicio}`,
          end:   `${b.fecha}T${b.horaFin}`,
          className: b.estado === 'ocupado' ? 'evento-ocupado' : 'evento-disponible',
          extendedProps: {
            estado: b.estado,
            idDisponibilidad: b.idDisponibilidad,
            idCita: b.idCita,
            cliente: b.cliente,
            servicio: b.servicio,
            horaInicio: b.horaInicio,
            horaFin: b.horaFin
          }
        }))
      } catch (e) { console.error(e) }
    },

    // CLICK EN EVENTO DEL CALENDARIO
    handleEventClick(info) {
      const evt = info.event
      const props = evt.extendedProps

      if (props.estado === 'ocupado') {
        // Mostrar detalle con opción de cancelar
        this.citaDetalle = {
          visible: true,
          idCita: props.idCita,
          cliente: props.cliente,
          servicio: props.servicio,
          horaInicio: this.formatHora(evt.start),
          horaFin: this.formatHora(evt.end)
        }
        return
      }

      // Bloque disponible → abrir modal de agendar
      this.bloqueSeleccionado = evt
      this.form = { idCliente: null, idServicio: null, slot: null }
      this.mostrarModal = true
    },

    cerrarModal() {
      this.mostrarModal = false
      this.form = { idCliente: null, idServicio: null, slot: null }
    },

    cerrarDetalle() {
      this.citaDetalle = { visible: false, idCita: null, cliente: '', servicio: '', horaInicio: '', horaFin: '' }
    },

    // GUARDAR CITA
    async guardarCita() {
      try {
        if (!this.form.idCliente || !this.form.idServicio || !this.form.slot) {
          this.mostrarToast('warning', 'Campos incompletos', 'Selecciona el cliente, el servicio y la hora.')
          return
        }

        const inicio = this.form.slot
        const fin    = this.finCitaEstimado

        const fecha = [
          inicio.getFullYear(),
          String(inicio.getMonth() + 1).padStart(2, '0'),
          String(inicio.getDate()).padStart(2, '0')
        ].join('-')

        await api.post('/citas', {
          idCliente:        this.form.idCliente,
          idEmpleado:       this.idEmpleado,
          idServicio:       this.form.idServicio,
          idDisponibilidad: this.bloqueSeleccionado.extendedProps.idDisponibilidad,
          fecha,
          horaInicio: inicio.toTimeString().slice(0, 8),
          horaFin:    fin.toTimeString().slice(0, 8),
          observaciones: ''
        })

        this.mostrarToast('success', 'Cita agendada', 'La cita quedó registrada. El resto del horario sigue disponible.')
        this.cerrarModal()
        await this.cargarAgenda()

      } catch (error) {
        console.error(error)
        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo agendar la cita. Intenta de nuevo.'
        this.mostrarToast('error', 'Error al agendar', msg)
      }
    },

    // CANCELAR CITA
    async confirmarCancelarCita() {
      if (!this.citaDetalle.idCita) {
        this.mostrarToast('error', 'Sin referencia', 'No se pudo identificar la cita. Recarga la agenda.')
        return
      }
      try {
        this.cancelando = true
        await api.delete(`/citas/${this.citaDetalle.idCita}`)
        this.mostrarToast('success', 'Cita cancelada', 'El horario volvió a estar disponible.')
        this.cerrarDetalle()
        await this.cargarAgenda()
      } catch (error) {
        console.error(error)
        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo cancelar la cita.'
        this.mostrarToast('error', 'Error al cancelar', msg)
      } finally {
        this.cancelando = false
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
  align-items: flex-end;
  gap: 20px;
  flex-wrap: wrap;
}

.header-text h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #173221;
}

.header-text p {
  margin: 6px 0 0;
  color: #5f6f66;
  font-size: 15px;
  font-weight: 500;
}

/* =========================
   SELECTOR ESTILISTA
========================= */

.estilista-selector {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 280px;
}

.estilista-label {
  font-size: 12px;
  font-weight: 700;
  color: #4a7c59;
  text-transform: uppercase;
  letter-spacing: 0.8px;
}

/* =========================
   MODAL OVERLAY
========================= */

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

/* =========================
   MODAL CARD (agendar)
========================= */

.modal-card {
  width: 100%;
  max-width: 480px;
  max-height: 92vh;
  overflow-y: auto;
  background: #f9fcf8;
  border: 1px solid #d7e3d6;
  border-radius: 26px;
  padding: 30px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.16);
  animation: modalFade 0.22s ease;
}

@keyframes modalFade {
  from { opacity: 0; transform: translateY(14px); }
  to   { opacity: 1; transform: translateY(0); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 22px;
}

.modal-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #173221;
}

.close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border: 1px solid #d7e3d6;
  border-radius: 12px;
  font-size: 18px;
  color: #6b7a72;
  cursor: pointer;
  transition: all 0.18s ease;
  flex-shrink: 0;
}

.close-btn:hover {
  background: #f0f7f1;
  color: #004518;
  border-color: #a4c4ac;
}

/* INFO DEL BLOQUE */

.bloque-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.bloque-chip {
  background: #eef6ef;
  border: 1px solid #d1e8d4;
  color: #014421;
  font-size: 12.5px;
  font-weight: 700;
  padding: 6px 14px;
  border-radius: 999px;
  text-transform: capitalize;
}

.bloque-chip.horario {
  background: #f0f7f1;
  font-variant-numeric: tabular-nums;
}

/* FORM GROUPS */

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}

.form-group label {
  font-size: 13px;
  font-weight: 700;
  color: #264434;
}

/* HORA / SLOTS */

.duracion-hint {
  font-weight: 600;
  color: #7a8f80;
  font-size: 12px;
}

.slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 9px;
}

.slot-chip {
  padding: 11px 6px;
  border-radius: 12px;
  border: 1px solid #d7e2da;
  background: #ffffff;
  color: #1e2a22;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.16s ease;
  font-family: inherit;
}

.slot-chip:hover {
  border-color: #004518;
  background: #f0f7f1;
  color: #004518;
}

.slot-chip.activo {
  background: #004518;
  border-color: #004518;
  color: #ffffff;
  box-shadow: 0 5px 14px rgba(0, 69, 24, 0.24);
}

.slots-vacio {
  margin: 0;
  font-size: 13px;
  color: #8a5a0a;
  background: #fdf5e6;
  border: 1px solid #f0d895;
  border-radius: 12px;
  padding: 12px 14px;
}

/* RESUMEN */

.resumen-cita {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #eef6ef;
  border: 1px solid #d1e8d4;
  border-radius: 12px;
  padding: 11px 14px;
  font-size: 13px;
  color: #2f4338;
  margin-bottom: 2px;
}

.resumen-cita svg { flex-shrink: 0; color: #1d7a3a; }
.resumen-cita strong { color: #014421; }

/* BOTONES */

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 22px;
}

.btn-primary {
  background: linear-gradient(135deg, #004518 0%, #0b5d23 100%);
  color: white;
  border: none;
  padding: 13px 26px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.22s ease;
  font-family: inherit;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 69, 24, 0.2);
}

.btn-primary:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #eef3ef;
  color: #2f4338;
  border: 1px solid #d7e3d6;
  padding: 13px 20px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
  font-family: inherit;
}

.btn-secondary:hover { background: #dde7df; }

/* =========================
   MODAL DETALLE CITA
========================= */

.detalle-card {
  max-width: 420px;
}

.detalle-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #173221;
}

.detalle-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #c0392b;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.18);
}

.detalle-grid {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 6px;
}

.detalle-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  background: #ffffff;
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
}

.detalle-actions {
  margin-top: 20px;
}

.btn-cancelar-cita {
  background: linear-gradient(135deg, #b42318 0%, #d9534f 100%);
  color: #ffffff;
  border: none;
  padding: 13px 22px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.22s ease;
  font-family: inherit;
}

.btn-cancelar-cita:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(180, 35, 24, 0.22);
}

.btn-cancelar-cita:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

/* =========================
   RESPONSIVE
========================= */

@media (max-width: 768px) {

  .agenda-header {
    flex-direction: column;
    align-items: stretch;
  }

  .estilista-selector {
    min-width: unset;
  }

  .modal-card {
    padding: 22px 18px;
  }

  :deep(.fc-header-toolbar) {
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }

  :deep(.fc-toolbar-title) {
    font-size: 17px;
    text-align: center;
    width: 100%;
  }
}

@media (max-width: 480px) {

  .modal-card {
    padding: 18px 14px;
    border-radius: 20px;
  }

  .slots-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .form-actions {
    flex-direction: column-reverse;
    gap: 10px;
  }

  .btn-primary,
  .btn-secondary,
  .btn-cancelar-cita {
    width: 100%;
    text-align: center;
  }
}

</style>

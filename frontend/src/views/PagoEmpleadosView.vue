<template>
  <section class="pe-page">

    <div class="page-header">
      <div>
        <h1>Pagos a empleados</h1>
        <p>Calcula y registra el pago por los servicios realizados en un periodo.</p>
      </div>
    </div>

    <!-- TABS -->
    <div class="tabs">
      <button class="tab" :class="{ activo: tab === 'calcular' }" @click="tab = 'calcular'">Calcular pago</button>
      <button class="tab" :class="{ activo: tab === 'historial' }" @click="tab = 'historial'">Historial</button>
    </div>

    <!-- FILTROS -->
    <div class="filtros-card">
      <div class="filtro-item">
        <label>Empleado</label>
        <SearchableSelect
          v-model="idEmpleado"
          :options="empleadosOpts"
          label-key="label"
          value-key="value"
          placeholder="Selecciona un empleado"
          search-placeholder="Buscar empleado..."
          @update:modelValue="onCambioFiltro"
        />
      </div>
      <div class="filtro-item">
        <label>Desde</label>
        <input type="date" v-model="desde" @change="onCambioFiltro" />
      </div>
      <div class="filtro-item">
        <label>Hasta</label>
        <input type="date" v-model="hasta" @change="onCambioFiltro" />
      </div>
    </div>

    <!-- ═══ CALCULAR ═══ -->
    <div v-if="tab === 'calcular'" class="seccion-card">

      <div v-if="!idEmpleado" class="vacio">Selecciona un empleado y un rango de fechas.</div>

      <div v-else-if="servicios.length === 0" class="vacio">
        No hay servicios finalizados de este empleado en el periodo seleccionado.
      </div>

      <template v-else>
        <div class="tabla-wrap">
          <table class="tabla">
            <thead>
              <tr>
                <th>Servicio</th>
                <th>Precio</th>
                <th>%</th>
                <th>Cantidad</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in servicios" :key="s.idServicio">
                <td class="td-bold">{{ s.nombre }}</td>
                <td>{{ formatoMoneda(s.precioUnitario) }}</td>
                <td>{{ s.porcentaje }}%</td>
                <td>
                  <input
                    type="number"
                    min="0"
                    class="cantidad-input"
                    v-model.number="s.cantidad"
                  />
                </td>
                <td class="td-valor">{{ formatoMoneda(subtotalFila(s)) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="total-bar">
          <span>Total a pagar</span>
          <strong>{{ formatoMoneda(totalCalculado) }}</strong>
        </div>

        <div class="acciones">
          <button class="btn-primario" :disabled="totalCalculado <= 0 || guardando" @click="confirmarRegistro = true">
            {{ guardando ? 'Registrando...' : 'Registrar pago' }}
          </button>
        </div>
      </template>
    </div>

    <!-- ═══ HISTORIAL ═══ -->
    <div v-else class="seccion-card">

      <div v-if="!idEmpleado" class="vacio">Selecciona un empleado para ver su historial.</div>

      <div v-else-if="historial.length === 0" class="vacio">
        No existen pagos registrados para este empleado.
      </div>

      <div v-else class="historial-lista">
        <div v-for="pago in historial" :key="pago.idPagoEmpleado" class="historial-item">
          <div class="historial-head">
            <div>
              <p class="historial-periodo">{{ fechaTexto(pago.periodoInicio) }} → {{ fechaTexto(pago.periodoFin) }}</p>
              <p class="historial-fecha">Pagado el {{ fechaTexto(pago.fechaPago) }}</p>
            </div>
            <span class="historial-monto">{{ formatoMoneda(pago.montoTotal) }}</span>
          </div>
          <div class="historial-detalles">
            <div v-for="(d, i) in pago.detalles" :key="i" class="detalle-linea">
              <span>{{ d.servicio }} × {{ d.cantidad }}</span>
              <span>{{ formatoMoneda(d.subtotal) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AppConfirmModal
      :visible="confirmarRegistro"
      title="Registrar pago"
      :message="`¿Registrar el pago de ${formatoMoneda(totalCalculado)} a ${nombreEmpleadoSel}?`"
      @confirm="registrar"
      @cancel="confirmarRegistro = false"
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
import api from '@/services/axiosInstance'
import { getServiciosTrabajados, registrarPagoEmpleado, getHistorialPagos } from '@/services/pagoEmpleadoService'
import SearchableSelect from '@/components/SearchableSelect.vue'
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'

export default {
  name: 'PagoEmpleadosView',

  components: { SearchableSelect, AppToast, AppConfirmModal },

  data() {
    const hoy = new Date()
    const hace7 = new Date(hoy.getTime() - 6 * 86400000)
    return {
      tab: 'calcular',
      empleados: [],
      idEmpleado: null,
      desde: hace7.toISOString().split('T')[0],
      hasta: hoy.toISOString().split('T')[0],
      servicios: [],
      historial: [],
      guardando: false,
      confirmarRegistro: false,
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

    nombreEmpleadoSel() {
      const e = this.empleados.find(x => x.idEmpleado === this.idEmpleado)
      return e?.usuario?.nombreCompleto || 'el empleado'
    },

    totalCalculado() {
      return this.servicios.reduce((acc, s) => acc + this.subtotalFila(s), 0)
    }
  },

  mounted() {
    this.cargarEmpleados()
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3500)
    },

    formatoMoneda(valor) {
      return Number(valor || 0).toLocaleString('es-CO', {
        style: 'currency', currency: 'COP', maximumFractionDigits: 0
      })
    },

    fechaTexto(fecha) {
      if (!fecha) return ''
      return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
        day: 'numeric', month: 'short', year: 'numeric'
      })
    },

    subtotalFila(s) {
      const cant = Number(s.cantidad || 0)
      return Number(s.precioUnitario || 0) * cant * Number(s.porcentaje || 0) / 100
    },

    async cargarEmpleados() {
      try {
        const res = await api.get('/empleados')
        this.empleados = res.data
      } catch (e) { console.error(e) }
    },

    onCambioFiltro() {
      if (!this.idEmpleado) return
      if (this.tab === 'calcular') this.cargarServicios()
      else this.cargarHistorial()
    },

    async cargarServicios() {
      try {
        const res = await getServiciosTrabajados(this.idEmpleado, this.desde, this.hasta)
        this.servicios = res.data.map(s => ({ ...s }))
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudieron cargar los servicios.')
      }
    },

    async cargarHistorial() {
      try {
        const res = await getHistorialPagos(this.idEmpleado)
        this.historial = res.data
      } catch (e) {
        console.error(e)
      }
    },

    async registrar() {
      this.confirmarRegistro = false
      this.guardando = true
      try {
        await registrarPagoEmpleado({
          idEmpleado: this.idEmpleado,
          periodoInicio: this.desde,
          periodoFin: this.hasta,
          detalles: this.servicios
            .filter(s => Number(s.cantidad) > 0)
            .map(s => ({ idServicio: s.idServicio, cantidad: Number(s.cantidad) }))
        })
        this.mostrarToast('success', 'Pago registrado', `Se registró el pago a ${this.nombreEmpleadoSel}.`)
        this.cargarServicios()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo registrar el pago.'
        this.mostrarToast('error', 'Error', msg)
      } finally {
        this.guardando = false
      }
    }
  },

  watch: {
    tab() {
      this.onCambioFiltro()
    }
  }
}
</script>

<style scoped>
.pe-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.tabs {
  display: flex;
  gap: 8px;
}

.tab {
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

.tab:hover { background: #f0f7f1; }
.tab.activo { background: #014421; border-color: #014421; color: #ffffff; }

.filtros-card {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 14px;
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.filtro-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filtro-item label {
  font-size: 12px;
  font-weight: 700;
  color: #4a7c59;
  text-transform: uppercase;
  letter-spacing: 0.6px;
}

.filtro-item input[type="date"] {
  padding: 13px 14px;
  border: 1px solid #d7e2da;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: #173221;
  background: #ffffff;
  outline: none;
}

.filtro-item input[type="date"]:focus {
  border-color: #004518;
  box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08);
}

.seccion-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.vacio {
  padding: 30px;
  text-align: center;
  color: #8a9b8f;
  font-style: italic;
  font-size: 14px;
}

.tabla-wrap { overflow-x: auto; }

.tabla {
  width: 100%;
  min-width: 600px;
  border-collapse: collapse;
  font-size: 13.5px;
  color: #173221;
}

.tabla th {
  background: #f0f7f1;
  color: #4a7c59;
  padding: 10px 14px;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 10px;
  letter-spacing: 1.3px;
  text-align: left;
  white-space: nowrap;
}

.tabla td {
  padding: 12px 14px;
  border-top: 1px solid #edf2ee;
  vertical-align: middle;
}

.td-bold { font-weight: 700; }
.td-valor { font-weight: 700; color: #014421; white-space: nowrap; }

.cantidad-input {
  width: 70px;
  padding: 8px 10px;
  border: 1px solid #d5dfd4;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  text-align: center;
  outline: none;
}

.cantidad-input:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 3px rgba(115, 156, 118, 0.16);
}

.total-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding: 14px 18px;
  background: #f0f7f1;
  border: 1px solid #d1e8d4;
  border-radius: 14px;
  font-size: 14px;
  color: #2f4338;
  font-weight: 600;
}

.total-bar strong { font-size: 22px; color: #014421; }

.acciones {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.btn-primario {
  background: #014421;
  color: white;
  border: none;
  padding: 13px 24px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
}

.btn-primario:hover:not(:disabled) { background: #1f6a34; }
.btn-primario:disabled { opacity: 0.45; cursor: not-allowed; }

/* HISTORIAL */
.historial-lista {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.historial-item {
  border: 1px solid #e8f0e9;
  border-radius: 14px;
  padding: 16px 18px;
  background: #f9fcf8;
}

.historial-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 10px;
}

.historial-periodo {
  margin: 0;
  font-weight: 700;
  color: #173221;
  font-size: 14px;
}

.historial-fecha {
  margin: 3px 0 0;
  font-size: 12px;
  color: #8a9b8f;
}

.historial-monto {
  font-size: 18px;
  font-weight: 800;
  color: #014421;
  white-space: nowrap;
}

.historial-detalles {
  border-top: 1px dashed #d9e8db;
  padding-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detalle-linea {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #4f5d52;
}

@media (max-width: 768px) {
  .filtros-card { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .seccion-card { padding: 18px 14px; }
}
</style>

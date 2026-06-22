<template>
  <section class="rep-page">

    <div class="page-header">
      <div>
        <h1>Reportes</h1>
        <p>Ingresos y desempeño del negocio por periodo.</p>
      </div>
    </div>

    <!-- TABS -->
    <div class="tabs">
      <button class="tab" :class="{ activo: tab === 'resumen' }" @click="tab = 'resumen'">Resumen</button>
      <button class="tab" :class="{ activo: tab === 'saldos' }" @click="irSaldos">Saldos de caja</button>
    </div>

    <!-- ═══ TAB RESUMEN ═══ -->
    <template v-if="tab === 'resumen'">
    <!-- FILTROS -->
    <div class="filtros-card">
      <div class="presets">
        <button v-for="p in presets" :key="p.valor" class="preset" :class="{ activo: preset === p.valor }" @click="aplicarPreset(p.valor)">
          {{ p.label }}
        </button>
      </div>
      <div class="rango">
        <div class="filtro-item">
          <label>Desde</label>
          <input type="date" v-model="desde" @change="preset = 'custom'; cargar()" />
        </div>
        <div class="filtro-item">
          <label>Hasta</label>
          <input type="date" v-model="hasta" @change="preset = 'custom'; cargar()" />
        </div>
      </div>
    </div>

    <div v-if="cargando" class="vacio">Cargando reporte...</div>

    <template v-else-if="datos">
      <!-- KPIs -->
      <div class="kpis">
        <div class="kpi-card">
          <span class="kpi-label">Ingresos del periodo</span>
          <span class="kpi-valor verde">{{ formatoMoneda(datos.totalIngresos) }}</span>
        </div>
        <div class="kpi-card">
          <span class="kpi-label">Citas finalizadas</span>
          <span class="kpi-valor">{{ datos.totalCitas }}</span>
        </div>
      </div>

      <div v-if="datos.totalCitas === 0 && datos.totalIngresos === 0" class="vacio">
        No hay información disponible para el periodo seleccionado.
      </div>

      <template v-else>
        <!-- SERVICIOS TOP -->
        <div class="seccion-card">
          <h2>Servicios más solicitados</h2>
          <div v-if="datos.serviciosTop.length === 0" class="vacio-mini">Sin servicios en el periodo.</div>
          <div v-else class="barras">
            <div v-for="s in datos.serviciosTop" :key="s.nombre" class="barra-row">
              <span class="barra-label">{{ s.nombre }}</span>
              <div class="barra-track">
                <div class="barra-fill" :style="{ width: anchoBarra(s.cantidad) + '%' }"></div>
              </div>
              <span class="barra-valor">{{ s.cantidad }}</span>
            </div>
          </div>
        </div>

        <!-- DESEMPEÑO BARBEROS -->
        <div class="seccion-card">
          <h2>Desempeño por estilista</h2>
          <div v-if="datos.desempenoBarberos.length === 0" class="vacio-mini">Sin datos de estilistas.</div>
          <div v-else class="tabla-wrap">
            <table class="tabla">
              <thead>
                <tr>
                  <th>Estilista</th>
                  <th>Citas atendidas</th>
                  <th>Monto generado</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="b in datos.desempenoBarberos" :key="b.nombre">
                  <td class="td-bold">{{ b.nombre }}</td>
                  <td>{{ b.citasAtendidas }}</td>
                  <td class="td-valor">{{ formatoMoneda(b.montoGenerado) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
    </template>
    </template>

    <!-- ═══ TAB SALDOS DE CAJA ═══ -->
    <template v-else>
      <div class="filtros-card">
        <div class="rango">
          <div class="filtro-item">
            <label>Mes</label>
            <select v-model.number="mes" @change="cargarSaldos">
              <option v-for="(m, i) in meses" :key="i" :value="i + 1">{{ m }}</option>
            </select>
          </div>
          <div class="filtro-item">
            <label>Año</label>
            <select v-model.number="anio" @change="cargarSaldos">
              <option v-for="a in anios" :key="a" :value="a">{{ a }}</option>
            </select>
          </div>
        </div>
        <div class="kpi-mini">
          <span class="kpi-mini-label">Saldo final del mes</span>
          <span class="kpi-mini-valor">{{ formatoMoneda(totalMes) }}</span>
        </div>
      </div>

      <div v-if="cargandoSaldos" class="vacio">Cargando saldos...</div>

      <div v-else-if="dias.length === 0" class="vacio">
        No hubo movimiento de caja en el mes seleccionado.
      </div>

      <div v-else class="seccion-card">
        <!-- Tabla (escritorio) -->
        <div class="tabla-wrap solo-desktop">
          <table class="tabla">
            <thead>
              <tr>
                <th>Día</th>
                <th>Servicios</th>
                <th>Efectivo</th>
                <th>Tarjeta</th>
                <th>Transferencia</th>
                <th>Base</th>
                <th>Saldo final</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="d in dias" :key="d.fecha" class="fila-click" @click="abrirDetalle(d.fecha)">
                <td class="td-bold">{{ formatoFechaCorta(d.fecha) }}</td>
                <td>{{ d.cantidadServicios }}</td>
                <td>{{ formatoMoneda(d.efectivo) }}</td>
                <td>{{ formatoMoneda(d.tarjeta) }}</td>
                <td>{{ formatoMoneda(d.transferencia) }}</td>
                <td>{{ formatoMoneda(d.baseCaja) }}</td>
                <td class="td-valor">{{ formatoMoneda(d.saldoFinal) }}</td>
                <td><span class="ver-mas">Ver detalle →</span></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Tarjetas (móvil) -->
        <div class="cards-dias solo-mobile">
          <button v-for="d in dias" :key="d.fecha" class="card-dia" @click="abrirDetalle(d.fecha)">
            <div class="card-dia-head">
              <strong>{{ formatoFechaCorta(d.fecha) }}</strong>
              <span class="card-dia-saldo">{{ formatoMoneda(d.saldoFinal) }}</span>
            </div>
            <div class="card-dia-meta">
              <span>{{ d.cantidadServicios }} servicios</span>
              <span>Base {{ formatoMoneda(d.baseCaja) }}</span>
            </div>
          </button>
        </div>
      </div>
    </template>

    <!-- ═══ MODAL DETALLE DEL DÍA ═══ -->
    <div v-if="detalle.visible" class="modal-overlay" @click.self="cerrarDetalle">
      <div class="modal-card">
        <div class="modal-header">
          <h2>Detalle del {{ formatoFechaLarga(detalle.fecha) }}</h2>
          <button class="close-btn" @click="cerrarDetalle">✕</button>
        </div>

        <div class="detalle-cards">
          <div class="dc"><span class="dc-l">Facturado</span><span class="dc-v">{{ formatoMoneda(detalle.datos.totalFacturado) }}</span></div>
          <div class="dc"><span class="dc-l">Arrendamiento (salón)</span><span class="dc-v">{{ formatoMoneda(detalle.datos.totalArrendamiento) }}</span></div>
          <div class="dc"><span class="dc-l">Pagado a estilistas</span><span class="dc-v">{{ formatoMoneda(detalle.datos.totalPagadoEstilistas) }}</span></div>
          <div class="dc destacada"><span class="dc-l">Saldo final (base {{ formatoMoneda(detalle.datos.baseCaja) }})</span><span class="dc-v">{{ formatoMoneda(detalle.datos.saldoFinal) }}</span></div>
        </div>

        <div v-if="(detalle.datos.filas || []).length === 0" class="vacio-mini">Sin servicios cobrados este día.</div>
        <div v-else class="tabla-wrap">
          <table class="tabla">
            <thead>
              <tr>
                <th>Hora</th>
                <th>Cliente</th>
                <th>Estilista</th>
                <th>Servicio</th>
                <th>Valor</th>
                <th>Arrend.</th>
                <th>Estilista</th>
                <th>Medio</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(f, i) in detalle.datos.filas" :key="i">
                <td class="td-hora">{{ horaCorta(f.hora) }}</td>
                <td class="td-bold">{{ f.cliente }}</td>
                <td>{{ f.estilista }}</td>
                <td>{{ f.servicio }}</td>
                <td class="td-valor">{{ formatoMoneda(f.valorServicio) }}</td>
                <td>{{ formatoMoneda(f.valorArrendamiento) }}</td>
                <td>{{ formatoMoneda(f.valorAPagarEstilista) }}</td>
                <td class="capitalize">{{ f.metodoPago }}</td>
              </tr>
            </tbody>
          </table>
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
import { getReporte, getSaldosDiarios, getSaldosDia } from '@/services/reporteService'
import AppToast from '@/components/AppToast.vue'

export default {
  name: 'ReportesView',

  components: { AppToast },

  data() {
    const hoy = new Date()
    return {
      tab: 'resumen',

      preset: 'mes',
      presets: [
        { valor: 'dia', label: 'Hoy' },
        { valor: 'semana', label: 'Semana' },
        { valor: 'mes', label: 'Mes' }
      ],
      desde: '',
      hasta: '',
      datos: null,
      cargando: false,

      // Saldos de caja
      meses: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
      mes: hoy.getMonth() + 1,
      anio: hoy.getFullYear(),
      anios: Array.from({ length: 6 }, (_, i) => hoy.getFullYear() - i),
      dias: [],
      cargandoSaldos: false,
      saldosCargados: false,
      detalle: { visible: false, fecha: '', datos: {} },

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    totalMes() {
      return this.dias.reduce((acc, d) => acc + Number(d.saldoFinal || 0), 0)
    }
  },

  mounted() {
    this.aplicarPreset('mes')
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

    formatoFechaCorta(fecha) {
      if (!fecha) return ''
      return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', { weekday: 'short', day: 'numeric', month: 'short' })
    },

    formatoFechaLarga(fecha) {
      if (!fecha) return ''
      return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
    },

    horaCorta(hora) {
      if (!hora) return ''
      const [h, m] = String(hora).split(':').map(Number)
      const d = new Date(); d.setHours(h, m)
      return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true })
    },

    irSaldos() {
      this.tab = 'saldos'
      if (!this.saldosCargados) this.cargarSaldos()
    },

    async cargarSaldos() {
      this.cargandoSaldos = true
      try {
        const res = await getSaldosDiarios(this.anio, this.mes)
        this.dias = res.data.dias
        this.saldosCargados = true
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudieron cargar los saldos de caja.')
      } finally {
        this.cargandoSaldos = false
      }
    },

    async abrirDetalle(fecha) {
      try {
        const res = await getSaldosDia(fecha)
        this.detalle = { visible: true, fecha, datos: res.data }
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudo cargar el detalle del día.')
      }
    },

    cerrarDetalle() {
      this.detalle = { visible: false, fecha: '', datos: {} }
    },

    aplicarPreset(valor) {
      this.preset = valor
      const hoy = new Date()
      const fin = hoy.toISOString().split('T')[0]
      let inicio = new Date(hoy)

      if (valor === 'dia') {
        inicio = hoy
      } else if (valor === 'semana') {
        inicio = new Date(hoy.getTime() - 6 * 86400000)
      } else if (valor === 'mes') {
        inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1)
      }

      this.desde = inicio.toISOString().split('T')[0]
      this.hasta = fin
      this.cargar()
    },

    anchoBarra(cantidad) {
      const max = Math.max(...this.datos.serviciosTop.map(s => s.cantidad), 1)
      return Math.round((cantidad / max) * 100)
    },

    async cargar() {
      if (!this.desde || !this.hasta) return
      this.cargando = true
      try {
        const res = await getReporte(this.desde, this.hasta)
        this.datos = res.data
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudo cargar el reporte.')
      } finally {
        this.cargando = false
      }
    }
  }
}
</script>

<style scoped>
.rep-page {
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

.filtros-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.presets {
  display: flex;
  gap: 8px;
}

.preset {
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

.preset:hover { background: #f0f7f1; }
.preset.activo { background: #014421; border-color: #014421; color: #ffffff; }

.rango {
  display: flex;
  gap: 12px;
}

.filtro-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.filtro-item label {
  font-size: 11px;
  font-weight: 700;
  color: #4a7c59;
  text-transform: uppercase;
  letter-spacing: 0.6px;
}

.filtro-item input[type="date"] {
  padding: 10px 12px;
  border: 1px solid #d7e2da;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: #173221;
  outline: none;
}

.filtro-item input[type="date"]:focus {
  border-color: #004518;
  box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08);
}

.kpis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.kpi-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.kpi-label {
  font-size: 13px;
  color: #5f6f66;
  font-weight: 600;
}

.kpi-valor {
  font-size: 30px;
  font-weight: 800;
  color: #173221;
}

.kpi-valor.verde { color: #014421; }

.seccion-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.seccion-card h2 {
  margin: 0 0 18px;
  font-size: 18px;
  font-weight: 700;
  color: #173221;
}

.vacio {
  padding: 40px;
  text-align: center;
  color: #8a9b8f;
  font-style: italic;
  background: #ffffff;
  border: 1px dashed #d9e8db;
  border-radius: 18px;
}

.vacio-mini {
  padding: 16px;
  text-align: center;
  color: #8a9b8f;
  font-style: italic;
  font-size: 13px;
}

/* BARRAS */
.barras {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.barra-row {
  display: grid;
  grid-template-columns: 160px 1fr 40px;
  align-items: center;
  gap: 12px;
}

.barra-label {
  font-size: 13px;
  font-weight: 600;
  color: #173221;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.barra-track {
  height: 14px;
  background: #eef5ef;
  border-radius: 999px;
  overflow: hidden;
}

.barra-fill {
  height: 100%;
  background: linear-gradient(90deg, #1f6a34, #014421);
  border-radius: 999px;
  transition: width 0.4s ease;
}

.barra-valor {
  font-size: 13px;
  font-weight: 700;
  color: #014421;
  text-align: right;
}

/* TABLA */
.tabla-wrap { overflow-x: auto; }

.tabla {
  width: 100%;
  min-width: 480px;
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
}

.tabla td {
  padding: 12px 14px;
  border-top: 1px solid #edf2ee;
}

.td-bold { font-weight: 700; }
.td-valor { font-weight: 700; color: #014421; }

/* TABS */
.tabs {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #e3ece4;
}

.tab {
  padding: 10px 18px;
  border: none;
  background: transparent;
  color: #5f6f66;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  border-bottom: 3px solid transparent;
  transition: all 0.18s ease;
}

.tab:hover { color: #014421; }
.tab.activo { color: #014421; border-bottom-color: #014421; }

/* SALDOS */
.filtros-card select {
  padding: 10px 12px;
  border: 1px solid #d7e2da;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: #173221;
  outline: none;
  background: #fff;
}
.filtros-card select:focus { border-color: #004518; box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08); }

.kpi-mini { display: flex; flex-direction: column; gap: 4px; text-align: right; }
.kpi-mini-label { font-size: 12px; color: #5f6f66; font-weight: 600; }
.kpi-mini-valor { font-size: 22px; font-weight: 800; color: #014421; }

.fila-click { cursor: pointer; }
.fila-click:hover { background: #f6fbf7; }
.ver-mas { font-size: 12px; font-weight: 700; color: #1f6a34; white-space: nowrap; }

.td-hora { font-weight: 700; color: #014421; white-space: nowrap; }
.capitalize { text-transform: capitalize; }

/* TARJETAS DÍA (móvil) */
.solo-mobile { display: none; }
.cards-dias { display: flex; flex-direction: column; gap: 10px; }
.card-dia {
  text-align: left;
  border: 1px solid #d9e8db;
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
  cursor: pointer;
  font-family: inherit;
}
.card-dia:hover { background: #f6fbf7; }
.card-dia-head { display: flex; justify-content: space-between; align-items: center; }
.card-dia-head strong { color: #173221; font-size: 15px; text-transform: capitalize; }
.card-dia-saldo { font-weight: 800; color: #014421; }
.card-dia-meta { display: flex; justify-content: space-between; margin-top: 6px; font-size: 12px; color: #6b7a72; }

/* MODAL DETALLE */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(10, 20, 14, 0.5); backdrop-filter: blur(3px);
  display: flex; align-items: center; justify-content: center; padding: 20px; z-index: 999;
}
.modal-card {
  width: 100%; max-width: 860px; max-height: 88vh; overflow-y: auto;
  background: #f9fcf8; border: 1px solid #d7e3d6; border-radius: 24px; padding: 26px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.16);
}
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.modal-header h2 { margin: 0; font-size: 20px; font-weight: 700; color: #173221; text-transform: capitalize; }
.close-btn {
  width: 34px; height: 34px; background: #fff; border: 1px solid #d7e3d6;
  border-radius: 11px; font-size: 16px; color: #6b7a72; cursor: pointer; flex-shrink: 0;
}
.close-btn:hover { background: #f0f7f1; color: #004518; }

.detalle-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 18px; }
.dc {
  background: #fff; border: 1px solid #e0ece2; border-radius: 14px; padding: 12px 14px;
  display: flex; flex-direction: column; gap: 4px;
}
.dc.destacada { background: #f0f7f1; border-color: #bfe3c8; }
.dc-l { font-size: 11.5px; color: #6b7a72; font-weight: 600; }
.dc-v { font-size: 18px; font-weight: 800; color: #014421; }

@media (max-width: 768px) {
  .solo-desktop { display: none; }
  .solo-mobile { display: block; }
  .detalle-cards { grid-template-columns: 1fr 1fr; }
}

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .filtros-card { flex-direction: column; align-items: stretch; }
  .barra-row { grid-template-columns: 110px 1fr 32px; }
  .kpi-mini { text-align: left; }
}
</style>

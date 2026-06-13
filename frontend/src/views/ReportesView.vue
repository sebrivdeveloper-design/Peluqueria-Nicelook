<template>
  <section class="rep-page">

    <div class="page-header">
      <div>
        <h1>Reportes</h1>
        <p>Ingresos y desempeño del negocio por periodo.</p>
      </div>
    </div>

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
import { getReporte } from '@/services/reporteService'
import AppToast from '@/components/AppToast.vue'

export default {
  name: 'ReportesView',

  components: { AppToast },

  data() {
    return {
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
      toast: { visible: false, type: 'info', title: '', message: '' }
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

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .filtros-card { flex-direction: column; align-items: stretch; }
  .barra-row { grid-template-columns: 110px 1fr 32px; }
}
</style>

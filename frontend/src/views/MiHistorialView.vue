<template>
  <section class="hist-page">

    <div class="page-header">
      <div>
        <h1>Mi historial</h1>
        <p>Tus servicios realizados y lo que te corresponde tras el arrendamiento.</p>
      </div>
      <button class="btn-informe" :disabled="filas.length === 0" @click="descargarInforme">
        Descargar informe PDF
      </button>
    </div>

    <!-- ═══ FILTROS ═══ -->
    <div class="filtros-card">
      <div class="filtro">
        <label>Desde</label>
        <input type="date" v-model="desde" @change="cargar" />
      </div>
      <div class="filtro">
        <label>Hasta</label>
        <input type="date" v-model="hasta" @change="cargar" />
      </div>
    </div>

    <!-- ═══ RESUMEN ═══ -->
    <div class="resumen-grid">
      <div class="resumen-card">
        <span class="rc-label">Servicios</span>
        <span class="rc-valor">{{ resumen.totalServicios || 0 }}</span>
      </div>
      <div class="resumen-card">
        <span class="rc-label">Total facturado</span>
        <span class="rc-valor">{{ formatoMoneda(resumen.totalFacturado) }}</span>
      </div>
      <div class="resumen-card">
        <span class="rc-label">Arrendamiento (salón)</span>
        <span class="rc-valor">{{ formatoMoneda(resumen.totalArrendamiento) }}</span>
      </div>
      <div class="resumen-card destacada">
        <span class="rc-label">A pagarme</span>
        <span class="rc-valor">{{ formatoMoneda(resumen.totalAPagarEstilista) }}</span>
      </div>
    </div>

    <!-- ═══ TABLA ═══ -->
    <div class="seccion-card">
      <div v-if="filas.length === 0" class="vacio">
        No hay servicios realizados en el período seleccionado.
      </div>

      <div v-else class="tabla-wrap">
        <table class="tabla">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Hora</th>
              <th>Cliente</th>
              <th>Servicio</th>
              <th>Valor servicio</th>
              <th>Arrendamiento</th>
              <th>A pagarme</th>
              <th>Pago</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="f in filas" :key="f.idCita">
              <td>{{ formatoFecha(f.fecha) }}</td>
              <td class="td-hora">{{ horaCorta(f.hora) }}</td>
              <td class="td-bold">{{ f.cliente }}</td>
              <td>{{ f.servicio }}</td>
              <td class="td-valor">{{ formatoMoneda(f.valorServicio) }}</td>
              <td>{{ formatoMoneda(f.valorArrendamiento) }}</td>
              <td class="td-valor">{{ formatoMoneda(f.valorAPagarEstilista) }}</td>
              <td>
                <span class="badge" :class="`badge-${f.estadoPago}`">
                  {{ f.estadoPago === 'pagado' ? 'Pagado' : 'Pendiente' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
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
import { getMiHistorial } from '@/services/historialEstilistaService'
import AppToast from '@/components/AppToast.vue'
import { historialPdf } from '@/utils/historialPdf'

export default {
  name: 'MiHistorialView',

  components: { AppToast },

  data() {
    const hoy = new Date()
    const primero = new Date(hoy.getFullYear(), hoy.getMonth(), 1)
    return {
      desde: primero.toISOString().split('T')[0],
      hasta: hoy.toISOString().split('T')[0],
      filas: [],
      resumen: {},
      estilistaNombre: '',
      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  mounted() {
    this.cargar()
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

    formatoFecha(fecha) {
      if (!fecha) return ''
      return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' })
    },

    horaCorta(hora) {
      if (!hora) return ''
      const [h, m] = String(hora).split(':').map(Number)
      const d = new Date(); d.setHours(h, m)
      return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true })
    },

    async cargar() {
      if (this.desde > this.hasta) {
        this.mostrarToast('warning', 'Rango inválido', 'La fecha "Desde" no puede ser posterior a "Hasta".')
        return
      }
      try {
        const res = await getMiHistorial(this.desde, this.hasta)
        this.filas = res.data.filas
        this.resumen = res.data.resumen
        this.estilistaNombre = res.data.estilista
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudo cargar tu historial.')
      }
    },

    descargarInforme() {
      const ok = historialPdf({
        estilista: this.estilistaNombre,
        desde: this.desde,
        hasta: this.hasta,
        filas: this.filas,
        resumen: this.resumen
      })
      if (!ok) this.mostrarToast('warning', 'Permite las ventanas emergentes', 'El navegador bloqueó la ventana del informe.')
    }
  }
}
</script>

<style scoped>
.hist-page { display: flex; flex-direction: column; gap: 20px; }

.page-header {
  display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; flex-wrap: wrap;
}
.page-header h1 { margin: 0; font-size: 30px; font-weight: 700; color: #173221; }
.page-header p { margin: 6px 0 0; font-size: 15px; color: #5f6f66; }

.btn-informe {
  padding: 11px 18px; border-radius: 12px; border: none; background: #014421; color: #fff;
  font-size: 13px; font-weight: 700; cursor: pointer; font-family: inherit; transition: all 0.2s ease;
}
.btn-informe:hover:not(:disabled) { background: #1f6a34; transform: translateY(-1px); }
.btn-informe:disabled { opacity: 0.45; cursor: not-allowed; }

.filtros-card {
  background: #ffffff; border: 1px solid #d9e8db; border-radius: 18px; padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
  display: flex; gap: 16px; flex-wrap: wrap; align-items: flex-end;
}
.filtro { display: flex; flex-direction: column; gap: 6px; flex: 1; min-width: 170px; }
.filtro label { font-size: 12px; font-weight: 700; color: #4a7c59; text-transform: uppercase; letter-spacing: 0.6px; }
.filtro input {
  border: 1px solid #d5dfd4; background: #fff; border-radius: 12px; padding: 11px 14px;
  font-size: 14px; color: #173221; outline: none; font-family: inherit; box-sizing: border-box;
}
.filtro input:focus { border-color: #014421; box-shadow: 0 0 0 4px rgba(1, 68, 33, 0.08); }

.resumen-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
.resumen-card {
  background: #ffffff; border: 1px solid #d9e8db; border-radius: 16px; padding: 16px 18px;
  display: flex; flex-direction: column; gap: 6px; box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}
.resumen-card.destacada { background: #f0f7f1; border-color: #bfe3c8; }
.rc-label { font-size: 12px; color: #6b7a72; font-weight: 600; }
.rc-valor { font-size: 22px; font-weight: 800; color: #014421; }

.seccion-card {
  background: #ffffff; border: 1px solid #d9e8db; border-radius: 18px; padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.vacio {
  padding: 28px; text-align: center; color: #8a9b8f; font-style: italic; font-size: 14px;
  background: #f9fcf8; border: 1px dashed #d9e8db; border-radius: 12px;
}

.tabla-wrap { overflow-x: auto; }
.tabla { width: 100%; min-width: 880px; border-collapse: collapse; font-size: 13.5px; color: #173221; }
.tabla th {
  background: #f0f7f1; color: #4a7c59; padding: 10px 14px; font-weight: 700;
  text-transform: uppercase; font-size: 10px; letter-spacing: 1.2px; text-align: left; white-space: nowrap;
}
.tabla td { padding: 12px 14px; border-top: 1px solid #edf2ee; vertical-align: middle; }
.tabla tbody tr:hover { background: #f6fbf7; }
.td-hora { font-weight: 700; color: #014421; white-space: nowrap; }
.td-bold { font-weight: 600; }
.td-valor { font-weight: 700; color: #014421; white-space: nowrap; }

.badge {
  font-size: 10.5px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.6px;
  padding: 4px 10px; border-radius: 999px; white-space: nowrap;
}
.badge-pagado { background: #e7f4ea; color: #1d7a3a; border: 1px solid #bfe3c8; }
.badge-pendiente { background: #fff5e6; color: #b45309; border: 1px solid #f3dcb0; }

@media (max-width: 900px) { .resumen-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .seccion-card { padding: 18px 14px; }
  .resumen-grid { grid-template-columns: 1fr 1fr; }
  .filtro { min-width: 100%; }
}
</style>

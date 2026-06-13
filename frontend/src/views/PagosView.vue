<template>
  <section class="pagos-page">

    <div class="page-header">
      <div>
        <h1>Pagos</h1>
        <p>Cobra citas finalizadas y consulta los pagos del día.</p>
      </div>
      <div class="header-acciones">
        <input type="date" v-model="fecha" class="fecha-input" @change="cargarTodo" />
        <button class="btn-informe" :disabled="pagosCompletados.length === 0" @click="descargarInforme">
          Descargar informe PDF
        </button>
      </div>
    </div>

    <!-- ═══ POR COBRAR ═══ -->
    <div class="seccion-card">
      <h2>Citas por cobrar</h2>

      <div v-if="porCobrar.length === 0" class="vacio">
        No hay citas finalizadas pendientes de pago en esta fecha.
      </div>

      <div v-else class="tabla-wrap">
        <table class="tabla">
          <thead>
            <tr>
              <th>Hora</th>
              <th>Cliente</th>
              <th>Servicio</th>
              <th>Estilista</th>
              <th>Valor</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in porCobrar" :key="c.idCita">
              <td class="td-hora">{{ horaCorta(c.horaInicio) }}</td>
              <td class="td-bold">{{ c.cliente }}</td>
              <td>{{ c.servicio }}</td>
              <td>{{ c.empleado }}</td>
              <td class="td-valor">{{ formatoMoneda(c.valor) }}</td>
              <td>
                <button class="btn-cobrar" @click="abrirCobro(c)">Registrar pago</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ═══ PAGOS DEL DÍA ═══ -->
    <div class="seccion-card">
      <div class="seccion-head">
        <h2>Pagos del día</h2>
        <span class="total-dia">Total válido: <strong>{{ formatoMoneda(totalValido) }}</strong></span>
      </div>

      <div v-if="pagos.length === 0" class="vacio">
        No hay pagos registrados en esta fecha.
      </div>

      <div v-else class="tabla-wrap">
        <table class="tabla">
          <thead>
            <tr>
              <th>Hora</th>
              <th>Cliente</th>
              <th>Estilista</th>
              <th>Servicio</th>
              <th>Medio</th>
              <th>Valor</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in pagos" :key="p.idPago" :class="{ anulado: p.estadoPago === 'anulado' }">
              <td class="td-hora">{{ horaCorta(p.hora) }}</td>
              <td class="td-bold">{{ p.cliente }}</td>
              <td>{{ p.empleado }}</td>
              <td>{{ p.servicio }}</td>
              <td class="capitalize">{{ p.metodoPago }}</td>
              <td class="td-valor">{{ formatoMoneda(p.valor) }}</td>
              <td>
                <span class="badge" :class="`badge-${p.estadoPago}`">
                  {{ p.estadoPago === 'completado' ? 'Completado' : 'Anulado' }}
                </span>
              </td>
              <td>
                <button
                  v-if="p.estadoPago === 'completado'"
                  class="btn-anular"
                  @click="pedirAnular(p)"
                >
                  Anular
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ═══ MODAL COBRAR ═══ -->
    <div v-if="cobro.visible" class="modal-overlay" @click.self="cerrarCobro">
      <div class="modal-card">

        <div class="modal-header">
          <h2>Registrar pago</h2>
          <button class="close-btn" @click="cerrarCobro">✕</button>
        </div>

        <div class="cobro-resumen">
          <p><strong>Cliente:</strong> {{ cobro.cita?.cliente }}</p>
          <p><strong>Servicio:</strong> {{ cobro.cita?.servicio }}</p>
          <p><strong>Estilista:</strong> {{ cobro.cita?.empleado }}</p>
          <div class="cobro-total">
            <span>Total a cobrar</span>
            <strong>{{ formatoMoneda(cobro.cita?.valor) }}</strong>
          </div>
        </div>

        <div class="form-group">
          <label>Medio de pago</label>
          <div class="medios">
            <button
              v-for="m in medios"
              :key="m.valor"
              type="button"
              class="medio-chip"
              :class="{ activo: cobro.medio === m.valor }"
              @click="cobro.medio = m.valor"
            >
              {{ m.icono }} {{ m.label }}
            </button>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-secundario" @click="cerrarCobro">Cancelar</button>
          <button
            class="btn-primario"
            :disabled="!cobro.medio || cobro.guardando"
            @click="confirmarCobro"
          >
            {{ cobro.guardando ? 'Registrando...' : 'Confirmar pago' }}
          </button>
        </div>

      </div>
    </div>

    <!-- CONFIRMAR ANULACIÓN -->
    <AppConfirmModal
      :visible="anular.visible"
      title="Anular pago"
      :message="`¿Anular el pago de ${formatoMoneda(anular.pago?.valor)} de ${anular.pago?.cliente}? Quedará excluido del cierre de caja pero se conserva para auditoría.`"
      @confirm="ejecutarAnulacion"
      @cancel="anular = { visible: false, pago: null }"
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
import { getCitasPorCobrar, registrarPago, getPagosDelDia, anularPago } from '@/services/pagoService'
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {
  name: 'PagosView',

  components: { AppToast, AppConfirmModal },

  data() {
    return {
      fecha: new Date().toISOString().split('T')[0],
      porCobrar: [],
      pagos: [],

      medios: [
        { valor: 'efectivo', label: 'Efectivo', icono: '💵' },
        { valor: 'transferencia', label: 'Transferencia', icono: '🏦' },
        { valor: 'tarjeta', label: 'Tarjeta', icono: '💳' }
      ],

      cobro: { visible: false, cita: null, medio: '', guardando: false },
      anular: { visible: false, pago: null },

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    pagosCompletados() {
      return this.pagos.filter(p => p.estadoPago === 'completado')
    },

    totalValido() {
      return this.pagosCompletados.reduce((acc, p) => acc + Number(p.valor || 0), 0)
    }
  },

  mounted() {
    this.cargarTodo()
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

    horaCorta(hora) {
      if (!hora) return ''
      const [h, m] = String(hora).split(':').map(Number)
      const d = new Date()
      d.setHours(h, m)
      return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true })
    },

    async cargarTodo() {
      try {
        const [resCobrar, resPagos] = await Promise.all([
          getCitasPorCobrar(this.fecha),
          getPagosDelDia(this.fecha)
        ])
        this.porCobrar = resCobrar.data
        this.pagos = resPagos.data
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudieron cargar los pagos.')
      }
    },

    abrirCobro(cita) {
      this.cobro = { visible: true, cita, medio: '', guardando: false }
    },

    cerrarCobro() {
      this.cobro = { visible: false, cita: null, medio: '', guardando: false }
    },

    async confirmarCobro() {
      this.cobro.guardando = true
      try {
        await registrarPago(this.cobro.cita.idCita, this.cobro.medio)
        this.mostrarToast('success', 'Pago registrado', `Se cobró ${this.formatoMoneda(this.cobro.cita.valor)} (${this.cobro.medio}).`)
        useNotificacionesStore().agregar('success', 'Pago registrado', `${this.cobro.cita.cliente} · ${this.formatoMoneda(this.cobro.cita.valor)}`)
        this.cerrarCobro()
        await this.cargarTodo()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo registrar el pago.'
        this.mostrarToast('error', 'Error al cobrar', msg)
        this.cobro.guardando = false
      }
    },

    // INFORME PDF (vía impresión del navegador → Guardar como PDF)
    descargarInforme() {
      const pagos = this.pagosCompletados
      if (pagos.length === 0) return

      const fechaTitulo = new Date(this.fecha + 'T00:00:00').toLocaleDateString('es-CO', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
      })

      // Resumen por estilista
      const porEstilista = {}
      pagos.forEach(p => {
        const est = p.empleado || 'Sin asignar'
        if (!porEstilista[est]) porEstilista[est] = { cantidad: 0, total: 0 }
        porEstilista[est].cantidad++
        porEstilista[est].total += Number(p.valor || 0)
      })

      const filas = pagos.map(p => `
        <tr>
          <td>${this.horaCorta(p.hora)}</td>
          <td>${p.servicio || ''}</td>
          <td>${p.empleado || ''}</td>
          <td>${p.cliente || ''}</td>
          <td style="text-transform:capitalize">${p.metodoPago || ''}</td>
          <td class="num">${this.formatoMoneda(p.valor)}</td>
        </tr>`).join('')

      const filasEstilista = Object.entries(porEstilista).map(([est, d]) => `
        <tr>
          <td>${est}</td>
          <td class="num">${d.cantidad}</td>
          <td class="num">${this.formatoMoneda(d.total)}</td>
        </tr>`).join('')

      const total = this.formatoMoneda(this.totalValido)
      const generado = new Date().toLocaleString('es-CO')

      const html = `
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8">
          <title>Informe de servicios - NiceLook</title>
          <style>
            * { font-family: 'Segoe UI', Arial, sans-serif; box-sizing: border-box; }
            body { margin: 32px; color: #173221; }
            .head { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #014421; padding-bottom: 14px; margin-bottom: 20px; }
            .marca { font-size: 26px; font-weight: 800; color: #014421; letter-spacing: 0.5px; }
            .sub { font-size: 13px; color: #5f6f66; margin-top: 2px; }
            .meta { text-align: right; font-size: 12px; color: #5f6f66; }
            h2 { font-size: 15px; color: #014421; margin: 24px 0 8px; }
            table { width: 100%; border-collapse: collapse; font-size: 12.5px; }
            th { background: #f0f7f1; color: #2f5a3a; text-align: left; padding: 8px 10px; text-transform: uppercase; font-size: 10px; letter-spacing: 0.8px; border-bottom: 2px solid #d9e8db; }
            td { padding: 8px 10px; border-bottom: 1px solid #edf2ee; }
            .num { text-align: right; font-variant-numeric: tabular-nums; }
            tr:nth-child(even) td { background: #fafdfb; }
            .total-row { margin-top: 14px; display: flex; justify-content: flex-end; gap: 18px; align-items: baseline; }
            .total-row .lbl { font-size: 13px; color: #5f6f66; }
            .total-row .val { font-size: 22px; font-weight: 800; color: #014421; }
            .foot { margin-top: 30px; font-size: 10.5px; color: #8a9b8f; border-top: 1px solid #e8f0e9; padding-top: 10px; }
            @media print { body { margin: 12mm; } }
          </style>
        </head>
        <body>
          <div class="head">
            <div>
              <div class="marca">NiceLook</div>
              <div class="sub">Informe de servicios realizados</div>
            </div>
            <div class="meta">
              <div><strong>Fecha:</strong> ${fechaTitulo}</div>
              <div>Servicios cobrados: ${pagos.length}</div>
            </div>
          </div>

          <h2>Detalle de servicios</h2>
          <table>
            <thead>
              <tr><th>Hora</th><th>Servicio</th><th>Estilista</th><th>Cliente</th><th>Medio</th><th class="num">Precio cobrado</th></tr>
            </thead>
            <tbody>${filas}</tbody>
          </table>

          <div class="total-row">
            <span class="lbl">Total cobrado en el día</span>
            <span class="val">${total}</span>
          </div>

          <h2>Resumen por estilista</h2>
          <table>
            <thead>
              <tr><th>Estilista</th><th class="num">Servicios</th><th class="num">Total generado</th></tr>
            </thead>
            <tbody>${filasEstilista}</tbody>
          </table>

          <div class="foot">
            Documento generado por el sistema NiceLook el ${generado}. Incluye únicamente pagos completados (sin anulados).
          </div>
        </body>
        </html>`

      const win = window.open('', '_blank')
      if (!win) {
        this.mostrarToast('warning', 'Permite las ventanas emergentes', 'El navegador bloqueó la ventana del informe.')
        return
      }
      win.document.write(html)
      win.document.close()
      win.focus()
      // Pequeña espera para que renderice antes de abrir el diálogo de impresión
      setTimeout(() => win.print(), 350)
    },

    pedirAnular(pago) {
      this.anular = { visible: true, pago }
    },

    async ejecutarAnulacion() {
      const pago = this.anular.pago
      this.anular = { visible: false, pago: null }
      try {
        await anularPago(pago.idPago)
        this.mostrarToast('success', 'Pago anulado', 'El pago quedó excluido del cierre de caja.')
        useNotificacionesStore().agregar('warning', 'Pago anulado', `${pago.cliente} · ${this.formatoMoneda(pago.valor)}`)
        await this.cargarTodo()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo anular el pago.'
        this.mostrarToast('error', 'Error al anular', msg)
      }
    }
  }
}
</script>

<style scoped>
.pagos-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
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

.header-acciones {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.fecha-input {
  padding: 11px 14px;
  border: 1px solid #d7e2da;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: #173221;
  background: #ffffff;
  outline: none;
  cursor: pointer;
}

.fecha-input:focus {
  border-color: #004518;
  box-shadow: 0 0 0 4px rgba(0, 69, 24, 0.08);
}

.btn-informe {
  padding: 11px 18px;
  border-radius: 12px;
  border: none;
  background: #014421;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
}

.btn-informe:hover:not(:disabled) { background: #1f6a34; transform: translateY(-1px); }
.btn-informe:disabled { opacity: 0.45; cursor: not-allowed; }

/* SECCIONES */
.seccion-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.seccion-card h2 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #173221;
}

.seccion-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.seccion-head h2 { margin: 0 0 16px; }

.total-dia {
  font-size: 13px;
  color: #4f5d52;
  margin-bottom: 16px;
}

.total-dia strong {
  color: #014421;
  font-size: 16px;
}

.vacio {
  padding: 28px;
  text-align: center;
  color: #8a9b8f;
  font-style: italic;
  font-size: 14px;
  background: #f9fcf8;
  border: 1px dashed #d9e8db;
  border-radius: 12px;
}

/* TABLAS */
.tabla-wrap { overflow-x: auto; }

.tabla {
  width: 100%;
  min-width: 820px;
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

.tabla tbody tr:hover { background: #f6fbf7; }

.tabla tbody tr.anulado { opacity: 0.55; }

.td-hora { font-weight: 700; color: #014421; white-space: nowrap; }
.td-bold { font-weight: 600; }
.td-valor { font-weight: 700; color: #014421; white-space: nowrap; }
.capitalize { text-transform: capitalize; }

.badge {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  padding: 4px 10px;
  border-radius: 999px;
  white-space: nowrap;
}

.badge-completado { background: #e7f4ea; color: #1d7a3a; border: 1px solid #bfe3c8; }
.badge-anulado    { background: #fdecec; color: #b42318; border: 1px solid #f3c2bd; }

.btn-cobrar {
  background: #014421;
  color: white;
  border: none;
  padding: 9px 14px;
  border-radius: 11px;
  font-size: 12.5px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-cobrar:hover { background: #1f6a34; }

.btn-anular {
  background: #fdecec;
  color: #b42318;
  border: 1px solid #f3c2bd;
  padding: 8px 14px;
  border-radius: 11px;
  font-size: 12.5px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
}

.btn-anular:hover { background: #fad6d3; }

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
  max-width: 430px;
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
  margin-bottom: 18px;
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
}

.close-btn:hover { background: #f0f7f1; color: #004518; }

.cobro-resumen {
  background: #ffffff;
  border: 1px solid #e8f0e9;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 18px;
}

.cobro-resumen p {
  margin: 4px 0;
  font-size: 13.5px;
  color: #2f4338;
}

.cobro-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e8f0e9;
  font-size: 13px;
  color: #4f5d52;
}

.cobro-total strong {
  font-size: 20px;
  color: #014421;
}

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

.medios {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.medio-chip {
  flex: 1;
  min-width: 110px;
  padding: 12px 8px;
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

.medio-chip:hover { border-color: #004518; background: #f0f7f1; }

.medio-chip.activo {
  background: #004518;
  border-color: #004518;
  color: #ffffff;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .seccion-card { padding: 18px 14px; }
  .modal-card { padding: 22px 16px; }
  .form-actions { flex-direction: column-reverse; }
  .btn-primario, .btn-secundario { width: 100%; }
}
</style>

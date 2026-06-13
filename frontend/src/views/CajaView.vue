<template>
  <section class="caja-page">

    <div class="page-header">
      <div>
        <h1>Caja</h1>
        <p>Registra la base con la que inicia el día y consulta el cierre.</p>
      </div>
    </div>

    <div class="caja-grid">

      <!-- ═══ REGISTRO DE BASE ═══ -->
      <div class="caja-card">

        <h2>Base del día</h2>

        <template v-if="baseRegistrada">
          <div class="base-registrada">
            <span class="base-check">✓</span>
            <div>
              <p class="base-monto">{{ formatoMoneda(baseRegistrada.montoBase) }}</p>
              <p class="base-fecha">Registrada el {{ fechaHoyTexto }}</p>
            </div>
          </div>
          <p class="base-nota">
            La base de hoy ya fue registrada. Solo se permite un registro por día.
          </p>
        </template>

        <template v-else>
          <div class="form-group">
            <label for="monto-base">Cantidad de dinero inicial</label>
            <div class="input-moneda">
              <span class="prefijo">$</span>
              <input
                id="monto-base"
                v-model="montoInput"
                type="text"
                inputmode="numeric"
                maxlength="10"
                placeholder="Ej. 200000"
                @input="montoInput = montoInput.replace(/[^0-9]/g, '')"
                @keyup.enter="pedirConfirmacion"
              />
            </div>
            <small class="hint">Solo números, máximo 8 dígitos.</small>
          </div>

          <button class="btn-registrar" :disabled="!montoInput" @click="pedirConfirmacion">
            Registrar base
          </button>
        </template>

      </div>

      <!-- ═══ CIERRE DEL DÍA ═══ -->
      <div class="caja-card">

        <h2>Cierre del día</h2>

        <div class="cierre-rows">
          <div class="cierre-row">
            <span class="cierre-label">Base inicial</span>
            <span class="cierre-valor">{{ formatoMoneda(cierre.baseInicial) }}</span>
          </div>
          <div class="cierre-row">
            <span class="cierre-label">Ingresos del día</span>
            <span class="cierre-valor ingresos">+ {{ formatoMoneda(cierre.totalIngresos) }}</span>
          </div>
          <div class="cierre-divider"></div>
          <div class="cierre-row total">
            <span class="cierre-label">Total esperado en caja</span>
            <span class="cierre-valor">{{ formatoMoneda(cierre.totalEsperado) }}</span>
          </div>
        </div>

        <p class="cierre-nota">
          Los ingresos incluyen únicamente pagos completados (sin anulados), separados de la base inicial.
        </p>

        <button class="btn-refrescar" @click="cargarCierre">Actualizar cierre</button>

      </div>

    </div>

    <!-- CONFIRMACIÓN -->
    <AppConfirmModal
      :visible="confirmVisible"
      title="Confirmar base del día"
      :message="`¿La cantidad ingresada es correcta? Se registrará ${formatoMoneda(montoInput)} como base de hoy.`"
      @confirm="registrar"
      @cancel="confirmVisible = false"
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
import { registrarBase, getBaseHoy, getCierre } from '@/services/cajaService'
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {
  name: 'CajaView',

  components: { AppToast, AppConfirmModal },

  data() {
    return {
      montoInput: '',
      baseRegistrada: null,
      cierre: { baseInicial: 0, totalIngresos: 0, totalEsperado: 0 },
      confirmVisible: false,
      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    fechaHoyTexto() {
      return new Date().toLocaleDateString('es-CO', {
        weekday: 'long', day: 'numeric', month: 'long'
      })
    }
  },

  mounted() {
    this.cargarBase()
    this.cargarCierre()
  },

  methods: {

    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3500)
    },

    formatoMoneda(valor) {
      const n = Number(valor || 0)
      return n.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
    },

    async cargarBase() {
      try {
        const res = await getBaseHoy()
        this.baseRegistrada = res.data
      } catch {
        this.baseRegistrada = null
      }
    },

    async cargarCierre() {
      try {
        const res = await getCierre()
        this.cierre = res.data
      } catch (e) {
        console.error(e)
      }
    },

    pedirConfirmacion() {
      if (!this.montoInput) return
      if (this.montoInput.length > 8) {
        this.mostrarToast('warning', 'Monto inválido', 'El monto no puede superar 8 dígitos.')
        return
      }
      this.confirmVisible = true
    },

    async registrar() {
      this.confirmVisible = false
      try {
        await registrarBase(this.montoInput)
        this.mostrarToast('success', 'Base registrada', 'El registro de la base diaria fue exitoso.')
        useNotificacionesStore().agregar('success', 'Base diaria', `Base de ${this.formatoMoneda(this.montoInput)} registrada`)
        this.montoInput = ''
        await this.cargarBase()
        await this.cargarCierre()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo registrar la base.'
        this.mostrarToast('error', 'Error al registrar', msg)
      }
    }
  }
}
</script>

<style scoped>
.caja-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.caja-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  max-width: 900px;
}

.caja-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 26px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.caja-card h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #173221;
}

/* REGISTRO */

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 13px;
  font-weight: 700;
  color: #264434;
}

.input-moneda {
  display: flex;
  align-items: center;
  border: 1px solid #d5dfd4;
  border-radius: 14px;
  background: #ffffff;
  overflow: hidden;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input-moneda:focus-within {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.prefijo {
  padding: 0 4px 0 16px;
  color: #4a7c59;
  font-weight: 700;
  font-size: 15px;
}

.input-moneda input {
  flex: 1;
  border: none;
  outline: none;
  padding: 13px 16px 13px 6px;
  font-size: 16px;
  font-weight: 600;
  color: #173221;
  font-family: inherit;
}

.hint {
  font-size: 12px;
  color: #8a9b8f;
}

.btn-registrar {
  background: #014421;
  color: white;
  border: none;
  padding: 13px 20px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.22s ease;
  box-shadow: 0 8px 20px rgba(1, 68, 33, 0.2);
}

.btn-registrar:hover:not(:disabled) {
  background: #1f6a34;
  transform: translateY(-1px);
}

.btn-registrar:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.base-registrada {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #f0f7f1;
  border: 1px solid #d1e8d4;
  border-radius: 14px;
  padding: 16px;
}

.base-check {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #014421;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}

.base-monto {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #014421;
}

.base-fecha {
  margin: 2px 0 0;
  font-size: 13px;
  color: #5f6f66;
  text-transform: capitalize;
}

.base-nota {
  margin: 0;
  font-size: 13px;
  color: #8a9b8f;
}

/* CIERRE */

.cierre-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cierre-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cierre-label {
  font-size: 14px;
  color: #4f5d52;
  font-weight: 600;
}

.cierre-valor {
  font-size: 15px;
  font-weight: 700;
  color: #173221;
  font-variant-numeric: tabular-nums;
}

.cierre-valor.ingresos {
  color: #1d7a3a;
}

.cierre-divider {
  height: 1px;
  background: #e8f0e9;
}

.cierre-row.total .cierre-label {
  font-weight: 700;
  color: #173221;
}

.cierre-row.total .cierre-valor {
  font-size: 20px;
  color: #014421;
}

.cierre-nota {
  margin: 0;
  font-size: 12.5px;
  color: #8a9b8f;
  line-height: 1.5;
}

.btn-refrescar {
  background: #eef3ef;
  color: #2f4338;
  border: 1px solid #d7e3d6;
  padding: 11px 18px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.2s;
  align-self: flex-start;
}

.btn-refrescar:hover {
  background: #dde7df;
}

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .caja-card { padding: 20px 16px; }
}
</style>

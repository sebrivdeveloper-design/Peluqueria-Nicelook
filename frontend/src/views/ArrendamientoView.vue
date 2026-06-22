<template>
  <section class="arr-page">

    <div class="page-header">
      <div>
        <h1>Arrendamiento</h1>
        <p>Define cuánto retiene el salón por cada servicio realizado por los estilistas.</p>
      </div>
    </div>

    <!-- ═══ VALOR POR DEFECTO ═══ -->
    <div class="seccion-card">
      <h2>Valor por defecto del salón</h2>
      <p class="hint">
        Se aplica a cualquier estilista o servicio que no tenga una tarifa específica configurada.
      </p>

      <div class="default-row">
        <div class="form-group">
          <label for="valorDefault">Valor de arrendamiento</label>
          <input
            id="valorDefault"
            v-model="valorDefault"
            type="number"
            min="0"
            placeholder="Ej. 52000"
          />
        </div>
        <button class="btn-primario" :disabled="guardandoDefault" @click="guardarDefault">
          {{ guardandoDefault ? 'Guardando...' : 'Guardar valor por defecto' }}
        </button>
      </div>
    </div>

    <!-- ═══ TARIFAS ESPECÍFICAS ═══ -->
    <div class="seccion-card">
      <div class="seccion-head">
        <h2>Tarifas específicas</h2>
        <button class="btn-secundario" @click="abrirForm()">+ Agregar tarifa</button>
      </div>
      <p class="hint">
        Sobrescribe el valor por defecto para un estilista en general, o para una
        combinación de estilista + servicio (más específico).
      </p>

      <div v-if="especificas.length === 0" class="vacio">
        Aún no hay tarifas específicas. Se usa el valor por defecto para todos.
      </div>

      <div v-else class="tabla-wrap">
        <table class="tabla">
          <thead>
            <tr>
              <th>Estilista</th>
              <th>Servicio</th>
              <th>Valor arrendamiento</th>
              <th>Actualizado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in especificas" :key="a.idArrendamiento">
              <td class="td-bold">{{ a.estilista }}</td>
              <td>
                <span v-if="a.servicio">{{ a.servicio }}</span>
                <span v-else class="badge-todos">Todos los servicios</span>
              </td>
              <td class="td-valor">{{ formatoMoneda(a.valor) }}</td>
              <td class="td-fecha">{{ formatoFecha(a.fechaActualizacion) }}</td>
              <td class="td-acciones">
                <button class="btn-mini" @click="abrirForm(a)">Editar</button>
                <button class="btn-mini btn-danger" @click="pedirEliminar(a)">Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ═══ MODAL TARIFA ═══ -->
    <div v-if="form.visible" class="modal-overlay" @click.self="cerrarForm">
      <div class="modal-card">
        <div class="modal-header">
          <h2>{{ form.editId ? 'Editar tarifa' : 'Nueva tarifa' }}</h2>
          <button class="close-btn" @click="cerrarForm">✕</button>
        </div>

        <div class="form-group">
          <label>Estilista</label>
          <select v-model="form.idEmpleado" :disabled="!!form.editId">
            <option disabled value="">Selecciona un estilista</option>
            <option v-for="e in estilistas" :key="e.idEmpleado" :value="e.idEmpleado">
              {{ e.usuario?.nombreCompleto }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Servicio</label>
          <select v-model="form.idServicio" :disabled="!!form.editId">
            <option value="">Todos los servicios (tarifa general del estilista)</option>
            <option v-for="s in servicios" :key="s.idServicio" :value="s.idServicio">
              {{ s.nombreServicio }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Valor de arrendamiento</label>
          <input v-model="form.valor" type="number" min="0" placeholder="Ej. 52000" />
        </div>

        <div class="form-actions">
          <button class="btn-secundario" @click="cerrarForm">Cancelar</button>
          <button class="btn-primario" :disabled="form.guardando" @click="guardarTarifa">
            {{ form.guardando ? 'Guardando...' : 'Guardar' }}
          </button>
        </div>
      </div>
    </div>

    <AppConfirmModal
      :visible="eliminar.visible"
      title="Eliminar tarifa"
      :message="`¿Eliminar la tarifa de arrendamiento de ${eliminar.item?.estilista}? Volverá a aplicarse el valor por defecto.`"
      @confirm="ejecutarEliminar"
      @cancel="eliminar = { visible: false, item: null }"
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
import {
  getArrendamientos,
  getArrendamientoDefault,
  guardarArrendamientoDefault,
  guardarArrendamiento,
  eliminarArrendamiento
} from '@/services/arrendamientoService'
import { getEmpleados } from '@/services/empleadoService'
import servicioService from '@/services/servicioService'
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {
  name: 'ArrendamientoView',

  components: { AppToast, AppConfirmModal },

  data() {
    return {
      lista: [],
      estilistas: [],
      servicios: [],

      valorDefault: '',
      guardandoDefault: false,

      form: { visible: false, editId: null, idEmpleado: '', idServicio: '', valor: '', guardando: false },
      eliminar: { visible: false, item: null },

      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    // Solo las tarifas específicas (el valor por defecto se gestiona arriba)
    especificas() {
      return this.lista.filter(a => a.tipo !== 'default')
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

    formatoFecha(fecha) {
      if (!fecha) return '—'
      return new Date(fecha).toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' })
    },

    async cargarTodo() {
      try {
        const [resLista, resDefault, resEmp, resServ] = await Promise.all([
          getArrendamientos(),
          getArrendamientoDefault(),
          getEmpleados(),
          servicioService.getServicios()
        ])
        this.lista = resLista.data
        this.valorDefault = resDefault.data.valor ?? ''
        // Solo estilistas (rol EMPLEADO), activos
        this.estilistas = resEmp.data.filter(e => e.usuario?.rol?.nombreRol === 'EMPLEADO')
        this.servicios = resServ.data
      } catch (e) {
        console.error(e)
        this.mostrarToast('error', 'Error', 'No se pudieron cargar las tarifas de arrendamiento.')
      }
    },

    async guardarDefault() {
      if (this.valorDefault === '' || Number(this.valorDefault) < 0) {
        this.mostrarToast('warning', 'Valor inválido', 'Indica un valor mayor o igual a 0.')
        return
      }
      this.guardandoDefault = true
      try {
        await guardarArrendamientoDefault(Number(this.valorDefault))
        this.mostrarToast('success', 'Valor por defecto guardado', `Arrendamiento base: ${this.formatoMoneda(this.valorDefault)}.`)
        useNotificacionesStore().agregar('success', 'Arrendamiento actualizado', `Valor por defecto: ${this.formatoMoneda(this.valorDefault)}`)
        await this.cargarTodo()
      } catch (error) {
        const msg = typeof error.response?.data === 'string' ? error.response.data : 'No se pudo guardar.'
        this.mostrarToast('error', 'Error', msg)
      } finally {
        this.guardandoDefault = false
      }
    },

    abrirForm(item = null) {
      if (item) {
        this.form = {
          visible: true,
          editId: item.idArrendamiento,
          idEmpleado: item.idEmpleado,
          idServicio: item.idServicio || '',
          valor: item.valor,
          guardando: false
        }
      } else {
        this.form = { visible: true, editId: null, idEmpleado: '', idServicio: '', valor: '', guardando: false }
      }
    },

    cerrarForm() {
      this.form = { visible: false, editId: null, idEmpleado: '', idServicio: '', valor: '', guardando: false }
    },

    async guardarTarifa() {
      if (!this.form.idEmpleado) {
        this.mostrarToast('warning', 'Falta estilista', 'Selecciona un estilista.')
        return
      }
      if (this.form.valor === '' || Number(this.form.valor) < 0) {
        this.mostrarToast('warning', 'Valor inválido', 'Indica un valor mayor o igual a 0.')
        return
      }
      this.form.guardando = true
      try {
        await guardarArrendamiento({
          idEmpleado: this.form.idEmpleado,
          idServicio: this.form.idServicio || null,
          valor: Number(this.form.valor)
        })
        this.mostrarToast('success', 'Tarifa guardada', 'La tarifa de arrendamiento fue guardada.')
        this.cerrarForm()
        await this.cargarTodo()
      } catch (error) {
        const msg = typeof error.response?.data === 'string' ? error.response.data : 'No se pudo guardar la tarifa.'
        this.mostrarToast('error', 'Error', msg)
        this.form.guardando = false
      }
    },

    pedirEliminar(item) {
      this.eliminar = { visible: true, item }
    },

    async ejecutarEliminar() {
      const item = this.eliminar.item
      this.eliminar = { visible: false, item: null }
      try {
        await eliminarArrendamiento(item.idArrendamiento)
        this.mostrarToast('success', 'Tarifa eliminada', 'Volverá a aplicarse el valor por defecto.')
        await this.cargarTodo()
      } catch (error) {
        const msg = typeof error.response?.data === 'string' ? error.response.data : 'No se pudo eliminar.'
        this.mostrarToast('error', 'Error', msg)
      }
    }
  }
}
</script>

<style scoped>
.arr-page { display: flex; flex-direction: column; gap: 20px; }

.page-header h1 { margin: 0; font-size: 30px; font-weight: 700; color: #173221; }
.page-header p { margin: 6px 0 0; font-size: 15px; color: #5f6f66; }

.seccion-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  padding: 22px 24px;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.06);
}

.seccion-card h2 { margin: 0 0 6px; font-size: 18px; font-weight: 700; color: #173221; }

.hint { margin: 0 0 16px; font-size: 13px; color: #6b7a72; }

.seccion-head {
  display: flex; justify-content: space-between; align-items: center;
  gap: 12px; flex-wrap: wrap;
}
.seccion-head h2 { margin: 0; }

.default-row {
  display: flex; align-items: flex-end; gap: 14px; flex-wrap: wrap;
}

.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-size: 13px; font-weight: 700; color: #264434; }

.form-group input, .form-group select {
  width: 100%;
  border: 1px solid #d5dfd4;
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 14px;
  color: #173221;
  outline: none;
  font-family: inherit;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.form-group input:focus, .form-group select:focus {
  border-color: #014421;
  box-shadow: 0 0 0 4px rgba(1, 68, 33, 0.08);
}

.vacio {
  padding: 24px; text-align: center; color: #8a9b8f;
  font-style: italic; font-size: 14px;
  background: #f9fcf8; border: 1px dashed #d9e8db; border-radius: 12px;
}

.tabla-wrap { overflow-x: auto; }
.tabla { width: 100%; min-width: 680px; border-collapse: collapse; font-size: 13.5px; color: #173221; }
.tabla th {
  background: #f0f7f1; color: #4a7c59; padding: 10px 14px; font-weight: 700;
  text-transform: uppercase; font-size: 10px; letter-spacing: 1.3px; text-align: left; white-space: nowrap;
}
.tabla td { padding: 12px 14px; border-top: 1px solid #edf2ee; vertical-align: middle; }
.tabla tbody tr:hover { background: #f6fbf7; }
.td-bold { font-weight: 600; }
.td-valor { font-weight: 700; color: #014421; white-space: nowrap; }
.td-fecha { color: #6b7a72; white-space: nowrap; }
.td-acciones { display: flex; gap: 8px; white-space: nowrap; }

.badge-todos {
  font-size: 11px; font-weight: 700; color: #4a7c59;
  background: #eef6ef; border: 1px solid #cfe3d2; border-radius: 999px; padding: 3px 10px;
}

.btn-primario {
  background: #014421; color: white; border: none;
  padding: 12px 18px; border-radius: 12px; font-size: 14px; font-weight: 700;
  cursor: pointer; font-family: inherit; transition: all 0.2s ease;
}
.btn-primario:hover:not(:disabled) { background: #1f6a34; transform: translateY(-1px); }
.btn-primario:disabled { opacity: 0.45; cursor: not-allowed; }

.btn-secundario {
  background: #eef3ef; color: #2f4338; border: 1px solid #d7e3d6;
  padding: 11px 16px; border-radius: 12px; font-size: 13px; font-weight: 700;
  cursor: pointer; font-family: inherit;
}
.btn-secundario:hover { background: #dde7df; }

.btn-mini {
  background: #eef3ef; color: #2f4338; border: 1px solid #d7e3d6;
  padding: 7px 12px; border-radius: 10px; font-size: 12px; font-weight: 700;
  cursor: pointer; font-family: inherit;
}
.btn-mini:hover { background: #dde7df; }
.btn-danger { background: #fdecec; color: #b42318; border-color: #f3c2bd; }
.btn-danger:hover { background: #fad6d3; }

.modal-overlay {
  position: fixed; inset: 0; background: rgba(10, 20, 14, 0.5); backdrop-filter: blur(3px);
  display: flex; align-items: center; justify-content: center; padding: 20px; z-index: 999;
}
.modal-card {
  width: 100%; max-width: 460px; background: #f9fcf8; border: 1px solid #d7e3d6;
  border-radius: 26px; padding: 28px; box-shadow: 0 24px 60px rgba(0, 0, 0, 0.16);
  display: flex; flex-direction: column; gap: 16px;
}
.modal-header { display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { margin: 0; font-size: 21px; font-weight: 700; color: #173221; }
.close-btn {
  width: 34px; height: 34px; background: #ffffff; border: 1px solid #d7e3d6;
  border-radius: 11px; font-size: 16px; color: #6b7a72; cursor: pointer;
}
.close-btn:hover { background: #f0f7f1; color: #004518; }
.form-actions { display: flex; justify-content: flex-end; gap: 10px; }

@media (max-width: 640px) {
  .page-header h1 { font-size: 26px; }
  .seccion-card { padding: 18px 14px; }
  .default-row { flex-direction: column; align-items: stretch; }
  .default-row .btn-primario { width: 100%; }
  .form-actions { flex-direction: column-reverse; }
  .form-actions .btn-primario, .form-actions .btn-secundario { width: 100%; }
}
</style>

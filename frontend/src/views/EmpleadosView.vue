<template>
  <section class="empleados-page">
    <div class="page-header">
      <div>
        <h1>Empleados</h1>
        <p>Consulta y administra la información del personal registrado en el sistema.</p>
      </div>
      <HeaderBar
        @crear="abrirModalCrear"
        textoBoton="Crear empleado"
      />
    </div>

    <div class="table-card">
      <div class="table-responsive">
        <table class="empleados-table">
          <thead>
            <tr>
              <th>Empleado</th>
              <th>Correo</th>
              <th>Documento</th>
              <th>Especialidad</th>
              <th>Salario</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="emp in filtrados" :key="emp.idEmpleado" :class="{ 'fila-inactiva': emp.estadoLaboral === 'inactivo' }">
              <td class="empleado-cell">
                <div class="avatar">
                  {{ obtenerInicial(emp.usuario?.nombreCompleto) }}
                </div>
                <div class="empleado-info">
                  <strong>{{ emp.usuario?.nombreCompleto }}</strong>
                  <span>{{ emp.usuario?.telefono || 'Sin teléfono' }}</span>
                </div>
              </td>

              <td>{{ emp.usuario?.correo || 'Sin correo' }}</td>
              <td>{{ emp.documento || 'Sin documento' }}</td>
              <td>
                <span class="badge">
                  {{ emp.especialidad || 'No definida' }}
                </span>
              </td>
              <td class="salary">{{ formatearMoneda(emp.salario) }}</td>
              <td>
                <span class="estado-badge" :class="emp.estadoLaboral === 'inactivo' ? 'badge-inactivo' : 'badge-activo'">
                  {{ emp.estadoLaboral === 'inactivo' ? 'Inactivo' : 'Activo' }}
                </span>
              </td>
              <td>
                <div class="acciones-cell">
                  <button class="btn-editar" @click="abrirModalEditar(emp)">
                    Editar
                  </button>
                  <button
                    class="btn-estado"
                    :class="emp.estadoLaboral === 'inactivo' ? 'activar' : 'desactivar'"
                    @click="pedirToggleEstado(emp)"
                  >
                    {{ emp.estadoLaboral === 'inactivo' ? 'Activar' : 'Desactivar' }}
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="filtrados.length === 0">
              <td colspan="7" class="empty-row">
                No se encontraron empleados con esa búsqueda.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <EmpleadoModal
      v-if="mostrarModal"
      :empleado="empleadoSeleccionado"
      @cerrar="cerrarModal"
      @actualizar="cargarEmpleados"
    />

    <AppConfirmModal
      :visible="confirmEstado.visible"
      :title="confirmEstado.emp?.estadoLaboral === 'inactivo' ? 'Activar empleado' : 'Desactivar empleado'"
      :message="confirmEstado.emp?.estadoLaboral === 'inactivo'
        ? `¿Reactivar a ${confirmEstado.emp?.usuario?.nombreCompleto}? Volverá a tener acceso al sistema.`
        : `¿Desactivar a ${confirmEstado.emp?.usuario?.nombreCompleto}? No podrá iniciar sesión y dejará de aparecer para agendar citas.`"
      @confirm="ejecutarToggleEstado"
      @cancel="confirmEstado = { visible: false, emp: null }"
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
import HeaderBar from '../components/HeaderBar.vue'
import { getEmpleados, desactivarEmpleado, activarEmpleado } from '../services/empleadoService'
import EmpleadoModal from '../components/EmpleadoModal.vue'
import AppToast from '../components/AppToast.vue'
import AppConfirmModal from '../components/AppConfirmModal.vue'
import { useNotificacionesStore } from '../stores/notificacionesStore'

export default {
  components: {
    EmpleadoModal,
    HeaderBar,
    AppToast,
    AppConfirmModal
  },

  inject: {
    adminSearch: { default: null }
  },

  data() {
    return {
      empleados: [],
      mostrarModal: false,
      empleadoSeleccionado: null,
      confirmEstado: { visible: false, emp: null },
      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    filtrados() {
      const texto = (this.adminSearch ?? '').toLowerCase().trim()
      return this.empleados.filter(e => {
        const nombre = e.usuario?.nombreCompleto?.toLowerCase() || ''
        const documento = e.documento?.toString() || ''
        return nombre.includes(texto) || documento.includes(texto)
      })
    }
  },

  mounted() {
    this.cargarEmpleados()
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3000)
    },

    async cargarEmpleados() {
      try {
        const res = await getEmpleados()
        this.empleados = res.data
      } catch (error) {
        console.error(error)
        this.mostrarToast('error', 'Error de carga', 'No se pudieron cargar los empleados. Revisa tu conexión.')
      }
    },

    abrirModalCrear() {
      this.empleadoSeleccionado = null
      this.mostrarModal = true
    },

    abrirModalEditar(emp) {
      this.empleadoSeleccionado = emp
      this.mostrarModal = true
    },

    cerrarModal() {
      this.mostrarModal = false
      this.empleadoSeleccionado = null
    },

    pedirToggleEstado(emp) {
      this.confirmEstado = { visible: true, emp }
    },

    async ejecutarToggleEstado() {
      const emp = this.confirmEstado.emp
      this.confirmEstado = { visible: false, emp: null }
      const desactivar = emp.estadoLaboral !== 'inactivo'
      try {
        if (desactivar) {
          await desactivarEmpleado(emp.idEmpleado)
        } else {
          await activarEmpleado(emp.idEmpleado)
        }
        const nombre = emp.usuario?.nombreCompleto || 'Empleado'
        this.mostrarToast(
          'success',
          desactivar ? 'Empleado desactivado' : 'Empleado activado',
          `"${nombre}" quedó ${desactivar ? 'inactivo' : 'activo'}.`
        )
        useNotificacionesStore().agregar(
          desactivar ? 'warning' : 'success',
          desactivar ? 'Empleado desactivado' : 'Empleado activado',
          nombre
        )
        await this.cargarEmpleados()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo cambiar el estado del empleado.'
        this.mostrarToast('error', 'Error', msg)
      }
    },

    obtenerInicial(nombre) {
      if (!nombre) return 'E'
      const partes = nombre.trim().split(' ')
      return partes.length > 1
        ? (partes[0][0] + partes[1][0]).toUpperCase()
        : partes[0][0].toUpperCase()
    },

    formatearMoneda(valor) {
      if (valor === null || valor === undefined || valor === '') return 'Sin salario'
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        maximumFractionDigits: 0
      }).format(valor)
    }
  }
}
</script>

<style scoped>
.empleados-page {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-header h1 {
  margin: 0;
  font-size: 38px;
  font-weight: 700;
  color: #173221;
  line-height: 1.1;
}

.page-header p {
  margin: 8px 0 0;
  font-size: 15px;
  color: #5f6f63;
  max-width: 620px;
}

.table-card {
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  box-shadow:
    0 2px 8px rgba(1, 68, 33, 0.06),
    0 1px 2px rgba(1, 68, 33, 0.04);
  overflow: hidden;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.empleados-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 940px;
}

.empleados-table thead {
  background: #f0f7f1;
}

.empleados-table th {
  text-align: left;
  padding: 14px 20px;
  font-size: 10px;
  font-weight: 700;
  color: #4a7c59;
  text-transform: uppercase;
  letter-spacing: 1.3px;
  white-space: nowrap;
  border-bottom: 1px solid #e8f0e9;
}

.empleados-table td {
  padding: 16px 20px;
  font-size: 14px;
  color: #173221;
  border-bottom: 1px solid #edf2ee;
  vertical-align: middle;
}

.empleados-table tbody tr {
  transition: background 0.15s ease;
}

.empleados-table tbody tr:hover {
  background: #f6fbf7;
}

.empleado-cell {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar {
  width: 44px;
  height: 44px;
  min-width: 44px;
  border-radius: 50%;
  background: #dfe9db;
  color: #004518;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.empleado-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.empleado-info strong {
  font-size: 15px;
  font-weight: 700;
  color: #173221;
}

.empleado-info span {
  font-size: 12px;
  color: #728376;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: #eef5ee;
  color: #35513b;
  font-size: 12px;
  font-weight: 600;
}

.salary {
  font-weight: 700;
  color: #004518;
}

.btn-editar {
  background: #eef5ee;
  border: 1px solid #c6d9c6;
  color: #2a4d32;
  padding: 8px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-editar:hover {
  background: #004518;
  color: #ffffff;
  border-color: #004518;
}

.acciones-cell {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.btn-estado {
  padding: 8px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.btn-estado.desactivar {
  background: #fdecec;
  color: #b42318;
  border: 1px solid #f3c2bd;
}
.btn-estado.desactivar:hover { background: #fad6d3; }

.btn-estado.activar {
  background: #004518;
  color: #ffffff;
  border: 1px solid #004518;
}
.btn-estado.activar:hover { background: #1f6a34; }

.estado-badge {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  padding: 4px 10px;
  border-radius: 999px;
  white-space: nowrap;
}

.badge-activo   { background: #e7f4ea; color: #1d7a3a; border: 1px solid #bfe3c8; }
.badge-inactivo { background: #fdecec; color: #b42318; border: 1px solid #f3c2bd; }

.fila-inactiva { opacity: 0.6; }

.empty-row {
  text-align: center;
  padding: 28px !important;
  color: #6a7b6d;
}

@media (max-width: 1024px) {
  .page-header h1 { font-size: 32px; }
}

@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  .page-header h1 { font-size: 28px; }
}
</style>

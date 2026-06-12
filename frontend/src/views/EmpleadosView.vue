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
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="emp in filtrados" :key="emp.idEmpleado">
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
                <button class="btn-editar" @click="abrirModalEditar(emp)">
                  ✏️ Editar
                </button>
              </td>
            </tr>

            <tr v-if="filtrados.length === 0">
              <td colspan="6" class="empty-row">
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
  </section>
</template>

<script>
import HeaderBar from '../components/HeaderBar.vue'
import { getEmpleados } from '../services/empleadoService'
import EmpleadoModal from '../components/EmpleadoModal.vue'

export default {
  components: {
    EmpleadoModal,
    HeaderBar
  },

  inject: {
    adminSearch: { default: null }
  },

  data() {
    return {
      empleados: [],
      mostrarModal: false,
      empleadoSeleccionado: null
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
    async cargarEmpleados() {
      try {
        const res = await getEmpleados()
        this.empleados = res.data
      } catch (error) {
        console.error(error)
        alert('Error cargando empleados')
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
  border: 1px solid #d9e4da;
  border-radius: 24px;
  box-shadow: 0 14px 32px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.empleados-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 960px;
}

.empleados-table thead {
  background: #f2f7f2;
}

.empleados-table th {
  text-align: left;
  padding: 18px 20px;
  font-size: 13px;
  font-weight: 700;
  color: #48604d;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  border-bottom: 1px solid #e0e8e1;
}

.empleados-table td {
  padding: 18px 20px;
  font-size: 14px;
  color: #173221;
  border-bottom: 1px solid #edf2ed;
  vertical-align: middle;
}

.empleados-table tbody tr:hover {
  background: #fafcf9;
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

.empty-row {
  text-align: center;
  padding: 28px !important;
  color: #6a7b6d;
}

@media (max-width: 1024px) {
  .page-header h1 { font-size: 32px; }
}

@media (max-width: 640px) {
  .page-header h1 { font-size: 28px; }
}
</style>

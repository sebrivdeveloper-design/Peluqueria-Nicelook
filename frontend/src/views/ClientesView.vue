<template>
  <section class="clientes-page">
    
    <div class="page-header">
      <div>
        <h1>Clientes</h1>
        <p>Consulta y registra los clientes del salón.</p>
      </div>
      <HeaderBar
        v-model:busqueda="textoBusqueda"
        :mostrarBoton="true"
        textoBoton="Registrar cliente"
        placeholder="Buscar cliente por nombre o documento..."
        @crear="abrirModalRegistro"
      />
    </div>

    <!-- FILTRO DE ESTADO -->
    <div class="filtros-estado">
      <button
        v-for="f in filtrosEstado"
        :key="f.valor"
        class="filtro-pill"
        :class="{ activo: filtroEstado === f.valor }"
        @click="filtroEstado = f.valor"
      >
        {{ f.label }}
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Cargando listado de clientes de la base de datos...</p>
    </div>

    <div v-else class="table-container">
      <table class="clientes-table">
        <thead>
          <tr>
            <th class="th-documento">ID Documento</th>
            <th class="th-nombre">Nombre Completo</th>
            <th class="th-telefono">Teléfono</th>
            <th class="th-correo">Correo</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filtrados.length === 0">
            <td colspan="6" class="empty-row">
              No se encontraron clientes que coincidan con la búsqueda.
            </td>
          </tr>
          <tr
            v-for="cliente in filtrados"
            :key="cliente.idCliente"
            class="table-row"
            :class="{ 'fila-inactiva': cliente.estado === 'inactivo' }"
          >
            <td class="col-documento">{{ cliente.documento }}</td>
            <td class="col-nombre">{{ cliente.nombreCompleto }}</td>
            <td class="col-telefono">{{ cliente.telefono || 'N/A' }}</td>
            <td class="col-correo">{{ cliente.correo || 'N/A' }}</td>
            <td>
              <span class="estado-badge" :class="cliente.estado === 'inactivo' ? 'badge-inactivo' : 'badge-activo'">
                {{ cliente.estado === 'inactivo' ? 'Inactivo' : 'Activo' }}
              </span>
            </td>
            <td class="col-acciones">
              <button class="btn-mini editar" @click="abrirModalEdicion(cliente)">Editar</button>
              <button
                class="btn-mini"
                :class="cliente.estado === 'inactivo' ? 'activar' : 'desactivar'"
                @click="pedirToggleEstado(cliente)"
              >
                {{ cliente.estado === 'inactivo' ? 'Activar' : 'Desactivar' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="mostrarModal" class="modal-overlay" @click.self="mostrarModal = false">
      <div class="modal-content">
        <h2>{{ clienteEditando ? 'Editar Cliente' : 'Registrar Nuevo Cliente' }}</h2>

        <div v-if="mensajeExito" class="alerta-exito">{{ mensajeExito }}</div>
        <div v-if="mensajeError" class="alerta-error">{{ mensajeError }}</div>

        <form @submit.prevent="guardarCliente" class="modal-form">
          <div class="form-group">
            <label>Nombre Completo:</label>
            <input type="text" v-model="nuevoCliente.nombreCompleto" maxlength="100" placeholder="Ej. Carlos Pérez" />
            <span v-if="errores.nombreCompleto" class="error-campo">{{ errores.nombreCompleto }}</span>
          </div>

          <div class="form-group">
            <label>Cédula / Documento:</label>
            <input type="text" v-model="nuevoCliente.documento" placeholder="Ej. 1109187376" />
            <span v-if="errores.documento" class="error-campo">{{ errores.documento }}</span>
          </div>

          <div class="form-group">
            <label>Correo Electrónico:</label>
            <input type="email" v-model="nuevoCliente.correo" maxlength="50" placeholder="carlos@gmail.com" />
            <span v-if="errores.correo" class="error-campo">{{ errores.correo }}</span>
          </div>

          <div class="form-group">
            <label>Teléfono:</label>
            <input type="text" v-model="nuevoCliente.telefono" maxlength="12" placeholder="315XXXXXXX" />
            <span v-if="errores.telefono" class="error-campo">{{ errores.telefono }}</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Género:</label>
              <select v-model="nuevoCliente.genero">
                <option value="masculino">Masculino</option>
                <option value="femenino">Femenino</option>
                <option value="otro">Otro</option>
              </select>
            </div>
            <div class="form-group">
              <label>Fecha Nacimiento:</label>
              <input type="date" v-model="nuevoCliente.fechaNacimiento" />
              <span v-if="errores.fechaNacimiento" class="error-campo">{{ errores.fechaNacimiento }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>Observaciones:</label>
            <textarea v-model="nuevoCliente.observaciones" rows="2" placeholder="Detalles estéticos o preferencias..."></textarea>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-cancelar" @click="mostrarModal = false">Cancelar</button>
            <button type="submit" class="btn-guardar">{{ clienteEditando ? 'Guardar cambios' : 'Registrar' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- CONFIRMAR CAMBIO DE ESTADO -->
    <AppConfirmModal
      :visible="confirmEstado.visible"
      :title="confirmEstado.cliente?.estado === 'inactivo' ? 'Activar cliente' : 'Desactivar cliente'"
      :message="confirmEstado.cliente?.estado === 'inactivo'
        ? `¿Reactivar a ${confirmEstado.cliente?.nombreCompleto}? Podrá iniciar sesión y reservar citas nuevamente.`
        : `¿Desactivar a ${confirmEstado.cliente?.nombreCompleto}? Se ocultará de las búsquedas activas y no podrá iniciar sesión, pero su información e historial se conservan.`"
      @confirm="ejecutarToggleEstado"
      @cancel="confirmEstado = { visible: false, cliente: null }"
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
import HeaderBar from '@/components/HeaderBar.vue'
import api from "@/services/clienteService"
import AppToast from '@/components/AppToast.vue'
import AppConfirmModal from '@/components/AppConfirmModal.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {
  name: 'ClientesView',
  components: { HeaderBar, AppToast, AppConfirmModal },
  data() {
    return {
      textoBusqueda: '',
      loading: true,
      mostrarModal: false,
      mensajeExito: '',
      mensajeError: '',
      errores: {},
      clientes: [],
      clienteEditando: null,
      filtroEstado: 'activos',
      filtrosEstado: [
        { valor: 'activos', label: 'Activos' },
        { valor: 'inactivos', label: 'Inactivos' },
        { valor: 'todos', label: 'Todos' }
      ],
      confirmEstado: { visible: false, cliente: null },
      toast: { visible: false, type: 'info', title: '', message: '' },
      nuevoCliente: {
        documento: '',
        nombreCompleto: '',
        correo: '',
        telefono: '',
        genero: 'masculino',
        fechaNacimiento: '',
        observaciones: ''
      }
    }
  },
  computed: {
    filtrados() {
      let lista = this.clientes

      if (this.filtroEstado === 'activos') {
        lista = lista.filter(c => c.estado !== 'inactivo')
      } else if (this.filtroEstado === 'inactivos') {
        lista = lista.filter(c => c.estado === 'inactivo')
      }

      if (!this.textoBusqueda) return lista
      const query = this.textoBusqueda.toLowerCase().trim()
      return lista.filter(c => {
        const nombre = (c.nombreCompleto || '').toLowerCase()
        const doc = (c.documento || '').toString()
        const mail = (c.correo || '').toLowerCase()
        return nombre.includes(query) || doc.includes(query) || mail.includes(query)
      })
    }
  },
  methods: {
    async cargarClientes() {
      this.loading = true
      try {
        const response = await api.getClientes()
        this.clientes = response.data
      } catch (error) {
        console.error("Error al cargar clientes:", error)
      } finally {
        this.loading = false
      }
    },

    abrirModalRegistro() {
      this.clienteEditando = null
      this.nuevoCliente = {
        documento: '',
        nombreCompleto: '',
        correo: '',
        telefono: '',
        genero: 'masculino',
        fechaNacimiento: '',
        observaciones: ''
      }
      this.errores = {}
      this.mensajeExito = ''
      this.mensajeError = ''
      this.mostrarModal = true
    },

    abrirModalEdicion(cliente) {
      this.clienteEditando = cliente
      this.nuevoCliente = {
        documento: cliente.documento || '',
        nombreCompleto: cliente.nombreCompleto || '',
        correo: cliente.correo || '',
        telefono: cliente.telefono || '',
        genero: cliente.genero || 'masculino',
        fechaNacimiento: cliente.fechaNacimiento || '',
        observaciones: cliente.observaciones || ''
      }
      this.errores = {}
      this.mensajeExito = ''
      this.mensajeError = ''
      this.mostrarModal = true
    },

    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3500)
    },

    pedirToggleEstado(cliente) {
      this.confirmEstado = { visible: true, cliente }
    },

    async ejecutarToggleEstado() {
      const cliente = this.confirmEstado.cliente
      this.confirmEstado = { visible: false, cliente: null }
      const desactivar = cliente.estado !== 'inactivo'
      try {
        if (desactivar) {
          await api.desactivarCliente(cliente.idCliente)
        } else {
          await api.activarCliente(cliente.idCliente)
        }
        this.mostrarToast(
          'success',
          desactivar ? 'Cliente desactivado' : 'Cliente activado',
          `"${cliente.nombreCompleto}" quedó ${desactivar ? 'inactivo' : 'activo'}.`
        )
        useNotificacionesStore().agregar(
          desactivar ? 'warning' : 'success',
          desactivar ? 'Cliente desactivado' : 'Cliente activado',
          cliente.nombreCompleto
        )
        await this.cargarClientes()
      } catch (error) {
        const msg = typeof error.response?.data === 'string'
          ? error.response.data : 'No se pudo cambiar el estado del cliente.'
        this.mostrarToast('error', 'Error', msg)
      }
    },

    validar() {
      const e = {}
      const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
      const soloNumeros = /^[0-9]+$/

      if (!this.nuevoCliente.nombreCompleto)
        e.nombreCompleto = 'El nombre es obligatorio.'
      else if (!soloLetras.test(this.nuevoCliente.nombreCompleto))
        e.nombreCompleto = 'El nombre solo debe contener letras.'
      else if (this.nuevoCliente.nombreCompleto.length > 100)
        e.nombreCompleto = 'El nombre no puede superar 100 caracteres.'

      if (!this.nuevoCliente.documento)
        e.documento = 'El documento es obligatorio.'

      if (!this.nuevoCliente.telefono)
        e.telefono = 'El teléfono es obligatorio.'
      else if (!soloNumeros.test(this.nuevoCliente.telefono))
        e.telefono = 'El teléfono solo debe contener números.'
      else if (this.nuevoCliente.telefono.length > 12)
        e.telefono = 'El teléfono no puede superar 12 caracteres.'

      if (!this.nuevoCliente.correo)
        e.correo = 'El correo es obligatorio.'
      else if (this.nuevoCliente.correo.length > 50)
        e.correo = 'El correo no puede superar 50 caracteres.'

      // La fecha de nacimiento solo es obligatoria al registrar
      if (!this.clienteEditando && !this.nuevoCliente.fechaNacimiento)
        e.fechaNacimiento = 'La fecha de nacimiento es obligatoria.'

      this.errores = e
      return Object.keys(e).length === 0
    },

    async guardarCliente() {
      if (!this.validar()) return

      try {
        if (this.clienteEditando) {
          await api.editarCliente(this.clienteEditando.idCliente, this.nuevoCliente)
          this.mensajeExito = '✓ Cliente actualizado exitosamente.'
          useNotificacionesStore().agregar('success', 'Cliente actualizado', this.nuevoCliente.nombreCompleto)
        } else {
          await api.registrarCliente(this.nuevoCliente)
          this.mensajeExito = '✓ Cliente registrado exitosamente.'
          useNotificacionesStore().agregar('success', 'Cliente registrado', this.nuevoCliente.nombreCompleto)
        }
        this.mensajeError = ''
        await this.cargarClientes()
        setTimeout(() => {
          this.mostrarModal = false
          this.mensajeExito = ''
        }, 2000)
      } catch (error) {
        if (error.response && (error.response.status === 409 || error.response.status === 400)) {
          this.mensajeError = error.response.data
        } else {
          this.mensajeError = this.clienteEditando
            ? 'Ocurrió un error al actualizar el cliente.'
            : 'Ocurrió un error al registrar el cliente.'
        }
      }
    }
  },
  mounted() {
    this.cargarClientes()
  }
}
</script>

<style scoped>
.clientes-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
}

.filtros-estado {
  display: flex;
  gap: 8px;
}

.filtro-pill {
  padding: 8px 16px;
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

.filtro-pill:hover { background: #f0f7f1; }

.filtro-pill.activo {
  background: #014421;
  border-color: #014421;
  color: #ffffff;
}

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

.col-acciones {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.btn-mini {
  padding: 7px 12px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.18s ease;
  white-space: nowrap;
}

.btn-mini.editar {
  background: #f0f7f1;
  color: #014421;
  border: 1px solid #cfe3d3;
}
.btn-mini.editar:hover { background: #e0efe3; }

.btn-mini.desactivar {
  background: #fdecec;
  color: #b42318;
  border: 1px solid #f3c2bd;
}
.btn-mini.desactivar:hover { background: #fad6d3; }

.btn-mini.activar {
  background: #014421;
  color: #ffffff;
  border: 1px solid #014421;
}
.btn-mini.activar:hover { background: #1f6a34; }

.table-container {
  width: 100%;
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 18px;
  box-shadow:
    0 2px 8px rgba(1, 68, 33, 0.06),
    0 1px 2px rgba(1, 68, 33, 0.04);
  overflow-x: auto;
}

.clientes-table {
  width: 100%;
  min-width: 640px;
  border-collapse: collapse;
  font-size: 14px;
  color: #173221;
}

.clientes-table th {
  background: #f0f7f1;
  color: #4a7c59;
  padding: 14px 20px;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 10px;
  letter-spacing: 1.3px;
  text-align: left;
  white-space: nowrap;
  border-bottom: 1px solid #e8f0e9;
}

.clientes-table td {
  padding: 16px 20px;
  border-top: 1px solid #edf2ee;
  vertical-align: middle;
}

.th-documento { width: 20%; }
.th-nombre    { width: 35%; }
.th-telefono  { width: 20%; }
.th-correo    { width: 25%; }

.table-row {
  transition: background-color 0.15s ease;
}

.table-row:hover {
  background-color: #f6fbf7;
}

.col-documento {
  font-weight: 700;
  color: #014421;
}

.col-nombre {
  font-weight: 600;
  color: #2c3e31;
}

.col-telefono, .col-correo {
  color: #4f5d52;
}

.empty-row {
  text-align: center;
  padding: 40px !important;
  color: #8a9b8f;
  font-style: italic;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  gap: 16px;
  color: #4f5d52;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #d1ded2;
  border-top-color: #004518;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 19, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  z-index: 1000;
}

.modal-content {
  background: #f7faf5;
  border: 1px solid #d7e3d6;
  padding: 28px;
  border-radius: 28px;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.14);
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h2 {
  color: #173221;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.1;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

.modal-form label {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e31;
}

.modal-form input,
.modal-form select,
.modal-form textarea {
  padding: 12px 16px;
  border: 1px solid #d5dfd4;
  background: #ffffff;
  border-radius: 14px;
  font-size: 14px;
  outline: none;
  color: #173221;
  font-family: inherit;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.modal-form input:focus,
.modal-form select:focus,
.modal-form textarea:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 15px;
}

.btn-cancelar {
  background: #eef3ea;
  border: 1px solid #d7e3d6;
  color: #35513b;
  padding: 12px 18px;
  border-radius: 14px;
  cursor: pointer;
  font-weight: 600;
  font-family: inherit;
  transition: background 0.2s;
}

.btn-cancelar:hover {
  background: #e5ede1;
}

.btn-guardar {
  background: #014421;
  color: white;
  border: none;
  padding: 12px 18px;
  border-radius: 14px;
  cursor: pointer;
  font-weight: 600;
  font-family: inherit;
  box-shadow: 0 8px 20px rgba(1, 68, 33, 0.2);
  transition: all 0.22s ease;
}

.btn-guardar:hover {
  background: #1f6a34;
  transform: translateY(-1px);
}

.alerta-exito {
  background: #e0f2e5;
  color: #1b5e20;
  padding: 12px 14px;
  border-radius: 12px;
  margin-bottom: 14px;
  font-weight: 600;
  font-size: 14px;
}

.alerta-error {
  background: #ffe5e5;
  color: #b42318;
  padding: 12px 14px;
  border-radius: 12px;
  margin-bottom: 14px;
  font-weight: 600;
  font-size: 14px;
}

.error-campo {
  color: #b42318;
  font-size: 12px;
  margin-top: 3px;
}

/* ── Responsive ── */
@media (max-width: 1024px) {
  .page-header h1 { font-size: 32px; }
}

@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .page-header h1 { font-size: 28px; }

  .modal-overlay {
    padding: 16px;
    align-items: flex-end;
  }

  .modal-content {
    max-width: 100%;
    padding: 22px 18px;
    border-radius: 24px 24px 18px 18px;
  }

  .form-row {
    flex-direction: column;
    gap: 14px;
  }

  .modal-actions {
    flex-direction: column;
  }

  .btn-cancelar,
  .btn-guardar {
    width: 100%;
  }
}
</style>
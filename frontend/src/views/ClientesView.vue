<template>
  <section class="clientes-page">
    
    <HeaderBar
      v-model:busqueda="textoBusqueda"
      :mostrarBoton="true"
      textoBoton="Registrar cliente"
      placeholder="Buscar cliente por nombre o documento..."
      @crear="abrirModalRegistro"
    />

    <div class="page-header">
      <h1>CLIENTES</h1>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Cargando listado de clientes de la base de datos...</p>
    </div>

    <div v-else class="table-container">
      <table class="clientes-table">
        <thead>
          <tr>
            <th class="text-center" style="width: 20%;">ID Documento</th>
            <th style="width: 35%;">Nombre Completo</th>
            <th style="width: 20%;">Teléfono</th>
            <th style="width: 25%;">Correo</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filtrados.length === 0">
            <td colspan="4" class="empty-row">
              No se encontraron clientes que coincidan con la búsqueda.
            </td>
          </tr>
          <tr
            v-for="cliente in filtrados"
            :key="cliente.documento"
            class="table-row"
          >
            <td class="col-documento">{{ cliente.documento }}</td>
            <td class="col-nombre">{{ cliente.nombreCompleto }}</td>
            <td class="col-telefono">{{ cliente.telefono || 'N/A' }}</td>
            <td class="col-correo">{{ cliente.correo || 'N/A' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="mostrarModal" class="modal-overlay" @click.self="mostrarModal = false">
      <div class="modal-content">
        <h2>Registrar Nuevo Cliente</h2>

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
            <button type="submit" class="btn-guardar">Registrar</button>
          </div>
        </form>
      </div>
    </div>

  </section>
</template>

<script>
import HeaderBar from '@/components/HeaderBar.vue'
import api from "@/services/clienteService"

export default {
  name: 'ClientesView',
  components: { HeaderBar },
  data() {
    return {
      textoBusqueda: '',
      loading: true,
      mostrarModal: false,
      mensajeExito: '',
      mensajeError: '',
      errores: {},
      clientes: [],
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
      if (!this.textoBusqueda) return this.clientes
      const query = this.textoBusqueda.toLowerCase().trim()
      return this.clientes.filter(c => {
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

      if (!this.nuevoCliente.fechaNacimiento)
        e.fechaNacimiento = 'La fecha de nacimiento es obligatoria.'

      this.errores = e
      return Object.keys(e).length === 0
    },

    async guardarCliente() {
      if (!this.validar()) return

      try {
        await api.registrarCliente(this.nuevoCliente)
        this.mensajeExito = '✓ Cliente registrado exitosamente.'
        this.mensajeError = ''
        await this.cargarClientes()
        setTimeout(() => {
          this.mostrarModal = false
          this.mensajeExito = ''
        }, 2000)
      } catch (error) {
        if (error.response && error.response.status === 409) {
          this.mensajeError = error.response.data
        } else {
          this.mensajeError = 'Ocurrió un error al registrar el cliente.'
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

.page-header h1 {
  font-size: 34px;
  font-weight: 700;
  color: #004518;
  text-align: center;
  margin: 10px 0 20px 0;
  letter-spacing: 0.5px;
}

.table-container {
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 4px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.clientes-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 15px;
  color: #173221;
}

.clientes-table th {
  background-color: #d1ded2;
  color: #1a3321;
  padding: 16px;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 14px;
  border: 1px solid #b8c7ba;
}

.clientes-table td {
  padding: 16px 20px;
  border: 1px solid #e3ece4;
  vertical-align: middle;
}

.table-row {
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background-color: #f4f8f5;
}

.col-documento {
  background-color: #7fa482;
  color: #ffffff;
  font-weight: 600;
  text-align: center;
}

.col-nombre {
  font-weight: 600;
  color: #2c3e31;
  text-transform: uppercase;
}

.col-telefono, .col-correo {
  color: #4f5d52;
}

.text-center {
  text-align: center;
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
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 500px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
}

.modal-content h2 {
  color: #004518;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 24px;
  border-bottom: 2px solid #f0f4f1;
  padding-bottom: 10px;
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
  padding: 10px 12px;
  border: 1px solid #d9e4da;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  color: #173221;
}

.modal-form input:focus,
.modal-form select:focus,
.modal-form textarea:focus {
  border-color: #004518;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 15px;
}

.btn-cancelar {
  background: #f1f5f2;
  border: none;
  color: #4f5d52;
  padding: 11px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.btn-guardar {
  background: #004518;
  color: white;
  border: none;
  padding: 11px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-guardar:hover {
  background: #145c43;
}

.alerta-exito {
  background: #d4edda;
  color: #155724;
  padding: 10px 14px;
  border-radius: 4px;
  margin-bottom: 14px;
  font-weight: 600;
}

.alerta-error {
  background: #f8d7da;
  color: #721c24;
  padding: 10px 14px;
  border-radius: 4px;
  margin-bottom: 14px;
  font-weight: 600;
}

.error-campo {
  color: #c0392b;
  font-size: 12px;
  margin-top: 3px;
}
</style>
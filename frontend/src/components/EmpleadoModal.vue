<template>
  <div class="overlay" @click.self="$emit('cerrar')">
    <div class="modal">
      <button class="cerrar" @click="$emit('cerrar')" aria-label="Cerrar modal">
        ×
      </button>

      <div class="modal-header">
        <h3>{{ empleado ? 'Editar empleado' : 'Registrar empleado' }}</h3>
        <p>{{ empleado ? 'Modifica la información del colaborador.' : 'Completa la información del colaborador para registrarlo en el sistema.' }}</p>
      </div>

      <div class="form-grid">
        <div class="form-group full">
          <label for="nombreCompleto">Nombre completo</label>
          <input
            id="nombreCompleto"
            v-model="form.nombreCompleto"
            placeholder="Nombre completo"
            :class="{ error: errors.nombreCompleto }"
            @input="errors.nombreCompleto = false"
          />
          <small v-if="errors.nombreCompleto" class="error-text">
            Este campo es obligatorio.
          </small>
        </div>

        <div class="form-group">
          <label for="correo">Correo</label>
          <input
            id="correo"
            v-model="form.correo"
            placeholder="Correo electrónico"
            :class="{ error: errors.correo }"
            @input="errors.correo = false"
          />
          <small v-if="errors.correo" class="error-text">
            Este campo es obligatorio.
          </small>
        </div>

        <div class="form-group">
          <label for="telefono">Teléfono</label>
          <input
            id="telefono"
            v-model="form.telefono"
            placeholder="Teléfono"
          />
        </div>

        <div class="form-group">
          <label for="rol">Rol</label>
          <select
            id="rol"
            v-model="form.rol"
            :class="{ error: errors.rol }"
            @change="errors.rol = false"
          >
            <option disabled value="">Seleccione rol</option>
            <option value="ADMIN">Administrador</option>
            <option value="EMPLEADO">Empleado</option>
            <option value="RECEPCIONISTA">Recepcionista</option>
          </select>
          <small v-if="errors.rol" class="error-text">
            Debes seleccionar un rol.
          </small>
        </div>

        <div class="form-group">
          <label for="documento">Documento</label>
          <input
            id="documento"
            v-model="form.documento"
            placeholder="Documento"
          />
        </div>

        <div class="form-group">
          <label for="especialidad">Especialidad</label>
          <input
            id="especialidad"
            v-model="form.especialidad"
            placeholder="Especialidad"
          />
        </div>

        <div class="form-group">
          <label for="salario">Salario</label>
          <input
            id="salario"
            v-model="form.salario"
            type="number"
            placeholder="Salario"
          />
        </div>
      </div>

      <div class="acciones">
        <button class="btn-secundario" @click="$emit('cerrar')">
          Cancelar
        </button>

        <button class="btn-guardar" @click="guardar">
          {{ empleado ? 'Guardar cambios' : 'Guardar empleado' }}
        </button>
      </div>
    </div>

    <AppToast
      :visible="toast.visible"
      :type="toast.type"
      :title="toast.title"
      :message="toast.message"
      @close="toast.visible = false"
    />
  </div>
</template>

<script>
import { crearEmpleado, editarEmpleado } from '../services/empleadoService'
import AppToast from '../components/AppToast.vue'
import { useNotificacionesStore } from '../stores/notificacionesStore'

export default {
  components: { AppToast },

  props: {
    // Si se pasa un empleado, el modal está en modo edición
    empleado: {
      type: Object,
      default: null
    }
  },

  emits: ['cerrar', 'actualizar'],

  data() {
    return {
      form: {
        nombreCompleto: '',
        correo: '',
        telefono: '',
        rol: '',
        documento: '',
        especialidad: '',
        salario: ''
      },
      toast: {
        visible: false,
        type: 'info',
        title: '',
        message: ''
      },
      errors: {
        nombreCompleto: false,
        correo: false,
        rol: false
      }
    }
  },

  mounted() {
    // Si viene un empleado, prellenar el formulario
    if (this.empleado) {
      this.form = {
        nombreCompleto: this.empleado.usuario?.nombreCompleto || '',
        correo:         this.empleado.usuario?.correo        || '',
        telefono:       this.empleado.usuario?.telefono      || '',
        rol:            this.empleado.usuario?.rol?.nombreRol || '',
        documento:      this.empleado.documento              || '',
        especialidad:   this.empleado.especialidad           || '',
        salario:        this.empleado.salario                || ''
      }
    }
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3200)
    },

    limpiarErrores() {
      this.errors = { nombreCompleto: false, correo: false, rol: false }
    },

    async guardar() {
      try {
        this.form.nombreCompleto = this.form.nombreCompleto.trim()
        this.form.correo         = this.form.correo.trim()
        this.form.telefono       = this.form.telefono.trim()
        this.form.documento      = this.form.documento.trim()
        this.form.especialidad   = this.form.especialidad.trim()

        this.limpiarErrores()

        this.errors.nombreCompleto = !this.form.nombreCompleto
        this.errors.correo         = !this.form.correo
        this.errors.rol            = !this.form.rol

        if (this.errors.nombreCompleto || this.errors.correo || this.errors.rol) {
          this.mostrarToast('warning', 'Campos incompletos', 'Debes completar nombre, correo y rol.')
          return
        }

        if (this.empleado) {
          // MODO EDICIÓN
          await editarEmpleado(this.empleado.idEmpleado, this.form)
          useNotificacionesStore().agregar('success', 'Empleado actualizado', `"${this.form.nombreCompleto}" fue actualizado`)
          this.mostrarToast('success', 'Empleado actualizado', 'Los cambios fueron guardados correctamente.')
        } else {
          // MODO CREACIÓN
          await crearEmpleado(this.form)
          useNotificacionesStore().agregar('success', 'Empleado registrado', `"${this.form.nombreCompleto}" fue registrado`)
          this.mostrarToast('success', 'Empleado registrado', 'El empleado fue creado correctamente.')
        }

        setTimeout(() => {
          this.$emit('cerrar')
          this.$emit('actualizar')
        }, 1000)

      } catch (error) {
        console.error(error)
        this.mostrarToast(
          'error',
          this.empleado ? 'No se pudo actualizar' : 'No se pudo registrar',
          error.response?.data || 'Error inesperado'
        )
      }
    }
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 19, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 999;
}

.modal {
  width: 100%;
  max-width: 760px;
  background: #f7faf5;
  border: 1px solid #d7e3d6;
  border-radius: 28px;
  padding: 28px;
  position: relative;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.14);
}

.modal-header {
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #173221;
  line-height: 1.1;
}

.modal-header p {
  margin: 8px 0 0;
  font-size: 14px;
  line-height: 1.5;
  color: #66766a;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.full {
  grid-column: 1 / -1;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #1d3524;
}

.form-group input,
.form-group select {
  width: 100%;
  border: 1px solid #d5dfd4;
  background: #ffffff;
  border-radius: 16px;
  padding: 14px 16px;
  font-size: 15px;
  color: #173221;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group input::placeholder {
  color: #90a08f;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.form-group input.error,
.form-group select.error {
  background: #fff8dc;
  border-color: #e2c96a;
  box-shadow: 0 0 0 4px rgba(226, 201, 106, 0.18);
}

.error-text {
  font-size: 12px;
  color: #8a6a00;
  margin-top: -2px;
}

.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.btn-secundario,
.btn-guardar {
  border: none;
  border-radius: 16px;
  padding: 13px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
  font-family: inherit;
}

.btn-secundario {
  background: #eef3ea;
  color: #35513b;
  border: 1px solid #d7e3d6;
}

.btn-secundario:hover {
  background: #e5ede1;
}

.btn-guardar {
  background: #004518;
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(0, 69, 24, 0.18);
}

.btn-guardar:hover {
  background: #1f6a34;
  transform: translateY(-1px);
}

.cerrar {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 40px;
  height: 40px;
  border: 1px solid #d7e3d6;
  background: #ffffff;
  color: #4a5d4f;
  border-radius: 14px;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.cerrar:hover {
  background: #f1f5ef;
  color: #173221;
}

@media (max-width: 768px) {
  .modal {
    max-width: 100%;
    padding: 22px 18px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .acciones {
    flex-direction: column;
  }

  .btn-secundario,
  .btn-guardar {
    width: 100%;
  }
}
</style>

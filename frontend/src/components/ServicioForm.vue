<template>
  <div class="overlay" @click.self="$emit('cerrar')">
    <div class="modal">

      <button class="cerrar" @click="$emit('cerrar')" aria-label="Cerrar modal">×</button>

      <div class="modal-header">
        <h3>{{ modoEdicion ? 'Editar servicio' : 'Crear servicio' }}</h3>
        <p>{{ modoEdicion ? 'Modifica la información del servicio.' : 'Completa la información para registrar un nuevo servicio.' }}</p>
      </div>

      <div class="form-group">
        <label for="sf-nombre">Nombre</label>
        <input id="sf-nombre" v-model="form.nombreServicio" maxlength="255" placeholder="Ej. Corte clásico" />
      </div>

      <div class="form-group">
        <label for="sf-desc">Descripción</label>
        <textarea id="sf-desc" v-model="form.descripcion" maxlength="255" rows="3" placeholder="Describe brevemente este servicio"></textarea>
      </div>

      <div class="form-group">
        <label for="sf-duracion">Duración</label>
        <input id="sf-duracion" v-model="form.duracion" placeholder="Ej. 30 min" />
      </div>

      <div class="form-group">
        <label for="sf-precio">Precio</label>
        <input id="sf-precio" v-model="form.precio" type="number" min="0" placeholder="Ej. 25000" />
      </div>

      <div class="form-group">
        <label for="sf-categoria">Categoría</label>
        <select id="sf-categoria" v-model="form.idCategoria">
          <option disabled value="">Seleccione categoría</option>
          <option v-for="c in categorias" :key="c.idCategoria" :value="c.idCategoria">
            {{ c.nombreCategoria }}
          </option>
        </select>
      </div>

      <div class="acciones">
        <button class="btn-secundario" @click="$emit('cerrar')">
          Cancelar
        </button>

        <button class="btn-guardar" @click="guardar" :disabled="guardando">
          {{ guardando ? 'Guardando…' : (modoEdicion ? 'Guardar cambios' : 'Guardar servicio') }}
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
import servicioService from '@/services/servicioService'
import AppToast from '@/components/AppToast.vue'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {

  components: { AppToast },

  props: {
    // Si se pasa un servicio, el modal trabaja en modo edición
    servicio: {
      type: Object,
      default: null
    }
  },

  emits: ['cerrar', 'guardado'],

  data() {
    return {
      categorias: [],
      guardando: false,
      form: {
        nombreServicio: '',
        descripcion: '',
        duracion: '',
        precio: '',
        idCategoria: ''
      },
      toast: { visible: false, type: 'info', title: '', message: '' }
    }
  },

  computed: {
    modoEdicion() {
      return !!this.servicio
    }
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast = { visible: true, type, title, message }
      setTimeout(() => { this.toast.visible = false }, 3000)
    },

    async cargarCategorias() {
      const res = await servicioService.getCategorias()
      this.categorias = res.data
    },

    async guardar() {
      if (!this.form.nombreServicio.trim()) {
        this.mostrarToast('warning', 'Campo requerido', 'El nombre del servicio es obligatorio.')
        return
      }
      if (!this.form.idCategoria) {
        this.mostrarToast('warning', 'Campo requerido', 'Selecciona una categoría.')
        return
      }
      if (this.form.precio === '' || Number(this.form.precio) < 0) {
        this.mostrarToast('warning', 'Precio inválido', 'Ingresa un precio válido.')
        return
      }

      this.guardando = true
      try {
        const payload = {
          nombreServicio: this.form.nombreServicio.trim(),
          descripcion: this.form.descripcion,
          duracion: this.form.duracion,
          precio: this.form.precio,
          categoria: { idCategoria: this.form.idCategoria }
        }

        if (this.modoEdicion) {
          await servicioService.actualizar(this.servicio.idServicio, payload)
          useNotificacionesStore().agregar('success', 'Servicio actualizado', `"${payload.nombreServicio}" fue actualizado`)
        } else {
          await servicioService.crearServicio(payload)
          useNotificacionesStore().agregar('success', 'Servicio creado', `"${payload.nombreServicio}" fue registrado`)
        }

        this.$emit('guardado')

      } catch (error) {
        console.error(error)
        const msg = typeof error.response?.data === 'string'
          ? error.response.data
          : 'No se pudo guardar el servicio. Intenta de nuevo.'
        this.mostrarToast('error', 'Error al guardar', msg)
      } finally {
        this.guardando = false
      }
    }
  },

  mounted() {
    this.cargarCategorias()

    // Si es edición, pre-cargar los datos del servicio en el form
    if (this.servicio) {
      this.form.nombreServicio = this.servicio.nombreServicio || ''
      this.form.descripcion    = this.servicio.descripcion    || ''
      this.form.duracion       = this.servicio.duracion       || ''
      this.form.precio         = this.servicio.precio         || ''
      this.form.idCategoria    = this.servicio.categoria?.idCategoria || ''
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
  max-width: 520px;
  background: #f7faf5;
  border: 1px solid #d7e3d6;
  border-radius: 28px;
  padding: 28px;
  position: relative;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.14);
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  margin-bottom: 22px;
}

.modal-header h3 {
  margin: 0;
  font-size: 28px;
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

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #1d3524;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  border: 1px solid #d5dfd4;
  background: #ffffff;
  border-radius: 14px;
  padding: 12px 16px;
  font-size: 14px;
  color: #173221;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #90a08f;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.btn-secundario,
.btn-guardar {
  border: none;
  border-radius: 14px;
  padding: 12px 18px;
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
  background: #014421;
  color: #ffffff;
  box-shadow: 0 8px 20px rgba(1, 68, 33, 0.2);
}

.btn-guardar:hover:not(:disabled) {
  background: #1f6a34;
  transform: translateY(-1px);
}

.btn-guardar:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.cerrar {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 38px;
  height: 38px;
  border: 1px solid #d7e3d6;
  background: #ffffff;
  color: #4a5d4f;
  border-radius: 12px;
  font-size: 22px;
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

@media (max-width: 600px) {
  .overlay {
    padding: 16px;
    align-items: flex-end;
  }

  .modal {
    max-width: 100%;
    padding: 22px 18px;
    border-radius: 24px 24px 18px 18px;
  }

  .modal-header h3 {
    font-size: 22px;
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

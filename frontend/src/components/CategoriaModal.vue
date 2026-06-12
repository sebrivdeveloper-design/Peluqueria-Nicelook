<template>
  <div class="overlay" @click.self="$emit('cerrar')">
    <div class="modal">
      <button class="cerrar" @click="$emit('cerrar')" aria-label="Cerrar modal">
        ×
      </button>

      <div class="modal-header">
        <h3>Crear categoría</h3>
        <p>Completa la información para registrar una nueva categoría en el sistema.</p>
      </div>

      <div class="form-group">
        <label for="nombre">Nombre</label>
        <input
          id="nombre"
          v-model="categoria.nombreCategoria"
          type="text"
          placeholder="Ej. Barbería"
        />
      </div>

      <div class="form-group">
        <label for="descripcion">Descripción</label>
        <textarea
          id="descripcion"
          v-model="categoria.descripcion"
          placeholder="Describe brevemente esta categoría"
          rows="4"
        ></textarea>
      </div>

      <div class="acciones">
        <button class="btn-secundario" @click="$emit('cerrar')">
          Cancelar
        </button>

        <button class="btn-guardar" @click="guardar">
          Guardar categoría
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
import { crearCategoria } from '../services/categoriaServices'
import AppToast from '../components/AppToast.vue'
import { useNotificacionesStore } from '../stores/notificacionesStore'

export default {
  components: {
    AppToast
  },

  data() {
    return {
      categoria: {
        nombreCategoria: '',
        descripcion: ''
      },
      toast: {
        visible: false,
        type: 'info',
        title: '',
        message: ''
      }
    }
  },

  methods: {
    mostrarToast(type, title, message) {
      this.toast.type = type
      this.toast.title = title
      this.toast.message = message
      this.toast.visible = true

      setTimeout(() => {
        this.toast.visible = false
      }, 3000)
    },

    async guardar() {
      try {
        if (!this.categoria.nombreCategoria.trim()) {
          this.mostrarToast('warning', 'Campo requerido', 'El nombre es obligatorio.')
          return
        }

        if (this.categoria.nombreCategoria.length > 255) {
          this.mostrarToast('warning', 'Nombre inválido', 'Máximo 255 caracteres en el nombre.')
          return
        }

        if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s]+$/.test(this.categoria.nombreCategoria)) {
          this.mostrarToast('warning', 'Nombre inválido', 'El nombre solo puede contener letras y espacios.')
          return
        }

        if (this.categoria.descripcion.length > 255) {
          this.mostrarToast('warning', 'Descripción inválida', 'Máximo 255 caracteres en la descripción.')
          return
        }

        await crearCategoria(this.categoria)

        useNotificacionesStore().agregar('success', 'Categoría creada', `"${this.categoria.nombreCategoria}" fue registrada`)
        this.mostrarToast('success', 'Categoría creada', 'La categoría se creó correctamente.')

        setTimeout(() => {
          this.$emit('cerrar')
          this.$emit('actualizar')
        }, 800)

      } catch (error) {
        console.error("ERROR BACKEND:", error.response)
        this.mostrarToast(
          'error',
          'Error al crear',
          error.response?.data || 'No se pudo crear la categoría.'
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
  max-width: 520px;
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

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #1d3524;
}

.form-group input,
.form-group textarea {
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

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #90a08f;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #739c76;
  box-shadow: 0 0 0 4px rgba(115, 156, 118, 0.16);
}

.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 26px;
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
    font-size: 24px;
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
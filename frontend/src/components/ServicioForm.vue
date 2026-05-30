<template>
  <div class="overlay" @click.self="$emit('cerrar')">
    <div class="modal">

      <button class="cerrar" @click="$emit('cerrar')">×</button>

      <div class="modal-header">
        <h3>Crear servicio</h3>
        <p>Completa la información para registrar un nuevo servicio.</p>
      </div>

      <div class="form-group">
        <label>Nombre</label>
        <input v-model="form.nombreServicio" placeholder="Ej. Corte clásico" />
      </div>

      <div class="form-group">
        <label>Descripción</label>
        <textarea v-model="form.descripcion" rows="3"></textarea>
      </div>

      <div class="form-group">
        <label>Duración</label>
        <input v-model="form.duracion" placeholder="Ej. 30 min" />
      </div>

      <div class="form-group">
        <label>Precio</label>
        <input v-model="form.precio" type="number" />
      </div>

      <div class="form-group">
        <label>Categoría</label>
        <select v-model="form.idCategoria">
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

        <button class="btn-guardar" @click="guardar">
          Guardar servicio
        </button>
      </div>

    </div>
  </div>
</template>

<script>
import servicioService from '@/services/servicioService'

export default {

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
      }
    }
  },

  methods: {
    async cargarCategorias() {
      const res = await servicioService.getCategorias()
      this.categorias = res.data
    },

    async guardar() {
      if (!this.form.nombreServicio || !this.form.idCategoria) {
        alert('El nombre y la categoría son obligatorios.')
        return
      }

      this.guardando = true
      try {
        const payload = {
          nombreServicio: this.form.nombreServicio,
          descripcion: this.form.descripcion,
          duracion: this.form.duracion,
          precio: this.form.precio,
          categoria: { idCategoria: this.form.idCategoria }
        }

        if (this.modoEdicion) {
          await servicioService.actualizar(this.servicio.idServicio, payload)
        } else {
          await servicioService.crearServicio(payload)
        }

        this.$emit('guardado')

      } catch (error) {
        console.error(error)
        alert('Error al guardar el servicio.')
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
/* 👇 COPIA EXACTO el mismo estilo del modal de categoría */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 19, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal {
  width: 100%;
  max-width: 520px;
  background: #f7faf5;
  border-radius: 28px;
  padding: 28px;
  position: relative;
}

.modal-header h3 {
  font-size: 28px;
  color: #173221;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 14px;
}

.form-group input,
.form-group textarea,
.form-group select {
  border: 1px solid #d5dfd4;
  border-radius: 14px;
  padding: 12px;
}

.acciones {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-secundario {
  background: #eef3ea;
  border-radius: 12px;
  padding: 10px;
}

.btn-guardar {
  background: #004518;
  color: white;
  border-radius: 12px;
  padding: 10px;
}

.cerrar {
  position: absolute;
  top: 10px;
  right: 10px;
}
</style>
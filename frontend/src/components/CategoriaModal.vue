<template>
  <div class="overlay">
    <div class="modal">
      <button class="cerrar" @click="$emit('cerrar')">X</button>

      <h3>Crear Categoría</h3>

      <input v-model="categoria.nombreCategoria" placeholder="Nombre" />
      <textarea v-model="categoria.descripcion"></textarea>
      <input v-model="categoria.imagen" placeholder="Imagen URL" />

      <button @click="guardar">Guardar</button>
    </div>
  </div>
</template>

<script>
import { crearCategoria } from '../services/categoriaServices'

export default {
  data() {
    return {
      categoria: {
        nombreCategoria: '',
        descripcion: '',
        imagen: ''
      }
    }
  },
  methods: {
    async guardar() {
  try {
    // 🔥 VALIDACIONES
    if (!this.categoria.nombreCategoria) {
      alert("El nombre es obligatorio")
      return
    }

    if (this.categoria.nombreCategoria.length > 20) {
      alert("Máximo 20 caracteres en el nombre")
      return
    }

    if (!/^[a-zA-Z ]+$/.test(this.categoria.nombreCategoria)) {
      alert("El nombre solo puede contener letras")
      return
    }

    if (this.categoria.descripcion.length > 150) {
      alert("Máximo 150 caracteres en la descripción")
      return
    }

    console.log("Enviando:", this.categoria)

    await crearCategoria(this.categoria)

    alert("✅ Categoría creada correctamente") // 🔥 MENSAJE REQUERIDO

    this.$emit('cerrar')
    this.$emit('actualizar')

  } catch (error) {
    console.error("ERROR BACKEND:", error.response)

    alert(error.response?.data || "Error al crear categoría")
  }
}
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
}

.modal {
  background: #dce6d7;
  padding: 20px;
  width: 400px;
  margin: 100px auto;
  border-radius: 15px;
  position: relative;
}

input, textarea {
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
}

.cerrar {
  position: absolute;
  top: 10px;
  right: 10px;
}

/* 📱 mobile */
@media (max-width: 600px) {
  .modal {
    width: 90%;
    margin: 50px auto;
  }
}
</style>
<template>
  

    <div class="container">

      <div v-if="categoria" class="card">

        <img
          :src="categoria.imagen"
          @error="e => e.target.src = 'https://placehold.co/200'"
        />

        <h1>{{ categoria.nombreCategoria }}</h1>

        <p v-if="categoria.descripcion">
          {{ categoria.descripcion }}
        </p>

        <hr />

        <h3>Servicios</h3>
        <p class="empty">
          Aquí se mostrarán los servicios de esta categoría
        </p>

        <button @click="$router.push('/admin/categorias')">
          Volver
        </button>

      </div>

      <p v-else class="loading">
        Cargando categoría...
      </p>

    </div>

  
</template>

<script>
import { getCategoriaById } from '../services/categoriaServices'

export default {

  data() {
    return {
      categoria: null
    }
  },

  
  async mounted() {
    try {
      const idCategoria = this.$route.params.idCategoria

      const res = await getCategoriaById(idCategoria)
      this.categoria = res.data

    } catch (error) {
      console.error("Error cargando categoría:", error)
      this.categoria = null
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.card {
  background: #dce6d7;
  padding: 30px;
  border-radius: 20px;
  width: 400px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

img {
  width: 100%;
  max-width: 200px;
  border-radius: 10px;
  margin-bottom: 15px;
}

button {
  margin-top: 20px;
  padding: 10px 15px;
  border: none;
  background: #4a7c59;
  color: white;
  border-radius: 10px;
  cursor: pointer;
}

button:hover {
  background: #3b6147;
}

.loading {
  font-size: 18px;
  color: #666;
}

.empty {
  color: #888;
  font-style: italic;
}
</style>
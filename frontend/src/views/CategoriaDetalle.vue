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
        <div v-if="servicios.length === 0" class="empty">
          No hay servicios en esta categoría
        </div>

        <ul v-else>
          <li v-for="s in servicios" :key="s.idServicio">
            {{ s.nombreServicio }} - ${{ s.precio }}
          </li>
        </ul>

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
import axios from 'axios'

export default {

  data() {
    return {
      servicios: [],
      categoria: null
    }
  },

  
  async mounted() {
      this.servicios = []; // 1. Limpia siempre al iniciar
      await this.cargarDatos();
  },
  methods: {
    async cargarDatos() {
    try {
      const idCategoria = this.$route.params.idCategoria

      const res = await getCategoriaById(idCategoria)
      this.categoria = res.data

      const resServicios = await axios.get(
        `http://localhost:8080/api/categorias/categoria/${idCategoria}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        }
      )

        this.servicios = resServicios.data

      } catch (error) {
        console.error("Error:", error)
      }
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
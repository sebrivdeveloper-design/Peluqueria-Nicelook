<template>
  

    <HeaderBar 
  @crear="mostrarModal = true"
  textoBoton="Crear categoría"
/>

    <h2>CATEGORÍAS</h2>

    <div class="grid">
      <CategoriaCard
        v-for="cat in filtradas"
        :key="cat.idCategoria"
        :categoria="cat"
        @deshabilitar="deshabilitar"
      />
    </div>

    <CategoriaModal
      v-if="mostrarModal"
      @cerrar="mostrarModal = false"
      @actualizar="cargarCategorias"
    />

  
</template>

<script>
import HeaderBar from '../components/HeaderBar.vue'
import CategoriaCard from '../components/CategoriaCard.vue'
import CategoriaModal from '../components/CategoriaModal.vue'
import { getCategorias } from '../services/categoriaServices'
import { deshabilitarCategoria } from '../services/categoriaServices'

export default {
  components: {
  HeaderBar,
  CategoriaCard,
  CategoriaModal
  },
  data() {
    return {
      categorias: [],
      mostrarModal: false,
      busqueda: ''
    }
  },

  computed: {
    filtradas() {
      return this.categorias.filter(c =>
        c.nombreCategoria?.toLowerCase().includes(this.busqueda.toLowerCase())
      )
    }
  },

  mounted() {
    this.cargarCategorias()
  },

  methods: {
    async cargarCategorias() {
      const res = await getCategorias()
      this.categorias = res.data
    },
  
    async deshabilitar(idCategoria) {
    if (confirm("¿Seguro que deseas desactivar esta categoría?")) {
    await deshabilitarCategoria(idCategoria)
    this.cargarCategorias()
  }
}
}
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

/* tablet */
@media (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* mobile */
@media (max-width: 600px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
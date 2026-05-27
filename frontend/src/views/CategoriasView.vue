<template>
  <section class="categorias-page">
    <div class="page-header">
      <div>
        <h1>Categorías</h1>
        <p>Administra las categorías disponibles para organizar los servicios del salón.</p>
      </div>
      <HeaderBar
        @crear="mostrarModal = true"
        textoBoton="Crear categoría"
      />
    </div>

    <div class="grid">
      <CategoriaCard
        v-for="cat in filtradas"
        :key="cat.idCategoria"
        :categoria="cat"
        @toggle-estado="toggleEstado"
      />
    </div>

    <div v-if="filtradas.length === 0" class="empty-state">
      <h3>No se encontraron categorías</h3>
      <p>No hay resultados para la búsqueda actual.</p>
    </div>

    <CategoriaModal
      v-if="mostrarModal"
      @cerrar="mostrarModal = false"
      @actualizar="cargarCategorias"
    />

    
  </section>
</template>

<script>
import HeaderBar from '../components/HeaderBar.vue'
import CategoriaCard from '../components/CategoriaCard.vue'
import CategoriaModal from '../components/CategoriaModal.vue'
import { getCategoriasAdmin, deshabilitarCategoria, activarCategoria } from '../services/categoriaServices'

export default {
  components: {
    HeaderBar,
    CategoriaCard,
    CategoriaModal
  },

  inject: {
    adminSearch: { default: null }
  },

  data() {
    return {
      categorias: [],
      mostrarModal: false
    }
  },

  computed: {
    filtradas() {
      const q = (this.adminSearch ?? '').toLowerCase().trim()
      if (!q) return this.categorias
      return this.categorias.filter(c =>
        c.nombreCategoria?.toLowerCase().includes(q)
      )
    }
  },

  mounted() {
    this.cargarCategorias()
  },

  methods: {
    async cargarCategorias() {
      const res = await getCategoriasAdmin()
      this.categorias = res.data
    },

    
    
    async toggleEstado(cat) {
      try {
        if (cat.activo) {
          const ok = confirm("¿Seguro que deseas desactivar esta categoría?")
          if (!ok) return

          await deshabilitarCategoria(cat.idCategoria)
          alert("Categoría desactivada correctamente")

        } else {
          const ok = confirm("¿Deseas activar esta categoría?")
          if (!ok) return

          await activarCategoria(cat.idCategoria)
          alert("Categoría activada correctamente")
        }

        this.cargarCategorias()

      } catch (error) {
        console.error(error)
        alert("Error al cambiar estado")
      }
    }
  }
}
</script>

<style scoped>
.categorias-page {
  display: flex;
  flex-direction: column;
  gap: 26px;
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
  max-width: 620px;
}

.stats-box {
  min-width: 120px;
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 18px;
  padding: 14px 18px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.04);
  text-align: center;
}

.stats-box span {
  display: block;
  font-size: 12px;
  color: #6c7c70;
  margin-bottom: 4px;
}

.stats-box strong {
  font-size: 26px;
  color: #004518;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 24px;
}

.empty-state {
  background: #ffffff;
  border: 1px dashed #c7d6c8;
  border-radius: 22px;
  padding: 40px 24px;
  text-align: center;
}

.empty-state h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #173221;
}

.empty-state p {
  margin: 0;
  color: #6b7a6f;
  font-size: 14px;
}

/* Tablet */
@media (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, minmax(220px, 1fr));
  }

  .page-header h1 {
    font-size: 32px;
  }
}

/* Mobile */
@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    align-items: stretch;
  }

  .page-header h1 {
    font-size: 28px;
  }

  .stats-box {
    width: 100%;
  }
}
</style>
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

    <!-- Filtros -->
    <div class="filtros">
      <button
        v-for="f in filtroOpts"
        :key="f.valor"
        class="filtro-btn"
        :class="{ 'filtro-activo': filtroEstado === f.valor }"
        @click="filtroEstado = f.valor"
      >{{ f.label }}</button>
    </div>

    <!-- Table -->
    <div class="cat-table-wrap">

      <div class="cat-table-head">
        <span class="th th-nombre">Categoría</span>
        <span class="th th-desc">Descripción</span>
        <span class="th th-estado">Estado</span>
        <span class="th th-acciones"></span>
      </div>

      <div class="cat-table-body">
        <CategoriaCard
          v-for="cat in filtradas"
          :key="cat.idCategoria"
          :categoria="cat"
          @toggle-estado="toggleEstado"
        />
      </div>

      <div v-if="filtradas.length === 0" class="cat-empty">
        <p>No se encontraron categorías para la búsqueda actual.</p>
      </div>

    </div>

    <CategoriaModal
      v-if="mostrarModal"
      @cerrar="mostrarModal = false"
      @actualizar="cargarCategorias"
    />

    <AppConfirmModal
      :visible="confirmModal.visible"
      :title="confirmModal.titulo"
      :message="confirmModal.mensaje"
      @confirm="ejecutarConfirmacion"
      @cancel="confirmModal.visible = false"
    />

  </section>
</template>

<script>
import HeaderBar from '../components/HeaderBar.vue'
import CategoriaCard from '../components/CategoriaCard.vue'
import CategoriaModal from '../components/CategoriaModal.vue'
import AppConfirmModal from '../components/AppConfirmModal.vue'
import { getCategoriasAdmin, deshabilitarCategoria, activarCategoria } from '../services/categoriaServices'
import { useNotificacionesStore } from '../stores/notificacionesStore'

export default {
  components: { HeaderBar, CategoriaCard, CategoriaModal, AppConfirmModal },

  inject: {
    adminSearch: { default: null }
  },

  data() {
    return {
      categorias: [],
      mostrarModal: false,
      filtroEstado: 'todos',
      filtroOpts: [
        { valor: 'todos', label: 'Todos' },
        { valor: 'activos', label: 'Activas' },
        { valor: 'inactivos', label: 'Inactivas' }
      ],
      confirmModal: { visible: false, titulo: '', mensaje: '', cat: null, accion: '' }
    }
  },

  computed: {
    filtradas() {
      let list = this.categorias
      if (this.filtroEstado === 'activos')   list = list.filter(c => c.activo)
      if (this.filtroEstado === 'inactivos') list = list.filter(c => !c.activo)
      const q = (this.adminSearch ?? '').toLowerCase().trim()
      return q ? list.filter(c => c.nombreCategoria?.toLowerCase().includes(q)) : list
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

    toggleEstado(cat) {
      if (cat.activo) {
        this.confirmModal = {
          visible: true,
          titulo: 'Desactivar categoría',
          mensaje: `¿Desactivar "${cat.nombreCategoria}"? No aparecerá en el catálogo de servicios.`,
          cat,
          accion: 'desactivar'
        }
      } else {
        this.confirmModal = {
          visible: true,
          titulo: 'Activar categoría',
          mensaje: `¿Activar "${cat.nombreCategoria}"? Volverá a aparecer en el catálogo.`,
          cat,
          accion: 'activar'
        }
      }
    },

    async ejecutarConfirmacion() {
      const { cat, accion } = this.confirmModal
      try {
        if (accion === 'desactivar') {
          await deshabilitarCategoria(cat.idCategoria)
          useNotificacionesStore().agregar('warning', 'Categoría desactivada', `"${cat.nombreCategoria}" fue desactivada`)
        } else {
          await activarCategoria(cat.idCategoria)
          useNotificacionesStore().agregar('success', 'Categoría activada', `"${cat.nombreCategoria}" está activa nuevamente`)
        }
        await this.cargarCategorias()
      } catch {
        useNotificacionesStore().agregar('error', 'Error', 'No se pudo cambiar el estado de la categoría')
      } finally {
        this.confirmModal.visible = false
      }
    }
  }
}
</script>

<style scoped>
.categorias-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ── Page header ── */
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
}

/* ── Table wrap ── */
.cat-table-wrap {
  background: #ffffff;
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid #d9e8db;
  box-shadow:
    0 2px 8px rgba(1, 68, 33, 0.06),
    0 1px 2px rgba(1, 68, 33, 0.04);
}

/* ── Column headers ── */
.cat-table-head {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 40px;
  border-bottom: 1px solid #e8f0e9;
  background: #f0f7f1;
}

.th {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.3px;
  text-transform: uppercase;
  color: #4a7c59;
  white-space: nowrap;
}

.th-nombre  { flex: 1; min-width: 0; overflow: hidden; }
.th-desc    { flex: 1.4; min-width: 0; overflow: hidden; padding-right: 16px; }
.th-estado  { width: 82px; flex-shrink: 0; }
.th-acciones{ width: 140px; flex-shrink: 0; }

/* ── Table body ── */
.cat-table-body {
  display: flex;
  flex-direction: column;
}

/* ── Empty state ── */
.cat-empty {
  padding: 40px 20px;
  text-align: center;
  color: #8fa895;
  font-size: 14px;
}

/* ── Filtros ── */
.filtros {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filtro-btn {
  border-radius: 999px;
  padding: 6px 18px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.2px;
  border: 1px solid #d9e8db;
  background: #f0f7f1;
  color: #4a7c59;
  cursor: pointer;
  transition: all 0.16s ease;
  font-family: inherit;
}

.filtro-btn:hover {
  background: #e4f0e7;
  border-color: #9abda0;
}

.filtro-btn.filtro-activo {
  background: #014421;
  color: #ffffff;
  border-color: #014421;
}

/* ── Responsive ── */
@media (max-width: 1150px) {
  .page-header h1 { font-size: 28px; }
  .th-desc { display: none; }
}

@media (max-width: 640px) {
  .page-header { flex-direction: column; align-items: stretch; }
  .cat-table-head { display: none; }
}
</style>

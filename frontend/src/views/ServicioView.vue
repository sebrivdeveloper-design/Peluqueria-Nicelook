<template>
  <section class="servicios-page">

    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h1>Servicios</h1>
        <p>Administra los servicios disponibles en el sistema.</p>
      </div>

      <button class="btn-crear" @click="mostrarForm = true">
        + Crear servicio
      </button>
    </div>

    <!-- GRID -->
    <div class="grid">
      <div class="card" v-for="s in servicios" :key="s.idServicio">
        <h3>{{ s.nombreServicio }}</h3>
        <p>{{ s.descripcion }}</p>

        <div class="info">
          <span>⏱ {{ s.duracion }}</span>
          <span>💲 {{ s.precio }}</span>
        </div>

        <div class="categoria">
          {{ s.categoria.nombreCategoria }}
        </div>
      </div>
    </div>

    <!-- EMPTY -->
    <div v-if="servicios.length === 0" class="empty">
      No hay servicios registrados
    </div>

    <!-- MODAL -->
    <ServicioForm
      v-if="mostrarForm"
      @cerrar="mostrarForm = false"
      @guardado="recargar"
    />

  </section>
</template>

<script>
import servicioService from '@/services/servicioService'
import ServicioForm from '@/components/ServicioForm.vue'

export default {
  components: { ServicioForm },

  data() {
    return {
      servicios: [],
      mostrarForm: false
    }
  },

  methods: {
    async recargar() {
      const res = await servicioService.getServicios()
      this.servicios = res.data
      this.mostrarForm = false
    }
  },

  mounted() {
    this.recargar()
  }
}
</script>

<style scoped>
.servicios-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* HEADER */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: 36px;
  color: #173221;
}

.btn-crear {
  background: #004518;
  color: white;
  padding: 12px 18px;
  border-radius: 14px;
  border: none;
  cursor: pointer;
}

/* GRID */
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

/* CARD */
.card {
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 18px;
  padding: 18px;
}

.card h3 {
  margin: 0;
  color: #173221;
}

.info {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.categoria {
  margin-top: 10px;
  font-size: 13px;
  color: #5f6f63;
}

/* EMPTY */
.empty {
  text-align: center;
  padding: 30px;
  color: #777;
}

/* RESPONSIVE */
@media (max-width: 900px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
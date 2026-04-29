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
      <div 
        class="card" 
        :class="{ inactivo: s.estado === 'inactivo' }"
        v-for="s in servicios" 
        :key="s.idServicio"
      >
        <h3>{{ s.nombreServicio }}</h3>
        <p>{{ s.descripcion }}</p>

        <div class="info">
          <span>⏱ {{ s.duracion }}</span>
          <span>💲 {{ s.precio }}</span>
        </div>

        <div class="categoria">
          {{ s.categoria.nombreCategoria }}
        </div>

        <!--  ESTADO -->
        <span 
          class="badge-estado"
          :class="s.estado === 'activo' ? 'activo' : 'inactivo'"
        >
          {{ s.estado === 'activo' ? 'Activo' : 'Inactivo' }}
        </span>


        <!--  BOTÓN -->
        <button 
          class="btn-estado"
          :class="s.estado === 'activo' ? 'btn-danger' : 'btn-success'"
          @click="toggleEstado(s)"
        >
          {{ s.estado === 'activo' ? 'Desactivar' : 'Activar' }}
        </button>


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
      const res = await servicioService.getServiciosAdmin()
      this.servicios = res.data
      this.mostrarForm = false
    },

    async toggleEstado(s) {
      try {
        if (s.estado === "activo") {

          const ok = confirm("¿Seguro que deseas desactivar este servicio?")
          if (!ok) return

          await servicioService.deshabilitar(s.idServicio)

          alert("✅ Servicio desactivado correctamente")

        } else {

          const ok = confirm("¿Deseas activar este servicio nuevamente?")
          if (!ok) return

          await servicioService.activar(s.idServicio)

          alert("✅ Servicio activado correctamente")
        }

        this.recargar()

      } catch (error) {
        console.error(error)
        alert("❌ Ocurrió un error")
      }
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

.inactivo {
  opacity: 0.5;
  filter: grayscale(80%);
}

.estado {
  margin-top: 10px;
  font-weight: bold;
}

.btn-estado {
  margin-top: 14px;
  width: 100%;
  padding: 10px 14px;
  border-radius: 12px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* DESACTIVAR */
.btn-danger {
  background: #ffe5e5;
  color: #b42318;
}

.btn-danger:hover {
  background: #fecdcd;
}

/*  ACTIVAR */
.btn-success {
  background: #e6f4ea;
  color: #1b5e20;
}

.btn-success:hover {
  background: #cde8d5;
}

.badge-estado {
  display: inline-block;
  margin-top: 10px;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

/* ACTIVO */
.badge-estado.activo {
  background: #e6f4ea;
  color: #1b5e20;
}

/* INACTIVO */
.badge-estado.inactivo {
  background: #fdecea;
  color: #b42318;
}

</style>
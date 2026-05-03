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

    <input 
      v-model="busqueda" 
      placeholder="Buscar servicio..."
      class="input-busqueda"
    />


    <!-- GRID -->
    <div v-for="(servs, categoria) in agrupados" :key="categoria">

      <h2 class="titulo-categoria">{{ categoria }}</h2>

      <div class="grid">
        <div 
          class="card" 
          :class="{ inactivo: s.estado === 'inactivo' }"
          v-for="s in servs" 
          :key="s.idServicio"
          @click="irDetalle(s.idServicio)"
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

          <span 
            class="badge-estado"
            :class="s.estado === 'activo' ? 'activo' : 'inactivo'"
          >
            {{ s.estado === 'activo' ? 'Activo' : 'Inactivo' }}
          </span>

          <button 
            class="btn-estado"
            :class="s.estado === 'activo' ? 'btn-danger' : 'btn-success'"
            @click.stop="toggleEstado(s)"
          >
            {{ s.estado === 'activo' ? 'Desactivar' : 'Activar' }}
         </button>

        </div>
      </div>

    </div>


    <!-- EMPTY -->
    <div v-if="servicios.length === 0" class="empty">
     No hay servicios registrados
    </div>

    <div v-else-if="filtrados.length === 0" class="empty">
      No se encontró el servicio
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
      mostrarForm: false,
      busqueda: ""
    }
  },

  methods: {
    async recargar() {
      const res = await servicioService.getServiciosAdmin()
      console.log(res.data)
      this.servicios = res.data
      this.mostrarForm = false
    },

    irDetalle(id) {
      this.$router.push({
        name: 'ServicioDetalle',
        params: { id }
      })
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

  computed: {
    filtrados() {
     if (!this.busqueda) return this.servicios

      return this.servicios.filter(s =>
        (s.nombreServicio || "")
          .toLowerCase()
          .includes(this.busqueda.toLowerCase()
        )
      )
    },
    agrupados() {
    const grupos = {}

    this.filtrados.forEach(s => {
      const nombreCat = s.categoria?.nombreCategoria || "Sin categoría"

      if (!grupos[nombreCat]) {
        grupos[nombreCat] = []
      }

      grupos[nombreCat].push(s)
    })

    return grupos
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

.titulo-categoria {
  margin: 20px 0 10px;
  color: #173221;
}

.input-busqueda {
  padding: 12px 16px;
  border-radius: 14px;
  border: 1px solid #d9e4da;
  outline: none;
  font-size: 14px;
  width: 300px;
}

.input-busqueda:focus {
  border-color: #004518;
  box-shadow: 0 0 0 2px rgba(0, 69, 24, 0.1);
}

.card {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.card:hover {
  transform: scale(1.02);
}



</style>
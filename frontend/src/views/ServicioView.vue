<template>
  <section class="servicios-page">
    <HeaderBar
      v-model:busqueda="busqueda"
      @crear="mostrarForm = true"
      @buscar="recargar"
      textoBoton="Crear servicio"
      placeholder="Buscar servicio"
    />

    <div class="page-header">
      <div>
        <h1>Servicios</h1>
        <p>Administra los servicios disponibles en el sistema.</p>
      </div>
    </div>

    <div v-if="filtrados.length > 0">
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
              <span>⏱ {{ s.duracionMinutos }} min</span>
              <span>💲 {{ s.precio }}</span>
            </div>

            <div class="categoria-label">
             🏷️ {{ s.categoria?.nombreCategoria || 'Sin categoría' }}
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
    </div>

    <div v-if="servicios.length === 0" class="empty">
      <h3>No hay servicios registrados</h3>
      <p>Comienza creando uno nuevo con el botón superior.</p>
    </div>

    <div v-else-if="filtrados.length === 0" class="empty">
      <h3>No se encontraron resultados</h3>
      <p>No hay servicios que coincidan con "{{ busqueda }}"</p>
    </div>

    <ServicioForm
      v-if="mostrarForm"
      @cerrar="mostrarForm = false"
      @guardado="recargar"
    />

  </section>
</template>

<script>
import HeaderBar from '@/components/HeaderBar.vue'
import servicioService from '@/services/servicioService'
import ServicioForm from '@/components/ServicioForm.vue'

export default {
  components: { 
    HeaderBar, 
    ServicioForm 
  },

  data() {
    return {
      servicios: [],
      mostrarForm: false,
      busqueda: ""
    }
  },

  computed: {
    // Filtrado en tiempo real por nombre
    filtrados() {
      if (!this.busqueda) return this.servicios
      const query = this.busqueda.toLowerCase()
      return this.servicios.filter(s =>
        (s.nombreServicio || "").toLowerCase().includes(query)
      )
    },
    
    // Agrupación de los servicios filtrados por su categoría
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

  watch: {
    // Restringe la entrada a 30 caracteres alfabéticos con tildes
    busqueda(nuevoVal) {
      let filtrado = nuevoVal.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, "");
      if (filtrado.length > 30) {
        filtrado = filtrado.substring(0, 30);
      }
      if (filtrado !== nuevoVal) {
        this.busqueda = filtrado;
      }
    }
  },

  methods: {
    async recargar() {
      try {
        const res = await servicioService.getServiciosAdmin()
        this.servicios = res.data
        this.mostrarForm = false
      } catch (error) {
        console.error("Error al cargar servicios:", error)
      }
    },

    irDetalle(id) {
      this.$router.push({
        name: 'ServicioDetalle',
        params: { id }
      })
    },

    async toggleEstado(s) {
      try {
        const nuevoEstado = s.estado === "activo" ? "desactivar" : "activar"
        const ok = confirm(`¿Deseas ${nuevoEstado} este servicio?`)
        if (!ok) return

        if (s.estado === "activo") {
          await servicioService.deshabilitar(s.idServicio)
        } else {
          await servicioService.activar(s.idServicio)
        }
        
        alert(`✅ Servicio actualizado correctamente`)
        this.recargar()
      } catch (error) {
        console.error(error)
        alert("❌ Error al cambiar el estado")
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

.page-header h1 {
  font-size: 38px;
  color: #173221;
  margin: 0;
}

.page-header p {
  color: #5f6f63;
  margin: 8px 0 0;
}

.titulo-categoria {
  margin: 30px 0 15px;
  color: #173221;
  border-bottom: 2px solid #d9e4da;
  padding-bottom: 8px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.card {
  background: #ffffff;
  border: 1px solid #d9e4da;
  border-radius: 18px;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  position: relative;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.card.inactivo {
  opacity: 0.7;
  filter: grayscale(40%);
  background: #f9f9f9;
}

.info {
  display: flex;
  justify-content: space-between;
  margin: 15px 0;
  font-weight: 600;
  color: #145c43;
}

.categoria-label {
  font-size: 13px;
  color: #5f6f63;
  margin-bottom: 10px;
}

.badge-estado {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.badge-estado.activo { background: #e6f4ea; color: #1b5e20; }
.badge-estado.inactivo { background: #fdecea; color: #b42318; }

.btn-estado {
  margin-top: 15px;
  width: 100%;
  padding: 10px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.btn-danger { background: #ffe5e5; color: #b42318; }
.btn-success { background: #e6f4ea; color: #1b5e20; }

.empty {
  text-align: center;
  padding: 50px;
  background: white;
  border-radius: 20px;
  border: 1px dashed #d9e4da;
}
</style>
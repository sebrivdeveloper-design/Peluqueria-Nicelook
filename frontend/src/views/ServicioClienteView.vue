<template>
  <section class="servicios-page">
    
    <HeaderBar
      v-model:busqueda="textoBusqueda"
      :mostrarBoton="false"
      textoBoton=""
      @buscar="cargarServicios"
      placeholder="Buscar servicio"
    />

    <div class="page-header">
      <div>
        <h1>Servicios disponibles</h1>
        <p>Explora y reserva nuestros servicios especializados de barbería y estilismo.</p>
      </div>
    </div>

    <div v-if="loading" class="empty-state">
      <h3>Cargando servicios...</h3>
      <p>Buscando las mejores opciones para ti.</p>
    </div>

    <div v-else-if="filtrados.length === 0" class="empty-state">
      <div v-if="textoBusqueda">
        <h3>No se encontraron resultados</h3>
        <p>No hay servicios que coincidan con "{{ textoBusqueda }}"</p>
        <button @click="limpiarBusqueda" class="btn-link">Ver todos los servicios</button>
      </div>
      <div v-else>
        <h3>No hay servicios disponibles</h3>
        <p>Vuelve más tarde para ver nuestras novedades.</p>
      </div>
    </div>

    <div v-else class="grid">
      <div
        v-for="serv in filtrados"
        :key="serv.idServicio"
        class="card"
      >
        <div class="card-content">
          <div class="categoria-tag">
            🏷️ {{ serv.categoria?.nombreCategoria || 'Otros' }}
          </div>
          <h3>{{ serv.nombreServicio }}</h3>
          <p class="desc">{{ serv.descripcion }}</p>
          
          <div class="card-meta">
            <span class="duracionMinutos">⏱ {{ serv.duracionMinutos }} min</span>
            <span class="precio">$ {{ formatearPrecio(serv.precio) }}</span>
          </div>

          <button 
            @click="$router.push({ name: 'ServicioDetalleCliente', params: { id: serv.idServicio } })" 
            class="btn-reservar"
          >
            Reservar
          </button>
        </div>
      </div>
    </div>

  </section>
</template>

<script>
import HeaderBar from '@/components/HeaderBar.vue'
import api from "@/services/servicioService"

export default {
  name: 'ServicioClienteView',
  components: {
    HeaderBar
  },
  data() {
    return {
      servicios: [],
      textoBusqueda: '',
      busquedaActiva: false,
      loading: true
    }
  },

  computed: {
    // Filtrado local idéntico al de CategoriasView.vue
    filtrados() {
      if (!this.textoBusqueda) return this.servicios;
      const query = this.textoBusqueda.toLowerCase();
      return this.servicios.filter(s =>
        (s.nombreServicio || "").toLowerCase().includes(query) ||
        (s.descripcion || "").toLowerCase().includes(query)
      );
    },
  },

  watch: {
    // Valida que solo ingresen letras (con tildes) y espacios, máx 30 chars
    textoBusqueda(nuevoVal) {
      let filtrado = nuevoVal.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, "");
      if (filtrado.length > 30) {
        filtrado = filtrado.substring(0, 30);
      }
      if (filtrado !== nuevoVal) {
        this.textoBusqueda = filtrado;
      }
    }
  },

  methods: {
    formatearPrecio(valor) {
      return new Intl.NumberFormat("es-CO").format(valor)
    },

    // Carga inicial de servicios (solo activos)
    async cargarServicios() {
      this.loading = true;
      try {
        const res = await api.getServicios()
        this.servicios = res.data // Traemos todos los activos una sola vez
      } catch (error) {
        console.error("Error cargando servicios:", error)
        this.servicios = []
      } finally {
        this.loading = false;
      }
    },

    limpiarBusqueda() {
      this.textoBusqueda = '';
      this.cargarServicios();
    }
  },

  mounted() {
    this.cargarServicios()
  }
}
</script>

<style scoped>
.servicios-page {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.page-header h1 {
  margin: 0;
  font-size: 38px;
  font-weight: 700;
  color: #173221;
}

.page-header p {
  color: #5f6f63;
  margin-top: 8px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 24px;
}

.card {
  background: white;
  border: 1px solid #d9e4da;
  border-radius: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.04);
}

.card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(20, 92, 67, 0.08);
}

.card-content {
  padding: 24px;
}

.categoria-tag {
  font-size: 13px;
  color: #5f6f63;
  margin-bottom: 10px;
}

.card h3 {
  color: #173221;
  margin: 0 0 12px 0;
}

.desc {
  font-size: 14px;
  color: #5f6f63;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  font-weight: bold;
}

.precio {
  color: #145c43;
  font-size: 1.2rem;
}

.btn-reservar {
  width: 100%;
  padding: 12px;
  background: #145c43;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-reservar:hover {
  background: #1f7a5c;
}

.empty-state {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 22px;
  border: 1px dashed #c7d6c8;
}

.btn-link {
  background: none;
  border: none;
  color: #145c43;
  text-decoration: underline;
  cursor: pointer;
  font-weight: bold;
  margin-top: 10px;
}

/* Responsivo idéntico a Categorias */
@media (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, minmax(220px, 1fr));
  }

  .page-header h1 {
    font-size: 32px;
  }
}

@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
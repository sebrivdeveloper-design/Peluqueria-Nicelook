<template>
  <div class="servicios-container">
    <h1 class="titulo">Servicios disponibles</h1>

    <!-- SIN DATOS -->
    <div v-if="servicios.length === 0" class="empty">
      No hay servicios disponibles en este momento
    </div>

    <!-- CON DATOS -->
    <div v-else>
      <div
        v-for="(grupo, categoria) in serviciosAgrupados"
        :key="categoria"
        class="categoria"
      >
        <h2 class="categoria-titulo">{{ categoria }}</h2>

        <div class="grid">
          <div
            v-for="serv in grupo"
            :key="serv.idServicio"
            class="card"
          >
            <h3>{{ serv.nombreServicio }}</h3>

            <p class="desc">
              {{ serv.descripcion }}
            </p>

            <p class="duracion">
              ⏱ {{ serv.duracion }}
            </p>

            <p class="precio">
              $ {{ formatearPrecio(serv.precio) }}
            </p>

            <button class="btn">
              Reservar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/servicioService"

export default {
  data() {
    return {
      servicios: []
    }
  },

  computed: {
    serviciosAgrupados() {
      const agrupados = {}

      this.servicios.forEach(s => {
        const categoria = s.categoria?.nombreCategoria || "Otros"

        if (!agrupados[categoria]) {
          agrupados[categoria] = []
        }

        agrupados[categoria].push(s)
      })

      return agrupados
    }
  },

  methods: {
    formatearPrecio(valor) {
      return new Intl.NumberFormat("es-CO").format(valor)
    },

    async cargarServicios() {
      try {
        const res = await api.getServicios()
        this.servicios = res.data
      } catch (error) {
        console.error("Error:", error)
        this.servicios = []
      }
    }
  },

  mounted() {
    this.cargarServicios()
  }
}
</script>

<style scoped>
.servicios-container {
  max-width: 1200px;
  margin: auto;
}

/* TITULO */
.titulo {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 25px;
  color: var(--primary);
}

/* CATEGORIA */
.categoria {
  margin-bottom: 40px;
}

.categoria-titulo {
  font-size: 20px;
  margin-bottom: 15px;
  color: var(--secondary);
  border-left: 6px solid var(--accent);
  padding-left: 10px;
}

/* GRID */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

/* CARD */
.card {
  background: var(--card);
  border-radius: 18px;
  padding: 18px;
  transition: 0.3s;
  border: 1px solid var(--border);
  box-shadow: 0 6px 15px rgba(0,0,0,0.05);
}

.card:hover {
  transform: translateY(-6px);
}

/* TITULO SERVICIO */
.card h3 {
  color: var(--primary);
  font-size: 18px;
}

/* DESCRIPCION */
.desc {
  font-size: 14px;
  margin: 10px 0;
  color: var(--muted);
}

/* DURACION */
.duracion {
  font-size: 13px;
  color: #888;
}

/* PRECIO */
.precio {
  font-weight: bold;
  margin-top: 10px;
  font-size: 18px;
  color: var(--secondary);
}

/* BOTON */
.btn {
  margin-top: 12px;
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(90deg, #145c43, #1f7a5c);
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: 0.2s;
}

.btn:hover {
  opacity: 0.9;
}

/* EMPTY */
.empty {
  text-align: center;
  padding: 50px;
  color: var(--muted);
}

/* RESPONSIVE */
@media (max-width: 600px) {
  .titulo {
    font-size: 24px;
  }
}
</style>
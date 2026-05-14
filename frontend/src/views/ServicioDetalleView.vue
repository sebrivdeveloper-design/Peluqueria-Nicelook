<template>
  <section class="detalle-page">
    <div class="navigation-header">
      <button @click="$router.back()" class="btn-back">
        <i class="pi pi-arrow-left"></i> Volver a servicios
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      <p>Cargando información del servicio...</p>
    </div>

    <div v-else-if="servicio" class="detalle-container">
      <div class="detalle-grid">
        
        <div class="info-column">
          <span class="categoria-badge">
            {{ servicio.categoria?.nombreCategoria || 'Servicio' }}
          </span>
          <h1 class="servicio-titulo">{{ servicio.nombreServicio }}</h1>
          
          <div class="metas-rapidas">
            <div class="meta-item">
              <span class="label">Duración</span>
              <span class="valor">⏱ {{ servicio.duracion }}</span>
            </div>
            <div class="meta-item">
              <span class="label">Precio</span>
              <span class="valor precio-destacado">$ {{ formatearPrecio(servicio.precio) }}</span>
            </div>
          </div>

          <div class="descripcion-box">
            <h3>Sobre este servicio</h3>
            <p>{{ servicio.descripcion }}</p>
          </div>
        </div>

        <div class="action-column">
          <div class="reserva-card">
            <div class="reserva-header">
              <i class="pi pi-calendar-plus"></i>
              <span>Agendar Cita</span>
            </div>
            <p>Selecciona este servicio para proceder con la elección de fecha y hora.</p>
            
            <button class="btn-confirmar-reserva" @click="confirmarReserva">
              Continuar con la reserva
            </button>
            
            <p class="nota-pago">El pago se realiza directamente en el local.</p>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="error-state">
      <h3>Servicio no encontrado</h3>
      <p>Lo sentimos, el servicio que buscas no está disponible o no existe.</p>
      <button @click="$router.push('/cliente/servicios')" class="btn-primary">
        Ir al catálogo
      </button>
    </div>
  </section>
</template>

<script>
import api from "@/services/servicioService"

export default {
  name: 'ServicioDetalleView',
  data() {
    return {
      servicio: null,
      loading: true
    }
  },
  methods: {
    formatearPrecio(valor) {
      return new Intl.NumberFormat("es-CO").format(valor)
    },
    async cargarDetalle() {
      this.loading = true;
      const id = this.$route.params.id; // Captura el ID de la ruta definida en router/index.js
      try {
        const res = await api.getById(id);
        this.servicio = res.data;
      } catch (error) {
        console.error("Error al obtener detalle:", error);
      } finally {
        this.loading = false;
      }
    },
    confirmarReserva() {
      // Aquí podrías redirigir al siguiente paso (Calendario de citas)
      alert(`Has seleccionado: ${this.servicio.nombreServicio}. Próximamente: Selección de fecha.`);
    }
  },
  mounted() {
    this.cargarDetalle();
  }
}
</script>

<style scoped>
.detalle-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.navigation-header {
  margin-bottom: 30px;
}

.btn-back {
  background: none;
  border: none;
  color: #145c43;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.detalle-container {
  background: white;
  border-radius: 24px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.06);
  padding: 40px;
  border: 1px solid #d9e4da;
}

.detalle-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 50px;
}

/* INFORMACIÓN */
.categoria-badge {
  background: #e6f4ea;
  color: #145c43;
  padding: 6px 16px;
  border-radius: 99px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
}

.servicio-titulo {
  font-size: 42px;
  color: #173221;
  margin: 15px 0 25px 0;
  line-height: 1.1;
}

.metas-rapidas {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.meta-item {
  display: flex;
  flex-direction: column;
}

.meta-item .label {
  font-size: 13px;
  color: #888;
  text-transform: uppercase;
}

.meta-item .valor {
  font-size: 20px;
  font-weight: 600;
  color: #173221;
}

.precio-destacado {
  color: #145c43 !important;
  font-size: 24px !important;
}

.descripcion-box h3 {
  color: #173221;
  margin-bottom: 15px;
}

.descripcion-box p {
  color: #5f6f63;
  line-height: 1.8;
  font-size: 16px;
}

/* COLUMNA ACCIÓN */
.reserva-card {
  background: #f8fafc;
  border-radius: 20px;
  padding: 30px;
  border: 1px solid #e2e8f0;
  text-align: center;
  position: sticky;
  top: 100px;
}

.reserva-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  color: #145c43;
  margin-bottom: 15px;
}

.reserva-card p {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 25px;
}

.btn-confirmar-reserva {
  width: 100%;
  padding: 16px;
  background: #145c43;
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-confirmar-reserva:hover {
  transform: scale(1.03);
  background: #1b5e20;
}

.nota-pago {
  margin-top: 15px;
  font-size: 12px !important;
  font-style: italic;
}

/* RESPONSIVO */
@media (max-width: 900px) {
  .detalle-grid {
    grid-template-columns: 1fr;
  }
  .reserva-card {
    position: static;
  }
  .servicio-titulo {
    font-size: 32px;
  }
}
</style>
<template>
  <div class="detalle-page">

    <button class="back-btn" @click="$router.push('/admin/categorias')">
      <ChevronLeft :size="15" />
      Categorías
    </button>

    <!-- Skeleton while loading -->
    <div v-if="!categoria" class="loading-wrap">
      <div class="skel skel-hero"></div>
      <div class="skel-grid">
        <div v-for="n in 6" :key="n" class="skel skel-card"></div>
      </div>
    </div>

    <template v-else>
      <!-- Hero -->
      <div class="hero" :class="{ 'hero--off': !categoria.activo }">
        <div class="hero-media">
          <img
            :src="categoria.imagen || fallback"
            @error="e => e.target.src = fallback"
            alt="Imagen de categoría"
          />
        </div>
        <div class="hero-body">
          <span class="estado-chip" :class="categoria.activo ? 'chip--on' : 'chip--off'">
            <span class="chip-dot"></span>
            {{ categoria.activo ? 'Activa' : 'Inactiva' }}
          </span>
          <h1>{{ categoria.nombreCategoria }}</h1>
          <p v-if="categoria.descripcion" class="hero-desc">{{ categoria.descripcion }}</p>
          <div class="hero-meta">
            <Scissors :size="13" />
            <span>
              {{ servicios.length === 0
                ? 'Sin servicios asignados'
                : `${servicios.length} ${servicios.length === 1 ? 'servicio' : 'servicios'}` }}
            </span>
          </div>
        </div>
      </div>

      <!-- Services -->
      <section class="srv-section">
        <h2 class="srv-heading">Servicios</h2>

        <div v-if="servicios.length === 0" class="srv-empty">
          <Scissors :size="32" />
          <p>Esta categoría no tiene servicios aún.</p>
        </div>

        <div v-else class="srv-grid">
          <div v-for="s in servicios" :key="s.idServicio" class="srv-card">
            <div class="srv-name-area">
              <span class="srv-name">{{ s.nombreServicio }}</span>
            </div>
            <div class="srv-price-block">
              <span class="price-label">precio</span>
              <span class="price-value">${{ formatNum(s.precio) }}</span>
            </div>
          </div>
        </div>
      </section>
    </template>

  </div>
</template>

<script>
import { ChevronLeft, Scissors } from 'lucide-vue-next'
import { getCategoriaById } from '../services/categoriaServices'
import api from '../services/axiosInstance'

export default {
  components: { ChevronLeft, Scissors },

  data() {
    return {
      categoria: null,
      servicios: [],
      fallback: 'https://placehold.co/400x400?text=Categoría'
    }
  },

  async mounted() {
    await this.cargarDatos()
  },

  methods: {
    async cargarDatos() {
      try {
        const id = this.$route.params.idCategoria
        const [resCat, resSrv] = await Promise.all([
          getCategoriaById(id),
          api.get(`/categorias/categoria/${id}`)
        ])
        this.categoria = resCat.data
        this.servicios = resSrv.data
      } catch (err) {
        console.error('Error cargando categoría:', err)
      }
    },

    formatNum(n) {
      return Number(n).toLocaleString('es-CO')
    }
  }
}
</script>

<style scoped>
/* ── Page shell ── */
.detalle-page {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* ── Back button ── */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  align-self: flex-start;
  background: none;
  border: 1px solid #c8d9ca;
  border-radius: 10px;
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 600;
  color: #4a7c59;
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease, color 0.18s ease;
}
.back-btn:hover {
  background: #e6f2e8;
  border-color: #9abda0;
  color: #014421;
}

/* ── Hero ── */
.hero {
  display: flex;
  gap: 36px;
  align-items: flex-start;
  background: #ffffff;
  border: 1px solid #dde8de;
  border-radius: 28px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(1, 68, 33, 0.06);
}
.hero--off {
  filter: grayscale(40%);
  opacity: 0.72;
}

.hero-media {
  width: 220px;
  height: 220px;
  flex-shrink: 0;
  border-radius: 20px;
  overflow: hidden;
  background: #edf2eb;
}
.hero-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-top: 4px;
}

/* Status chip */
.estado-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.2px;
  align-self: flex-start;
}
.chip--on  { background: #e0f2e5; color: #1b5e20; }
.chip--off { background: #ffe5e5; color: #b42318; }
.chip-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.65;
}

.hero-body h1 {
  margin: 0;
  font-size: 34px;
  font-weight: 800;
  color: #0d2117;
  line-height: 1.15;
  letter-spacing: -0.6px;
}

.hero-desc {
  margin: 0;
  font-size: 15px;
  line-height: 1.65;
  color: #56675a;
  max-width: 520px;
}

.hero-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #4a7c59;
}

/* ── Services section ── */
.srv-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.srv-heading {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0d2117;
}

/* ── Service grid ── */
.srv-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(185px, 1fr));
  gap: 14px;
}

/* ── Service card ── */
.srv-card {
  background: #ffffff;
  border: 1px solid #dde8de;
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(1, 68, 33, 0.04);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.srv-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 24px rgba(1, 68, 33, 0.1);
}

.srv-name-area {
  padding: 20px 18px 18px;
  flex: 1;
}
.srv-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a3327;
  line-height: 1.45;
}

/* The signature element: forest-green price footer */
.srv-price-block {
  background: #014421;
  padding: 13px 18px 14px;
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.price-label {
  font-size: 10px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.45);
  text-transform: uppercase;
  letter-spacing: 0.8px;
}
.price-value {
  font-size: 20px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.5px;
  line-height: 1.2;
}

/* ── Empty state ── */
.srv-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 52px 24px;
  background: #ffffff;
  border: 1px dashed #c7d6c8;
  border-radius: 22px;
  color: #8fa895;
}
.srv-empty p {
  margin: 0;
  font-size: 15px;
}

/* ── Shimmer skeleton ── */
.loading-wrap {
  display: flex;
  flex-direction: column;
  gap: 28px;
}
.skel {
  background: linear-gradient(90deg, #edf2eb 25%, #e0e9de 50%, #edf2eb 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite linear;
  border-radius: 24px;
}
.skel-hero  { height: 284px; }
.skel-grid  {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(185px, 1fr));
  gap: 14px;
}
.skel-card  { height: 130px; border-radius: 18px; }
@keyframes shimmer {
  to { background-position: -200% 0; }
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    gap: 20px;
    padding: 24px;
  }
  .hero-media {
    width: 100%;
    height: 200px;
  }
  .hero-body h1 { font-size: 26px; }
}

@media (max-width: 480px) {
  .srv-grid {
    grid-template-columns: 1fr 1fr;
  }
  .hero-media { height: 160px; }
  .hero-body h1 { font-size: 22px; }
}
</style>

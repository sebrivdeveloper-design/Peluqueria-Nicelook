<template>
  <div class="cat-row-wrap" :class="{ expanded: dropdownAbierto }">

    <!-- Main category row -->
    <div class="cat-row" :class="{ inactiva: !categoria.activo }">

      <!-- Nombre -->
      <div class="col-nombre" @click.stop="toggleDropdown">
        <span class="row-nombre">{{ categoria.nombreCategoria }}</span>
      </div>

      <!-- Descripción -->
      <div class="col-desc">
        <span class="row-desc">
          {{ categoria.descripcion || '—' }}
        </span>
      </div>

      <!-- Estado -->
      <div class="col-estado">
        <span class="estado-chip" :class="categoria.activo ? 'chip-on' : 'chip-off'">
          <span class="chip-dot"></span>
          {{ categoria.activo ? 'Activa' : 'Inactiva' }}
        </span>
      </div>

      <!-- Acciones -->
      <div class="col-acciones">
        <button
          class="btn-toggle-estado"
          :class="categoria.activo ? 'btn-off' : 'btn-on'"
          @click.stop="toggleEstado"
        >
          {{ categoria.activo ? 'Desactivar' : 'Activar' }}
        </button>

        <button
          class="btn-expand"
          :class="{ 'btn-expand--open': dropdownAbierto }"
          @click.stop="toggleDropdown"
          :title="dropdownAbierto ? 'Cerrar servicios' : 'Ver servicios'"
        >
          <ChevronDown :size="15" class="chevron-icon" />
        </button>
      </div>
    </div>

    <!-- Service sub-rows -->
    <transition name="srv-fade">
      <div v-if="dropdownAbierto" class="srv-rows-wrap">

        <!-- Service header -->
        <div class="srv-header-row">
          <span class="srv-th">Servicio</span>
          <span class="srv-th srv-th-precio">Costo</span>
          <span class="srv-th srv-th-dur">Duración</span>
          <span class="srv-th-end"></span>
        </div>

        <!-- Loading -->
        <div v-if="cargandoServicios" class="srv-loading">
          <span class="spinner"></span>
          Cargando servicios...
        </div>

        <!-- Empty -->
        <div v-else-if="servicios.length === 0" class="srv-empty-row">
          Esta categoría no tiene servicios asignados.
        </div>

        <!-- Rows -->
        <div
          v-else
          v-for="(s, i) in servicios"
          :key="s.idServicio"
          class="srv-row"
          :style="{ animationDelay: `${i * 40}ms` }"
        >
          <span class="srv-nombre">{{ s.nombreServicio }}</span>
          <span class="srv-precio">${{ formatNum(s.precio) }}</span>
          <span class="srv-dur">{{ s.duracion || '—' }}</span>
          <span class="srv-end"></span>
        </div>

      </div>
    </transition>

  </div>
</template>

<script>
import { ChevronDown } from 'lucide-vue-next'
import api from '../services/axiosInstance'

export default {
  components: { ChevronDown },

  props: ['categoria'],

  data() {
    return {
      servicios: [],
      dropdownAbierto: false,
      cargandoServicios: false
    }
  },

  mounted() {
    document.addEventListener('click', this.cerrarDropdown)
  },

  beforeUnmount() {
    document.removeEventListener('click', this.cerrarDropdown)
  },

  methods: {
    toggleEstado() {
      this.$emit('toggle-estado', this.categoria)
    },

    async toggleDropdown() {
      this.dropdownAbierto = !this.dropdownAbierto
      if (this.dropdownAbierto && this.servicios.length === 0) {
        this.cargandoServicios = true
        try {
          const res = await api.get(`/categorias/categoria/${this.categoria.idCategoria}`)
          this.servicios = res.data
        } catch (e) {
          console.error(e)
        } finally {
          this.cargandoServicios = false
        }
      }
    },

    cerrarDropdown() {
      this.dropdownAbierto = false
    },

    formatNum(n) {
      return Number(n).toLocaleString('es-CO')
    }
  }
}
</script>

<style scoped>
/* ── Row wrap — holds category row + service rows ── */
.cat-row-wrap {
  border-bottom: 1px solid #edf2ee;
  transition: border-color 0.18s ease;
}

.cat-row-wrap:last-child {
  border-bottom: none;
}

/* Signature: left-border bookmark on expanded rows */
.cat-row-wrap.expanded {
  border-left: 3px solid #014421;
}

/* ── Main category row ── */
.cat-row {
  display: flex;
  align-items: center;
  padding: 0 20px;
  min-height: 58px;
  gap: 0;
  transition: background 0.15s ease;
  cursor: default;
}

.cat-row:hover {
  background: #f6fbf7;
}

.cat-row.inactiva {
  opacity: 0.5;
}

/* ── Columns ── */
.col-nombre {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  padding-right: 12px;
  cursor: pointer;
}

.row-nombre {
  font-size: 14px;
  font-weight: 700;
  color: #0d2117;
  letter-spacing: -0.1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  transition: color 0.15s ease;
}

.col-nombre:hover .row-nombre {
  color: #014421;
}

.col-desc {
  flex: 1.4;
  min-width: 0;
  overflow: hidden;
  padding-right: 16px;
}

.row-desc {
  font-size: 12.5px;
  color: #7a9280;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.col-estado {
  width: 82px;
  flex-shrink: 0;
}

.estado-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.2px;
  padding: 4px 9px;
  border-radius: 999px;
}

.chip-on  { background: #e0f2e5; color: #1b5e20; }
.chip-off { background: #ffe5e5; color: #b42318; }

.chip-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
  flex-shrink: 0;
}

.col-acciones {
  width: 140px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

/* Toggle estado button */
.btn-toggle-estado {
  font-size: 11px;
  font-weight: 700;
  padding: 5px 12px;
  border-radius: 7px;
  border: 1px solid;
  cursor: pointer;
  letter-spacing: 0.1px;
  transition: all 0.16s ease;
  background: transparent;
}

.btn-off {
  border-color: rgba(180, 35, 24, 0.3);
  color: #b42318;
}

.btn-off:hover {
  background: #fff1f0;
  border-color: rgba(180, 35, 24, 0.55);
}

.btn-on {
  border-color: rgba(1, 68, 33, 0.3);
  color: #014421;
}

.btn-on:hover {
  background: #e8f5ec;
  border-color: rgba(1, 68, 33, 0.55);
}

/* Expand chevron */
.btn-expand {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  border: 1px solid #c8d9ca;
  background: transparent;
  color: #6a9c76;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.18s ease;
  flex-shrink: 0;
}

.btn-expand:hover {
  background: #e8f2e9;
  color: #014421;
  border-color: #9abda0;
}

.btn-expand--open {
  background: #014421;
  border-color: #014421;
  color: #ffffff;
}

.chevron-icon {
  transition: transform 0.22s ease;
}

.btn-expand--open .chevron-icon {
  transform: rotate(180deg);
}

/* ── Service rows block ── */
.srv-rows-wrap {
  background: #f6fbf7;
  border-top: 1px solid #e0ebe2;
}

/* Service sub-header */
.srv-header-row {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 32px;
  border-bottom: 1px solid #e8f0e9;
}

.srv-th {
  font-size: 9.5px;
  font-weight: 700;
  letter-spacing: 1.2px;
  text-transform: uppercase;
  color: #4a7c59;
  flex: 1;
}

.srv-th-precio {
  width: 120px;
  flex: none;
  flex-shrink: 0;
}

.srv-th-dur {
  width: 100px;
  flex: none;
  flex-shrink: 0;
}

.srv-th-end {
  width: 140px;
  flex-shrink: 0;
}

/* Individual service row */
.srv-row {
  display: flex;
  align-items: center;
  padding: 0 20px;
  min-height: 42px;
  border-bottom: 1px solid #edf2ee;
  animation: srvSlide 0.18s ease both;
  transition: background 0.13s ease;
}

.srv-row:last-child {
  border-bottom: none;
}

.srv-row:hover {
  background: #eef7f0;
}

@keyframes srvSlide {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}

.srv-nombre {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: #2d4a36;
}

.srv-precio {
  width: 120px;
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 800;
  color: #014421;
  letter-spacing: -0.2px;
}

.srv-dur {
  width: 100px;
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 500;
  color: #7a9280;
}

.srv-end {
  width: 140px;
  flex-shrink: 0;
}

/* Loading / empty */
.srv-loading,
.srv-empty-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px 16px 72px;
  font-size: 13px;
  color: #8fa895;
}

.spinner {
  width: 13px;
  height: 13px;
  border: 2px solid #c8d9ca;
  border-top-color: #014421;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Transition for service block */
.srv-fade-enter-active {
  transition: opacity 0.18s ease, max-height 0.22s ease;
  max-height: 800px;
  overflow: hidden;
}

.srv-fade-leave-active {
  transition: opacity 0.14s ease, max-height 0.18s ease;
  max-height: 800px;
  overflow: hidden;
}

.srv-fade-enter-from,
.srv-fade-leave-to {
  opacity: 0;
  max-height: 0;
}

/* ── Responsive ── */
@media (max-width: 1150px) {
  .col-desc  { display: none; }
  .srv-th-dur { display: none; }
  .srv-dur    { display: none; }
}

@media (max-width: 640px) {
  .cat-row { padding: 0 12px; }
  .col-img img { width: 28px; height: 28px; }
  .col-acciones { gap: 6px; }
  .btn-toggle-estado { display: none; }
}
</style>

<template>
  <article class="card"
   :class="{inactiva: !categoria.activo}" 
   @click="verDetalle"
   >
    <div class="card-image-wrapper">
      <img
        :src="categoria.imagen || fallbackImage"
        @error="onImageError"
        alt="Imagen de categoría"
      />
    </div>

    <div class="card-content">
      <h3>{{ categoria.nombreCategoria }}</h3>

      <span 
        class="badge-estado"
        :class="categoria.activo ? 'activo' : 'inactivo'"
      >
       {{ categoria.activo ? 'Activa' : 'Inactiva' }}
      </span>
      <p class="descripcion">
        {{ categoria.descripcion || 'Categoría disponible para organizar servicios dentro del sistema.' }}
      </p>

      <div class="card-footer">
        <button 
          class="btn-estado"
          :class="categoria.activo ? 'btn-danger' : 'btn-success'"
          @click.stop="toggleEstado"
         >
          {{ categoria.activo ? 'Desactivar' : 'Activar' }}
        </button>


        <span class="ver-mas">Ver detalle</span>
      </div>
    </div>
  </article>
</template>

<script>
export default {
  props: ['categoria'],
  data() {
    return {
      fallbackImage: 'https://placehold.co/120x120?text=Categoria'
    }
  },
  methods: {
    verDetalle() {
      this.$router.push(`/admin/categorias/${this.categoria.idCategoria}`)
    },

    onImageError(e) {
      e.target.src = this.fallbackImage
    },

    toggleEstado() {
      this.$emit('toggle-estado', this.categoria)
    }


  }
}
</script>

<style scoped>
.card {
  background: #dfe9db;
  border: 1px solid #cfddca;
  border-radius: 28px;
  padding: 22px;
  min-height: 330px;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 34px rgba(0, 0, 0, 0.08);
}

.card-image-wrapper {
  width: 110px;
  height: 110px;
  margin: 0 auto 18px;
  border-radius: 24px;
  background: #f4f7f1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.card-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-align: center;
}

.card-content h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #173221;
  line-height: 1.25;
}

.descripcion {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: #56675a;
  min-height: 44px;
}

.card-footer {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.btn-estado {
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

/* ACTIVAR */
.btn-success {
  background: #e6f4ea;
  color: #1b5e20;
}

.btn-success:hover {
  background: #cde8d5;
}

.btn-warning {
  border: none;
  background: #fff0b8;
  color: #6e5600;
  padding: 11px 18px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease;
  box-shadow: 0 6px 14px rgba(255, 208, 74, 0.18);
}

.btn-warning:hover {
  background: #ffe58a;
  transform: translateY(-1px);
}

.ver-mas {
  font-size: 13px;
  font-weight: 600;
  color: #004518;
  opacity: 0.85;
}

/* Mobile */
@media (max-width: 640px) {
  .card {
    min-height: 300px;
    padding: 20px;
  }

  .card-image-wrapper {
    width: 96px;
    height: 96px;
  }

  .card-content h3 {
    font-size: 18px;
  }
}

.badge-estado {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  margin: 6px auto 0;
}

.badge-estado.activo {
  background: #e6f4ea;
  color: #1b5e20;
}

.badge-estado.inactivo {
  background: #ffe5e5;
  color: #b42318;
}
.inactiva {
  opacity: 0.5;
  filter: grayscale(80%);
}

</style>
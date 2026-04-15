<template>
  <div class="card" @click="verDetalle">

    <img 
      :src="categoria.imagen"
      @error="e => e.target.src = 'https://placehold.co/80'"
    />

    <p>{{ categoria.nombreCategoria }}</p>

    <button @click.stop="desactivar">
      Desactivar
    </button>

  </div>
</template>

<script>
export default {
  props: ['categoria'],

  methods: {
    verDetalle() {
      this.$router.push(`/admin/categorias/${this.categoria.idCategoria}`)
    },

    desactivar() {
      this.$emit('deshabilitar', this.categoria.idCategoria)
    }
  }
}
</script>

<style scoped>
.card {
  background: #c8d5c0;
  border-radius: 15px;
  text-align: center;
  padding: 15px;
  width: 80%;
  aspect-ratio: 1/1;
  cursor: pointer;
  transition: 0.2s;
}

.card:hover {
  transform: scale(1.02);
}

img {
  width: 50%;
  max-width: 80px;
}

button {
  margin-top: 10px;
}
</style>
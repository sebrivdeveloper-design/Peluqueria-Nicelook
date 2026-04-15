<template>
  <div class="header">
    <input 
  :value="busqueda" 
  @input="$emit('update:busqueda', $event.target.value)" 
  placeholder="Buscar..." 
/>
    <button @click="$emit('crear')">Crear categoría</button>
  </div>
</template>

<script>
export default {
  props: ['busqueda'],
  emits: ['update:busqueda', 'crear']
}
</script>

<style scoped>
.header {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

input {
  flex: 1;
  padding: 10px;
  border-radius: 10px;
}

button {
  background: #0b3d2e;
  color: white;
  padding: 10px;
  border-radius: 10px;
}

/* 📱 mobile */
@media (max-width: 600px) {
  .header {
    flex-direction: column;
  }

  button {
    width: 100%;
  }
}
</style>
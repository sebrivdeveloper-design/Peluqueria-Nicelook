<template>
  <div class="layout">
    
    <!-- Sidebar fijo -->
    <Sidebar />

    <!-- Contenido dinámico según la ruta -->
    <div class="content">
      <router-view />
    </div>

  </div>
</template>

<script>
import Sidebar from '../components/Sidebar.vue'

export default {
  name: 'AdminLayout',
  components: { Sidebar }
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f4f4f4;
  overflow-y: auto;
}

/* 📱 Responsive */
@media (max-width: 768px) {
  .layout {
    flex-direction: column;
  }

  .content {
    padding: 15px;
  }
}
</style>
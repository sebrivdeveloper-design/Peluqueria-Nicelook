<template>
  <div class="layout-container">

    <!-- SIDEBAR GLOBAL -->
    <Sidebar rol="EMPLEADO" />

    <!-- CONTENIDO -->
    <main class="main-content">
      <div class="view-wrapper">
        <router-view />
      </div>
    </main>

  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'EmpleadoLayout',

  components: {
    Sidebar
  },

  computed: {

    rolUsuario() {
      return localStorage.getItem('rol') || 'CLIENTE'
    }

  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background: #f6f8f5;
}

/* CONTENIDO */
.main-content {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* WRAPPER GENERAL */
.view-wrapper {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px;
  box-sizing: border-box;
}

/* RESPONSIVE */
@media (max-width: 1024px) {
  .view-wrapper {
    padding: 22px;
  }
}

@media (max-width: 768px) {
  .view-wrapper {
    padding: 18px;
  }
}
</style>
<template>
  <div class="admin-layout">
    <!-- Sidebar fijo -->
    <Sidebar />

    <!-- Área principal -->
    <div class="admin-main">
      <div class="admin-content">
        <router-view />
      </div>
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
.admin-layout {
  display: flex;
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(139, 93, 122, 0.08), transparent 22%),
    radial-gradient(circle at bottom right, rgba(139, 93, 122, 0.05), transparent 20%),
    linear-gradient(180deg, #f9f7fb 0%, #f4f1f7 100%);
  overflow: hidden;
}

.admin-main {
  flex: 1;
  min-width: 0;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.admin-content {
  min-height: 100%;
  padding: 32px;
  box-sizing: border-box;
}

/* Tablet */
@media (max-width: 1024px) {
  .admin-content {
    padding: 24px;
  }
}

/* Móvil */
@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-main {
    height: auto;
  }

  .admin-content {
    padding: 18px;
  }
}

/* Móvil pequeño */
@media (max-width: 480px) {
  .admin-content {
    padding: 14px;
  }
}
</style>
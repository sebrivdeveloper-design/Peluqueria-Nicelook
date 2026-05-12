<template>
  <div class="layout">
    
    <Sidebar rol="CLIENTE" class="sidebar-fixed" />

    <main class="main-container">
      <header class="top-bar">
        <div class="user-info">
          <span>Panel de Cliente</span>
        </div>
      </header>

      <section class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </section>
    </main>

  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue';

export default {
  name: 'ClienteLayout',
  components: {
    Sidebar
  }
}
</script>

<style scoped>
.layout {
  display: flex;
  background: #f8fafc; /* Un gris azulado muy suave para resaltar las cards blancas */
  min-height: 100vh;
  width: 100%;
}

.sidebar-fixed {
  width: 260px; /* Ancho estándar para sidebars */
  position: sticky;
  top: 0;
  height: 100vh;
  flex-shrink: 0;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.top-bar {
  height: 60px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 30px;
  border-bottom: 1px solid #e2e8f0;
}

.user-info {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.content {
  padding: 30px;
  max-width: 1300px;
  width: 100%;
  margin: 0 auto;
}

/* Transición suave entre vistas */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsivo para móviles */
@media (max-width: 768px) {
  .layout {
    flex-direction: column;
  }
  
  .sidebar-fixed {
    width: 100%;
    height: auto;
    position: relative;
  }
  
  .content {
    padding: 15px;
  }
}
</style>
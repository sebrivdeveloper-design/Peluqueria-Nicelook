<template>
  <div class="layout-container">

    <!-- SIDEBAR GLOBAL NICELOOK -->
    <Sidebar rol="RECEPCIONISTA" />

    <!-- CONTENIDO -->
    <main class="main-content">
      <div class="topbar">
        <div>
          <h1>Panel Recepcionista</h1>
          <p>Gestión de clientes, agenda y disponibilidad</p>
        </div>

        <div class="user-box">
          <div class="avatar">
            {{ inicial }}
          </div>

          <div class="user-info">
            <h4>Recepcionista</h4>
            <span>{{ correo }}</span>
          </div>
        </div>
      </div>

      <!-- VISTAS -->
      <section class="view-wrapper">
        <router-view />
      </section>
    </main>

  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'RecepcionistaLayout',

  components: {
    Sidebar
  },

  computed: {
    correo() {
      const token = localStorage.getItem('token')

      if (!token) return 'recepcion@nicelook.com'

      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        return payload.sub || 'recepcion@nicelook.com'
      } catch {
        return 'recepcion@nicelook.com'
      }
    },

    inicial() {
      return this.correo.charAt(0).toUpperCase()
    }
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background: #f5f6f8;
}

/* CONTENIDO */

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

/* TOPBAR */

.topbar {
  height: 90px;
  background: white;
  border-bottom: 1px solid #e8ece9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  box-sizing: border-box;
}

.topbar h1 {
  margin: 0;
  font-size: 26px;
  color: #004518;
  font-weight: 700;
}

.topbar p {
  margin: 4px 0 0;
  color: #687076;
  font-size: 14px;
}

/* USER BOX */

.user-box {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #ffffff;
  border: 1px solid #edf1ee;
  padding: 10px 16px;
  border-radius: 18px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

.avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: #004518;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
}

.user-info h4 {
  margin: 0;
  color: #004518;
  font-size: 15px;
}

.user-info span {
  font-size: 13px;
  color: #6c757d;
}

/* CONTENIDO DINÁMICO */

.view-wrapper {
  padding: 32px;
  width: 100%;
  box-sizing: border-box;
}

/* RESPONSIVE */

@media (max-width: 768px) {

  .topbar {
    padding: 16px;
    height: auto;
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .view-wrapper {
    padding: 20px;
  }

  .user-box {
    width: 100%;
  }
}
</style>
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

        <div class="topbar-right">

          <!-- NOTIFICACIONES -->
          <div class="notif-wrap" @click.stop>
            <button class="notif-btn" @click="togglePanel" title="Notificaciones">
              <Bell :size="18" />
              <span v-if="noLeidas > 0" class="notif-badge">{{ noLeidas > 9 ? '9+' : noLeidas }}</span>
            </button>
            <transition name="panel-drop">
              <div v-if="panelNotifAbierto" class="notif-panel">
                <div class="notif-panel-head">
                  <span class="notif-panel-title">Notificaciones</span>
                </div>
                <p v-if="listaNotif.length === 0" class="notif-empty">Sin notificaciones recientes</p>
                <ul v-else class="notif-list">
                  <li
                    v-for="n in listaNotif"
                    :key="n.id"
                    class="notif-item"
                    :class="[`notif-tipo-${n.tipo}`, { nueva: !n.leida }]"
                  >
                    <span class="notif-acento"></span>
                    <div class="notif-body">
                      <p class="notif-titulo">{{ n.titulo }}</p>
                      <p class="notif-msg">{{ n.mensaje }}</p>
                    </div>
                  </li>
                </ul>
              </div>
            </transition>
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
import { Bell } from 'lucide-vue-next'
import { useNotificacionesStore } from '@/stores/notificacionesStore'

export default {
  name: 'RecepcionistaLayout',

  components: {
    Sidebar,
    Bell
  },

  data() {
    return {
      panelNotifAbierto: false
    }
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
    },

    listaNotif() { return useNotificacionesStore().lista },
    noLeidas()   { return useNotificacionesStore().noLeidas }
  },

  mounted() {
    document.addEventListener('click', this.cerrarPanel)
    useNotificacionesStore().cargar()
  },

  beforeUnmount() {
    document.removeEventListener('click', this.cerrarPanel)
  },

  methods: {
    togglePanel() {
      this.panelNotifAbierto = !this.panelNotifAbierto
      if (this.panelNotifAbierto) useNotificacionesStore().marcarTodasLeidas()
    },
    cerrarPanel() { this.panelNotifAbierto = false }
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

/* TOPBAR RIGHT + NOTIFICACIONES */

.topbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.notif-wrap {
  position: relative;
}

.notif-btn {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  border: 1px solid #e8ece9;
  background: #f7f8f7;
  color: #4f5d52;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.18s ease;
}

.notif-btn:hover {
  background: #ffffff;
  color: #014421;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notif-badge {
  position: absolute;
  top: 6px;
  right: 5px;
  min-width: 17px;
  height: 17px;
  border-radius: 999px;
  background: #ef4444;
  color: #ffffff;
  font-size: 10px;
  font-weight: 800;
  padding: 0 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #ffffff;
  line-height: 1;
}

.notif-panel {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 310px;
  background: #ffffff;
  border: 1px solid #d9e8db;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(1, 68, 33, 0.13);
  z-index: 300;
  max-height: 400px;
  overflow-y: auto;
}

.notif-panel-head {
  padding: 14px 16px 12px;
  border-bottom: 1px solid #edf2ee;
}

.notif-panel-title {
  font-size: 14px;
  font-weight: 700;
  color: #0d2117;
}

.notif-empty {
  padding: 28px 16px;
  text-align: center;
  font-size: 13px;
  color: #8fa895;
  margin: 0;
}

.notif-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.notif-item {
  display: flex;
  gap: 10px;
  padding: 12px 14px;
  border-bottom: 1px solid #edf2ee;
}

.notif-item:last-child { border-bottom: none; }
.notif-item.nueva { background: #f6fbf7; }

.notif-acento {
  width: 3px;
  border-radius: 2px;
  flex-shrink: 0;
  align-self: stretch;
  min-height: 32px;
  background: #014421;
}

.notif-tipo-warning .notif-acento { background: #b45309; }
.notif-tipo-error   .notif-acento { background: #b42318; }

.notif-body { flex: 1; min-width: 0; }

.notif-titulo {
  font-size: 13px;
  font-weight: 700;
  color: #0d2117;
  margin: 0 0 2px;
}

.notif-msg {
  font-size: 12px;
  color: #56675a;
  margin: 0;
  line-height: 1.4;
}

.panel-drop-enter-active { transition: opacity 0.18s ease, transform 0.18s ease; }
.panel-drop-leave-active { transition: opacity 0.14s ease, transform 0.14s ease; }
.panel-drop-enter-from,
.panel-drop-leave-to { opacity: 0; transform: translateY(-6px); }

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
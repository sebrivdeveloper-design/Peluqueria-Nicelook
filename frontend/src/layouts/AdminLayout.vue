<template>
  <div class="admin-layout">

    <!-- SIDEBAR -->
    <!-- DESPUÉS -->
    <Sidebar rol="ADMIN" />

    <!-- MAIN -->
    <div class="admin-main">

      <!-- ═══ TOPBAR ═══ -->
      <header class="topbar">

        <!-- LEFT: SEARCH -->
        <div class="topbar-left">
          <div class="search-box" :class="{ focused: searchFocused }">
            <span class="search-icon"><Search :size="16" /></span>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Buscar servicios, categorías, empleados…"
              @focus="searchFocused = true"
              @blur="searchFocused = false"
              @keydown.escape="searchQuery = ''"
            />
            <transition name="fade-quick">
              <button
                v-if="searchQuery"
                class="clear-btn"
                @click="searchQuery = ''"
                title="Limpiar búsqueda"
              >
                <X :size="14" />
              </button>
            </transition>
          </div>
        </div>

        <!-- RIGHT: ACTIONS -->
        <div class="topbar-right">

          <!-- NOTIFICACIONES -->
          <div class="notif-wrap" @click.stop>
            <button class="icon-btn notif-btn" @click="togglePanel" title="Notificaciones">
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
                      <p class="notif-tiempo">{{ formatTiempo(n.tiempo) }}</p>
                    </div>
                  </li>
                </ul>
              </div>
            </transition>
          </div>


        </div>

      </header>

      <!-- ═══ CONTENT ═══ -->
      <main class="admin-content">
        <router-view />
      </main>

    </div>

  </div>
</template>

<script>
import { computed } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import { Search, Bell, X } from 'lucide-vue-next'
import { useNotificacionesStore } from '../stores/notificacionesStore'

export default {

  name: 'AdminLayout',

  components: {
    Sidebar,
    Search,
    Bell,
    X
  },

  data() {
    return {
      searchQuery: '',
      searchFocused: false,
      panelNotifAbierto: false
    }
  },

  computed: {
    listaNotif() { return useNotificacionesStore().lista },
    noLeidas()   { return useNotificacionesStore().noLeidas }
  },

  mounted() {
    document.addEventListener('click', this.cerrarPanel)
  },

  beforeUnmount() {
    document.removeEventListener('click', this.cerrarPanel)
  },

  methods: {
    togglePanel() {
      this.panelNotifAbierto = !this.panelNotifAbierto
      if (this.panelNotifAbierto) useNotificacionesStore().marcarTodasLeidas()
    },
    cerrarPanel() { this.panelNotifAbierto = false },
    marcarTodasLeidas() { useNotificacionesStore().marcarTodasLeidas() },
    formatTiempo(date) {
      const diff = Math.floor((Date.now() - new Date(date)) / 1000)
      if (diff < 60)    return 'hace un momento'
      if (diff < 3600)  return `hace ${Math.floor(diff / 60)} min`
      if (diff < 86400) return `hace ${Math.floor(diff / 3600)} h`
      return `hace ${Math.floor(diff / 86400)} días`
    }
  },

  provide() {
    return {
      adminSearch: computed(() => this.searchQuery)
    }
  }
}
</script>

<style scoped>

/* ═══════════════════════════════
   LAYOUT
═══════════════════════════════ */

.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f4f6f4;
}

.admin-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* ═══════════════════════════════
   TOPBAR
═══════════════════════════════ */

.topbar {
  height: 72px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  background: #ffffff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 50;
  flex-shrink: 0;
}

/* ── SEARCH ── */

.topbar-left {
  flex: 1;
  max-width: 420px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 44px;
  background: #f7f8f7;
  border: 1.5px solid transparent;
  border-radius: 14px;
  padding: 0 14px;
  transition: all 0.2s ease;
}

.search-box.focused {
  background: #ffffff;
  border-color: rgba(1, 68, 33, 0.28);
  box-shadow:
    0 0 0 4px rgba(1, 68, 33, 0.07),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-icon {
  color: #9aa5b4;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  transition: color 0.18s;
}

.search-box.focused .search-icon {
  color: #014421;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  font-weight: 400;
  font-family: 'Manrope', sans-serif;
  color: #101828;
}

.search-box input::placeholder {
  color: #b0bac5;
}

.clear-btn {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  border: none;
  background: #e8eaea;
  color: #667085;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.18s;
}

.clear-btn:hover {
  background: #d5d8d5;
  color: #344054;
}

/* ── ACTIONS ── */

.topbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.icon-btn {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  border: 1.5px solid rgba(0, 0, 0, 0.06);
  background: #f7f8f7;
  color: #667085;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.18s ease;
  position: relative;
}

.icon-btn:hover {
  background: #ffffff;
  border-color: rgba(0, 0, 0, 0.1);
  color: #344054;
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
  pointer-events: none;
}

/* ── NOTIFICATION PANEL ── */

.notif-wrap {
  position: relative;
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px 12px;
  border-bottom: 1px solid #edf2ee;
  position: sticky;
  top: 0;
  background: #ffffff;
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
  transition: background 0.13s;
}

.notif-item:last-child {
  border-bottom: none;
}

.notif-item.nueva {
  background: #f6fbf7;
}

.notif-acento {
  width: 3px;
  border-radius: 2px;
  flex-shrink: 0;
  align-self: stretch;
  min-height: 32px;
}

.notif-tipo-success .notif-acento { background: #014421; }
.notif-tipo-warning .notif-acento { background: #b45309; }
.notif-tipo-error   .notif-acento { background: #b42318; }

.notif-body {
  flex: 1;
  min-width: 0;
}

.notif-titulo {
  font-size: 13px;
  font-weight: 700;
  color: #0d2117;
  margin: 0 0 2px;
}

.notif-msg {
  font-size: 12px;
  color: #56675a;
  margin: 0 0 4px;
  line-height: 1.4;
}

.notif-tiempo {
  font-size: 11px;
  color: #8fa895;
  margin: 0;
}

.panel-drop-enter-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.panel-drop-leave-active {
  transition: opacity 0.14s ease, transform 0.14s ease;
}
.panel-drop-enter-from,
.panel-drop-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ═══════════════════════════════
   CONTENT
═══════════════════════════════ */

.admin-content {
  flex: 1;
  padding: 32px 36px 40px;
}

/* ═══════════════════════════════
   TRANSITIONS
═══════════════════════════════ */

.fade-quick-enter-active,
.fade-quick-leave-active {
  transition: opacity 0.15s ease;
}

.fade-quick-enter-from,
.fade-quick-leave-to {
  opacity: 0;
}

/* ═══════════════════════════════
   RESPONSIVE
═══════════════════════════════ */

@media (max-width: 1024px) {
  .topbar {
    padding: 0 20px;
  }
  .admin-content {
    padding: 24px 20px 32px;
  }
}

@media (max-width: 768px) {
  .topbar {
    height: auto;
    flex-direction: column;
    align-items: stretch;
    padding: 14px 16px;
    gap: 12px;
  }
  .topbar-left {
    max-width: 100%;
  }
  .topbar-right {
    justify-content: flex-end;
  }
  .admin-content {
    padding: 16px 16px 28px;
  }
}

</style>

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
          <button class="icon-btn notif-btn" title="Notificaciones">
            <Bell :size="18" />
            <span class="notif-badge"></span>
          </button>

          <!-- PERFIL -->
          <div class="admin-profile">
            <div class="admin-avatar">A</div>
            <div class="admin-info">
              <span class="admin-name">Administrador</span>
              <span class="admin-role">NiceLook</span>
            </div>
            <ChevronDown :size="14" class="profile-chevron" />
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
import { Search, Bell, X, ChevronDown } from 'lucide-vue-next'

export default {

  name: 'AdminLayout',

  components: {
    Sidebar,
    Search,
    Bell,
    X,
    ChevronDown
  },

  data() {
    return {
      searchQuery: '',
      searchFocused: false
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
  top: 9px;
  right: 9px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #ef4444;
  border: 2px solid #ffffff;
}

/* ── PROFILE ── */

.admin-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 12px 7px 7px;
  background: #f7f8f7;
  border: 1.5px solid rgba(0, 0, 0, 0.06);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.admin-profile:hover {
  background: #ffffff;
  border-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.admin-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #014421, #1a6b3c);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 13px;
  flex-shrink: 0;
}

.admin-info {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 13.5px;
  font-weight: 600;
  color: #101828;
  line-height: 1.2;
}

.admin-role {
  font-size: 11.5px;
  color: #9aa5b4;
  font-weight: 400;
}

.profile-chevron {
  color: #b0bac5;
  flex-shrink: 0;
  margin-left: 2px;
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
  .admin-info {
    display: none;
  }
  .admin-content {
    padding: 16px 16px 28px;
  }
}

</style>

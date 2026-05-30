<template>
  <aside :class="['sidebar', { collapsed: isCollapsed }]">

    <!-- ═══ BRAND ═══ -->
    <div class="sidebar-brand">
      <div class="brand-logo">
        <img src="../assets/NICELOOK LOGO SLIDE BAR.png" alt="NiceLook" />
      </div>
      <transition name="fade">
        <div v-if="!isCollapsed" class="brand-text">
          <span class="brand-name">{{ roleConfig.title }}</span>
          <span class="brand-tagline">{{ roleConfig.subtitle }}</span>
        </div>
      </transition>
      <button class="collapse-btn" @click="toggleSidebar">
        <PanelLeftClose v-if="!isCollapsed" :size="17" />
        <PanelLeftOpen v-else :size="17" />
      </button>
    </div>

    <!-- ═══ MAIN NAV ═══ -->
    <nav class="nav-section">
      <span v-if="!isCollapsed" class="section-label">Navegación</span>

      <!-- ADMIN -->
      <template v-if="rol === 'ADMIN'">
        <button class="nav-item" :class="{ active: isActive('/admin/categorias') }" @click="goTo('/admin/categorias')">
          <span class="nav-icon"><LayoutGrid :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Categorías</span></transition>
          <span v-if="isActive('/admin/categorias') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/admin/servicios') }" @click="goTo('/admin/servicios')">
          <span class="nav-icon"><BriefcaseBusiness :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Servicios</span></transition>
          <span v-if="isActive('/admin/servicios') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/admin/empleados') }" @click="goTo('/admin/empleados')">
          <span class="nav-icon"><Users :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Empleados</span></transition>
          <span v-if="isActive('/admin/empleados') && !isCollapsed" class="active-pill"></span>
        </button>
      </template>

      <!-- CLIENTE -->
      <template v-else-if="rol === 'CLIENTE'">
        <button class="nav-item" :class="{ active: isActive('/') }" @click="goTo('/')">
          <span class="nav-icon"><LayoutGrid :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Inicio</span></transition>
          <span v-if="isActive('/') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/cliente/servicios') }" @click="goTo('/cliente/servicios')">
          <span class="nav-icon"><BriefcaseBusiness :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Servicios</span></transition>
          <span v-if="isActive('/cliente/servicios') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/cliente/mis-citas') }" @click="goTo('/cliente/mis-citas')">
          <span class="nav-icon"><Users :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Mis citas</span></transition>
          <span v-if="isActive('/cliente/mis-citas') && !isCollapsed" class="active-pill"></span>
        </button>
      </template>

      <!-- EMPLEADO -->
      <template v-else-if="rol === 'EMPLEADO'">
        <button class="nav-item" :class="{ active: isActive('/empleado/agenda') }" @click="goTo('/empleado/agenda')">
          <span class="nav-icon"><LayoutGrid :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Agenda</span></transition>
          <span v-if="isActive('/empleado/agenda') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/empleado/turnos') }" @click="goTo('/empleado/turnos')">
          <span class="nav-icon"><Users :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Turnos</span></transition>
          <span v-if="isActive('/empleado/turnos') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/empleado/servicios') }" @click="goTo('/empleado/servicios')">
          <span class="nav-icon"><BriefcaseBusiness :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Servicios</span></transition>
          <span v-if="isActive('/empleado/servicios') && !isCollapsed" class="active-pill"></span>
        </button>
      </template>

      <!-- RECEPCIONISTA -->
      <template v-else-if="rol === 'RECEPCIONISTA'">
        <button class="nav-item" :class="{ active: isActive('/recepcionista/clientes') }" @click="goTo('/recepcionista/clientes')">
          <span class="nav-icon"><Users :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Clientes</span></transition>
          <span v-if="isActive('/recepcionista/clientes') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/recepcionista/agenda') }" @click="goTo('/recepcionista/agenda')">
          <span class="nav-icon"><LayoutGrid :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Agenda</span></transition>
          <span v-if="isActive('/recepcionista/agenda') && !isCollapsed" class="active-pill"></span>
        </button>
        <button class="nav-item" :class="{ active: isActive('/recepcionista/base-diaria') }" @click="goTo('/recepcionista/base-diaria')">
          <span class="nav-icon"><BriefcaseBusiness :size="17" /></span>
          <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Base diaria</span></transition>
          <span v-if="isActive('/recepcionista/base-diaria') && !isCollapsed" class="active-pill"></span>
        </button>
      </template>

    </nav>

    <!-- ═══ SPACER ═══ -->
    <div class="flex-grow"></div>

    <!-- ═══ FOOTER ═══ -->
    <div class="sidebar-footer">

      <div class="footer-divider"></div>

      <span v-if="!isCollapsed" class="section-label">Cuenta</span>

      <button class="nav-item">
        <span class="nav-icon"><Settings :size="17" /></span>
        <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Configuración</span></transition>
      </button>

      <button class="nav-item logout-item" @click="cerrarSesion">
        <span class="nav-icon"><LogOut :size="17" /></span>
        <transition name="fade"><span v-if="!isCollapsed" class="nav-text">Cerrar sesión</span></transition>
      </button>

      <!-- PROFILE -->
      <div class="profile-card">
        <div class="profile-avatar">{{ roleConfig.avatar }}</div>
        <transition name="fade">
          <div v-if="!isCollapsed" class="profile-info">
            <span class="profile-name">{{ roleConfig.title }}</span>
            <span class="profile-status">
              <span class="status-dot"></span>
              En línea
            </span>
          </div>
        </transition>
      </div>

    </div>

  </aside>
</template>

<script>
import {
  LayoutGrid,
  BriefcaseBusiness,
  Users,
  Settings,
  LogOut,
  PanelLeftClose,
  PanelLeftOpen
} from 'lucide-vue-next'

export default {
  name: 'Sidebar',

  props: {
    rol: {
      type: String,
      default: 'CLIENTE'
    }
  },

  components: {
    LayoutGrid,
    BriefcaseBusiness,
    Users,
    Settings,
    LogOut,
    PanelLeftClose,
    PanelLeftOpen
  },

  data() {
    return {
      isCollapsed: false
    }
  },

  computed: {
    isLogged() {
      return !!localStorage.getItem('token')
    },
    roleConfig() {
      const roles = {
        ADMIN:         { title: 'Administrador', subtitle: 'Panel de gestión',   avatar: 'A' },
        CLIENTE:       { title: 'Cliente',        subtitle: 'Panel de usuario',   avatar: 'C' },
        EMPLEADO:      { title: 'Estilista',       subtitle: 'Panel de agenda',    avatar: 'E' },
        RECEPCIONISTA: { title: 'Recepción',       subtitle: 'Panel operativo',    avatar: 'R' }
      }
      return roles[this.rol] || roles.CLIENTE
    }
  },

  methods: {
    goTo(route) {
      this.$router.push(route)
      if (window.innerWidth <= 768) this.isCollapsed = true
    },
    isActive(route) {
      return this.$route.path === route
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
    },
    handleResize() {
      if (window.innerWidth <= 768) this.isCollapsed = true
    },
    cerrarSesion() {
      localStorage.removeItem('token')
      localStorage.removeItem('rol')
      this.$router.replace('/')
    }
  },

  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
  }
}
</script>

<style scoped>

/* ═══════════════════════════════
   SIDEBAR BASE
═══════════════════════════════ */

.sidebar {
  width: 258px;
  height: 100vh;
  background: #014421;
  display: flex;
  flex-direction: column;
  padding: 22px 12px 26px;
  position: sticky;
  top: 0;
  overflow-x: hidden;
  overflow-y: auto;
  flex-shrink: 0;
  z-index: 100;
  transition: width 0.28s ease, padding 0.28s ease;
  border-right: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar.collapsed {
  width: 72px;
  padding: 22px 8px 26px;
}

/* ═══════════════════════════════
   BRAND
═══════════════════════════════ */

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 4px 4px 2px;
  margin-bottom: 30px;
}

.sidebar.collapsed .sidebar-brand {
  flex-direction: column;
  gap: 10px;
}

.brand-logo {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-text {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.brand-name {
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.brand-tagline {
  color: rgba(255, 255, 255, 0.38);
  font-size: 11px;
  font-weight: 400;
  margin-top: 1px;
}

.collapse-btn {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.18s ease;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

/* ═══════════════════════════════
   NAVIGATION
═══════════════════════════════ */

.nav-section {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.section-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.3);
  padding: 0 14px;
  margin-bottom: 8px;
  margin-top: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 11px 13px;
  border-radius: 11px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  transition: all 0.18s ease;
  width: 100%;
  font-size: 13.5px;
  font-weight: 500;
  font-family: 'Manrope', sans-serif;
  text-align: left;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 11px;
  gap: 0;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.95);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.97);
  color: #014421;
  font-weight: 700;
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.08);
}

.nav-icon {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
}

.active-pill {
  flex-shrink: 0;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #014421;
  opacity: 0.4;
}

/* ═══════════════════════════════
   SPACER
═══════════════════════════════ */

.flex-grow {
  flex: 1;
  min-height: 20px;
}

/* ═══════════════════════════════
   FOOTER
═══════════════════════════════ */

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.footer-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.07);
  margin: 2px 4px 16px;
}

.logout-item {
  color: rgba(255, 160, 160, 0.75);
}

.logout-item:hover {
  background: rgba(255, 80, 80, 0.1);
  color: rgba(255, 180, 180, 1);
}

/* ═══════════════════════════════
   PROFILE CARD
═══════════════════════════════ */

.profile-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 11px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.06);
  margin-top: 10px;
  cursor: default;
  transition: background 0.18s ease;
}

.sidebar.collapsed .profile-card {
  justify-content: center;
  padding: 10px;
}

.profile-card:hover {
  background: rgba(255, 255, 255, 0.09);
}

.profile-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #a8d8b0, #d4edda);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #014421;
  font-weight: 800;
  font-size: 13px;
  flex-shrink: 0;
}

.profile-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.profile-name {
  color: rgba(255, 255, 255, 0.92);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.profile-status {
  display: flex;
  align-items: center;
  gap: 5px;
  color: rgba(255, 255, 255, 0.38);
  font-size: 11px;
  margin-top: 2px;
}

.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ade80;
  flex-shrink: 0;
  box-shadow: 0 0 5px rgba(74, 222, 128, 0.5);
}

/* ═══════════════════════════════
   TRANSITIONS
═══════════════════════════════ */

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ═══════════════════════════════
   RESPONSIVE
═══════════════════════════════ */

@media (max-width: 768px) {
  .sidebar {
    width: 72px;
  }
  .sidebar:not(.collapsed) {
    width: 258px;
  }
}

</style>

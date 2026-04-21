<template>
  <aside :class="['sidebar', { collapsed: isCollapsed }]">
    <div class="sidebar-top">
      <div class="top-row">
        <div class="brand">
          <div class="brand-logo">
            <img src="../assets/NICELOOK LOGO SLIDE BAR.png" alt="Logo" />
          </div>

          <transition name="fade">
            <div v-if="!isCollapsed" class="brand-text">
              <h2>Admin</h2>
              <p>Panel de gestión</p>
            </div>
          </transition>
        </div>

        <button class="collapse-btn" @click="toggleSidebar">
          <PanelLeftClose v-if="!isCollapsed" :size="20" />
          <PanelLeftOpen v-else :size="20" />
        </button>
      </div>
    </div>

    <nav class="nav-menu">
      <button
        class="nav-item"
        :class="{ active: isActive('/admin/categorias') }"
        @click="goTo('/admin/categorias')"
        title="Categorías"
      >
        <LayoutGrid :size="20" />
        <span v-if="!isCollapsed">Categorías</span>
      </button>

      <button
        class="nav-item"
        :class="{ active: isActive('/admin/servicios') }"
        @click="goTo('/admin/servicios')"
        title="Servicios"
      >
        <BriefcaseBusiness :size="20" />
        <span v-if="!isCollapsed">Servicios</span>
      </button>

      <button
        class="nav-item"
        :class="{ active: isActive('/admin/empleados') }"
        @click="goTo('/admin/empleados')"
        title="Empleados"
      >
        <Users :size="20" />
        <span v-if="!isCollapsed">Empleados</span>
      </button>
    </nav>

    <div class="sidebar-bottom">
      <button class="nav-item secondary" title="Configuración">
        <Settings :size="20" />
        <span v-if="!isCollapsed">Configuración</span>
      </button>

      <div class="profile-box">
        <div class="avatar">A</div>

        <transition name="fade">
          <div v-if="!isCollapsed" class="profile-info">
            <h4>Administrador</h4>
            <p>Cuenta activa</p>
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
  PanelLeftClose,
  PanelLeftOpen
} from 'lucide-vue-next'

export default {
  name: 'Sidebar',
  components: {
    LayoutGrid,
    BriefcaseBusiness,
    Users,
    Settings,
    PanelLeftClose,
    PanelLeftOpen
  },
  data() {
    return {
      isCollapsed: true
    }
  },
  methods: {
    goTo(route) {
      this.$router.push(route)

      if (window.innerWidth <= 768) {
        this.isCollapsed = true
      }
    },
    isActive(route) {
      return this.$route.path === route
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
    },
    handleResize() {
      if (window.innerWidth <= 768) {
        this.isCollapsed = true
      }
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
.sidebar {
  width: 270px;
  min-height: 100vh;
  background: linear-gradient(180deg, #004518 0%, #063d1a 100%);
  color: #ffffff;
  display: flex;
  flex-direction: column;
  padding: 18px 14px;
  box-sizing: border-box;
  transition: width 0.28s ease, padding 0.28s ease;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 8px 0 24px rgba(0, 69, 24, 0.12);
}

.sidebar.collapsed {
  width: 92px;
  padding: 18px 10px;
}

.sidebar-top {
  margin-bottom: 22px;
}

.top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
}

.sidebar.collapsed .top-row {
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.sidebar.collapsed .brand {
  justify-content: center;
}

.brand-logo {
  width: 54px;
  height: 54px;
  min-width: 54px;
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  box-sizing: border-box;
  overflow: hidden;
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.brand-text {
  min-width: 0;
}

.brand-text h2 {
  margin: 0;
  font-size: 17px;
  line-height: 1.1;
  font-weight: 700;
  color: #ffffff;
}

.brand-text p {
  margin: 4px 0 0;
  font-size: 11px;
  line-height: 1.35;
  color: rgba(255, 255, 255, 0.82);
}

.collapse-btn {
  width: 54px;
  height: 54px;
  min-width: 54px;
  border: 1.5px solid rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  background: transparent;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.22s ease;
  flex-shrink: 0;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar.collapsed .collapse-btn {
  margin: 0 auto;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.nav-item {
  width: 100%;
  border: none;
  background: transparent;
  color: #ffffff;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.22s ease;
  font-size: 15px;
  font-weight: 600;
  text-align: left;
}

.nav-item svg {
  flex-shrink: 0;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  background: #d6e2d7;
  color: #004518;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.08);
}

.sidebar.collapsed .nav-menu {
  align-items: center;
}

.sidebar.collapsed .nav-item {
  width: 56px;
  height: 56px;
  padding: 0;
  border-radius: 18px;
  justify-content: center;
}

.sidebar-bottom {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-top: 18px;
}

.profile-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 12px 14px;
}

.sidebar.collapsed .profile-box {
  justify-content: center;
  padding: 10px;
}

.avatar {
  width: 42px;
  height: 42px;
  min-width: 42px;
  border-radius: 50%;
  background: #fffedb;
  color: #004518;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.profile-info h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
}

.profile-info p {
  margin: 2px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.76);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .sidebar {
    width: 92px;
    min-height: 100vh;
  }

  .sidebar:not(.collapsed) {
    width: 250px;
  }
}
</style>
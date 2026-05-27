<template>
  <aside class="sidebar">

    <!-- ═══ BRAND ═══ -->
    <div class="sidebar-brand">
      <div class="brand-logo">
        <img src="../assets/NICELOOK LOGO SLIDE BAR.png" alt="NiceLook" />
      </div>
      <div class="brand-text">
        <span class="brand-name">NiceLook</span>
        <span class="brand-tagline">Beauty Salon</span>
      </div>
    </div>

    <!-- ═══ MAIN NAV ═══ -->
    <nav class="nav-section">
      <span class="section-label">Navegación</span>

      <button
        class="nav-item"
        :class="{ active: isActive('/admin/categorias') }"
        @click="goTo('/admin/categorias')"
      >
        <span class="nav-icon"><LayoutGrid :size="17" /></span>
        <span class="nav-text">Categorías</span>
        <span v-if="isActive('/admin/categorias')" class="active-pill"></span>
      </button>

      <button
        class="nav-item"
        :class="{ active: isActive('/admin/servicios') }"
        @click="goTo('/admin/servicios')"
      >
        <span class="nav-icon"><BriefcaseBusiness :size="17" /></span>
        <span class="nav-text">Servicios</span>
        <span v-if="isActive('/admin/servicios')" class="active-pill"></span>
      </button>

      <button
        class="nav-item"
        :class="{ active: isActive('/admin/empleados') }"
        @click="goTo('/admin/empleados')"
      >
        <span class="nav-icon"><Users :size="17" /></span>
        <span class="nav-text">Empleados</span>
        <span v-if="isActive('/admin/empleados')" class="active-pill"></span>
      </button>
    </nav>

    <!-- ═══ SPACER ═══ -->
    <div class="flex-grow"></div>

    <!-- ═══ FOOTER ═══ -->
    <div class="sidebar-footer">

      <div class="footer-divider"></div>

      <span class="section-label">Cuenta</span>

      <button class="nav-item">
        <span class="nav-icon"><Settings :size="17" /></span>
        <span class="nav-text">Configuración</span>
      </button>

      <button class="nav-item logout-item" @click="cerrarSesion">
        <span class="nav-icon"><LogOut :size="17" /></span>
        <span class="nav-text">Cerrar sesión</span>
      </button>

      <!-- PROFILE -->
      <div class="profile-card">
        <div class="profile-avatar">A</div>
        <div class="profile-info">
          <span class="profile-name">Administrador</span>
          <span class="profile-status">
            <span class="status-dot"></span>
            En línea
          </span>
        </div>
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
  LogOut
} from 'lucide-vue-next'

export default {

  name: 'Sidebar',

  components: {
    LayoutGrid,
    BriefcaseBusiness,
    Users,
    Settings,
    LogOut
  },

  methods: {

    goTo(route) {
      this.$router.push(route)
    },

    isActive(route) {
      return this.$route.path === route
    },

    cerrarSesion() {
      localStorage.removeItem('token')
      localStorage.removeItem('rol')
      this.$router.replace('/')
    }
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
}

.brand-name {
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.4px;
}

.brand-tagline {
  color: rgba(255, 255, 255, 0.38);
  font-size: 11px;
  font-weight: 400;
  margin-top: 1px;
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

</style>
